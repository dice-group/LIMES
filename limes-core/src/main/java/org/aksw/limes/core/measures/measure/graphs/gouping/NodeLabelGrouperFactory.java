package org.aksw.limes.core.measures.measure.graphs.gouping;

public class NodeLabelGrouperFactory {

    public INodeLabelGrouper create(){
        return new ExactNodeLabelGrouper();
    }

}
