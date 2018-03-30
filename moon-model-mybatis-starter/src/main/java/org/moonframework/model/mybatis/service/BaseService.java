package org.moonframework.model.mybatis.service;

import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.repository.BaseRepository;

import java.util.function.Supplier;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/11/26
 */
public interface BaseService<T extends BaseEntity> extends BaseRepository<T, Long> {

    /**
     * 包装事务
     *
     * @param supplier
     * @param <E>
     * @return
     */
    <E> E transactional(Supplier<E> supplier);

}
