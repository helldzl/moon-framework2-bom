package org.moonframework.model.mybatis.criterion;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/1/19
 */
public class QueryField {

    private String table;
    private String field;

    public QueryField(String field) {
        int i = field.indexOf('.');
        if (i != -1) {
            this.table = field.substring(0, i);
            this.field = field.substring(i + 1);
        } else {
            this.field = field;
        }
    }

    public QueryField(String table, String field) {
        this.table = table;
        this.field = field;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (table != null && !table.isEmpty())
            builder.append(table).append('.');
        if ("*".equals(field))
            builder.append('*');
        else
            builder.append(field);
        return builder.toString();
    }
}
