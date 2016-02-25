package org.aksw.limes.core.evaluation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.*;
import org.aksw.limes.core.ml.algorithm.*;
import org.aksw.limes.core.io.cache.*;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.evaluation.MeasureType;
import org.aksw.limes.core.evaluation.quality.*;
import org.aksw.limes.core.evaluation.quantity.QuantitativeMeasure;

public class Evaluator {

	QualitativeMeasuresEvaluator eval = new QualitativeMeasuresEvaluator();
	public Table<String,String,Map<MeasureType, Double>> evaluate(Set<MLAlgorithm> algorithms, Set<DataSetsPair> datasets, Set<MeasureType> QlMeasures, Set<QuantitativeMeasure> QnMeasures)
	{
		Table<String,String,Map<MeasureType, Double>> overallEvaluations = HashBasedTable.create();// multimap stores aglortihmName:datasetname:List of evaluations
		
		Mapping predictions=null;
		Map<MeasureType, Double> evaluationResults = null;
		
		for (MLAlgorithm algorithm : algorithms) {// select a ML algorithm
			for (DataSetsPair dataset : datasets) {// select a dataset-pair to evaluate each ML algorithm on
				
				algorithm.setSourceCache(dataset.source);
				algorithm.setTargetCache(dataset.target);
				predictions = algorithm.computePredictions();
				evaluationResults = eval.evaluate(predictions, dataset.goldStandard, dataset.source.getAllUris(), dataset.target.getAllUris(), QlMeasures);
				overallEvaluations.put(algorithm.getName(), dataset.pairName,evaluationResults);

			}
		}
	
		return overallEvaluations;
		
	}

/*	public Multimap<String, Map<String,Map<MeasureType, Double>>> evaluate(Set<MLAlgorithm> algorithms, Set<DataSetsPair> datasets, Set<MeasureType> QlMeasures, Set<QuantitativeMeasure> QnMeasures)
	{
		Multimap<String, Map<String,Map<MeasureType, Double>>> overallEvaluations = HashMultimap.create();// multimap stores aglortihmName:datasetname:List of evaluations
		Map<String,Map<MeasureType, Double>> datasetPairsEvaluations = new HashMap<String,Map<MeasureType, Double>>();// map stores dataset name:set of measures and their values
		Mapping predictions=null;
		Map<MeasureType, Double> evaluationResults = null;
		for (MLAlgorithm algorithm : algorithms) {// select a ML algorithm
			for (DataSetsPair dataset : datasets) {// select a dataset-pair to evaluate each ML algorithm on
				
				algorithm.setSourceCache(dataset.source);
				algorithm.setTargetCache(dataset.target);
				predictions = algorithm.computePredictions();
				for (MeasureType measure : QlMeasures) {
					evaluationResults = eval.evaluate(predictions, dataset.goldStandard, dataset.source.getAllUris(), dataset.target.getAllUris(), measure);
					
					datasetPairsEvaluations.put(dataset.pairName, evaluationResults); 
				}
				overallEvaluations.put(algorithm.getName(), datasetPairsEvaluations);

			}
		}
		
		return overallEvaluations;
		
	}*/
	
}
