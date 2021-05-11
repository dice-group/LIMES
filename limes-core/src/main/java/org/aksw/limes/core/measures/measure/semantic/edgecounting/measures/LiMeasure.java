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
import org.aksw.limes.core.measures.measure.semantic.edgecounting.finders.LeastCommonSubsumerFinder;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.AIndex;

import java.util.ArrayList;

/**
 * Implements the Li et al. (LI) semantic string similarity between two concepts
 * (synsets), using the path between two concepts and their least common
 * subsumer.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class LiMeasure extends AEdgeCountingSemanticMeasure {
    double a = 0.2;
    double b = 0.6;

    public LiMeasure(AIndex Indexer) {
        super(Indexer);
    }

    /**
     * Calculates the actual LI similarity.
     *
     * @param synset1Tree,
     *            the set of all hypernym paths for a concept synset1
     * @param synset2Tree,
     *            the set of all hypernym paths for a concept synset2
     * @return the LI similarity between two concepts
     */
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

    /**
     * Computes the LI similarity between two concepts. To do so, it retrieves
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

        return calculate(paths1, paths2);
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
