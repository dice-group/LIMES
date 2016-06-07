package org.aksw.limes.core.evaluation.evaluator;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import org.aksw.limes.core.datastrutures.TaskAlgorithm;
import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.TaskData;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.QualitativeMeasuresEvaluator;
import org.aksw.limes.core.evaluation.quantitativeMeasures.IQuantitativeMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MappingFactory.MappingType;
import org.aksw.limes.core.ml.algorithm.ACoreMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.ActiveMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.Eagle;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.SupervisedMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.UnsupervisedMLAlgorithm;
import org.aksw.limes.core.ml.oldalgorithm.MLAlgorithm;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;
import org.aksw.limes.core.ml.setting.LearningParameters;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This evaluator is responsible for evaluating set of datasets that have source,target,goldstandard and mappings against set of measures
 *
 * @author mofeed
 * @version 1.0
 */
public class Evaluator {

    private QualitativeMeasuresEvaluator eval = new QualitativeMeasuresEvaluator();
    
    //remember
    //---------AMLAlgorithm(concrete:SupervisedMLAlgorithm,ActiveMLAlgorithm or UnsupervisedMLAlgorithm--------
    //---                                                                                              --------
    //---         ACoreMLAlgorithm (concrete: EAGLE,WOMBAT,LION) u can retrieve it by get()            --------
    //---                                                                                              --------
    //---------------------------------------------------------------------------------------------------------

    /**
     * @param algorithms:
     *         the set of algorithms used to generate the predicted mappings
     * @param datasets:
     *         the set of dataets to apply the algorithms on them. The should include source Cache, target Cache, goldstandard and predicted mapping
     * @param QlMeasures:
     *         set of qualitative measures
     * @param QnMeasures;
     *         set of quantitative measures
     * @return table contains the results corresponding to the algorithms and measures (algorithm,measure,{measure,value})
     */
    public Table<String, String, Map<EvaluatorType, Double>> evaluate(List<TaskAlgorithm> TaskAlgorithms, Set<TaskData> datasets ,Set<EvaluatorType> QlMeasures, Set<IQuantitativeMeasure> QnMeasures) {
        Table<String, String, Map<EvaluatorType, Double>> overallEvaluations = HashBasedTable.create(); // multimap stores aglortihmName:datasetname:List of evaluations

        AMapping predictions = null;
        Map<EvaluatorType, Double> evaluationResults = null;
        try{
            for (TaskAlgorithm tAlgorithm : TaskAlgorithms) {     //iterate over algorithms tasks(type,algorithm,parameter)
                for (TaskData dataset : datasets) {     //iterate over datasets(name,source,target,mapping,training,pseudofm)
                    tAlgorithm.getMlAlgorithm().init(tAlgorithm.getMlParameter(), dataset.source, dataset.target);//initialize the algorithm with source and target data, passing its parameters too
                    
                    ACoreMLAlgorithm ml = tAlgorithm.getMlAlgorithm().getMl(); //get the core machine learning working inside the algorithm
                    MLModel mlModel= null; // model resulting from the learning process
                    if(tAlgorithm.getMlType().equals(MLImplementationType.SUPERVISED_BATCH))
                    {
                        SupervisedMLAlgorithm sml =(SupervisedMLAlgorithm)tAlgorithm.getMlAlgorithm();
                        mlModel = sml.learn(dataset.training);
                        predictions = tAlgorithm.getMlAlgorithm().predict(dataset.source, dataset.target, mlModel);
                        evaluationResults = eval.evaluate(predictions, dataset.goldStandard, QlMeasures);
                        overallEvaluations.put(tAlgorithm.getMlAlgorithm().getName(), dataset.dataName, evaluationResults);
                    }
                    else if(tAlgorithm.getMlType().equals(MLImplementationType.SUPERVISED_ACTIVE))
                    {
                        ActiveMLAlgorithm sml =(ActiveMLAlgorithm)tAlgorithm.getMlAlgorithm();
                        mlModel = sml.activeLearn(dataset.training);
                        predictions = tAlgorithm.getMlAlgorithm().predict(dataset.source, dataset.target, mlModel);
                        evaluationResults = eval.evaluate(predictions, dataset.goldStandard, QlMeasures);
                        overallEvaluations.put(tAlgorithm.getMlAlgorithm().getName(), dataset.dataName, evaluationResults);
                    }
                    else if(tAlgorithm.getMlType().equals(MLImplementationType.UNSUPERVISED))
                    {
                        UnsupervisedMLAlgorithm sml =(UnsupervisedMLAlgorithm)tAlgorithm.getMlAlgorithm();
                        mlModel = sml.learn(dataset.pseudoFMeasure);
                        predictions = tAlgorithm.getMlAlgorithm().predict(dataset.source, dataset.target, mlModel);
                        evaluationResults = eval.evaluate(predictions, dataset.goldStandard, QlMeasures);
                        overallEvaluations.put(tAlgorithm.getMlAlgorithm().getName(), dataset.dataName, evaluationResults);
                    }

                }
            }
        }
        catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
        }

        return overallEvaluations;

    }

    /**
     * @param algo
     * @param datasets
     * @param folds
     * @param qlMeasures
     * @param qnMeasures
     * @return
     * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
     * @version 2016-02-26
     */
    public Table<String, String, Map<EvaluatorType, Double>> crossValidate(AMLAlgorithm algorithm, Set<TaskData> datasets,
            int folds, Set<EvaluatorType> qlMeasures, Set<IQuantitativeMeasure> qnMeasures) {

        Table<String, String, Map<EvaluatorType, Double>> evalTable = HashBasedTable.create();// multimap stores aglortihmName:datasetname:List of evaluations

        // select a dataset-pair to evaluate each ML algorithm on
        for (TaskData dataset : datasets) {

            Cache source = dataset.source;
            ArrayList<Instance> srcInstances = source.getAllInstances();
            AMapping mapping = dataset.mapping;
            AMapping goldstd = dataset.goldStandard.goldStandardMappings;

            // create source partitions: S into S1, .., Sk
            Cache[] srcParts = new Cache[folds];
            // create source folds (opposite of partitions)
            Cache[] srcFolds = new Cache[folds];
            // create mappings
            AMapping[] srcMap = new AMapping[folds];
            AMapping[] srcGold = new AMapping[folds];
            for (int i = 0; i < folds; i++) {
                srcParts[i] = new MemoryCache();
                srcFolds[i] = new MemoryCache();
                // AN: Changed the type of mapping as the input is a type, not the name
                // srcMap[i] = MappingFactory.createMapping(dataset.pairName + "_mapping_" + i);
                //srcGold[i] = MappingFactory.createMapping(dataset.pairName + "_goldstd_" + i);
                srcMap[i] = MappingFactory.createDefaultMapping();
                srcGold[i] = MappingFactory.createDefaultMapping();
            }

            // randomly distribute instances into #folds partitions
            for (Instance inst : srcInstances) {
                int destination;
                do {
                    destination = (int) (Math.random() * folds);
                } while (srcParts[destination].size() > source.size() / folds);
                srcParts[destination].addInstance(inst);

                // build folds
                for (int i = 0; i < folds; i++)
                    if (i != destination)
                        srcFolds[i].addInstance(inst);
            }

            // copy mapping entries into the one of the respective fold
            HashMap<String, HashMap<String, Double>> map = mapping.getMap();
            for (int i = 0; i < folds; i++) {
                for (Instance inst : srcParts[i].getAllInstances()) {
                    String uri = inst.getUri();
                    // look for (s, t) belonging to mapping and create G1, ..., G10
                    if (map.containsKey(uri))
                        srcMap[i].add(uri, map.get(uri));
                }
            }

            // copy gold standard entries into the one of the respective fold
            HashMap<String, HashMap<String, Double>> gst = goldstd.getMap();
            for (int i = 0; i < folds; i++) {
                for (Instance inst : srcParts[i].getAllInstances()) {
                    String uri = inst.getUri();
                    // look for (s, t) belonging to gold standard and create G1, ..., G10
                    if (gst.containsKey(uri))
                        srcGold[i].add(uri, gst.get(uri));
                }
            }

            //////////////
            GoldStandard goldStandard = new GoldStandard(null, dataset.source.getAllUris(), dataset.target.getAllUris());

            // train and test folds
            for (int i = 0; i < folds; i++) {
                
               // algorithm .setSourceCache(srcFolds[i]); 
                algorithm.init(null, srcFolds[i], null);
                // target cache is invariant
                MLModel model =null;
                //algorithm.learn(srcMap[i]);
                
                    try {
                        if(algorithm instanceof SupervisedMLAlgorithm)
                            model = algorithm.asSupervised().learn(srcMap[i]);
                        else if(algorithm instanceof ActiveMLAlgorithm)
                            model = algorithm.asActive().activeLearn(srcMap[i]);
                    } catch (UnsupportedMLImplementationException e) {
                        e.printStackTrace();
                    }

                
                goldStandard.goldStandardMappings = srcGold[i];
                evalTable.put(algorithm.getName() + " - fold " + i, dataset.dataName, eval.evaluate(algorithm.predict(srcFolds[i], srcFolds[i], model), goldStandard, qlMeasures));
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
