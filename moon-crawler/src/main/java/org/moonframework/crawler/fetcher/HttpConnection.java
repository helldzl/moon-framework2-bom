package org.moonframework.crawler.fetcher;

import org.moonframework.crawler.parse.Parser;
import org.moonframework.crawler.storage.WebPage;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/7/11
 */
public interface HttpConnection {

    <T extends WebPage> void request(Fetcher fetcher, Parser parser, T page);

}
