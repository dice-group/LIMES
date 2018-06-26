package org.aksw.limes.core.ml.algorithm.fptld;

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
import org.aksw.limes.core.measures.mapper.CrispSetOperations;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions.GiniIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Eval {
	private static List<FoldData> folds = new ArrayList<>();
	private static int FOLDS_COUNT = 10;
	protected static Logger logger = LoggerFactory.getLogger(Eval.class);
	private static Random randnum = new Random();

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

	public static List<FoldData> generateFoldsDeterministically(EvaluationData data) {
		randnum.setSeed(423112);
		return generateFolds(data);
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

	public static void main(String[] args) {
		try {
			performCrossValidationEverything(args[0]);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void createDirectoriesIfNecessarry(String... folders) {
		for (String folder : folders) {
			File f = new File(folder);
			if (!f.exists()) {
				boolean success = f.mkdir();
				if (success) {
					logger.info("Successfully created directory: " + f.getPath());
				} else {
					logger.error("Error while trying to create: " + f.getPath());
				}
			} else {
				logger.info(f.getPath() + " already exists");
			}
		}
	}

	public static void performCrossValidationEverything(String baseFolder) throws FileNotFoundException {
		// ================================================================================================================
		// Set up output
		// ================================================================================================================
		long start;
		long end;
		String fMeasureBase = baseFolder + "FMeasure/";
		String precisionBase = baseFolder + "Precision/";
		String recallBase = baseFolder + "Recall/";
		String timeBase = baseFolder + "Time/";
		String sizeBase = baseFolder + "Size/";
		createDirectoriesIfNecessarry(baseFolder, fMeasureBase, precisionBase, recallBase, timeBase, sizeBase);

		String[] datasets = { "person1", "person2", "drugs", "restaurants", "dblpacm",
				"abtbuy", "dblpscholar", "amazongoogleproducts" };
		String header = "Data\tFPTLD\n";

		for (int k = 0; k < 10; k++) {

			PrintWriter writerFMeasure = new PrintWriter(new FileOutputStream(fMeasureBase + k + ".csv", false));
			writerFMeasure.write(header);
			String datalineFMeasure = "";
			PrintWriter writerPrecision = new PrintWriter(new FileOutputStream(precisionBase + k + ".csv", false));
			writerPrecision.write(header);
			String datalinePrecision = "";
			PrintWriter writerRecall = new PrintWriter(new FileOutputStream(recallBase + k + ".csv", false));
			writerRecall.write(header);
			String datalineRecall = "";
			PrintWriter writerTime = new PrintWriter(new FileOutputStream(timeBase + k + ".csv", false));
			writerTime.write(header);
			String datalineTime = "";
			PrintWriter writerSize = new PrintWriter(new FileOutputStream(sizeBase + k + ".csv", false));
			writerSize.write(header);
			String datalineSize = "";

			// ================================================================================================================
			// Set up training data folds and caches
			// ================================================================================================================

			for (String dataName : datasets) {
				logger.info("\n\n >>>>>>>>>>>>> " + dataName.toUpperCase() + "<<<<<<<<<<<<<<<<<\n\n");
				EvaluationData c = DataSetChooser.getData(dataName);
				folds = generateFolds(c);

				FoldData trainData = new FoldData();
				FoldData testData = folds.get(FOLDS_COUNT - 1);
				// perform union on test folds
				for (int i = 0; i < FOLDS_COUNT; i++) {
					if (i != 9) {
						trainData.map = CrispSetOperations.INSTANCE.union(trainData.map, folds.get(i).map);
						trainData.sourceCache = cacheUnion(trainData.sourceCache, folds.get(i).sourceCache);
						trainData.targetCache = cacheUnion(trainData.targetCache, folds.get(i).targetCache);
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

				// ================================================================================================================
				// Learning Phase
				// ================================================================================================================
				try {
					AMLAlgorithm fptld = null;
					Configuration config = null;
					MLResults res = null;
					AMapping mapping = null;

					GiniIndex.middlePoints = false;

					// ========================================
					logger.info("========FPTDL==========");

					fptld = MLAlgorithmFactory.createMLAlgorithm(FPTLD.class, MLImplementationType.SUPERVISED_BATCH);
					logger.info("source size: " + testSourceCache.size());
					logger.info("target size: " + testTargetCache.size());
					fptld.init(null, trainSourceCache, trainTargetCache);
					config = c.getConfigReader().read();
					fptld.getMl().setConfiguration(config);
					((FPTLD) fptld.getMl()).setParameter(FPTLD.PARAMETER_PROPERTY_MAPPING, c.getPropertyMapping());
					start = System.currentTimeMillis();
					res = fptld.asSupervised().learn(trainingData);
					end = System.currentTimeMillis();
					logger.info("LinkSpec: " + res.getLinkSpecification().toStringPretty());
					mapping = fptld.predict(testSourceCache, testTargetCache, res);
					double fm = new FMeasure().calculate(mapping,
							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
					double precision = new Precision().calculate(mapping,
							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
					double recall = new Recall().calculate(mapping,
							new GoldStandard(testData.map, testSourceCache.getAllUris(), testTargetCache.getAllUris()));
					int size = res.getLinkSpecification().size();
					logger.info("FMeasure: " + fm);
					logger.info("Precision: " + precision);
					logger.info("Recall: " + recall);
					long time = end - start;
					logger.info("Time: " + time);
					logger.info("Size: " + size);


					// ================================================================================================================
					// Print results for iteration
					// ================================================================================================================

					datalineFMeasure += dataName.replace("full", "").replace("products", "") + "\t" + fm + "\n";
					writerFMeasure.write(datalineFMeasure);
					datalineFMeasure = "";

					datalinePrecision += dataName.replace("full", "").replace("products", "") + "\t" + precision + "\n";
					writerPrecision.write(datalinePrecision);
					datalinePrecision = "";

					datalineRecall += dataName.replace("full", "").replace("products", "") + "\t" + recall + "\n";
					writerRecall.write(datalineRecall);
					datalineRecall = "";

					datalineTime += dataName.replace("full", "").replace("products", "") + "\t" + time + "\n";
					writerTime.write(datalineTime);
					datalineTime = "";

					datalineSize += dataName.replace("full", "").replace("products", "") + "\t" + size + "\n";
					writerSize.write(datalineSize);
					datalineSize = "";

				} catch (UnsupportedMLImplementationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			writerFMeasure.close();
			writerPrecision.close();
			writerRecall.close();
			writerTime.close();
			writerSize.close();
		}
	}

	public static ACache cacheUnion(ACache a, ACache b) {
		ACache result = new HybridCache();
		for (Instance i : a.getAllInstances()) {
			result.addInstance(i);
		}
		for (Instance i : b.getAllInstances()) {
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
		return generateMappingFolds(refMap, source, target, true);
	}

	public static List<AMapping> generateMappingFolds(AMapping refMap, ACache source, ACache target, boolean random) {
		Random rand;
		if (random) {
			rand = new Random();
		} else {
			rand = randnum;
		}
		List<AMapping> foldMaps = new ArrayList<>();
		int mapSize = refMap.getMap().keySet().size();
		int foldSize = mapSize / FOLDS_COUNT;

		Iterator<HashMap<String, Double>> it = refMap.getMap().values().iterator();
		ArrayList<String> values = new ArrayList<>();
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
					if (random) {
						number = (int) (mapSize * Math.random());
					} else {
						number = randnum.nextInt(mapSize - 1);
					}
				} while (index.contains(number));
				index.add(number);
			}
			// get data
			AMapping foldMap = MappingFactory.createDefaultMapping();
			int count = 0;
			for (String key : refMap.getMap().keySet()) {
				if (foldIndex != FOLDS_COUNT - 1) {
					if (index.contains(count) && count % 2 == 0) {
						HashMap<String, Double> help = new HashMap<>();
						for (String k : refMap.getMap().get(key).keySet()) {
							help.put(k, 1.0);
						}
						foldMap.getMap().put(key, help);
					} else if (index.contains(count)) {
						HashMap<String, Double> help = new HashMap<>();
						help.put(getRandomTargetInstance(source, target, values, rand, refMap.getMap(), key, -1), 0.0);
						foldMap.getMap().put(key, help);
					}
				} else {
					if (index.contains(count)) {
						HashMap<String, Double> help = new HashMap<>();
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
					HashMap<String, Double> help = new HashMap<>();
					for (String k : refMap.getMap().get(key).keySet()) {
						help.put(k, 1.0);
					}
					foldMaps.get(i).add(key, help);
				} else {

					HashMap<String, Double> help = new HashMap<>();
					help.put(getRandomTargetInstance(source, target, values, rand, refMap.getMap(), key, -1), 0.0);
					foldMaps.get(i).add(key, help);
				}
			} else {
				HashMap<String, Double> help = new HashMap<>();
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
