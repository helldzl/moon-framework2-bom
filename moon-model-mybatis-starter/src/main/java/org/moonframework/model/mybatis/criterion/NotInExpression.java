package org.moonframework.model.mybatis.criterion;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/9/29
 */
public class NotInExpression extends RangeExpression {

    protected NotInExpression(String propertyName, Object[] values) {
        super(Range.NOT_IN, propertyName, values);
    }

}
