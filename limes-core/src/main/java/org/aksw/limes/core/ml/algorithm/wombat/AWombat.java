/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.wombat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.Tree;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Precision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Recall;
import org.aksw.limes.core.evaluation.qualititativeMeasures.fuzzy.FuzzyFMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.fuzzy.FuzzyPrecision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.fuzzy.FuzzyRecall;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
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
import org.aksw.limes.core.measures.mapper.MappingOperations;
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

    private static final double MIN_INIT_CLASSIFIER_F_MEASURE = 0.1;

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

    protected Set<String> wombatParameterNames = new HashSet<>();
    protected Tree<RefinementNode> refinementTreeRoot = null;

    protected boolean isUnsupervised = false;
    protected boolean isFuzzy = false;


    protected AWombat() {
        super();
        setDefaultParameters();
    }


    /* 
     * Get a set of examples to be added to the mapping.
     *
     * @param size of the examples
     * @return the mapping
     * @throws UnsupportedMLImplementationException Exception
     * 
     * (non-Javadoc)
     * @see org.aksw.limes.core.ml.algorithm.ACoreMLAlgorithm#getNextExamples(int)
     */
    @Override
    //    protected AMapping getNextExamples(int activeLearningRate) throws UnsupportedMLImplementationException {
    //        List<RefinementNode> bestNodes = getBestKNodes(refinementTreeRoot, activeLearningRate);
    //        AMapping intersectionMapping = bestNodes.get(0).getMapping();
    //        AMapping unionMapping = bestNodes.get(0).getMapping();
    //
    //        for(int index = 1 ; index < bestNodes.size() ; index++){
    //            AMapping bestNodeMapping = bestNodes.get(index).getMapping();
    //            intersectionMapping = MappingOperations.intersection(intersectionMapping, bestNodeMapping);
    //            unionMapping = MappingOperations.union(unionMapping, bestNodeMapping);
    //        }
    //        AMapping posEntropyMapping = MappingOperations.difference(unionMapping, intersectionMapping);
    //        
    //        // special case where the same mapping in each leaf
    //        if(posEntropyMapping.size() == 0){
    //            return intersectionMapping;
    //        }
    //
    //        TreeSet<LinkEntropy> linkEntropy = new TreeSet<>();
    //
    //        for(String s : posEntropyMapping.getMap().keySet()){
    //            int entropyPos = 0, entropyNeg = 0;
    //            for(String t : posEntropyMapping.getMap().get(s).keySet()){
    //                // compute Entropy(s,t)
    //                for(RefinementNode bestNode : bestNodes){
    //                    if(bestNode.getMapping().contains(s, t)){
    //                        entropyPos++;
    //                    }else{
    //                        entropyNeg++;
    //                    }
    //                }
    //                int entropy = (activeLearningRate - entropyPos) * (activeLearningRate - entropyNeg);
    //                linkEntropy.add(new LinkEntropy(s, t, entropy));
    //            }
    //        }
    //        // get highestEntropyLinks
    //        List<LinkEntropy> highestEntropyLinks = new ArrayList<>();
    //        int i = 0;
    //        Iterator<LinkEntropy> itr = linkEntropy.descendingIterator();
    //        while(itr.hasNext() && i < activeLearningRate) {
    //            highestEntropyLinks.add(itr.next());
    //            i++;
    //        }
    //        AMapping result = MappingFactory.createDefaultMapping();
    //        for(LinkEntropy l: highestEntropyLinks){
    //            result.add(l.getSourceUri(), l.getTargetUri(), l.getEntropy());
    //        }
    //        return result;
    //    }   

    protected AMapping getNextExamples(int activeLearningRate) throws UnsupportedMLImplementationException {
        refinementTreeRoot.print();
        List<RefinementNode> bestNodes = getBestKNodes(refinementTreeRoot, activeLearningRate);
        AMapping intersectionMapping = bestNodes.get(0).getMapping();
        AMapping unionMapping = bestNodes.get(0).getMapping();
        for(int index = 1 ; index < bestNodes.size() ; index++){
            AMapping bestNodeMapping = bestNodes.get(index).getMapping();
            intersectionMapping = MappingOperations.intersection(intersectionMapping, bestNodeMapping);
            unionMapping = MappingOperations.union(unionMapping, bestNodeMapping);
        }
        AMapping distinctMapping = MappingOperations.difference(unionMapping, intersectionMapping);

        // special case where the same mapping in each leaf
        if(distinctMapping.size() == 0){
            distinctMapping = intersectionMapping;
        }

        TreeSet<LinkEntropy> linkEntropy = new TreeSet<>();

        for(String s : distinctMapping.getMap().keySet()){
            for(String t : distinctMapping.getMap().get(s).keySet()){
                if(!trainingData.contains(s, t)){
                    int mappingEntryCount = 0;
                    for(RefinementNode bestNode : bestNodes){
                        if(bestNode.getMapping().contains(s, t)){
                            mappingEntryCount++;
                        }
                    }
                    double linkProbability = (double) mappingEntryCount / (double) bestNodes.size();
                    double entropy = - linkProbability * Math.log(linkProbability);
                    entropy = (entropy == -0.0d)? 0.0d : entropy; // remove negative zero
                    linkEntropy.add(new LinkEntropy(s, t, entropy, linkProbability));
                }
            }
        }
        // get highestEntropyLinks
        List<LinkEntropy> highestEntropyLinks = new ArrayList<>();
        System.out.println(linkEntropy);
        int i = 0;
        Iterator<LinkEntropy> itr = linkEntropy.descendingIterator();
        while(itr.hasNext() && i < activeLearningRate) {
            LinkEntropy l = itr.next();
            highestEntropyLinks.add(l);
            i++;
        }
        AMapping result = MappingFactory.createDefaultMapping();
        for(LinkEntropy l: highestEntropyLinks){
            result.add(l.getSourceUri(), l.getTargetUri(), l.getProbability());
        }
        return result;
    }


    /**
     * @param r the root of the refinement tree
     * @param k number of best nodes
     * @return sorted list of best k tree nodes
     */
    protected List<RefinementNode> getBestKNodes(Tree<RefinementNode> r, int k) {
        TreeSet<RefinementNode> ts = new TreeSet<>();
        TreeSet<RefinementNode> sortedNodes = getSortedNodes(r, getOverAllPenaltyWeight(), ts);
        List<RefinementNode> resultList = new ArrayList<>();
        int i = 0;
        Iterator<RefinementNode> itr = sortedNodes.descendingIterator();
        while(itr.hasNext() && i < k) {
            RefinementNode nextNode = itr.next();
            if(nextNode.getFMeasure() > 0){
                resultList.add(nextNode);
                i++;
            }
        }
        return resultList;
    }


    /**
     * @param r the root of the refinement tree
     * @param penaltyWeight from 0 to 1
     * @param result refinement tree
     * @return sorted list of tree nodes
     */
    protected TreeSet<RefinementNode> getSortedNodes(Tree<RefinementNode> r, double penaltyWeight, TreeSet<RefinementNode> result) {
        // add current node
        if (r.getValue().getFMeasure() >= 0) {
            result.add(r.getValue());
        }

        // case leaf node
        if (r.getchildren() == null || r.getchildren().size() == 0) {
            return result;
        }else{ 
            // otherwise
            for (Tree<RefinementNode> child : r.getchildren()) {
                result.addAll(getSortedNodes(child, penaltyWeight, result));
            }
        }
        return result;
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
        if(isFuzzy){
            // compute fuzzy-F-Measure
            return new FuzzyFMeasure().calculate(predictions, new GoldStandard(trainingData));
        }
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
        if(isFuzzy){
            // compute fuzzy-precision
            return new FuzzyPrecision().calculate(predictions, new GoldStandard(trainingData));
        }
        if (isUnsupervised) {
            // compute pseudo-precision
            return pseudoFMeasure.precision(predictions, new GoldStandard(null, sourceUris, targetUris));
        }
        // compute real precision based on training data 
        return new Precision().calculate(predictions, new GoldStandard(trainingData));
    }


    /**
     * calculate either a real or a pseudo-Recall
     *
     * @param predictions Mapping
     * @return recall
     */
    protected double recall(AMapping predictions) {
        if(isFuzzy){
            // compute fuzzy-recall
            return new FuzzyRecall().calculate(predictions, new GoldStandard(trainingData));
        }
        if (isUnsupervised) {
            // compute pseudo-recall
            return pseudoFMeasure.recall(predictions, new GoldStandard(null, sourceUris, targetUris));
        }
        // compute real recall based on training data 
        return new Recall().calculate(predictions, new GoldStandard(trainingData));

    }

    /**
     * @return initial classifiers
     */
    public List<ExtendedClassifier> findInitialClassifiers() {
        logger.debug("Geting all initial classifiers ...");
        List<ExtendedClassifier> initialClassifiers = new ArrayList<>();
        for (String p : sourcePropertiesCoverageMap.keySet()) {
            for (String q : targetPropertiesCoverageMap.keySet()) {
                for (String m : getAtomicMeasures()) {
                    ExtendedClassifier cp = findInitialClassifier(p, q, m);
                    //only add if classifier covers all entries
                    if(cp.getfMeasure() > MIN_INIT_CLASSIFIER_F_MEASURE){
                        initialClassifiers.add(cp);
                    }
                }
            }
        }
        logger.debug("Done computing all initial classifiers.");
        System.out.println(initialClassifiers);
        return initialClassifiers;
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
        double maxFMeasure = 0;
        double theta = 1.0;
        AMapping bestMapping = MappingFactory.createDefaultMapping();
        for (double threshold = 1d; threshold > getMinPropertyCoverage(); threshold = threshold * getPropertyLearningRate()) {
            AMapping mapping = executeAtomicMeasure(sourceProperty, targetProperty, measure, threshold);
            double fMeasure = fMeasure(mapping);
            if (maxFMeasure < fMeasure) { //only interested in largest threshold with recall 1
                bestMapping = mapping;
                theta = threshold;
                maxFMeasure = fMeasure;
                bestMapping = mapping;
            }
        }
        ExtendedClassifier cp = new ExtendedClassifier(measure, theta, sourceProperty, targetProperty);
        cp.setfMeasure(maxFMeasure);
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

    protected boolean saveMapping() {
        return Boolean.parseBoolean(getParameter(PARAMETER_SAVE_MAPPING).toString());
    }

}
