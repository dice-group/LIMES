package org.aksw.limes.core.measures.measure.semantic.edgecounting.preprocessing;

import java.util.Iterator;
import java.util.List;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.preprocessing.DB.DBImplementation;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.HypernymTreesFinder;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.SemanticDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.POS;

public class Preprocessing {
    private static final Logger logger = LoggerFactory.getLogger(Preprocessing.class);

    public static DBImplementation db = new DBImplementation();
    protected static SemanticDictionary dictionary = null;


    public static void main(String[] args){
        db.init();
        dictionary.exportDictionaryToFile();
        dictionary.openDictionaryFromFile();
        for (POS pos : POS.values()) {
            Iterator<ISynset> iterator = dictionary.getDictionary().getSynsetIterator(pos);
            while (iterator.hasNext()) {
                ISynset synset = iterator.next();
                List<List<ISynset>> trees = HypernymTreesFinder.getHypernymTrees(dictionary,synset);
                db.addSysnetTrees(synset.getID(), trees);
            }
        }
        dictionary.removeDictionary();
        
        db.close();
    }
    
}
