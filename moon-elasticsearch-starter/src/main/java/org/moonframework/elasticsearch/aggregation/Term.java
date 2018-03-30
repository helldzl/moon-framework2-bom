package org.moonframework.elasticsearch.aggregation;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/11/25
 */
public class Term implements AggregationBuilderHandler<Term> {

    private String name;
    private String field;

    private int size;
    private Terms.Order order;

    private List<Term> list = new ArrayList<>();

    public Term(String name, String field) {
        this.name = name;
        this.field = field;
    }

    @Override
    public Iterator<Term> iterator() {
        return list.iterator();
    }

    @Override
    public AbstractAggregationBuilder build() {
        TermsBuilder termsBuilder = AggregationBuilders.terms(name);
        termsBuilder.field(field);
        termsBuilder.size(size);
        if (order != null)
            termsBuilder.order(order);
        return termsBuilder;
    }

    public Term add(Term... term) {
        Arrays.stream(term).forEach(t -> list.add(t));
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Terms.Order getOrder() {
        return order;
    }

    public void setOrder(Terms.Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        if (list.isEmpty()) {
            return String.format("{%s}", name);
        } else {
            return String.format("{%s, %s}", name, list);
        }
    }
}
