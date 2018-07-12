package org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class based on Affinity Propagation (AP) [1].
 *
 * AP as a clustering algorithm has the advantage to be define with absence of similarity.
 * Therefore, it can be computed very fast for sparse similarity matrices.
 *
 * [1] Brendan J. Frey; Delbert Dueck (2007). "Clustering by passing messages between data points"
 *
 * @author Cedric Richter
 */

public class AffinityPropagation {

    private Table<String, String, Double> similarities = HashBasedTable.create();
    private APState state;

    private double zero = 0.0;

    private double accumulatedError;

    private Set<Integer> openIds;

    public AffinityPropagation(Table<String, String, Double> similarities) {
        this.similarities = similarities;
        initSelfSimilarities();
        state = new APState(similarities);
    }


    private void initSelfSimilarities(){
        for(String s: new HashSet<>(similarities.rowKeySet())){
            double min = Double.POSITIVE_INFINITY;

            for(Double d: similarities.column(s).values())
                min = Math.min(min, d);

            min = min == Double.POSITIVE_INFINITY?0.0:min;

            similarities.put(s, s, min);
            zero = Math.min(zero, min);
        }
    }


    public double iterate(){

        APStateView view = state.newPhase();
        accumulatedError = 0;

        openIds = new HashSet<>();
        int id = 0;

        openIds.add(id);
        new APExecutor(this, id, similarities.rowKeySet(), view).run();

        synchronized (this) {
            while (!openIds.isEmpty()) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return accumulatedError;
    }

    public void stats(){
        System.out.println("A:");
        for(Map.Entry<String, Map<String, Double>> e: state.getCurrentA().entrySet()){
            for(Map.Entry<String, Double> t: e.getValue().entrySet()){
                if(t.getValue() != 0){
                    System.out.println(e.getKey()+":"+t.getKey()+" = "+t.getValue());
                }
            }
        }
        System.out.println("R:");
        for(Map.Entry<String, Map<String, Double>> e: state.getCurrentR().entrySet()){
            for(Map.Entry<String, Double> t: e.getValue().entrySet()){
                if(t.getValue() != 0){
                    System.out.println(e.getKey()+":"+t.getKey()+" = "+t.getValue());
                }
            }
        }
    }

    public Map<String, String> cluster(){
        Map<String, String> out = new HashMap<>();

        for(String s: similarities.rowKeySet()){
            double max = Double.NEGATIVE_INFINITY;
            String avail = null;

            Map<String, Double> A = state.getCurrentA().get(s);
            Map<String, Double> R = state.getCurrentR().get(s);

            for(String j: A.keySet()){
                double val = A.get(j) + R.getOrDefault(j, Double.NEGATIVE_INFINITY);
                if(val > max){
                    avail = j;
                    max = val;
                }
            }

            out.put(s, avail);

        }

        return out;

    }


    synchronized void finish(int id, double accError, boolean error){
        accumulatedError += accError;
        openIds.remove(id);

        this.notifyAll();
    }




}
