package org.aksw.limes.core.measures.mapper.customGraphs;

import org.aksw.limes.core.measures.mapper.bags.jaccard.JaccardBagMapper;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.impl.ConstantRelabel;

/**
 * Class for a pre-config graph mapper and contains the base logic of WSL-Kernel Graph Similarity
 * and abstracts all resources and literals.
 *
 * Consequently, it only compares based on the topology for given graphs.
 *
 * @author Cedric Richter
 */
public class TopologicalGraphMapper extends ConfigurableGraphMapper {
    public TopologicalGraphMapper(int descriptionRecursionDepth, int wlIterationDepth) {
        super(descriptionRecursionDepth, wlIterationDepth, new ConstantRelabel(), new JaccardBagMapper());
    }

    public TopologicalGraphMapper() {
        super(3, 2, new ConstantRelabel(), new JaccardBagMapper());
    }
}
