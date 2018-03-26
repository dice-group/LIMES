package org.aksw.limes.core.ml.algorithm.ligon;

import com.google.common.io.Files;
import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Precision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Recall;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory.ExecutionEngineType;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory.ExecutionPlannerType;
import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.execution.rewriter.Rewriter;
import org.aksw.limes.core.execution.rewriter.RewriterFactory;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.*;
import org.aksw.limes.core.ml.algorithm.wombat.AWombat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * @author mohamedsherif
 *
 */
@SuppressWarnings("Duplicates")
public class Ligon {
	static Logger logger = LoggerFactory.getLogger(Ligon.class);

	private long t_eval;

	String resultStr = "";

	protected List<NoisyOracle> blackBoxOracles;
	protected List<NoisyOracle> estimatedOracles;

	protected List<AMapping> lastOracleResponses = new ArrayList<>();

	private File logOut = null;
	private int iteration = 1;

	public enum ODDS {
		IGNORE, RANDOM, EQUIVALENCE, APPROXIMATE
	}

	double randomOddL = 0.5;

	ACache sourceTrainCache = null;
	ACache targetTrainCache = null;

	// Only for evaluation
	ACache fullSourceCache = null;
	ACache fullTargetCache = null;

	ACache sourceTestingCache = null;
	ACache targetTestingCache = null;

	AMapping testReferenceMap = null;
	AMapping fullReferenceMap = null;

	protected ActiveMLAlgorithm activeWombat = null;
	protected double wombatBestFmeasure = 1.0;


	/**
	 * Simplest contractor
	 *
	 * @param blackBoxOracles
	 */
	public Ligon(List<NoisyOracle> blackBoxOracles) {
		super();
		this.blackBoxOracles = blackBoxOracles;
		this.estimatedOracles = new ArrayList<>();
		for (NoisyOracle o : blackBoxOracles) {
			this.estimatedOracles.add(new NoisyOracle());
			lastOracleResponses.add(MappingFactory.createDefaultMapping());
		}
	}

	/**
	 * Complex constructor for evaluation
	 *
	 * @param blackBoxOracles
	 */
	public Ligon(List<NoisyOracle> blackBoxOracles,
			ACache fullSourceCache,
			ACache fullTargetCache,
			AMapping fullReferenceMapping) {
		this(blackBoxOracles);
		this.fullSourceCache = fullSourceCache;
		this.fullTargetCache = fullTargetCache;
		this.fullReferenceMap = fullReferenceMapping;
	}


	public Ligon(List<NoisyOracle> blackBoxOracles,
			ACache fullSourceCache,
			ACache fullTargetCache,
			AMapping fullReferenceMapping,
			ACache sourceTestingCache,
			ACache targetTestingCache,
			AMapping testReferenceMap) {
		this(blackBoxOracles, fullSourceCache, fullTargetCache, fullReferenceMapping);
		this.sourceTestingCache = sourceTestingCache;
		this.targetTestingCache = targetTestingCache;
		this.testReferenceMap = testReferenceMap;
	}


	/**
	 * Process 1 link by all black-box oracles
	 *
	 * @param subject
	 * @param object
	 * @return vector of all black-box oracles results
	 */
	protected List<Boolean> getOracleFeedback(String subject, String object) {
		List<Boolean> result = new ArrayList<>();
		for (int i = 0; i < blackBoxOracles.size(); i++) {
			result.set(i, blackBoxOracles.get(i).predict(subject, object));
		}
		return result;
	}


	public void gatherOracleResponses(AMapping labeledExamples) {
		for (String subject : labeledExamples.getMap().keySet()) {
			for (String object : labeledExamples.getMap().get(subject).keySet()) {
				for (int i = 0; i < blackBoxOracles.size(); i++) {
					lastOracleResponses.get(i).add(subject, object, blackBoxOracles.get(i).predict(subject, object) ? 1d : 0d);
				}
			}
		}
	}

	public void updateOraclesConfusionMatrices(AMapping labeledExamples) {
		for (String subject : labeledExamples.getMap().keySet()) {
			for (String object : labeledExamples.getMap().get(subject).keySet()) {
				double confidence = labeledExamples.getConfidence(subject, object);
				for (int i = 0; i < lastOracleResponses.size(); i++) {
					AMapping oracleMap = lastOracleResponses.get(i);
					ConfusionMatrix confMat = estimatedOracles.get(i).confusionMatrix;
					if (confidence == 1.0d) { 									// Positive example
						if (oracleMap.getConfidence(subject, object) == 1.0d) { 	// True prediction
							confMat.incrementTruePositiveCount();
						} else { 												// False prediction
							confMat.incrementFalsePositiveCount();
						}
					} else if (confidence == 0.0d) { 							// Negative example
						if (oracleMap.getConfidence(subject, object) == 0.0d) { 	// True prediction
							confMat.incrementTrueNegativeCount();   
						} else { 												// False prediction
							confMat.incrementFalseNegativeCount(); 
						}
					}
				}
			}
		}
		logConfusionMatrixes();
	}

	private void logConfusionMatrixes() {
		try {
			if (logOut == null) {
				logOut = new File("./" + EvaluateLigon.datasetName + "_" + blackBoxOracles.size() + "_cm.txt");
				String out = "it";
				for (int i = 0; i < blackBoxOracles.size(); i++) {
					out += "\tbb"+i+"-TT\tbb"+i+"-TF\tbb"+i+"-FT\tbb"+i+"-FF\test"+i+"-TT\test"+i+"-TF\test"+i+"-FT\test"+i+"-FF";
				}
				Files.write(out + "\n", logOut, Charset.defaultCharset());
			} else {
				String out = iteration + "";
				for (int i = 0; i < blackBoxOracles.size(); i++) {
					ConfusionMatrix bb = blackBoxOracles.get(i).confusionMatrix;
					ConfusionMatrix est = estimatedOracles.get(i).confusionMatrix;
					out += "\t" + bb.getTruePositiveProbability()
					+ "\t" + bb.getFalsePositiveProbability()
					+ "\t" + bb.getFalseNegativeProbability()
					+ "\t" + bb.getTrueNegativeProbability()
					+ "\t" + est.getTruePositiveProbability()
					+ "\t" + est.getFalsePositiveProbability()
					+ "\t" + est.getFalseNegativeProbability()
					+ "\t" + est.getTrueNegativeProbability();
				}
				Files.append(out + "\n", logOut, Charset.defaultCharset());
				iteration++;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public double computeOdds(String subject, String object, ODDS oddsStrategy) {
		double result = 0.0d;
		for (int i = 0; i < lastOracleResponses.size(); i++) {
			AMapping x = lastOracleResponses.get(i);
			ConfusionMatrix est = estimatedOracles.get(0).confusionMatrix;
			if (x.getConfidence(subject, object) == 1.0d) {
				result += Math.log(est.getTruePositiveProbability()) - Math.log(est.getFalsePositiveProbability()); 
			} else {
				result +=  Math.log(est.getFalseNegativeProbability()) - Math.log(est.getTrueNegativeProbability());
				//                result +=  Math.log(est.getTrueNegativeProbability() -Math.log(est.getFalseNegativeProbability()));
			}

		}
		//        for (int i = 0; i < lastOracleResponses.size(); i++) {
		//            AMapping x = lastOracleResponses.get(i);
		//            ConfusionMatrix est = estimatedOracles.get(0).confusionMatrix;
		//            if (x.getConfidence(subject, object) == 1.0d) {
		//                result += Math.log(est.getRightClassifiedPositiveExamplesProbability()) - Math.log(est.getWrongClassifiedNegativeExamplesProbability());
		//            } else {
		//                result += Math.log(est.getWrongClassifiedPositiveExamplesProbability()) - Math.log(est.getRightClassifiedNegativeExamplesProbability());
		//            }
		//
		//        }
		//
		//        for (int i = 0; i < estimatedOracles.size(); i++) {
		//            result +=
		//                    Math.log(estimatedOracles.get(i).confusionMatrix.getRightClassifiedPositiveExamplesProbability() +
		//                            estimatedOracles.get(i).confusionMatrix.getWrongClassifiedPositiveExamplesProbability()) -
		//                            Math.log(estimatedOracles.get(i).confusionMatrix.getRightClassifiedNegativeExamplesProbability() +
		//                            estimatedOracles.get(i).confusionMatrix.getWrongClassifiedNegativeExamplesProbability());
		//            logger.info("T=" + (estimatedOracles.get(i).confusionMatrix.getRightClassifiedPositiveExamplesProbability() + estimatedOracles.get(i).confusionMatrix.getWrongClassifiedPositiveExamplesProbability()));
		//            logger.info("F=" + (estimatedOracles.get(i).confusionMatrix.getWrongClassifiedNegativeExamplesProbability() + estimatedOracles.get(i).confusionMatrix.getRightClassifiedNegativeExamplesProbability()));
		//            logger.info("result=" + result);
		//        }
		//        result = result / (double) estimatedOracles.size();
		double oddsL;
		switch (oddsStrategy) {
		case APPROXIMATE:
			oddsL = wombatBestFmeasure;
			break;
		case EQUIVALENCE:
			double minKbSize = (sourceTrainCache.size() < targetTrainCache.size()) ? sourceTrainCache.size() : targetTrainCache.size();
			oddsL = minKbSize / (double) (sourceTrainCache.size() * targetTrainCache.size() - minKbSize);
			break;
		case RANDOM:
			oddsL = randomOddL;
			break;
		case IGNORE:
		default:
			oddsL = 1.0d;
			break;
		}
		return result + Math.log(oddsL);
	}

	/**
	 * Classify unlabeled examples to be positive or negative ones
	 *
	 * @param unlabeledexamples
	 * @param k
	 * @param oddsStrategy
	 * @return mapping of positive and negative examples, where
	 * positive examples have confidence score of 1d and
	 * negative examples have a confidence score of 0d
	 */
	protected AMapping classifyUnlabeledExamples(AMapping unlabeledexamples, double k, ODDS oddsStrategy) {
		AMapping labeledExamples = MappingFactory.createDefaultMapping();
		for (String subject : unlabeledexamples.getMap().keySet()) {
			for (String object : unlabeledexamples.getMap().get(subject).keySet()) {
				double OddsValue = computeOdds(subject, object, oddsStrategy);
				logger.info("odds=" + OddsValue + ", k=" + Math.log(k) + ", classifyAs " + (OddsValue >= Math.log(k) ? "+" : (OddsValue <= -Math.log(k) ? "-" : "?")));
				if (OddsValue >= Math.log(k)) {
					labeledExamples.add(subject, object, 1.0d);
				} else if (OddsValue <= -Math.log(k)) { 			// log(1/k)
					labeledExamples.add(subject, object, 0.0d);
				}
			}
		}
		return labeledExamples;
	}


	//    public AMapping learn(AMapping labeledExamples,double k, ODDS odds,
	//            int mostInformativeExamplesCount) throws UnsupportedMLImplementationException{
	public String learn(AMapping labeledExamples, double k, ODDS odds,
			int mostInformativeExamplesCount) throws UnsupportedMLImplementationException {
		fillTrainingCaches(labeledExamples);
		initActiveWombat(sourceTrainCache, targetTrainCache);
		int i = 0;
		AMapping newLabeledExamples = labeledExamples;
		gatherOracleResponses(labeledExamples);
        long start = System.currentTimeMillis();
        long t_wombat = 0;
		do {
			updateOraclesConfusionMatrices(newLabeledExamples);
            long start_wombat = System.currentTimeMillis();
            AMapping mostInformativeExamples = getWombatMostInformativeExamples(labeledExamples, mostInformativeExamplesCount);
            t_wombat += (System.currentTimeMillis() - start_wombat);
            gatherOracleResponses(mostInformativeExamples);
			newLabeledExamples = classifyUnlabeledExamples(mostInformativeExamples, k, odds);
			labeledExamples = MappingOperations.union(labeledExamples, newLabeledExamples);
			fillTrainingCaches(labeledExamples);
			i++;
		} while (i < 10);
		//        System.out.println(resultStr);
		return resultStr + "/t" + (t_eval + System.currentTimeMillis() - start) +  "/t" + (t_wombat + t_eval);
		//        return labeledExamples;
	}

	/**
	 * Initializes active Wombat
	 *
	 * @return MLResults
	 * @throws UnsupportedMLImplementationException
	 */
	protected MLResults initActiveWombat(ACache sourceCache, ACache targetCache) throws UnsupportedMLImplementationException {

		try {
			activeWombat = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
					MLImplementationType.SUPERVISED_ACTIVE).asActive();
		} catch (UnsupportedMLImplementationException e) {
			e.printStackTrace();
			fail();
		}
		assert (activeWombat.getClass().equals(ActiveMLAlgorithm.class));
		activeWombat.init(null, sourceCache, targetCache);
		//        activeWombat.init(null, sourcePredictionCache, targetPredictionCache);
		return null;
		//                activeWombat.activeLearn();
	}

	/**
	 * @param labeledExamples
	 * @param mostInformativeExamplesCount
	 * @return
	 * @throws UnsupportedMLImplementationException
	 */
	protected AMapping getWombatMostInformativeExamples(AMapping labeledExamples, int mostInformativeExamplesCount) throws UnsupportedMLImplementationException {
		MLResults mlModel = activeWombat.activeLearn(labeledExamples);
		wombatBestFmeasure = mlModel.getQuality();
		AMapping learnedMap = activeWombat.predict(sourceTrainCache, targetTrainCache, mlModel);
        long start = System.currentTimeMillis();
		computePerformanceIndicatorsWombat(labeledExamples, learnedMap, mlModel);
		t_eval -= (System.currentTimeMillis() - start);
		((AWombat) activeWombat.getMl()).updateActiveLearningCaches(sourceTrainCache, targetTrainCache, fullSourceCache, fullTargetCache);
		return activeWombat.getNextExamples(mostInformativeExamplesCount);
	}


	/**
	 * Computes performance indicators for Active Wombat
	 * i.e. compute precision, recall and F-measure for training and testing phases
	 * NOTE: for evaluation purpose only
	 *
	 * @param labeledExamples
	 * @param learnedMap
	 * @param mlModel
	 * @return result String
	 */
	private String computePerformanceIndicatorsWombat(AMapping labeledExamples, AMapping learnedMap, MLResults mlModel) {

		//PIs for training data
		resultStr += String.format("%.2f", new Precision().calculate(learnedMap, new GoldStandard(labeledExamples))) + "\t" +
				String.format("%.2f", new Recall().calculate(learnedMap, new GoldStandard(labeledExamples))) + "\t" +
				String.format("%.2f", new FMeasure().calculate(learnedMap, new GoldStandard(labeledExamples))) + "\t";

		//PIs for whole KB
		AMapping kbMap;
		LinkSpecification linkSpecification = mlModel.getLinkSpecification();
		Rewriter rw = RewriterFactory.getDefaultRewriter();
		LinkSpecification rwLs = rw.rewrite(linkSpecification);
		resultStr += "0.00\t0.00\t0.00\t";
        IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT, sourceTestingCache, targetTestingCache);
        ExecutionEngine engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sourceTestingCache, targetTestingCache, "?x", "?y");
        AMapping fullResultMap = engine.execute(rwLs, planner);
		kbMap = fullResultMap.getSubMap(linkSpecification.getThreshold());
        GoldStandard fullRefgoldStandard = new GoldStandard(testReferenceMap);
		resultStr += String.format("%.2f", new Precision().calculate(kbMap, fullRefgoldStandard)) + "\t" +
				String.format("%.2f", new Recall().calculate(kbMap, fullRefgoldStandard)) + "\t" +
				String.format("%.2f", new FMeasure().calculate(kbMap, fullRefgoldStandard)) + "\t" + computeMSE() + "\t" +
				linkSpecification.toStringOneLine() +
				"\n";

		return resultStr;
	}

	private String computeMSE() {
		double tp = 0, tn = 0, fp = 0, fn = 0;
		double x;
		ConfusionMatrix estOrcConfMat,bbOrcConfMat;
		double size = estimatedOracles.size();
		for (int i = 0 ; i < estimatedOracles.size() ; i++) {
			estOrcConfMat = estimatedOracles.get(i).confusionMatrix;
			bbOrcConfMat = blackBoxOracles.get(i).confusionMatrix;
			x = estOrcConfMat.getTruePositiveProbability() - bbOrcConfMat.getTruePositiveProbability();
			tp += x*x;
			x = estOrcConfMat.getFalsePositiveProbability() - bbOrcConfMat.getFalsePositiveProbability();
			fp += x*x;
			x = estOrcConfMat.getTrueNegativeProbability() - bbOrcConfMat.getTrueNegativeProbability();
			tn += x*x;
			x = estOrcConfMat.getFalseNegativeProbability() - bbOrcConfMat.getFalseNegativeProbability();
			fn += x*x;
		}
		return String.format("%.4f", tp/size) + "\t" +
		String.format("%.4f", tn/size) + "\t" +
		String.format("%.4f", fp/size) + "\t" +
		String.format("%.4f", fn/size);
	}


	/**
	 * Extract the source and target training cache instances based on the input learnMap
	 *
	 * @param trainMap to be used for training caches filling
	 * @author sherif
	 */
	protected void fillTrainingCaches(AMapping trainMap) {
		sourceTrainCache = new HybridCache();
		targetTrainCache = new HybridCache();
		for (String s : trainMap.getMap().keySet()) {
			if (fullSourceCache.containsUri(s)) {
				sourceTrainCache.addInstance(fullSourceCache.getInstance(s));
				for (String t : trainMap.getMap().get(s).keySet()) {
					if (fullTargetCache.containsUri(t)) {
						if (!targetTrainCache.containsUri(t)) {
							targetTrainCache.addInstance(fullTargetCache.getInstance(t));
						}
					} else {
						logger.warn("Instance " + t + " not exist in the target dataset");
					}
				}
			} else {
				logger.warn("Instance " + s + " not exist in the source dataset");
			}
		}
	}


}
