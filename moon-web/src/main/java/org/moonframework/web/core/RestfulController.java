package org.moonframework.web.core;

import org.moonframework.model.mybatis.criterion.Criterion;
import org.moonframework.model.mybatis.criterion.QueryFieldOperator;
import org.moonframework.model.mybatis.criterion.Restrictions;
import org.moonframework.model.mybatis.domain.BaseEntity;
import org.moonframework.model.mybatis.domain.Include;
import org.moonframework.model.mybatis.service.Services;
import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.ResourceObject;
import org.moonframework.web.jsonapi.Response;
import org.moonframework.web.jsonapi.Responses;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.moonframework.validation.ValidationGroups.Patch;
import static org.moonframework.validation.ValidationGroups.Post;

/**
 * <p>GET /users：列出所有用户</p>
 * <p>POST /users：新建一个用户</p>
 * <p>GET /users/{id}：获取某个指定用户的信息</p>
 * <p>PUT /users/{id}：更新某个指定用户的信息（提供该用户的全部信息）</p>
 * <p>PATCH /users/{id}：更新某个指定用户的信息（提供该用户的部分信息）</p>
 * <p>DELETE /users/{id}：删除某个用户</p>
 * <p>GET /users/{id}/accounts：列出某个指定用户的所有账户</p>
 * <p>DELETE /users/{id}/accounts/{id}：删除某个指定用户的指定账户</p>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/03/17
 */
public abstract class RestfulController<T extends BaseEntity> extends BaseController<T> implements Restful<T, Long> {

    public static final int ACCEPTED = -202;
    public static final int NO_CONTENT = -204;
    public static final int FORBIDDEN = -403;

    private final String path;
    private final String type;

    private static Map<String, Class<? extends BaseEntity>> resources = new ConcurrentHashMap<>();
    private static Map<String, RestfulController<BaseEntity>> controller = new ConcurrentHashMap<>();

    public static Class<? extends BaseEntity> get(String key) {
        Class<? extends BaseEntity> clazz = resources.get(key);
        if (clazz == null)
            throw new IllegalArgumentException(String.format("Unknown resource type [%s]", key));
        return clazz;
    }

    public static RestfulController<BaseEntity> controller(String type) {
        return controller.get(type);
    }

    @SuppressWarnings("unchecked")
    public RestfulController() {
        Class<? extends RestfulController> clazz = getClass();
        if (clazz.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping mapping = clazz.getAnnotation(RequestMapping.class);
            path = mapping.value()[0];
            type = path.substring(path.lastIndexOf("/") + 1);

//            if (resources.containsKey(type))
//                throw new IllegalStateException(String.format("Resource type [%s] is exists!", type));
//
//            resources.put(type, entityClass);
//            controller.put(type, (RestfulController<BaseEntity>) this);
        } else {
            path = null;
            type = null;
            // throw new IllegalArgumentException("Require @RequestMapping annotation");
        }
    }

    /**
     * [OK]
     *
     * @param data data
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Response> doPost(@RequestBody Data<T> data) {
        T entity = data.getData();
        Long userId = getCurrentUserId();
        if (userId != null) {
            entity.setCreator(userId);
            entity.setModifier(userId);
        }
        hasError(validate(entity, Post.class));
        afterPostValidate(entity);
        switch (Services.save(entityClass, entity)) {
            case 0:
                throw new IllegalStateException();
            case ACCEPTED:
                return ResponseEntity.accepted().build();
            case NO_CONTENT:
                return ResponseEntity.noContent().build();
            default:
                Map<String, Object> result = new HashMap<>();
                result.put("type", type);
                result.put("id", entity.getId());
                return ResponseEntity.created(URI.create(path + "/" + entity.getId())).body(Responses.builder().data(result));
        }
    }

    /**
     * [OK]
     *
     * @param id id
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Response> doDelete(@PathVariable Long id) {
        Criterion criterion = doDeleteCriterion();
        int n;
        if (criterion == null) {
            n = Services.remove(entityClass, id);
        } else {
            n = Services.remove(entityClass, id, criterion);
        }
        switch (n) {
            case 0:
                return ResponseEntity.notFound().build();
            case ACCEPTED:
                return ResponseEntity.accepted().build();
            default:
                return ResponseEntity.noContent().build();
        }
    }

    /**
     * [OK]
     *
     * @param id   id
     * @param data data
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Response> doPatch(
            @PathVariable Long id,
            @RequestBody Data<T> data) {
        T entity = data.getData();
        entity.setId(id);
        Long userId = getCurrentUserId();
        if (userId != null) {
            entity.setModifier(userId);
        }
        hasError(validate(entity, Patch.class));
        afterPatchValidate(entity);
        Criterion criterion = doPatchCriterion();
        if (criterion == null) {
            return result(Services.update(entityClass, entity));
        } else {
            return result(Services.update(entityClass, entity, criterion));
        }
    }

    /**
     * [OK]
     *
     * @param id           id
     * @param relationship relationship
     * @param data         data
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Response> doToOnePatch(
            @PathVariable Long id,
            @PathVariable String relationship,
            @RequestBody Data<ResourceObject> data) {
        ResourceObject resource = data.getData();
        return result(Services.doToOnePatch(entityClass, resource.getId(), relationship));
    }

    /**
     * [OK]
     *
     * @param id           id
     * @param relationship relationship
     * @param data         data
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Response> doToManyPatch(
            @PathVariable Long id,
            @PathVariable String relationship,
            @RequestBody Data<List<ResourceObject>> data) {
        return doToManyPatch(id, relationship, data, true);
    }

    @Override
    public ResponseEntity<Response> doToManyPatch(Long id, String relationship, Data<List<ResourceObject>> data, boolean delete) {
        List<ResourceObject> list = data.getData();
        notNull(list);
        return result(Services.doToManyPatch(entityClass, id, relationship(data, relationship), data(list), delete, entity -> hasError(validate(entity, Patch.class))));
    }

    /**
     * [OK]
     *
     * @param id           id
     * @param relationship relationship
     * @param data         data
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Response> doToManyPost(
            @PathVariable Long id,
            @PathVariable String relationship,
            @RequestBody Data<List<ResourceObject>> data) {
        return doToManyPost(id, relationship, data, true);
    }

    @Override
    public ResponseEntity<Response> doToManyPost(Long id, String relationship, Data<List<ResourceObject>> data, boolean delete) {
        List<ResourceObject> list = data.getData();
        notNull(list);
        return result(Services.doToManyPost(entityClass, id, relationship(data, relationship), data(list), delete, entity -> hasError(validate(entity, Post.class))));
    }

    /**
     * [OK]
     *
     * @param id           id
     * @param relationship relationship
     * @param data         data
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Response> doToManyDelete(
            @PathVariable Long id,
            @PathVariable String relationship,
            @RequestBody Data<List<ResourceObject>> data) {
        return doToManyDelete(id, relationship, data, true);
    }

    @Override
    public ResponseEntity<Response> doToManyDelete(Long id, String relationship, Data<List<ResourceObject>> data, boolean delete) {
        List<ResourceObject> list = data.getData();
        notNull(list);
        return result(Services.doToManyDelete(entityClass, id, relationship(data, relationship), data(list), delete));
    }

    /**
     * [OK]
     *
     * @param id      id
     * @param include include
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Response> doGet(
            @PathVariable Long id,
            @RequestParam(required = false) String[] include) {
        T t;
        try {
            HttpServletRequest request = getHttpServletRequest();
            if (include != null)
                Restrictions.put(Include.class, new Include(include));
            t = Services.queryForObject(entityClass, id, QueryFieldOperator.fields(request.getParameterMap()));
            if (t == null)
                return ResponseEntity.notFound().build();
            afterGet(t);
        } finally {
            Restrictions.remove();
        }
        Responses.DefaultBuilder builder = Responses.builder();
        Map<String, Object> meta = meta();
        if (!CollectionUtils.isEmpty(meta))
            builder.meta(meta);
        return ResponseEntity.ok(builder.data(t));
    }

    /**
     * [OK]
     *
     * @param page    page
     * @param size    size
     * @param sort    sort
     * @param include include
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<Response> doGetPage(
            @RequestParam(required = false, name = "page[number]", defaultValue = "1") int page,
            @RequestParam(required = false, name = "page[size]", defaultValue = "10") int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] include) {
        HttpServletRequest request;
        Page<T> result;
        try {
            request = getHttpServletRequest();
            if (size > 200) size = 200;
            if (size < 1) size = 1;
            if (include != null)
                Restrictions.put(Include.class, new Include(include));

            //
            Criterion criterion = QueryFieldOperator.criterion(request.getParameterMap());
            if (!Include.exists("admin") || !isRoleAdmin()) {
                criterion = criterion(criterion);
            }

            //
            result = Services.findAll(entityClass, criterion, QueryFieldOperator.pageRequest(page, size, sort), QueryFieldOperator.fields(request.getParameterMap()));
            page(result);
        } finally {
            Restrictions.remove();
        }
        return ResponseEntity.ok(Responses.builder().page(result, path, request.getParameterMap()).data(result.getContent()));
    }

    // protected methods, hook methods

    protected void afterGet(T t) {
    }

    protected void afterPostValidate(T t) {
    }

    protected void afterPatchValidate(T t) {
    }

    protected Criterion criterion(Criterion criterion) {
        return criterion;
    }

    protected Criterion doDeleteCriterion() {
        return null;
    }

    protected Criterion doPatchCriterion() {
        return null;
    }

    protected void page(Page<T> page) {
    }

    protected Map<String, Object> meta() {
        return null;
    }

    // private methods

    private String relationship(Data<List<ResourceObject>> data, String relationship) {
        Map<String, Object> meta = data.getMeta();
        String relationships;
        if (CollectionUtils.isEmpty(meta) || (relationships = (String) meta.get("relationships")) == null) {
            return relationship;
        } else {
            return relationship + ":" + relationships;
        }
    }

    private Map<Long, Map<String, Object>> data(List<ResourceObject> list) {
        Map<Long, Map<String, Object>> map = new HashMap<>();
        for (ResourceObject resourceObject : list) {
            map.put(resourceObject.getId(), resourceObject.getAttributes());
        }
        return map;
    }

    private ResponseEntity<Response> result(int n) {
        switch (n) {
            case 0:
                // 更新成功: 请求中的资源与更新后的结果一致(数据未发生变化)
                return ResponseEntity.noContent().build();
            case ACCEPTED:
                // 服务器接收请求, 但是处理还未完成. e.g : 异步处理
                return ResponseEntity.accepted().build();
            case FORBIDDEN:
                // 不支持的更新关联
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Responses.builder().error(Responses.error(String.valueOf(HttpStatus.FORBIDDEN.value()), getMessage(type + ".request.error.to.one.patch"))));
            default:
                // 更新成功:
                return ResponseEntity.ok(null);
        }
    }

}
