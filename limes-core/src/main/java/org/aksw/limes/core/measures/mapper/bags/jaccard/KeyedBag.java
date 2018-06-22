package org.aksw.limes.core.measures.mapper.bags.jaccard;

import com.google.common.collect.Multiset;

import java.util.Objects;

/**
 * @author Cedric Richter
 * @param <T>
 */
public class KeyedBag<T> {

    private String key;
    private Multiset<T> bag;

    public KeyedBag(String key, Multiset<T> bag) {
        this.key = key;
        this.bag = bag;
    }

    public String getKey() {
        return key;
    }

    public Multiset<T> getBag() {
        return bag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyedBag<?> keyedBag = (KeyedBag<?>) o;
        return Objects.equals(key, keyedBag.key);
    }

    @Override
    public int hashCode() {

        return Objects.hash(key);
    }

}
