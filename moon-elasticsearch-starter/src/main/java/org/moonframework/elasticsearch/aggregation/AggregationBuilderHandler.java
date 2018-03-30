package org.moonframework.elasticsearch.aggregation;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.moonframework.core.support.Builder;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/11/25
 */
public interface AggregationBuilderHandler<T extends AggregationBuilderHandler> extends Iterable<T>, Builder<AbstractAggregationBuilder> {
}
