package org.moonframework.web.jsonapi;

import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/22
 */
public class ResourceObject {

    private String type;
    private Long id;
    private Map<String, Object> attributes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
