package org.moonframework.amqp;

import org.moonframework.core.util.BeanUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>META, 自定义元数据</p>
 * <ol>
 * <li>datetime:消息创建的时间戳, 可以使用LocalDateTime</li>
 * <li>method:消息动作类型['PATCH','POST','GET',...], 遵循HTTP method语义</li>
 * <li>event:事件名称</li>
 * </ol>
 *
 * @author quzile
 * @version 1.0
 * @since 2017/4/24
 */
public class Resource implements Serializable {

    public static final String META_DATETIME = "datetime";
    public static final String META_METHOD = "method";
    public static final String META_EVENT = "event";

    private static final long serialVersionUID = 3076376976237740527L;
    private Map<Class<?>, Object> payload = new HashMap<>();
    private Map<String, Object> meta;
    private Data data;

    public <T> T get(Class<T> type) {
        return type.cast(payload.computeIfAbsent(type, clazz -> BeanUtils.copyProperties(data.getAttributes(), clazz)));
    }

    public Resource(Map<String, Object> meta, Data data) {
        this.meta = meta;
        this.data = data;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public enum Method {

        POST, DELETE, PATCH, PUT, GET

    }

}
