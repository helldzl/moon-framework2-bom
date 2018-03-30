package org.moonframework.model.mongodb.service;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.moonframework.core.toolkit.generator.IdGenerator;
import org.moonframework.model.mongodb.domain.BaseEntity;
import org.moonframework.model.mongodb.enums.DeleteState;
import org.moonframework.model.mongodb.enums.EntityOrder;
import org.moonframework.model.mongodb.repository.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public abstract class AbstractBaseService<T extends BaseEntity, E extends BaseDao<T>> implements BaseService<T> {

    protected final Log logger = LogFactory.getLog(this.getClass());

    @Autowired(required = false)
    private IdGenerator idGenerator;

    @Autowired
    protected E baseDao;

    @Override
    public <S extends T> S save(S entity) {
        Date date = new Date();
        entity.setLastModifyTime(date);
        baseDao.save(entity);
        return entity;
    }

    @Override
    public <S extends T> List<S> save(Iterable<S> entities) {
        baseDao.save(entities);
        //TODO transfer
        return null;
    }

    @Override
    public <S extends T> S insert(S entity) {
        Date date = new Date();
        entity.setState(DeleteState.CREATE.getCode());
        entity.setIsDel(DeleteState.CREATE.getCode());
        entity.setOrder(EntityOrder.INITIAL.getCode());
        entity.setCreateTime(date);
        entity.setLastModifyTime(date);
        entity.setId(idGenerator.generateMongoId(entity.getId()));
        baseDao.insert(entity);
        return entity;
    }

    @Override
    public <S extends T> List<S> insert(Iterable<S> entities) {
        baseDao.insert(entities);
        //TODO transfer
        return null;
    }

    @Override
    public void delete(String id) {
        T entity = baseDao.findOne(id);
        entity.setIsDel(DeleteState.DELETE.getCode());
        baseDao.save(entity);
    }

    @Override
    public void delete(T entity) {
        entity.setIsDel(DeleteState.DELETE.getCode());
        baseDao.save(entity);
    }

    @Override
    public void delete(Iterable<? extends T> entities) {
        baseDao.delete(entities);
    }

    @Override
    public void deleteAll() {
        baseDao.deleteAll();
    }
    @Override
    public T findOne(String id) {
        return baseDao.findOne(id);
    }

    @Override
    public <S extends T> S findOne(Example<S> example) {
        return baseDao.findOne(example);
    }

    @Override
    public List<T> findAll() {
        return baseDao.findAll();
    }

    @Override
    public Iterable<T> findAll(Iterable<String> ids) {
        return baseDao.findAll(ids);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return baseDao.findAll(pageable);
    }

    @Override
    public List<T> findAll(Sort sort) {
        return baseDao.findAll(sort);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        return baseDao.findAll(example);
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return baseDao.findAll(example,pageable);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        return baseDao.findAll(example, sort);
    }

    @Override
    public long count() {
        return baseDao.count();
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        return baseDao.count(example);
    }

    @Override
    public boolean exists(String id) {
        return baseDao.exists(id);
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        return baseDao.exists(example);
    }

    // check method

    protected boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    protected boolean matches(String regex, String input) {
        return Pattern.matches(regex, input);
    }

}
