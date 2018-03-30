package org.moonframework.web.jsonapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import org.moonframework.core.json.ResponseView;
import org.moonframework.core.util.BeanUtils;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/16
 */
public class Errors extends Response {

    /**
     * Errors Objects
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Error> errors;

    public static Errors fromJson(String json) {
        return BeanUtils.readValue(json, Errors.class);
    }

    private Errors() {
    }

    public Errors(Responses.DefaultBuilder builder) {
        super(builder);
        this.errors = builder.getErrors();
    }

    @JsonView(ResponseView.class)
    public List<Error> getErrors() {
        return errors;
    }

}
