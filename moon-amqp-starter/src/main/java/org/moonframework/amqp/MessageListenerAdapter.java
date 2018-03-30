package org.moonframework.amqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * <p>消息监听器, 用于接收消息, 监听MQ队列投递过来的消息, 并将之转换为BaseEntity</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/4/24
 */
@Deprecated
public abstract class MessageListenerAdapter extends AbstractResourceMessageListener {

    public MessageListenerAdapter(RabbitTemplate template, String queueName) {
        super(template, queueName);
    }

    @Override
    public void accept(Resource.Method method, Resource resource) {
        try {
            switch (method) {
                case POST:
                    doPost(resource);
                    break;
                case DELETE:
                    doDelete(resource);
                    break;
                case PATCH:
                    doPatch(resource);
                    break;
                case PUT:
                    doPut(resource);
                    break;
                case GET:
                    doGet(resource);
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // hook methods

    protected void doPost(Resource resource) throws Exception {

    }

    protected void doDelete(Resource resource) throws Exception {

    }

    protected void doPatch(Resource resource) throws Exception {

    }

    protected void doPut(Resource resource) throws Exception {

    }

    protected void doGet(Resource resource) throws Exception {

    }

}
