package org.aksw.limes.core.measures.measure.semantic.edgecounting.measures;

import java.util.ArrayList;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.AEdgeCountingSemanticMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.AIndex;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.LeastCommonSubsumerFinder;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;

public class LiMeasure extends AEdgeCountingSemanticMeasure {
    double a = 0.2;
    double b = 0.6;

    public LiMeasure( AIndex Indexer) {
        super(Indexer);
    }

    public double calculate(ArrayList<ArrayList<ISynsetID>> synset1Tree, ArrayList<ArrayList<ISynsetID>> synset2Tree) {

        LeastCommonSubsumerFinder finder = new LeastCommonSubsumerFinder();
        finder.getLeastCommonSubsumer(synset1Tree, synset2Tree);

        double depth = (double) finder.getDepth();
        // problem with finding lsc
        if (depth == -1) {
            return 0.0d;
        }
        
        double length = (double) finder.getSynsetsDistance();
        // problem with finding lsc
        if (length == -1) {
            return 0.0d;
        }

        double s1 = Math.pow(Math.E, -(a * length));
        double s2 = Math.pow(Math.E, (b * depth));
        double s3 = Math.pow(Math.E, -(b * depth));

        double sim = s1 * ((s2 - s3) / (s2 + s3));

        return sim;
    }


    @Override
    public double getSim(ISynset synset1, ISynset synset2) {
        double sim = 0.0d;
        ArrayList<ArrayList<ISynsetID>> paths1 = getPaths(synset1);
        ArrayList<ArrayList<ISynsetID>> paths2 = getPaths(synset2);

        sim = getSimilarity(synset1, paths1, synset2, paths2);
        return sim;
    }

    public double getSimilarity(ISynset synset1, ArrayList<ArrayList<ISynsetID>> synset1Tree, ISynset synset2,
            ArrayList<ArrayList<ISynsetID>> synset2Tree) {

        if (synset1Tree.isEmpty() == true || synset2Tree.isEmpty() == true) {
            return 0;
        }
        if (synset1.getType() != synset2.getType()) {
            return 0;
        }

        return calculate(synset1Tree, synset2Tree);

    }


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
}
