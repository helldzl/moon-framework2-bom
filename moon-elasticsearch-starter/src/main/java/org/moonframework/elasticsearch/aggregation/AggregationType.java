package org.moonframework.elasticsearch.aggregation;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.moonframework.core.support.Builder;
import org.moonframework.elasticsearch.AbstractSearchEngine;
import org.moonframework.elasticsearch.aggregation.Bucket.NameField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by quzile on 2016/8/11.
 */
public enum AggregationType {

    RANGE {
        @Override
        public boolean add(Aggregation aggregation, List<Map<String, Object>> buckets) {
            if (aggregation instanceof Range) {
                Range agg = (Range) aggregation;
                for (Range.Bucket bucket : agg.getBuckets()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("key", bucket.getKeyAsString());
                    map.put("to", bucket.getTo());
                    map.put("from", bucket.getFrom());
                    map.put("doc_count", bucket.getDocCount());
                    aggregations(bucket.getAggregations(), map);
                    buckets.add(map);
                }
                return true;
            }
            return false;
        }
    },

    TERMS {
        @Override
        public boolean add(Aggregation aggregation, List<Map<String, Object>> buckets) {
            if (aggregation instanceof Terms) {
                Terms terms = (Terms) aggregation;
                for (Terms.Bucket bucket : terms.getBuckets()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("key", bucket.getKeyAsString());
                    map.put("doc_count", bucket.getDocCount());
                    aggregations(bucket.getAggregations(), map);
                    buckets.add(map);
                }
                return true;
            }
            return false;
        }
    };

    public static void aggregations(Aggregations aggregations, Map<String, Object> result) {
        if (aggregations == null)
            return;

        aggregations.forEach(aggregation -> {
            String name = aggregation.getName();

            List<Map<String, Object>> buckets = new ArrayList<>();
            Map<String, Object> bucketsMap = new HashMap<>();
            bucketsMap.put(AbstractSearchEngine.FIELD_BUCKETS, buckets);
            result.put(name, bucketsMap);

            for (AggregationType aggregationType : AggregationType.values()) {
                if (aggregationType.add(aggregation, buckets))
                    break;
            }
        });
    }

    public static <T extends Builder<AbstractAggregationBuilder> & Iterable<? extends T>> AbstractAggregationBuilder buildAggregation(T t) {
        AbstractAggregationBuilder builder = t.build();
        if (builder instanceof AggregationBuilder)
            t.forEach(child -> ((AggregationBuilder) builder).subAggregation(buildAggregation(child)));
        return builder;
    }


    public static void aggregations(Map<String, Object> aggregations, Consumer<Bucket> consumer) {
        aggregations(aggregations, null, consumer);
    }

    @SuppressWarnings("unchecked")
    public static void aggregations(Map<String, Object> aggregations, NameField nameField, Consumer<Bucket> consumer) {
        if (aggregations == null)
            return;
        List<Bucket.NameField> pairs = new ArrayList<>();
        aggregations.forEach((key, value) -> {
            if (value instanceof Map) {
                Bucket.NameField nf;
                if (key.contains(",")) {
                    String[] split = key.split(",");
                    nf = new Bucket.NameField(split[0], split[1], key);
                    pairs.add(nf);
                } else {
                    nf = new NameField(key, key);
                }

                Map<String, Object> map = (Map<String, Object>) value;
                List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("buckets");
                if (list != null)
                    list.forEach(stringObjectMap -> aggregations(stringObjectMap, nf, consumer));
            } else if ("key".equals(key) && consumer != null) {
                consumer.accept(new Bucket(aggregations, nameField, key, value));
            }
        });
        // change name
        pairs.stream().filter(nf -> nf.getOrigin() != null).forEach(nf -> {
            Object remove = aggregations.remove(nf.getOrigin());
            if (remove != null)
                aggregations.put(nf.getName(), remove);
        });
    }

    AggregationType() {
    }

    public abstract boolean add(Aggregation aggregation, List<Map<String, Object>> buckets);

}
