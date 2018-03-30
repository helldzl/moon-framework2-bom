package org.moonframework.elasticsearch;

/**
 * Created by quzile on 2016/8/11.
 */
public class SearchUtils {

    public static final SearchResult.EmptySearchResult EMPTY_SEARCH_RESULT = new SearchResult.EmptySearchResult();

    @SuppressWarnings("unchecked")
    public static <T> SearchResult<T> emptySearchResult() {
        return (SearchResult<T>) EMPTY_SEARCH_RESULT;
    }

}
