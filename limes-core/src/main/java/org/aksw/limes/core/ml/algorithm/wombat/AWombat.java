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
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.ml.algorithm.ACoreMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.euclid.LinearSelfConfigurator;
import org.aksw.limes.core.ml.setting.LearningParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * This class uses Least General Generalization (LGG) to learn Link Specifications
 *
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Jun 7, 2016
 */
public abstract class AWombat extends ACoreMLAlgorithm {

    static Logger logger = LoggerFactory.getLogger(AWombat.class.getName());

    // Parameters
    public static final String PARAMETER_MAX_REFINEMENT_TREE_SIZE = "max refinement tree size";
    public static final String PARAMETER_MAX_ITERATIONS_NUMBER = "max iterations number";
    public static final String PARAMETER_MAX_ITERATION_TIME_IN_MINUTES = "max iteration time in minutes";
    public static final String PARAMETER_EXECUTION_TIME_IN_MINUTES = "max execution time in minutes";
    public static final String PARAMETER_MAX_FITNESS_THRESHOLD = "max fitness threshold";
    public static final String PARAMETER_MIN_PROPERTY_COVERAGE = "minimum properity coverage";
    public static final String PARAMETER_PROPERTY_LEARNING_RATE = "properity learning rate";
    public static final String PARAMETER_OVERALL_PENALTY_WEIT = "overall penalty weit";
    public static final String PARAMETER_CHILDREN_PENALTY_WEIT = "children penalty weit";
    public static final String PARAMETER_COMPLEXITY_PENALTY_WEIT = "complexity penalty weit";
    public static final String PARAMETER_VERBOSE = "verbose";
    public static final String PARAMETER_MEASURES = "measures";
    public static final String PARAMETER_SAVE_MAPPING = "save mapping";

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
    protected AMapping trainingData = MappingFactory.createDefaultMapping();
    protected boolean isUnsupervised = false;
    protected Set<String> wombatParameterNames = new HashSet<>();
    protected Tree<RefinementNode> refinementTreeRoot = null;

    protected AWombat() {
        super();
        setDefaultParameters();
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
            double pfm = pseudoFMeasure.calculate(mapping, new GoldStandard(null, sourceUris, targetUris));
            return new RefinementNode(pfm, mapping, metricExpr);
        }
        return new RefinementNode(mapping, metricExpr, trainingData);        
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
     * calculate either a real or a pseudo-F-Measure
     *
     * @param predictions
     * @return
     */
    protected double fMeasure(AMapping predictions) {
        if (isUnsupervised) {
            // compute pseudo-F-Measure
            return pseudoFMeasure.calculate(predictions, new GoldStandard(null, sourceUris, targetUris));            
        }
        // get real F-Measure based on training data 
        return new FMeasure().calculate(predictions, new GoldStandard(trainingData));
    }

    public long getChildrenPenaltyWeit() {
        return childrenPenaltyWeit;
    }

    public long getComplexityPenaltyWeit() {
        return complexityPenaltyWeit;
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

    public int getMaxExecutionTimeInMin() {
        return maxExecutionTimeInMin;
    }

    public double getMaxFitnessThreshold() {
        return maxFitnessThreshold;
    }


    public int getMaxIterationNumber() {
        return maxIterationNumber;
    }
    public int getMaxIterationTimeInMin() {
        return maxIterationTimeInMin;
    }
    public long getMaxRefineTreeSize() {
        return maxRefineTreeSize;
    }
    public Set<String> getMeasures() {
        return measures;
    }
    public double getMinPropertyCoverage() {
        return minPropertyCoverage;
    }
    public double getOverallPenaltyWeight() {
        return overallPenaltyWeight;
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
    
    public double getPropertyLearningRate() {
        return propertyLearningRate;
    }
    
    public PseudoFMeasure getPseudoFMeasure() {
        return pseudoFMeasure;
    }
    
    public AMapping getTrainingData() {
        return trainingData;
    }
    
    @Override
    protected void init(List<LearningParameter> lp, Cache sourceCache, Cache targetCache) {
        super.init(lp, sourceCache, targetCache);
        sourcePropertiesCoverageMap = LinearSelfConfigurator.getPropertyStats(sourceCache, minPropertyCoverage);
        targetPropertiesCoverageMap = LinearSelfConfigurator.getPropertyStats(targetCache, minPropertyCoverage);
        RefinementNode.setSaveMapping(saveMapping);
    }
    
    public boolean isSaveMapping() {
        return saveMapping;
    }
    
    public boolean isUnsupervised() {
        return isUnsupervised;
    }
    
    public boolean isVerbose() {
        return verbose;
    }
    /**
     * calculate either a real or a pseudo-Precision
     *
     * @param predictions
     * @return
     */
    protected double precision(AMapping predictions) {
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
     * @return
     */
    protected double recall(AMapping predictions) {
        if (isUnsupervised) {
            // compute pseudo-recall
            return pseudoFMeasure.recall(predictions, new GoldStandard(null, sourceUris, targetUris));
        }
        // get real recall based on training data 
        return new Recall().calculate(predictions, new GoldStandard(trainingData));

    }
    
    public void setChildrenPenaltyWeit(long childrenPenaltyWeit) {
        this.childrenPenaltyWeit = childrenPenaltyWeit;
    }
    
    public void setComplexityPenaltyWeit(long complexityPenaltyWeit) {
        this.complexityPenaltyWeit = complexityPenaltyWeit;
    }
    
    @Override
    public void setDefaultParameters() {
        parameters = new ArrayList<>();
        parameters.add(new LearningParameter(PARAMETER_MAX_REFINEMENT_TREE_SIZE, maxRefineTreeSize, Integer.class, 10d, Integer.MAX_VALUE, 10d, PARAMETER_MAX_REFINEMENT_TREE_SIZE));
        parameters.add(new LearningParameter(PARAMETER_MAX_ITERATIONS_NUMBER, maxIterationNumber, Integer.class, 1d, Integer.MAX_VALUE, 10d, PARAMETER_MAX_ITERATIONS_NUMBER));
        parameters.add(new LearningParameter(PARAMETER_MAX_ITERATION_TIME_IN_MINUTES, maxIterationTimeInMin, Integer.class, 1d, Integer.MAX_VALUE,1, PARAMETER_MAX_ITERATION_TIME_IN_MINUTES));
        parameters.add(new LearningParameter(PARAMETER_EXECUTION_TIME_IN_MINUTES, maxExecutionTimeInMin, Integer.class, 1d, Integer.MAX_VALUE,1,PARAMETER_EXECUTION_TIME_IN_MINUTES));
        parameters.add(new LearningParameter(PARAMETER_MAX_FITNESS_THRESHOLD, maxFitnessThreshold, Double.class, 0d, 1d, 0.01d, PARAMETER_MAX_FITNESS_THRESHOLD));
        parameters.add(new LearningParameter(PARAMETER_MIN_PROPERTY_COVERAGE, minPropertyCoverage, Double.class, 0d, 1d, 0.01d, PARAMETER_MIN_PROPERTY_COVERAGE));
        parameters.add(new LearningParameter(PARAMETER_PROPERTY_LEARNING_RATE, propertyLearningRate,Double.class, 0d, 1d, 0.01d, PARAMETER_PROPERTY_LEARNING_RATE));
        parameters.add(new LearningParameter(PARAMETER_OVERALL_PENALTY_WEIT, overallPenaltyWeight, Double.class, 0d, 1d, 0.01d, PARAMETER_OVERALL_PENALTY_WEIT));
        parameters.add(new LearningParameter(PARAMETER_CHILDREN_PENALTY_WEIT, childrenPenaltyWeit, Double.class, 0d, 1d, 0.01d, PARAMETER_CHILDREN_PENALTY_WEIT));
        parameters.add(new LearningParameter(PARAMETER_COMPLEXITY_PENALTY_WEIT, complexityPenaltyWeit, Double.class, 0d, 1d, 0.01d, PARAMETER_COMPLEXITY_PENALTY_WEIT));
        parameters.add(new LearningParameter(PARAMETER_VERBOSE, verbose, Boolean.class, 0, 1, 0, PARAMETER_VERBOSE));
        parameters.add(new LearningParameter(PARAMETER_MEASURES, measures, MeasureType.class, 0, 0, 0, PARAMETER_MEASURES));
        parameters.add(new LearningParameter(PARAMETER_SAVE_MAPPING, saveMapping, Boolean.class, 0, 1, 0, PARAMETER_SAVE_MAPPING));
    }

    public void setMaxExecutionTimeInMin(int maxExecutionTimeInMin) {
        this.maxExecutionTimeInMin = maxExecutionTimeInMin;
    }

    public void setMaxFitnessThreshold(double maxFitnessThreshold) {
        this.maxFitnessThreshold = maxFitnessThreshold;
    }


    public void setMaxIterationNumber(int maxIterationNumber) {
        this.maxIterationNumber = maxIterationNumber;
    }

    public void setMaxIterationTimeInMin(int maxIterationTimeInMin) {
        this.maxIterationTimeInMin = maxIterationTimeInMin;
    }

    public void setMaxRefineTreeSize(long maxRefineTreeSize) {
        this.maxRefineTreeSize = maxRefineTreeSize;
    }


    public void setMeasures(Set<String> measures) {
        this.measures = measures;
    }

    public void setMinPropertyCoverage(double minPropertyCoverage) {
        this.minPropertyCoverage = minPropertyCoverage;
    }


    public void setOverallPenaltyWeight(double overallPenaltyWeight) {
        this.overallPenaltyWeight = overallPenaltyWeight;
    }

    public void setPropertyLearningRate(double propertyLearningRate) {
        this.propertyLearningRate = propertyLearningRate;
    }


    public void setPseudoFMeasure(PseudoFMeasure pseudoFMeasure) {
        this.pseudoFMeasure = pseudoFMeasure;
    }

    public void setSaveMapping(boolean saveMapping) {
        this.saveMapping = saveMapping;
    }

    public void setTrainingData(AMapping trainingData) {
        this.trainingData = trainingData;
    }

    public void setUnsupervised(boolean isUnsupervised) {
        this.isUnsupervised = isUnsupervised;
    }


    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

}
