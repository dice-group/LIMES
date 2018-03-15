package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import java.util.List;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.LeastCommonSubsumer;

import edu.mit.jwi.item.ISynset;

public class WuPalmerMeasure extends AEdgeCountingSemanticMeasure {

    public WuPalmerMeasure() {
        super();
    }
    double maxValue = 1;

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;

    }

    @Override
    public String getName() {
        return "wupalmer";
    }

    @Override
    public String getType() {
        return "semantic";
    }

    @Override
    public double getSimilarity(ISynset synset1, List<List<ISynset>> synset1Tree, ISynset synset2,
            List<List<ISynset>> synset2Tree) {
        if (synset1.getType() != synset2.getType())
            return 0;

        if (synset1.getOffset() == synset2.getOffset()) {
            return maxValue;
        }

        LeastCommonSubsumer lcs = this.getLeastCommonSubsumer(synset1, synset1Tree, synset2, synset2Tree);

        double sim = 0;
        sim = (double) (2.0 * (double) lcs.getDepth())
                / (double) (lcs.getSynsetsDistance() + (2.0 * lcs.getDepth()));
        return sim;
    }

}
