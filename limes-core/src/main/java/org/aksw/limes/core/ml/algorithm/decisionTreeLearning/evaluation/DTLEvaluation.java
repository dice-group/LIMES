package org.aksw.limes.core.ml.algorithm.decisionTreeLearning.evaluation;

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

public class DTLEvaluation {

	private static List<FoldData> folds = new ArrayList<>();
	private static double learningIterations = 25.0;
	private static int FOLDS_COUNT = 10;

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
	
	public static void performCrossvalidation() throws FileNotFoundException{
		long start;
		long end;
		String[] datasets = {
				"dbplinkedmdb", "person1full", "person2full","drugs", "restaurantsfull" };
	 for(int k = 0; k < 10; k++){
		PrintWriter writer = new PrintWriter(new FileOutputStream("/home/ohdorno/Documents/Arbeit/DecisionTrees/smallCrossValidation/smallresults"+k+".csv",false));
		String header = "Data\tWombat\tMandC\tGlobE\tGlobGl\tGiniGl\tGiniE\tj48\tj48opt\n";
		writer.write(header);
		String dataline = "";
		PrintWriter writerTime = new PrintWriter(new FileOutputStream("/home/ohdorno/Documents/Arbeit/DecisionTrees/smallCrossValidation/smallresultstime"+k+".csv",false));
		String headerTime = "Data\tWombat\tMandC\tGlobE\tGlobGl\tGiniGl\tGiniE\tj48\tj48opt\n";
		writerTime.write(headerTime);
		String datalineTime = "";

		for (String dataName : datasets) {
			DecisionTreeLearning.useJ48optimized = false;
			DecisionTreeLearning.useJ48 = false;
			EvaluationData c = DataSetChooser.getData(dataName);
			folds = generateFolds(c); 

			System.out.println("\n\n >>>>>>>>>>>>> " + dataName.toUpperCase() + "<<<<<<<<<<<<<<<<<\n\n");
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
			// fix cahces if necessary
			for (String s : trainData.map.getMap().keySet()) {
				for (String t : trainData.map.getMap().get(s).keySet()) {
					if (!trainData.targetCache.containsUri(t)) {
						// System.out.println("target: " + t);
						trainData.targetCache.addInstance(c.getTargetCache().getInstance(t));
					}
				}
				if (!trainData.sourceCache.containsUri(s)) {
					// System.out.println("source: " + s);
					trainData.sourceCache.addInstance(c.getSourceCache().getInstance(s));
				}
			}
			AMapping trainingData = trainData.map;
			ACache trainSourceCache = trainData.sourceCache;
			ACache trainTargetCache = trainData.targetCache;
			ACache testSourceCache = testData.sourceCache;
			ACache testTargetCache = testData.targetCache;

			try {
				AMLAlgorithm dtl = null;
				Configuration config = null;
				MLResults res = null;
				AMapping mapping = null;

				System.out.println("\n========WOMBAT==========");
				AMLAlgorithm wombat = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.SUPERVISED_BATCH);
				wombat.init(null, trainSourceCache, trainTargetCache);
				wombat.getMl().setConfiguration(c.getConfigReader().read());
				start = System.currentTimeMillis();
				MLResults resWom = wombat.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();

				double womFM = new FMeasure().calculate(wombat.predict(testSourceCache, testTargetCache, resWom),
								new GoldStandard(testData.map, testSourceCache, testTargetCache));
				System.out.println(resWom.getLinkSpecification());
				System.out.println("FMeasure: " + womFM);
				long womTime = (end - start);
				System.out.println("Time: " + womTime);

				System.out.println("========DTL==========");
				
				dtl  = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				System.out.println("source size: " + trainSourceCache.size());
				System.out.println("target size: " + trainTargetCache.size());
				dtl.init(null, trainSourceCache, trainTargetCache);
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
				start = System.currentTimeMillis();
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
				DecisionTreeLearning.useMergeAndConquer = true;
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
				System.out.println(res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(testSourceCache, testTargetCache, res);
				double MaCFM = new FMeasure().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				System.out.println( "\n\n==Merge and Conquer ==\nFMeasure: " + MaCFM);
				long MaCTime = (end - start);
				System.out.println("Time: " + MaCTime);
				DecisionTreeLearning.useMergeAndConquer = false;
//==================================
				
				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				System.out.println("source size: " + testSourceCache.size());
				System.out.println("target size: " + testTargetCache.size());
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
				System.out.println(res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(testSourceCache, testTargetCache, res);
				double GErFM = new FMeasure().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				System.out.println( "\n\n==Global + ErrorEstimate==\nFMeasure: " + GErFM);
				long GErTime = (end - start);
				System.out.println("Time: " + GErTime);
// ========================================
				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				System.out.println("source size: " + testSourceCache.size());
				System.out.println("target size: " + testTargetCache.size());
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
				System.out.println(res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(testSourceCache, testTargetCache, res);
				double GGFM = new FMeasure().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				System.out.println(
						"\n\n==Global + Global==\nFMeasure: " + GGFM);
				long GGTime = (end - start);
				System.out.println("Time: " + GGTime);

// ========================================
				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				System.out.println("source size: " + testSourceCache.size());
				System.out.println("target size: " + testTargetCache.size());
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
				System.out.println(res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(testSourceCache, testTargetCache, res);
				double giGFM = new FMeasure().calculate(mapping, new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				System.out.println( "\n\n==Gini + Global==\nFMeasure: " + giGFM);
				long giGTime = (end - start);
				System.out.println("Time: " + giGTime);
				
// ========================================
				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				System.out.println("source size: " + testSourceCache.size());
				System.out.println("target size: " + testTargetCache.size());
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
				System.out.println(res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(testSourceCache, testTargetCache, res);
				double giErFM = new FMeasure().calculate(mapping, new GoldStandard(testData.map,
								testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				System.out.println( "\n\n==Gini + ErrorEstimate==\nFMeasure: " + giErFM);
				long giErTime = (end - start);
				System.out.println("Time: " + giErTime);
// ========================================
				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				System.out.println("source size: " + testSourceCache.size());
				System.out.println("target size: " + testTargetCache.size());
				dtl.init(null, trainSourceCache, trainTargetCache);
				config = c.getConfigReader().read();
				dtl.getMl().setConfiguration(config);
				((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(c.getPropertyMapping());
				start = System.currentTimeMillis();
				dtl.getMl().setParameter(DecisionTreeLearning.PARAMETER_MAX_LINK_SPEC_HEIGHT, 3);
				DecisionTreeLearning.useJ48 = true;
				res = dtl.asSupervised().learn(trainingData);
				end = System.currentTimeMillis();
				System.out.println(res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(testSourceCache, testTargetCache, res);
				double j48FM = new FMeasure().calculate(mapping, new GoldStandard(testData.map,
								testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				System.out.println( "\n\n==J48==\nFMeasure: " + j48FM);
				long j48Time = (end - start);
				System.out.println("Time: " + j48Time);
// ========================================
				dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
						MLImplementationType.SUPERVISED_BATCH);
				System.out.println("source size: " + testSourceCache.size());
				System.out.println("target size: " + testTargetCache.size());
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
				System.out.println(res.getLinkSpecification().toStringPretty());
				mapping = dtl.predict(testSourceCache, testTargetCache, res);
				double j48optFM = new FMeasure().calculate(mapping, new GoldStandard(testData.map,
								testSourceCache.getAllUris(), testTargetCache.getAllUris()));
				System.out.println( "\n\n==j48optimized==\nFMeasure: " + j48optFM);
				long j48optTime = (end - start);
				System.out.println("Time: " + j48optTime);
				DecisionTreeLearning.useJ48 = false;
				DecisionTreeLearning.useJ48optimized = false;
				dataline += dataName + "\t"+ womFM + "\t" +MaCFM + "\t" + GErFM + "\t" + GGFM + "\t" + giGFM + "\t" + giErFM +"\t" + j48FM + "\t" + j48optFM + "\n";
				writer.write(dataline);
				dataline = "";

				datalineTime += dataName + "\t"+ womTime + "\t" +MaCTime + "\t" + GErTime + "\t" + GGTime + "\t" + giGTime + "\t" + giErTime +"\t" + j48Time + "\t" + j48optTime + "\n";
				writerTime.write(datalineTime);
				datalineTime = "";
			} catch (UnsupportedMLImplementationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		writer.close();
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

	/**
	 * Helps to create random mappings that are balanced
	 * 
	 * @param source
	 * @param target
	 * @param values
	 * @param random
	 * @param refMap
	 * @param sourceInstance
	 * @param previousRandom
	 * @return
	 */
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

	/**
	 * divide the input refMap to foldCount foldMaps Note: refMap will be empty
	 * at the end
	 * 
	 * @param refMap
	 * @param foldCount
	 * @return
	 */
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

	/**
	 * Removes subMap from mainMap
	 * 
	 * @param mainMap
	 * @param subMap
	 * @param useScores
	 * @return subMap = mainMap / subMap
	 * @author sherif
	 */
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
