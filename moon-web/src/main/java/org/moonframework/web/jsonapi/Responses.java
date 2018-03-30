package org.moonframework.web.jsonapi;

import org.springframework.data.domain.Page;

import java.util.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/16
 */
public final class Responses {

    public static DefaultBuilder builder() {
        return new DefaultBuilder();
    }

    public static Error.ErrorBuilder errorBuilder() {
        Error.ErrorBuilder builder = new Error.ErrorBuilder();
        return builder;
    }

    public static Error error(String status, String title, String detail) {
        return new Error.ErrorBuilder().status(status).title(title).detail(detail).build();
    }

    public static Error error(String status, String title) {
        return new Error.ErrorBuilder().status(status).title(title).build();
    }

    public static class DefaultBuilder {

        private Map<String, Object> meta;
        private List<Error> errors;
        private Links links;

        public DefaultBuilder() {
        }

        public DefaultBuilder meta(String key, Object value) {
            if (this.meta == null)
                this.meta = new LinkedHashMap<>();
            this.meta.put(key, value);
            return this;
        }

        public DefaultBuilder meta(Map<String, Object> meta) {
            if (this.meta == null)
                this.meta = new LinkedHashMap<>();
            this.meta.putAll(meta);
            return this;
        }

        public DefaultBuilder page(Page page) {
            return page(page, null, null);
        }

        public DefaultBuilder page(Page page, String prefix, Map<String, String[]> parameterMap) {
            if (this.meta == null)
                this.meta = new LinkedHashMap<>();
            this.meta.put("totalPages", page.getTotalPages());
            this.meta.put("totalElements", page.getTotalElements());
            this.meta.put("size", page.getSize());
            this.meta.put("number", page.getNumber() + 1);
            this.meta.put("numberOfElements", page.getNumberOfElements());
            this.meta.put("first", page.isFirst());
            this.meta.put("last", page.isLast());
            this.meta.put("sort", page.getSort());

            if (prefix != null) {
                StringBuilder sb = new StringBuilder("");
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    String key = entry.getKey();
                    if (key.matches("page\\[[A-Za-z]+\\]")) {
                        continue;
                    }
                    for (String value : entry.getValue()) {
                        sb.append(key);
                        sb.append("=");
                        sb.append(value);
                        sb.append("&");
                    }
                }

                if (page.getTotalPages() > 0) {
                    String pattern = "%s?%spage[number]=%d&page[size]=%d";
                    Links link = new Links();
                    link.setSelf(String.format(pattern, prefix, sb, page.getNumber() + 1, page.getSize()));
                    link.setFirst(String.format(pattern, prefix, sb, 1, page.getSize()));
                    link.setLast(String.format(pattern, prefix, sb, page.getTotalPages(), page.getSize()));
                    if (page.getNumber() > 0) {
                        link.setPrev(String.format(pattern, prefix, sb, page.getNumber(), page.getSize()));
                    }
                    if (page.getNumber() + 1 < page.getTotalPages()) {
                        link.setNext(String.format(pattern, prefix, sb, page.getNumber() + 2, page.getSize()));
                    }
                    this.links = link;
                }
            }
            return this;
        }

        public Response errors(List<Error> errors) {
            this.errors = errors;
            return new Errors(this);
        }

        public Response errors(Error... errors) {
            this.errors = Arrays.asList(errors);
            return new Errors(this);
        }

        public Response error(Error error) {
            if (errors == null)
                errors = new ArrayList<>();
            errors.add(error);
            return new Errors(this);
        }

        public <T> Response data(T data) {
//            if (meta == null && data == null && errors == null)
//                throw new IllegalArgumentException("A document MUST contain at least one of the following top-level members:[meta, data, errors]");
            if (data != null && errors != null)
                throw new IllegalArgumentException("The members data and errors MUST NOT coexist in the same document.");
            return new Data<>(this, data);
        }

        public Map<String, Object> getMeta() {
            return meta;
        }

        public List<Error> getErrors() {
            return errors;
        }

        public Links getLinks() {
            return links;
        }
    }

}
