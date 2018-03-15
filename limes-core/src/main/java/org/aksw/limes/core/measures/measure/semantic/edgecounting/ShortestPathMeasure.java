package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import java.util.List;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.LeastCommonSubsumer;

import edu.mit.jwi.item.ISynset;

public class ShortestPathMeasure extends AEdgeCountingSemanticMeasure {

    public ShortestPathMeasure() {
        super();
    }

    double maxValue = 1;

    @Override
    public double getSimilarity(ISynset synset1, List<List<ISynset>> synset1Tree, ISynset synset2,
            List<List<ISynset>> synset2Tree) {

        if (synset1.getType() != synset2.getType())
            return 0;

        if (synset1.getOffset() == synset2.getOffset()) {
            return maxValue;
        }

        LeastCommonSubsumer lcs = this.getLeastCommonSubsumer(synset1, synset1Tree, synset2, synset2Tree);

        double D = (double) getHierarchyDepth(lcs.getCommonSynset().getType());
        double sim = (double) (2.0 * D) - (double) (lcs.getSynsetsDistance());
        // normalize
        sim /= (double) (2.0 * D);
        return sim;
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;

    }

    @Override
    public String getName() {
        return "shortestPath";
    }

    @Override
    public String getType() {
        return "semantic";
    }

}
