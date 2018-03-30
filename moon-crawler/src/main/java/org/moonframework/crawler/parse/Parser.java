package org.moonframework.crawler.parse;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.jsoup.select.Elements;
import org.moonframework.concurrent.pool.AbstractPool;
import org.moonframework.concurrent.pool.Task;
import org.moonframework.crawler.analysis.PageAnalyzer;
import org.moonframework.crawler.fetcher.Fetcher;
import org.moonframework.crawler.storage.Persistence;
import org.moonframework.crawler.storage.WebPage;

import java.util.List;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/6/16
 */
public class Parser extends AbstractPool<WebPage, Void> {

    protected static Log logger = LogFactory.getLog(Parser.class);

    private Fetcher fetcher;

    /**
     * 文档分析器
     */
    private PageAnalyzer analyzer;

    /**
     * 文档优化器
     */
    private Optimizer optimizer = new Optimizer();

    /**
     * 文档持久化
     */
    private Persistence persistence;

    /**
     * 主题相关度计算, 只处理相关的URL
     */
    private RankUrl rankUrl = new PageRankUrl();

    public Parser() {
    }

    public Parser(int size) {
        super(size);
    }

    public Parser(int size, int permits) {
        super(size, permits);
    }

    public Parser(int size, int permits, boolean fair) {
        super(size, permits, fair);
    }

    @Override
    protected Task<WebPage, Void> newTask() {
        return new ParserJob(this);
    }

    /**
     * <p>属性替换, 将HTML中的图片链接替换成本地链接. img a</p>
     *
     * @param elements elements
     */
    protected List<Object> replacement(Elements elements, WebPage webPage) {
        return null;
    }

    protected  void addObjectMap(Map<Long, Object> objectMap) {
    }

    /**
     * <p>不符合主题相关度, 丢弃的URL</p>
     *
     * @param webPage webPage
     */
    protected void discard(WebPage webPage) {

    }

    /**
     * <p>解析出现错误的URL</p>
     *
     * @param webPage webPage
     */
    protected void error(WebPage webPage) {

    }

    // get and set method

    public Fetcher getFetcher() {
        return fetcher;
    }

    public void setFetcher(Fetcher fetcher) {
        this.fetcher = fetcher;
    }

    public PageAnalyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(PageAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    public Optimizer getOptimizer() {
        return optimizer;
    }

    public void setOptimizer(Optimizer optimizer) {
        this.optimizer = optimizer;
    }

    public Persistence getPersistence() {
        return persistence;
    }

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }

    public RankUrl getRankUrl() {
        return rankUrl;
    }

    public void setRankUrl(RankUrl rankUrl) {
        this.rankUrl = rankUrl;
    }

}
