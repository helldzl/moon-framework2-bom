package org.moonframework.elasticsearch.aggregation;

import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/11/28
 */
public class Bucket {

    private Map<String, Object> aggregations;
    private NameField nameField;
    private String key;
    private Object value;

    public Bucket(Map<String, Object> aggregations, NameField nameField, String key, Object value) {
        this.aggregations = aggregations;
        this.nameField = nameField;
        this.key = key;
        this.value = value;
    }

    public Map<String, Object> getAggregations() {
        return aggregations;
    }

    public void setAggregations(Map<String, Object> aggregations) {
        this.aggregations = aggregations;
    }

    public NameField getNameField() {
        return nameField;
    }

    public void setNameField(NameField nameField) {
        this.nameField = nameField;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public static class NameField {

        // name and field
        private String name;
        private String field;
        // origin name, null able
        private String origin;

        public NameField(String name, String field) {
            this.name = name;
            this.field = field;
        }

        public NameField(String name, String field, String origin) {
            this.name = name;
            this.field = field;
            this.origin = origin;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }
    }

}
