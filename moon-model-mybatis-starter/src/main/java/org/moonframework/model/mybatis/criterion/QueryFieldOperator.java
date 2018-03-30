package org.moonframework.model.mybatis.criterion;

import org.moonframework.model.mybatis.domain.Field;
import org.moonframework.model.mybatis.domain.Fields;
import org.moonframework.model.mybatis.domain.Pages;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/7/7
 */
public class QueryFieldOperator {

    private static final String REGEX_FILTER = "filter\\[[A-Za-z0-9,_.:]+\\]";
    private static final String REGEX_FIELDS = "fields\\[[A-Za-z0-9_]+\\]";

    private QueryField queryField;
    private Operator operator = Operator.EQ;

    public QueryFieldOperator(QueryField queryField) {
        this.queryField = queryField;
    }

    public Criterion apply(Object[] values) {
        return operator.apply(queryField, values);
    }

    public QueryFieldOperator operator(String operator) {
        Operator op = Operator.from(operator);
        if (op != null)
            this.operator = op;
        return this;
    }

    @Override
    public String toString() {
        return String.format("Query field : %s,  Operator : %s", queryField.toString(), operator);
    }

    public QueryField getQueryField() {
        return queryField;
    }

    public Operator getOperator() {
        return operator;
    }

    public static List<Field> fields(Map<String, String[]> params) {
        List<Map.Entry<String, String[]>> list = params.entrySet().stream().filter(key -> key.getKey().matches(REGEX_FIELDS)).collect(Collectors.toList());
        boolean single = list.size() == 1;
        return list.stream().map(entry -> {
            String key = replace(entry.getKey(), true);
            String[] array = convert(entry.getValue());
            Fields.FieldBuilder builder = Fields.builder();

            if (array != null && array.length != 0) {
                for (String values : array) {
                    for (String value : values.split(",")) {
                        builder.add(single ? value : key + "." + value);
                    }
                }
            }
            return builder.build();
        }).flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * <p>filter中的key分成三部分filter[{table}.{field}:{eq|ne|gt|lt|le|ge|like|between|in|null|notnull}]</p>
     * <p>第一部分是表名称: table name</p>
     * <p>第二部分是字段名: field name</p>
     * <p>第三部分过滤条件: conditions, 默认是eq, 不能单独出现, 只有field出现, 其才有意义</p>
     *
     * @param params params
     * @return Criterion
     */
    public static Criterion criterion(Map<String, String[]> params) {
        return criterion(params, true);
    }

    public static Criterion criterion(Map<String, String[]> params, boolean convert) {
        List<Criterion> list = params.entrySet()
                .stream()
                .filter(entry -> entry.getKey().matches(REGEX_FILTER))
                .map(entry -> function(entry, convert))
                .reduce(new ArrayList<>(), QueryFieldOperator::accumulator);
        return Restrictions.and(list);
    }

    /**
     * 分页与排序
     *
     * @param page  page start from 1
     * @param size  size
     * @param sorts sort array
     * @return page request
     */
    public static PageRequest pageRequest(int page, int size, String[] sorts) {
        Sort sort = null;
        if (sorts != null) {
            sorts = convert(sorts);
            Pages.SortBuilder builder = Pages.sortBuilder();
            for (String value : sorts) {
                if (value.startsWith("-"))
                    builder.add(value.substring(1), false);
                else
                    builder.add(value, true);
            }
            sort = builder.build();
        }
        return Pages.builder().page(page).size(size).sort(sort).build();
    }

    public static String[] convert(String[] array) {
        if (array == null)
            return null;
        String[] copy = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            copy[i] = convert(array[i]);
        }
        return copy;
    }

    public static String convert(String s) {
        if (StringUtils.isEmpty(s))
            return s;
        char[] chars = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c >= 65 && c <= 90) {
                char last;
                if (i > 0 && (last = chars[i - 1]) != '.' && last != ',' && last != ':' && last != '-') {
                    sb.append("_");
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static List<Criterion> accumulator(List<Criterion> left, List<Criterion> right) {
        if (right.size() == 1)
            left.addAll(right);
        else
            left.add(Restrictions.or(right));
        return left;
    }

    /**
     * <p>去掉多余中括号, 格式化为数据库命名规则</p>
     *
     * @param key     key
     * @param convert convert
     * @return key
     */
    private static String replace(String key, boolean convert) {
        String s = key.replaceAll(".*\\[|\\]", "");
        if (convert)
            return convert(s);
        return s;

    }

    private static QueryFieldOperator alias(String alias, String operator) {
        return alias(alias).operator(operator);
    }

    private static QueryFieldOperator alias(String alias) {
        return function(alias.split("\\."), field -> new QueryFieldOperator(new QueryField(field)), (table, field) -> new QueryFieldOperator(new QueryField(table, field)));
    }

    private static List<Criterion> function(Map.Entry<String, String[]> entry, boolean convert) {
        String key = replace(entry.getKey(), convert);
        QueryFieldOperator operator = function(key.split(":"), QueryFieldOperator::alias, QueryFieldOperator::alias);
        return function(entry.getValue(), v -> operator.apply(v.split(",")));
    }

    private static <T, R> List<R> function(T[] array, Function<T, R> function) {
        List<R> list = new ArrayList<>();
        if (array != null && array.length != 0) {
            for (T t : array) {
                list.add(function.apply(t));
            }
        }
        return list;
    }

    private static <T extends CharSequence, R> R function(T[] array, Function<T, R> x, BiFunction<T, T, R> y) {
        switch (array.length) {
            case 1:
                return x.apply(array[0]);
            case 2:
                return y.apply(array[0], array[1]);
            default:
                return y.apply(join(Arrays.copyOfRange(array, 0, array.length - 1)), array[array.length - 1]);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends CharSequence> T join(T[] array) {
        return (T) String.join(".", (CharSequence[]) array);
    }

    private enum Operator {
        EQ("eq") {
            @Override
            public Criterion apply(QueryField queryField, Object[] values) {
                return apply(queryField, values, Restrictions::eq);
            }
        },
        NE("ne") {
            @Override
            public Criterion apply(QueryField queryField, Object[] values) {
                return apply(queryField, values, Restrictions::ne);
            }
        },
        GT("gt") {
            @Override
            public Criterion apply(QueryField queryField, Object[] values) {
                return apply(queryField, values, Restrictions::gt);
            }
        },
        LT("lt") {
            @Override
            public Criterion apply(QueryField queryField, Object[] values) {
                return apply(queryField, values, Restrictions::lt);
            }
        },
        LE("le") {
            @Override
            public Criterion apply(QueryField queryField, Object[] values) {
                return apply(queryField, values, Restrictions::le);
            }
        },
        GE("ge") {
            @Override
            public Criterion apply(QueryField queryField, Object[] values) {
                return apply(queryField, values, Restrictions::ge);
            }
        },
        LIKE("like") {
            @Override
            public Criterion apply(QueryField queryField, Object[] values) {
                return apply(queryField, values, Restrictions::like);
            }
        },
        RANGE("range") {
            @Override
            public Criterion apply(QueryField queryField, Object[] values) {
                return BETWEEN.apply(queryField, values);
            }
        },
        BETWEEN("between") {
            @Override
            public Criterion apply(QueryField queryField, Object[] values) {
                if (values.length != 2) {
                    throw new IllegalArgumentException();
                }
                String x = (String) values[0];
                String y = (String) values[1];
                String lo = x.substring(1);
                String hi = y.substring(0, y.length() - 1);
                boolean l = x.startsWith("[");
                boolean r = y.endsWith("]");
                if (l && r) {
                    return Restrictions.between(queryField, lo, hi);
                } else if (l) {
                    return Restrictions.and(
                            GE.apply(queryField, new Object[]{lo}),
                            LT.apply(queryField, new Object[]{hi}));
                } else if (r) {
                    return Restrictions.and(
                            GT.apply(queryField, new Object[]{lo}),
                            LE.apply(queryField, new Object[]{hi}));
                } else {
                    return Restrictions.and(
                            GT.apply(queryField, new Object[]{lo}),
                            LT.apply(queryField, new Object[]{hi}));
                }

            }
        },
        IN("in") {
            @Override
            public Criterion apply(QueryField queryField, Object[] values) {
                return Restrictions.in(queryField, values);
            }
        },
        NOT_IN("nin") {
            @Override
            public Criterion apply(QueryField queryField, Object[] values) {
                return Restrictions.notIn(queryField, values);
            }
        },
        NULL("null") {
            public Criterion apply(QueryField queryField, Object[] values) {
                return Restrictions.isNull(queryField);
            }
        },
        NOT_NULL("notnull") {
            @Override
            public Criterion apply(QueryField queryField, Object[] values) {
                return Restrictions.isNotNull(queryField);
            }
        };

        private static Map<String, Operator> map = new HashMap<>(11);

        static {
            for (Operator operator : Operator.values()) {
                map.put(operator.getName(), operator);
            }
        }

        public static Operator from(String name) {
            return map.get(name.toLowerCase());
        }

        private final String name;

        Operator(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public abstract Criterion apply(QueryField queryField, Object[] values);

        protected Criterion apply(QueryField queryField, Object[] values, BiFunction<QueryField, Object, Criterion> function) {
            List<Criterion> disjunctions = new ArrayList<>();
            for (Object value : values)
                disjunctions.add(function.apply(queryField, value));
            if (disjunctions.size() == 1)
                return disjunctions.get(0);
            else
                return Restrictions.or(disjunctions);
        }
    }


}
