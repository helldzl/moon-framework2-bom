package org.moonframework.model.mongodb.service;

import org.moonframework.model.mongodb.domain.BaseEntity;
import org.moonframework.model.mongodb.repository.BaseRepository;

public interface BaseService<T extends BaseEntity> extends BaseRepository<T> {
}
