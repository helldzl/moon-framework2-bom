package org.moonframework.model.mybatis.annotation;

import java.lang.annotation.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/23
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OneToManyArray {

    OneToMany[] value() default {};

}
