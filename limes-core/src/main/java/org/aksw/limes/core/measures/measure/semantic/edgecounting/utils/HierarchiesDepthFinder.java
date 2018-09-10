package org.aksw.limes.core.measures.measure.semantic.edgecounting.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.dictionary.SemanticDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.POS;

public class HierarchiesDepthFinder {
    private static final Logger logger = LoggerFactory.getLogger(HierarchiesDepthFinder.class);

    protected static SemanticDictionary dictionary = null;
    //public static DBImplementation db = new DBImplementation();
    
    public HierarchiesDepthFinder() {
        dictionary = new SemanticDictionary();
        dictionary.exportDictionaryToFile();
        dictionary.openDictionaryFromFile();
    }


    public void getHierarchySynsets(POS pos) {
        Iterator<ISynset> iterator = dictionary.getDictionary().getSynsetIterator(pos);
        int maxDepth = 0;
        int counter = 0;
        while (iterator.hasNext()) {
            ISynset synset = iterator.next();
            counter++;
            List<ArrayList<ISynsetID>> trees = HypernymPathsFinder.getHypernymPaths(dictionary, synset);
            for (ArrayList<ISynsetID> tree : trees) {
                int size = tree.size();
                if(pos.equals(POS.NOUN))
                    size--;
                if (size > maxDepth) {
                    maxDepth = tree.size();
                }
            }

        }
        logger.info(pos.name()+" Hierarchy depth = " + maxDepth);
        logger.info("Number of synsets: " + counter);
    }

    

    public static void main(String[] args) {
        HierarchiesDepthFinder hdc = new HierarchiesDepthFinder();
        hdc.getHierarchySynsets(POS.NOUN);
        hdc.getHierarchySynsets(POS.VERB);
        hdc.getHierarchySynsets(POS.ADJECTIVE);
        hdc.getHierarchySynsets(POS.ADVERB);
        dictionary.removeDictionary();

    }
}
