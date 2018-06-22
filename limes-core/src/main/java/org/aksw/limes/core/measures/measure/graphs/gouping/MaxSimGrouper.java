package org.aksw.limes.core.measures.measure.graphs.gouping;

import org.aksw.limes.core.measures.measure.graphs.gouping.labels.SimDefinition;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Literal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MaxSimGrouper implements IDependendNodeLabelGrouper {

    private Set<String> labels;
    private List<SimDefinition> definitions;
    private INodeLabelGrouper defaultStrategy;

    public MaxSimGrouper(List<SimDefinition> definitions, INodeLabelGrouper defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
        this.definitions = definitions;
    }



    @Override
    public String group(Node n) {
        if(n instanceof Node_Literal && labels!=null){
            String label = n.getLiteral().toString();

            for(SimDefinition definition: definitions){
                String maxLabel = null;
                double maxScore = 0;
                for(String compareLabel: labels){
                    try {
                        double score = definition.getSimilarity().score(compareLabel, label);
                        if(score >= definition.getThreshold() && score > maxScore) {
                            maxLabel = compareLabel;
                            maxScore = score;
                        }
                    }catch(Exception e){}
                }
                if(maxLabel != null)
                    return maxLabel;
            }

        }
        return defaultStrategy.group(n);
    }

    @Override
    public void endGrouping() {
        defaultStrategy.endGrouping();
    }

    private void saveString(String s){
        if(labels == null)
            labels = new HashSet<>();
        labels.add(s);
    }

    @Override
    public Consumer<String> getDependLabelConsumer() {
        return (x -> saveString(x));
    }
}
