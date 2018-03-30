package org.moonframework.crawler.storage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jsoup.select.Elements;
import org.moonframework.crawler.fetcher.ConnectionType;
import org.moonframework.crawler.util.HttpClientUtils;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/6/13
 */
public class WebPage {

    /*
    * post另外调用的url
    * */
    private URI postUri;
    /*
    * 代理IP
    * */
    private  String agencyIp;
    /*
    * 代理IP的端口
    * */
    private  Integer agencyIpPort;

    /*
    * post需要的headergroup
    * */
    private Map<String,String> postHeaderGroup;
    /*
    *post的Map参数
    */
    private Map<String,String> postParamsMap;
    /*
    * post的text参数
    * */
    private String postParamsText;

    /*
    * 网页获取方式get,post(默认是get)
    */
    private  String httpMethod;
    /*
    * 网页字符编码
    * */
    private String charset;
    /**
     * 失败重试次数
     */
    @JsonIgnore
    private int retry;

    /**
     * 网页深度, 如果使用的是优先级队列, 可以根据该属性控制爬取网页的优先级, 深度优先(depth越大优先级越大), 广度优先(depth越小优先级越大)
     */
    @JsonIgnore
    private int depth;

    /**
     * 在每个host中唯一的一个名称, 同一个host下抓取不同数据内容的一个简称 e.g : product, brand
     */
    @JsonIgnore
    private String name;

    /**
     * 主机名称
     */
    @JsonIgnore
    private String host;

    /**
     * URI
     */
    @JsonIgnore
    private URI uri;

    /**
     * 是否参与到过滤器链中
     */
    @JsonIgnore
    private boolean filter;

    /**
     * HTTP状态码
     */
    @JsonIgnore
    private int statusCode;

    /**
     * HTML页面
     */
    @JsonIgnore
    private String html;

    /**
     * The system time in milliseconds for when the page was fetched.
     */
    @JsonIgnore
    private long fetchTime;

    /**
     * data
     */
    @JsonIgnore
    private Map<String, Object> data;

    /**
     * 其他数据部分
     */
    @JsonIgnore
    private Map<Media, Elements> medias;

    /**
     * 采用先序遍历的顺序依次加入链接队列中
     */
    @JsonIgnore
    private Queue<String> links;

    /**
     * 链接类型
     */
    @JsonIgnore
    private ConnectionType connectionType = ConnectionType.HTTP_CLIENT;

    /**
     * 保存页面历史链接, 最多保存最近的n个URL
     */
    @JsonIgnore
    private List<String> histories = new ArrayList<>();

    /**
     * <p>抓取页面的dom节点配置信息, Parser根据节点配置信息进行处理</p>
     * <ul>
     * <li>发现URL</li>
     * <li>处理文本</li>
     * </ul>
     */
    private List<Node> nodes;

    public WebPage() {
    }

    public WebPage(URI uri) {
        this.uri = uri;
    }

    public WebPage(String url) {
        this.uri = HttpClientUtils.newInstance(url);
    }

    public void addHistory(List<String> parent, String url) {
        histories.addAll(parent);
        if (histories.size() >= 3)
            histories.remove(0);
        histories.add(url);
    }

    public boolean containsUrl(String url) {
        return histories.contains(url);
    }

    public List<Node> transfer() {
        if (nodes == null)
            return Collections.emptyList();
        return nodes.stream().filter(node -> node.hasNext() || node.isTransfer()).collect(Collectors.toList());
    }

    public URI getPostUri() {
        return postUri;
    }

    public void setPostUri(URI postUri) {
        this.postUri = postUri;
    }

    public String getAgencyIp() {
        return agencyIp;
    }

    public void setAgencyIp(String agencyIp) {
        this.agencyIp = agencyIp;
    }

    public Integer getAgencyIpPort() {
        return agencyIpPort;
    }

    public void setAgencyIpPort(Integer agencyIpPort) {
        this.agencyIpPort = agencyIpPort;
    }

    public Map<String, String> getPostHeaderGroup() {
        return postHeaderGroup;
    }

    public void setPostHeaderGroup(Map<String, String> postHeaderGroup) {
        this.postHeaderGroup = postHeaderGroup;
    }

    public Map<String, String> getPostParamsMap() {
        return postParamsMap;
    }

    public void setPostParamsMap(Map<String, String> postParamsMap) {
        this.postParamsMap = postParamsMap;
    }

    public String getPostParamsText() {
        return postParamsText;
    }

    public void setPostParamsText(String postParamsText) {
        this.postParamsText = postParamsText;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public long getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(long fetchTime) {
        this.fetchTime = fetchTime;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<Media, Elements> getMedias() {
        return medias;
    }

    public void setMedias(Map<Media, Elements> medias) {
        this.medias = medias;
    }

    public Queue<String> getLinks() {
        return links;
    }

    public void setLinks(Queue<String> links) {
        this.links = links;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public List<String> getHistories() {
        return histories;
    }

    public void setHistories(List<String> histories) {
        this.histories = histories;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    /**
     * @param node node
     */
    public void setNode(Node node) {
        if (this.nodes == null)
            this.nodes = new ArrayList<>();
        nodes.add(node);
    }
}
