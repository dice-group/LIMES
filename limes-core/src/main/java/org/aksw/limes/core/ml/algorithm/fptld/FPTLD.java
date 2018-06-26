package org.aksw.limes.core.ml.algorithm.fptld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory.ExecutionEngineType;
import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.execution.planning.planner.CanonicalPlanner;
import org.aksw.limes.core.execution.rewriter.Rewriter;
import org.aksw.limes.core.execution.rewriter.RewriterFactory;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.ACoreMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.dragon.FitnessFunctions.FitnessFunctionDTL;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.aksw.limes.core.ml.algorithm.fptld.fitness.FuzzySimilarity;
import org.aksw.limes.core.ml.algorithm.fptld.fitness.SimFuzzyRMSE;
import org.aksw.limes.core.ml.algorithm.fptld.nodes.ANode;
import org.aksw.limes.core.ml.algorithm.fptld.nodes.LeafNode;

public class FPTLD extends ACoreMLAlgorithm {

	public static final String PARAMETER_PROPERTY_MAPPING = "property mapping";
	public static final String PARAMETER_MIN_PROPERTY_COVERAGE = "minimum property coverage";
	public static final String PARAMETER_BEAM_SIZE = "beam size";
	public static final String PARAMETER_EPSILON = "relative improvement";
	public static final String PARAMETER_FITNESS_FUNCTION = "similarity measure to determine fitness";

	// Strings for different function options
	public static final String FITNESS_NAME_JAC = "JaccardSimilarity";
	public static final String FITNESS_NAME_RMSE = "RootMeanSquared";
	public static final String FITNESS_NAME_FM = "Fmeasure";

	public static final String[] defaultMeasures = { "jaccard", "trigrams", "cosine", "qgrams" };
	private static final double minPropertyCoverage = 0.4;
	private static final int beamSize = 3;
	private static final double eps = 0.01;
	private static final double propertyLearningRate = 0.95;
	private static FuzzySimilarity fitnessFunction = SimFuzzyRMSE.INSTANCE;
	private static LogicOperator[] operators = { LogicOperator.LUKASIEWICZT, LogicOperator.LUKASIEWICZTCO,
			LogicOperator.AND, LogicOperator.OR, LogicOperator.EINSTEINT, LogicOperator.EINSTEINTCO,
			LogicOperator.ALGEBRAICT, LogicOperator.ALGEBRAICTCO, LogicOperator.DIFF, LogicOperator.ALGEBRAICDIFF,
			LogicOperator.LUKASIEWICZDIFF, LogicOperator.EINSTEINDIFF };

	@Override
	protected String getName() {
		return "FPTLD";
	}

	@Override
	public void init(List<LearningParameter> lp, ACache sourceCache, ACache targetCache) {
		super.init(lp, sourceCache, targetCache);
		if (lp == null) {
			setDefaultParameters();
		}
	}

	@Override
	public void setDefaultParameters() {
		learningParameters = new ArrayList<>();
		learningParameters.add(new LearningParameter(PARAMETER_PROPERTY_MAPPING, new PropertyMapping(),
				PropertyMapping.class, Double.NaN, Double.NaN, Double.NaN, PARAMETER_PROPERTY_MAPPING));
		learningParameters.add(new LearningParameter(PARAMETER_MIN_PROPERTY_COVERAGE, minPropertyCoverage, Double.class,
				0d, 1d, 0.01d, PARAMETER_MIN_PROPERTY_COVERAGE));
		learningParameters
		.add(new LearningParameter(PARAMETER_EPSILON, eps, Double.class, 0d, 0.99d, 0.01d, PARAMETER_EPSILON));
		learningParameters.add(
				new LearningParameter(PARAMETER_BEAM_SIZE, beamSize, Integer.class, 0, 100, 1, PARAMETER_BEAM_SIZE));
		learningParameters.add(new LearningParameter(PARAMETER_FITNESS_FUNCTION, fitnessFunction,
				FitnessFunctionDTL.class, new String[] { FITNESS_NAME_JAC, FITNESS_NAME_RMSE, FITNESS_NAME_FM },
				PARAMETER_FITNESS_FUNCTION));
	}

	@Override
	protected MLResults learn(AMapping trainingData) throws UnsupportedMLImplementationException {
		Integer bs = (Integer) getParameter(PARAMETER_BEAM_SIZE);
		Double eps = (Double) getParameter(PARAMETER_EPSILON);
		List<LeafNode> pptlist = createPrimitivePatternTrees(trainingData);
		List<ANode> candidates = pptlist.stream().limit(bs).collect(Collectors.toList());
		ANode best = candidates.get(0);
		while (true) {
			List<ANode> newCandidates = new ArrayList<>();
			for (ANode candidate : candidates) {
				for (LeafNode leaf : candidate.getLeaves()) {
					for (LogicOperator op : operators) {
						Set<String> leafCombinations = new HashSet<>();
						for (LeafNode ppt : pptlist) {
							if (!ppt.equals(leaf)
									&& !leafCombinations.contains(leaf.getFuzzyTerm() + ppt.getFuzzyTerm())
									&& !leafCombinations.contains(ppt.getFuzzyTerm() + leaf.getFuzzyTerm())) {
								ANode clone = candidate.clone();
								clone = clone.replaceLeaf(leaf, op, ppt);
								clone.setSet(clone.calculateSet());
								double quality = fitnessFunction.getSimilarity(clone.getSet(), trainingData);
								clone.setQuality(quality);
								newCandidates.add(clone);
								if (!(op.equals(LogicOperator.DIFF) || op.equals(LogicOperator.LUKASIEWICZDIFF)
										|| op.equals(LogicOperator.ALGEBRAICDIFF)
										|| op.equals(LogicOperator.EINSTEINDIFF))) {
									leafCombinations.add(leaf.getFuzzyTerm() + ppt.getFuzzyTerm());
								}
							}
						}
					}
				}
			}
			candidates.addAll(newCandidates);
			Collections.sort(candidates, (o1, o2) -> Double.compare(o1.getQuality(), o2.getQuality()) * -1);
			candidates = candidates.stream().limit(bs).collect(Collectors.toList());
			System.out.println("Improvement: " + (candidates.get(0).getQuality() - best.getQuality()));
			if (candidates.get(0).getQuality() < (1 + eps) * best.getQuality()) {
				break;
			}
			best = candidates.get(0);
		}
		LinkSpecification ls = best.toLS();
		MLResults res = new MLResults(ls, best.getSet(), best.getQuality(), null);
		return res;
	}

	private List<LeafNode> createPrimitivePatternTrees(AMapping trainingData) {
		List<LeafNode> ppts = new ArrayList<>();
		PropertyMapping pm = (PropertyMapping) getParameter(PARAMETER_PROPERTY_MAPPING);
		for (PairSimilar<String> propPair : pm.stringPropPairs) {
			for (String measure : defaultMeasures) {
				String metricExpression = measure + "(x." + propPair.a + ",y." + propPair.b + ")";
				LeafNode bestThresholdNode = new LeafNode("", 0.0, null, 0.0);
				for (double threshold = 1d; threshold > minPropertyCoverage; threshold = threshold
						* propertyLearningRate) {
					AMapping set = executeAtomicMeasure(metricExpression, threshold);
					double quality = fitnessFunction.getSimilarity(set, trainingData);
					if (quality > bestThresholdNode.getQuality()) {
						bestThresholdNode = new LeafNode(metricExpression, threshold, set, quality);
					}
				}
				if (bestThresholdNode.getQuality() > 0.0) {
					ppts.add(bestThresholdNode);
				}
			}
		}
		Collections.sort(ppts, (o1, o2) -> Double.compare(o1.getQuality(), o2.getQuality()) * -1);
		return ppts;
	}

	private AMapping executeAtomicMeasure(String measureExpression, double threshold) {
		Instruction inst = new Instruction(Instruction.Command.RUN, measureExpression, threshold + "", -1, -1, -1);
		ExecutionEngine ee = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sourceCache, targetCache,
				"?x", "?y");
		Plan plan = new Plan();
		plan.addInstruction(inst);
		return ((SimpleExecutionEngine) ee).executeInstructions(plan);
	}

	@Override
	protected MLResults learn(PseudoFMeasure pfm) throws UnsupportedMLImplementationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AMapping predict(ACache source, ACache target, MLResults mlModel) {
		LinkSpecification ls = mlModel.getLinkSpecification();
		Rewriter rw = RewriterFactory.getDefaultRewriter();
		ls = rw.rewrite(ls);
		CanonicalPlanner planner = new CanonicalPlanner();
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, configuration.getSourceInfo().getVar(),
				configuration.getTargetInfo().getVar());
		return ee.execute(ls, planner);
	}

	@Override
	protected boolean supports(MLImplementationType mlType) {
		return mlType == MLImplementationType.SUPERVISED_BATCH;
	}

	@Override
	protected AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected MLResults activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected MLResults activeLearn() throws UnsupportedMLImplementationException {
		// TODO Auto-generated method stub
		return null;
	}

}
