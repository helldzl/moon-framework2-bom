package org.moonframework.web.feign;

import org.moonframework.core.security.Signature;
import org.moonframework.core.support.Builder;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pages;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author quzile
 * @version 1.0
 * @since 2018/1/20
 */
public final class Feigns {

    public static ParamsBuilder params() {
        return new ParamsBuilder();
    }

    public static void asRemote() {
        asRemote("ROLE_REMOTE");
    }

    public static void asRemote(String... roles) {
        RequestContextHolder.getRequestAttributes().setAttribute(Signature.REMOTE, String.join(",", roles), RequestAttributes.SCOPE_REQUEST);
    }

    public static class ParamsBuilder implements Builder<Map<String, Object>> {

        private Map<String, Object> params = new HashMap<>();

        public ParamsBuilder page(Function<Pages.PageRequestBuilder, Pages.PageRequestBuilder> function) {
            PageRequest pageRequest = function.apply(Pages.builder()).build();
            Sort sort = pageRequest.getSort();

            params.put("page[number]", pageRequest.getPageNumber() + 1);
            params.put("page[size]", pageRequest.getPageSize());

            if (sort != null) {
                params.put("sort", StreamSupport.stream(sort.spliterator(), false).map(order -> (order.getDirection().isDescending() ? "-" : "") + order.getProperty()).collect(Collectors.joining(",")));
            }
            return this;
        }

        public ParamsBuilder include(String... includes) {
            params.put("include", String.join(",", includes));
            return this;
        }

        public ParamsBuilder fields(String name, Iterable<? extends Field> iterable) {
            // https://stackoverflow.com/questions/23932061/convert-iterable-to-stream-using-java-8-jdk
            // https://stackoverflow.com/questions/23114015/why-does-iterablet-not-provide-stream-and-parallelstream-methods?rq=1
            params.put(fields(name), StreamSupport.stream(iterable.spliterator(), false).map(Field::getFullname).collect(Collectors.joining(",")));
            return this;
        }

        public ParamsBuilder fields(String name, Function<Fields.FieldBuilder, Fields.FieldBuilder> function) {
            fields(name, function.apply(Fields.builder()).build());
            return this;
        }

        public ParamsBuilder filter(String name, Object value) {
            params.put(format(name), value);
            return this;
        }

        public ParamsBuilder filter(String name, Object value, String operator) {
            params.put(format(name, operator), value);
            return this;
        }

        public <T extends Number> ParamsBuilder filter(String name, T value) {
            params.put(format(name), value);
            return this;
        }

        public <T extends Number> ParamsBuilder filter(String name, T value, String operator) {
            params.put(format(name, operator), value);
            return this;
        }

        public <T extends Number> ParamsBuilder filter(String name, Collection<T> list) {
            params.put(format(name), list.stream().map(String::valueOf).collect(Collectors.joining(",")));
            return this;
        }

        public ParamsBuilder custom(String name, Object value) {
            params.put(name, value);
            return this;
        }

        @Override
        public Map<String, Object> build() {
            return params;
        }

        private String fields(String name) {
            return String.format("fields[%s]", name);
        }

        private String format(String name) {
            return String.format("filter[%s]", name);
        }

        private String format(String name, String operator) {
            return String.format("filter[%s:%s]", name, operator);
        }

    }

    public static void main(String[] args) {
        ParamsBuilder fields = Feigns.params()
                .page(p -> p.page(1).size(1).sort(s -> s.add("a", true).add("b", false)))
                .fields("profiles", f -> f.add("nickname"))
                .fields("users", f -> f.add("id").add("username"))
                .filter("id", 1)
                .filter("created", "1", "ne")
                .include("profiles", "users");

        System.out.println(fields.build());
    }

}
