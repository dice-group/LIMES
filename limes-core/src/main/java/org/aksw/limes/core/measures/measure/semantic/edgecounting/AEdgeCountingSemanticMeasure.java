package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.AMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.dictionary.SemanticDictionary;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.AIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import weka.core.Stopwords;
import weka.core.tokenizers.Tokenizer;
import weka.core.tokenizers.WordTokenizer;

/**
 * Implements the edge-counting semantic string similarity abstract class.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public abstract class AEdgeCountingSemanticMeasure extends AMeasure implements IEdgeCountingSemanticMeasure {

    private static final Logger logger = LoggerFactory.getLogger(AEdgeCountingSemanticMeasure.class);

    // fixed hierarchy depths for the wordnet database
    protected static final int NOUN_DEPTH = 19;
    protected static final int VERB_DEPTH = 13;
    protected static final int ADJECTIVE_DEPTH = 1;
    protected static final int ADVERB_DEPTH = 1;

    protected AIndex Indexer = null;
    protected SemanticDictionary dictionary = null;

    public AEdgeCountingSemanticMeasure(AIndex indexer) {
        dictionary = new SemanticDictionary();
        dictionary.exportDictionaryToFile();
        dictionary.openDictionaryFromFile();
        Indexer = indexer;

    }

    public SemanticDictionary getSemanticDictionary() {
        return dictionary;
    }

    public void setDictionary(SemanticDictionary dict) {
        this.dictionary = dict;
    }

    /**
     * Retrieves the hierarchy depth for a given POS.
     * 
     * @param posNumber,
     *            the input POS number id
     * @return the corresponding hierarchy depth
     */
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

    /**
     * Retrieves a IWord for a given IWordID
     * 
     * @param wordID,
     *            the input IWordID
     * @return the corresponding IWord
     */
    public IWord getIWord(IWordID wordID) {
        IWord iword = null;
        if (wordID != null)
            iword = dictionary.getWord(wordID);
        return iword;
    }

    /**
     * Retrieves an ISynset for a given IWord
     * 
     * @param iword,
     *            the input IWord
     * @return the corresponding ISynset
     */
    public ISynset getSynset(IWord iword) {
        if (iword == null)
            return null;
        return iword.getSynset();
    }

    /**
     * Retrieves a list of IWordID for a given IIndexWord
     * 
     * @param w,
     *            the input IIndexWord
     * @return the corresponding list of IWordIDs
     */
    public List<IWordID> getWordIDs(IIndexWord w) {
        List<IWordID> wordIDs = w.getWordIDs();
        return wordIDs;
    }

    /**
     * Retrieves a list of hypernym paths for a given concept, stored in an
     * index instance.
     * 
     * @param synset,
     *            the input concept
     * @return the corresponding list of hypernym paths
     */
    public ArrayList<ArrayList<ISynsetID>> getPaths(ISynset synset) {
        if (synset == null)
            return new ArrayList<ArrayList<ISynsetID>>();

        ArrayList<ArrayList<ISynsetID>> paths = Indexer.getHypernymPaths(synset);

        return paths;
    }

    /**
     * Calculates the semantic similarity between two concepts.
     * 
     * @param synset1,
     *            the first input concept
     * @param synset2,
     *            the second input concept
     * @return the actual semantic similarity between synset1 and synset2
     */
    public double getSimilarity(ISynset synset1, ISynset synset2) {
        double sim = 0.0;
        sim = getSimilarityBetweenConcepts(synset1, synset2);
        return sim;
    }

    /**
     * Calculates the semantic similarity between two IIndexWords.
     * 
     * @param w1,
     *            the first input IIndexWord
     * @param w2,
     *            the second input IIndexWord
     * @return the actual semantic similarity between w1 and w2
     */
    public double getSimilarity(IIndexWord w1, IIndexWord w2) {
        // test in each semantic similarity
        double sim = 0.0d;
        double maxSim = 0.0d;

        if (w1 == null || w2 == null)
            return maxSim;

        if (w1.getPOS().getNumber() != w2.getPOS().getNumber())
            return maxSim;

        // runtime calculated individually
        List<IWordID> wordIDs1 = getWordIDs(w1);
        if (wordIDs1 == null) {
            return maxSim;
        }
        // runtime calculated individually
        List<IWordID> wordIDs2 = getWordIDs(w2);
        if (wordIDs2 == null) {
            return maxSim;
        }

        for (IWordID wordID1 : wordIDs1) {
            // runtime calculated individually
            IWord iword1 = getIWord(wordID1);
            if (iword1 != null) {
                // runtime calculated individually
                ISynset synset1 = getSynset(iword1);
                if (synset1 != null) {

                    for (IWordID wordID2 : wordIDs2) {
                        // runtime calculated individually
                        IWord iword2 = getIWord(wordID2);
                        if (iword2 != null) {
                            // runtime calculated individually
                            ISynset synset2 = getSynset(iword2);
                            if (synset2 != null) {

                                // runtime calculated individually
                                sim = this.getSimilarity(synset1, synset2);

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
        }

        return maxSim;
    }

    /**
     * Tokenizes an array of strings
     * 
     * @param strings,
     *            the input array of strings
     * @return an array of tokenized strings
     */
    public String[] tokenize(String[] strings) {
        String[] tokens = null;
        try {
            tokens = Tokenizer.tokenize(new WordTokenizer(), strings);
        } catch (Exception e) {
            logger.error("Couldn't tokenize: " + strings[0]);
            e.printStackTrace();
        }
        return tokens;
    }

    /**
     * Checks if a semantic similarity between two tokens has already been
     * calculated.
     * 
     * @param similaritiesMap,
     *            the map that stores the semantic similarities between two
     *            tokens
     * @param sourceToken,
     *            the source input token
     * @param targetToken,
     *            the target input token
     * @return the similarity of the tokens, if it exists, or the maximum double
     *         value
     */
    public double checkSimilarity(HashMap<String, Double> similaritiesMap, String sourceToken, String targetToken) {
        double similarity = 0.0d;
        String together = sourceToken + "||" + targetToken;
        String together2 = targetToken + "||" + sourceToken;

        if (similaritiesMap.containsKey(together)) {
            similarity = similaritiesMap.get(together);
        } else if (similaritiesMap.containsKey(together2)) {
            similarity = similaritiesMap.get(together2);
        } else {
            similarity = Double.MAX_VALUE;
        }

        return similarity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.aksw.limes.core.measures.measure.IMeasure#getSimilarity(org.aksw.
     * limes.core.io.cache.Instance, org.aksw.limes.core.io.cache.Instance,
     * java.lang.String, java.lang.String)
     */
    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        // test in each semantic similarity

        double sim = 0;
        double maxSim = 0;

        // pre-tokenize all target labels
        ArrayList<String[]> targetInTokens = new ArrayList<String[]>();
        for (String targetValue : instance2.getProperty(property2)) {
            if (targetValue.equals(""))
                continue;
            String[] tempTokens = tokenize(new String[] { targetValue });
            targetInTokens.add(tempTokens);
        }

        ///////////////////////////////////////////////////////////////////
        HashMap<String, Double> similaritiesMap = new HashMap<String, Double>();

        for (String sourceValue : instance1.getProperty(property1)) {
            if (sourceValue.equals(""))
                continue;

            String[] sourceTokens = tokenize(new String[] { sourceValue });

            for (String[] targetTokens : targetInTokens) {

                double sourceTokensSum = 0;
                // compare each token of the current source value
                // with every token of the current target value
                int nonSWCounter = 0;

                for (String sourceToken : sourceTokens) {
                    boolean flagSource = Stopwords.isStopword(sourceToken);

                    if (!flagSource) {

                        nonSWCounter++;
                        double maxTargetTokenSim = 0;

                        for (String targetToken : targetTokens) {

                            boolean flagTarget = Stopwords.isStopword(targetToken);

                            if (!flagTarget) {

                                double targetTokenSim = 0.0d;

                                double tempSim = checkSimilarity(similaritiesMap, sourceToken, targetToken);
                                if (tempSim == Double.MAX_VALUE) {
                                    targetTokenSim = (sourceToken.equals(targetToken) == true) ? 1d
                                            : getSimilarity(sourceToken, targetToken);
                                    similaritiesMap.put(sourceToken + "||" + targetToken, targetTokenSim);
                                } else {
                                    targetTokenSim = tempSim;
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
                // sim = sourceTokensSum;
                if (nonSWCounter > 0)
                    sim = (double) sourceTokensSum / ((double) (nonSWCounter));
                else
                    sim = 0;

                // System.out.println("Score " + sim);

                if (sim > maxSim) {
                    maxSim = sim;
                }
                if (maxSim == 1.0d) {
                    break;
                }
            }
        }
        return maxSim;
    }

    /**
     * Closes and removes the semantic dictionary from memory
     * 
     */
    public void close() {
        dictionary.removeDictionary();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.aksw.limes.core.measures.measure.IMeasure#getSimilarity(java.lang.
     * Object, java.lang.Object)
     */
    @Override
    public double getSimilarity(Object object1, Object object2) {
        if (object1 == null || object2 == null)
            return 0.0d;

        // runtime calculated individually
        IIndexWord idxWord1 = getIIndexWord(object1.toString());
        // runtime calculated individually
        IIndexWord idxWord2 = getIIndexWord(object2.toString());

        if (idxWord1 == null || idxWord2 == null)
            return 0.0d;
        else {
            if (idxWord1.getPOS().getNumber() != idxWord2.getPOS().getNumber()) {
                return 0.0d;
            } else {
                return getSimilarity(idxWord1, idxWord2);
            }
        }
    }

    /**
     * Retrieves the IIndexWord for a given input string
     * 
     * @param str,
     *            the input string
     * @return the corresponding IIndexWord
     */
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
