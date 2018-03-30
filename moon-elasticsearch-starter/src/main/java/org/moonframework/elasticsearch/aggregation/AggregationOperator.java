package org.moonframework.elasticsearch.aggregation;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.util.*;
import java.util.function.Function;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/26
 */
public class AggregationOperator {

    private static final String REGEX_AGGREGATION = "aggregation\\[[A-Za-z0-9_]+\\]";

    public static boolean addAggregation(SearchRequestBuilder builder, Map<String, String[]> params, int size, String sort) {
        return addAggregation(builder, params, size, sort, null);
    }

    /**
     * @param builder  search builder
     * @param params   request params
     * @param size     aggregation size
     * @param sort     aggregation sort, e.g : term or count
     * @param function function
     * @return true if add any Aggregations
     */
    public static boolean addAggregation(SearchRequestBuilder builder, Map<String, String[]> params, int size, String sort, Function<String, String> function) {
        List<Term> list = params.entrySet()
                .stream()
                .filter(entry -> entry.getKey().matches(REGEX_AGGREGATION))
                .map(entry -> function(entry, size, sort, function))
                .reduce(new ArrayList<>(), (left, right) -> {
                    left.addAll(right);
                    return left;
                });
        list.forEach(term -> builder.addAggregation(AggregationType.buildAggregation(term)));
        return !list.isEmpty();
    }

    public static Terms.Order order(String sort) {
        if (sort != null) {
            if (sort.startsWith("-")) {
                return order(false, sort.substring(1));
            } else {
                return order(true, sort);
            }
        }
        return null;
    }

    private static Terms.Order order(boolean asc, String term) {
        if ("term".equalsIgnoreCase(term)) {
            return Terms.Order.term(asc);
        } else if ("count".equalsIgnoreCase(term)) {
            return Terms.Order.count(asc);
        } else {
            return null;
        }
    }

    private static List<Term> function(Map.Entry<String, String[]> entry, int size, String sort, Function<String, String> function) {
        String name = replace(entry.getKey());
        List<Term> terms = new ArrayList<>();
        for (String value : entry.getValue()) {
            if (function != null)
                value = function.apply(value);
            Term head;
            if (value.contains("-")) {
                Iterator<String> it = Arrays.asList(value.split("-")).iterator();
                String next = it.next();
                head = term(name, next, size, sort);
                Term tail = head;
                while (it.hasNext()) {
                    next = it.next();
                    Term term = term(name, next, size, sort);
                    tail.add(term);
                    tail = term;
                }
            } else {
                head = term(name, value, size, sort);
            }
            terms.add(head);
        }
        return terms;
    }

    private static Term term(String name, String field, int size, String sort) {
        Term term = new Term(name + "," + field, field);
        term.setSize(size);
        term.setOrder(order(sort));
        return term;
    }

    private static String replace(String key) {
        return key.replaceAll(".*\\[|\\]", "");
    }

}
