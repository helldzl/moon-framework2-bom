package org.moonframework.model.mybatis.domain;

import com.fasterxml.jackson.annotation.JsonView;
import org.moonframework.core.json.View;

import java.io.Serializable;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/12/9
 */
public class Response implements Serializable {

    private static final long serialVersionUID = -7428785006765669701L;
    private String code;
    private String message;

    public Response(String code) {
        this.code = code;
    }

    public Response(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @JsonView(View.class)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonView(View.class)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
