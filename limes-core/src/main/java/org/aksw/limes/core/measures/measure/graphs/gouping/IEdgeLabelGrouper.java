package org.aksw.limes.core.measures.measure.graphs.gouping;

import org.apache.jena.graph.Node;

public interface IEdgeLabelGrouper {

    String group(Node node);

    void endGrouping();

}
