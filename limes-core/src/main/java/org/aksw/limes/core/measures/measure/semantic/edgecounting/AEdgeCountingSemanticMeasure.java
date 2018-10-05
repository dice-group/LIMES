package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.semantic.ASemanticMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.dictionary.SemanticDictionary;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.AIndex;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.HypernymPathsFinder;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.MinMaxDepthFinder;
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
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 */
public abstract class AEdgeCountingSemanticMeasure extends ASemanticMeasure implements IEdgeCountingSemanticMeasure {

    private static final Logger logger = LoggerFactory.getLogger(AEdgeCountingSemanticMeasure.class);

    protected static final int NOUN_DEPTH = 19;
    protected static final int VERB_DEPTH = 13;
    protected static final int ADJECTIVE_DEPTH = 1;
    protected static final int ADVERB_DEPTH = 1;

    protected AIndex Indexer = null;
    protected SemanticDictionary dictionary = null;
    protected double theta;
    protected boolean preIndex;
    protected boolean filtering = true;

    public boolean simple = false;

    protected RuntimeStorage runtimes = new RuntimeStorage();

    public RuntimeStorage getRuntimeStorage() {
        return runtimes;
    }

    public AEdgeCountingSemanticMeasure(double threshold, boolean pre, boolean fil, AIndex indexer) {
        theta = threshold;
        preIndex = pre;
        filtering = fil;
        logger.info("filtering: " + filtering);
        logger.info("indexing: " + preIndex);

        runtimes = new RuntimeStorage();

        long b = System.currentTimeMillis();
        dictionary = new SemanticDictionary();
        dictionary.exportDictionaryToFile();
        dictionary.openDictionaryFromFile();
        long e = System.currentTimeMillis();
        runtimes.createDictionary += e - b;

        if (preIndex) {
            // in case of db, connection is already opened
            Indexer = indexer;
        }

    }

    public class RuntimeStorage {
        protected long createDictionary = 0l;
        protected long sourceTokenizing = 0l;
        protected long targetTokenizing = 0l;
        protected long checkStopWords = 0l;
        protected long checkSimilarity = 0l;
        protected long getIIndexWords = 0l;
        protected long getWordIDs = 0l;
        protected long getIWord = 0l;
        protected long getSynset = 0l;
        protected long getMinMaxDepth = 0l;
        protected long filter = 0l;
        protected long getHypernymPaths = 0l;
        protected long getSynsetSimilarity = 0l;
        protected long getSimilarityInstances = 0l;



        public long createDictionary() {
            return createDictionary;
        }

        public long checkStopWords() {
            return checkStopWords;
        }

        public long getSourceTokenizing() {
            return sourceTokenizing;
        }

        public long getTargetTokenizing() {
            return targetTokenizing;
        }

        public long getCheckSimilarity() {
            return checkSimilarity;
        }

        public long getGetIIndexWords() {
            return getIIndexWords;
        }

        public long getGetWordIDs() {
            return getWordIDs;
        }

        public long getGetIWord() {
            return getIWord;
        }

        public long getGetSynset() {
            return getSynset;
        }

        public long getGetMinMaxDepth() {
            return getMinMaxDepth;
        }

        public long getFilter() {
            return filter;
        }

        public long getGetHypernymPaths() {
            return getHypernymPaths;
        }

        public long getGetSynsetSimilarity() {
            return getSynsetSimilarity;
        }

        public long getSimilarityInstances() {
            return getSimilarityInstances;
        }

    }

    public SemanticDictionary getSemanticDictionary() {
        return dictionary;
    }

    public void setDictionary(SemanticDictionary dict) {
        this.dictionary = dict;
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
        long b = System.currentTimeMillis();
        IWord iword = null;
        if (wordID != null)
            iword = dictionary.getWord(wordID);
        long e = System.currentTimeMillis();
        runtimes.getIWord += e - b;
        return iword;
    }

    public ISynset getSynset(IWord iword) {
        long b = System.currentTimeMillis();
        if (iword == null)
            return null;
        long e = System.currentTimeMillis();
        // called multiple times
        runtimes.getSynset += e - b;
        return iword.getSynset();
    }

    public List<IWordID> getWordIDs(IIndexWord w) {
        long b = System.currentTimeMillis();
        List<IWordID> wordIDs = w.getWordIDs();
        long e = System.currentTimeMillis();
        // called multiple times
        runtimes.getWordIDs += e - b;
        return wordIDs;
    }

    public ArrayList<ArrayList<ISynsetID>> getPaths(ISynset synset) {
        long b = System.currentTimeMillis();
        if (synset == null)
            return new ArrayList<ArrayList<ISynsetID>>();

        ArrayList<ArrayList<ISynsetID>> paths = preIndex ? Indexer.getHypernymPaths(synset)
                : HypernymPathsFinder.getHypernymPaths(dictionary, synset);

        // ArrayList<ArrayList<ISynsetID>> paths =
        // HypernymPathsFinder.getHypernymPaths(dictionary, synset);

        long e = System.currentTimeMillis();
        // called multiple times
        runtimes.getHypernymPaths += e - b;
        return paths;
    }

    @Override
    public double getSimilarity(ISynset synset1, ISynset synset2) {

        double sim = 0.0;

        if (filtering == true) {

            long r = System.currentTimeMillis();
            int minDepth1 = 0, minDepth2 = 0;
            int[] depths1 = new int[3], depths2 = new int[3];
            long m = System.currentTimeMillis();
            runtimes.filter += m - r;

            long bMinMax = System.currentTimeMillis();
            if (preIndex) {
                minDepth1 = Indexer.getMinDepth(synset1);
                minDepth2 = Indexer.getMinDepth(synset2);

            } else {

                MinMaxDepthFinder finder = new MinMaxDepthFinder();
                finder.calculateMinMaxDepths(synset1.getPOS(), dictionary);
                HashMap<Integer, int[]> depths = finder.getDepths();

                depths1 = depths.get(synset1.getOffset());
                minDepth1 = depths1[0];
                depths2 = depths.get(synset2.getOffset());
                minDepth2 = depths2[0];

            }
            long eMinMax = System.currentTimeMillis();
            // called multiple times
            runtimes.getMinMaxDepth += eMinMax - bMinMax;

            r = System.currentTimeMillis();
            ArrayList<Integer> parameters = new ArrayList<Integer>();
            parameters.add(minDepth1);
            parameters.add(minDepth2);
            m = System.currentTimeMillis();
            runtimes.filter += m - r;

            if (this.getName().equals("wupalmer") || this.getName().equals("li")) {
                int maxDepth1 = 0, maxDepth2 = 0;
                bMinMax = System.currentTimeMillis();
                if (preIndex) {
                    maxDepth1 = Indexer.getMaxDepth(synset1);
                    maxDepth2 = Indexer.getMaxDepth(synset2);
                } else {
                    maxDepth1 = depths1[1];
                    maxDepth2 = depths2[1];
                }
                eMinMax = System.currentTimeMillis();
                // called multiple times
                runtimes.getMinMaxDepth += eMinMax - bMinMax;

                r = System.currentTimeMillis();
                parameters.add(maxDepth1);
                parameters.add(maxDepth2);
                m = System.currentTimeMillis();
                runtimes.filter += m - r;
            }

            r = System.currentTimeMillis();
            int D = getHierarchyDepth(synset1.getType());
            parameters.add(D);
            m = System.currentTimeMillis();
            runtimes.filter += m - r;

            long bFilter = System.currentTimeMillis();
            boolean passed = filter(parameters);
            long eFilter = System.currentTimeMillis();
            // called multiple times
            runtimes.filter += eFilter - bFilter;

            if (passed == false) {
                return 0.0d;
            } else {
                // if (simple == false)
                sim = getSimilarityComplex(synset1, synset2);
                // else
                // sim = getSimilaritySimple(synset1, synset2);
            }
        } else {
            // if (simple == false)
            sim = getSimilarityComplex(synset1, synset2);
            // else
            // sim = getSimilaritySimple(synset1, synset2);
        }

        return sim;

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

    public String[] tokenize(String[] input) {
        String[] tokens = null;
        try {
            tokens = Tokenizer.tokenize(new WordTokenizer(), input);
        } catch (Exception e) {
            logger.error("Could tokenize: " + input[0]);
            e.printStackTrace();
        }
        return tokens;
    }

    public double checkSimilarity(HashMap<String, Double> similaritiesMap, String sourceToken, String targetToken) {
        long bCheck = System.currentTimeMillis();
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
        long eCheck = System.currentTimeMillis();
        // called multiple times
        runtimes.checkSimilarity += eCheck - bCheck;

        return similarity;
    }

    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        // test in each semantic similarity
        long b = System.currentTimeMillis();

        // if (dictionaryFlag == true)
        // dictionary.openDictionaryFromFile();
        double sim = 0;
        double maxSim = 0;

        // pre-tokenize all target labels
        long bTokenizeTarget = System.currentTimeMillis();
        ArrayList<String[]> targetInTokens = new ArrayList<String[]>();
        for (String targetValue : instance2.getProperty(property2)) {
            if (targetValue.equals(""))
                continue;
            String[] tempTokens = tokenize(new String[] { targetValue });
            targetInTokens.add(tempTokens);
        }
        long eTokenizeTarget = System.currentTimeMillis();
        // called once per instance pair
        runtimes.targetTokenizing += eTokenizeTarget - bTokenizeTarget;

        ///////////////////////////////////////////////////////////////////
        HashMap<String, Double> similaritiesMap = new HashMap<String, Double>();

        for (String sourceValue : instance1.getProperty(property1)) {
            // System.out.println("++++++++++++++++++++++++++++++");
            if (sourceValue.equals(""))
                continue;

            long bTokenizeBegin = System.currentTimeMillis();
            String[] sourceTokens = tokenize(new String[] { sourceValue });
            long eTokenizeBegin = System.currentTimeMillis();

            // called multiple times, once for each source value
            runtimes.sourceTokenizing += eTokenizeBegin - bTokenizeBegin;

            for (String[] targetTokens : targetInTokens) {

                double sourceTokensSum = 0;
                // compare each token of the current source value
                // with every token of the current target value
                int nonSWCounter = 0;

                for (String sourceToken : sourceTokens) {
                    long bStopSource = System.currentTimeMillis();
                    boolean flagSource = Stopwords.isStopword(sourceToken);
                    long eStopSource = System.currentTimeMillis();
                    runtimes.checkStopWords += eStopSource - bStopSource;

                    if (!flagSource) {

                        nonSWCounter++;
                        double maxTargetTokenSim = 0;

                        for (String targetToken : targetTokens) {

                            long bStopTarget = System.currentTimeMillis();
                            boolean flagTarget = Stopwords.isStopword(targetToken);
                            long eStopTarget = System.currentTimeMillis();
                            runtimes.checkStopWords += eStopTarget - bStopTarget;

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
                                // System.out.println("----> "+sourceToken + " "
                                // + targetToken + " = " + targetTokenSim);
                                if (targetTokenSim > maxTargetTokenSim) {
                                    maxTargetTokenSim = targetTokenSim;
                                }
                                if (maxTargetTokenSim == 1.0d) {
                                    break;
                                }
                            }
                        }
                        // System.out.println("--------------------------------");
                        // for the current source bag of words, add the max
                        // similarity to the sum over all current source
                        // token similarities
                        // if(maxTargetTokenSim > sourceTokensSum)
                        // sourceTokensSum = maxTargetTokenSim;

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

        // if (dictionaryFlag == true)
        // dictionary.removeDictionary();

        long e = System.currentTimeMillis();
        // called once
        runtimes.getSimilarityInstances += e - b;
        return maxSim;
    }

    public void close() {
        dictionary.removeDictionary();
    }

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

    @Override
    public IIndexWord getIIndexWord(String str) {
        long b = System.currentTimeMillis();
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
        long e = System.currentTimeMillis();
        // called multiple times
        runtimes.getIIndexWords += e - b;
        return idxWord1;
    }

}
