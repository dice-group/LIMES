package org.aksw.limes.core.evaluation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.datastrutures.Task;
import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.QualitativeMeasuresEvaluator;
import org.aksw.limes.core.evaluation.quantitativeMeasures.IQuantitativeMeasure;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MappingFactory.MappingType;
import org.aksw.limes.core.ml.algorithm.MLAlgorithm;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * This evaluator is responsible for evaluating set of datasets that have source,target,goldstandard and mappings against set of measures
 * @author mofeed
 * @version 1.0
 */
public class Evaluator {

	private QualitativeMeasuresEvaluator eval = new QualitativeMeasuresEvaluator();
	
	/**
	 * @param algorithms: the set of algorithms used to generate the predicted mappings
	 * @param datasets: the set of dataets to apply the algorithms on them. The should include source Cache, target Cache, goldstandard and predicted mapping
	 * @param QlMeasures: set of qualitative measures
	 * @param QnMeasures; set of quantitative measures
	 * @return table contains the results corresponding to the algorithms and measures
	 */
	public Table<String,String,Map<MeasureType, Double>> evaluate(Set<MLAlgorithm> algorithms, Set<Task> datasets, Set<MeasureType> QlMeasures, Set<IQuantitativeMeasure> QnMeasures)
	{
		Table<String,String,Map<MeasureType, Double>> overallEvaluations = HashBasedTable.create();// multimap stores aglortihmName:datasetname:List of evaluations
		
		Mapping predictions=null;
		Map<MeasureType, Double> evaluationResults = null;
		
		for (MLAlgorithm algorithm : algorithms) {// select a ML algorithm
			for (Task dataset : datasets) {// select a dataset-pair to evaluate each ML algorithm on
				algorithm.setSourceCache(dataset.source);
				algorithm.setTargetCache(dataset.target);
				// XXX ???
//				algorithm.learn(trainingData)
				predictions = algorithm.computePredictions();
				/////////prepare gold standard information
				GoldStandard goldstandard = new GoldStandard(dataset.goldStandard,dataset.source.getAllUris(),dataset.target.getAllUris());
		
				evaluationResults = eval.evaluate(predictions,goldstandard, QlMeasures);
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
	public Table<String, String, Map<MeasureType, Double>> crossValidate(MLAlgorithm algorithm, Set<Task> datasets, 
			int folds, Set<MeasureType> qlMeasures, Set<IQuantitativeMeasure> qnMeasures) {
		
		Table<String, String, Map<MeasureType, Double>> evalTable = HashBasedTable.create();// multimap stores aglortihmName:datasetname:List of evaluations
		
		// select a dataset-pair to evaluate each ML algorithm on
		for (Task dataset : datasets) {
			
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
				// AN: Changed the type of mapping as the input is a type, not the name
				// srcMap[i] = MappingFactory.createMapping(dataset.pairName + "_mapping_" + i);
				//srcGold[i] = MappingFactory.createMapping(dataset.pairName + "_goldstd_" + i);
				srcMap[i] = MappingFactory.createMapping(MappingType.MEMORY_MAPPING);
				srcGold[i] = MappingFactory.createMapping(MappingType.MEMORY_MAPPING);
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

			//////////////
			GoldStandard goldStandard = new GoldStandard(null,dataset.source.getAllUris(),dataset.target.getAllUris());
			
			// train and test folds
			for(int i=0; i<folds; i++) {
				
				algorithm.setSourceCache(srcFolds[i]);
				// target cache is invariant
				
				algorithm.learn(srcMap[i]);
				goldStandard.goldStandard = srcGold[i];
				evalTable.put(algorithm.getName() + " - fold "+i,dataset.pairName,eval.evaluate(algorithm.computePredictions(), goldStandard, qlMeasures));
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
