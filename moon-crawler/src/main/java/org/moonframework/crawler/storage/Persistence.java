package org.moonframework.crawler.storage;

import java.util.List;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/6/16
 */
public interface Persistence {

    /**
     * <p>持久化</p>
     *
     * @param name      name
     * @param className class name
     * @param items     items为待执行持久化的数据记录集合, 根据class name进行映射
     */
    Map<Long,Object> persist(String name, String className, List<Item> items);

}
