package org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster;

import java.util.Map;
import java.util.Set;

public class APExecutor implements Runnable {

    private double lambda = 0.5;

    private AffinityPropagation AP;
    private int id;
    private Set<String> keys;
    private APStateView view;

    public APExecutor(AffinityPropagation prop, int id, Set<String> keys, APStateView view) {
        this.AP = prop;
        this.id = id;
        this.keys = keys;
        this.view = view;
    }

    @Override
    public void run() {
        try {
            double accError = 0.0;

            for (String s : keys) {

                Map<String, Double> neighbours = view.getNeighbours(s);

                for (Map.Entry<String, Double> n : neighbours.entrySet()) {
                    double max = Double.NEGATIVE_INFINITY;
                    for (Map.Entry<String, Double> ni : neighbours.entrySet()) {
                        if (n.getKey().equals(ni.getKey())) continue;

                        double tmp = view.readA(s, ni.getKey()) + ni.getValue();

                        max = Math.max(tmp, max);
                    }

                    max = max == Double.NEGATIVE_INFINITY?0.0:max;

                    double r = (1 - lambda) * (n.getValue() - max) + lambda * view.readR(s, n.getKey());

                    accError += Math.pow((view.readR(s, n.getKey()) - r), 2);

                    view.putR(s, n.getKey(), r);
                }
            }

            for(String s: keys){

                Map<String, Double> neighbours = view.getNeighbours(s);

                for (Map.Entry<String, Double> n : neighbours.entrySet()) {
                    double a = 0;
                    if (n.getKey().equals(s)) {
                        for (Map.Entry<String, Double> ni : view.getNeighbours(n.getKey()).entrySet()) {
                            if (ni.getKey().equals(n.getKey())) continue;
                            a += Math.max(0, view.readR(ni.getKey(), n.getKey()));
                        }
                    } else {
                        for (Map.Entry<String, Double> ni : view.getNeighbours(n.getKey()).entrySet()) {
                            if (ni.getKey().equals(n.getKey()) || ni.getKey().equals(s)) continue;
                            a += Math.max(0, view.readR(ni.getKey(), n.getKey()));
                        }
                        a += view.readR(n.getKey(), n.getKey());
                        a = Math.max(0, a);
                    }
                    a = (1 - lambda) * a + lambda * view.readA(s, n.getKey());

                    accError += Math.pow((view.readA(s, n.getKey()) - a), 2);

                    view.putA(s, n.getKey(), a);
                }

            }

            this.AP.finish(id, accError, false);
        }catch(Exception e){
            e.printStackTrace();
            this.AP.finish(id, -1, true);
        }
    }
}
