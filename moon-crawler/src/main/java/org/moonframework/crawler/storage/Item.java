package org.moonframework.crawler.storage;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.moonframework.crawler.analysis.Analysable;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>页面中的一部分, 映射到数据结构的一条记录, 或数据库中的一行</p>
 * <p>一条记录可以包含多个媒体格式文件: 如音频、视频、附件等</p>
 * <p>
 * <p>2016年6月28日</p>
 * <p>修正: 增加集合管理elements</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2016/6/27
 */
public class Item implements Analysable {

    /**
     * 普通数据部分
     */
    private Map<String, Object> data = new HashMap<>();

    /**
     * 其他数据部分
     */
    private Map<Media, Elements> medias = new EnumMap<>(Media.class);

    /**
     * 采用先序遍历的顺序依次加入链接队列中
     */
    private Queue<String> links = new LinkedList<>();

    @Override
    public Map<String, Object> getContent() {
        return data;
    }

    public Object put(String key, Object value) {
        return data.put(key, value);
    }

    /**
     * <p>merge</p>
     *
     * @param key   key
     * @param value value
     */
    public void merge(String key, Object value) {
        if (data.containsKey(key)) {
            mergeObject(key, value);
        } else {
            data.put(key, value);
        }
    }

    /**
     * <p>merge</p>
     *
     * @param key    key
     * @param second second
     */
    public void mergeObject(String key, Object second) {
        Object first = data.get(key);
        if (first instanceof BigDecimal && second instanceof BigDecimal) {
            BigDecimal x = (BigDecimal) first;
            BigDecimal y = (BigDecimal) second;
            data.put(key, x.add(y));
        } else if (first instanceof String && second instanceof String) {
            data.put(key, String.valueOf(first) + second);
        }
    }

    public boolean add(Media media, Element element) {
        contains(media);
        return medias.get(media).add(element);
    }

    public boolean addAll(Media media, Elements elements) {
        contains(media);
        return medias.get(media).addAll(elements);
    }

    public boolean offer(String link) {
        return links.offer(link);
    }

    public String poll() {
        return links.poll();
    }

    public boolean hasData() {
        return !data.isEmpty();
    }

    public boolean hasMedias() {
        return !medias.isEmpty();
    }

    public boolean hasLinks() {
        return !links.isEmpty();
    }

    // get and set method

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

    //

    private void contains(Media media) {
        if (!medias.containsKey(media))
            medias.put(media, new Elements());
    }

}
