package org.aksw.limes.core.measures.mapper.bags.jaccard;

import com.google.common.collect.Multiset;

import java.util.Objects;

/**
 * The class implements the idea of assigning Keys to a Bag.
 * Works on the idea simialar as bags but compares keys.
 *
 * @param <T>
 * It takes a MultiSet as an parameter
 *
 * @author Cedric Richter
 *
 */


public class KeyedBag<T> {

    private String key;
    private Multiset<T> bag;


    /**
     * @return Assigns a key to a bag
     */
    public KeyedBag(String key, Multiset<T> bag) {
        this.key = key;
        this.bag = bag;
    }

    /**
     * @return Key
     */
    public String getKey() {
        return key;
    }


    /**
     * @return Bag
     */
    public Multiset<T> getBag() {
        return bag;
    }



    /**
     * @return Object that equals a key and a bag assigned to a key
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyedBag<?> keyedBag = (KeyedBag<?>) o;
        return Objects.equals(key, keyedBag.key);
    }

    /**
     * @return a HashCode Object
     */
    @Override
    public int hashCode() {

        return Objects.hash(key);
    }

}
