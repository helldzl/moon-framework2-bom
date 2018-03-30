package org.moonframework.elasticsearch;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.search.aggregations.Aggregations;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/8/2
 */
public interface Clustering {

    SearchWithClustersResult searchWithClusters(String index, String type, String queryHint, String titleFieldName, String contentFieldName, Consumer<SearchRequestBuilder> consumer);

    SearchWithClustersResult searchWithClusters(String index, String type, String queryHint, String titleFieldName, String contentFieldName, Consumer<SearchRequestBuilder> consumer, Function<Aggregations, Map<String, Object>> function);

    SearchWithClustersResult searchWithClusters(String index, String[] types, String queryHint, String titleFieldName, String contentFieldName, Consumer<SearchRequestBuilder> consumer);

    SearchWithClustersResult searchWithClusters(String index, String[] types, String queryHint, String titleFieldName, String contentFieldName, Consumer<SearchRequestBuilder> consumer, Function<Aggregations, Map<String, Object>> function);

}
