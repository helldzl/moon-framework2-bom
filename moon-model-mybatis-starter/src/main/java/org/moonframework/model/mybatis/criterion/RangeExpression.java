package org.moonframework.model.mybatis.criterion;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/9/29
 */
public abstract class RangeExpression implements Criterion {

    private final Range range;
    private final String propertyName;
    private final Object[] values;

    protected RangeExpression(Range range, String propertyName, Object[] values) {
        this.range = range;
        this.propertyName = propertyName;
        this.values = values;
    }

    @Override
    public void toSqlString(QueryCondition condition) {
        condition.stringToken(propertyName + " " + range.getName() + " (");
        int len = values.length;
        if (len > 0) {
            for (int i = 0; i < len - 1; i++) {
                condition.valueToken(values[i]);
                condition.stringToken(", ");
            }
            condition.valueToken(values[len - 1]);
        }
        condition.stringToken(")");
    }

    @Override
    public String toString() {
        return propertyName + " " + range.getName() + " (" + StringHelper.toString(values) + ')';
    }


    public enum Range {

        IN("IN"),

        NOT_IN("NOT IN");

        private final String name;

        Range(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
