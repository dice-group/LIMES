package org.aksw.limes.core.measures.measure.customGraphs.relabling.impl;

import org.aksw.limes.core.measures.measure.customGraphs.relabling.IGraphRelabel;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.ILabel;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.ILabelCollector;
import org.aksw.limes.core.measures.measure.graphs.gouping.indexing.JaccardIndex;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Cedric Richter
 */
public class JaccardIndexRelabel implements IGraphRelabel {

    private JaccardIndex index;
    private double threshold;

    public JaccardIndexRelabel(int n, double threshold){
        this.index = new JaccardIndex(n);
        this.threshold = threshold;
    }

    public JaccardIndexRelabel(double threshold){
        this(3, threshold);
    }

    @Override
    public ILabelCollector getPriorLabelCollector() {
        return new ILabelCollector() {
            @Override
            public Consumer<ILabel> getSourceLabelConsumer() {
                return (x -> {if(x.getType()==ILabel.LabelType.NODE)index.index(x.getContent());});
            }
        };
    }

    @Override
    public String relabel(ILabel label) {
        if(label.getType() == ILabel.LabelType.EDGE)
            return null;

        String l = label.getContent();
        Map<String, Double> nearest = index.getNearestStrings(l, threshold);

        double max = Double.MIN_VALUE;
        String max_label = l;

        for(Map.Entry<String, Double> e: nearest.entrySet()){
            if(e.getValue() > max){
                max = e.getValue();
                max_label = e.getKey();
            }
        }
        return max_label;
    }
}
