package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.semantic.ASemanticMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.preprocessing.DB.DBImplementation;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.HypernymTreesFinder;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.SemanticDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
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

    protected DBImplementation db = null;
    protected SemanticDictionary dictionary = null;
    protected boolean dictionaryFlag = true;

    public SemanticDictionary getSemanticDictionary() {
        return dictionary;
    }

    public void setDictionary(SemanticDictionary dict) {
        this.dictionary = dict;
    }

    public AEdgeCountingSemanticMeasure() {
        dictionary = new SemanticDictionary();
        dictionary.exportDictionaryToFile();
    }

    public void setDictionaryFlag(boolean f) {
        dictionaryFlag = f;
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
                    
                    List<List<ISynset>> trees = null;
                    if (db.isEmpty()) {
                        trees = HypernymTreesFinder.getHypernymTrees(dictionary, synset);
                    } else {
                        trees = db.getSynsetsTrees(synset.getID());
                    }

                    if (trees.isEmpty() == false)
                        hypernymTrees.put(synset, trees);
                }
            }

        }
        return hypernymTrees;
    }

    @Override
    public double getSimilarity(ISynset synset1, ISynset synset2) {
        List<List<ISynset>> list1 = null;
        if (db.isEmpty()) {
            list1 = HypernymTreesFinder.getHypernymTrees(dictionary, synset1);
        } else {
            list1 = db.getSynsetsTrees(synset1.getID());
        }
        
        List<List<ISynset>> list2 = null;
        if (db.isEmpty()) {
            list2 = HypernymTreesFinder.getHypernymTrees(dictionary, synset2);
        } else {
            list2 = db.getSynsetsTrees(synset2.getID());
        }

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
                    List<List<ISynset>> synset1Tree = null;
                    if (db.isEmpty()) {
                        synset1Tree = HypernymTreesFinder.getHypernymTrees(dictionary, synset1);
                    } else {
                        synset1Tree = db.getSynsetsTrees(synset1.getID());
                    }
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
        db = new DBImplementation();
        db.init();
        if (dictionaryFlag == true)
            dictionary.openDictionaryFromFile();
        double sim = 0;
        double maxSim = 0;
        HashMap<String, Double> similaritiesMap = new HashMap<String, Double>();
        for (String sourceValue : instance1.getProperty(property1)) {
            // logger.info("Source value: " + sourceValue);
            for (String targetValue : instance2.getProperty(property2)) {
                // create bag of words for each property value
                // logger.info("Target value: " + targetValue);
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
                                // logger.info("Source token: " + sourceToken +
                                // " Target token: " + targetToken);
                                String together = sourceToken + "||" + targetToken;
                                String together2 = targetToken + "||" + sourceToken;
                                if (similaritiesMap.containsKey(together)) {
                                    targetTokenSim = similaritiesMap.get(together);
                                    // logger.info("Similarity exists");
                                } else if (similaritiesMap.containsKey(together2)) {
                                    targetTokenSim = similaritiesMap.get(together2);
                                    // logger.info("Similarity exists2");
                                } else {
                                    // logger.info("Similarity doesn't exist");
                                    targetTokenSim = (sourceToken.equals(targetToken) == true) ? 1d
                                            : getSimilarity(sourceToken, targetToken);
                                    similaritiesMap.put(together, targetTokenSim);
                                }

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
                // logger.info("Non stop words " + counter);
                if (counter > 0)
                    sim = (double) sourceTokensSum / ((double) (counter));
                else
                    sim = 0;
                // logger.info("Average Sum " + sim);

                if (sim > maxSim) {
                    maxSim = sim;
                }
                if (maxSim == 1.0d) {
                    break;
                }
            }
        }
        if (dictionaryFlag == true)
            dictionary.removeDictionary();
        db.close();
        
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

}
