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
package org.aksw.limes.core.measures.measure.semantic.edgecounting.finders;

import edu.mit.jwi.item.ISynsetID;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the shortest path finder class. The shortest path between two
 * concepts c1 and c2 is a path that includes a common subsumer of c1 and c2 (a
 * concept that is a hypernym of both c1 and c2) and has the shortest length.
 *
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 *
 */
public class ShortestPathFinder {

    /**
     * Computes the shortest path between two concepts using their hypernym
     * paths. For each pair of hypernym paths, it traverses both the paths
     * simultaneously, starting from the root and until they share no more
     * common concepts. Then it calculates the path length between the two
     * concepts, as the number of the concepts that they do not have in common.
     * In case the distance between the two concepts is smaller than the
     * previously found one, the algorithm sets it as the new shortest path.
     *
     *
     * @param synset1Tree,
     *            the hypernym paths of the first concept
     * @param synset2Tree,
     *            the hypernym paths of the second concept
     *
     * @return the length of the shortest path between two concepts
     */
    public static int shortestPath(ArrayList<ArrayList<ISynsetID>> synset1Tree,
                                   ArrayList<ArrayList<ISynsetID>> synset2Tree) {

        int path = Integer.MAX_VALUE;

        if (synset1Tree == null || synset2Tree == null)
            return -1;

        if (synset1Tree.isEmpty() == true || synset2Tree.isEmpty() == true)
            return -1;

        int path1Pos, path2Pos;

        for (List<ISynsetID> synset1HypernymPath : synset1Tree) {
            for (List<ISynsetID> synset2HypernymPath : synset2Tree) {

                path1Pos = 0;
                path2Pos = 0;
                while ((path1Pos < synset1HypernymPath.size()) && (path2Pos < synset2HypernymPath.size())
                        && (synset1HypernymPath.get(path1Pos).getOffset() == synset2HypernymPath.get(path2Pos)
                        .getOffset())) {
                    ++path1Pos;
                    ++path2Pos;
                }
                int newPath = synset1HypernymPath.size() + synset2HypernymPath.size() - 2 * path1Pos;
                if (newPath < path) {
                    path = newPath;
                }

            }
        }
        if (path == Integer.MAX_VALUE)
            path = -1;

        return path;

    }

}
