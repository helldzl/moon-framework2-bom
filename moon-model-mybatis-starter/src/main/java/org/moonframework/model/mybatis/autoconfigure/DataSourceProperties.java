package org.moonframework.model.mybatis.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author quzile
 * @version 1.0
 * @since 2018/1/10
 */
@ConfigurationProperties(DataSourceProperties.PREFIX)
public class DataSourceProperties {

    public static final String PREFIX = "moon.model.mybatis";

    private String configLocation = "mybatis-config.xml";

    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }
}
