package org.moonframework.model.mybatis.service;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.moonframework.core.toolkit.generator.IdGenerator;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Pair;
import org.moonframework.model.mybatis.repository.BaseDao;
import org.moonframework.model.mybatis.support.AbstractGenericEntity;
import org.moonframework.model.mybatis.support.Association;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/11/26
 */
public abstract class AbstractBaseService<T extends BaseEntity, E extends BaseDao<T>> extends AbstractGenericEntity<T> implements BaseService<T> {

    protected final Log logger = LogFactory.getLog(this.getClass());

    private IdGenerator idGenerator = IdGenerator.DEFAULT_GENERATOR;

    @Autowired
    protected E baseDao;

    @Override
    public <S extends T> int saveOrUpdate(S insert, S update) {
        Date date = new Date();
        insert.setCreated(date);
        insert.setModified(date);
        insert.setId(idGenerator.generateId(insert.getId()));
        update.setModified(date);
        return baseDao.saveOrUpdate(insert, update);
    }

    @Override
    public <S extends T> int saveOrUpdate(S entity) {
        Date date = new Date();
        entity.setCreated(date);
        entity.setModified(date);
        entity.setId(idGenerator.generateId(entity.getId()));
        return baseDao.saveOrUpdate(entity);
    }

    @Override
    public <S extends T> int[] saveOrUpdate(Iterable<S> entities) {
        Date date = new Date();
        for (S entity : entities) {
            entity.setCreated(date);
            entity.setModified(date);
            entity.setId(idGenerator.generateId(entity.getId()));
        }
        return baseDao.saveOrUpdate(entities);
    }

    @Override
    public <S extends T> int save(S entity) {
        Date date = new Date();
        entity.setCreated(date);
        entity.setModified(date);
        entity.setId(idGenerator.generateId(entity.getId()));
        return baseDao.save(entity);
    }

    @Override
    public <S extends T> int[] save(Iterable<S> entities) {
        Date date = new Date();
        for (S entity : entities) {
            entity.setCreated(date);
            entity.setModified(date);
            entity.setId(idGenerator.generateId(entity.getId()));
        }
        return baseDao.save(entities);
    }

    @Override
    public int update(Long id, List<Pair> increments, List<Pair> fields) {
        return update(id, increments, fields, null);
    }

    @Override
    public int update(Long id, List<Pair> increments, List<Pair> fields, Criterion criterion) {
        if (increments == null)
            increments = Collections.emptyList();
        if (fields == null)
            fields = Collections.emptyList();
        return baseDao.update(id, increments, fields, criterion);
    }

    @Override
    public <S extends T> int update(S entity) {
        Date date = new Date();
        entity.setModified(date);
        return baseDao.update(entity);
    }

    @Override
    public <S extends T> int update(Iterable<S> entities) {
        Date date = new Date();
        for (S entity : entities) {
            entity.setModified(date);
        }
        return baseDao.update(entities);
    }

    @Override
    public <S extends T> int update(S entity, Criterion criterion) {
        Date date = new Date();
        entity.setModified(date);
        return baseDao.update(entity, criterion);
    }

    @Override
    public int delete(Long id) {
        notNull(id);
        return baseDao.delete(id);
    }

    @Override
    public int delete(T entity) {
        notNull(entity);
        return baseDao.delete(entity);
    }

    @Override
    public int[] delete(Iterable<Long> ids) {
        notNull(ids);
        return baseDao.delete(ids);
    }

    @Override
    public int remove(Long id) {
        notNull(id);
        return baseDao.remove(id);
    }

    @Override
    public int remove(Long id, Criterion criterion) {
        notNull(id);
        return baseDao.remove(id, criterion);
    }

    @Override
    public int remove(Iterable<Long> ids) {
        notNull(ids);
        return baseDao.remove(ids);
    }

    @Override
    public int remove(Iterable<Long> ids, Criterion criterion) {
        notNull(ids);
        return baseDao.remove(ids, criterion);
    }

    @Override
    public T findOne(Criterion criterion) {
        return baseDao.findOne(criterion);
    }

    @Override
    public T findOne(Criterion criterion, Iterable<? extends Field> fields) {
        return baseDao.findOne(criterion, fields);
    }

    @Override
    public T findOne(Long id) {
        notNull(id);
        return baseDao.findOne(id);
    }

    @Override
    public T findOne(Long id, Iterable<? extends Field> fields) {
        notNull(id);
        return baseDao.findOne(id, fields);
    }

    @Override
    public T findOne(T entity) {
        notNull(entity);
        return baseDao.findOne(entity);
    }

    @Override
    public T findOne(T entity, Iterable<? extends Field> fields) {
        notNull(entity);
        return baseDao.findOne(entity, fields);
    }

    @Override
    public List<T> findAll(T entity) {
        notNull(entity);
        return baseDao.findAll(entity);
    }

    @Override
    public List<T> findAll(T entity, Iterable<? extends Field> fields) {
        notNull(entity);
        return baseDao.findAll(entity, fields);
    }

    @Override
    public List<T> findAll(Iterable<Long> ids) {
        notNull(ids);
        return baseDao.findAll(ids);
    }

    @Override
    public List<T> findAll(Iterable<Long> ids, Iterable<? extends Field> fields) {
        notNull(ids);
        return baseDao.findAll(ids, fields);
    }

    @Override
    public List<T> findAll(String field, Iterable<Long> ids) {
        notNull(ids);
        return baseDao.findAll(field, ids);
    }

    @Override
    public List<T> findAll(String field, Iterable<Long> ids, Iterable<? extends Field> fields) {
        notNull(ids);
        return baseDao.findAll(field, ids, fields);
    }

    @Override
    public List<T> findAll(Criterion criterion) {
        notNull(criterion);
        return baseDao.findAll(criterion);
    }

    @Override
    public List<T> findAll(Criterion criterion, Iterable<? extends Field> fields) {
        notNull(criterion);
        return baseDao.findAll(criterion, fields);
    }

    @Override
    public Page<T> findAll(T entity, Pageable pageable) {
        notNull(entity);
        notNull(pageable);
        return baseDao.findAll(entity, pageable);
    }

    @Override
    public Page<T> findAll(T entity, Pageable pageable, Iterable<? extends Field> fields) {
        notNull(entity);
        notNull(pageable);
        return baseDao.findAll(entity, pageable, fields);
    }

    @Override
    public Page<T> findAll(Criterion criterion, Pageable pageable) {
        notNull(pageable);
        notNull(criterion);
        return baseDao.findAll(criterion, pageable);
    }

    @Override
    public Page<T> findAll(Criterion criterion, Pageable pageable, boolean count) {
        notNull(pageable);
        notNull(criterion);
        return baseDao.findAll(criterion, pageable, count);
    }

    @Override
    public Page<T> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        notNull(pageable);
        notNull(criterion);
        return baseDao.findAll(criterion, pageable, fields);
    }

    @Override
    public Page<T> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields, boolean count) {
        notNull(pageable);
        notNull(criterion);
        return baseDao.findAll(criterion, pageable, fields, count);
    }

    @Override
    public Page<T> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields, boolean count, Association association) {
        notNull(pageable);
        notNull(criterion);
        return baseDao.findAll(criterion, pageable, fields, count, association);
    }

    @Override
    public Page<Map<String, Object>> queryForList(Criterion criterion, Pageable pageable) {
        notNull(pageable);
        notNull(criterion);
        return baseDao.queryForList(criterion, pageable);
    }

    @Override
    public Page<Map<String, Object>> queryForList(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        notNull(pageable);
        notNull(criterion);
        return baseDao.queryForList(criterion, pageable, fields);
    }

    @Override
    public <U> Page<Map<String, Object>> queryForList(Iterable<U> keys) {
        return baseDao.queryForList(keys);
    }

    @Override
    public <U> Page<Map<String, Object>> queryForList(Iterable<U> keys, int type) {
        return baseDao.queryForList(keys, type);
    }

    @Override
    public long count() {
        return baseDao.count();
    }

    @Override
    public long count(T entity) {
        notNull(entity);
        return baseDao.count(entity);
    }

    @Override
    public long count(Criterion criterion) {
        notNull(criterion);
        return baseDao.count(criterion);
    }

    @Override
    public boolean exists(Long id) {
        notNull(id);
        return baseDao.exists(id);
    }

    @Override
    public boolean exists(T entity) {
        notNull(entity);
        return baseDao.exists(entity);
    }

    @Override
    public boolean exists(Criterion criterion) {
        notNull(criterion);
        return baseDao.exists(criterion);
    }

    @Override
    public T queryForObject(Long id) {
        notNull(id);
        return baseDao.queryForObject(id);
    }

    @Override
    public T queryForObject(Long id, Iterable<? extends Field> fields) {
        notNull(id);
        return baseDao.queryForObject(id, fields);
    }

    @Override
    public <U> U transactional(Supplier<U> supplier) {
        return supplier.get();
    }

    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    // assist method

    /**
     * <p>数组中有一条执行成功则返回成功</p>
     *
     * @param array array
     * @return is successful
     */
    protected static int result(int[] array) {
        return Arrays.stream(array).reduce(0, (left, right) -> left + right);
    }

    // check method

    protected boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    protected boolean matches(String regex, String input) {
        return Pattern.matches(regex, input);
    }

}
