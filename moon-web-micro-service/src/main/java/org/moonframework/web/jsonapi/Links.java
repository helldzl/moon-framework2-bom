package org.moonframework.web.jsonapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import org.moonframework.core.json.ResponseView;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/15
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Links {

    private String self;
    private String first;
    private String prev;
    private String next;
    private String last;

    @JsonView(ResponseView.class)
    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    @JsonView(ResponseView.class)
    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    @JsonView(ResponseView.class)
    public String getPrev() {
        return prev;
    }

    public void setPrev(String prev) {
        this.prev = prev;
    }

    @JsonView(ResponseView.class)
    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    @JsonView(ResponseView.class)
    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
