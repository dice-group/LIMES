package org.aksw.limes.core.measures.measure.semantic.edgecounting.utils;

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

public class ShortestPathFinder {

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
