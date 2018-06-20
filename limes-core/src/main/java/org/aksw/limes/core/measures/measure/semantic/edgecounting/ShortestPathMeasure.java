package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import java.util.List;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.preprocessing.DB.DBImplementation;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.ShortestPathFinder;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;

public class ShortestPathMeasure extends AEdgeCountingSemanticMeasure {

    public ShortestPathMeasure(DBImplementation d) {
        super(d);
    }

    double maxValue = 1;

    @Override
    public double getSimilarity(ISynset synset1, List<List<ISynsetID>> synset1Tree, ISynset synset2,
            List<List<ISynsetID>> synset2Tree) {
        
        if (synset1Tree.isEmpty() == true || synset2Tree.isEmpty() == true) {
            return 0;
        }
        if (synset1.getType() != synset2.getType()) {
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
        double sim = (double) (2.0 * D) - (double) (shortestPath);
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
