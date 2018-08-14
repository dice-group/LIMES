package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import java.util.ArrayList;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.filters.ASemanticFilter;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.filters.SemanticFilterFactory;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.LeastCommonSubsumerFinder;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;

public class WuPalmerMeasure extends AEdgeCountingSemanticMeasure {
    double maxValue = 1;

    public WuPalmerMeasure(double threshold, boolean preindex, boolean filtering) {
        super(threshold, preindex, filtering);
    }

    public double calculate(ArrayList<ArrayList<ISynsetID>> synset1Tree, ArrayList<ArrayList<ISynsetID>> synset2Tree) {
        double sim = 0;

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

        sim = (double) (2.0 * (double) depth) / (double) (length + (2.0 * depth));

        return sim;
    }

    @Override
    public double getSimilaritySimple(ISynset synset1, ISynset synset2) {
        return getSimilarityComplex(synset1, synset2);
    }

    @Override
    public double getSimilarityComplex(ISynset synset1, ISynset synset2) {
        double sim = 0.0d;
        ArrayList<ArrayList<ISynsetID>> paths1 = getPaths(synset1);
        ArrayList<ArrayList<ISynsetID>> paths2 = getPaths(synset2);

        long b = System.currentTimeMillis();
        sim = getSimilarity(synset1, paths1, synset2, paths2);
        long e = System.currentTimeMillis();
        // called multiple times
        runtimes.getSynsetSimilarity += e - b;
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

        if (synset1.getOffset() == synset2.getOffset()) {
            return maxValue;
        }

        return calculate(synset1Tree, synset2Tree);
    }

    @Override
    public boolean filter(ArrayList<Integer> parameters) {
        if (parameters == null)
            return true;
        if (parameters.isEmpty())
            return true;
        ASemanticFilter filter = SemanticFilterFactory.createFilter(SemanticFilterFactory.getFilterType("wupalmer"),
                theta, parameters.get(4));
        boolean flag = filter.filter(parameters);
        return flag;
    }

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

}
