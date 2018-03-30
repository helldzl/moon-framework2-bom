package org.moonframework.crawler.facade;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.moonframework.crawler.fetcher.Fetcher;
import org.moonframework.crawler.parse.Parser;
import org.moonframework.crawler.storage.WebPage;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/9/10
 */
public class Spider implements AutoCloseable, InitializingBean, DisposableBean {

    protected final Log logger = LogFactory.getLog(this.getClass());

    private ExecutorService service = Executors.newSingleThreadExecutor();
    private Fetcher fetcher;
    private Parser parser;

    public Spider(Fetcher fetcher, Parser parser) {
        this.fetcher = fetcher;
        this.parser = parser;
    }

    public void fetchUrl(WebPage page) {
        fetcher.add(page);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        service.submit(fetcher);
    }

    @Override
    public void destroy() throws Exception {
        close();
    }

    @Override
    public void close() throws Exception {
        service.shutdown();
        fetcher.close();
        parser.close();
    }

    public Fetcher getFetcher() {
        return fetcher;
    }

    public void setFetcher(Fetcher fetcher) {
        this.fetcher = fetcher;
    }

    public Parser getParser() {
        return parser;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }
}
