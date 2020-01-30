package org.aksw.limes.core.measures.measure.semantic.edgecounting.measures;

import java.util.ArrayList;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.AEdgeCountingSemanticMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.finders.LeastCommonSubsumerFinder;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.AIndex;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;

/**
 * Implements the Wu and Palmer (WuPalmer) semantic string similarity between
 * two concepts (synsets), using the path between two concepts and their least
 * common subsumer.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class WuPalmerMeasure extends AEdgeCountingSemanticMeasure {
    double maxValue = 1;

    public WuPalmerMeasure(AIndex Indexer) {
        super(Indexer);
    }

    /**
     * Calculates the actual WuPalmer similarity.
     * 
     * @param synset1Tree,
     *            the set of all hypernym paths for a concept synset1
     * @param synset2Tree,
     *            the set of all hypernym paths for a concept synset2
     * @return the LI similarity between two concepts
     */
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

    /**
     * Computes the WuPalmer similarity between two concepts. To do so, it retrieves
     * all possible hypernym paths for the two concepts, finds their least
     * common subsumer concept, calculates the length of the shortest path
     * between the concepts which passes via their least common subsumer.
     * 
     * @param synset1,
     *            the first input synset
     * @param synset2,
     *            the second input synset
     * @return the LCH similarity between synset1 and synset2
     */
    @Override
    public double getSimilarityBetweenConcepts(ISynset synset1, ISynset synset2) {
        ArrayList<ArrayList<ISynsetID>> paths1 = getPaths(synset1);
        ArrayList<ArrayList<ISynsetID>> paths2 = getPaths(synset2);

        if (paths1.isEmpty() == true || paths2.isEmpty() == true) {
            return 0;
        }
        if (synset1.getType() != synset2.getType()) {
            return 0;
        }

        if (synset1.getOffset() == synset2.getOffset()) {
            return maxValue;
        }

        return calculate(paths1, paths2);

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
