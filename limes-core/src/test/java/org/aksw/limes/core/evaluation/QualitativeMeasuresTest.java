/**
 * 
 */
package org.aksw.limes.core.evaluation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Multimap;

import org.aksw.limes.core.evaluation.quantity.FMeasure;
import org.aksw.limes.core.evaluation.quantity.Precision;
import org.aksw.limes.core.evaluation.quantity.QuantitativeMeasuresEvaluator;
import org.aksw.limes.core.evaluation.quantity.Recall;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;

/**
 * @author mofeed
 * @author Klaus Lyko
 */
public class QualitativeMeasuresTest {

	@Test
	public void test() {
//		Model model = ModelFactory.createDefaultModel();
//		Set<Link> m1 = new TreeSet<Link>();
//		Set<Link> m2 = new TreeSet<Link>();
//		Set<Link> ref = new TreeSet<Link>();

		Mapping goldStandard = initGoldStandardList();
		Mapping predictions = initPredictionsList();
		List<String> dataSet =initDataSet();
		
		Set<MeasureType> measure = initEvalMeasures();
		measure.add(MeasureType.precision);
		Map<MeasureType,Double> calculations = testEvaluate(predictions,goldStandard,dataSet,dataSet,measure);
		
		double precision = calculations.get(MeasureType.precision);
		assertTrue(precision == 0.7);
		
		double recall = calculations.get(MeasureType.recall);
		assertTrue(recall == 0.7);
		
		double fmeasure = calculations.get(MeasureType.fmeasure);
		assertTrue(fmeasure == 0.7);
		
		double accuracy = calculations.get(MeasureType.accuracy);
		assertTrue(accuracy == 4.85);
		
		double pprecision = calculations.get(MeasureType.pseuPrecision);
		assertTrue(pprecision == 0.8);
		
		double precall = calculations.get(MeasureType.PseuRecall);
		assertTrue(precall == 0.8);
		
		
		double pfmeasure = calculations.get(MeasureType.pseuFMeasure);
		assertTrue(pfmeasure > 0.7 && pfmeasure < 0.9);


	}
	private Map<MeasureType,Double> testEvaluate(Mapping predictions,Mapping goldStandard,List<String> sourceUris,List<String> targetUris,Set<MeasureType> evaluationMeasure)
	{
		return new QuantitativeMeasuresEvaluator().evaluate(predictions, goldStandard, sourceUris, targetUris, evaluationMeasure);
	}
	private Mapping initGoldStandardList()
	{
	
		Mapping gold = new MemoryMapping();
		gold.add("http://dbpedia.org/resource/A", "http://dbpedia.org/resource/A", 1);
		gold.add("http://dbpedia.org/resource/B", "http://dbpedia.org/resource/B", 1);
		gold.add("http://dbpedia.org/resource/C", "http://dbpedia.org/resource/C", 1);
		gold.add("http://dbpedia.org/resource/D", "http://dbpedia.org/resource/D", 1);
		gold.add("http://dbpedia.org/resource/E", "http://dbpedia.org/resource/E", 1);
		gold.add("http://dbpedia.org/resource/F", "http://dbpedia.org/resource/F", 1);
		gold.add("http://dbpedia.org/resource/G", "http://dbpedia.org/resource/G", 1);
		gold.add("http://dbpedia.org/resource/H", "http://dbpedia.org/resource/H", 1);
		gold.add("http://dbpedia.org/resource/I", "http://dbpedia.org/resource/I", 1);
		gold.add("http://dbpedia.org/resource/J", "http://dbpedia.org/resource/J", 1);
		return gold;
		
	}
	private Mapping initPredictionsList()
	{
		
		Mapping pred = new MemoryMapping();
		pred.add("http://dbpedia.org/resource/A", "http://dbpedia.org/resource/A", 1);
		pred.add("http://dbpedia.org/resource/B", "http://dbpedia.org/resource/B", 1);
		pred.add("http://dbpedia.org/resource/C", "http://dbpedia.org/resource/C", 1);
		pred.add("http://dbpedia.org/resource/C", "http://dbpedia.org/resource/D", 1);
		pred.add("http://dbpedia.org/resource/D", "http://dbpedia.org/resource/F", 1);
		pred.add("http://dbpedia.org/resource/F", "http://dbpedia.org/resource/F", 1);
		pred.add("http://dbpedia.org/resource/G", "http://dbpedia.org/resource/G", 1);
		pred.add("http://dbpedia.org/resource/H", "http://dbpedia.org/resource/H", 1);
		pred.add("http://dbpedia.org/resource/I", "http://dbpedia.org/resource/H", 1);
		pred.add("http://dbpedia.org/resource/I", "http://dbpedia.org/resource/I", 1);

		return pred;
		
	}
	private List<String> initDataSet()
	{
		List<String> dataSet = new ArrayList<String>();
		dataSet.add("http://dbpedia.org/resource/A");
		dataSet.add("http://dbpedia.org/resource/B");
		dataSet.add("http://dbpedia.org/resource/C");
		dataSet.add("http://dbpedia.org/resource/C");
		dataSet.add("http://dbpedia.org/resource/D");
		dataSet.add("http://dbpedia.org/resource/F");
		dataSet.add("http://dbpedia.org/resource/G");
		dataSet.add("http://dbpedia.org/resource/H");
		dataSet.add("http://dbpedia.org/resource/I");
		dataSet.add("http://dbpedia.org/resource/I");
		return dataSet;

	}
	private Set<MeasureType> initEvalMeasures()
	{
		Set<MeasureType> measure = new HashSet<MeasureType>();

		measure.add(MeasureType.precision);
		measure.add(MeasureType.recall);
		measure.add(MeasureType.fmeasure);
		measure.add(MeasureType.accuracy);
		measure.add(MeasureType.pseuPrecision);
		measure.add(MeasureType.PseuRecall);
		measure.add(MeasureType.pseuFMeasure);
		
		
		return measure;

	}

}
