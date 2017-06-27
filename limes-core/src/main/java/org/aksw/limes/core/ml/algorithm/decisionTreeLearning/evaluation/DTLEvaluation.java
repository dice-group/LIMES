package org.aksw.limes.core.ml.algorithm.decisionTreeLearning.evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Precision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Recall;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.DecisionTreeLearning;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.FitnessFunctions.GiniIndex;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.FitnessFunctions.GlobalFMeasure;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.Pruning.ErrorEstimatePruning;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.Pruning.GlobalFMeasurePruning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DTLEvaluation {

	private static List<FoldData> folds = new ArrayList<>();
	private static int FOLDS_COUNT = 10;
	protected static Logger logger = LoggerFactory.getLogger(DTLEvaluation.class);

	public static List<FoldData> generateFolds(EvaluationData data) {
		folds = new ArrayList<>();

		// Fill caches
		ACache source = data.getSourceCache();
		ACache target = data.getTargetCache();
		AMapping refMap = data.getReferenceMapping();

		// remove error mappings (if any)
		refMap = removeLinksWithNoInstances(refMap, source, target);

		// generate AMapping folds
		List<AMapping> foldMaps = generateMappingFolds(refMap, source, target);

		// fill fold caches
		for (AMapping foldMap : foldMaps) {
			ACache sourceFoldCache = new HybridCache();
			ACache targetFoldCache = new HybridCache();
			for (String s : foldMap.getMap().keySet()) {
				if (source.containsUri(s)) {
					sourceFoldCache.addInstance(source.getInstance(s));
					for (String t : foldMap.getMap().get(s).keySet()) {
						if (target.containsUri(t)) {
							targetFoldCache.addInstance(target.getInstance(t));
						} else {
							// logger.warn("Instance " + t +
							// " not exist in the target dataset");
						}
					}
				} else {
					// logger.warn("Instance " + s +
					// " not exist in the source dataset");
				}
			}
			folds.add(new FoldData(foldMap, sourceFoldCache, targetFoldCache));
		}
		return folds;
	}

	/**
	 * Labels prediction mapping according referencemapping
	 * 
	 * @param predictionMapping
	 * @param referenceMapping
	 * @return result labeled mapping
	 */
	public static AMapping oracleFeedback(AMapping predictionMapping, AMapping referenceMapping) {
		AMapping result = MappingFactory.createDefaultMapping();

		for (String s : predictionMapping.getMap().keySet()) {
			for (String t : predictionMapping.getMap().get(s).keySet()) {
				if (referenceMapping.contains(s, t)) {
					// result.add(s, t,
					// predictionMapping.getMap().get(s).get(t));
					result.add(s, t, 1.0);
				} else {
					result.add(s, t, 0.0);
				}
			}
		}
		return result;
	}
	
	public static void main(String[] args){
		try {
			performCrossvalidation();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void createDirectoriesIfNecessarry(String ... folders){
		for(String folder: folders){
			File f = new File(folder);
			if(!f.exists()){
				f.mkdir();
			}
		}
	}
	
	public static void performCrossvalidation() throws FileNotFoundException{

//================================================================================================================
//			Set up output
//================================================================================================================
		long start;
		long end;
		String baseFolder = "/tmp/";
		String fMeasureBase = baseFolder + "FMeasure/";
		String precisionBase = baseFolder + "Precision/";
		String recallBase = baseFolder + "Recall/";
		String timeBase = baseFolder + "Time/";
		createDirectoriesIfNecessarry(baseFolder, fMeasureBase, precisionBase, recallBase, timeBase);
		
		String[] datasets = {"dbplinkedmdb", "person1full", "person2full","drugs", "restaurantsfull", "dblpacm", "abtbuy", "dblpscholar", "amazongoogleproducts"};
		String header = "Data\tWombat\tMergeAndConquer\tGlobal+ErrorEstimate\tGlobal+Global\tGini+Global\tGini+ErrorEstimate\tj48\tj48opt\n";
		
		

	 for(int k = 0; k < 10; k++){

		PrintWriter writerFMeasure = new PrintWriter(new FileOutputStream(fMeasureBase +k+ ".csv",false));
		writerFMeasure.write(header);
		String datalineFMeasure = "";
		PrintWriter writerPrecision = new PrintWriter(new FileOutputStream(precisionBase +k+ ".csv",false));
		writerPrecision.write(header);
		String datalinePrecision = "";
		PrintWriter writerRecall = new PrintWriter(new FileOutputStream(recallBase +k+ ".csv",false));
		writerRecall.write(header);
		String datalineRecall = "";
		PrintWriter writerTime = new PrintWriter(new FileOutputStream(timeBase +k+ ".csv",false));
		writerTime.write(header);
		String datalineTime = "";


//================================================================================================================
//			Set up training data folds and caches
//================================================================================================================

		for (String dataName : datasets) {
			logger.info("\n\n >>>>>>>>>>>>> " + dataName.toUpperCase() + "<<<<<<<<<<<<<<<<<\n\n");
			DecisionTreeLearning.useJ48optimized = false;
			DecisionTreeLearning.useJ48 = false;
			EvaluationData c = DataSetChooser.getData(dataName);
			folds = generateFolds(c); 

			FoldData trainData = new FoldData();
			FoldData testData = folds.get(FOLDS_COUNT - 1);
			// perform union on test folds
			for (int i = 0; i < FOLDS_COUNT; i++) {
				if (i != 9) {
					trainData.map = MappingOperations.union(trainData.map,folds.get(i).map);
					trainData.sourceCache = cacheUnion(trainData.sourceCache,folds.get(i).sourceCache);
					trainData.targetCache = cacheUnion(trainData.targetCache,folds.get(i).targetCache);
				}
			}
			// fix caches if necessary
			for (String s : trainData.map.getMap().keySet()) {
				for (String t : trainData.map.getMap().get(s).keySet()) {
					if (!trainData.targetCache.containsUri(t)) {
						// logger.info("target: " + t);
						trainData.targetCache.addInstance(c.getTargetCache().getInstance(t));
					}
				}
				if (!trainData.sourceCache.containsUri(s)) {
					// logger.info("source: " + s);
					trainData.sourceCache.addInstance(c.getSourceCache().getInstance(s));
				}
			}
			

			AMapping trainingData = trainData.map;
			ACache trainSourceCache = trainData.sourceCache;
			ACache trainTargetCache = trainData.targetCache;
			ACache testSourceCache = testData.sourceCache;
			ACache testTargetCache = testData.targetCache;

//================================================================================================================
//			Learning Phase
//================================================================================================================
			try {
				AMLAlgorithm dtl = null;
				Configuration config = null;
				MLResults res = null;
				AMapping mapping = null;

				logger.info("\n========WOMBAT==========");
				AMLAlgorithm wombat = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.SUPERVISED_BATCH);
				wombat.init(null, trainSourceCache, trainTargetCache);
				wombat.getMl().setConfiguration(c.getConfigReader().read());
				start = System.currentTimeMillis();
				res = wombat.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();

				logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
				mapping = wombat.predict(testSourceCache, testTargetCache, res);
				double womFM = new FMeasure().calculate(mapping, new GoldStandard(testData.map, testSourceCache, testTargetCache));
				double womPrecision = new Precision().calculate(mapping, new GoldStandard(testData.map, testSourceCache, testTargetCache));
				double womRecall = new Recall().calculate(mapping, new GoldStandard(testData.map, testSourceCache, testTargetCache));
				logger.info("FMeasure: " + womFM);
				logger.info("Precision: " + womPrecision);
				logger.info("Recall: " + womRecall);
				long womTime = (end - start);
				logger.info("Time: " + womTime);

				logger.info("========Merge and Conquer==========");
				
				dtl  = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				logger.info("source size: " + trainSourceCache.size());
				logger.info("target size: " + trainTargetCache.size());
				dtl.init(null, trainSourceCache, trainTargetCache);
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
				start = System.currentTimeMillis();
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
				DecisionTreeLearning.useMergeAndConquer = true;
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
				logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(testSourceCache, testTargetCache, res);
				double MaCFM = new FMeasure().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				double MaCPrecision = new Precision().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				double MaCRecall = new Recall().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				logger.info("FMeasure: " + MaCFM);
				logger.info("Precision: " + MaCPrecision);
				logger.info("Recall: " + MaCRecall);
				long MaCTime = (end - start);
				logger.info("Time: " + MaCTime);
				DecisionTreeLearning.useMergeAndConquer = false;
//==================================
				
				logger.info("========Global + ErrorEstimate==========");

				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				logger.info("source size: " + testSourceCache.size());
				logger.info("target size: " + testTargetCache.size());
				dtl.init(null, trainSourceCache, trainTargetCache);
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
				start = System.currentTimeMillis();
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_FITNESS_FUNCTION, new GlobalFMeasure());
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_PRUNING_FUNCTION, new ErrorEstimatePruning());
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
				logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(testSourceCache, testTargetCache, res);
				double GErFM = new FMeasure().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				double GErPrecision = new Precision().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				double GErRecall = new Recall().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				logger.info("FMeasure: " + GErFM);
				logger.info("Precision: " + GErPrecision);
				logger.info("Recall: " + GErRecall);
				long GErTime = (end - start);
				logger.info("Time: " + GErTime);
// ========================================

				logger.info("========Global + Global==========");

				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				logger.info("source size: " + testSourceCache.size());
				logger.info("target size: " + testTargetCache.size());
				dtl.init(null, trainSourceCache, trainTargetCache);
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
				start = System.currentTimeMillis();
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_FITNESS_FUNCTION, new GlobalFMeasure());
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_PRUNING_FUNCTION, new GlobalFMeasurePruning());
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
				logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(testSourceCache, testTargetCache, res);
				double GGFM = new FMeasure().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				double GGPrecision = new Precision().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				double GGRecall = new Recall().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				logger.info("FMeasure: " + GGFM);
				logger.info("Precision: " + GGPrecision);
				logger.info("Recall: " + GGRecall);
				long GGTime = (end - start);
				logger.info("Time: " + GGTime);

// ========================================
				logger.info("========Global + Gini==========");

				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				logger.info("source size: " + testSourceCache.size());
				logger.info("target size: " + testTargetCache.size());
				dtl.init(null, trainSourceCache, trainTargetCache);
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
				start = System.currentTimeMillis();
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_PRUNING_FUNCTION, new GlobalFMeasurePruning());
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
				logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(testSourceCache, testTargetCache, res);
				double giGFM = new FMeasure().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				double giGPrecision = new Precision().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				double giGRecall = new Recall().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				logger.info("FMeasure: " + giGFM);
				logger.info("Precision: " + giGPrecision);
				logger.info("Recall: " + giGRecall);
				long giGTime = (end - start);
				logger.info("Time: " + giGTime);
				
// ========================================

				logger.info("========Gini + ErrorEstimate==========");

				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				logger.info("source size: " + testSourceCache.size());
				logger.info("target size: " + testTargetCache.size());
				dtl.init(null, trainSourceCache, trainTargetCache);
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
				start = System.currentTimeMillis();
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_FITNESS_FUNCTION, new GiniIndex());
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_PRUNING_FUNCTION, new ErrorEstimatePruning());
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
				logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(testSourceCache, testTargetCache, res);
				double giErFM = new FMeasure().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				double giErPrecision = new Precision().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				double giErRecall = new Recall().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				logger.info("FMeasure: " + giErFM);
				logger.info("Precision: " + giErPrecision);
				logger.info("Recall: " + giErRecall);
				long giErTime = (end - start);
				logger.info("Time: " + giErTime);
// ========================================

				logger.info("========J48==========");

				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				logger.info("source size: " + testSourceCache.size());
				logger.info("target size: " + testTargetCache.size());
				dtl.init(null, trainSourceCache, trainTargetCache);
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
				start = System.currentTimeMillis();
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
				DecisionTreeLearning.useJ48 = true;
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
				logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(testSourceCache, testTargetCache, res);
				double j48FM = new FMeasure().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				double j48Precision = new Precision().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				double j48Recall = new Recall().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				logger.info("FMeasure: " + j48FM);
				logger.info("Precision: " + j48Precision);
				logger.info("Recall: " + j48Recall);
				long j48Time = (end - start);
				logger.info("Time: " + j48Time);
// ========================================

				logger.info("========J48 optimized==========");

				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				logger.info("source size: " + testSourceCache.size());
				logger.info("target size: " + testTargetCache.size());
				dtl.init(null, trainSourceCache, trainTargetCache);
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
				start = System.currentTimeMillis();
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
				DecisionTreeLearning.useJ48 = true;
				DecisionTreeLearning.useJ48optimized = true;
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
				logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(testSourceCache, testTargetCache, res);
				double j48optFM = new FMeasure().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				double j48optPrecision = new Precision().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				double j48optRecall = new Recall().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				logger.info("FMeasure: " + j48optFM);
				logger.info("Precision: " + j48optPrecision);
				logger.info("Recall: " + j48optRecall);
				long j48optTime = (end - start);
				logger.info("Time: " + j48optTime);
				DecisionTreeLearning.useJ48 = false;
				DecisionTreeLearning.useJ48optimized = false;
				
				
//================================================================================================================
//		Print results for iteration	
//================================================================================================================
				
				datalineFMeasure += dataName + "\t"+ womFM + "\t" +MaCFM + "\t" + GErFM + "\t" + GGFM + "\t" + giGFM + "\t" + giErFM +"\t" + j48FM + "\t" + j48optFM + "\n";
				writerFMeasure.write(datalineFMeasure);
				datalineFMeasure = "";

				datalinePrecision += dataName + "\t"+ womPrecision + "\t" +MaCPrecision + "\t" + GErPrecision + "\t" + GGPrecision + "\t" + giGPrecision + "\t" + giErPrecision +"\t" + j48Precision + "\t" + j48optPrecision + "\n";
				writerPrecision.write(datalinePrecision);
				datalinePrecision = "";

				datalineRecall += dataName + "\t"+ womRecall + "\t" +MaCRecall + "\t" + GErRecall + "\t" + GGRecall + "\t" + giGRecall + "\t" + giErRecall +"\t" + j48Recall + "\t" + j48optRecall + "\n";
				writerRecall.write(datalineRecall);
				datalineRecall = "";

				datalineTime += dataName + "\t"+ womTime + "\t" +MaCTime + "\t" + GErTime + "\t" + GGTime + "\t" + giGTime + "\t" + giErTime +"\t" + j48Time + "\t" + j48optTime + "\n";
				writerTime.write(datalineTime);
				datalineTime = "";
			} catch (UnsupportedMLImplementationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		writerFMeasure.close();
		writerPrecision.close();
		writerRecall.close();
		writerTime.close();
		}
	}
	
	
    private static ACache cacheUnion(ACache a, ACache b) {
    	ACache result = new HybridCache(); 
        for(Instance i : a.getAllInstances()){
        	result.addInstance(i);
        }
        for(Instance i : b.getAllInstances()){
        	result.addInstance(i);
        }
        return result;
    }

	public static String getRandomTargetInstance(ACache source, ACache target, List<String> values, Random random,
			HashMap<String, HashMap<String, Double>> refMap, String sourceInstance, int previousRandom) {
		int randomInt;
		do {
			randomInt = random.nextInt(values.size());
		} while (randomInt == previousRandom);

		String tmpTarget = values.get(randomInt);
		if (refMap.get(sourceInstance).get(tmpTarget) == null && target.getInstance(tmpTarget) != null) {
			return tmpTarget;
		}
		return getRandomTargetInstance(source, target, values, random, refMap, sourceInstance, randomInt);
	}


	public static List<AMapping> generateMappingFolds(AMapping refMap, ACache source, ACache target) {
		Random rand = new Random();
		List<AMapping> foldMaps = new ArrayList<>();
		int mapSize = refMap.getMap().keySet().size();
		int foldSize = (int) (mapSize / FOLDS_COUNT);

		Iterator<HashMap<String, Double>> it = refMap.getMap().values().iterator();
		ArrayList<String> values = new ArrayList<String>();
		while (it.hasNext()) {
			for (String t : it.next().keySet()) {
				values.add(t);
			}
		}
		for (int foldIndex = 0; foldIndex < FOLDS_COUNT; foldIndex++) {
			Set<Integer> index = new HashSet<>();
			// get random indexes
			while (index.size() < foldSize) {
				int number;
				do {
					number = (int) (mapSize * Math.random());
				} while (index.contains(number));
				index.add(number);
			}
			// get data
			AMapping foldMap = MappingFactory.createDefaultMapping();
			int count = 0;
			for (String key : refMap.getMap().keySet()) {
				if (foldIndex != FOLDS_COUNT - 1) {
					if (index.contains(count) && count % 2 == 0) {
						HashMap<String, Double> help = new HashMap<String, Double>();
						for (String k : refMap.getMap().get(key).keySet()) {
							help.put(k, 1.0);
						}
						foldMap.getMap().put(key, help);
					} else if (index.contains(count)) {
						HashMap<String, Double> help = new HashMap<String, Double>();
						help.put(getRandomTargetInstance(source, target, values, rand, refMap.getMap(), key, -1), 0.0);
						foldMap.getMap().put(key, help);
					}
				} else {
					if (index.contains(count)) {
						HashMap<String, Double> help = new HashMap<String, Double>();
						for (String k : refMap.getMap().get(key).keySet()) {
							help.put(k, 1.0);
						}
						foldMap.getMap().put(key, help);
					}
				}
				count++;
			}

			foldMaps.add(foldMap);
			refMap = removeSubMap(refMap, foldMap);
		}
		int i = 0;
		int odd = 0;
		// if any remaining links in the refMap, then distribute them to all
		// folds
		for (String key : refMap.getMap().keySet()) {
			if (i != FOLDS_COUNT - 1) {
				if (odd % 2 == 0) {
					HashMap<String, Double> help = new HashMap<String, Double>();
					for (String k : refMap.getMap().get(key).keySet()) {
						help.put(k, 1.0);
					}
					foldMaps.get(i).add(key, help);
				} else {

					HashMap<String, Double> help = new HashMap<String, Double>();
					help.put(getRandomTargetInstance(source, target, values, rand, refMap.getMap(), key, -1), 0.0);
					foldMaps.get(i).add(key, help);
				}
			} else {
				HashMap<String, Double> help = new HashMap<String, Double>();
				for (String k : refMap.getMap().get(key).keySet()) {
					help.put(k, 1.0);
				}
				foldMaps.get(i).add(key, help);

			}
			odd++;
			i = (i + 1) % FOLDS_COUNT;
		}
		return foldMaps;
	}

	public static AMapping removeSubMap(AMapping mainMap, AMapping subMap) {
		AMapping result = MappingFactory.createDefaultMapping();
		double value = 0;
		for (String mainMapSourceUri : mainMap.getMap().keySet()) {
			for (String mainMapTargetUri : mainMap.getMap().get(mainMapSourceUri).keySet()) {
				if (!subMap.contains(mainMapSourceUri, mainMapTargetUri)) {
					result.add(mainMapSourceUri, mainMapTargetUri, value);
				}
			}
		}
		return result;
	}

	protected static AMapping removeLinksWithNoInstances(AMapping map, ACache source, ACache target) {
		AMapping result = MappingFactory.createDefaultMapping();
		for (String s : map.getMap().keySet()) {
			for (String t : map.getMap().get(s).keySet()) {
				if (source.containsUri(s) && target.containsUri(t)) {
					result.add(s, t, map.getMap().get(s).get(t));
				}
			}
		}
		return result;
	}

}
