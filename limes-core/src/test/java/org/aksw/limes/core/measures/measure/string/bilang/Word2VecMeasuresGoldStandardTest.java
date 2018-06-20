/**
 *
 */
package org.aksw.limes.core.measures.measure.string.bilang;

import java.io.File;
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
import org.aksw.limes.core.measures.measure.string.Doc2VecMeasure;
import org.aksw.limes.core.measures.measure.string.MyUtil;
import org.junit.Before;
import org.junit.Test;

public class Word2VecMeasuresGoldStandardTest {
	
	public static final double epsilon = 0.00001;
	static String basePath = "src/test/resources/";
	
	WordEmbeddings we = new WordEmbeddings("src/test/resources/unsup.128");
	
	@Test
	public void testMyThreeWordMeasures() throws IOException {
		ArrayList<String> words1 = new ArrayList<>();
		ArrayList<String> words2 = new ArrayList<>();
		ArrayList<Double> similarities = new ArrayList<>();
		Stream<String> lines = Files.lines(Paths.get("src/test/resources/wordsim352.tab"));
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
		BilangDictionary dictionary = new BilangDictionary(BilangDictionary.DEFAULT_DICTIONARY_PATH);
		SimpleDictionaryMeasure naiveMeasure = new SimpleDictionaryMeasure(dictionary);
		WordNetInterface wn = new WordNetInterface("src/test/resources/WordNet-3.0");
		ArrayList<Double> humanSims = new ArrayList<Double>();
		ArrayList<Double> naiveSims = new ArrayList<Double>();
		ArrayList<Double> wnSims = new ArrayList<Double>();
		ArrayList<Double> w2vSims = new ArrayList<Double>();
		System.out.println("human, naive, wordnet, embedding");
		int size = words1.size();
		for (int i = 0; i < size; i++) {
			String a = words1.get(i);
			String b = words2.get(i);
			double simHuman = similarities.get(i);
			double simNaive = naiveMeasure.getSimilarity(a, b);
			double simWordNet = wn.computeWuPalmerSimilarity(a, b);
			double simWe = we.getCosineSimilarityForWords(a, b);
			System.out.println(a + " " + b + " " + simHuman + " " + simNaive + " " +
				simWordNet + " " + simWe);
			humanSims.add(simHuman);
			naiveSims.add(simNaive);
			wnSims.add(simWordNet);
			w2vSims.add(simWe);
		}
		
		
		AMapping goldMapping = MappingFactory.createDefaultMapping();
		
		double humanMedian = MyUtil.getMedian(humanSims);
		for (int i = 0; i < size; i++) {
			if (humanSims.get(i) > humanMedian) {
				goldMapping.add(words1.get(i), words2.get(i), 1.0);
			}
		}
		System.out.println("Evaluation measures for naive, WordNet, Word2Vec");
		GoldStandard goldStandard =  new GoldStandard(goldMapping, words1, words2);
		Arrays.asList(naiveSims, wnSims, w2vSims).forEach(sims -> {
			AMapping predictions = MappingFactory.createDefaultMapping();
			double median = MyUtil.getMedian(sims);
			for (int i = 0; i < size; i++) {
				if (sims.get(i) > median) {
					predictions.add(words1.get(i), words2.get(i), 1.0);
				}
			}
			testPredictionAgaintHumanScores(predictions, goldStandard);
		});
		
		
	}
	
	
	public void testPredictionAgaintHumanScores(AMapping predictions, GoldStandard goldStandard) {
		Set<EvaluatorType> evaluationMeasures = initEvalMeasures();
		
		Map<EvaluatorType, Double> calculations = new QualitativeMeasuresEvaluator().evaluate(predictions, goldStandard, evaluationMeasures);
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
