package org.moonframework.core.toolkit.generator;

import org.moonframework.core.toolkit.idworker.IdWorkerUtil;

/**
 * Created by Freeman on 2016/1/8.
 */
public class IdWorkerGenerator implements IdGenerator {

    @Override
    public Long generateId() {
        return generateId(null);
    }

    @Override
    public Long generateId(Long id) {
        return id == null ? IdWorkerUtil.nextLong() : id;
    }

    @Override
    public String generateMongoId() {
        return generateMongoId(null);
    }

    @Override
    public String generateMongoId(String id) {
        return id == null ? IdWorkerUtil.nextMongoId() : id;
    }
}
