package org.aksw.limes.core.measures.measure.graphs.gouping;

import java.util.Set;

public interface IDependendNodeLabelGrouper extends INodeLabelGrouper {

    public void injectLabels(Set<String> labels);
}
