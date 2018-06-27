package org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.aksw.limes.core.io.mapping.AMapping;
import org.apache.jena.base.Sys;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class
 *
 * @author Cedric Richter
 */

public class AffinityPropagation {

    private static final int THREAD_COUNT = 8;

    private List<SimilarityFilter> filters;
    private Table<String, String, Double> similarities = HashBasedTable.create();
    private APState state;

    private double accumulatedError;

    private ExecutorService service;
    private Set<Integer> openIds;

    public AffinityPropagation(List<SimilarityFilter> filters, Set<String> items) {
        this.filters = filters;
        this.initSimilarities(items);
        state = new APState(similarities);
        service = Executors.newFixedThreadPool(THREAD_COUNT);
    }

    private Table<String, String, Double> fromMapping(AMapping mapping, double threshold){
        Table<String, String, Double> ret = HashBasedTable.create();

        for(Map.Entry<String, HashMap<String, Double>> e: mapping.getMap().entrySet()){
            for(Map.Entry<String, Double> t: e.getValue().entrySet()){
                if(e.getKey().equals(t.getKey()))continue;
                double distance = 1 - (t.getValue() - threshold)/(1 - threshold);
                ret.put(e.getKey(), t.getKey(),
                        distance*distance);

            }
        }

        return ret;
    }

    private Table<String, String, Double> eucMerge(AMapping mapping, Table<String, String, Double> base, double threshold){
        Table<String, String, Double> ret = HashBasedTable.create();

        for(Map.Entry<String, HashMap<String, Double>> e: mapping.getMap().entrySet()){
            for(Map.Entry<String, Double> t: e.getValue().entrySet()){
                if(e.getKey().equals(t.getKey()))continue;

                double baseVal = base.contains(e.getKey(), t.getKey())?base.get(e.getKey(), t.getKey()):0.0;

                double distance = 1 - (t.getValue() - threshold)/(1 - threshold);
                ret.put(e.getKey(), t.getKey(),
                        baseVal + distance*distance);

            }
        }

        return ret;
    }

    private void putSim(String s1, String s2, double sim){
        if(similarities.contains(s1, s1)){
            similarities.put(s1, s1, Math.min(similarities.get(s1, s1), sim));
        }else{
            similarities.put(s1, s1, sim);
        }
        if(similarities.contains(s2, s2)){
            similarities.put(s2, s2, Math.min(similarities.get(s2, s2), sim));
        }else{
            similarities.put(s2, s2, sim);
        }
        similarities.put(s1, s2, sim);
    }


    private void initSimilarities(Set<String> set){

        MappingPlanner planner = new MappingPlanner(set);

        Table<String, String, Double> ret = null;

        for(SimilarityFilter filter: filters){

            AMapping mapping = planner.execute(filter);

            if(ret == null){
                ret = fromMapping(mapping, filter.getThreshold());
            }else{
                ret = eucMerge(mapping, ret, filter.getThreshold());
            }

        }

        for(Table.Cell<String, String, Double> cell: ret.cellSet()){
            putSim(cell.getRowKey(), cell.getColumnKey(),(-1)*Math.sqrt(cell.getValue()));
        }


    }

    public double iterate(){

        APStateView view = state.newPhase();
        accumulatedError = 0;

        openIds = new HashSet<>();
        int id = 0;
        int steps = 0;

        openIds.add(id);
        new APExecutor(this, id, similarities.rowKeySet(), view).run();

        /**
        Set<String> rows = new HashSet<>(chunk);
        for(String key: similarities.rowKeySet()){
            rows.add(key);
            steps ++;

            if(steps >= chunk){
                openIds.add(id);
                new APExecutor(this, id++, rows, view).run();
                steps = 0;
                rows = new HashSet<>(chunk);
            }
        }

        if(steps > 0) {
            openIds.add(id);
            new APExecutor(this, id++, rows, view).run();
        }
        */

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
