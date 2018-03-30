package org.moonframework.model.mongodb.enums;

public enum EntityOrder {
    INITIAL (99);

    EntityOrder(Integer code) {
        this.code = code;
    }

    private Integer code;

    public Integer getCode() {
        return code;
    }
}
