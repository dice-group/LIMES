package org.aksw.limes.core.measures.measure.graphs.gouping;

import org.aksw.limes.core.measures.measure.graphs.gouping.indexing.JaccardIndex;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Literal;

import java.util.Map;
import java.util.Set;

public class StringSimilarityGrouper implements IDependendNodeLabelGrouper {

    private JaccardIndex index;
    private double threshold;
    private INodeLabelGrouper defaultStrategy;

    public StringSimilarityGrouper(double threshold, INodeLabelGrouper defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
        this.threshold = threshold;
    }

    @Override
    public void injectLabels(Set<String> labels) {
        index = new JaccardIndex(3, labels);
    }

    @Override
    public String group(Node n) {
        if(n instanceof Node_Literal && index!=null){
            String label = n.getLiteral().toString();
            Map<String, Double> nearest = index.getNearestStrings(label, this.threshold);

            double max = Double.MIN_VALUE;
            String max_label = label;

            for(Map.Entry<String, Double> e: nearest.entrySet()){
                if(e.getValue() > max){
                    max = e.getValue();
                    max_label = e.getKey();
                }
            }
            return max_label;
        }
        return defaultStrategy.group(n);
    }

    @Override
    public void endGrouping() {
    }

}
