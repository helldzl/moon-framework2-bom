package org.moonframework.elasticsearch;

import org.carrot2.elasticsearch.ClusteringAction;
import org.carrot2.elasticsearch.LogicalField;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.aggregations.Aggregations;
import org.moonframework.core.util.BeanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/8/2
 */
public abstract class ClusteringSearchEngine extends AbstractSearchEngine implements Clustering {

    public ClusteringSearchEngine(Client client) {
        super(client);
    }

    @Override
    public SearchWithClustersResult searchWithClusters(String index, String type, String queryHint, String titleFieldName, String contentFieldName, Consumer<SearchRequestBuilder> consumer) {
        return searchWithClusters(index, new String[]{type}, queryHint, titleFieldName, contentFieldName, consumer, null);
    }

    @Override
    public SearchWithClustersResult searchWithClusters(String index, String type, String queryHint, String titleFieldName, String contentFieldName, Consumer<SearchRequestBuilder> consumer, Function<Aggregations, Map<String, Object>> function) {
        return searchWithClusters(index, new String[]{type}, queryHint, titleFieldName, contentFieldName, consumer, function);
    }

    @Override
    public SearchWithClustersResult searchWithClusters(String index, String[] types, String queryHint, String titleFieldName, String contentFieldName, Consumer<SearchRequestBuilder> consumer) {
        return searchWithClusters(index, types, queryHint, titleFieldName, contentFieldName, consumer, null);
    }

    @Override
    public SearchWithClustersResult searchWithClusters(String index, String[] types, String queryHint, String titleFieldName, String contentFieldName, Consumer<SearchRequestBuilder> consumer, Function<Aggregations, Map<String, Object>> function) {
        ClusteringAction.ClusteringActionResponse result = new ClusteringAction.ClusteringActionRequestBuilder(client)
                .setQueryHint(queryHint)
                .addFieldMapping(titleFieldName, LogicalField.TITLE)
                .addHighlightedFieldMapping(contentFieldName, LogicalField.CONTENT)
                .setSearchRequest(prepareSearch(index, types, consumer))
                .execute()
                .actionGet();

        // build clusters result
        SearchResponse response = result.getSearchResponse();
        SearchWithClustersResult searchWithClustersResult = new SearchWithClustersResult(response.getHits().getTotalHits());

        // process hits and aggregations
        result(searchWithClustersResult, response, function);

        // process clustering
        List<Map<String, Object>> clusters = BeanUtils.toList(Arrays.asList(result.getDocumentGroups()));
        searchWithClustersResult.setClusters(clusters);
        return searchWithClustersResult;
    }

}
