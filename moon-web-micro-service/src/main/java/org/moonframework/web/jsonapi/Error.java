package org.moonframework.web.jsonapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import org.moonframework.core.json.ResponseView;
import org.moonframework.core.support.Builder;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/15
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Error {

    private String status;
    private String code;
    private String title;
    private String detail;
    private Source source;

    private Error() {
    }

    public Error(ErrorBuilder builder) {
        this.status = builder.status;
        this.code = builder.code;
        this.title = builder.title;
        this.detail = builder.detail;
        this.source = builder.source;
    }

    public static ErrorBuilder builder() {
        return new ErrorBuilder();
    }

    public static class ErrorBuilder implements Builder<Error> {

        private String status;
        private String code;
        private String title;
        private String detail;
        private Source source;

        public ErrorBuilder status(String status) {
            this.status = status;
            return this;
        }

        public ErrorBuilder code(String code) {
            this.code = code;
            return this;
        }

        public ErrorBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ErrorBuilder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public ErrorBuilder source(String pointer, String parameter) {
            this.source = new Source(pointer, parameter);
            return this;
        }

        @Override
        public Error build() {
            return new Error(this);
        }
    }

    @JsonView(ResponseView.class)
    public String getStatus() {
        return status;
    }

    @JsonView(ResponseView.class)
    public String getCode() {
        return code;
    }

    @JsonView(ResponseView.class)
    public String getTitle() {
        return title;
    }

    @JsonView(ResponseView.class)
    public String getDetail() {
        return detail;
    }

    @JsonView(ResponseView.class)
    public Source getSource() {
        return source;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Source {

        private final String pointer;
        private final String parameter;

        public Source(String pointer, String parameter) {
            this.pointer = pointer;
            this.parameter = parameter;
        }

        @JsonView(ResponseView.class)
        public String getPointer() {
            return pointer;
        }

        @JsonView(ResponseView.class)
        public String getParameter() {
            return parameter;
        }
    }

}
