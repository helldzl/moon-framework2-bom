package org.moonframework.model.mybatis.criterion;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/1/18
 */
public class InExpression extends RangeExpression {

    public InExpression(String propertyName, Object[] values) {
        super(Range.IN, propertyName, values);
    }

}
