package org.aksw.limes.core.measures.measure.semantic.edgecounting.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.Pointer;

public class HypernymTreesFinder {
    public static boolean useInstanceHypernyms = true;
    public static boolean useHypernyms = true;

    public static List<List<ISynset>> getHypernymTrees(SemanticDictionary dictionary, ISynset synset) {
        if (synset == null)
            return new ArrayList<List<ISynset>>();

        List<List<ISynset>> trees = getHypernymTrees(dictionary, synset, new HashSet<ISynsetID>());

        return trees;
    }

    public static List<List<ISynset>> getHypernymTrees(SemanticDictionary dictionary, ISynset synset, Set<ISynsetID> history) {

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

        List<List<ISynset>> result = new ArrayList<List<ISynset>>();

        // If this is the highest node and has no other hypernyms
        if ((hypernymIds.size() == 0) && (instanceHypernymIds.size() == 0)) {
            // return the tree containing only the current node
            List<ISynset> tree = new ArrayList<ISynset>();
            tree.add(synset);
            result.add(tree);
        } else {
            // for all (direct) hypernyms of this synset
            for (ISynsetID hypernymId : hypernymIds) {

                List<List<ISynset>> hypernymTrees = getHypernymTrees(dictionary,dictionary.getSynset(hypernymId), history);
                // add the current Tree and
                for (List<ISynset> hypernymTree : hypernymTrees) {
                    hypernymTree.add(synset);
                    result.add(hypernymTree);
                }
            }
            for (ISynsetID hypernymId : instanceHypernymIds) {
                List<List<ISynset>> hypernymTrees = getHypernymTrees(dictionary, dictionary.getSynset(hypernymId), history);
                // add the current Tree and
                for (List<ISynset> hypernymTree : hypernymTrees) {
                    hypernymTree.add(synset);
                    result.add(hypernymTree);
                }
            }
        }

        return result;
    }
}
