package org.moonframework.elasticsearch;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.aggregations.Aggregations;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/7/7
 */
public interface SearchEngine {

    AnalyzeResponse analyze(AnalyzeRequest analyzeRequest);

    DeleteResponse delete(String index, String type, String id);

    <T extends Searchable> BulkResponse index(String index, String type, T t, BiConsumer<? super XContentBuilder, ? super T> consumer) throws IOException;

    <T extends Searchable> BulkResponse index(String index, String type, Iterable<T> iterable, BiConsumer<? super XContentBuilder, ? super T> consumer) throws IOException;

    SearchResult<Map<String, Object>> search(String index, String type, Consumer<SearchRequestBuilder> consumer);

    SearchResult<Map<String, Object>> search(String index, String type, Consumer<SearchRequestBuilder> consumer, Function<Aggregations, Map<String, Object>> function);

    SearchResult<Map<String, Object>> search(String index, String[] types, Consumer<SearchRequestBuilder> consumer);

    SearchResult<Map<String, Object>> search(String index, String[] types, Consumer<SearchRequestBuilder> consumer, Function<Aggregations, Map<String, Object>> function);

}