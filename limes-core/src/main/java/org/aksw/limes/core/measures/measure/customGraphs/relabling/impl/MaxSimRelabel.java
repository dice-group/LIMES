package org.aksw.limes.core.measures.measure.customGraphs.relabling.impl;

import org.aksw.limes.core.measures.measure.customGraphs.relabling.IGraphRelabel;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.ILabel;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.ILabelCollector;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Cedric Richter
 */
public class MaxSimRelabel implements IGraphRelabel {

    private List<SimDefinition> definitions;
    private Set<String> labels = new HashSet<>();

    public MaxSimRelabel(List<SimDefinition> definitions) {
        this.definitions = definitions;
    }

    @Override
    public ILabelCollector getPriorLabelCollector() {
        return new ILabelCollector() {
            @Override
            public Consumer<ILabel> getSourceLabelConsumer() {
                return (x -> {if(x.getType()==ILabel.LabelType.NODE)labels.add(x.getContent());});
            }
        };
    }

    private Set<String> dominates(Vector<Double> vec, Map<String, Vector<Double>> compare){
        Set<String> dominated = new HashSet<>();

        for(Map.Entry<String, Vector<Double>> e: compare.entrySet()){
            boolean isDominated = true;

            Vector<Double> partner = e.getValue();

            for(int i = 0; i < vec.size() && i < partner.size() && isDominated; i++){
                isDominated &= vec.get(i) >= partner.get(i);
            }

            if(isDominated)
                dominated.add(e.getKey());
        }

        return dominated;
    }

    private void paretoAdd(String s, Vector<Double> vec, Map<String, Vector<Double>> compare){
        for(String del: dominates(vec, compare))
            compare.remove(del);
        compare.put(s, vec);
    }

    @Override
    public String relabel(ILabel label) {
        if(label.getType() == ILabel.LabelType.EDGE)
            return null;

        Map<String, Vector<Double>> candidates = new HashMap<>();

        for(String comp: labels) {
            Vector<Double> v = new Vector<>(definitions.size());
            boolean save = false;

            for(SimDefinition definition: definitions){
                double sim = definition.getMeasure().getSimilarity(label.getContent(), comp);
                v.add(sim);
                save |= sim >= definition.getThreshold();
            }

            if(save)
                paretoAdd(comp, v, candidates);
        }

        String out = null;
        double max_vol = 0;

        for(Map.Entry<String, Vector<Double>> candidate: candidates.entrySet()){
            double vol = candidate.getValue().stream().reduce((x, y)->x+y).get();

            if(vol > max_vol) {
                out = candidate.getKey();
                max_vol = vol;
            }

        }

        return out;
    }


}
