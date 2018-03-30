package org.moonframework.amqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.moonframework.amqp.Resource.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/17
 */
public abstract class AbstractMessageListener<T extends Resource> extends MessageListener<T> implements BiConsumer<Resource.Method, T> {

    private Map<Method, Consumer<T>> methods = new EnumMap<>(Method.class);

    public AbstractMessageListener(RabbitTemplate template, String queueName) {
        super(template, queueName);
    }

    @Override
    protected void accept(T resource) {
        try {
            Map<String, Object> meta = resource.getMeta();
            Data data = resource.getData();
            if (meta == null) {
                return;
            }

            if (meta.containsKey(META_EVENT)) {
                String event = (String) meta.get(META_EVENT);
                MessageEventProcessor processor = get(event);
                if (processor != null)
                    processor.trigger();
            } else if (data != null) {
                Method method = (Method) meta.get(META_METHOD);
                accept(method, resource);
            }
        } catch (Exception e) {
            logger.error("error", e);
        }
    }

    @Override
    public void accept(Method method, T t) {
        methods.get(method).accept(t);
    }

    public Consumer<T> put(Method method, Consumer<T> consumer) {
        return methods.put(method, consumer);
    }

    public Consumer<T> remove(Method method) {
        return methods.remove(method);
    }

}
