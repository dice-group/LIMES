/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.wombat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.Tree;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Precision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Recall;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory.ExecutionEngineType;
import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Plan;
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
import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.ml.algorithm.ACoreMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.euclid.LinearSelfConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class uses Least General Generalization (LGG) to learn Link
 * Specifications (LS)
 *
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jun 7, 2016
 */
public abstract class AWombat extends ACoreMLAlgorithm {

    // Parameters
    public static final String PARAMETER_MAX_REFINEMENT_TREE_SIZE = "max refinement tree size";
    public static final String PARAMETER_MAX_ITERATIONS_NUMBER = "max iterations number";
    public static final String PARAMETER_MAX_ITERATION_TIME_IN_MINUTES = "max iteration time in minutes";
    public static final String PARAMETER_EXECUTION_TIME_IN_MINUTES = "max execution time in minutes";
    public static final String PARAMETER_MAX_FITNESS_THRESHOLD = "max fitness threshold";
    public static final String PARAMETER_MIN_PROPERTY_COVERAGE = "minimum property coverage";
    public static final String PARAMETER_PROPERTY_LEARNING_RATE = "property learning rate";
    public static final String PARAMETER_OVERALL_PENALTY_WEIGHT = "overall penalty weight";
    public static final String PARAMETER_CHILDREN_PENALTY_WEIGHT = "children penalty weight";
    public static final String PARAMETER_COMPLEXITY_PENALTY_WEIGHT = "complexity penalty weight";
    public static final String PARAMETER_VERBOSE = "verbose";
    public static final String PARAMETER_ATOMIC_MEASURES = "atomic measures";
    public static final String PARAMETER_SAVE_MAPPING = "save mapping";
    public static final String PARAMETER_FMEASURE_BETA = "beta";
    public static List<String> sourceUris;
    public static List<String> targetUris;
    static Logger logger = LoggerFactory.getLogger(AWombat.class);
    protected String sourceVariable = "x";
    protected String targetVariable = "y";
    protected Map<String, Double> sourcePropertiesCoverageMap; // coverage map
                                                               // for latter
                                                               // computations
    protected Map<String, Double> targetPropertiesCoverageMap; // coverage map
                                                               // for latter
                                                               // computations

    protected PseudoFMeasure pseudoFMeasure = null;
    protected AMapping trainingData = MappingFactory.createDefaultMapping();
    protected boolean isUnsupervised = false;
    protected Set<String> wombatParameterNames = new HashSet<>();
    protected ACache sourceSample = new HybridCache();
    protected ACache targetSample = new HybridCache();

    protected AWombat() {
        super();
        setDefaultParameters();
        if (configuration != null) {
            sourceVariable = configuration.getSourceInfo().getVar().trim().substring(1);
            targetVariable = configuration.getTargetInfo().getVar().trim().substring(1);
        }
    }

    /**
     * Create new RefinementNode using either real or pseudo-F-Measure
     *
     * @param mapping
     *            of the node
     * @param metricExpr
     *            learning specifications
     * @return new RefinementNode
     */
    protected RefinementNode createNode(AMapping mapping, String metricExpr) {
        if (!saveMapping()) {
            mapping = null;
        }
        return new RefinementNode(fMeasure(mapping), mapping, metricExpr);
    }

    /**
     * @param sourceProperty
     *            URI
     * @param targetProperty
     *            URI
     * @param measure
     *            name
     * @param threshold
     *            of the LS
     * @return Mapping from source to target resources after applying the atomic
     *         mapper measure(sourceProperty, targetProperty)
     */
    private AMapping executeAtomicMeasure(String sourceProperty, String targetProperty, String measure,
            double threshold) {
        String measureExpression = measure + "(" + sourceVariable + "." + sourceProperty + ", " + targetVariable + "."
                + targetProperty + ")";
        Instruction inst = new Instruction(Instruction.Command.RUN, measureExpression, threshold + "", -1, -1, -1);
        ExecutionEngine ee = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sourceCache, targetCache,
                "?" + sourceVariable, "?" + targetVariable, 0, 1.0);
        Plan plan = new Plan();
        plan.addInstruction(inst);
        return ((SimpleExecutionEngine) ee).executeInstructions(plan);
    }

    /**
     * calculate either a real or a pseudo-F-Measure
     *
     * @param predictions
     *            Mapping
     * @return F-measure
     */
    protected final double fMeasure(AMapping predictions) {
        if (isUnsupervised) {
            // compute pseudo-F-Measure
            return pseudoFMeasure.calculate(predictions, new GoldStandard(null, sourceUris, targetUris), getBeta());
        }
        // get real F-Measure based on training data
        return new FMeasure().calculate(predictions, new GoldStandard(trainingData), getBeta());
    }

    /**
     * Looks first for the input metricExpression in the already constructed
     * tree, if found the corresponding mapping is returned. Otherwise, the
     * SetConstraintsMapper is generate the mapping from the metricExpression.
     *
     * @param metricExpression
     *            learning specifications
     * @return Mapping corresponding to the input metric expression
     */
    protected final AMapping getMappingOfMetricExpression(String metricExpression,
            Tree<? extends RefinementNode> root) {
        AMapping map = null;
        if (saveMapping()) {
            map = getMappingOfMetricFromTree(metricExpression, root);
        }
        if (map == null) {
            double threshold = Double.parseDouble(metricExpression.substring(metricExpression.lastIndexOf("|") + 1));
            map = executeLS(new LinkSpecification(metricExpression, threshold), sourceCache, targetCache);
        }
        return map;
    }

    /**
     * @param metricExpression
     *            learning specifications
     * @param root
     *            refinement tree
     * @return return mapping of the input metricExpression from the search tree
     */
    private AMapping getMappingOfMetricFromTree(String metricExpression, Tree<? extends RefinementNode> root) {
        if (root != null) {
            if (root.getValue().getMetricExpression().equals(metricExpression)) {
                return root.getValue().getMapping();
            }
            if (root.getchildren() != null && root.getchildren().size() > 0) {
                for (Tree<? extends RefinementNode> c : root.getchildren()) {
                    AMapping map = getMappingOfMetricFromTree(metricExpression, c);
                    if (map != null && map.size() != 0) {
                        return map;
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected AMapping predict(ACache source, ACache target, MLResults mlModel) {
        LinkSpecification ls = mlModel.getLinkSpecification();
        return executeLS(ls, source, target);
    }

    /**
     * get mapping from source cache to target cache using metricExpression
     *
     * @param ls
     *            learning specifications
     * @param sCache
     *            source Cache
     * @param tCache
     *            target Cache
     * @return Mapping from sCache to tCache
     */
    private AMapping executeLS(LinkSpecification ls, ACache sCache, ACache tCache) {
        Rewriter rw = RewriterFactory.getDefaultRewriter();
        LinkSpecification rwLs = rw.rewrite(ls);
        IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT, sCache, tCache);
        assert planner != null;
        ExecutionEngine engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sCache, tCache,
                "?" + sourceVariable, "?" + targetVariable, 0, 1.0);
        assert engine != null;
        AMapping resultMap = engine.execute(rwLs, planner);
        return resultMap.getSubMap(ls.getThreshold());
    }

    @Override
    protected void init(List<LearningParameter> lp, ACache sourceCache, ACache targetCache) {
        super.init(lp, sourceCache, targetCache);
        sourcePropertiesCoverageMap = LinearSelfConfigurator.getPropertyStats(sourceCache, getMinPropertyCoverage());
        targetPropertiesCoverageMap = LinearSelfConfigurator.getPropertyStats(targetCache, getMinPropertyCoverage());
    }

    public boolean isUnsupervised() {
        return isUnsupervised;
    }

    /**
     * calculate either a real or a pseudo-Precision
     *
     * @param predictions
     *            Mapping
     * @return precision
     */
    protected final double precision(AMapping predictions) {
        if (isUnsupervised) {
            // compute pseudo-precision
            return pseudoFMeasure.precision(predictions, new GoldStandard(null, sourceUris, targetUris));
        }
        // get real precision based on training data
        return new Precision().calculate(predictions, new GoldStandard(trainingData));
    }

    /**
     * calculate either a real or a pseudo-Recall
     *
     * @param predictions
     *            Mapping
     * @return recall
     */
    protected final double recall(AMapping predictions) {
        if (isUnsupervised) {
            // compute pseudo-recall
            return pseudoFMeasure.recall(predictions, new GoldStandard(null, sourceUris, targetUris));
        }
        // get real recall based on training data
        return new Recall().calculate(predictions, new GoldStandard(trainingData));
    }

    protected final void fillSampleSourceTargetCaches(AMapping sample) {
        for (String s : sample.getMap().keySet()) {
            if (this.sourceCache.containsUri(s)) {
                sourceSample.addInstance(this.sourceCache.getInstance(s));
                for (String t : sample.getMap().get(s).keySet())
                    if (targetCache.containsUri(t))
                        targetSample.addInstance(targetCache.getInstance(t));
                    else
                        logger.warn("Instance " + t + " does not exist in the target dataset");
            } else {
                logger.warn("Instance " + s + " does not exist in the source dataset");
            }
        }
    }

    /**
     * @return initial classifiers
     */
    protected final List<ExtendedClassifier> findInitialClassifiers() {
        logger.debug("Geting all initial classifiers ...");
        List<ExtendedClassifier> initialClassifiers = new ArrayList<>();
        for (String p : sourcePropertiesCoverageMap.keySet()) {
            for (String q : targetPropertiesCoverageMap.keySet()) {
                for (String m : getAtomicMeasures()) {
                    ExtendedClassifier cp = findInitialClassifier(p, q, m);
                    // only add if classifier covers all entries
                    initialClassifiers.add(cp);
                }
            }
        }
        logger.debug("Done computing all initial classifiers.");
        return initialClassifiers;
    }

    double getThreshold(String measure) {
        double threshold = 0.4d;
        if (measure.equals("li") || measure.equals("lch") || measure.equals("wupalmer")
                || measure.equals("shortest_path"))
            threshold = 0.7d;
        return threshold;
    }

    /**
     * Computes the atomic classifiers by finding the highest possible F-measure
     * achievable on a given property pair
     *
     * @param sourceProperty
     *            Property of source to use
     * @param targetProperty
     *            Property of target to use
     * @param measure
     *            Measure to be used
     * @return Best simple classifier
     */
    private ExtendedClassifier findInitialClassifier(String sourceProperty, String targetProperty, String measure) {
        double maxOverlap = 0;
        double theta = 1.0;
        AMapping bestMapping = MappingFactory.createDefaultMapping();

        double minThreshold = this.getThreshold(measure);
        for (double threshold = 1d; threshold > minThreshold; threshold = threshold * getPropertyLearningRate()) {
            AMapping mapping = executeAtomicMeasure(sourceProperty, targetProperty, measure, threshold);
            double overlap = fMeasure(mapping);
            if (maxOverlap < overlap) {
                theta = threshold;
                maxOverlap = overlap;
                bestMapping = mapping;
            }
        }
        ExtendedClassifier cp = new ExtendedClassifier(measure, theta, sourceProperty, targetProperty);
        cp.setfMeasure(maxOverlap);
        cp.setMapping(bestMapping);
        return cp;
    }

    /**
     * Get the most promising node as the node with the best F-score
     *
     * @param root
     *            The whole refinement tree
     * @return best node from the input tree root
     * @author sherif
     */
    protected final <T extends RefinementNode> Tree<T> getBestNode(Tree<T> root) {
        return getMostPromisingNode(root, 0);
    }

    /**
     * Get the most promising node as the node with the best F-score
     *
     * @param root
     *            The whole refinement tree
     * @return most promising node from the input tree root
     * @author sherif
     */
    protected final <T extends RefinementNode> Tree<T> getMostPromisingNode(Tree<T> root) {
        return getMostPromisingNode(root, getOverAllPenaltyWeight());
    }

    /**
     * Get the most promising node as the node with the best F-score
     *
     * @param root
     *            The whole refinement tree
     * @param penaltyWeight
     *            penalty weight
     * @return most promising node from the input tree root
     * @author sherif
     */
    private <T extends RefinementNode> Tree<T> getMostPromisingNode(Tree<T> root, double penaltyWeight) {
        // trivial case
        if (root.getchildren() == null || root.getchildren().size() == 0) {
            return root;
        }
        // get mostPromisingChild of children
        Tree<T> mostPromisingChild = new Tree<>();
        double bestFitness = -1;

        for (Tree<T> child : root.getchildren()) {
            if (child.getValue().getFMeasure() >= 0) {
                Tree<T> promisingChild = getMostPromisingNode(child, penaltyWeight);
                double penalty = computePenalty(root, promisingChild);
                double newFitness = promisingChild.getValue().getFMeasure() - penaltyWeight * penalty;
                if (newFitness > bestFitness) {
                    mostPromisingChild = promisingChild;
                    bestFitness = newFitness;
                } else if (newFitness == bestFitness) {
                    double tieBreakerFitness = bestFitness
                            - getOverAllPenaltyWeight() * computePenalty(root, mostPromisingChild);
                    double newTieBreakerFitness = promisingChild.getValue().getFMeasure()
                            - getOverAllPenaltyWeight() * penalty;
                    if (newTieBreakerFitness > tieBreakerFitness) {
                        mostPromisingChild = promisingChild;
                        bestFitness = mostPromisingChild.getValue().getFMeasure();
                    }
                }
            }
        }
        // return the argmax{root, mostPromisingChild}
        if (penaltyWeight > 0) {
            return mostPromisingChild;
        } else if (root.getValue().getFMeasure() >= mostPromisingChild.getValue().getFMeasure()) {
            return root;
        } else {
            return mostPromisingChild;
        }
    }

    /**
     * @param promisingChild
     *            promising child
     * @return children penalty + complexity penalty
     * @author sherif
     */
    private double computePenalty(Tree<?> root, Tree<?> promisingChild) {
        long childrenCount = promisingChild.size() - 1;
        double childrenPenalty = (getChildrenPenaltyWeight() * childrenCount) / root.size();
        long level = promisingChild.level();
        double complexityPenalty = (getComplexityPenaltyWeight() * level) / root.depth();
        return childrenPenalty + complexityPenalty;
    }

    protected final boolean saveMapping() {
        return Boolean.parseBoolean(getParameter(PARAMETER_SAVE_MAPPING).toString());
    }

    @Override
    public void setDefaultParameters() {
        // default parameters
        long maxRefineTreeSize = 2000;
        int maxIterationNumber = 3;
        int maxIterationTimeInMin = 20;
        int maxExecutionTimeInMin = 600;
        double maxFitnessThreshold = 1;
        double childrenPenaltyWeight = 1;
        double complexityPenaltyWeight = 1;
        double beta = 1;
        boolean saveMapping = true;
        double minPropertyCoverage = 0.4;
        double propertyLearningRate = 0.9;
        double overallPenaltyWeight = 0.5d;
        boolean verbose = false;
        Set<String> measures = new HashSet<>(Arrays.asList("jaccard", "cosine", "qgrams"));

        learningParameters = new ArrayList<>();
        learningParameters.add(new LearningParameter(PARAMETER_MAX_REFINEMENT_TREE_SIZE, maxRefineTreeSize, Long.class,
                10d, Long.MAX_VALUE, 10d, PARAMETER_MAX_REFINEMENT_TREE_SIZE));
        learningParameters.add(new LearningParameter(PARAMETER_MAX_ITERATIONS_NUMBER, maxIterationNumber, Integer.class,
                1d, Integer.MAX_VALUE, 10d, PARAMETER_MAX_ITERATIONS_NUMBER));
        learningParameters.add(new LearningParameter(PARAMETER_MAX_ITERATION_TIME_IN_MINUTES, maxIterationTimeInMin,
                Integer.class, 1d, Integer.MAX_VALUE, 1, PARAMETER_MAX_ITERATION_TIME_IN_MINUTES));
        learningParameters.add(new LearningParameter(PARAMETER_EXECUTION_TIME_IN_MINUTES, maxExecutionTimeInMin,
                Integer.class, 1d, Integer.MAX_VALUE, 1, PARAMETER_EXECUTION_TIME_IN_MINUTES));
        learningParameters.add(new LearningParameter(PARAMETER_MAX_FITNESS_THRESHOLD, maxFitnessThreshold, Double.class,
                0d, 1d, 0.01d, PARAMETER_MAX_FITNESS_THRESHOLD));
        learningParameters.add(new LearningParameter(PARAMETER_MIN_PROPERTY_COVERAGE, minPropertyCoverage, Double.class,
                0d, 1d, 0.01d, PARAMETER_MIN_PROPERTY_COVERAGE));
        learningParameters.add(new LearningParameter(PARAMETER_PROPERTY_LEARNING_RATE, propertyLearningRate,
                Double.class, 0d, 1d, 0.01d, PARAMETER_PROPERTY_LEARNING_RATE));
        learningParameters.add(new LearningParameter(PARAMETER_OVERALL_PENALTY_WEIGHT, overallPenaltyWeight,
                Double.class, 0d, 1d, 0.01d, PARAMETER_OVERALL_PENALTY_WEIGHT));
        learningParameters.add(new LearningParameter(PARAMETER_CHILDREN_PENALTY_WEIGHT, childrenPenaltyWeight,
                Double.class, 0d, 1d, 0.01d, PARAMETER_CHILDREN_PENALTY_WEIGHT));
        learningParameters.add(new LearningParameter(PARAMETER_COMPLEXITY_PENALTY_WEIGHT, complexityPenaltyWeight,
                Double.class, 0d, 1d, 0.01d, PARAMETER_COMPLEXITY_PENALTY_WEIGHT));
        learningParameters.add(new LearningParameter(PARAMETER_FMEASURE_BETA, beta, Double.class, 0d, Double.MAX_VALUE,
                0.01d, "beta parameter for f-measure"));
        learningParameters
                .add(new LearningParameter(PARAMETER_VERBOSE, verbose, Boolean.class, 0, 1, 0, PARAMETER_VERBOSE));
        learningParameters.add(new LearningParameter(PARAMETER_ATOMIC_MEASURES, measures, MeasureType.class, 0, 0, 0,
                PARAMETER_ATOMIC_MEASURES));
        learningParameters.add(new LearningParameter(PARAMETER_SAVE_MAPPING, saveMapping, Boolean.class, 0, 1, 0,
                PARAMETER_SAVE_MAPPING));
    }

    protected boolean isVerbose() {
        return Boolean.parseBoolean(getParameter(PARAMETER_VERBOSE).toString());
    }

    protected double getOverAllPenaltyWeight() {
        return Double.parseDouble(getParameter(PARAMETER_OVERALL_PENALTY_WEIGHT).toString());
    }

    protected double getChildrenPenaltyWeight() {
        return Double.parseDouble(getParameter(PARAMETER_CHILDREN_PENALTY_WEIGHT).toString());
    }

    protected double getComplexityPenaltyWeight() {
        return Double.parseDouble(getParameter(PARAMETER_COMPLEXITY_PENALTY_WEIGHT).toString());
    }

    protected double getMinPropertyCoverage() {
        return Double.parseDouble(getParameter(PARAMETER_MIN_PROPERTY_COVERAGE).toString());
    }

    protected double getPropertyLearningRate() {
        return Double.parseDouble(getParameter(PARAMETER_PROPERTY_LEARNING_RATE).toString());
    }

    protected double getBeta() {
        return Double.parseDouble(getParameter(PARAMETER_FMEASURE_BETA).toString());
    }

    protected int getIterationTimeInMinutes() {
        return Integer.parseInt(getParameter(PARAMETER_MAX_ITERATION_TIME_IN_MINUTES).toString());
    }

    protected int getExcutionTimeInMinutes() {
        return Integer.parseInt(getParameter(PARAMETER_EXECUTION_TIME_IN_MINUTES).toString());
    }

    protected double getMaxFitnessThreshold() {
        return Double.parseDouble(getParameter(PARAMETER_MAX_FITNESS_THRESHOLD).toString());
    }

    protected int getMaxIterationNumber() {
        return Integer.parseInt(getParameter(PARAMETER_MAX_ITERATIONS_NUMBER).toString());
    }

    protected int getMaxRefinmentTreeSize() {
        return Integer.parseInt(getParameter(PARAMETER_MAX_REFINEMENT_TREE_SIZE).toString());
    }

    protected Set<String> getAtomicMeasures() {
        Set<String> atomicMeasures = new HashSet<>();
        String measuresAsString = getParameter(PARAMETER_ATOMIC_MEASURES).toString().replace("[", "").replace("]", "");
        for (String m : measuresAsString.split(",")) {
            atomicMeasures.add(m.trim());
        }
        return atomicMeasures;
    }

}
