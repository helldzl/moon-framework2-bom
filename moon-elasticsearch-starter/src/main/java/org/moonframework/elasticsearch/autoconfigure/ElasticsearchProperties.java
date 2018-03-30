package org.moonframework.elasticsearch.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/10
 */
@ConfigurationProperties(ElasticsearchProperties.PREFIX)
public class ElasticsearchProperties {

    public static final String PREFIX = "moon.data.elasticsearch";

    private String clusterName = "elasticsearch";
    private String host = "localhost";
    private int port = 9300;
    private boolean sniff = true;
    private int timeout = 5;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isSniff() {
        return sniff;
    }

    public void setSniff(boolean sniff) {
        this.sniff = sniff;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
