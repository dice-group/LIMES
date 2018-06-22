package org.aksw.limes.core.measures.mapper.bags.cosine;

import java.util.HashMap;
import java.util.Map;

/**
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

    public int getSize(){
        return offset;
    }

    public Object[] reverse(){
        Object[] o = new Object[offset];

        for(Map.Entry<T, Integer> e: cache.entrySet()){
            o[e.getValue()] = e.getKey();
        }

        return o;
    }
}
