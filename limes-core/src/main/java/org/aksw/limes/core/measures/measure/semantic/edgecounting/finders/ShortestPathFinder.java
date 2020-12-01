package org.aksw.limes.core.measures.measure.semantic.edgecounting.finders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.dictionary.SemanticDictionary;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.Pointer;

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
