package org.moonframework.concurrent.pool;

import java.util.concurrent.Future;

/**
 * @param <T> input type
 * @param <R> the result type
 * @author quzile
 * @version 1.0
 * @since 2016/9/10
 */
public interface Pool<T, R> {

    Future<R> submit(Task<T, R> task);

    Future<R> submit(T t);

    void execute(T t);

    void release();

    void shutdown();

    boolean isShutdown();

}
