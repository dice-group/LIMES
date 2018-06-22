package org.aksw.limes.core.measures.measure.graphs.gouping;

import java.util.function.Consumer;

public interface ICollectingNodeLabelGrouper extends INodeLabelGrouper {

    public void collectOn(Consumer<String> consumer);

}
