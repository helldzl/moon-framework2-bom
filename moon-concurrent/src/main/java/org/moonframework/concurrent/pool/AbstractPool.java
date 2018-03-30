package org.moonframework.concurrent.pool;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/9/10
 */
public abstract class AbstractPool<T, R> implements Pool<T, R>, AutoCloseable {

    private static Log logger = LogFactory.getLog(AbstractPool.class);

    private static final int DEFAULT_PERMITS = 100;
    private static final int DEFAULT_POOL_SIZE = 2;
    private static final int MIN_THREADS = 1;
    private static final int MAX_THREADS = 24;

    /**
     * Size of pool
     */
    private int size;

    /**
     * Pool
     */
    private ExecutorService service;

    /**
     * Semaphore
     */
    private Semaphore semaphore;

    public AbstractPool() {
        this(DEFAULT_POOL_SIZE);
    }

    public AbstractPool(int size) {
        this(size, DEFAULT_PERMITS, false);
    }

    public AbstractPool(int size, int permits) {
        this(size, permits, false);
    }

    /**
     * @param size    pool size
     * @param permits permits
     * @param fair    fair
     */
    public AbstractPool(int size, int permits, boolean fair) {
        if (MIN_THREADS > size || MAX_THREADS < size) {
            int n = Runtime.getRuntime().availableProcessors() - 1;
            this.size = n < 1 ? 1 : n;
        }
        if (size == 1)
            service = Executors.newSingleThreadExecutor();
        else
            service = Executors.newFixedThreadPool(size);

        if (permits > -1)
            this.semaphore = new Semaphore(permits, fair);
    }

    @Override
    public Future<R> submit(Task<T, R> task) {
        try {
            if (semaphore != null)
                semaphore.acquire();
            return service.submit(task);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Future<R> submit(T t) {
        Task<T, R> task = newTask();
        task.accept(t);
        return submit(task);
    }

    @Override
    public void execute(T t) {
        submit(t);
    }

    @Override
    public void release() {
        semaphore.release();
    }

    @Override
    public void shutdown() {
        if (!isShutdown()) {
            service.shutdown();
        }
    }

    @Override
    public boolean isShutdown() {
        return service.isShutdown();
    }

    @Override
    public void close() throws IOException {
        shutdown();
    }

    // get and set method

    public int getSize() {
        return size;
    }

    public ExecutorService getService() {
        return service;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    /**
     * <p>safe net</p>
     *
     * @throws Throwable Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        try {
            close();
        } catch (IOException ignore) {
        }
    }

    /**
     * <p>
     * new instance of Task
     * </p>
     *
     * @return task
     */
    protected abstract Task<T, R> newTask();

}
