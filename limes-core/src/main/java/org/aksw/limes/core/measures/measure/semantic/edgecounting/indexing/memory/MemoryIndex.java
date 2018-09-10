package org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.dictionary.SemanticDictionary;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.AIndex;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.HypernymPathsFinder;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.MinMaxDepthFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.POS;

public class MemoryIndex extends AIndex {
    private static final Logger logger = LoggerFactory.getLogger(MemoryIndex.class);

    HashMap<Integer, Integer> minDepths = new HashMap<Integer, Integer>();
    HashMap<Integer, Integer> maxDepths = new HashMap<Integer, Integer>();

    HashMap<Integer, ArrayList<ArrayList<ISynsetID>>> paths = new HashMap<Integer, ArrayList<ArrayList<ISynsetID>>>();
    protected SemanticDictionary dictionary = null;
    
    @Override
    public void init(boolean f) {
    }

    @Override
    public void close() {
    }

    @Override
    public void preIndex() {
        for (int i = 0; i < durations.length; i++)
            durations[i] = 0l;

        long bIndex = System.currentTimeMillis();
        this.preIndexMinMaxDepths();
        long eIndex = System.currentTimeMillis();
        long indexMinMax = eIndex - bIndex;
        durations[0] = indexMinMax;

        /*
         * bIndex = System.currentTimeMillis(); this.preIndexPaths(); eIndex =
         * System.currentTimeMillis(); long indexPaths = eIndex - bIndex;
         * durations[1] = indexPaths;
         */

    }
    
    protected void preIndexMinMaxDepths() {
        
        dictionary = new SemanticDictionary();
        dictionary.exportDictionaryToFile();
        dictionary.openDictionaryFromFile();

        logger.info("Finding min and max depths.");
        for (POS pos : POS.values()) {
            MinMaxDepthFinder finder = new MinMaxDepthFinder();
            finder.calculateMinMaxDepths(pos, dictionary);
            HashMap<Integer, int[]> depths = finder.getDepths();

            for (Integer sid : depths.keySet()) {
                int[] values = depths.get(sid);
                minDepths.put(sid, values[0]);
                maxDepths.put(sid, values[1]);
            }
        }

        logger.info("Done.");
        dictionary.removeDictionary();
    }

    public void preIndexPaths() {
        dictionary = new SemanticDictionary();
        dictionary.exportDictionaryToFile();
        dictionary.openDictionaryFromFile();

        logger.info("Finding all paths from root");
        for (POS pos : POS.values()) {
            Iterator<ISynset> iterator = dictionary.getDictionary().getSynsetIterator(pos);
            while (iterator.hasNext()) {
                ISynset synset = iterator.next();
                ArrayList<ArrayList<ISynsetID>> trees = HypernymPathsFinder.getHypernymPaths(dictionary, synset);
                paths.put(synset.getOffset(), trees);
            }
        }
        logger.info("Done.");
        dictionary.removeDictionary();
        
    }

    @Override
    public int getMinDepth(ISynset synset) {
        return minDepths.get(synset.getOffset());
    }

    @Override
    public int getMaxDepth(ISynset synset) {
        return maxDepths.get(synset.getOffset());
    }

    @Override
    public ArrayList<ArrayList<ISynsetID>> getHypernymPaths(ISynset synset) {
        return paths.get(synset.getOffset());
    }

}
