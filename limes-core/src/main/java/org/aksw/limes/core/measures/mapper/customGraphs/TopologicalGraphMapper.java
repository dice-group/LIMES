package org.aksw.limes.core.measures.mapper.customGraphs;

import org.aksw.limes.core.measures.mapper.bags.IBagMapper;
import org.aksw.limes.core.measures.mapper.bags.jaccard.JaccardBagMapper;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.IGraphRelabel;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.impl.ConstantRelabel;

public class TopologicalGraphMapper extends ConfigurableGraphMapper {
    public TopologicalGraphMapper(int descriptionRecursionDepth, int wlIterationDepth) {
        super(descriptionRecursionDepth, wlIterationDepth, new ConstantRelabel(), new JaccardBagMapper());
    }

    public TopologicalGraphMapper() {
        super(3, 2, new ConstantRelabel(), new JaccardBagMapper());
    }
}
