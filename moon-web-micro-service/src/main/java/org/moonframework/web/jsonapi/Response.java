package org.moonframework.web.jsonapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import org.moonframework.core.json.ResponseView;

import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/16
 */
public class Response {

    /**
     * a meta member can be used to include non-standard meta-information.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> meta;

    /**
     *
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Links links;

    public Response() {
    }

    public Response(Responses.DefaultBuilder builder) {
        this.meta = builder.getMeta();
        this.links = builder.getLinks();
    }

    @JsonView(ResponseView.class)
    public Map<String, Object> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }

    @JsonView(ResponseView.class)
    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }
}
