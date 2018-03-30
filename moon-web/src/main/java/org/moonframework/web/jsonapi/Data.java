package org.moonframework.web.jsonapi;

import com.fasterxml.jackson.annotation.JsonView;
import org.moonframework.core.json.ResponseView;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/15
 */
public class Data<T> extends Response {

    /**
     * Resource objects appear in a JSON API document to represent resources.
     */
    private T data;

    public static <T> Data<T> of(T t) {
        return new Data<>(t);
    }

    public static <T> Data<T> of(Responses.DefaultBuilder builder, T t) {
        return new Data<>(builder, t);
    }

    public Data() {
    }

    public Data(T data) {
        this.data = data;
    }

    public Data(Responses.DefaultBuilder builder, T data) {
        super(builder);
        this.data = data;
    }

    @JsonView(ResponseView.class)
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
