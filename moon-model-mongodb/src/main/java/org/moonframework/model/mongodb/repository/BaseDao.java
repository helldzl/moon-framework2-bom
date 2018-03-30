package org.moonframework.model.mongodb.repository;

import org.moonframework.model.mongodb.domain.BaseEntity;

public interface BaseDao<T extends BaseEntity> extends BaseRepository<T> {
    }
