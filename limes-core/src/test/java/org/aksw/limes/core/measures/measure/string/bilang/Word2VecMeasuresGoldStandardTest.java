/**
 *
 */
package org.aksw.limes.core.measures.measure.string.bilang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.qualititativeMeasures.QualitativeMeasuresEvaluator;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.measure.string.MyTestUtil;
import org.junit.Test;

/**
 * Tests for the {@link Word2VecMeasure} similarity measure in comparison with the other, less sophisticated
 * word similarity metrics. We can see that {@link Word2VecMeasure} works better than the other ones.
 * The test set is a dataset of 352 English word pairs, attached with average similarity scores assigned to
 * them by human test subjects. No similar dataset was available for English/German word pairs,
 * so the testings is performed only mono-lingually unfortunately.
 *
 * For this test to work you need the
 *
 * @author Swante Scholz
 */
public class Word2VecMeasuresGoldStandardTest {
    
    public static final double epsilon = 0.00001;
    private static String basePath = "src/test/resources/";
    
    private WordEmbeddings wordEmbeddings = new WordEmbeddings(basePath + "unsup.128");
    
    @Test
    public void testMyThreeWordMeasures() throws IOException {
        ArrayList<String> words1 = new ArrayList<>();
        ArrayList<String> words2 = new ArrayList<>();
        ArrayList<Double> similarities = new ArrayList<>();
        Stream<String> lines = Files.lines(Paths.get(basePath + "wordsim352.tab"));
        lines.forEach(line -> {
            String[] parts = line.split("\t");
            String word1 = parts[0];
            String word2 = parts[1];
            double similarity = Double.parseDouble(parts[2]);
            words1.add(word1);
            words2.add(word2);
            similarities.add(similarity);
        });
        lines.close();
        BilangDictionary dictionary = new BilangDictionary(
            BilangDictionary.DEFAULT_DICTIONARY_PATH);
        SimpleDictionaryMeasure naiveMeasure = new SimpleDictionaryMeasure(dictionary);
        WordNetInterface wn = new WordNetInterface(basePath + "/WordNet-3.0");
        ArrayList<Double> humanSims = new ArrayList<>();
        ArrayList<Double> naiveSims = new ArrayList<>();
        ArrayList<Double> wnSims = new ArrayList<>();
        ArrayList<Double> w2vSims = new ArrayList<>();
        System.out.println("human, naive, wordnet, embedding");
        int size = words1.size();
        for (int i = 0; i < size; i++) {
            String a = words1.get(i);
            String b = words2.get(i);
            double simHuman = similarities.get(i);
            double simNaive = naiveMeasure.getSimilarity(a, b);
            double simWordNet = wn.computeWuPalmerSimilarity(a, b);
            double simWe = wordEmbeddings.getCosineSimilarityForWords(a, b);
            System.out.println(a + " " + b + " " + simHuman + " " + simNaive + " " +
                simWordNet + " " + simWe);
            humanSims.add(simHuman);
            naiveSims.add(simNaive);
            wnSims.add(simWordNet);
            w2vSims.add(simWe);
        }
        
        AMapping goldMapping = MappingFactory.createDefaultMapping();
        
        double humanMedian = MyTestUtil.getMedian(humanSims);
        for (int i = 0; i < size; i++) {
            if (humanSims.get(i) > humanMedian) {
                goldMapping.add(words1.get(i), words2.get(i), 1.0);
            }
        }
        System.out.println("Evaluation measures for naive, WordNet, Word2Vec");
        GoldStandard goldStandard = new GoldStandard(goldMapping, words1, words2);
        Arrays.asList(naiveSims, wnSims, w2vSims).forEach(sims -> {
            AMapping predictions = MappingFactory.createDefaultMapping();
            double median = MyTestUtil.getMedian(sims);
            for (int i = 0; i < size; i++) {
                if (sims.get(i) > median) {
                    predictions.add(words1.get(i), words2.get(i), 1.0);
                }
            }
            testPredictionAgaintHumanScores(predictions, goldStandard);
        });
        
        
    }
	
	/*
	Evaluation measures for naive, WordNet, Word2Vec
======================================
precision: 0.484375
recall: 0.3563218390804598
fmeasure: 0.4105960264900662
accuracy: 0.9985634039256198
pprecision: 0.82421875
precall: 0.2997159090909091
pfmeasure: 0.4395833333333333
======================================
precision: 0.5838150289017341
recall: 0.5804597701149425
fmeasure: 0.5821325648414986
accuracy: 0.9988297391528925
pprecision: 0.7716763005780347
precall: 0.37926136363636365
pfmeasure: 0.5085714285714286
======================================
precision: 0.6514285714285715
recall: 0.6551724137931034
fmeasure: 0.6532951289398281
accuracy: 0.9990234375
pprecision: 0.8085714285714286
precall: 0.40198863636363635
pfmeasure: 0.5370018975332068
	 */
    
    
    private void testPredictionAgaintHumanScores(AMapping predictions, GoldStandard goldStandard) {
        Set<EvaluatorType> evaluationMeasures = initEvalMeasures();
        
        Map<EvaluatorType, Double> calculations = new QualitativeMeasuresEvaluator()
            .evaluate(predictions, goldStandard, evaluationMeasures);
        System.out.println("======================================");
        double precision = calculations.get(EvaluatorType.PRECISION);
        System.out.println("precision: " + precision);
//		assertEquals(0.7, precision, epsilon);
        
        double recall = calculations.get(EvaluatorType.RECALL);
        System.out.println("recall: " + recall);
//		assertEquals(0.7, recall, epsilon);
        
        double fmeasure = calculations.get(EvaluatorType.F_MEASURE);
        System.out.println("fmeasure: " + fmeasure);
//		assertEquals(0.7, fmeasure, epsilon);
        
        double accuracy = calculations.get(EvaluatorType.ACCURACY);
        System.out.println("accuracy: " + accuracy);
//		assertEquals(0.94, accuracy, epsilon);
        
        double pprecision = calculations.get(EvaluatorType.P_PRECISION);
        System.out.println("pprecision: " + pprecision);
//		assertEquals(0.8, pprecision, epsilon);
        
        double precall = calculations.get(EvaluatorType.P_RECALL);
        System.out.println("precall: " + precall);
//		assertEquals(0.8, precall, epsilon);
        
        double pfmeasure = calculations.get(EvaluatorType.PF_MEASURE);
        System.out.println("pfmeasure: " + pfmeasure);
//		assertTrue(pfmeasure > 0.7 && pfmeasure < 0.9);
    }
    
    private Set<EvaluatorType> initEvalMeasures() {
        Set<EvaluatorType> measures = new HashSet<EvaluatorType>();
        measures.add(EvaluatorType.PRECISION);
        measures.add(EvaluatorType.RECALL);
        measures.add(EvaluatorType.F_MEASURE);
        measures.add(EvaluatorType.ACCURACY);
        measures.add(EvaluatorType.P_PRECISION);
        measures.add(EvaluatorType.P_RECALL);
        measures.add(EvaluatorType.PF_MEASURE);
        return measures;
    }
    
}
