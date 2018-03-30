package org.moonframework.crawler.storage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.select.Elements;
import org.moonframework.core.amqp.Message;
import org.moonframework.core.util.ObjectMapperFactory;
import org.moonframework.crawler.util.ElementUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/6/16
 */
public abstract class PersistenceAdapter implements Persistence {

    protected final static Log logger = LogFactory.getLog(PersistenceAdapter.class);

    private Message message;

    @Override
    public Map<Long, Object> persist(String name, String className, List<Item> items) {
        for (Item item : items) {
            // primary data
            Map<String, Object> data = item.getData();

            // check if exists, if true than ignore this record
            Object identity;
            if (data.containsKey(Field.ID) && (identity = data.get(Field.ID)) != null && exists(className, (String) identity)) {
                if (logger.isInfoEnabled()) {
                    logger.info("already exists, identity : " + identity);
                }
                continue;
            }

            // to json if necessary
            json(data);

            // get origin url
            String origin = (String) data.get(Field.ORIGIN);

            // meta data
            Map<String, String> meta = new HashMap<>(3);
            meta.put("name", name);
            meta.put("origin", origin);
            meta.put("className", className);

            // relations data
            Map<String, Set<Long>> relations = item.getMedias().entrySet().stream().collect(Collectors.groupingBy(entry -> entry.getKey().getName(), Collectors.reducing(new HashSet<>(), entry -> set(entry.getValue()), this::merge)));
            List<Object> attachmentsList = null;
            if (item.getData().get("imagesDownloader") != null) {
                attachmentsList = (List<Object>) (item.getData().get("imagesDownloader"));
                data.remove("imagesDownloader");
            }

            // save resource and relations
            Map<String, Object> result = message.sendAndReceive(meta, data, relations);
            //判断是否需要一个图片下载的list
            Map<Long, Object> objectMap = new HashMap<>();
            if (result.get("id") != null) {
                objectMap.put(Long.parseLong(result.get("id").toString()), attachmentsList);
            }
            // update url
            visited(name, className, origin);
            return objectMap;
        }
        return null;
    }

    protected Set<Long> merge(Set<Long> left, Set<Long> right) {
        left.addAll(right);
        return left;
    }

    protected Set<Long> set(Elements elements) {
        Set<Long> ids = new HashSet<>();
        ElementUtils.iterate(elements.iterator(), null, (parent, node) -> {
            if (node.attr(Field.DATA_ID) != "" && node.attr("data-model") != null && node.attr("data-model") != "")
                ids.add(Long.valueOf(node.attr(Field.DATA_ID)));
        });
        return ids;
    }

    protected void json(Map<String, Object> data) {
        data.putAll(data.entrySet().stream().filter(entry -> entry.getKey().startsWith(Field.JSON)).collect(Collectors.toMap(entry -> entry.getKey().substring(Field.JSON.length()), this::writeValueAsString)));
    }

    protected String writeValueAsString(Map.Entry<String, Object> entry) {
        return ObjectMapperFactory.writeValueAsString(entry.getValue());
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * <p>资源是否已存在</p>
     *
     * @param type     资源类型
     * @param identity 资源唯一标识
     * @return true if exists
     */
    protected abstract boolean exists(String type, String identity);

    /**
     * <p>对已处理的URL进行持久化</p>
     *
     * @param name 持久化对象集合的简称, e.g : products, brands
     * @param type 资源类型
     * @param url  统一资源定位符
     */
    protected abstract void visited(String name, String type, String url);

}
