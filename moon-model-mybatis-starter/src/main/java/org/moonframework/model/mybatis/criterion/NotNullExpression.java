package org.moonframework.model.mybatis.criterion;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/2/21
 */
public class NotNullExpression implements Criterion {

    private final String propertyName;

    public NotNullExpression(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public void toSqlString(QueryCondition condition) {
        condition.stringToken(propertyName + " IS NOT NULL");
    }

    @Override
    public String toString() {
        return propertyName + " IS NOT NULL";
    }

}
