package org.moonframework.crawler.storage;

import org.moonframework.core.support.Builder;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.net.URI;
import java.util.*;

/**
 * <ul>
 * <li>Node节点表示每个网页中一部分被关注或感兴趣的内容块（data block）的配置信息</li>
 * <li>一个网页可以有多个独立的部分, 也就是说一个网页可以有多个Node节点配置</li>
 * <li>Node节点如果有元数据项[_link]属性, 则使用该节点的[next]属性（如果当前节点没有[next]则使用父节点的[next]属性）链接到下一个网页节点. (非尾部节点 非叶子节点 非tail节点)</li>
 * <li>Node节点如果没有元数据项[_link]属性, 则该节点将被数据持久化接口调用, 注意:就算有next节点不会使用到. (尾部节点 叶子节点 tail节点)</li>
 * </ul>
 *
 * @author quzile
 * @version 1.0
 * @since 2016/9/1
 */
public class Node implements Iterable<Node>, Serializable {

    private static final long serialVersionUID = -8307303626942880435L;

    /**
     * <p>当多个Node的数据合并时, 使用primary节点的origin作为数据来源主URL, primary节点会将origin属性一直传递下去, 直到遇到另一个primary节点</p>
     */
    private boolean primary = true;

    /**
     * <p>是否是transfer节点, 默认为true, 当[_link]节点的next为null时, transfer节点会作为下一级链接的配置节点</p>
     */
    private boolean transfer = true;

    /**
     * <p>是否对[_link]节点执行过滤器链, 默认false</p>
     */
    private boolean filter;

    /**
     * <p>为空时忽略, 默认true</p>
     */
    private boolean ignoreOnEmpty = true;

    /**
     * <p>Css选择器: 选择一个网页中用户关注的一个区域块(Area Block), 该区域块是Elements类型, 是List的实现</p>
     */
    private String selector;

    /**
     * <p>optional</p>
     * <p>Class全限定类名称: 通常为叶子节点使用, 只有叶子节点会被持久化接口调用, 通过反射将数据集绑定到指定的类中</p>
     */
    private String name;

    /**
     * <p>删除指定的元素, 用来过滤Html文档中不想处理的元素区域</p>
     */
    private Remove remove;

    /**
     * 数据项集合配置
     */
    private List<Field> fields;

    /**
     * 对下一级节点的描述
     */
    private List<Node> next;
    /**
     * httpMethod方式
     */
    private String httpMethod;

    /*
    * post需要的headergroup
    * */
    private Map<String,String> postHeaderGroup;
    /*
    * post参数从页面获取
    * */
    private List<Field> postsParams;

    /*
    * post另外调用的url
    * */
    private URI postUri;

    /*
    * post参数辅助selector  css选择器
    * */
    private String auxSelector;

    /*
   * 初始化url的页号 第一页 firstPage
    * */
    private Integer firstPage;

    /*
    * 基础url
    * */
    private String baseUrl;
    /*
    * 共多少页
    * */
    private Integer sumPage;

    /*
   * 是否生成由一个当前一个连接生成 n条link，并生成种子 开关
   * */
    private Boolean oneToManyLink = false;






    public static NodeBuilder builder() {
        return new NodeBuilder();
    }

    public Node() {
    }

    public Node(String selector) {
        this.selector = selector;
    }

    public Node(NodeBuilder builder) {
        this.primary = builder.primary;
        this.transfer = builder.transfer;
        this.filter = builder.filter;
        this.ignoreOnEmpty = builder.ignoreOnEmpty;
        this.selector = builder.selector;
        this.name = builder.name;
        this.remove = builder.remove;
        this.fields = builder.fields;
        this.next = builder.next;
        this.httpMethod = builder.httpMethod;
        this.postHeaderGroup = builder.postHeaderGroup;
        this.postsParams = builder.postsParams;
        this.postUri=builder.postUri;
        this.auxSelector=builder.auxSelector;
        this.firstPage=builder.firstPage;
        this.sumPage=builder.sumPage;
        this.baseUrl=builder.baseUrl;
        this.oneToManyLink = builder.oneToManyLink;
    }

    @Override
    public Iterator<Node> iterator() {
        if (next == null)
            return Collections.emptyIterator();
        return next.iterator();
    }

    public boolean hasNext() {
        return !CollectionUtils.isEmpty(next);
    }

    // get and set method


    public URI getPostUri() {
        return postUri;
    }

    public void setPostUri(URI postUri) {
        this.postUri = postUri;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isTransfer() {
        return transfer;
    }

    public void setTransfer(boolean transfer) {
        this.transfer = transfer;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public boolean isIgnoreOnEmpty() {
        return ignoreOnEmpty;
    }

    public void setIgnoreOnEmpty(boolean ignoreOnEmpty) {
        this.ignoreOnEmpty = ignoreOnEmpty;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Remove getRemove() {
        return remove;
    }

    public void setRemove(Remove remove) {
        this.remove = remove;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Node> getNext() {
        return next;
    }

    public void setNext(List<Node> next) {
        this.next = next;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Map<String, String> getPostHeaderGroup() {
        return postHeaderGroup;
    }

    public void setPostHeaderGroup(Map<String, String> postHeaderGroup) {
        this.postHeaderGroup = postHeaderGroup;
    }

    public List<Field> getPostsParams() {
        return postsParams;
    }

    public void setPostsParams(List<Field> postsParams) {
        this.postsParams = postsParams;
    }

    public String getAuxSelector() {
        return auxSelector;
    }

    public void setAuxSelector(String auxSelector) {
        this.auxSelector = auxSelector;
    }

    public Integer getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(Integer firstPage) {
        this.firstPage = firstPage;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Integer getSumPage() {
        return sumPage;
    }

    public void setSumPage(Integer sumPage) {
        this.sumPage = sumPage;
    }

    public Boolean getOneToManyLink() {
        return oneToManyLink;
    }

    public void setOneToManyLink(Boolean oneToManyLink) {
        this.oneToManyLink = oneToManyLink;
    }

    public static class NodeBuilder implements Builder<Node> {

        private boolean primary = true;
        private boolean transfer = true;
        private boolean filter;
        private boolean ignoreOnEmpty = true;
        private String selector;
        private String name;
        private Remove remove;
        private List<Field> fields;
        private List<Node> next;
        private String httpMethod;
        private Map<String,String> postHeaderGroup;
        //post参数从页面获取
        private List<Field> postsParams;
        private URI postUri;
        private String auxSelector;
        private Integer firstPage;
        private String baseUrl;
        private Integer sumPage;
        private Boolean oneToManyLink = false;

        public NodeBuilder primary(boolean primary) {
            this.primary = primary;
            return this;
        }

        public NodeBuilder transfer(boolean transfer) {
            this.transfer = transfer;
            return this;
        }

        public NodeBuilder filter(boolean filter) {
            this.filter = filter;
            return this;
        }

        public NodeBuilder ignoreOnEmpty(boolean ignoreOnEmpty) {
            this.ignoreOnEmpty = ignoreOnEmpty;
            return this;
        }

        public NodeBuilder selector(String selector) {
            this.selector = selector;
            return this;
        }

        public NodeBuilder auxSelector(String auxSelector) {
            this.auxSelector = auxSelector;
            return this;
        }


        public NodeBuilder name(String name) {
            this.name = name;
            return this;
        }

        public NodeBuilder remove(boolean clone, String... selectors) {
            Remove remove = new Remove();
            remove.setClone(clone);
            remove.setSelectors(Arrays.asList(selectors));
            this.remove = remove;
            return this;
        }

        public NodeBuilder fields(List<Field> fields) {
            this.fields = fields;
            return this;
        }

        public NodeBuilder addField(Field field) {
            if (fields == null)
                fields = new ArrayList<>();
            fields.add(field);
            return this;
        }

        public NodeBuilder next(List<Node> next) {
            this.next = next;
            return this;
        }

        public NodeBuilder addNode(Node... nodes) {
            if (next == null)
                next = new ArrayList<>();
            Collections.addAll(next, nodes);
            return this;
        }

        public NodeBuilder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public  NodeBuilder postHeaderGroup(Map<String,String> postHeaderGroup){
            this.postHeaderGroup=postHeaderGroup;
            return this;
        }

        public NodeBuilder addPostParams(Field field){
            if (postsParams == null)
                postsParams = new ArrayList<>();
            postsParams.add(field);
            return  this;
        }
        public  NodeBuilder  postUri(URI postUri){
            this.postUri=postUri;
            return  this;
        }

        public  NodeBuilder  firstPage(Integer firstPage){
            this.firstPage=firstPage;
            return  this;
        }
        public  NodeBuilder  baseUrl(String baseUrl){
            this.baseUrl=baseUrl;
            return  this;
        }
        public  NodeBuilder  sumPage(Integer sumPage){
            this.sumPage=sumPage;
            return  this;
        }

        public  NodeBuilder oneToManyLink(Boolean oneToManyLink){
            this.oneToManyLink=oneToManyLink;
            return this;
        }

        @Override
        public Node build() {
            return new Node(this);
        }
    }
}
