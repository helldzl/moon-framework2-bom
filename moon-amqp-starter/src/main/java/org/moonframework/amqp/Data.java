package org.moonframework.amqp;

import java.io.Serializable;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/24
 */
public class Data implements Serializable {

    private String id;
    private String type;
    private Map<String, Object> attributes;

    public Data(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
