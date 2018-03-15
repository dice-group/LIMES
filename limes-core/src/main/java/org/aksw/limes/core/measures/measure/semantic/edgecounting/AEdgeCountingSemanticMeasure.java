package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.AMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.LeastCommonSubsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;

/**
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 */
public abstract class AEdgeCountingSemanticMeasure extends AMeasure implements IEdgeCountingSemanticMeasure {

    private static final Logger logger = LoggerFactory.getLogger(AEdgeCountingSemanticMeasure.class);

    protected static final int NOUN_DEPTH = 20;
    protected static final int VERB_DEPTH = 13;
    protected static final int ADJECTIVE_DEPTH = 1;
    protected static final int ADVERB_DEPTH = 1;

    protected IRAMDictionary dictionary;

    protected File exFile = null;
    protected String wordNetFolder = System.getProperty("user.dir") + "/src/main/resources/wordnet/dict/";

    // System.getProperty("user.dir") + "/src/main/resources/wordnet/dict/"
    public AEdgeCountingSemanticMeasure() {
        exportDictionaryInFile();
    }

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

    public void openDictionary() {
        
        logger.info("Exported wordnet file is found.");
        if (dictionary.isOpen() == false) {
            dictionary = new RAMDictionary(exFile);
            try {
                dictionary.open();
            } catch (IOException e2) {
                logger.error("Couldn't open wordnet dictionary. Exiting..");
                e2.printStackTrace();
                throw new RuntimeException();
            }
        }
    }

    public int getHierarchyDepth(int posNumber) {
        if (posNumber == 1)
            return NOUN_DEPTH;
        else if (posNumber == 2)
            return VERB_DEPTH;
        else if (posNumber == 4)
            return ADVERB_DEPTH;
        else
            return ADJECTIVE_DEPTH;
    }

    protected HashMap<ISynset, List<List<ISynset>>> preprocessHypernymTrees(IIndexWord word) {
        
        ISynset synset;
        HashMap<ISynset, List<List<ISynset>>> hypernymTrees = new HashMap<ISynset, List<List<ISynset>>>();
        List<IWordID> wordIDs = word.getWordIDs();

        if (wordIDs == null) {
            return hypernymTrees;
        }
        for (IWordID wordID : wordIDs) {
            IWord iword = dictionary.getWord(wordID);
            if (iword != null) {
                synset = iword.getSynset();
                if (synset != null) {
                    hypernymTrees.put(synset, getHypernymTrees(synset));
                }
            }

        }
        return hypernymTrees;
    }

    @Override
    public double getSimilarity(IIndexWord w1, IIndexWord w2) {

        double sim = 0;
        double maxSim = 0;
        this.openDictionary();
        HashMap<ISynset, List<List<ISynset>>> trees1 = preprocessHypernymTrees(w1);

        HashMap<ISynset, List<List<ISynset>>> trees2 = preprocessHypernymTrees(w2);
        dictionary.close();
        
        for (Map.Entry<ISynset, List<List<ISynset>>> entry :  trees1.entrySet()){

            ISynset synset1 = entry.getKey();
            List<List<ISynset>> synset1Tree = entry.getValue();

            for (Map.Entry<ISynset, List<List<ISynset>>> entry2 :  trees2.entrySet()){
                
                ISynset synset2 = entry2.getKey();
                List<List<ISynset>> synset2Tree = entry2.getValue();
                
                sim = getSimilarity(synset1, synset1Tree, synset2, synset2Tree);
                if (sim > maxSim)
                    maxSim = sim;
            }
        }

        return maxSim;
    }

    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        double sim = 0;
        double maxSim = 0;
        for (String sourceValue : instance1.getProperty(property1)) {
            String[] sourceBoW = sourceValue.split(" ");
            for (String targetValue : instance2.getProperty(property2)) {
                // create bag of words for each property value
                String[] targetBoW = targetValue.split(" ");

                double sum = 0;
                // compare each token of the current source value
                // with every token of the current target value
                for (String sourceToken : sourceBoW) {
                    double smallSim = 0;
                    double smallMaxSim = 0;

                    for (String targetToken : targetBoW) {

                        smallSim = getSimilarity(sourceToken, targetToken);

                        // for the current source token, keep the highest
                        // similarity obtained by comparing it with all current
                        // target tokens

                        if (smallSim > smallMaxSim) {
                            smallMaxSim = smallSim;
                        }
                    }
                    // for the current source bag of words, add the max
                    // similarity to the sum over all current source
                    // token similarities
                    sum += smallMaxSim;

                }
                // get the average of the max similarities of each source token
                // this is the similarity of the current source bag of words
                sim = (double) sum / ((double) (sourceBoW.length));

                if (sim > maxSim) {
                    maxSim = sim;
                }
            }
        }
        return maxSim;
    }

    @Override
    public double getSimilarity(Object object1, Object object2) {
        IIndexWord idxWord1 = getIIndexWord(object1.toString());
        IIndexWord idxWord2 = getIIndexWord(object1.toString());
        if (idxWord1 == null || idxWord2 == null)
            return 0;
        else {
            if (idxWord1.getPOS().getNumber() != idxWord2.getPOS().getNumber())
                return 0;
            else {
                return this.getSimilarity(idxWord1, idxWord2);
            }
        }
    }

    @Override
    public IIndexWord getIIndexWord(String str) {
        openDictionary();
        IIndexWord idxWord1 = dictionary.getIndexWord(str, POS.NOUN);
        if (idxWord1 == null) {
            idxWord1 = dictionary.getIndexWord(str, POS.ADJECTIVE);
            if (idxWord1 == null) {
                idxWord1 = dictionary.getIndexWord(str, POS.ADVERB);
                if (idxWord1 == null) {
                    idxWord1 = dictionary.getIndexWord(str, POS.VERB);
                }
            }

        }
        dictionary.close();
        return idxWord1;
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

    public LeastCommonSubsumer getLeastCommonSubsumer(ISynset synset1, List<List<ISynset>> synset1Tree, ISynset synset2,
            List<List<ISynset>> synset2Tree) {

        LeastCommonSubsumer lcs = new LeastCommonSubsumer(synset1, synset2);
        int path1Pos, path2Pos;
        for (List<ISynset> synset1HypernymPath : synset1Tree) {
            for (List<ISynset> synset2HypernymPath : synset2Tree) {
                path1Pos = 0;
                path2Pos = 0;
                while ((path1Pos < synset1HypernymPath.size()) && (path2Pos < synset2HypernymPath.size())
                        && (synset1HypernymPath.get(path1Pos).getOffset() == synset2HypernymPath.get(path2Pos)
                                .getOffset())) {
                    ++path1Pos;
                    ++path2Pos;
                }
                if (path1Pos >= lcs.getDepth()) {
                    lcs.setPaths(synset1HypernymPath.subList(0, path1Pos),
                            synset1HypernymPath.subList(path1Pos, synset1HypernymPath.size()),
                            synset2HypernymPath.subList(path2Pos, synset2HypernymPath.size()));
                }
            }
        }
        return lcs;
    }

}
