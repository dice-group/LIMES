/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.wombat;

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
import org.aksw.limes.core.execution.rewriter.RewriterFactory.RewriterFactoryType;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.ACoreMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.euclid.LinearSelfConfigurator;
import org.aksw.limes.core.ml.setting.LearningParameters;
import org.apache.log4j.Logger;

import java.util.*;


/**
 * This class uses Least General Generalization (LGG) to learn Link Specifications
 *
 * @author sherif
 */
public abstract class AWombat extends ACoreMLAlgorithm {
    
    static Logger logger = Logger.getLogger(AWombat.class.getName());

    // Parameters
    protected static final String PARAMETER_MAX_REFINEMENT_TREE_SIZE = "max refinement tree size";
    protected static final String PARAMETER_MAX_ITERATIONS_NUMBER = "max iterations number";
    protected static final String PARAMETER_MAX_ITERATION_TIME_IN_MINUTES = "max iteration time in minutes";
    protected static final String PARAMETER_EXECUTION_TIME_IN_MINUTES = "max execution time in minutes";
    protected static final String PARAMETER_MAX_FITNESS_THRESHOLD = "max fitness threshold";
    protected static final String PARAMETER_MIN_PROPERTY_COVERAGE = "minimum properity coverage";
    protected static final String PARAMETER_PROPERTY_LEARNING_RATE = "properity learning rate";
    protected static final String PARAMETER_OVERALL_PENALTY_WEIT = "overall penalty weit";
    protected static final String PARAMETER_CHILDREN_PENALTY_WEIT = "children penalty weit";
    protected static final String PARAMETER_COMPLEXITY_PENALTY_WEIT = "complexity penalty weit";
    protected static final String PARAMETER_VERBOSE = "verbose";
    protected static final String PARAMETER_MEASURES = "measures";
    protected static final String PARAMETER_SAVE_MAPPING = "save mapping";
    
    public static List<String> sourceUris;
    public static List<String> targetUris;
    protected long maxRefineTreeSize = 2000;
    protected int maxIterationNumber = 3;
    protected int maxIterationTimeInMin = 20;
    protected int maxExecutionTimeInMin = 600;
    protected double maxFitnessThreshold = 1;
    protected long childrenPenaltyWeit = 1;
    protected long complexityPenaltyWeit = 1;
    protected boolean saveMapping = true;
    protected double minPropertyCoverage = 0.4;
    protected double propertyLearningRate = 0.9;
    protected double overallPenaltyWeight = 0.5d;
    protected boolean verbose = false;
    protected Set<String> measures = new HashSet<>(Arrays.asList("jaccard", "trigrams", "cosine", "qgrams"));
    protected Map<String, Double> sourcePropertiesCoverageMap; //coverage map for latter computations
    protected Map<String, Double> targetPropertiesCoverageMap; //coverage map for latter computations
    protected PseudoFMeasure pseudoFMeasure = null;
    protected AMapping trainingData;
    protected boolean isUnsupervised = false;

    protected Set<String> wombatParameterNames = new HashSet<>();

    protected Tree<RefinementNode> refinementTreeRoot = null;


    protected AWombat() {
        super();
        setDefaultParameters();
    }

    private void setDefaultParameters() {
        parameters.put(PARAMETER_MAX_REFINEMENT_TREE_SIZE, String.valueOf(maxRefineTreeSize));
        parameters.put(PARAMETER_MAX_ITERATIONS_NUMBER, String.valueOf(maxIterationNumber));
        parameters.put(PARAMETER_MAX_ITERATION_TIME_IN_MINUTES, String.valueOf(maxIterationTimeInMin));
        parameters.put(PARAMETER_EXECUTION_TIME_IN_MINUTES, String.valueOf(maxExecutionTimeInMin));
        parameters.put(PARAMETER_MAX_FITNESS_THRESHOLD, String.valueOf(maxFitnessThreshold));
        parameters.put(PARAMETER_MIN_PROPERTY_COVERAGE, String.valueOf(minPropertyCoverage));
        parameters.put(PARAMETER_PROPERTY_LEARNING_RATE, String.valueOf(propertyLearningRate));
        parameters.put(PARAMETER_OVERALL_PENALTY_WEIT, String.valueOf(overallPenaltyWeight));
        parameters.put(PARAMETER_CHILDREN_PENALTY_WEIT, String.valueOf(childrenPenaltyWeit));
        parameters.put(PARAMETER_COMPLEXITY_PENALTY_WEIT, String.valueOf(complexityPenaltyWeit));
        parameters.put(PARAMETER_VERBOSE, String.valueOf(false));
        parameters.put(PARAMETER_MEASURES, String.valueOf(measures));
        parameters.put(PARAMETER_SAVE_MAPPING, String.valueOf(saveMapping));
    }

    @Override
    protected void init(LearningParameters lp, Cache sourceCache, Cache targetCache) {
        super.init(lp, sourceCache, targetCache);
        sourcePropertiesCoverageMap = LinearSelfConfigurator.getPropertyStats(sourceCache, minPropertyCoverage);
        targetPropertiesCoverageMap = LinearSelfConfigurator.getPropertyStats(targetCache, minPropertyCoverage);
        RefinementNode.setSaveMapping(saveMapping);
    }


    /**
     * @param sourceCache
     *         cache
     * @param targetCache
     *         cache
     * @param sourceProperty
     * @param targetProperty
     * @param measure
     * @param threshold
     * @return Mapping from source to target resources after applying
     * the atomic mapper measure(sourceProperity, targetProperty)
     */
    public AMapping executeAtomicMeasure(String sourceProperty, String targetProperty, String measure, double threshold) {
        String measureExpression = measure + "(x." + sourceProperty + ", y." + targetProperty + ")";
        Instruction inst = new Instruction(Instruction.Command.RUN, measureExpression, threshold + "", -1, -1, -1);
        ExecutionEngine ee = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sourceCache, targetCache, "?x", "?y");
        Plan plan = new Plan();
        plan.addInstruction(inst);
        return ((SimpleExecutionEngine) ee).executeInstructions(plan);
    }

    /**
     * Looks first for the input metricExpression in the already constructed tree,
     * if found the corresponding mapping is returned.
     * Otherwise, the SetConstraintsMapper is generate the mapping from the metricExpression.
     *
     * @param metricExpression
     * @return Mapping corresponding to the input metric expression
     * @author sherif
     */
    protected AMapping getMapingOfMetricExpression(String metricExpression) {
        AMapping map = null;
        if (RefinementNode.isSaveMapping()) {
            map = getMapingOfMetricFromTree(metricExpression, refinementTreeRoot);
        }
        if (map == null) {
            Double threshold = Double.parseDouble(metricExpression.substring(metricExpression.lastIndexOf("|") + 1, metricExpression.length()));
            Rewriter rw = RewriterFactory.getRewriter(RewriterFactoryType.DEFAULT);
            LinkSpecification ls = new LinkSpecification(metricExpression, threshold);
            LinkSpecification rwLs = rw.rewrite(ls);
            IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT, sourceCache, targetCache);
            assert planner != null;
            ExecutionEngine engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sourceCache, targetCache, "?x", "?y");
            assert engine != null;
            AMapping resultMap = engine.execute(rwLs, planner);
            map = resultMap.getSubMap(threshold);
        }
        return map;
    }


    /**
     * get mapping from source cache to target cache using metricExpression
     *
     * @param metricExpression
     * @param sCache
     * @param tCache
     * @return
     */
    protected AMapping getPredictions(LinkSpecification ls, Cache sCache, Cache tCache) {
        AMapping map;
        Rewriter rw = RewriterFactory.getDefaultRewriter();
        LinkSpecification rwLs = rw.rewrite(ls);
        IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT, sCache, tCache);
        assert planner != null;
        ExecutionEngine engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sCache, tCache, "?x", "?y");
        assert engine != null;
        AMapping resultMap = engine.execute(rwLs, planner);
        map = resultMap.getSubMap(ls.getThreshold());
        return map;
    }

    /**
     * @param string
     * @return return mapping of the input metricExpression from the search tree
     * @author sherif
     */
    protected AMapping getMapingOfMetricFromTree(String metricExpression, Tree<RefinementNode> r) {
        if (r != null) {
            if (r.getValue().getMetricExpression().equals(metricExpression)) {
                return r.getValue().getMapping();
            }
            if (r.getchildren() != null && r.getchildren().size() > 0) {
                for (Tree<RefinementNode> c : r.getchildren()) {
                    AMapping map = getMapingOfMetricFromTree(metricExpression, c);
                    if (map != null && map.size() != 0) {
                        return map;
                    }
                }
            }
        }
        return null;
    }


    /**
     * calculate either a real or a pseudo-F-Measure
     *
     * @param predictions
     * @return
     */
    protected double fMeasure(AMapping predictions) {
        if (isUnsupervised) {
            // get real F-Measure based on training data 
            return new FMeasure().calculate(predictions, new GoldStandard(trainingData));
        }
        // compute pseudo-F-Measure
        return pseudoFMeasure.calculate(predictions, new GoldStandard(null, sourceUris, targetUris));
    }

    /**
     * calculate either a real or a pseudo-Precision
     *
     * @param predictions
     * @return
     */
    protected double precision(AMapping predictions) {
        if (isUnsupervised) {
            // get real precision based on training data 
            return new Precision().calculate(predictions, new GoldStandard(trainingData));
        }
        // compute pseudo-precision
        return pseudoFMeasure.precision(predictions, new GoldStandard(null, sourceUris, targetUris));
    }

    /**
     * calculate either a real or a pseudo-Recall
     *
     * @param predictions
     * @return
     */
    protected double recall(AMapping predictions) {
        if (isUnsupervised) {
            // get real recall based on training data 
            return new Recall().calculate(predictions, new GoldStandard(trainingData));
        }
        // compute pseudo-recall
        return pseudoFMeasure.recall(predictions, new GoldStandard(null, sourceUris, targetUris));
    }
    
    /**
     * Create new RefinementNode using either real or pseudo-F-Measure
     *
     * @param mapping
     * @param metricExpr
     * @return
     */
    protected RefinementNode createNode(AMapping mapping, String metricExpr) {
        if(!saveMapping){
            mapping = null;
        }
        if (isUnsupervised) {
            return new RefinementNode(mapping, metricExpr, trainingData);
        }
        double pfm = pseudoFMeasure.calculate(mapping, new GoldStandard(null, sourceUris, targetUris));
        return new RefinementNode(pfm, mapping, metricExpr);
    }


    /**
     * @param childMetricExpr
     * @return
     * @author sherif
     */
    protected RefinementNode createNode(String metricExpr) {
        AMapping map = null;
        if(saveMapping){
            map = getMapingOfMetricExpression(metricExpr);
        }
        return createNode(map, metricExpr);
    }

}
