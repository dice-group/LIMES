package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.semantic.ASemanticMeasure;
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
import weka.core.Stopwords;
import weka.core.tokenizers.WordTokenizer;

/**
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 */
public abstract class AEdgeCountingSemanticMeasure extends ASemanticMeasure implements IEdgeCountingSemanticMeasure {

    private static final Logger logger = LoggerFactory.getLogger(AEdgeCountingSemanticMeasure.class);

    protected static final int NOUN_DEPTH = 20;
    protected static final int VERB_DEPTH = 13;
    protected static final int ADJECTIVE_DEPTH = 1;
    protected static final int ADVERB_DEPTH = 1;

    protected IRAMDictionary dictionary = null;
    protected static boolean useInstanceHypernyms = true;
    protected static boolean useHypernyms = true;

    public IRAMDictionary getDictionary() {
        return dictionary;
    }

    protected File exFile = null;
    protected String wordNetFolder = System.getProperty("user.dir") + "/src/main/resources/wordnet/dict/";

    // System.getProperty("user.dir") + "/src/main/resources/wordnet/dict/"
    public AEdgeCountingSemanticMeasure() {
        exportDictionaryToFile();
    }

    public void exportDictionaryToFile() {
        File dictionaryFolder = new File(wordNetFolder);
        if (!dictionaryFolder.exists()) {
            logger.error("Wordnet dictionary folder doesn't exist. Can't do anything");
            logger.error(
                    "Please read the instructions in the README.md file on how to download the worndet database files.");
            throw new RuntimeException();
        } else {
            exFile = new File(wordNetFolder + "JWI_Export_.wn");
            if (!exFile.exists()) {
                logger.info("No exported wordnet file is found. Creating one..");
                dictionary = new RAMDictionary(dictionaryFolder);
                dictionary.setLoadPolicy(ILoadPolicy.IMMEDIATE_LOAD);
                logger.info("Loaded dictionary into memory. Now exporting it to file.");
                try {
                    dictionary.open();
                    dictionary.export(new FileOutputStream(exFile));
                    //logger.info("Export is " + (exFile.length() / 1048576) + " MB");
                } catch (IOException e1) {
                    logger.error("Couldn't open wordnet dictionary. Exiting..");
                    e1.printStackTrace();
                    throw new RuntimeException();
                } finally {
                    removeDictionary();
                }
            }
        }

    }

    public void removeDictionary() {
        if (dictionary != null) {
            dictionary.close();
            dictionary = null;
        }
    }

    public void openDictionaryFromFile() {
        if (!exFile.exists()) {
            logger.error("No exported wordnet file is found. Exiting..");
            logger.error("Please execute the exportDictionaryToFile function first.");
            logger.error(
                    "Please read the instructions in the README.md file on how to download the worndet database files.");
            throw new RuntimeException();
        } else {
            if (dictionary == null) {
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

    }

    public int getHierarchyDepth(int posNumber) {
        if (posNumber == 1)
            return NOUN_DEPTH;
        else if (posNumber == 2)
            return VERB_DEPTH;
        else if (posNumber == 4)
            return ADVERB_DEPTH;
        else if (posNumber == 5 || posNumber == 3)
            return ADJECTIVE_DEPTH;
        else {
            logger.error("Unknown POS. Exiting");
            throw new RuntimeException();
        }

    }

    public IWord getIWord(IWordID wordID) {
        IWord iword = null;
        if (wordID != null)
            iword = dictionary.getWord(wordID);

        return iword;
    }

    public ISynset getSynset(IWord iword) {
        if (iword == null)
            return null;
        return iword.getSynset();
    }

    protected HashMap<ISynset, List<List<ISynset>>> preprocessHypernymTrees(IIndexWord word) {

        if (word == null)
            return null;

        ISynset synset;
        HashMap<ISynset, List<List<ISynset>>> hypernymTrees = new HashMap<ISynset, List<List<ISynset>>>();

        List<IWordID> wordIDs = word.getWordIDs();
        if (wordIDs == null) {
            return hypernymTrees;
        }
        for (IWordID wordID : wordIDs) {
            IWord iword = getIWord(wordID);
            if (iword != null) {
                synset = getSynset(iword);
                if (synset != null) {
                    List<List<ISynset>> trees = getHypernymTrees(synset);
                    if (trees.isEmpty() == false)
                        hypernymTrees.put(synset, trees);
                }
            }

        }
        return hypernymTrees;
    }

    @Override
    public double getSimilarity(ISynset synset1, ISynset synset2) {
        List<List<ISynset>> list1 = this.getHypernymTrees(synset1);
        List<List<ISynset>> list2 = this.getHypernymTrees(synset2);

        return getSimilarity(synset1, list1, synset2, list2);
    }

    @Override
    public double getSimilarity(IIndexWord w1, IIndexWord w2) {
        // test in each semantic similarity
        double sim = 0.0d;
        double maxSim = 0.0d;

        if (w1 == null || w2 == null)
            return maxSim;

        if (w1.getPOS().getNumber() != w2.getPOS().getNumber())
            return maxSim;

        HashMap<ISynset, List<List<ISynset>>> trees2 = preprocessHypernymTrees(w2);
        if (trees2.isEmpty() == true)
            return maxSim;

        List<IWordID> wordIDs1 = w1.getWordIDs();
        if (wordIDs1 == null) {
            return maxSim;
        }
        for (IWordID wordID1 : wordIDs1) {
            IWord iword1 = getIWord(wordID1);
            if (iword1 != null) {
                ISynset synset1 = getSynset(iword1);
                if (synset1 != null) {
                    List<List<ISynset>> synset1Tree = getHypernymTrees(synset1);
                    if (synset1Tree.isEmpty() == false) {

                        for (Map.Entry<ISynset, List<List<ISynset>>> entry2 : trees2.entrySet()) {

                            ISynset synset2 = entry2.getKey();
                            List<List<ISynset>> synset2Tree = entry2.getValue();
                            sim = getSimilarity(synset1, synset1Tree, synset2, synset2Tree);
                            if (sim > maxSim) {
                                maxSim = sim;
                            }
                            if (maxSim == 1.0d) {
                                return maxSim;
                            }
                        }
                    }

                }
            }

        }

        return maxSim;
    }

    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        // test in each semantic similarity
        this.openDictionaryFromFile();
        double sim = 0;
        double maxSim = 0;
        HashMap<String, Double> similaritiesMap = new HashMap<String, Double>();
        for (String sourceValue : instance1.getProperty(property1)) {
            //logger.info("Source value: " + sourceValue);
            for (String targetValue : instance2.getProperty(property2)) {
                // create bag of words for each property value
                //logger.info("Target value: " + targetValue);
                double sourceTokensSum = 0;
                // compare each token of the current source value
                // with every token of the current target value
                WordTokenizer tokenizerSource = new WordTokenizer();
                tokenizerSource.tokenize(sourceValue);
                int counter = 0;
                while (tokenizerSource.hasMoreElements() == true) {

                    String sourceToken = tokenizerSource.nextElement();

                    if (!Stopwords.isStopword(sourceToken)) {
                        counter++;
                        double maxTargetTokenSim = 0;
                        WordTokenizer tokenizerTarget = new WordTokenizer();
                        tokenizerTarget.tokenize(targetValue);

                        while (tokenizerTarget.hasMoreElements() == true) {

                            String targetToken = tokenizerTarget.nextElement();

                            if (!Stopwords.isStopword(targetToken)) {

                                double targetTokenSim = 0.0d;
                                //logger.info("Source token: " + sourceToken + " Target token: " + targetToken);
                                String together = sourceToken + "||" + targetToken;
                                String together2 = targetToken + "||" + sourceToken;
                                if (similaritiesMap.containsKey(together)) {
                                    targetTokenSim = similaritiesMap.get(together);
                                    //logger.info("Similarity exists");
                                } else if (similaritiesMap.containsKey(together2)) {
                                    targetTokenSim = similaritiesMap.get(together2);
                                    //logger.info("Similarity exists2");
                                } else {
                                    //logger.info("Similarity doesn't exist");
                                    targetTokenSim = getSimilarity(sourceToken, targetToken);
                                    similaritiesMap.put(together, targetTokenSim);
                                }
                                //logger.info("Source token: " + sourceToken + " Target token: " + targetToken + ":"
                                //        + targetTokenSim);

                                // for the current source token, keep the
                                // highest
                                // similarity obtained by comparing it with all
                                // current
                                // target tokens

                                if (targetTokenSim > maxTargetTokenSim) {
                                    maxTargetTokenSim = targetTokenSim;
                                }
                                if (maxTargetTokenSim == 1.0d) {
                                    break;
                                }
                            } 
                        }
                        // for the current source bag of words, add the max
                        // similarity to the sum over all current source
                        // token similarities
                        sourceTokensSum += maxTargetTokenSim;
                    } 

                }
                // get the average of the max similarities of each source token
                // this is the similarity of the current source bag of words
                //logger.info("Non stop words " + counter);
                if (counter > 0)
                    sim = (double) sourceTokensSum / ((double) (counter));
                else
                    sim = 0;
                //logger.info("Average Sum  " + sim);

                if (sim > maxSim) {
                    maxSim = sim;
                }
                if (maxSim == 1.0d) {
                    break;
                }
            }
        }
        this.removeDictionary();
        return maxSim;

    }

    @Override
    public double getSimilarity(Object object1, Object object2) {
        if (object1 == null || object2 == null)
            return 0.0d;

        IIndexWord idxWord1 = getIIndexWord(object1.toString());
        IIndexWord idxWord2 = getIIndexWord(object2.toString());
        if (idxWord1 == null || idxWord2 == null)
            return 0.0d;
        else {
            if (idxWord1.getPOS().getNumber() != idxWord2.getPOS().getNumber()) {
                return 0.0d;
            } else {
                return this.getSimilarity(idxWord1, idxWord2);
            }
        }
    }

    @Override
    public IIndexWord getIIndexWord(String str) {
        if (str == null)
            return null;

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
        return idxWord1;
    }

    public List<List<ISynset>> getHypernymTrees(ISynset synset) {
        if (synset == null)
            return new ArrayList<List<ISynset>>();

        List<List<ISynset>> trees = getHypernymTrees(synset, new HashSet<ISynsetID>());

        return trees;
    }

    public List<List<ISynset>> getHypernymTrees(ISynset synset, Set<ISynsetID> history) {

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

    

}
