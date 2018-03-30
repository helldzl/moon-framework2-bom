package org.moonframework.model.mybatis.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.moonframework.core.util.BeanUtils;
import org.moonframework.model.mybatis.annotation.Join;
import org.moonframework.model.mybatis.annotation.JoinArray;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.QueryCondition;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Include;
import org.moonframework.model.mybatis.domain.Pair;
import org.moonframework.model.mybatis.support.AbstractGenericEntity;
import org.moonframework.model.mybatis.support.Association;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2015/11/17
 */
public abstract class AbstractBaseDao<T extends BaseEntity> extends AbstractGenericEntity<T> implements BaseDao<T> {

    protected final Log logger = LogFactory.getLog(this.getClass());

    protected SqlSessionTemplate session;

    private static final String DOT = ".";

    private final String save;
    private final String insertOnDuplicate;
    private final String saveOrUpdate;
    private final String delete;
    private final String updateByMap;
    private final String update;
    private final String updateByCriterion;
    private final String findOne;
    private final String findOneByObject;
    private final String findOneByCriterion;
    private final String findAll;
    private final String findAllByObject;
    private final String findAllByCriterion;
    private final String findPage;
    private final String findPageByCriterion;
    private final String count;
    private final String countByCondition;
    private final String countByCriterion;
    private final String exists;

    private static Iterable<? extends Field> empty = Collections.emptyList();

    @SuppressWarnings("unchecked")
    public AbstractBaseDao() {
        // operation
        save = name("save");
        insertOnDuplicate = name("insertOnDuplicate");
        saveOrUpdate = name("saveOrUpdate");
        delete = name("delete");
        updateByMap = name("updateByMap");
        update = name("update");
        updateByCriterion = name("updateByCriterion");
        findOne = name("findOne");
        findOneByObject = name("findOneByObject");
        findOneByCriterion = name("findOneByCriterion");
        findAll = name("findAll");
        findAllByObject = name("findAllByObject");
        findAllByCriterion = name("findAllByCriterion");
        findPage = name("findPage");
        findPageByCriterion = name("findPageByCriterion");
        count = name("count");
        countByCondition = name("countByCondition");
        countByCriterion = name("countByCriterion");
        exists = name("exists");
    }

    @Override
    public <S extends T> int saveOrUpdate(S insert, S update) {
        Map<String, Object> map = new HashMap<>();
        map.put("insert", insert);
        map.put("entity", update);
        return session.insert(insertOnDuplicate, map);
    }

    @Override
    public <S extends T> int saveOrUpdate(S entity) {
        return session.insert(saveOrUpdate, entity);
    }

    @Override
    public <S extends T> int[] saveOrUpdate(Iterable<S> entities) {
        List<Integer> list = new ArrayList<>();
        for (T entity : entities)
            list.add(session.insert(saveOrUpdate, entity));
        return toArray(list);
    }

    @Override
    public <S extends T> int save(S entity) {
        return session.insert(save, entity);
    }

    @Override
    public <S extends T> int[] save(Iterable<S> entities) {
        List<Integer> list = new ArrayList<>();
        for (T entity : entities)
            list.add(session.insert(save, entity));
        return toArray(list);
    }

    @Override
    public int update(Long id, List<Pair> increments, List<Pair> fields) {
        return update(id, increments, fields, null);
    }

    @Override
    public int update(Long id, List<Pair> increments, List<Pair> fields, Criterion criterion) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("increments", increments);
        map.put("fields", fields);
        map.put("conditions", condition(criterion));
        return session.update(updateByMap, map);
    }

    @Override
    public <S extends T> int update(S entity) {
        return session.update(update, entity);
    }

    @Override
    public <S extends T> int update(Iterable<S> entities) {
        // @Flush
        int n = 0;
        for (T entity : entities)
            n += session.update(update, entity);
        // List<BatchResult> results = session.flushStatements();
        return n;
    }

    @Override
    public <S extends T> int update(S entity, Criterion criterion) {
        Map<String, Object> map = new HashMap<>();
        map.put("entity", entity);
        map.put("conditions", condition(criterion));
        return session.update(updateByCriterion, map);
    }

    @Override
    public int delete(Long id) {
        return session.delete(delete, id);
    }

    @Override
    public int delete(T entity) {
        return delete(entity.getId());
    }

    @Override
    public int[] delete(Iterable<Long> ids) {
        List<Integer> list = new ArrayList<>();
        for (Long id : ids)
            list.add(session.delete(delete, id));
        // List<BatchResult> results = session.flushStatements();
        return toArray(list);
    }

    @Override
    public int remove(Long id) {
        return update(newInstance(id, new Date()));
    }

    @Override
    public int remove(Long id, Criterion criterion) {
        if (criterion == null)
            return remove(id);
        return update(newInstance(id, new Date()), criterion);
    }

    @Override
    public int remove(Iterable<Long> ids) {
        List<T> list = new ArrayList<>();
        Date date = new Date();
        for (Long id : ids)
            list.add(newInstance(id, date));
        return update(list);
    }

    @Override
    public int remove(Iterable<Long> ids, Criterion criterion) {
        int n = 0;
        for (Long id : ids)
            n += remove(id, criterion);
        return n;
    }

    @Override
    public T findOne(Criterion criterion) {
        return findOne(criterion, empty);
    }

    @Override
    public T findOne(Criterion criterion, Iterable<? extends Field> fields) {
        Map<String, Object> map = new HashMap<>();
        map.put("conditions", condition(criterion));
        map.put("fields", fields);
        return session.selectOne(findOneByCriterion, map);
    }

    @Override
    public T findOne(Long id) {
        return findOne(id, empty);
    }

    @Override
    public T findOne(Long id, Iterable<? extends Field> fields) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("fields", fields);
        return session.selectOne(findOne, map);
    }

    @Override
    public T findOne(T entity) {
        return findOne(entity, empty);
    }

    @Override
    public T findOne(T entity, Iterable<? extends Field> fields) {
        Map<String, Object> map = new HashMap<>();
        map.put("entity", entity);
        map.put("fields", fields);
        return session.selectOne(findOneByObject, map);
    }

    @Override
    public List<T> findAll(T entity) {
        return findAll(entity, empty);
    }

    @Override
    public List<T> findAll(T entity, Iterable<? extends Field> fields) {
        Map<String, Object> map = new HashMap<>();
        map.put("entity", entity);
        map.put("fields", fields);
        return session.selectList(findAllByObject, map);
    }

    @Override
    public List<T> findAll(Iterable<Long> ids) {
        return findAll(null, ids, empty);
    }

    @Override
    public List<T> findAll(Iterable<Long> ids, Iterable<? extends Field> fields) {
        return findAll(null, ids, fields);
    }

    @Override
    public List<T> findAll(String field, Iterable<Long> ids) {
        return findAll(field, ids, empty);
    }

    @Override
    public List<T> findAll(String field, Iterable<Long> ids, Iterable<? extends Field> fields) {
        Map<String, Object> map = new HashMap<>();
        map.put("field", field == null ? BaseEntity.ID : field);
        map.put("ids", ids);
        map.put("fields", fields);
        return session.selectList(findAll, map);
    }

    @Override
    public List<T> findAll(Criterion criterion) {
        return findAll(criterion, empty);
    }

    @Override
    public List<T> findAll(Criterion criterion, Iterable<? extends Field> fields) {
        Map<String, Object> map = new HashMap<>();
        map.put("conditions", condition(criterion));
        map.put("fields", fields);
        return session.selectList(findAllByCriterion, map);
    }

    @Override
    public Page<T> findAll(T entity, Pageable pageable) {
        return findAll(entity, pageable, empty);
    }

    @Override
    public Page<T> findAll(T entity, Pageable pageable, Iterable<? extends Field> fields) {
        Map<String, Object> map = pageMap(pageable);
        map.put("entity", entity);
        map.put("fields", fields);
        List<T> content = session.selectList(findPage, map);
        return new PageImpl<>(content, pageable, count(entity));
    }

    @Override
    public Page<T> findAll(Criterion criterion, Pageable pageable) {
        return findAll(criterion, pageable, empty);
    }

    @Override
    public Page<T> findAll(Criterion criterion, Pageable pageable, boolean count) {
        return findAll(criterion, pageable, empty, count);
    }

    @Override
    public Page<T> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        return findAll(criterion, pageable, fields, true);
    }

    @Override
    public Page<T> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields, boolean count) {
        return findAll(criterion, pageable, fields, count, Association.NORMAL);
    }

    @Override
    public Page<T> findAll(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields, boolean count, Association association) {
        Map<String, Object> map = pageMap(pageable);
        map.put("conditions", condition(criterion));
        map.put("fields", fields);
        map.put("relationships", join());
        map.put("association", association);
        List<T> content = session.selectList(findPageByCriterion, map);
        if (count) {
            return new PageImpl<>(content, pageable, session.selectOne(countByCriterion, map));
        } else {
            return new PageImpl<>(content);
        }
    }

    @Override
    public Page<Map<String, Object>> queryForList(Criterion criterion, Pageable pageable) {
        return queryForList(criterion, pageable, empty);
    }

    @Override
    public Page<Map<String, Object>> queryForList(Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        Map<String, Object> map = pageMap(pageable);
        map.put("conditions", condition(criterion));
        map.put("fields", fields);
        List<T> content = session.selectList(findPageByCriterion, map);
        return new PageImpl<>(content.stream().map(BeanUtils::toMap).collect(Collectors.toList()), pageable, session.selectOne(countByCriterion, map));
    }

    @Override
    public <E> Page<Map<String, Object>> queryForList(Iterable<E> keys) {
        return null;
    }

    @Override
    public <E> Page<Map<String, Object>> queryForList(Iterable<E> keys, int type) {
        return null;
    }

    @Override
    public long count() {
        return session.selectOne(count);
    }

    @Override
    public long count(T entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("entity", entity);
        return session.selectOne(countByCondition, map);
    }

    @Override
    public long count(Criterion criterion) {
        Map<String, Object> map = new HashMap<>();
        map.put("conditions", condition(criterion));
        map.put("relationships", join());
        return session.selectOne(countByCriterion, map);
    }

    @Override
    public boolean exists(Long id) {
        long count = session.selectOne(exists, id);
        return count > 0;
    }

    @Override
    public boolean exists(T entity) {
        return count(entity) > 0;
    }

    @Override
    public boolean exists(Criterion criterion) {
        return count(criterion) > 0;
    }

    @Override
    public T queryForObject(Long id) {
        // this is a adapter method, default to findOne()
        return findOne(id);
    }

    @Override
    public T queryForObject(Long id, Iterable<? extends Field> fields) {
        // this is a adapter method, default to findOne()
        return findOne(id, fields);
    }

    /**
     * <p>构造分页映射对象, 子类可以使用该方法来直接创建分页map对象</p>
     *
     * @param pageable pageable
     * @return Map
     */
    protected Map<String, Object> pageMap(Pageable pageable) {
        Map<String, Object> map = new HashMap<>();
        map.put("offset", pageable.getOffset());
        map.put("pageSize", pageable.getPageSize());
        map.put("orders", pageable.getSort() == null ? Collections.emptyList() : pageable.getSort());
        return map;
    }

    // private method

    private static QueryCondition condition(Criterion criterion) {
        if (criterion == null)
            return null;

        QueryCondition condition = new QueryCondition();
        criterion.toSqlString(condition);
        return condition;
    }

    private static int[] toArray(List<Integer> list) {
        int[] result = new int[list.size()];
        int length = result.length;
        for (int i = 0; i < length; i++)
            result[i] = list.get(i);
        return result;
    }

    private String name(String operation) {
        return entityClass.getName() + DOT + operation;
    }

    private List<Join> join() {
        Include include = Restrictions.get(Include.class);
        if (CollectionUtils.isEmpty(include)) {
            return null;
        }
        List<Join> list = new ArrayList<>();
        for (java.lang.reflect.Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Join.class)) {
                Join annotation = field.getAnnotation(Join.class);
                join(annotation, include, list);
            } else if (field.isAnnotationPresent(JoinArray.class)) {
                JoinArray annotations = field.getAnnotation(JoinArray.class);
                for (Join annotation : annotations.value()) {
                    join(annotation, include, list);
                }
            }
        }
        return list;
    }

    private void join(Join annotation, Include include, List<Join> list) {
        if (include.contains(annotation.table())) {
            list.add(annotation);
        }
    }

    // get and set method

    public SqlSessionTemplate getSession() {
        return session;
    }

    @Autowired
    public void setSession(SqlSessionTemplate session) {
        this.session = session;
    }

}
