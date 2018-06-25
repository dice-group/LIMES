package org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster;

import com.google.common.collect.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class APState {

    private IterationId currentId = new IterationId(0);

    private Table<String, String, Double> similarities;
    private Map<IterationId, Map<String, Map<String, Double>>> A = new WeakHashMap<>();
    private Map<IterationId, Map<String, Map<String, Double>>> R = new WeakHashMap<>();

    public APState(Table<String, String, Double> similarities) {
        this.similarities = similarities;
    }

    public APStateView newPhase(){

        IterationId next = new IterationId(currentId.i + 1);

        A.put(next, new HashMap<>());
        R.put(next, new HashMap<>());

        APStateView view =  new APStateView(this, next, currentId);
        currentId = next;
        return view;
    }

    Double readA(IterationId id, String s1, String s2){

        Map<String, Map<String, Double>> map = A.getOrDefault(id, new HashMap<>());

        if(map.containsKey(s1)){
            Map<String, Double> base = map.get(s1);
            return base.getOrDefault(s2, 0.0);
        }

        return null;
    }

    Double readR(IterationId id, String s1, String s2){

        Map<String, Map<String, Double>> map = R.getOrDefault(id, new HashMap<>());

        if(map.containsKey(s1)){
            Map<String, Double> base = map.get(s1);
            return base.getOrDefault(s2, 0.0);
        }

        return null;
    }

    void putA(IterationId id, String s1, String s2, double d) {

        Map<String, Map<String, Double>> map = A.get(id);

        map.putIfAbsent(s1, new HashMap<>());
        Map<String, Double> base = map.get(s1);
        base.put(s2, d);
    }

    void putR(IterationId id, String s1, String s2, double d) {

        Map<String, Map<String, Double>> map = R.get(id);

        map.putIfAbsent(s1, new HashMap<>());
        Map<String, Double> base = map.get(s1);
        base.put(s2, d);
    }

    Map<String, Double> getNeighbours(String s){
        return similarities.rowMap().get(s);
    }

    public Map<String, Map<String, Double>> getCurrentA(){
        return A.get(currentId);
    }

    public Map<String, Map<String, Double>> getCurrentR(){
        return R.get(currentId);
    }

    class IterationId{

        int i = 0;

        public IterationId(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IterationId that = (IterationId) o;
            return i == that.i;
        }

        @Override
        public int hashCode() {

            return Objects.hash(i);
        }


    }
}
