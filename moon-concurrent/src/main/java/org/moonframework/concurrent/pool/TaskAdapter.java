package org.moonframework.concurrent.pool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>2016年9月9日 重构代码, 采用信号量控制资源在同一个时间段内可以被访问的数量</p>
 *
 * @param <T> input type
 * @param <R> the return result
 * @author quzile
 * @version 2.0
 * @since 2016/5/27
 */
public abstract class TaskAdapter<T, R> implements Task<T, R> {

    protected static Log logger = LogFactory.getLog(TaskAdapter.class);

    private T t;

    public TaskAdapter() {
    }

    @Override
    public R call() throws Exception {
        try {
            return call(t);
        } catch (Exception e) {
            logger.error("error", e);
            failed(e);
            return null;
        }
    }

    @Override
    public void accept(T t) {
        this.t = t;
    }

    /**
     * @param t type of input
     * @return result type
     * @throws Exception Exception
     */
    protected abstract R call(T t) throws Exception;

    /**
     * @param ex exception
     */
    protected void failed(Exception ex) {

    }

}
