package org.moonframework.web.core;

import org.moonframework.web.jsonapi.Data;
import org.moonframework.web.jsonapi.ResourceObject;
import org.moonframework.web.jsonapi.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/17
 */
public interface Restful<T, ID> {

    /**
     * <p>POST</p>
     * <p>新增资源</p>
     *
     * @param data data
     * @return response
     */
    ResponseEntity<Response> doPost(Data<T> data);

    /**
     * <p>DELETE</p>
     *
     * @param id id
     * @return response
     */
    ResponseEntity<Response> doDelete(ID id);

    /**
     * <p>PATCH</p>
     * <p>更新资源</p>
     * <ol>
     * <li>向表示资源的URL发出PATCH请求, 即可进行资源更新.</li>
     * <li>PATCH请求必须包括一个资源对象(resource object)作为主数据(primary data), 资源对象必须 [MUST] 包含id成员.</li>
     * </ol>
     * <p>更新属性</p>
     * <ol>
     * <li>资源的任何一个或所有属性, 可能 [MAY] 被包含在PATCH请求的资源对象中(resource object).</li>
     * <li>如果请求不包括资源所有的属性, 那么服务器解译请求时, 必须 [MUST] 添加这些属性并赋予其当前的值. 服务器不能 [MUST NOT] 给缺失的属性赋值为null.</li>
     * </ol>
     * <pre>
     * PATCH /products/1 HTTP/1.1
     * Content-Type: application/json
     * Accept: application/json<
     * </pre>
     *
     * @param id   id
     * @param data data
     * @return response
     */
    ResponseEntity<Response> doPatch(ID id, Data<T> data);

    /**
     * <p>更新 To-One 关联</p>
     * <ol>
     * <li>PATCH请求必须 [MUST] 包括top-level中的data的成员, data中的数据包含如下其一:
     * <ul>
     * <li>关联到新资源的资源标识对象</li>
     * <li>或null, 用来删除关联</li>
     * </ul>
     * </li>
     * <li>relationships作为URL中的关键字来表示不同资源之间的关联关系.</li>
     * <li>如果关联被成功更新, 服务器必须 [MUST] 返回一个成功的响应.</li>
     * </ol>
     *
     * @param id           id
     * @param relationship relationship
     * @param data         data
     * @return
     */
    ResponseEntity<Response> doToOnePatch(ID id, String relationship, Data<ResourceObject> data);

    /**
     * <ol>
     * <li>对于To-Many关联连接的URL, 服务器必须 [MUST] 能够响应 PATCH, POST, 和 DELETE 请求.</li>
     * <li>对于所有请求类型, 实体主体(entity-body)必须 [MUST] 包括一个data成员, 其值要么是一个空数组, 要么是一个资源标识对象数组.</li>
     * <li>如果客户端向一个to-many关联连接的URL发出 PATCH 请求, 服务器必须 [MUST] 完全更改关联的每一个成员, 如果资源不存在或者无法使用, 返回合适的错误响应, 如果服务器不允许完全更改, 则返回[403 Forbidden].(PATCH 相当于先 DELETE ALL, 再POST)</li>
     * </ol>
     *
     * @param id           id
     * @param relationship relationship
     * @param data         data
     * @return
     */
    ResponseEntity<Response> doToManyPatch(Long id, String relationship, Data<List<ResourceObject>> data);

    ResponseEntity<Response> doToManyPost(Long id, String relationship, Data<List<ResourceObject>> data);

    ResponseEntity<Response> doToManyDelete(Long id, String relationship, Data<List<ResourceObject>> data);

    ResponseEntity<Response> doToManyPatch(Long id, String relationship, Data<List<ResourceObject>> data, boolean delete);

    ResponseEntity<Response> doToManyPost(Long id, String relationship, Data<List<ResourceObject>> data, boolean delete);

    ResponseEntity<Response> doToManyDelete(Long id, String relationship, Data<List<ResourceObject>> data, boolean delete);

    /**
     * <p>GET</p>
     * <p>获取资源</p>
     *
     * @param id      id
     * @param include include
     * @return
     */
    ResponseEntity<Response> doGet(ID id, String[] include);

    /**
     * <p>GET</p>
     * <p>资源列表</p>
     *
     * @param page    page
     * @param size    size
     * @param sort    sort
     * @param include include
     * @return response
     */
    ResponseEntity<Response> doGetPage(int page, int size, String[] sort, String[] include);

}
