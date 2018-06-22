package org.aksw.limes.core.measures.measure.graphs.gouping;


import java.util.function.Consumer;

public interface IDependendNodeLabelGrouper extends INodeLabelGrouper {

    public Consumer<String> getDependLabelConsumer();
}
