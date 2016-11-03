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
import org.aksw.limes.core.execution.rewriter.RewriterFactory.RewriterType;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.ml.algorithm.ACoreMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.euclid.LinearSelfConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class uses Least General Generalization (LGG) to learn Link Specifications (LS)
 *
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jun 7, 2016
 */
public abstract class AWombat extends ACoreMLAlgorithm {

    static Logger logger = LoggerFactory.getLogger(AWombat.class);

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

    public static List<String> sourceUris;
    public static List<String> targetUris;

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
     * @param mapping of the node 
     * @param metricExpr learning specifications
     * @return new RefinementNode
     */
    protected RefinementNode createNode(AMapping mapping, String metricExpr) {
        if(!saveMapping()){
            mapping = null;
        }
        if (isUnsupervised) {
            double pfm = pseudoFMeasure.calculate(mapping, new GoldStandard(null, sourceUris, targetUris));
            return new RefinementNode(pfm, mapping, metricExpr);
        }
        return new RefinementNode(mapping, metricExpr, trainingData);        
    }



    /**
     * @param metricExpr learning specifications
     * @return new RefinementNode
     */
    protected RefinementNode createNode(String metricExpr) {
        AMapping map = null;
        if(saveMapping()){
            map = getMapingOfMetricExpression(metricExpr);
        }
        return createNode(map, metricExpr);
    }
    


    /**
     * @param sourceProperty URI
     * @param targetProperty URI
     * @param measure name
     * @param threshold of the LS
     * @return Mapping from source to target resources after applying
     * the atomic mapper measure(sourceProperty, targetProperty)
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
     * @param predictions Mapping
     * @return F-measure
     */
    protected double fMeasure(AMapping predictions) {
        if (isUnsupervised) {
            // compute pseudo-F-Measure
            return pseudoFMeasure.calculate(predictions, new GoldStandard(null, sourceUris, targetUris));            
        }
        // get real F-Measure based on training data 
        return new FMeasure().calculate(predictions, new GoldStandard(trainingData));
    }


    /**
     * Looks first for the input metricExpression in the already constructed tree,
     * if found the corresponding mapping is returned.
     * Otherwise, the SetConstraintsMapper is generate the mapping from the metricExpression.
     *
     * @param metricExpression learning specifications
     * @return Mapping corresponding to the input metric expression
     */
    protected AMapping getMapingOfMetricExpression(String metricExpression) {
        AMapping map = null;
        if (RefinementNode.isSaveMapping()) {
            map = getMapingOfMetricFromTree(metricExpression, refinementTreeRoot);
        }
        if (map == null) {
            Double threshold = Double.parseDouble(metricExpression.substring(metricExpression.lastIndexOf("|") + 1, metricExpression.length()));
            Rewriter rw = RewriterFactory.getRewriter(RewriterType.DEFAULT);
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
     * @param metricExpression learning specifications
     * @param r refinement tree
     * @return return mapping of the input metricExpression from the search tree
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
     * get mapping from source cache to target cache using metricExpression
     *
     * @param ls learning specifications
     * @param sCache source Cache
     * @param tCache target Cache
     * @return Mapping from sCache to tCache
     */
    protected AMapping getPredictions(LinkSpecification ls, ACache sCache, ACache tCache) {
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


    public PseudoFMeasure getPseudoFMeasure() {
        return pseudoFMeasure;
    }

    public AMapping getTrainingData() {
        return trainingData;
    }

    @Override
    protected void init(List<LearningParameter> lp, ACache sourceCache, ACache targetCache) {
        super.init(lp, sourceCache, targetCache);
        sourcePropertiesCoverageMap = LinearSelfConfigurator.getPropertyStats(sourceCache, getMinPropertyCoverage());
        targetPropertiesCoverageMap = LinearSelfConfigurator.getPropertyStats(targetCache, getMinPropertyCoverage());
        RefinementNode.setSaveMapping(saveMapping());
    }



    public boolean isUnsupervised() {
        return isUnsupervised;
    }


    /**
     * calculate either a real or a pseudo-Precision
     *
     * @param predictions Mapping
     * @return precision
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
     * @param predictions Mapping
     * @return recall
     */
    protected double recall(AMapping predictions) {
        if (isUnsupervised) {
            // compute pseudo-recall
            return pseudoFMeasure.recall(predictions, new GoldStandard(null, sourceUris, targetUris));
        }
        // get real recall based on training data 
        return new Recall().calculate(predictions, new GoldStandard(trainingData));

    }
    
    /**
     * Computes the atomic classifiers by finding the highest possible F-measure
     * achievable on a given property pair
     *
     * @param sourceProperty Property of source to use
     * @param targetProperty Property of target to use
     * @param measure Measure to be used
     * @return Best simple classifier
     */
    protected ExtendedClassifier findInitialClassifier(String sourceProperty, String targetProperty, String measure) {
        double maxOverlap = 0;
        double theta = 1.0;
        AMapping bestMapping = MappingFactory.createDefaultMapping();
        for (double threshold = 1d; threshold > getMinPropertyCoverage(); threshold = threshold * getPropertyLearningRate()) {
            AMapping mapping = executeAtomicMeasure(sourceProperty, targetProperty, measure, threshold);
            double overlap = recall(mapping);
            if (maxOverlap < overlap) { //only interested in largest threshold with recall 1
                bestMapping = mapping;
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

    @Override
    public void setDefaultParameters() {
        //default parameters
        long maxRefineTreeSize = 2000;
        int maxIterationNumber = 3;
        int maxIterationTimeInMin = 20;
        int maxExecutionTimeInMin = 600;
        double maxFitnessThreshold = 1;
        double childrenPenaltyWeight = 1;
        double complexityPenaltyWeight = 1;
        boolean saveMapping = true;
        double minPropertyCoverage = 0.4;
        double propertyLearningRate = 0.9;
        double overallPenaltyWeight = 0.5d;
        boolean verbose = false;
        Set<String> measures = new HashSet<>(Arrays.asList("jaccard", "trigrams", "cosine", "qgrams"));
        
        learningParameters = new ArrayList<>();
        learningParameters.add(new LearningParameter(PARAMETER_MAX_REFINEMENT_TREE_SIZE, maxRefineTreeSize, Long.class, 10d, Long.MAX_VALUE, 10d, PARAMETER_MAX_REFINEMENT_TREE_SIZE));
        learningParameters.add(new LearningParameter(PARAMETER_MAX_ITERATIONS_NUMBER, maxIterationNumber, Integer.class, 1d, Integer.MAX_VALUE, 10d, PARAMETER_MAX_ITERATIONS_NUMBER));
        learningParameters.add(new LearningParameter(PARAMETER_MAX_ITERATION_TIME_IN_MINUTES, maxIterationTimeInMin, Integer.class, 1d, Integer.MAX_VALUE,1, PARAMETER_MAX_ITERATION_TIME_IN_MINUTES));
        learningParameters.add(new LearningParameter(PARAMETER_EXECUTION_TIME_IN_MINUTES, maxExecutionTimeInMin, Integer.class, 1d, Integer.MAX_VALUE,1,PARAMETER_EXECUTION_TIME_IN_MINUTES));
        learningParameters.add(new LearningParameter(PARAMETER_MAX_FITNESS_THRESHOLD, maxFitnessThreshold, Double.class, 0d, 1d, 0.01d, PARAMETER_MAX_FITNESS_THRESHOLD));
        learningParameters.add(new LearningParameter(PARAMETER_MIN_PROPERTY_COVERAGE, minPropertyCoverage, Double.class, 0d, 1d, 0.01d, PARAMETER_MIN_PROPERTY_COVERAGE));
        learningParameters.add(new LearningParameter(PARAMETER_PROPERTY_LEARNING_RATE, propertyLearningRate,Double.class, 0d, 1d, 0.01d, PARAMETER_PROPERTY_LEARNING_RATE));
        learningParameters.add(new LearningParameter(PARAMETER_OVERALL_PENALTY_WEIGHT, overallPenaltyWeight, Double.class, 0d, 1d, 0.01d, PARAMETER_OVERALL_PENALTY_WEIGHT));
        learningParameters.add(new LearningParameter(PARAMETER_CHILDREN_PENALTY_WEIGHT, childrenPenaltyWeight, Double.class, 0d, 1d, 0.01d, PARAMETER_CHILDREN_PENALTY_WEIGHT));
        learningParameters.add(new LearningParameter(PARAMETER_COMPLEXITY_PENALTY_WEIGHT, complexityPenaltyWeight, Double.class, 0d, 1d, 0.01d, PARAMETER_COMPLEXITY_PENALTY_WEIGHT));
        learningParameters.add(new LearningParameter(PARAMETER_VERBOSE, verbose, Boolean.class, 0, 1, 0, PARAMETER_VERBOSE));
        learningParameters.add(new LearningParameter(PARAMETER_ATOMIC_MEASURES, measures, MeasureType.class, 0, 0, 0, PARAMETER_ATOMIC_MEASURES));
        learningParameters.add(new LearningParameter(PARAMETER_SAVE_MAPPING, saveMapping, Boolean.class, 0, 1, 0, PARAMETER_SAVE_MAPPING));
    }

    protected boolean isVerbose() {
        return Boolean.parseBoolean((String) getParameter(PARAMETER_VERBOSE).toString());
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
        Set<String> atomicMeasures = new HashSet<String>();
        
        String measuresAsString = getParameter(PARAMETER_ATOMIC_MEASURES).toString().replace("[","").replace("]", "");
        for(String m : measuresAsString.split(",")){
            atomicMeasures.add(m.trim());
        }
        return atomicMeasures;
    }
    
    private boolean saveMapping() {
        return Boolean.parseBoolean(getParameter(PARAMETER_SAVE_MAPPING).toString());
    }
    
}
