/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.bags.jaccard;

import com.google.common.collect.Multiset;
import org.aksw.limes.core.measures.mapper.string.fastngram.ITokenizer;
import org.aksw.limes.core.measures.mapper.string.fastngram.NGramTokenizer;

import java.util.*;

/**
 *
 * @param <T>
 * @author  Cedric Richter
 */
public class Index<T> {

    private Map<Integer, Map<T, Set<KeyedBag<T>>>> sizeObjectIndex;

    public Index() {
        sizeObjectIndex = new HashMap<>();
    }

    public void addBag(KeyedBag<T> bag){
        int size = bag.getBag().size();
        sizeObjectIndex.putIfAbsent(size, new HashMap<>());

        Map<T, Set<KeyedBag<T>>> setIndex = sizeObjectIndex.get(size);
        for(T obj: bag.getBag().elementSet()) {
            setIndex.putIfAbsent(obj, new HashSet<>());
            setIndex.get(obj).add(bag);
        }
    }

    public Set<KeyedBag<T>> getBags(int size, T obj){
        Set<KeyedBag<T>> output = new HashSet<>();
        if(sizeObjectIndex.containsKey(size)) {
            Map<T, Set<KeyedBag<T>>> setIndex = sizeObjectIndex.get(size);
            Set<KeyedBag<T>> bags = setIndex.getOrDefault(obj, new HashSet<>());
            output.addAll(bags);
        }
        return output;
    }

    public Map<T, Set<KeyedBag<T>>> getBags(int size){
        return sizeObjectIndex.getOrDefault(size, new HashMap<>());
    }


    public Set<Integer> getAllSizes(){
        return sizeObjectIndex.keySet();
    }



}
