package org.aksw.limes.core.measures.measure.semantic.edgecounting.finders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.dictionary.SemanticDictionary;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.Pointer;

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
