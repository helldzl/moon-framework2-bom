package org.moonframework.amqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/16
 */
public abstract class AbstractResourceMessageListener extends AbstractMessageListener<Resource> {

    public AbstractResourceMessageListener(RabbitTemplate template, String queueName) {
        super(template, queueName);
    }

}
