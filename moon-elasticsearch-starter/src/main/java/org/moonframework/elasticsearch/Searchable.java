package org.moonframework.elasticsearch;

import org.elasticsearch.common.Nullable;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/7/7
 */
public interface Searchable {

    @Nullable
    String getDocId();

    Integer getEnabled();

}
