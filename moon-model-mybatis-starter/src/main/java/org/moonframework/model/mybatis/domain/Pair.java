package org.moonframework.model.mybatis.domain;

import org.moonframework.core.support.Builder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/5
 */
public class Pair {

    public static PairBuilder builder() {
        return new PairBuilder();
    }

    public static class PairBuilder implements Builder<List<Pair>> {

        private List<Pair> pairs = new ArrayList<>();

        public PairBuilder add(String key, Object value) {
            pairs.add(new Pair(key, value));
            return this;
        }

        @Override
        public List<Pair> build() {
            return pairs;
        }
    }

    private String key;
    private Object value;

    public Pair(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int hashCode() {
        return key.hashCode() ^ value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                (obj instanceof Pair && (
                        (((Pair) obj).getKey().equals(this.getKey())) &&
                                (((Pair) obj).getValue().equals(this.getValue()))
                ));
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
