package org.moonframework.web.feign;

import com.fasterxml.jackson.databind.JavaType;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.moonframework.core.util.BeanUtils;
import org.moonframework.web.jsonapi.Data;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author quzile
 * @version 1.0
 * @since 2018/1/17
 */
public class DataDecoder implements Decoder {

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        Response.Body body = response.body();
        if (body == null || !(type instanceof ParameterizedType))
            return null;

        String json = Util.toString(body.asReader());
        Type actual = ((ParameterizedType) type).getActualTypeArguments()[0];

        if (actual instanceof Class<?>) {
            // Data<T>
            if (Void.class == actual) {
                return new Data<Void>(null);
            } else {
                JavaType javaType = BeanUtils.constructParametricType(Data.class, (Class<?>) actual);
                return BeanUtils.readValue(json, javaType);
            }
        } else if (actual instanceof ParameterizedType) {
            // Data<List<T>>
            ParameterizedType parameterizedType = (ParameterizedType) actual;
            actual = parameterizedType.getActualTypeArguments()[0];
            if (List.class == parameterizedType.getRawType()) {
                JavaType javaType = BeanUtils.constructParametricType(
                        Data.class,
                        BeanUtils.constructCollectionType(List.class, (Class<?>) actual));
                return BeanUtils.readValue(json, javaType);
            } else if (Map.class == parameterizedType.getRawType()) {
                JavaType javaType = BeanUtils.constructParametricType(
                        Data.class,
                        BeanUtils.constructMapType(Map.class, String.class, Object.class));
                return BeanUtils.readValue(json, javaType);
            }
        }

        return null;
    }

}
