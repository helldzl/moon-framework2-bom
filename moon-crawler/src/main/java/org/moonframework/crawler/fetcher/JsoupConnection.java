package org.moonframework.crawler.fetcher;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.moonframework.crawler.parse.Parser;
import org.moonframework.crawler.storage.WebPage;

import java.io.IOException;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/7/11
 */
public class JsoupConnection implements HttpConnection {

    @Override
    public <T extends WebPage> void request(Fetcher fetcher, Parser parser, T page) {
        try {
            Connection connect = Jsoup.connect(page.getUri().toString());
            String html = connect.get().toString();
            page.setStatusCode(200);
            page.setHtml(html);
            parser.execute(page);
        } catch (IOException e) {
            throw new IllegalStateException();
        } finally {
            fetcher.release();
        }
    }

}
