package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import java.util.List;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.LeastCommonSubsumer;

import edu.mit.jwi.item.ISynset;

public class LiMeasure extends AEdgeCountingSemanticMeasure {

    public LiMeasure() {
        super();
    }

    double a = 0.2;
    double b = 0.6;

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }

    @Override
    public String getName() {
        return "li";
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

        LeastCommonSubsumer lcs = this.getLeastCommonSubsumer(synset1, synset1Tree, synset2, synset2Tree);

        double depth = (double) lcs.getDepth();
        double length = (double) lcs.getSynsetsDistance();

        double s1 = Math.pow(Math.E, -(a * length));
        double s2 = Math.pow(Math.E, (b * depth));
        double s3 = Math.pow(Math.E, -(b * depth));

        double sim = s1 * ((s2 - s3) / (s2 + s3));

        return sim;

    }

}
