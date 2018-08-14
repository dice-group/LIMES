package org.aksw.limes.core.measures.measure.semantic.edgecounting.preprocessing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.dictionary.SemanticDictionary;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.preprocessing.DB.DBImplementation;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.HypernymPathsFinder;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.MinMaxDepthFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.POS;

public class PreIndexing {
    private static final Logger logger = LoggerFactory.getLogger(PreIndexing.class);

    public static DBImplementation db = null;
    public static boolean flush = false;
    protected static SemanticDictionary dictionary = null;
    public static long durationMinMax = 0;
    public static long durationPaths = 0;

    public static void init() {
        logger.info("Init dictionary.");
        db = new DBImplementation();
        db.init();
        logger.info("Flush? "+flush);
        if(flush)
            db.flush();
        logger.info("DB is empth? "+db.isEmpty());
        dictionary = new SemanticDictionary();
        dictionary.exportDictionaryToFile();
        dictionary.openDictionaryFromFile();
    }

    public static void close() {
        logger.info("Closing dictionary..");
        dictionary.removeDictionary();
        db.close();
    }

    public static void preIndexMinMaxDepths() {
        init();
        logger.info("Finding min and max depths.");
        long begin = System.currentTimeMillis();
        MinMaxDepthFinder.getLengths(null, POS.values(), dictionary, db);
        long end = System.currentTimeMillis();
        durationMinMax = end - begin;
        logger.info("Done.");
        close();
    }

    public static void preIndexPaths() {
        init();
        logger.info("Finding all paths from root");
        long begin = System.currentTimeMillis();
        for (POS pos : POS.values()) {
            Iterator<ISynset> iterator = dictionary.getDictionary().getSynsetIterator(pos);
            while (iterator.hasNext()) {
                ISynset synset = iterator.next();
                ArrayList<ArrayList<ISynsetID>> trees = HypernymPathsFinder.getHypernymPaths(dictionary, synset);
                db.addSysnetPaths(synset.getID().toString(), trees);
            }
        }
        long end = System.currentTimeMillis();
        durationPaths = end - begin;
        logger.info("Done.");
        close();
    }

    public static void preIndexAll() {
        init();
        logger.info("Finding all paths from root and min and max depths.");
        for (POS pos : POS.values()) {
            Iterator<ISynset> iterator = dictionary.getDictionary().getSynsetIterator(pos);
            while (iterator.hasNext()) {
                ISynset synset = iterator.next();

                long beginPaths = System.currentTimeMillis();
                ArrayList<ArrayList<ISynsetID>> trees = HypernymPathsFinder.getHypernymPaths(dictionary, synset);
                db.addSysnetPaths(synset.getID().toString(), trees);
                long endPaths = System.currentTimeMillis();
                durationPaths += endPaths - beginPaths;

                long beginMinMax = System.currentTimeMillis();
                ArrayList<ISynsetID> max = trees.stream().max(Comparator.comparingInt(List::size)).get();
                ArrayList<ISynsetID> min = trees.stream().min(Comparator.comparingInt(List::size)).get();
                db.addMinDepth(synset.toString(), min.size());
                db.addMaxDepth(synset.toString(), max.size());
                long endMinMax = System.currentTimeMillis();
                durationMinMax += endMinMax - beginMinMax;

            }
        }
        logger.info("Done.");
        close();
    }

}
