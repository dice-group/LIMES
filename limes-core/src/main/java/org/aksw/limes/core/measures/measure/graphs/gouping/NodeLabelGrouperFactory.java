package org.aksw.limes.core.measures.measure.graphs.gouping;

public class NodeLabelGrouperFactory {

    public INodeLabelGrouper create(){
        return new StringSimilarityGrouper(0.4, new ExactNodeLabelGrouper());
    }

}
