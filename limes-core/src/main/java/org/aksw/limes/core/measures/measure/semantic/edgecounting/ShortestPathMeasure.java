package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import java.util.ArrayList;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.filters.ASemanticFilter;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.filters.SemanticFilterFactory;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.AIndex;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.ShortestPathFinder;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;

public class ShortestPathMeasure extends AEdgeCountingSemanticMeasure {

    double maxValue = 1;

    public ShortestPathMeasure(double threshold, boolean preindex, boolean filtering, AIndex Indexer) {
        super(threshold, preindex, filtering, Indexer);
    }

    public double calculate(ISynset synset1, double shortestPath) {
        
        double sim = 0;
        double D = (double) getHierarchyDepth(synset1.getType());
        sim = (double) (2.0 * D) - (double) (shortestPath);
        // normalize
        sim /= (double) (2.0 * D);

        return sim;
    }

    @Override
    public double getSimilarityComplex(ISynset synset1, ISynset synset2) {
        double sim = 0.0d;
        ArrayList<ArrayList<ISynsetID>> paths1 = getPaths(synset1);
        ArrayList<ArrayList<ISynsetID>> paths2 = getPaths(synset2);

        //long b = System.currentTimeMillis();
        sim = getSimilarity(synset1, paths1, synset2, paths2);
        //long e = System.currentTimeMillis();
        //runtimes.getSynsetSimilarity += e - b;
        return sim;
    }

    @Override
    public double getSimilaritySimple(ISynset synset1, ISynset synset2) {
        double sim = 0.0d;
        //long b = System.currentTimeMillis();

        if (synset1.getType() != synset2.getType())
            return 0;

        if (synset1.getOffset() == synset2.getOffset())
            return maxValue;

        int shortestPath = ShortestPathFinder.shortestPath(synset1, synset2, dictionary);
        sim = calculate(synset1, shortestPath);

        //long e = System.currentTimeMillis();
        //runtimes.getSynsetSimilarity += e - b;

        return sim;

    }

    public double getSimilarity(ISynset synset1, ArrayList<ArrayList<ISynsetID>> synset1Tree, ISynset synset2,
            ArrayList<ArrayList<ISynsetID>> synset2Tree) {

        
        if (synset1Tree.isEmpty() == true || synset2Tree.isEmpty() == true)
            return 0;

        if (synset1.getType() != synset2.getType())
            return 0;

        if (synset1.getOffset() == synset2.getOffset())
            return maxValue;

        int shortestPath = ShortestPathFinder.shortestPath(synset1Tree, synset2Tree);
        //there is a problem with the synset paths, so return 0 similarity
        if(shortestPath == -1)
            return 0.0d;
        
        return calculate(synset1, shortestPath);
    }


    @Override
    public boolean filter(ArrayList<Integer> parameters) {
        
        if (parameters == null)
            return true;
        if (parameters.isEmpty())
            return true;
        ASemanticFilter filter = SemanticFilterFactory
                .createFilter(SemanticFilterFactory.getFilterType("shortest_path"), theta, parameters.get(2));

        boolean flag = filter.filter(parameters);
        return flag;
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
