package org.moonframework.web.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author quzile
 * @version 1.0
 * @since 2018/1/10
 */
@ConfigurationProperties(MicroServiceProperties.PREFIX)
public class MicroServiceProperties {

    public static final String PREFIX = "moon.web.micro-service";

    private boolean enabled;
    private String exception = "classpath:messages/exceptions";
    private String message= "classpath:messages/messages";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
