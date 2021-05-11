/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.measures.measure.semantic.edgecounting.measures;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.AEdgeCountingSemanticMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.finders.ShortestPathFinder;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.AIndex;

import java.util.ArrayList;

/**
 * Implements the Leacock and Chodorow (LCH) semantic string similarity between
 * two concepts (synsets), that considers both the path between two concepts and
 * the depth of the hierarchy.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class LCHMeasure extends AEdgeCountingSemanticMeasure {

    public LCHMeasure(AIndex Indexer) {
        super(Indexer);
    }

    double maxValue = 1;

    /**
     * Calculates the actual LCH similarity.
     *
     * @param synset1,
     *            an input concept, used to retrieve the depth of the
     *            corresponding hierarchy
     * @param shortestPath,
     *            the length of the shortest path between two concepts
     * @return the LCH similarity between two concepts
     */
    public double calculate(ISynset synset1, double shortestPath) {
        double sim = 0;
        double D = (double) getHierarchyDepth(synset1.getType());
        sim = -Math.log((double) (1.0 + shortestPath) / (double) (1.0 + (2.0 * D)));
        sim /= (double) (Math.log((double) (1.0 + (2.0 * D))));
        return sim;
    }

    /**
     * Computes the LCH similarity between two concepts. It retrieves all
     * possible hypernym paths for the two concepts and finds the shortest path
     * between two concepts via their least common subsumer.
     *
     * @param synset1,
     *            the first input concept
     * @param synset2,
     *            the second input concept
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
        return "lch";
    }

    @Override
    public String getType() {
        return "semantic";
    }
}
