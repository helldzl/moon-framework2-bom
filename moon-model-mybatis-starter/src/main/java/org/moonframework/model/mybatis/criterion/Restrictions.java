package org.moonframework.model.mybatis.criterion;

import org.moonframework.model.mybatis.domain.Include;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/1/18
 */
public final class Restrictions {

    private static final ThreadLocal<Map<Class<?>, Object>> threadLocal =
            new ThreadLocal<Map<Class<?>, Object>>() {
                @Override
                protected Map<Class<?>, Object> initialValue() {
                    return new HashMap<>();
                }
            };

    public static <T> T get(Class<T> type) {
        return type.cast(threadLocal.get().get(type));
    }

    public static <T> void put(Class<T> type, T instance) {
        if (type == null)
            throw new NullPointerException("Type is null");
        threadLocal.get().put(type, instance);
    }

    /**
     * <p>One should always remember to correctly set and unset (where the latter simply involved a call to ThreadLocal.set(null)) the resource local to the thread.</p>
     * <p>Unsetting should be done in any case since not unsetting it might result in problematic behavior.</p>
     */
    public static void remove() {
        threadLocal.remove();
    }

    public static void put(String[] array, BooleanSupplier supplier) {
        try {
            Restrictions.put(Include.class, new Include(null));
            supplier.getAsBoolean();
        } finally {
            Restrictions.remove();
        }
    }

    public static SimpleExpression bitand(QueryField queryField, Object value) {
        return new SimpleExpression(queryField.toString(), value, "&");
    }

    public static SimpleExpression eq(QueryField queryField, Object value) {
        return new SimpleExpression(queryField.toString(), value, "=");
    }

    public static SimpleExpression ne(QueryField queryField, Object value) {
        return new SimpleExpression(queryField.toString(), value, "<>");
    }

    public static SimpleExpression gt(QueryField queryField, Object value) {
        return new SimpleExpression(queryField.toString(), value, ">");
    }

    public static SimpleExpression lt(QueryField queryField, Object value) {
        return new SimpleExpression(queryField.toString(), value, "<");
    }

    public static SimpleExpression le(QueryField queryField, Object value) {
        return new SimpleExpression(queryField.toString(), value, "<=");
    }

    public static SimpleExpression ge(QueryField queryField, Object value) {
        return new SimpleExpression(queryField.toString(), value, ">=");
    }

    //

    /**
     * <p>Apply an "&" constraint to the named property</p>
     *
     * @param propertyName The name of the property
     * @param value        The value to use in comparison
     * @return SimpleExpression
     */
    public static SimpleExpression bitand(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "&");
    }

    /**
     * <p>Apply an "equal" constraint to the named property</p>
     *
     * @param propertyName The name of the property
     * @param value        The value to use in comparison
     * @return SimpleExpression
     * @see SimpleExpression
     */
    public static SimpleExpression eq(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "=");
    }

    /**
     * <p>Apply a "not equal" constraint to the named property</p>
     *
     * @param propertyName The name of the property
     * @param value        The value to use in comparison
     * @return The Criterion
     * @see SimpleExpression
     */
    public static SimpleExpression ne(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "<>");
    }

    /**
     * <p>Apply a "greater than" constraint to the named property</p>
     *
     * @param propertyName The name of the property
     * @param value        The value to use in comparison
     * @return The Criterion
     * @see SimpleExpression
     */
    public static SimpleExpression gt(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, ">");
    }

    /**
     * <p>Apply a "less than" constraint to the named property</p>
     *
     * @param propertyName The name of the property
     * @param value        The value to use in comparison
     * @return The Criterion
     * @see SimpleExpression
     */
    public static SimpleExpression lt(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "<");
    }

    /**
     * <p>Apply a "less than or equal" constraint to the named property</p>
     *
     * @param propertyName The name of the property
     * @param value        The value to use in comparison
     * @return The Criterion
     * @see SimpleExpression
     */
    public static SimpleExpression le(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, "<=");
    }

    /**
     * <p>Apply a "greater than or equal" constraint to the named property</p>
     *
     * @param propertyName The name of the property
     * @param value        The value to use in comparison
     * @return The Criterion
     * @see SimpleExpression
     */
    public static SimpleExpression ge(String propertyName, Object value) {
        return new SimpleExpression(propertyName, value, ">=");
    }

    /**
     * <p>Apply a "like" constraint to the named property</p>
     *
     * @param propertyName The name of the property
     * @param value        The value to use in comparison
     * @return The Criterion
     * @see SimpleExpression
     */
    public static SimpleExpression like(String propertyName, Object value) {
        // todo : update this to use LikeExpression
        return new SimpleExpression(propertyName, value, " LIKE ");
    }

    public static SimpleExpression like(QueryField queryField, Object value) {
        return new SimpleExpression(queryField.toString(), value, " LIKE ");
    }

    /**
     * <p>Apply a "like" constraint to the named property using the provided match mode</p>
     *
     * @param propertyName The name of the property
     * @param value        The value to use in comparison
     * @param matchMode    The match mode to use in comparison
     * @return The Criterion
     * @see SimpleExpression
     */
    public static SimpleExpression like(String propertyName, String value, MatchMode matchMode) {
        // todo : update this to use LikeExpression
        return new SimpleExpression(propertyName, matchMode.toMatchString(value), " LIKE ");
    }

    public static SimpleExpression like(QueryField queryField, String value, MatchMode matchMode) {
        return new SimpleExpression(queryField.toString(), matchMode.toMatchString(value), " LIKE ");
    }

    /**
     * <p>A case-insensitive "like" (similar to Postgres <tt>ilike</tt> operator)</p>
     *
     * @param propertyName The name of the property
     * @param value        The value to use in comparison
     * @return The Criterion
     * @see LikeExpression
     */
    public static Criterion ilike(String propertyName, Object value) {
        if (value == null)
            throw new IllegalArgumentException("Comparison value passed to ilike cannot be null");
        return ilike(propertyName, value.toString(), MatchMode.EXACT);
    }

    /**
     * <p>A case-insensitive "like" (similar to Postgres <tt>ilike</tt> operator) using the provided match mode</p>
     *
     * @param propertyName The name of the property
     * @param value        The value to use in comparison
     * @param matchMode    The match mode to use in comparison
     * @return The Criterion
     * @see LikeExpression
     */
    public static Criterion ilike(String propertyName, String value, MatchMode matchMode) {
        if (value == null)
            throw new IllegalArgumentException("Comparison value passed to ilike cannot be null");
        return new LikeExpression(propertyName, value, matchMode, null, true);
    }

    /**
     * <p>Apply a "between" constraint to the named property</p>
     *
     * @param propertyName The name of the property
     * @param lo           The low value
     * @param hi           The high value
     * @return The Criterion
     * @see BetweenExpression
     */
    public static Criterion between(String propertyName, Object lo, Object hi) {
        return new BetweenExpression(propertyName, lo, hi);
    }

    public static Criterion between(QueryField queryField, Object lo, Object hi) {
        return new BetweenExpression(queryField.toString(), lo, hi);
    }

    /**
     * <p>Apply an "in" constraint to the named property</p>
     *
     * @param propertyName The name of the property
     * @param values       The literal values to use in the IN restriction
     * @return The Criterion
     * @see InExpression
     */
    public static Criterion in(String propertyName, Object[] values) {
        return new InExpression(propertyName, values);
    }

    public static Criterion in(QueryField queryField, Object[] values) {
        return new InExpression(queryField.toString(), values);
    }

    public static Criterion notIn(String propertyName, Object[] values) {
        return new NotInExpression(propertyName, values);
    }

    public static Criterion notIn(QueryField queryField, Object[] values) {
        return new NotInExpression(queryField.toString(), values);
    }

    /**
     * Apply an "is null" constraint to the named property
     *
     * @param propertyName The name of the property
     * @return Criterion
     * @see NullExpression
     */
    public static Criterion isNull(String propertyName) {
        return new NullExpression(propertyName);
    }

    public static Criterion isNull(QueryField queryField) {
        return new NullExpression(queryField.toString());
    }

    /**
     * Apply an "is not null" constraint to the named property
     *
     * @param propertyName The property name
     * @return The Criterion
     * @see NotNullExpression
     */
    public static Criterion isNotNull(String propertyName) {
        return new NotNullExpression(propertyName);
    }

    public static Criterion isNotNull(QueryField queryField) {
        return new NotNullExpression(queryField.toString());
    }

    /**
     * <p>Return the conjuction of two expressions</p>
     *
     * @param lhs One expression
     * @param rhs The other expression
     * @return The Criterion
     */
    public static LogicalExpression and(Criterion lhs, Criterion rhs) {
        return new LogicalExpression(lhs, rhs, "AND");
    }

    /**
     * <p>Return the conjuction of multiple expressions</p>
     *
     * @param predicates The predicates making up the initial junction
     * @return The conjunction
     */
    public static Conjunction and(List<Criterion> predicates) {
        return conjunction(predicates.toArray(new Criterion[predicates.size()]));
    }

    /**
     * <p>Return the conjuction of multiple expressions</p>
     *
     * @param predicates The predicates making up the initial junction
     * @return The conjunction
     */
    public static Conjunction and(Criterion... predicates) {
        return conjunction(predicates);
    }

    /**
     * <p>Return the disjuction of two expressions</p>
     *
     * @param lhs One expression
     * @param rhs The other expression
     * @return The Criterion
     */
    public static LogicalExpression or(Criterion lhs, Criterion rhs) {
        return new LogicalExpression(lhs, rhs, "OR");
    }

    /**
     * <p>Return the disjuction of multiple expressions</p>
     *
     * @param predicates The predicates making up the initial junction
     * @return The conjunction
     */
    public static Disjunction or(List<Criterion> predicates) {
        return disjunction(predicates.toArray(new Criterion[predicates.size()]));
    }

    /**
     * <p>Return the disjuction of multiple expressions</p>
     *
     * @param predicates The predicates making up the initial junction
     * @return The conjunction
     */
    public static Disjunction or(Criterion... predicates) {
        return disjunction(predicates);
    }

    /**
     * <p>Group expressions together in a single conjunction (A and B and C...).</p>
     * <p>This form creates an empty conjunction.  See {@link Conjunction#add(Criterion)}</p>
     *
     * @return Conjunction
     */
    public static Conjunction conjunction() {
        return new Conjunction();
    }

    /**
     * <p>Group expressions together in a single conjunction (A and B and C...).</p>
     *
     * @param conditions The initial set of conditions to put into the Conjunction
     * @return Conjunction
     */
    public static Conjunction conjunction(Criterion... conditions) {
        return new Conjunction(conditions);
    }

    /**
     * <p>Group expressions together in a single disjunction (A or B or C...).</p>
     * <p>This form creates an empty disjunction.  See {@link Disjunction#add(Criterion)}</p>
     *
     * @return Conjunction
     */
    public static Disjunction disjunction() {
        return new Disjunction();
    }

    /**
     * <p>Group expressions together in a single disjunction (A or B or C...).</p>
     *
     * @param conditions The initial set of conditions to put into the Disjunction
     * @return Conjunction
     */
    public static Disjunction disjunction(Criterion... conditions) {
        return new Disjunction(conditions);
    }

    /**
     * <p>Apply an "equals" constraint to each property in the key set of a <tt>Map</tt></p>
     *
     * @param propertyNameValues a map from property names to values
     * @return Criterion
     * @see Conjunction
     */
    @SuppressWarnings("UnusedDeclaration")
    public static Criterion allEq(Map<String, ?> propertyNameValues) {
        final Conjunction conj = conjunction();

        for (Map.Entry<String, ?> entry : propertyNameValues.entrySet()) {
            conj.add(eq(entry.getKey(), entry.getValue()));
        }
        return conj;
    }

    protected Restrictions() {
        // cannot be instantiated, but needs to be protected so Expression can extend it
    }

}
