package org.aksw.limes.core.measures.mapper.customGraphs;

import java.util.Objects;

public class Pair<S, T> {

    private S key;
    private T value;

    public Pair(S key, T value) {
        this.key = key;
        this.value = value;
    }

    public S getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(key, pair.key) &&
                Objects.equals(value, pair.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "<"+key.toString()+", "+ value.toString()+">";
    }
}
