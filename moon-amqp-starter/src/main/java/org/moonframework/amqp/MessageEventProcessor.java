package org.moonframework.amqp;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/24
 */
@FunctionalInterface
public interface MessageEventProcessor {

    void trigger();

}
