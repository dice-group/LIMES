package org.aksw.limes.core.measures.measure.semantic.edgecounting.measures;

import java.util.ArrayList;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.AEdgeCountingSemanticMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.finders.ShortestPathFinder;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.AIndex;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;

/**
 * Implements the Shortest Path semantic string similarity between two concepts
 * (synsets), that considers both the path between two concepts and the depth of
 * the hierarchy.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class ShortestPathMeasure extends AEdgeCountingSemanticMeasure {

    double maxValue = 1;

    public ShortestPathMeasure(AIndex Indexer) {
        super(Indexer);
    }

    /**
     * Calculates the actual Shortest Path similarity.
     * 
     * @param synset1,
     *            an input concept, used to retrieve the depth of the
     *            corresponding hierarchy
     * @param shortestPath,
     *            the length of the shortest path between two concepts
     * @return the Shortest Path similarity between two concepts
     */
    public double calculate(ISynset synset1, double shortestPath) {

        double sim = 0;
        double D = (double) getHierarchyDepth(synset1.getType());
        sim = (double) (2.0 * D) - (double) (shortestPath);
        // normalize
        sim /= (double) (2.0 * D);

        return sim;
    }

    /**
     * Computes the Shortest Path similarity between two concepts. It retrieves
     * all possible hypernym paths for the two concepts and finds the shortest
     * path between two concepts via their least common subsumer.
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

        if (paths1.isEmpty() == true || paths2.isEmpty() == true)
            return 0;

        if (synset1.getType() != synset2.getType())
            return 0;

        if (synset1.getOffset() == synset2.getOffset())
            return maxValue;

        int shortestPath = ShortestPathFinder.shortestPath(paths1, paths2);
        // there is a problem with the synset paths, so return 0 similarity
        if (shortestPath == -1)
            return 0.0d;

        return calculate(synset1, shortestPath);

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
