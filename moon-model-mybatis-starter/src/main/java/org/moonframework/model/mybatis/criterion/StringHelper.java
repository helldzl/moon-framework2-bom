package org.moonframework.model.mybatis.criterion;

import java.util.Iterator;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/1/19
 */
public class StringHelper {

    static String toString(Object[] array) {
        int len = array.length;
        if (len == 0)
            return "";
        StringBuilder builder = new StringBuilder(len * 12);
        builder.append(array[0]);
        for (int i = 1; i < len; i++)
            builder.append(", ").append(array[i]);
        return builder.toString();
    }

    static <E> String join(String delimiter, Iterator<E> it) {
        StringBuilder builder = new StringBuilder();
        if (it.hasNext())
            builder.append(it.next());
        while (it.hasNext())
            builder.append(delimiter).append(it.next());
        return builder.toString();
    }

    protected StringHelper() {
    }

}
