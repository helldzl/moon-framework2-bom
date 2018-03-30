package org.moonframework.amqp.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/21
 */
@ConfigurationProperties(RabbitProperties.PREFIX)
public class RabbitProperties {

    public static final String PREFIX = "moon.amqp.rabbit";

    public static final String REMOTE_QUEUE = "remote.direct.queue";
    public static final String REMOTE_EXCHANGE = "remote.direct.exchange";

    public static final String SERVER_DATA_QUEUE = "server.data.direct.queue";
    public static final String SERVER_DATA_EXCHANGE = "server.data.direct.exchange";
    public static final String CLIENT_DATA_QUEUE = "client.data.direct.queue";
    public static final String CLIENT_DATA_EXCHANGE = "client.data.direct.exchange";

    public static final String SERVER_EVENT_QUEUE = "server.event.direct.queue";
    public static final String SERVER_EVENT_EXCHANGE = "server.event.direct.exchange";
    public static final String CLIENT_EVENT_QUEUE = "client.event.direct.queue";
    public static final String CLIENT_EVENT_EXCHANGE = "client.event.direct.exchange";

    // RPC
    private String remoteQueue = REMOTE_QUEUE;
    private String remoteExchange = REMOTE_EXCHANGE;

    // server 端发送数据的
    private String serverDataQueue = SERVER_DATA_QUEUE;
    private String serverDataExchange = SERVER_DATA_EXCHANGE;

    // client 端发送数据的
    private String clientDataQueue = CLIENT_DATA_QUEUE;
    private String clientDataExchange = CLIENT_DATA_EXCHANGE;

    // server 端发送的事件
    private String serverEventQueue = SERVER_EVENT_QUEUE;
    private String serverEventExchange = SERVER_EVENT_EXCHANGE;

    // client 端发送的事件
    private String clientEventQueue = CLIENT_EVENT_QUEUE;
    private String clientEventExchange = CLIENT_EVENT_EXCHANGE;

    public String getRemoteQueue() {
        return remoteQueue;
    }

    public void setRemoteQueue(String remoteQueue) {
        this.remoteQueue = remoteQueue;
    }

    public String getRemoteExchange() {
        return remoteExchange;
    }

    public void setRemoteExchange(String remoteExchange) {
        this.remoteExchange = remoteExchange;
    }

    public String getServerDataQueue() {
        return serverDataQueue;
    }

    public void setServerDataQueue(String serverDataQueue) {
        this.serverDataQueue = serverDataQueue;
    }

    public String getServerDataExchange() {
        return serverDataExchange;
    }

    public void setServerDataExchange(String serverDataExchange) {
        this.serverDataExchange = serverDataExchange;
    }

    public String getClientDataQueue() {
        return clientDataQueue;
    }

    public void setClientDataQueue(String clientDataQueue) {
        this.clientDataQueue = clientDataQueue;
    }

    public String getClientDataExchange() {
        return clientDataExchange;
    }

    public void setClientDataExchange(String clientDataExchange) {
        this.clientDataExchange = clientDataExchange;
    }

    public String getServerEventQueue() {
        return serverEventQueue;
    }

    public void setServerEventQueue(String serverEventQueue) {
        this.serverEventQueue = serverEventQueue;
    }

    public String getServerEventExchange() {
        return serverEventExchange;
    }

    public void setServerEventExchange(String serverEventExchange) {
        this.serverEventExchange = serverEventExchange;
    }

    public String getClientEventQueue() {
        return clientEventQueue;
    }

    public void setClientEventQueue(String clientEventQueue) {
        this.clientEventQueue = clientEventQueue;
    }

    public String getClientEventExchange() {
        return clientEventExchange;
    }

    public void setClientEventExchange(String clientEventExchange) {
        this.clientEventExchange = clientEventExchange;
    }
}
