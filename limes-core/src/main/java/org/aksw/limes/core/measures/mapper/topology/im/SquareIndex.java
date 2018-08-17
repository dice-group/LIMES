package org.aksw.limes.core.measures.mapper.topology.im;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SquareIndex {

    public HashMap<Integer, HashMap<Integer, List<MBBIndex>>> map = new HashMap<>();

    public SquareIndex() {

    }

    public SquareIndex(int capacity) {
        this.map = new HashMap<>(capacity);
    }

    public void add(int i, int j, MBBIndex m) {
        if (!map.containsKey(i)) {
            map.put(i, new HashMap<>());
        }
        if (!map.get(i).containsKey(j)) {
            map.get(i).put(j, new ArrayList<>());
        }
        map.get(i).get(j).add(m);
    }

    public List<MBBIndex> getSquare(int i, int j) {
        if (!map.containsKey(i) || !map.get(i).containsKey(j))
            return null;
        else
            return map.get(i).get(j);
    }
}
