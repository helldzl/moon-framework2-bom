package org.moonframework.elasticsearch;

import java.util.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/7/7
 */
public class SearchResult<T> implements Iterable<T> {

    private static final String HITS = "hits";
    private static final String TOTAL = "total";

    private Map<String, Object> hits = new HashMap<>();
    private Map<String, Object> aggregations;
    private Map<String, Object> suggest;

    public SearchResult(long total) {
        this.hits.put(TOTAL, total);
    }

    public SearchResult(long total, List<T> hits) {
        this.hits.put(TOTAL, total);
        this.hits.put(HITS, hits);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<T> iterator() {
        if (!hits.containsKey(HITS))
            return Collections.emptyIterator();
        return ((List<T>) hits.get(HITS)).iterator();
    }

    @SuppressWarnings("unchecked")
    public boolean isEmpty() {
        Object o = hits.get(HITS);
        return o == null || ((List<T>) o).isEmpty();
    }

    public void setTotal(long total) {
        this.hits.put(TOTAL, total);
    }

    public Map<String, Object> getHits() {
        return hits;
    }

    public void setHits(List<T> hits) {
        this.hits.put(HITS, hits);
    }

    public Map<String, Object> getAggregations() {
        return aggregations;
    }

    public void setAggregations(Map<String, Object> aggregations) {
        this.aggregations = aggregations;
    }

    public Map<String, Object> getSuggest() {
        return suggest;
    }

    public void setSuggest(Map<String, Object> suggest) {
        this.suggest = suggest;
    }

    protected static class EmptySearchResult<T> extends SearchResult<T> {

        private Map<String, Object> hits;

        protected EmptySearchResult() {
            super(0, Collections.emptyList());
            this.hits = Collections.unmodifiableMap(super.getHits());
        }

        @Override
        public Map<String, Object> getHits() {
            return hits;
        }

        @Override
        public Iterator<T> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public void setTotal(long total) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setHits(List<T> hits) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setAggregations(Map<String, Object> aggregations) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setSuggest(Map<String, Object> suggest) {
            throw new UnsupportedOperationException();
        }

    }

}
