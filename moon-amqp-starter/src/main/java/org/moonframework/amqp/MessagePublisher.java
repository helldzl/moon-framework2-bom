package org.moonframework.amqp;

import org.moonframework.core.util.BeanUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/25
 */
public class MessagePublisher {

    private RabbitTemplate template;

    public MessagePublisher(RabbitTemplate template) {
        this.template = template;
    }

    public void send(String exchange, String routingKey, Object entity, String id, Consumer<Map<String, Object>> consumer) {
        send(exchange, routingKey, BeanUtils.toMap(entity, true), id, entity.getClass().getName(), consumer);
    }

    public void send(String exchange, String routingKey, Map<String, Object> map, String id, String type, Consumer<Map<String, Object>> consumer) {
        Map<String, Object> meta = new HashMap<>();
        meta.put(Resource.META_DATETIME, LocalDateTime.now());
        if (consumer != null)
            consumer.accept(meta);

        Data data = new Data(id, type);
        data.setAttributes(map);

        template.convertAndSend(exchange, routingKey, new Resource(meta, data), new CorrelationData(id));
    }

}
