package org.moonframework.model.mybatis.service;

import org.moonframework.model.mybatis.annotation.OneToMany;
import org.moonframework.model.mybatis.annotation.OneToManyArray;
import org.moonframework.model.mybatis.annotation.OneToOne;
import org.moonframework.model.mybatis.annotation.OneToOneArray;
import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.QueryFieldOperator;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.*;
import org.moonframework.model.mybatis.repository.BaseRepository;
import org.moonframework.model.mybatis.support.Association;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/3/10
 */
public class Services implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private static Map<Class<?>, String> map = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Services.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <S extends BaseEntity> int saveOrUpdate(Class<S> clazz, S insert, S update) {
        return service(clazz, service -> service.saveOrUpdate(insert, update));
    }

    public static <S extends BaseEntity> int saveOrUpdate(Class<S> clazz, S entity) {
        return service(clazz, service -> service.saveOrUpdate(entity));
    }

    public static <S extends BaseEntity> int[] saveOrUpdate(Class<S> clazz, Iterable<S> entities) {
        return service(clazz, service -> service.saveOrUpdate(entities));
    }

    public static <S extends BaseEntity> int save(Class<S> clazz, S entity) {
        return service(clazz, service -> service.save(entity));
    }

    public static <S extends BaseEntity> int[] save(Class<S> clazz, Iterable<S> entities) {
        return service(clazz, service -> service.save(entities));
    }

    public static <S extends BaseEntity> int update(Class<S> clazz, Long id, List<Pair> increments, List<Pair> fields) {
        return service(clazz, service -> service.update(id, increments, fields));
    }

    public static <S extends BaseEntity> int update(Class<S> clazz, Long id, List<Pair> increments, List<Pair> fields, Criterion criterion) {
        return service(clazz, service -> service.update(id, increments, fields, criterion));
    }

    public static <S extends BaseEntity> int update(Class<S> clazz, S entity) {
        return service(clazz, service -> service.update(entity));
    }

    public static <S extends BaseEntity> int update(Class<S> clazz, S entity, Criterion criterion) {
        return service(clazz, service -> service.update(entity, criterion));
    }

    public static <S extends BaseEntity> int update(Class<S> clazz, Iterable<S> entities) {
        return service(clazz, service -> service.update(entities));
    }

    public static <S extends BaseEntity> int delete(Class<S> clazz, Long id) {
        return service(clazz, service -> service.delete(id));
    }

    public static <S extends BaseEntity> int delete(Class<S> clazz, S entity) {
        return service(clazz, service -> service.delete(entity));
    }

    public static <S extends BaseEntity> int[] delete(Class<S> clazz, Iterable<Long> longs) {
        return service(clazz, service -> service.delete(longs));
    }

    public static <S extends BaseEntity> int remove(Class<S> clazz, Long id) {
        return service(clazz, service -> service.remove(id));
    }

    public static <S extends BaseEntity> int remove(Class<S> clazz, Long id, Criterion criterion) {
        return service(clazz, service -> service.remove(id, criterion));
    }

    public static <S extends BaseEntity> int remove(Class<S> clazz, Iterable<Long> longs) {
        return service(clazz, service -> service.remove(longs));
    }

    public static <S extends BaseEntity> int remove(Class<S> clazz, Iterable<Long> longs, Criterion criterion) {
        return service(clazz, service -> service.remove(longs, criterion));
    }

    public static <S extends BaseEntity> S findOne(Class<S> clazz, Long id) {
        return service(clazz, service -> service.findOne(id));
    }

    public static <S extends BaseEntity> S findOne(Class<S> clazz, Long id, Iterable<? extends Field> fields) {
        return service(clazz, service -> service.findOne(id, fields));
    }

    public static <S extends BaseEntity> S findOne(Class<S> clazz, S entity) {
        return service(clazz, service -> service.findOne(entity));
    }

    public static <S extends BaseEntity> S findOne(Class<S> clazz, S entity, Iterable<? extends Field> fields) {
        return service(clazz, service -> service.findOne(entity, fields));
    }

    public static <S extends BaseEntity> S findOne(Class<S> clazz, Criterion criterion) {
        return service(clazz, service -> service.findOne(criterion));
    }

    public static <S extends BaseEntity> S findOne(Class<S> clazz, Criterion criterion, Iterable<? extends Field> fields) {
        return service(clazz, service -> service.findOne(criterion, fields));
    }

    public static <S extends BaseEntity> List<S> findAll(Class<S> clazz, S entity) {
        return service(clazz, service -> service.findAll(entity));
    }

    public static <S extends BaseEntity> List<S> findAll(Class<S> clazz, S entity, Iterable<? extends Field> fields) {
        return service(clazz, service -> service.findAll(entity, fields));
    }

    public static <S extends BaseEntity> List<S> findAll(Class<S> clazz, Iterable<Long> longs) {
        return service(clazz, service -> service.findAll(longs));
    }

    public static <S extends BaseEntity> List<S> findAll(Class<S> clazz, Iterable<Long> longs, Iterable<? extends Field> fields) {
        return service(clazz, service -> service.findAll(longs, fields));
    }

    public static <S extends BaseEntity> List<S> findAll(Class<S> clazz, String field, Iterable<Long> longs) {
        return service(clazz, service -> service.findAll(field, longs));
    }

    public static <S extends BaseEntity> List<S> findAll(Class<S> clazz, String field, Iterable<Long> longs, Iterable<? extends Field> fields) {
        return service(clazz, service -> service.findAll(field, longs, fields));
    }

    public static <S extends BaseEntity> List<S> findAll(Class<S> clazz, Criterion criterion) {
        return service(clazz, service -> service.findAll(criterion));
    }

    public static <S extends BaseEntity> List<S> findAll(Class<S> clazz, Criterion criterion, Iterable<? extends Field> fields) {
        return service(clazz, service -> service.findAll(criterion, fields));
    }

    public static <S extends BaseEntity> Page<S> findAll(Class<S> clazz, S entity, Pageable pageable) {
        return service(clazz, service -> service.findAll(entity, pageable));
    }

    public static <S extends BaseEntity> Page<S> findAll(Class<S> clazz, S entity, Pageable pageable, Iterable<? extends Field> fields) {
        return service(clazz, service -> service.findAll(entity, pageable, fields));
    }

    public static <S extends BaseEntity> Page<S> findAll(Class<S> clazz, Criterion criterion, Pageable pageable) {
        return service(clazz, service -> service.findAll(criterion, pageable));
    }

    public static <S extends BaseEntity> Page<S> findAll(Class<S> clazz, Criterion criterion, Pageable pageable, boolean count) {
        return service(clazz, service -> service.findAll(criterion, pageable, count));
    }

    public static <S extends BaseEntity> Page<S> findAll(Class<S> clazz, Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        return service(clazz, service -> service.findAll(criterion, pageable, fields));
    }

    public static <S extends BaseEntity> Page<S> findAll(Class<S> clazz, Criterion criterion, Pageable pageable, Iterable<? extends Field> fields, boolean count) {
        return service(clazz, service -> service.findAll(criterion, pageable, fields, count));
    }

    public static <S extends BaseEntity> Page<S> findAll(Class<S> clazz, Criterion criterion, Pageable pageable, Iterable<? extends Field> fields, boolean count, Association association) {
        return service(clazz, service -> service.findAll(criterion, pageable, fields, count, association));
    }

    public static <S extends BaseEntity> Page<Map<String, Object>> queryForList(Class<S> clazz, Criterion criterion, Pageable pageable) {
        return service(clazz, service -> service.queryForList(criterion, pageable));
    }

    public static <S extends BaseEntity> Page<Map<String, Object>> queryForList(Class<S> clazz, Criterion criterion, Pageable pageable, Iterable<? extends Field> fields) {
        return service(clazz, service -> service.queryForList(criterion, pageable, fields));
    }

    public static <S extends BaseEntity, E> Page<Map<String, Object>> queryForList(Class<S> clazz, Iterable<E> keys) {
        return service(clazz, service -> service.queryForList(keys));
    }

    public static <S extends BaseEntity, E> Page<Map<String, Object>> queryForList(Class<S> clazz, Iterable<E> keys, int type) {
        return service(clazz, service -> service.queryForList(keys, type));
    }

    public static <S extends BaseEntity> long count(Class<S> clazz) {
        return service(clazz, BaseRepository::count);
    }

    public static <S extends BaseEntity> long count(Class<S> clazz, S entity) {
        return service(clazz, service -> service.count(entity));
    }

    public static <S extends BaseEntity> long count(Class<S> clazz, Criterion criterion) {
        return service(clazz, service -> service.count(criterion));
    }

    public static <S extends BaseEntity> boolean exists(Class<S> clazz, Long id) {
        return service(clazz, service -> service.exists(id));
    }

    public static <S extends BaseEntity> boolean exists(Class<S> clazz, S entity) {
        return service(clazz, service -> service.exists(entity));
    }

    public static <S extends BaseEntity> boolean exists(Class<S> clazz, Criterion criterion) {
        return service(clazz, service -> service.exists(criterion));
    }

    public static <S extends BaseEntity> S queryForObject(Class<S> clazz, Long id) {
        return service(clazz, service -> service.queryForObject(id));
    }

    public static <S extends BaseEntity> S queryForObject(Class<S> clazz, Long id, Iterable<? extends Field> fields) {
        return service(clazz, service -> service.queryForObject(id, fields));
    }

    public static <S extends BaseEntity, E> E transactional(Class<S> clazz, Supplier<E> supplier) {
        return service(clazz, service -> service.transactional(supplier));
    }

    public static <S extends BaseEntity> void doPage(Class<S> clazz, S entity, Pageable pageable, Consumer<? super S> consumer) {
        Page<S> page;
        do {
            page = findAll(clazz, entity, pageable);
            page.forEach(consumer);
        } while (page.hasNext() && (pageable = pageable.next()) != null);
    }

    public static <S extends BaseEntity> void doPage(Class<S> clazz, S entity, Pageable pageable, Iterable<? extends Field> fields, Consumer<? super S> consumer) {
        Page<S> page;
        do {
            page = findAll(clazz, entity, pageable, fields);
            page.forEach(consumer);
        } while (page.hasNext() && (pageable = pageable.next()) != null);
    }

    public static <S extends BaseEntity> void doPage(Class<S> clazz, Criterion criterion, Pageable pageable, Consumer<? super S> consumer) {
        Page<S> page;
        do {
            page = findAll(clazz, criterion, pageable);
            page.forEach(consumer);
        } while (page.hasNext() && (pageable = pageable.next()) != null);
    }

    public static <S extends BaseEntity> void doPage(Class<S> clazz, Criterion criterion, Pageable pageable, Iterable<? extends Field> fields, Consumer<? super S> consumer) {
        Page<S> page;
        do {
            page = findAll(clazz, criterion, pageable, fields);
            page.forEach(consumer);
        } while (page.hasNext() && (pageable = pageable.next()) != null);
    }

    public static <S extends BaseEntity> void doList(Class<S> clazz, S entity, Pageable pageable, Consumer<? super List<S>> consumer) {
        Page<S> page;
        do {
            page = findAll(clazz, entity, pageable);
            consumer.accept(page.getContent());
        } while (page.hasNext() && (pageable = pageable.next()) != null);
    }

    public static <S extends BaseEntity> void doList(Class<S> clazz, S entity, Pageable pageable, Iterable<? extends Field> fields, Consumer<? super List<S>> consumer) {
        Page<S> page;
        do {
            page = findAll(clazz, entity, pageable, fields);
            consumer.accept(page.getContent());
        } while (page.hasNext() && (pageable = pageable.next()) != null);
    }

    public static <S extends BaseEntity> void doList(Class<S> clazz, Criterion criterion, Pageable pageable, Consumer<? super List<S>> consumer) {
        Page<S> page;
        do {
            page = findAll(clazz, criterion, pageable);
            consumer.accept(page.getContent());
        } while (page.hasNext() && (pageable = pageable.next()) != null);
    }

    public static <S extends BaseEntity> void doList(Class<S> clazz, Criterion criterion, Pageable pageable, Iterable<? extends Field> fields, Consumer<? super List<S>> consumer) {
        Page<S> page;
        do {
            page = findAll(clazz, criterion, pageable, fields);
            consumer.accept(page.getContent());
        } while (page.hasNext() && (pageable = pageable.next()) != null);
    }

    public static <T extends BaseEntity> void doList(Class<T> clazz, Date from, Date to, List<Field> fields, Criterion criterion, Consumer<Page<T>> consumer) {
        Pageable pageable = Pages.builder().page(1).size(100).sort(Pages.sortBuilder().add(BaseEntity.ID, true).build()).build();
        Page<T> page = Services.findAll(clazz, criterion(from, to, 0L, criterion), pageable, fields, false);
        while (page.hasContent()) {
            consumer.accept(page);
            Long id = page.getContent().get(page.getContent().size() - 1).getId();
            page = Services.findAll(clazz, criterion(from, to, id, criterion), pageable, fields, false);
        }
    }

    /**
     * @param clazz     clazz
     * @param criterion criterion
     * @param fields    fields
     * @param function  function
     * @param <T>       T
     */
    public static <T extends BaseEntity> void doList(Class<T> clazz, Criterion criterion, List<Field> fields, Function<Page<T>, Boolean> function) {
        Pageable pageable = Pages.builder().page(1).size(100).sort(Pages.sortBuilder().add(BaseEntity.ID, true).build()).build();
        Page<T> page = Services.findAll(clazz, Restrictions.and(criterion, Restrictions.gt(BaseEntity.ID, 0)), pageable, fields, false);
        try {
            while (page.hasContent()) {
                if (Boolean.TRUE == function.apply(page)) {
                    Long id = page.getContent().get(page.getNumberOfElements() - 1).getId();
                    page = Services.findAll(clazz, Restrictions.and(criterion, Restrictions.gt(BaseEntity.ID, id)), pageable, fields, false);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param clazz     clazz
     * @param criterion criterion
     * @param function  function
     * @param <T>       T
     */
    public static <T extends BaseEntity> void doList(Class<T> clazz, Criterion criterion, Function<Page<T>, Boolean> function) {
        Pageable pageable = Pages.builder().page(1).size(100).sort(Pages.sortBuilder().add(BaseEntity.ID, true).build()).build();
        Page<T> page = Services.findAll(clazz, Restrictions.and(criterion, Restrictions.gt(BaseEntity.ID, 0)), pageable, false);
        while (page.hasContent()) {
            if (Boolean.TRUE == function.apply(page)) {
                Long id = page.getContent().get(page.getNumberOfElements() - 1).getId();
                page = Services.findAll(clazz, Restrictions.and(criterion, Restrictions.gt(BaseEntity.ID, id)), pageable, false);
            } else {
                break;
            }
        }
    }

    public static <T extends BaseEntity> int saveOrUpdate(Class<T> clazz, T unique, T insert, T update, Consumer<Action> consumer) {
        return saveOrUpdate(clazz, unique, insert, update, null, consumer);
    }

    public static <T extends BaseEntity> int saveOrUpdate(Class<T> clazz, T unique, T insert, T update, Criterion criterion, Consumer<Action> consumer) {
        T t = Services.findOne(clazz, unique, Fields.builder().add(BaseEntity.ID).build());
        int n;
        Action action = Action.NONE;
        if (t == null) {
            n = Services.save(clazz, insert);
            if (n > 0) {
                action = Action.INSERT;
            }
        } else {
            update.setId(t.getId());
            n = Services.update(clazz, update, Restrictions.eq(BaseEntity.ENABLED, 0));
            if (n > 0) {
                action = Action.INSERT;
            } else if (criterion != null) {
                n = Services.update(clazz, update, criterion);
                if (n > 0) {
                    action = Action.UPDATE_BY_CRITERION;
                }
            } else {
                n = Services.update(clazz, update);
                if (n > 0) {
                    action = Action.UPDATE;
                }
            }
        }

        consumer.accept(action);
        return n;
    }

    public static <T extends BaseEntity> int remove(Class<T> clazz, Iterable<Long> ids, String target, Consumer<T> consumer) {
        return remove(clazz, ids, Fields.builder().add(target).build(), consumer);
    }

    public static <T extends BaseEntity> int remove(Class<T> clazz, Iterable<Long> ids, List<Field> fields, Consumer<T> consumer) {
        Set<Long> set = new HashSet<>();
        for (Long id : ids) {
            if (Services.remove(clazz, id, Restrictions.eq(BaseEntity.ENABLED, 1)) > 0) {
                set.add(id);
            }
        }

        if (set.isEmpty()) {
            return 0;
        }

        Services.findAll(clazz, Restrictions.in(BaseEntity.ID, set.toArray()), fields).forEach(consumer);

        return set.size();
    }

    @SuppressWarnings("unchecked")
    public static <S extends BaseEntity> int doToOnePatch(Class<S> clazz, Long id, String relationship) {
        OneToOne annotation = oneToOne(clazz, relationship);
        validate(annotation, relationship);
        try {
            BaseEntity update = annotation.targetEntity().newInstance();
            update.setId(id);
            update.setModified(new Date());

            BeanWrapper wrapper = new BeanWrapperImpl(update);
            wrapper.setPropertyValue(property(annotation.mappedBy()), id == null ? 0 : id);
            return Services.update((Class<BaseEntity>) annotation.targetEntity(), update);
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * @param clazz        clazz
     * @param id           id
     * @param relationship relationship
     * @param ids          ids
     * @param <S>          S
     * @return
     * @deprecated delete in the future
     */
    @Deprecated
    public static <S extends BaseEntity> int doToManyPatch(Class<S> clazz, Long id, String relationship, Set<Long> ids) {
        throw new UnsupportedOperationException();
    }

    public static <S extends BaseEntity> int doToManyPatch(Class<S> clazz, Long id, String relationship, Map<Long, Map<String, Object>> data, boolean delete) {
        return doToManyPatch(clazz, id, relationship, data, delete, null);
    }

    @SuppressWarnings("unchecked")
    public static <S extends BaseEntity> int doToManyPatch(Class<S> clazz, Long id, String relationship, Map<Long, Map<String, Object>> data, boolean delete, Consumer<BaseEntity> validator) {
        OneToMany annotation = oneToMany(clazz, relationship);
        validate(annotation, relationship);

        // TODO 检查外检约束

        Criterion criterion;
        if (delete)
            criterion = Restrictions.eq(annotation.mappedBy(), id);
        else
            criterion = Restrictions.and(Restrictions.eq(BaseEntity.ENABLED, 1), Restrictions.eq(annotation.mappedBy(), id));

        // delete all if data is empty
        if (data.isEmpty()) {
            Set<Long> ids = toSet(findAll(annotation, criterion));
            if (delete) {
                // delete
                return result(ids.isEmpty(), () -> Services.delete(annotation.targetEntity(), ids));
            } else {
                // remove
                return Services.remove(annotation.targetEntity(), ids);
            }
        } else {
            // insert and delete
            return Services.transactional(annotation.targetEntity(), () -> {
                Map<Long, Long> map = findAll(annotation, criterion).stream().collect(Collectors.toMap(entity -> map(annotation, entity), BaseEntity::getId));
                Set<Long> targets = map.keySet();

                Map<Long, Map<String, Object>> inserts = new HashMap<>(data);
                targets.stream().filter(key -> CollectionUtils.isEmpty(inserts.get(key))).forEach(inserts::remove);
                targets.removeAll(data.keySet());
                Collection<Long> deletes = map.values();

                int result = 0;
                if (delete) {
                    result += result(inserts.isEmpty(), () -> Services.save((Class<BaseEntity>) annotation.targetEntity(), map(annotation, id, inserts, validator)));
                    result += result(deletes.isEmpty(), () -> Services.delete(annotation.targetEntity(), deletes));
                } else {
                    if (!inserts.isEmpty())
                        result += saveOrUpdate(annotation, id, inserts, validator);
                    if (!deletes.isEmpty())
                        result += Services.remove(annotation.targetEntity(), deletes);
                }
                return result;
            });

        }
    }

    public static <S extends BaseEntity> int doToManyPost(Class<S> clazz, Long id, String relationship, Map<Long, Map<String, Object>> data, boolean delete) {
        return doToManyPost(clazz, id, relationship, data, delete, null);
    }

    @SuppressWarnings("unchecked")
    public static <S extends BaseEntity> int doToManyPost(Class<S> clazz, Long id, String relationship, Map<Long, Map<String, Object>> data, boolean delete, Consumer<BaseEntity> validator) {
        OneToMany annotation = oneToMany(clazz, relationship);
        validate(annotation, relationship);
        if (delete) {
            Map<Long, Map<String, Object>> inserts = new HashMap<>(data);
            findAll(annotation, id, data).stream().map(entity -> map(annotation, entity)).forEach(inserts::remove);
            return result(inserts.isEmpty(), () -> Services.save((Class<BaseEntity>) annotation.targetEntity(), map(annotation, id, inserts, validator)));
        } else {
            return saveOrUpdate(annotation, id, data, validator);
        }
    }

    @SuppressWarnings("unchecked")
    public static <S extends BaseEntity> int doToManyDelete(Class<S> clazz, Long id, String relationship, Map<Long, Map<String, Object>> data, boolean delete) {
        OneToMany annotation = oneToMany(clazz, relationship);
        validate(annotation, relationship);
        if (delete) {
            // delete
            Set<Long> deletes = toSet(findAll(annotation, id, data));
            return result(deletes.isEmpty(), () -> Services.delete(annotation.targetEntity(), deletes));
        } else {
            // remove
            Set<Long> removes = toSet(findAll(annotation, id, data, true));
            return Services.remove(annotation.targetEntity(), removes);
        }
    }

    /**
     * 当lock为null时, 并且获取到了唯一锁, 就执行supplier并返回数据
     *
     * @param lock            锁资源
     * @param booleanSupplier 获取唯一锁是否成功, true:成功, false:失败
     * @param supplier        初始化逻辑
     * @param <T>             初始化逻辑返回的结果
     * @return T
     */
    public static <T> T lock(Object lock, BooleanSupplier booleanSupplier, Supplier<T> supplier) {
        try {
            if (lock == null) {
                if (supplier != null && booleanSupplier != null && booleanSupplier.getAsBoolean()) {
                    return supplier.get();
                }
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    public enum Action {
        INSERT,
        DELETE,
        UPDATE,
        UPDATE_BY_CRITERION,
        NONE;
    }

    /**
     * @param clazz    clazz
     * @param function function
     * @param <T>      T
     * @param <R>      R
     * @return R
     */
    @SuppressWarnings("unchecked")
    private static <T extends BaseEntity, R> R service(Class<T> clazz, Function<? super BaseService<T>, R> function) {
        BaseService<T> baseService = applicationContext.getBean(map.computeIfAbsent(clazz, Services::getBeanName), BaseService.class);
        return function.apply(baseService);
    }

    private static String property(String field) {
        StringBuilder sb = new StringBuilder();
        char[] chars = field.toCharArray();
        for (int i = 0; i < chars.length; i++)
            sb.append(chars[i] == '_' ? Character.toUpperCase(chars[++i]) : chars[i]);
        return sb.toString();
    }

    /**
     * insert into on duplicate key update
     *
     * @param annotation annotation
     * @param id         id
     * @param data       data
     * @return int
     */
    @SuppressWarnings("unchecked")
    private static int saveOrUpdate(OneToMany annotation, Long id, Map<Long, Map<String, Object>> data, Consumer<BaseEntity> validator) {
        AtomicInteger result = new AtomicInteger();
        map(annotation, id, data, validator, (entity, entry) -> {
            BaseEntity update = newInstance(annotation.targetEntity());
            update.setEnabled(1);

            Map<String, Object> attributes = entry.getValue();
            if (!CollectionUtils.isEmpty(attributes)) {
                BeanWrapper wrapper = new BeanWrapperImpl(update);
                wrapper.setPropertyValues(new MutablePropertyValues(attributes), true);
            }
            result.addAndGet(Services.saveOrUpdate((Class<BaseEntity>) annotation.targetEntity(), entity, update));
        });
        return result.get();
    }

    /**
     * @param annotation annotation
     * @param id         source ID
     * @param data       target ID with attributes
     * @return List
     */
    private static List<BaseEntity> map(OneToMany annotation, Long id, Map<Long, Map<String, Object>> data, Consumer<BaseEntity> validator) {
        return map(annotation, id, data, validator, null);
    }

    private static List<BaseEntity> map(OneToMany annotation, Long id, Map<Long, Map<String, Object>> data, Consumer<BaseEntity> validator, BiConsumer<BaseEntity, Map.Entry<Long, Map<String, Object>>> consumer) {
        return data.entrySet().stream().map(entry -> {
            BaseEntity entity = newInstance(annotation.targetEntity());

            String mappedBy = property(annotation.mappedBy());
            String mappedTo = property(annotation.mappedTo());

            BeanWrapper wrapper = new BeanWrapperImpl(entity);
            wrapper.setPropertyValue(mappedBy, id);
            wrapper.setPropertyValue(mappedTo, entry.getKey());

            Map<String, Object> attributes = entry.getValue();
            if (!CollectionUtils.isEmpty(attributes))
                wrapper.setPropertyValues(new MutablePropertyValues(attributes), true);

            if (validator != null)
                validator.accept(entity);

            if (consumer != null)
                consumer.accept(entity, entry);

            return entity;
        }).collect(Collectors.toList());
    }

    private static Long map(OneToMany annotation, BaseEntity entity) {
        BeanWrapper wrapper = new BeanWrapperImpl(entity);
        return (Long) wrapper.getPropertyValue(property(annotation.mappedTo()));
    }

    private static List<BaseEntity> findAll(OneToMany annotation, Long id, Map<Long, Map<String, Object>> data) {
        return findAll(annotation, id, data, null);
    }

    private static List<BaseEntity> findAll(OneToMany annotation, Long id, Map<Long, Map<String, Object>> data, Boolean enabled) {
        if (CollectionUtils.isEmpty(data))
            return Collections.emptyList();

        List<Criterion> root = new ArrayList<>();
        data.forEach((identifier, attributes) -> {
            List<Criterion> criteria = new ArrayList<>();
            criteria.add(Restrictions.eq(annotation.mappedBy(), id));
            criteria.add(Restrictions.eq(annotation.mappedTo(), identifier));

            if (enabled != null)
                criteria.add(Restrictions.eq(BaseEntity.ENABLED, enabled ? 1 : 0));

            if (!CollectionUtils.isEmpty(attributes))
                attributes.forEach((key, value) -> criteria.add(Restrictions.eq(QueryFieldOperator.convert(key), value)));

            root.add(Restrictions.and(criteria));
        });
        return findAll(annotation, Restrictions.or(root));
    }

    @SuppressWarnings("unchecked")
    private static List<BaseEntity> findAll(OneToMany annotation, Criterion criterion) {
        Fields.FieldBuilder builder = Fields.builder()
                .add(BaseEntity.ID)
                .add(annotation.mappedBy())
                .add(annotation.mappedTo());
        return Services.findAll((Class<BaseEntity>) annotation.targetEntity(), criterion, builder.build());
    }

    private static Set<Long> toSet(List<BaseEntity> list) {
        return list.stream().map(BaseEntity::getId).collect(Collectors.toSet());
    }

    private static BaseEntity newInstance(Class<? extends BaseEntity> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * <p>查找符合条件的 one-to-one 关系</p>
     *
     * @param value value
     * @return OneToOne
     */
    private static <T extends BaseEntity> OneToOne oneToOne(Class<T> entityClass, String value) {
        return function(entityClass, OneToOne.class, OneToOneArray.class, (annotation, annotations) -> {
            if (annotation != null && annotation.value().equals(value)) {
                return annotation;
            } else if (annotations != null) {
                for (OneToOne a : annotations.value()) {
                    if (a.value().equals(value))
                        return a;
                }
            }
            return null;
        });
    }

    /**
     * <p>查找符合条件的 one-to-many 关系</p>
     *
     * @param value value
     * @return OneToMany
     */
    private static <T extends BaseEntity> OneToMany oneToMany(Class<T> entityClass, String value) {
        return function(entityClass, OneToMany.class, OneToManyArray.class, (annotation, annotations) -> {
            if (annotation != null && annotation.value().equals(value)) {
                return annotation;
            } else if (annotations != null) {
                for (OneToMany a : annotations.value())
                    if (a.value().equals(value))
                        return a;
            }
            return null;
        });
    }

    private static <T extends BaseEntity, E extends Annotation, U extends Annotation> E function(Class<T> entityClass, Class<E> annotation, Class<U> annotations, BiFunction<E, U, E> function) {
        for (java.lang.reflect.Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotation) || field.isAnnotationPresent(annotations)) {
                E result = function.apply(field.getAnnotation(annotation), field.getAnnotation(annotations));
                if (result != null)
                    return result;
            }
        }
        return null;
    }

    private static int result(boolean empty, Supplier<int[]> supplier) {
        int n = 0;
        if (!empty)
            n = reduce(supplier.get());
        return n;
    }

    private static int reduce(int[] array) {
        return Arrays.stream(array).reduce(0, (left, right) -> left + right);
    }

    private static void validate(Annotation annotation, String relationship) {
        if (annotation == null)
            throw new IllegalArgumentException(String.format("Unknown relationship : [%s]", relationship));
    }

    /**
     * @param clazz clazz
     * @return service bean name
     */
    private static String getBeanName(Class<?> clazz) {
        StringBuilder sb = new StringBuilder(clazz.getSimpleName());
        if (!isUpperCase(sb, 1)) {
            char c = Character.toLowerCase(sb.charAt(0));
            sb.deleteCharAt(0);
            sb.insert(0, c);
        }
        sb.append("ServiceImpl");
        return sb.toString();
    }

    private static boolean isUpperCase(StringBuilder sb, int index) {
        return sb.length() > index && Character.isUpperCase(sb.charAt(index));
    }

    private static Criterion criterion(Date modified, Date now, Long id, Criterion criterion) {
        List<Criterion> list = new ArrayList<>(5);
        list.add(Restrictions.ge(BaseEntity.MODIFIED, modified));
        list.add(Restrictions.lt(BaseEntity.MODIFIED, now));
        // list.add(Restrictions.eq(BaseEntity.ENABLED, 1));
        list.add(Restrictions.gt(BaseEntity.ID, id));
        if (criterion != null)
            list.add(criterion);
        return Restrictions.and(list);
    }

}
