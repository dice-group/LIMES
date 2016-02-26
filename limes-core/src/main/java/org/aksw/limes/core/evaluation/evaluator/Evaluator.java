package org.aksw.limes.core.evaluation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.evaluation.MeasureType;
import org.aksw.limes.core.evaluation.quality.QualitativeMeasure;
import org.aksw.limes.core.evaluation.quantity.QuantitativeMeasuresEvaluator;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.MLAlgorithm;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 *
 */
public class Evaluator {

	private QuantitativeMeasuresEvaluator eval = new QuantitativeMeasuresEvaluator();
	
	public Table<String,String,Map<MeasureType, Double>> evaluate(Set<MLAlgorithm> algorithms, Set<DataSetsPair> datasets, Set<MeasureType> QlMeasures, Set<QualitativeMeasure> QnMeasures)
	{
		Table<String,String,Map<MeasureType, Double>> overallEvaluations = HashBasedTable.create();// multimap stores aglortihmName:datasetname:List of evaluations
		
		Mapping predictions=null;
		Map<MeasureType, Double> evaluationResults = null;
		
		for (MLAlgorithm algorithm : algorithms) {// select a ML algorithm
			for (DataSetsPair dataset : datasets) {// select a dataset-pair to evaluate each ML algorithm on
				
				algorithm.setSourceCache(dataset.source);
				algorithm.setTargetCache(dataset.target);
				// XXX ???
//				algorithm.learn(trainingData)
				predictions = algorithm.computePredictions();
				evaluationResults = eval.evaluate(predictions, dataset.goldStandard, dataset.source.getAllUris(), dataset.target.getAllUris(), QlMeasures);
				overallEvaluations.put(algorithm.getName(), dataset.pairName,evaluationResults);

			}
		}
	
		return overallEvaluations;
		
	}
	
	/**
	 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
	 * @version 2016-02-26
	 * 
	 * @param algo
	 * @param datasets
	 * @param folds
	 * @param qlMeasures
	 * @param qnMeasures
	 * @return
	 */
	public Table<String, String, Map<MeasureType, Double>> crossValidate(MLAlgorithm algorithm, Set<DataSetsPair> datasets, 
			int folds, Set<MeasureType> qlMeasures, Set<QualitativeMeasure> qnMeasures) {
		
		Table<String, String, Map<MeasureType, Double>> evalTable = HashBasedTable.create();// multimap stores aglortihmName:datasetname:List of evaluations
		
		// select a dataset-pair to evaluate each ML algorithm on
		for (DataSetsPair dataset : datasets) {
			
			Cache source = dataset.source;
			ArrayList<Instance> srcInstances = source.getAllInstances();
			Mapping mapping = dataset.mapping;
			Mapping goldstd = dataset.goldStandard;
			
			// create source partitions: S into S1, .., Sk
			Cache[] srcParts = new Cache[folds];
			// create source folds (opposite of partitions)
			Cache[] srcFolds = new Cache[folds];
			// create mappings
			Mapping[] srcMap = new Mapping[folds];
			Mapping[] srcGold = new Mapping[folds];
			for(int i=0; i<folds; i++) {
				srcParts[i] = new MemoryCache();
				srcFolds[i] = new MemoryCache();
				srcMap[i] = MappingFactory.createMapping(dataset.pairName + "_mapping_" + i);
				srcGold[i] = MappingFactory.createMapping(dataset.pairName + "_goldstd_" + i);
			}
			
			// randomly distribute instances into #folds partitions
			for(Instance inst : srcInstances) {
				int destination;
				do {
					destination = (int) (Math.random() * folds);
				} while(srcParts[destination].size() > source.size() / folds);
				srcParts[destination].addInstance(inst);
				
				// build folds
				for(int i=0; i<folds; i++)
					if(i != destination)
						srcFolds[i].addInstance(inst);
			}
			
			// copy mapping entries into the one of the respective fold
			HashMap<String, HashMap<String, Double>> map = mapping.getMap();
			for(int i=0; i<folds; i++) {
				for(Instance inst : srcParts[i].getAllInstances()) {
					String uri = inst.getUri();
					// look for (s, t) belonging to mapping and create G1, ..., G10
					if(map.containsKey(uri))
						srcMap[i].add(uri, map.get(uri));
				}	
			}

			// copy gold standard entries into the one of the respective fold
			HashMap<String, HashMap<String, Double>> gst = goldstd.getMap();
			for(int i=0; i<folds; i++) {
				for(Instance inst : srcParts[i].getAllInstances()) {
					String uri = inst.getUri();
					// look for (s, t) belonging to gold standard and create G1, ..., G10
					if(gst.containsKey(uri))
						srcGold[i].add(uri, gst.get(uri));
				}	
			}

			// train and test folds
			for(int i=0; i<folds; i++) {
				
				algorithm.setSourceCache(srcFolds[i]);
				// target cache is invariant
				
				algorithm.learn(srcMap[i]);
				
				evalTable.put(algorithm.getName() + " - fold "+i, 
						dataset.pairName,
						eval.evaluate(algorithm.computePredictions(), srcGold[i], dataset.source.getAllUris(), 
								dataset.target.getAllUris(), qlMeasures));
			}
		}
		
		return evalTable;
		
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
