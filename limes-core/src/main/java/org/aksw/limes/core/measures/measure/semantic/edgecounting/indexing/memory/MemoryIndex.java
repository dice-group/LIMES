package org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.dictionary.SemanticDictionary;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.finders.HypernymPathsFinder;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.AIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.POS;

/**
 * Implements the memory index class that computes, stores and loads the
 * hypernym paths of every synset in wordnet.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class MemoryIndex extends AIndex {
    private static final Logger logger = LoggerFactory.getLogger(MemoryIndex.class);

    HashMap<String, HashMap<Integer, Integer>> minDepths = new HashMap<String, HashMap<Integer, Integer>>();
    HashMap<String, HashMap<Integer, Integer>> maxDepths = new HashMap<String, HashMap<Integer, Integer>>();

    HashMap<String, HashMap<Integer, ArrayList<ArrayList<ISynsetID>>>> paths = new HashMap<String, HashMap<Integer, ArrayList<ArrayList<ISynsetID>>>>();

    protected SemanticDictionary dictionary = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(boolean f) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preIndex() {
        this.preIndexPaths();

    }

    /**
     * Stores in memory all possible hypernym paths of a wordnet's synset from
     * the root(s).
     * 
     *
     */
    public void preIndexPaths() {
        dictionary = new SemanticDictionary();
        dictionary.exportDictionaryToFile();
        dictionary.openDictionaryFromFile();

        logger.info("Indexing begins.");
        for (POS pos : POS.values()) {
            paths.put(pos.toString(), new HashMap<Integer, ArrayList<ArrayList<ISynsetID>>>());
            Iterator<ISynset> iterator = dictionary.getDictionary().getSynsetIterator(pos);
            while (iterator.hasNext()) {
                ISynset synset = iterator.next();
                ArrayList<ArrayList<ISynsetID>> trees = HypernymPathsFinder.getHypernymPaths(dictionary, synset);

                HashMap<Integer, ArrayList<ArrayList<ISynsetID>>> temp = paths.get(pos.toString());
                temp.put(synset.getOffset(), trees);
                paths.put(pos.toString(), temp);
            }
        }
        logger.info("Indexing done.");
        dictionary.removeDictionary();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<ArrayList<ISynsetID>> getHypernymPaths(ISynset synset) {
        return paths.get(synset.getPOS().toString()).get(synset.getOffset());
    }

}
