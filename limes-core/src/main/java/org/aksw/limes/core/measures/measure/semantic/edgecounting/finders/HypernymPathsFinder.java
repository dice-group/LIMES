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

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.Pointer;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.dictionary.SemanticDictionary;

import java.util.*;

/**
 * Computes all the paths from a synset (concept) to the root(s) of the tree
 * hierarchy. These paths are called hypernym paths.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @author Michael Roeder (michael.roeder@uni-paderborn.de)
 * @version 1.0
 */
public class HypernymPathsFinder {
    public static boolean useInstanceHypernyms = true;
    public static boolean useHypernyms = true;

    /**
     * Retrieves all hypernym paths in a tree hierarchy for a synset (concept) .
     *
     * @param dictionary,
     *            a semantic dictionary that represents the tree hierarchy
     * @param synset,
     *            the input synset
     * @return all hypernym paths of synset
     */
    public static ArrayList<ArrayList<ISynsetID>> getHypernymPaths(SemanticDictionary dictionary, ISynset synset) {
        if (synset == null)
            return new ArrayList<ArrayList<ISynsetID>>();

        ArrayList<ArrayList<ISynsetID>> trees = getHypernymPaths(dictionary, synset, new HashSet<ISynsetID>());

        return trees;
    }

    /**
     * Recursive function that computes all hypernym paths in a tree hierarchy
     * for a synset (concept), by traversing the graph in a BFS manner.
     *
     * @param dictionary,
     *            a semantic dictionary that represents the tree hierarchy
     * @param synset,
     *            the input synset
     * @param history,
     *            auxiliary set that stores the traversed paths
     * @return all hypernym paths of synset
     */
    public static ArrayList<ArrayList<ISynsetID>> getHypernymPaths(SemanticDictionary dictionary, ISynset synset,
                                                                   Set<ISynsetID> history) {

        // only noun hierarchy has instance hypernyms
        useInstanceHypernyms = synset.getType() == 1;
        // only noun and verb hierarchies have hypernyms
        useHypernyms = (synset.getType() == 1 || synset.getType() == 2);

        // get the hypernyms
        List<ISynsetID> hypernymIds = useHypernyms ? synset.getRelatedSynsets(Pointer.HYPERNYM)
                : Collections.emptyList();
        // get the hypernyms (if this is an instance)
        List<ISynsetID> instanceHypernymIds = useInstanceHypernyms ? synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE)
                : Collections.emptyList();

        ArrayList<ArrayList<ISynsetID>> result = new ArrayList<ArrayList<ISynsetID>>();

        // If this is the highest node and has no other hypernyms
        if ((hypernymIds.size() == 0) && (instanceHypernymIds.size() == 0)) {
            // return the tree containing only the current node
            ArrayList<ISynsetID> tree = new ArrayList<ISynsetID>();
            tree.add(synset.getID());
            result.add(tree);
        } else {
            // for all (direct) hypernyms of this synset
            for (ISynsetID hypernymId : hypernymIds) {
                ArrayList<ArrayList<ISynsetID>> hypernymTrees = getHypernymPaths(dictionary,
                        dictionary.getSynset(hypernymId), history);
                // add the current Tree and
                for (ArrayList<ISynsetID> hypernymTree : hypernymTrees) {
                    hypernymTree.add(synset.getID());
                    result.add(hypernymTree);
                }
            }
            for (ISynsetID hypernymId : instanceHypernymIds) {
                ArrayList<ArrayList<ISynsetID>> hypernymTrees = getHypernymPaths(dictionary,
                        dictionary.getSynset(hypernymId), history);
                // add the current Tree and
                for (ArrayList<ISynsetID> hypernymTree : hypernymTrees) {
                    hypernymTree.add(synset.getID());
                    result.add(hypernymTree);
                }

            }
        }

        return result;
    }
}
