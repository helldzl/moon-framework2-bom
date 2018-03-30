package org.moonframework.crawler.filter;

/**
 * <p>链接过滤器</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2016/6/17
 */
public interface LinkFilter {

    /**
     * <p>URL过滤</p>
     *
     * @param type type
     * @param url  url
     * @return true if accept
     */
    boolean filter(String type, String url);

    boolean  exist(String type, String url);

}
