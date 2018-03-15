package org.aksw.limes.core.measures.measure.semantic.edgecounting.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;

public class HierarchiesDepthCounter {
    private static final Logger logger = LoggerFactory.getLogger(HierarchiesDepthCounter.class);

    protected IRAMDictionary dictionary;

    protected File exFile = null;
    protected String wordNetFolder = System.getProperty("user.dir") + "/src/main/resources/wordnet/dict/";

    public void exportDictionaryInFile() {
        File dictionaryFolder = new File(wordNetFolder);
        if (!dictionaryFolder.exists()) {
            logger.error("Wordnet dictionary folder doesn't exist. Can't do anything");
            throw new RuntimeException();
        } else {
            logger.info("Wordnet dictionary folder exists.");

            exFile = new File(wordNetFolder + "JWI_Export_.wn");
            if (!exFile.exists()) {
                logger.info("No exported wordnet file is found. Creating one..");
                dictionary = new RAMDictionary(dictionaryFolder);
                dictionary.setLoadPolicy(ILoadPolicy.IMMEDIATE_LOAD);
                logger.info("Loaded dictionary");
                try {
                    dictionary.open();
                    dictionary.export(new FileOutputStream(exFile));
                    dictionary.close();
                    logger.info("Export is " + (exFile.length() / 1048576) + " MB");
                } catch (IOException e1) {
                    logger.error("Couldn't open wordnet dictionary. Exiting..");
                    e1.printStackTrace();
                    throw new RuntimeException();
                }
            } else {
                logger.info("Exported wordnet file exists. Nothing else to do.");
            }
        }

    }

    public List<List<ISynset>> getHypernymTrees(ISynset synset) {
        List<List<ISynset>> trees = getHypernymTrees(synset, new HashSet<ISynsetID>());
        return trees;
    }

    public List<List<ISynset>> getHypernymTrees(ISynset synset, Set<ISynsetID> history) {

        // get the hypernyms
        List<ISynsetID> hypernymIds = synset.getRelatedSynsets(Pointer.HYPERNYM);
        // get the hypernyms (if this is an instance)
        List<ISynsetID> instanceHypernymIds = synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE);

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
                // if (!history.contains(hypernymId)) {
                // history.add(hypernymId);

                List<List<ISynset>> hypernymTrees = getHypernymTrees(dictionary.getSynset(hypernymId), history);
                // add the current Tree and
                for (List<ISynset> hypernymTree : hypernymTrees) {
                    hypernymTree.add(synset);
                    result.add(hypernymTree);
                }
            }
            for (ISynsetID hypernymId : instanceHypernymIds) {
                List<List<ISynset>> hypernymTrees = getHypernymTrees(dictionary.getSynset(hypernymId), history);
                // add the current Tree and
                for (List<ISynset> hypernymTree : hypernymTrees) {
                    hypernymTree.add(synset);
                    result.add(hypernymTree);
                }
            }
        }

        return result;
    }

    public void getNounSynsets() {
        Iterator<ISynset> iterator = dictionary.getSynsetIterator(POS.NOUN);
        int maxDepth = 0;
        int counter = 0;
        while (iterator.hasNext()) {
            ISynset synset = iterator.next();
            counter++;
            List<List<ISynset>> trees = this.getHypernymTrees(synset);
            for (List<ISynset> tree : trees) {
                if (tree.size() > maxDepth) {
                    maxDepth = tree.size();
                }
            }

        }
        logger.info("Noun Hierarchy depth = " + maxDepth);
        logger.info("Number of synsets: " + counter);
    }

    public void getVerbSynsets() {
        Iterator<ISynset> iterator = dictionary.getSynsetIterator(POS.VERB);
        int maxDepth = 0;
        int counter = 0;
        while (iterator.hasNext()) {
            ISynset synset = iterator.next();
            counter++;
            List<List<ISynset>> trees = this.getHypernymTrees(synset);
            for (List<ISynset> tree : trees) {
                if (tree.size() > maxDepth) {
                    maxDepth = tree.size();
                }
            }

        }
        logger.info("Verb Hierarchy depth = " + maxDepth);
        logger.info("Number of synsets: " + counter);
    }

    public void getAdjectiveSynsets() {
        Iterator<ISynset> iterator = dictionary.getSynsetIterator(POS.ADJECTIVE);
        int maxDepth = 0;
        int counter = 0;
        while (iterator.hasNext()) {
            ISynset synset = iterator.next();
            synset.getRelatedSynsets(Pointer.DERIVED_FROM_ADJ);
            counter++;
            List<List<ISynset>> trees = this.getHypernymTrees(synset);
            for (List<ISynset> tree : trees) {
                if (tree.size() > maxDepth) {
                    maxDepth = tree.size();
                }
            }
            List<ISynsetID> list = synset.getRelatedSynsets(Pointer.SIMILAR_TO);
            if (!list.isEmpty()) {
                logger.info("Similar to = " + list.toString());
            }
        }
        logger.info("Adjective Hierarchy depth = " + maxDepth);
        logger.info("Number of synsets: " + counter);
    }

    public void getAdverbSynsets() {
        Iterator<ISynset> iterator = dictionary.getSynsetIterator(POS.ADVERB);
        int maxDepth = 0;
        int counter = 0;
        while (iterator.hasNext()) {
            ISynset synset = iterator.next();
            counter++;
            List<List<ISynset>> trees = this.getHypernymTrees(synset);
            for (List<ISynset> tree : trees) {
                if (tree.size() > maxDepth) {
                    maxDepth = tree.size();
                }
            }

        }
        logger.info("Adverb Hierarchy depth = " + maxDepth);
        logger.info("Number of synsets: " + counter);
    }

    public static void main(String[] args) {
        HierarchiesDepthCounter hdc = new HierarchiesDepthCounter();
        hdc.exportDictionaryInFile();
        try {
            hdc.dictionary = new RAMDictionary(hdc.exFile);
            hdc.dictionary.open();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //hdc.getNounSynsets();
        //hdc.getVerbSynsets();
        hdc.getAdjectiveSynsets();
        //hdc.getAdverbSynsets();
        hdc.dictionary.close();

    }
}
