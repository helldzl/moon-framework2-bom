package org.moonframework.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/7/7
 */
public final class BeanUtils {

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
    }

    public static TypeFactory typeFactory() {
        return mapper.getTypeFactory();
    }

    /**
     * <p>将源对象的属性复制到目标类型中</p>
     *
     * @param source source
     * @param target target
     * @param <S>    SOURCE
     * @param <T>    TARGET
     * @return T
     */
    public static <S, T> T copyProperties(S source, Class<T> target) {
        try {
            return mapper.readValue(mapper.writeValueAsBytes(source), target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <pre>
     * Map<String, ResultValue> results = mapper.readValue(jsonSource, new TypeReference<Map<String, ResultValue>>() { } );
     * </pre>
     *
     * @param value         value
     * @param typeReference typeReference
     * @param <T>           T
     * @return T
     */
    public static <T> T readValue(String value, TypeReference<?> typeReference) {
        try {
            return mapper.readValue(value, typeReference);
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> T readValue(String value, Class<T> clazz) {
        try {
            return mapper.readValue(value, clazz);
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> T readValue(String value, JavaType valueType) {
        try {
            return mapper.readValue(value, valueType);
        } catch (IOException e) {
            return null;
        }
    }

    public static String writeValueAsString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static JavaType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

    public static JavaType constructCollectionType(Class<? extends Collection> collectionClass, JavaType elementType) {
        return mapper.getTypeFactory().constructCollectionType(collectionClass, elementType);
    }

    public static JavaType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }

    public static JavaType constructMapType(Class<? extends Map> mapClass, JavaType keyType, JavaType valueType) {
        return mapper.getTypeFactory().constructMapType(mapClass, keyType, valueType);
    }

    public static JavaType constructParametricType(Class<?> parametrized, Class<?>... parameterClasses) {
        return mapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }

    public static JavaType constructParametricType(Class<?> rawType, JavaType... parameterTypes) {
        return mapper.getTypeFactory().constructParametricType(rawType, parameterTypes);
    }

    /**
     * {@link #readValue(String, TypeReference)}
     *
     * @param parametrized     parametrized
     * @param parametersFor    parameters for
     * @param parameterClasses parameter classes
     * @return JavaType
     */
    public static JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, Class... parameterClasses) {
        return mapper.getTypeFactory().constructParametrizedType(parametrized, parametersFor, parameterClasses);
    }

    public static JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, JavaType... parameterTypes) {
        return mapper.getTypeFactory().constructParametrizedType(parametrized, parametersFor, parameterTypes);
    }

    /**
     * <p>Bean转换成Map</p>
     *
     * @param obj obj
     * @return map
     */
    public static Map<String, Object> toMap(Object obj) {
        if (obj == null)
            return Collections.emptyMap();
        Map<String, Object> map = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                String key = descriptor.getName();
                if (!"class".equals(key)) {
                    Object value = descriptor.getReadMethod().invoke(obj);
                    if (value != null && !"".equals(value))
                        map.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    //是否嵌套转成map
    public static Map<String, Object> toMap(Object obj, Boolean bool) {
        if (obj == null)
            return Collections.emptyMap();
        if (!bool) {
            return BeanUtils.toMap(obj);
        }
        Map<String, Object> map = new HashMap<>();
        if (obj instanceof List) {
            map.put("1", obj);
        } else {
            map = BeanUtils.toMap(obj);
        }
        objectToMap(map);
        return map;
    }

    // TODO: 2017/8/2  对list支持不是不行，需要再加上
    // FIXME: 需要重构
    public static Map<String, Object> objectToMap(Map<String, Object> map) {
        for (String key : map.keySet()) {
            Object keyObj = map.get(key);
            if (keyObj == null || keyObj == "") {
                continue;
            } else if (keyObj instanceof String) {
                continue;
            } else if (keyObj instanceof Number) {
                continue;
            } else if (keyObj instanceof Date) {
                continue;
            } else if (keyObj instanceof Boolean) {
                continue;
            } else if (keyObj instanceof Enum) {
                map.put(key, ((Enum) keyObj).name());
                continue;
            } else if (keyObj instanceof List) {
                if (((List) keyObj).size() > 0) {
                    Map map001 = new HashMap<>();
                    for (int i = 0; i < ((List) keyObj).size(); i++) {
                        map001.put(i, BeanUtils.toMap(((List) keyObj).get(i), true));
                    }
                    map.put(key, map001);
                }
                // TODO: 2017/8/3 测试完毕之后，再进行优化一次
                /*if (((List) keyObj).size()>0){
                    if(((List) keyObj).get(0) instanceof String||((List) keyObj).get(0) instanceof Number||((List) keyObj).get(0) instanceof Date||((List) keyObj).get(0) instanceof  Boolean){
                        Map map001= new HashMap<>();
                        for (int i=0;i<((List) keyObj).size();i++){
                            map001.put(i,((List) keyObj).get(i));
                        }
                        map.put(key,map001);
                    }
                }*/
            } else {
                map.put(key, objectToMap(BeanUtils.toMap(keyObj)));
            }
        }

        return map;
    }

    /**
     * <p>Iterable对象转换成List</p>
     *
     * @param iterable iterable
     * @param <E>      E
     * @return List
     */
    public static <E> List<Map<String, Object>> toList(Iterable<E> iterable) {
        if (iterable == null)
            return Collections.emptyList();
        List<Map<String, Object>> list = new ArrayList<>();
        for (E e : iterable) {
            Map<String, Object> map = toMap(e);
            if (!map.isEmpty())
                list.add(map);
        }
        return list;
    }

}
