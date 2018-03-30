package org.moonframework.amqp.autoconfigure;

import org.moonframework.amqp.MessagePublisher;
import org.moonframework.core.amqp.Message;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.remoting.client.AmqpProxyFactoryBean;
import org.springframework.amqp.remoting.service.AmqpInvokerServiceExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/21
 */
@Configuration
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitAutoConfiguration {

    @Autowired
    private RabbitProperties properties;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @ConditionalOnMissingBean(MessagePublisher.class)
    @Bean
    public MessagePublisher messagePublisher() {
        return new MessagePublisher(rabbitTemplate);
    }

    @ConditionalOnBean(Message.class)
    @Configuration
    protected class RabbitServerConfig {

        @Autowired
        private Message message;

        @Bean
        public SimpleMessageListenerContainer container() {
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
            container.setConnectionFactory(connectionFactory);
            container.setQueueNames(properties.getRemoteQueue());
            container.setMessageListener(listener());
            return container;
        }

        @Bean
        public AmqpInvokerServiceExporter listener() {
            AmqpInvokerServiceExporter exporter = new AmqpInvokerServiceExporter();
            exporter.setServiceInterface(Message.class);
            exporter.setService(message);
            exporter.setAmqpTemplate(rabbitTemplate);
            return exporter;
        }

    }

    @ConditionalOnMissingBean(Message.class)
    @Configuration
    protected class RabbitClientConfig {

        @Bean
        public AmqpProxyFactoryBean amqpClient() throws Exception {
            // retry template
            RetryTemplate retryTemplate = new RetryTemplate();
            ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
            backOffPolicy.setInitialInterval(500);
            backOffPolicy.setMultiplier(10.0);
            backOffPolicy.setMaxInterval(10000);
            retryTemplate.setBackOffPolicy(backOffPolicy);
            retryTemplate.setRetryPolicy(new SimpleRetryPolicy(3));

            // rabbit template
            RabbitTemplate template = new RabbitTemplate(connectionFactory);
            template.setExchange(properties.getRemoteExchange());
            template.setRoutingKey(properties.getRemoteQueue());
            template.setReplyTimeout(30000);
            template.setRetryTemplate(retryTemplate);

            // factory bean
            AmqpProxyFactoryBean amqpProxyFactoryBean = new AmqpProxyFactoryBean();
            amqpProxyFactoryBean.setAmqpTemplate(template);
            amqpProxyFactoryBean.setServiceInterface(Message.class);
            return amqpProxyFactoryBean;
        }

    }

    /**
     * <p>RPC队列</p>
     */
    @Configuration
    protected class RemoteConfig {

        @Bean
        public Queue remoteQueue() {
            return new Queue(properties.getRemoteQueue());
        }

        @Bean
        public DirectExchange remoteExchange() {
            return new DirectExchange(properties.getRemoteExchange());
        }

        @Bean
        public Binding remoteBinding(
                @Qualifier("remoteQueue") Queue queue,
                @Qualifier("remoteExchange") DirectExchange exchange) {
            return BindingBuilder.bind(queue).to(exchange).with(properties.getRemoteQueue());
        }
    }

    /**
     * <p>数据传输的队列</p>
     */
    @Configuration
    protected class DataConfig {

        // server

        @Bean
        public Queue serverDataQueue() {
            return new Queue(properties.getServerDataQueue());
        }

        @Bean
        public DirectExchange serverDataExchange() {
            return new DirectExchange(properties.getServerDataExchange());
        }

        @Bean
        public Binding serverDataBinding(
                @Qualifier("serverDataQueue") Queue queue,
                @Qualifier("serverDataExchange") DirectExchange exchange) {
            return BindingBuilder.bind(queue).to(exchange).with(properties.getServerDataQueue());
        }

        // client

        @Bean
        public Queue clientDataQueue() {
            return new Queue(properties.getClientDataQueue());
        }

        @Bean
        public DirectExchange clientDataExchange() {
            return new DirectExchange(properties.getClientDataExchange());
        }

        @Bean
        public Binding clientDataBinding(
                @Qualifier("clientDataQueue") Queue queue,
                @Qualifier("clientDataExchange") DirectExchange exchange) {
            return BindingBuilder.bind(queue).to(exchange).with(properties.getClientDataQueue());
        }

    }

    /**
     * <p>事件队列</p>
     */
    @Configuration
    protected class EventConfig {

        // server

        @Bean
        public Queue serverEventQueue() {
            return new Queue(properties.getServerEventQueue());
        }

        @Bean
        public DirectExchange serverEventExchange() {
            return new DirectExchange(properties.getServerEventExchange());
        }

        @Bean
        public Binding serverEventBinding(
                @Qualifier("serverEventQueue") Queue queue,
                @Qualifier("serverEventExchange") DirectExchange exchange) {
            return BindingBuilder.bind(queue).to(exchange).with(properties.getServerEventQueue());
        }

        // client

        @Bean
        public Queue clientEventQueue() {
            return new Queue(properties.getClientEventQueue());
        }

        @Bean
        public DirectExchange clientEventExchange() {
            return new DirectExchange(properties.getClientEventExchange());
        }

        @Bean
        public Binding clientEventBinding(
                @Qualifier("clientEventQueue") Queue queue,
                @Qualifier("clientEventExchange") DirectExchange exchange) {
            return BindingBuilder.bind(queue).to(exchange).with(properties.getClientEventQueue());
        }

    }

}
