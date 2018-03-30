package org.moonframework.model.mybatis.domain;

import org.moonframework.model.mybatis.criterion.QueryFieldOperator;
import org.moonframework.model.mybatis.criterion.Restrictions;

import java.util.Collections;
import java.util.HashSet;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/6
 */
public class Include extends HashSet<String> {

    private static final long serialVersionUID = -1110999398354032575L;

    public static boolean exists() {
        return Restrictions.get(Include.class) != null;
    }

    public static boolean exists(String name) {
        Include include = Restrictions.get(Include.class);
        return include != null && include.contains(name);
    }

    public Include(String[] array) {
        Collections.addAll(this, QueryFieldOperator.convert(array));
    }

}
