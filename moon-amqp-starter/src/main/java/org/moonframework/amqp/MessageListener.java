package org.moonframework.amqp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/24
 */
public abstract class MessageListener<T> implements InitializingBean, DisposableBean, Runnable {

    protected final Log logger = LogFactory.getLog(this.getClass());

    private Map<String, MessageEventProcessor> processors = new HashMap<>();
    private ExecutorService service = Executors.newSingleThreadExecutor();
    private volatile boolean run = true;

    private final RabbitTemplate template;
    private final String queueName;
    private long timeoutMillis = 3000;

    public MessageListener(RabbitTemplate template, String queueName) {
        this.template = template;
        this.queueName = queueName;
    }

    /**
     * <p><获得事件处理器/p>
     *
     * @param name name
     * @return MessageEventProcessor
     */
    public MessageEventProcessor get(String name) {
        return processors.get(name);
    }

    /**
     * <p>添加事件处理器</p>
     *
     * @param name      name
     * @param processor processor
     * @return MessageEventProcessor
     */
    public MessageEventProcessor put(String name, MessageEventProcessor processor) {
        return processors.put(name, processor);
    }

    /**
     * <p>删除事件处理器</p>
     *
     * @param name name
     * @return MessageEventProcessor
     */
    public MessageEventProcessor remove(String name) {
        return processors.remove(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        while (run) {
            try {
                T t = (T) template.receiveAndConvert(queueName, timeoutMillis);
                if (t != null)
                    accept(t);
            } catch (Exception e) {
                logger.error("error", e);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        service.submit(this);
    }

    @Override
    public void destroy() throws Exception {
        run = false;
        service.shutdown();
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    protected abstract void accept(T t);

}
