package org.moonframework.core.security;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author quzile
 * @version 1.0
 * @since 2018/1/25
 */
public final class Signature {

    public static final String REMOTE = "_remote";
    public static final String SIGNATURE = "signature";
    public static final String TIMESTAMP = "timestamp";

    /**
     * <p>数据格式转换</p>
     *
     * @param map map
     * @return map
     */
    public static Map<String, String[]> apply(Map<String, Collection<String>> map) {
        Map<String, String[]> params = new HashMap<>();
        for (Map.Entry<String, Collection<String>> entry : map.entrySet()) {
            params.put(entry.getKey(), entry.getValue().toArray(new String[entry.getValue().size()]));
        }
        return params;
    }

    /**
     * <p>数据格式转换</p>
     *
     * @param key    key
     * @param values values
     * @return string
     */
    public static String apply(String key, String[] values) {
        if (key == null)
            throw new IllegalArgumentException("KEY must be NOT NULL");

        if (values == null || values.length == 0)
            return key;

        Arrays.sort(values);
        StringBuilder sb = new StringBuilder(key);
        for (String value : values)
            sb.append(value);
        return sb.toString();
    }

    /**
     * <p>根据请求参数生成签名, 并添加一个时间戳</p>
     *
     * @param params    request parameters
     * @param salt      salt
     * @param timestamp timestamp
     * @return md5
     */
    public static String signature(Map<String, String[]> params, String salt, long timestamp) {
        return signature(params, salt, timestamp, null);
    }

    /**
     * <p>根据请求参数生成签名, 并添加一个时间戳, 并可以自定义添加其他参数</p>
     *
     * @param params    request parameters
     * @param salt      salt
     * @param timestamp timestamp
     * @param consumer  params list consumer
     * @return md5
     */
    public static String signature(Map<String, String[]> params, String salt, long timestamp, Consumer<List<String>> consumer) {
        List<String> list = new ArrayList<>();

        if (salt != null)
            list.add(salt);

        if (timestamp > 0L)
            list.add(TIMESTAMP + timestamp);

        if (consumer != null)
            consumer.accept(list);

        params.entrySet().stream().filter(entry -> !SIGNATURE.equals(entry.getKey())).forEach((entry) -> list.add(apply(entry.getKey(), entry.getValue())));
        Collections.sort(list);

        StringBuilder sb = new StringBuilder();
        list.forEach(sb::append);

        return DigestUtils.md5Hex(sb.toString());
    }

    /**
     * <p>比较签名</p>
     *
     * @param map      map
     * @param salt     salt
     * @param function function
     */
    public static <T> T apply(Map<String, String[]> map, String salt, Function<List<String>, T> function) {
        String[] signatures = map.get(SIGNATURE);
        String[] roles = map.get(REMOTE);
        if (function != null
                && roles != null
                && roles.length != 0
                && signatures != null
                && signatures.length != 0
                && signatures[0].equals(signature(map, salt, 0L))) {
            return function.apply(Arrays.asList(roles));
        }
        return null;
    }

}
