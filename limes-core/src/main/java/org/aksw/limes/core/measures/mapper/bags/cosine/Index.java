package org.aksw.limes.core.measures.mapper.bags.cosine;

import java.util.HashMap;
import java.util.Map;

/**
 * Create the indexes of CosineBagMapper, works on the basic idea of each instance assigned to new identifier
 *
 * @author Cedric Richter
 */
public class Index<T> {

    private int offset = 0;
    private Map<T, Integer> cache = new HashMap<>();


    public int index(T obj){
        if(!cache.containsKey(obj)){
            cache.put(obj, offset++);
        }
        return cache.get(obj);
    }

    /**
     * @return size of index
     */
    public int getSize(){
        return offset;
    }

    /**
     * maps each index obj to index
     * @return an array O with keys and values
     */
    public Object[] reverse(){
        Object[] o = new Object[offset];

        for(Map.Entry<T, Integer> e: cache.entrySet()){
            o[e.getValue()] = e.getKey();
        }

        return o;
    }
}
