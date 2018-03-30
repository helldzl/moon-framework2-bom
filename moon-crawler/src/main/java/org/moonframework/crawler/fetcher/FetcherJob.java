package org.moonframework.crawler.fetcher;

import org.moonframework.concurrent.pool.TaskAdapter;
import org.moonframework.crawler.parse.Parser;
import org.moonframework.crawler.storage.WebPage;

/**
 * <p>this is a web page downloader</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2016/6/3
 */
public class FetcherJob extends TaskAdapter<WebPage, Void> {

    private Fetcher fetcher;
    private Parser parser;

    /**
     * @param fetcher fetcher
     * @param parser  parser
     */
    public FetcherJob(Fetcher fetcher, Parser parser) {
        this.fetcher = fetcher;
        this.parser = parser;
    }

    /**
     * @param page page
     * @return null because this is a async task
     * @throws Exception Exception
     */
    @Override
    protected Void call(WebPage page) throws Exception {
        page.getConnectionType().getInstance().request(fetcher, parser, page);
        return null;
    }

    @Override
    protected void failed(Exception ex) {
        fetcher.release();
    }
}
