package org.moonframework.model.mongodb.enums;

public enum DeleteState {

    CREATE(1),
    DELETE(0);

    DeleteState(Integer code) {
        this.code = code;
    }

    private Integer code;

    public Integer getCode() {
        return code;
    }
}
