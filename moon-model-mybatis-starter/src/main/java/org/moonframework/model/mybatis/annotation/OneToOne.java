package org.moonframework.model.mybatis.annotation;

import org.moonframework.model.mybatis.domain.BaseEntity;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/21
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(OneToOneArray.class)
public @interface OneToOne {

    @AliasFor("type")
    String value() default "";

    @AliasFor("value")
    String type() default "";

    /**
     * relations class type
     */
    Class<? extends BaseEntity> targetEntity();

    /**
     * field name
     */
    String mappedBy() default "";

}
