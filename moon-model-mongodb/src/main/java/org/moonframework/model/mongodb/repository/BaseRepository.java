package org.moonframework.model.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BaseRepository<T> extends MongoRepository<T, String> {
}
