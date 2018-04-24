package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import java.util.List;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.ShortestPathFinder;

import edu.mit.jwi.item.ISynset;

public class LCHMeasure extends AEdgeCountingSemanticMeasure {

    public LCHMeasure() {
        super();
    }

    double maxValue = 1;

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;

    }

    @Override
    public String getName() {
        return "lch";
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
        if (synset1.getOffset() == synset2.getOffset()) {
            return maxValue;
        }

        int shortestPath = ShortestPathFinder.shortestPath(synset1Tree, synset2Tree);
        if (shortestPath == -1) {
            return 0;
        }

        double D = (double) getHierarchyDepth(synset1.getType());

        double sim = 0;
        sim = -Math.log((double) (1.0 + shortestPath) / (double) (1.0 + (2.0 * D)));
        sim /= (double) (Math.log((double) (1.0 + (2.0 * D))));

        return sim;

    }

}
