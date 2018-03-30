package org.moonframework.crawler.parse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.moonframework.concurrent.pool.TaskAdapter;
import org.moonframework.crawler.storage.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.moonframework.crawler.storage.Field.LINK;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/9/10
 */
public class ParserJob extends TaskAdapter<WebPage, Void> {

    protected static Log logger = LogFactory.getLog(ParserJob.class);

    private Parser parser;

    public ParserJob(Parser parser) {
        this.parser = parser;
    }

    Map<Long, Object> objectMap = new HashMap<>();

    @Override
    protected Void call(WebPage webPage) throws Exception {
        try {
            long start = System.currentTimeMillis();
            String url = webPage.getUri().toString();

            // 检查网页相关度
            if (logger.isDebugEnabled()) {
                logger.debug("Parsing URL [Start] : " + url);
            }
            if (!parser.getRankUrl().accept(url, webPage.getHtml())) {
                if (logger.isInfoEnabled()) {
                    logger.info("Parsing URL [Not accept] : " + url);
                }
                parser.discard(webPage);
            }

            // 处理解析结果
            process(webPage);
            if (objectMap != null) {
                parser.addObjectMap(objectMap);
            }


            long end = System.currentTimeMillis();
            if (logger.isInfoEnabled()) {
                logger.info("Parsing URL [Successful] in (" + (end - start) + ")ms: " + url);
            }
        } catch (Exception e) {
            parser.error(webPage);
            logger.error("Parsing URL [Error]", e);
        } finally {
            parser.release();
        }
        return null;
    }

    /**
     * <p>core method</p>
     *
     * @param page page
     * @return ParseResult 解析结果
     */
    protected ParseResult parse(WebPage page) {
        Document document = parser.getOptimizer().optimize(Jsoup.parse(page.getHtml()), page);
        ParseResult parseResult = new ParseResult();
        parseResult.setDocument(document);
        page.getNodes().forEach(node -> node(parseResult, document, page, node));
        return parseResult;
    }

    /**
     * @param parseResult parseResult
     * @param document    document
     * @param page        page
     * @param node        node
     */
    protected void node(ParseResult parseResult, Document document, WebPage page, Node node) {
        List<Item> items = new ArrayList<>();
        parseResult.put(node, items);

        // remove element
        Remove remove = node.getRemove();
        if (remove != null && remove.getSelectors() != null) {
            if (remove.isClone())
                document = document.clone();
            for (String selector : remove.getSelectors())
                document.select(selector).remove();
        }

        List<Field> fields = node.getFields();
        List<Field> fixedFields = fields.stream().filter(field -> field.getType().equals(Field.FieldType.FIXED_FIELD)).collect(Collectors.toList());
        //得到 需要跨页的字段
        List<Field> stridePageMerges = fields.stream().filter(field -> field.getStridePageMerge() == true).collect(Collectors.toList());

        Elements auxElements = null;
        if (node.getAuxSelector() != null) {
            auxElements = document.select(node.getAuxSelector());
        }

        for (Element element : document.select(node.getSelector())) {
            if (auxElements != null) {
                Document document1 = Jsoup.parseBodyFragment(auxElements.get(0).toString() + element.toString());
                element = document1.select("body").get(0);
            }
            Item item = new Item();
            preProcess(page, item);
            // TODO: 2017/7/26 因为我看getSelector 下面才有item,所以把固定字段放在了下面。
            fixedFields.forEach(field -> item.merge(field.getName(), field.getFixedField()));

            Element finalElement = element;
            Field.iterate(finalElement, fields, (elements, field) -> {
                // lifecycle method, process [MEDIA] type
                elements = service(item, elements, field, page);
                // process if type is [DATA]
                if (field.getType().contains(Field.FieldType.SubType.DATA)) {
                    Object obj = field.getType().get(finalElement, elements, field);
                    if (obj != null) {
                        if (LINK.equals(field.getName()) || field.getName().equals("postParamsText") || field.getName().equals("baseUrl")) {
                            item.offer(obj.toString());
                            //减少没有的代码执行
                            if (!LINK.equals(field.getName())) {
                                //同一页面 同一字段内容的合并
                                item.merge(field.getName(), obj);
                            }
                        } else
                            //同一页面 同一字段内容的合并
                            item.merge(field.getName(), obj);
                    }
                }
            });

            // add to list if not empty
            if (!node.isIgnoreOnEmpty() || item.hasData() || item.hasLinks()) {
                //这个跨页面的 同一字段内容的合并
                postProcess(page, item, stridePageMerges);
                if (node.isPrimary())
                    item.put(Field.ORIGIN, page.getUri().toString());
                items.add(item);
                if (logger.isDebugEnabled()) {
                    logger.debug(item.getData());
                }
            }
        }
    }

    /**
     * <p>前置处理器</p>
     *
     * @param page page
     * @param item item
     */
    protected void preProcess(WebPage page, Item item) {
        if (!CollectionUtils.isEmpty(page.getMedias()))
            item.getMedias().putAll(page.getMedias());
    }

    /**
     * <p>后置处理器</p>
     *
     * @param page page
     * @param item item
     */
    protected void postProcess(WebPage page, Item item, List<Field> stridePageMerges) {
        //跨页面内容合并
        if (!CollectionUtils.isEmpty(page.getData())) {
            Map<String, Object> data = page.getData();
            if (stridePageMerges.size() != 0) {
                Map<String, Object> itemData = item.getData();
                for (Field field : stridePageMerges) {
                    String key = field.getName();
                    if (data.get(key) instanceof String && itemData.get(key) instanceof String) {
                        data.put(key, String.valueOf(data.get(key)) + String.valueOf(itemData.get(key)));
                    }
                }
            }
            item.getData().putAll(data);
        }

        // 先序遍历深度优先, 要放到后置处理器中
        if (!CollectionUtils.isEmpty(page.getLinks()))
            item.getLinks().addAll(page.getLinks());
    }

    /**
     * <p>处理HTML或ELEMENT类型中的图片、音频、视频等多媒体格式数据</p>
     * <p>other [video|audio|attachment]</p>
     * <p>object[type=application/x-shockwave-flash]</p>
     *
     * @param item     item
     * @param elements elements
     * @param field    field
     */
    protected Elements service(Item item, Elements elements, Field field, WebPage webPage) {
        Field.FieldType type = field.getType();
        if (type.contains(Field.FieldType.SubType.MEDIA)) {
            // 后续操作基于克隆对象进行操作
            Elements c = field.isClone() ? elements.clone() : elements;
            Elements e = type.elements(c, field);
            List<Object> objectList = parser.replacement(e, webPage);
            Map<String, Object> data = item.getData();
            data.put("imagesDownloader", objectList);
            item.addAll(Media.IMAGE, e);
            //如果 图片传过来的是一个数组，只能添加一个关联关系到topics_attachments中，所以下面解析一下图片model不等于0的自动加到关联关系表中
            if (e.size() > 0) {
                for (Element element : e) {
                    if (element.children().size() > 0) {
                        for (Element element2 : element.children()) {
                            if (element2.tag().getName().equals("img")) {
                                Elements es = new Elements();
                                es.add(element2);
                                item.addAll(Media.IMAGE, es);
                            }
                        }
                    }
                }
            }
            return c;
        }
        return elements;
    }

    /**
     * <p>判断节点类型, 如果是链接节点就执行发现URL的操作, 如果是内容节点就执行持久化的操作</p>
     *
     * @param page page
     */
    protected void process(WebPage page) {
        parse(page).getMap().forEach((node, items) -> items.stream().collect(Collectors.partitioningBy(Item::hasLinks)).forEach((branch, list) -> process(page, node, branch, list)));
    }

    protected void process(WebPage page, Node node, Boolean branch, List<Item> items) {
        if (branch) {
            if (node.getOneToManyLink() == true) {
                for (Item item : items) {
                    List<Item> items1 = new ArrayList<>();
                    if (item.getData().get("baseUrl") != null && item.getData().get("_link") != null && node.getFirstPage() != null && node.getSumPage() != null && node.getFirstPage() <= node.getSumPage()) {
                        for (int i = node.getFirstPage(); i <= node.getSumPage(); i++) {
                            item.getData().put("_link", item.getData().get("baseUrl") + Integer.toString(i));
                            items1.add(item);
                        }
                        addLink(page, node, items1);
                    }
                }
            } else {
                addLink(page, node, items);
            }
        } else if (parser.getAnalyzer() == null || parser.getAnalyzer().analyze(items))
            objectMap = parser.getPersistence().persist(page.getName(), node.getName(), items);
    }

    /**
     * <p>将新链接加入到队列中</p>
     *
     * @param parent page
     * @param node   node
     * @param items  items
     */
    protected void addLink(WebPage parent, Node node, List<Item> items) {
        for (Item item : items) {
            String url = item.poll();

            // avoid web black hole
            if (StringUtils.isEmpty(url) || parent.containsUrl(url))
                continue;

            List<Node> nodes;
            int depth = parent.getDepth();
            if (node.hasNext()) {
                nodes = node.getNext();
                depth += 1;
            } else {
                nodes = parent.transfer();
            }

            if (nodes.isEmpty())
                continue;

            // add to url queue, build new url request
            WebPage webPage = new WebPage(url);

            webPage.setCharset(parent.getCharset());
            webPage.setDepth(depth);
            webPage.setName(parent.getName());
            webPage.setHost(parent.getHost());
            webPage.setAgencyIp(parent.getAgencyIp());
            webPage.setAgencyIpPort(parent.getAgencyIpPort());
            webPage.setFilter(node.isFilter());
            webPage.setNodes(nodes);
            webPage.addHistory(parent.getHistories(), url);
            if (node.getHttpMethod() == null) {
                node.setHttpMethod("get");
            }
            webPage.setHttpMethod(node.getHttpMethod());
            webPage.setPostHeaderGroup(node.getPostHeaderGroup());
            if (node.getHttpMethod().equals("post")) {
                webPage.setPostUri(node.getPostUri());
                webPage.setUri(node.getPostUri());
                //post
                if (item.getData().get("postParamsText") != null) {
                    webPage.setPostParamsText(item.getData().get("postParamsText").toString());
                    item.getData().remove("postParamsText");
                }
            }
            //baseUrl 这个基本链接使用完毕之后，及时从下个种子中移除，以便使用新的种子中baseUrl
            if (item.getData().get("baseUrl") != null) {
                item.getData().remove("baseUrl");
            }
            // set value if exists
            if (item.hasData())
                webPage.setData(item.getData());
            if (item.hasMedias()) {
                webPage.setMedias(item.getMedias());
            }
            if (item.hasLinks())
                webPage.setLinks(item.getLinks());

            parser.getFetcher().add(webPage);
        }
    }

}
