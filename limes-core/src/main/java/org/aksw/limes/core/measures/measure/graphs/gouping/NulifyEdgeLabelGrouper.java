package org.aksw.limes.core.measures.measure.graphs.gouping;

import org.apache.jena.graph.Node;

public class NulifyEdgeLabelGrouper implements IEdgeLabelGrouper {
    @Override
    public String group(Node node) {
        return null;
    }

    @Override
    public void endGrouping() {

    }
}
