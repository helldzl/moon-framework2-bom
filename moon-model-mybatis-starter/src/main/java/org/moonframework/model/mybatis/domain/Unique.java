package org.moonframework.model.mybatis.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Unique {

    private Set<Pair> pairs = new HashSet<>();

    public boolean add(Pair pair) {
        return pairs.add(pair);
    }

    public boolean add(String key, Long value) {
        return pairs.add(new Pair(key, value));
    }

    @Override
    public int hashCode() {
        return pairs.stream().map(Pair::hashCode).reduce(0, (left, right) -> left.hashCode() ^ right.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || ((obj instanceof Unique && this.getPairs().equals(((Unique) obj).getPairs())));
    }

    public Set<Pair> getPairs() {
        return pairs;
    }

    public void forEach(Consumer<? super Pair> action) {
        pairs.forEach(action);
    }

    public static void main(String[] args) {
        Unique unique = new Unique();
        unique.add("topic", 1L);
        unique.add("folder", 2L);

        Unique unique2 = new Unique();
        unique2.add("topic", 1L);
        unique2.add("folder", 2L);

        Set<Unique> set = new HashSet<>();
        set.add(unique);
        set.add(unique2);

        System.out.println(unique.hashCode());
    }

}
