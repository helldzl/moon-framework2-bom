package org.moonframework.elasticsearch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.highlight.HighlightField;
import org.moonframework.elasticsearch.aggregation.AggregationType;
import org.moonframework.elasticsearch.suggestion.SuggestionType;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/7/7
 */
public abstract class AbstractSearchEngine implements SearchEngine {

    protected static final Log logger = LogFactory.getLog(AbstractSearchEngine.class);

    // Meta-fields are used to customize how a document’s metadata associated is treated. Examples of meta-fields include the document’s _index, _type, _id, and _source fields.

    public static final String META_FIELD_INDEX = "_index";
    public static final String META_FIELD_TYPE = "_type";
    public static final String META_FIELD_ID = "_id";
    public static final String META_FIELD_SCORE = "_score";
    public static final String META_FIELD_SOURCE = "_source";

    // fields

    public static final String FIELD_HIGHLIGHT = "highlight";
    public static final String FIELD_BUCKETS = "buckets";

    protected Client client;

    public static void order(SearchResult<Map<String, Object>> result, Collection<String> ids) {
        List<Map<String, Object>> hits = AbstractSearchEngine.hits(result);
        if (!hits.isEmpty()) {
            Map<String, Integer> map = new HashMap<>();
            int index = 0;
            for (String id : ids) {
                map.put(id, index++);
            }
            hits.sort((o1, o2) -> {
                String s1 = (String) o1.get(META_FIELD_ID);
                String s2 = (String) o2.get(META_FIELD_ID);
                Integer x = map.get(s1);
                Integer y = map.get(s2);
                return Integer.compare(x, y);
            });
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> hits(SearchResult<Map<String, Object>> result) {
        Map<String, Object> root = result.getHits();
        if (root == null)
            return Collections.emptyList();

        List<Map<String, Object>> hits = (List<Map<String, Object>>) root.get("hits");
        if (hits == null)
            return Collections.emptyList();
        return hits;
    }

    /**
     * <p>Get meta data source of search result</p>
     *
     * @param result search result
     * @return source
     */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> source(SearchResult<Map<String, Object>> result) {
        return hits(result).stream().map(hit -> (Map<String, Object>) hit.get(META_FIELD_SOURCE)).collect(Collectors.toList());
    }

    /**
     * @param result search result
     * @param rel    rational map
     * @param route  route field
     * @param name   new field name
     */
    public static void join(SearchResult<Map<String, Object>> result, Map<String, Object> rel, String route, String name) {
        List<Map<String, Object>> source = source(result);
        list(source, null, token -> {
            if (token.getRoute().equals(route)) {
                token.getMap().put(name, rel.get(token.getValue().toString()));
            }
        });
    }

    public static <T> void list(List<T> array, String path, Consumer<Token> consumer) {
        array.forEach(t -> parse(t, path, consumer));
    }

    @SuppressWarnings("unchecked")
    public static <T> void map(Map<String, T> map, String path, Consumer<Token> consumer) {
        for (Map.Entry<String, T> entry : map.entrySet().stream().collect(Collectors.toList())) {
            String key = entry.getKey();
            T value = entry.getValue();
            String route = path == null ? key : path + "." + key;
            if (parse(value, route, consumer)) {
                consumer.accept(new Token((Map<String, Object>) map, key, value, route));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean parse(T value, String path, Consumer<Token> consumer) {
        if (value instanceof List) {
            list((List<T>) value, path, consumer);
            return false;
        } else if (value instanceof Map) {
            map((Map<String, T>) value, path, consumer);
            return false;
        } else {
            return true;
        }
    }

    public static class Token {

        private Map<String, Object> map;
        private String key;
        private Object value;
        private String route;

        public Token(Map<String, Object> map, String key, Object value, String route) {
            this.map = map;
            this.key = key;
            this.value = value;
            this.route = route;
        }

        public Map<String, Object> getMap() {
            return map;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public String getRoute() {
            return route;
        }
    }

    public AbstractSearchEngine(Client client) {
        this.client = client;
    }

    @Override
    public AnalyzeResponse analyze(AnalyzeRequest analyzeRequest) {
        return client.admin().indices().analyze(analyzeRequest).actionGet();
    }

    @Override
    public DeleteResponse delete(String index, String type, String id) {
        return client.prepareDelete(index, type, id).get();
    }

    @Override
    public <T extends Searchable> BulkResponse index(String index, String type, T t, BiConsumer<? super XContentBuilder, ? super T> consumer) throws IOException {
        return index(index, type, Collections.singletonList(t), consumer);
    }

    @Override
    public <T extends Searchable> BulkResponse index(String index, String type, Iterable<T> iterable, BiConsumer<? super XContentBuilder, ? super T> consumer) throws IOException {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (T t : iterable) {
            String docId = t.getDocId();
            if (t.getEnabled() == null || t.getEnabled() == 1) {
                bulkRequest.add(client.prepareIndex(index, type, docId).setSource(source(t, consumer)));
                if (logger.isInfoEnabled()) {
                    logger.info(String.format("[INDEX] index : %s, type : %s, id : %s", index, type, docId));
                }
            } else {
                bulkRequest.add(client.prepareDelete(index, type, docId));
                if (logger.isInfoEnabled()) {
                    logger.info(String.format("[DELETE] index : %s, type : %s, id : %s", index, type, docId));
                }
            }
        }
        return bulkRequest.get();
    }

    @Override
    public SearchResult<Map<String, Object>> search(String index, String type, Consumer<SearchRequestBuilder> consumer) {
        return search(index, new String[]{type}, consumer, null);
    }

    @Override
    public SearchResult<Map<String, Object>> search(String index, String type, Consumer<SearchRequestBuilder> consumer, Function<Aggregations, Map<String, Object>> function) {
        return search(index, new String[]{type}, consumer, function);
    }

    @Override
    public SearchResult<Map<String, Object>> search(String index, String[] types, Consumer<SearchRequestBuilder> consumer) {
        return search(index, types, consumer, null);
    }

    @Override
    public SearchResult<Map<String, Object>> search(String index, String[] types, Consumer<SearchRequestBuilder> consumer, Function<Aggregations, Map<String, Object>> function) {
        SearchResponse response = prepareSearch(index, types, consumer).execute().actionGet();
        SearchResult<Map<String, Object>> searchResult = new SearchResult<>(response.getHits().getTotalHits());
        result(searchResult, response, function);
        return searchResult;
    }

    // get and set method

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    // protected method

    protected SearchRequestBuilder prepareSearch(String index, String[] types, Consumer<SearchRequestBuilder> consumer) {
        SearchRequestBuilder builder = client.prepareSearch(index);
        builder.setTypes(types);
        builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        consumer.accept(builder);
        return builder;
    }

    protected <T extends Searchable> XContentBuilder source(T t, BiConsumer<? super XContentBuilder, ? super T> consumer) throws IOException {
        XContentBuilder builder = jsonBuilder();
        builder.startObject();
        consumer.accept(builder, t);
        builder.endObject();
        return builder;
    }

    protected void result(SearchResult<Map<String, Object>> searchResult, SearchResponse response, Function<Aggregations, Map<String, Object>> responseMapFunction) {
        // Hits
        SearchHits hits = response.getHits();
        List<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit hit : hits) {
            Map<String, Object> map = new HashMap<>();
            // meta field
            map.put(META_FIELD_ID, hit.getId());
            map.put(META_FIELD_SCORE, hit.getScore());
            map.put(META_FIELD_SOURCE, hit.getSource());

            // other field
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (!CollectionUtils.isEmpty(highlightFields)) {
                Map<String, Object> highlight = new HashMap<>();
                highlightFields.entrySet().forEach(entry -> highlight.put(entry.getValue().getName(), Arrays.stream(entry.getValue().getFragments()).map(Text::string).collect(Collectors.toList())));
                map.put(FIELD_HIGHLIGHT, highlight);
            }
            list.add(map);
        }

        // Aggregations
        Map<String, Object> aggregationMap = null;
        Aggregations aggregations = response.getAggregations();
        if (aggregations != null) {
            if (responseMapFunction != null) {
                aggregationMap = responseMapFunction.apply(aggregations);
            } else {
                aggregationMap = new HashMap<>();
                AggregationType.aggregations(aggregations, aggregationMap);
            }
        }

        // Suggest
        searchResult.setSuggest(SuggestionType.suggest(response.getSuggest()));

        // Set result
        searchResult.setHits(list);
        if (!CollectionUtils.isEmpty(aggregationMap))
            searchResult.setAggregations(aggregationMap);
    }

}
