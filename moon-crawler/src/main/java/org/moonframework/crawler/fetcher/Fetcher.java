package org.moonframework.crawler.fetcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.moonframework.concurrent.pool.AbstractPool;
import org.moonframework.concurrent.pool.Task;
import org.moonframework.concurrent.util.LockUtils;
import org.moonframework.crawler.filter.LinkFilter;
import org.moonframework.crawler.parse.Parser;
import org.moonframework.crawler.storage.WebPage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>网页内容提取下载器</p>
 * <p>这里采用双队列, 连接池本身的阻塞队列不能直接使用优先级队列, 所以URL入队时先进入优先级队列（unbounded queue）, 再进入有semaphore限制的连接池队列</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2016/6/3
 */
public class Fetcher extends AbstractPool<WebPage, Void> implements Runnable {

    private static Log logger = LogFactory.getLog(Fetcher.class);

    /**
     * Web content parser
     */
    private Parser parser;

    /**
     * Priority queue for depth first
     */
    private PriorityBlockingQueue<WebPage> queue = new PriorityBlockingQueue<>(10000, (o1, o2) -> {
        if (o1 == o2)
            return 0;
        return Integer.compare(o2.getDepth(), o1.getDepth());
    });

    /**
     * Filter chain
     */
    private List<LinkFilter> filters = new ArrayList<>();

    /**
     * lock
     */
    private ReentrantLock lock = new ReentrantLock();

    public Fetcher() {
    }

    public Fetcher(int size) {
        super(size);
    }

    public Fetcher(int size, int permits) {
        super(size, permits);
    }

    public Fetcher(int size, int permits, boolean fair) {
        super(size, permits, fair);
    }

    /**
     * <p>注册过滤器</p>
     *
     * @param linkFilter linkFilter
     */
    public Fetcher addFilter(LinkFilter linkFilter) {
        filters.add(linkFilter);
        return this;
    }

    @Override
    public void run() {
        LockUtils.tryLock(lock, () -> {
            logger.info("Fetcher is start");
            int count = 0;
            while (!isShutdown()) {
                try {
                    if (count++ % 32 == 0)
                        logger.info(getSemaphore().toString());
                    execute(queue.take());
                } catch (InterruptedException e) {
                    logger.error("error", e);
                }
            }
            logger.info("Fetcher is shutdown");
            return null;
        });
    }

    /**
     * <p>先将URL加入无界的优先级队列, 进行优先级排序</p>
     *
     * @param webPage
     */
    public void add(WebPage webPage) {
        if (webPage.isFilter())
            for (LinkFilter filter : filters)
                if (!filter.filter(webPage.getName(), webPage.getUri().toString()))
                    return;
        queue.add(webPage);
    }

    // get and set method

    public Parser getParser() {
        return parser;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    @Override
    protected Task<WebPage, Void> newTask() {
        return new FetcherJob(this, parser);
    }

}
