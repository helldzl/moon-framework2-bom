package org.moonframework.model.mybatis.annotation;

import java.lang.annotation.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/11
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(JoinArray.class)
public @interface Join {

    String table();

    String alias() default "";

    String condition();

    JoinType type() default JoinType.JOIN;

    enum JoinType {

        JOIN("JOIN"),
        LEFT_JOIN("LEFT JOIN"),
        RIGHT_JOIN("RIGHT JOIN"),
        STRAIGHT_JOIN("STRAIGHT_JOIN"),
        INNER_JOIN("INNER JOIN"),
        CROSS_JOIN("CROSS JOIN");

        private final String name;

        JoinType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
