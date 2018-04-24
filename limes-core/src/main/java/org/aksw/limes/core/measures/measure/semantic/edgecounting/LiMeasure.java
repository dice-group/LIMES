package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import java.util.List;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.LeastCommonSubsumerFinder;

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
        
        if (synset1.getType() != synset2.getType()) {
            return 0;
        }
        if (synset1Tree.isEmpty() == true || synset2Tree.isEmpty() == true) {
            return 0;
        }

        LeastCommonSubsumerFinder finder = new LeastCommonSubsumerFinder();
        finder.getLeastCommonSubsumerViaShortestPath(synset1Tree, synset2Tree);

        double depth = (double) finder.getDepth();
        if (depth == -1) {
            return 0.0d;
        }
        double length = (double) finder.getSynsetsDistance();
        if (length == -1) {
            return 0.0d;
        }
        double s1 = Math.pow(Math.E, -(a * length));
        double s2 = Math.pow(Math.E, (b * depth));
        double s3 = Math.pow(Math.E, -(b * depth));

        double sim = s1 * ((s2 - s3) / (s2 + s3));

        return sim;

    }

}
