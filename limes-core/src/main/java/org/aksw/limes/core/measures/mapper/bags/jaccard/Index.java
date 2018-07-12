/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.bags.jaccard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Create the indexes of JaccardBagMapper
 *
 * The Jaccard index, will be a good index to identify mirror sites,
 * but not so great at catching copy pasta plagiarism
 *
 * @param <T>
 * @author  Cedric Richter
 */
public class Index<T> {

    private Map<Integer, Map<T, Set<KeyedBag<T>>>> sizeObjectIndex;

    public Index() {
        sizeObjectIndex = new HashMap<>();
    }

    /**
     * @return Add bags to the HashMap
     */
    public void addBag(KeyedBag<T> bag){
        int size = bag.getBag().size();
        sizeObjectIndex.putIfAbsent(size, new HashMap<>());

        Map<T, Set<KeyedBag<T>>> setIndex = sizeObjectIndex.get(size);
        for(T obj: bag.getBag().elementSet()) {
            setIndex.putIfAbsent(obj, new HashSet<>());
            setIndex.get(obj).add(bag);
        }
    }

    /**
     * @return All Bags in the HashMap
     */
    public Set<KeyedBag<T>> getBags(int size, T obj){
        Set<KeyedBag<T>> output = new HashSet<>();
        if(sizeObjectIndex.containsKey(size)) {
            Map<T, Set<KeyedBag<T>>> setIndex = sizeObjectIndex.get(size);
            Set<KeyedBag<T>> bags = setIndex.getOrDefault(obj, new HashSet<>());
            output.addAll(bags);
        }
        return output;
    }
    /**
     * @return Size of HashMap
     */
    public Map<T, Set<KeyedBag<T>>> getBags(int size){
        return sizeObjectIndex.getOrDefault(size, new HashMap<>());
    }

    /**
     * @return Size of ObjectIndexes
     */
    public Set<Integer> getAllSizes(){
        return sizeObjectIndex.keySet();
    }



}
