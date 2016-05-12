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
import org.aksw.limes.core.evaluation.qualititativeMeasures.Recall;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory.ExecutionEngineType;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory.ExecutionPlannerType;
import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.execution.rewriter.Rewriter;
import org.aksw.limes.core.execution.rewriter.RewriterFactory;
import org.aksw.limes.core.execution.rewriter.RewriterFactory.RewriterFactoryType;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MappingFactory.MappingType;
import org.apache.log4j.Logger;

/**
 * @author sherif
 *
 */
/**
 * @author sherif
 *
 */
public abstract class Wombat {   
	static Logger logger = Logger.getLogger(Wombat.class.getName());
	
	protected Tree<RefinementNode> root = null;
	protected static double MAX_FITNESS_THRESHOLD = 1;
	protected static long MAX_TREE_SIZE = 2000;//10000;
	protected static int MAX_ITER_NR = 3;//Integer.MAX_VALUE;
	protected static int MAX_ITER_TIME_MIN = 10;
	protected double MIN_THRESHOLD = 0.4;
	protected double learningRate = 0.9;
	protected boolean verbose = false;
	protected Map<String, Double> sourcePropertiesCoverageMap; //coverage map for latter computations
	protected Map<String, Double> targetPropertiesCoverageMap; //coverage map for latter computations
	protected double minCoverage;
	protected Cache source, target;
	protected Mapping reference;
	
	public enum Operator {
		AND, OR, MINUS
	};
	
	
	//TODO: make measures configurables
	Set<String> measures = new HashSet<>(Arrays.asList(
			"jaccard"
			,"trigrams"
			,"cosine"
			,"ngrams"
			));
	
	abstract Mapping getMapping();
	abstract String getMetricExpression();
	
    /**
     * @return initial classifiers
     */
    public List<ExtendedClassifier> getAllInitialClassifiers() {
    	logger.info("Geting all initial classifiers ...");
        List<ExtendedClassifier> initialClassifiers = new ArrayList<>();
        for (String p : sourcePropertiesCoverageMap.keySet()) {
            for (String q : targetPropertiesCoverageMap.keySet()) {
                for (String m : measures) {
                    ExtendedClassifier cp = getInitialClassifier(p, q, m);
                    //only add if classifier covers all entries
                    initialClassifiers.add(cp);
                }
            }
        }
        logger.info("Done computing all initial classifiers.");
        return initialClassifiers;
    }
    
    /**
     * Computes the atomic classifiers by finding the highest possible F-measure
     * achievable on a given property pair
     *
     * @param source Source cache
     * @param target Target cache
     * @param sourceProperty Property of source to use
     * @param targetProperty Property of target to use
     * @param measure Measure to be used
     * @param reference Reference mapping
     * @return Best simple classifier
     */
    private ExtendedClassifier getInitialClassifier(String sourceProperty, String targetProperty, String measure) {
        double maxOverlap = 0;
        double theta = 1.0;
        Mapping bestMapping = MappingFactory.createMapping(MappingType.MEMORY_MAPPING);
        for (double threshold = 1d; threshold > MIN_THRESHOLD; threshold = threshold * learningRate) {
            Mapping mapping = executeAtomicMeasure(sourceProperty, targetProperty, measure, threshold);
            double overlap = new Recall().calculate(mapping, new GoldStandard(reference));
            if (maxOverlap < overlap) //only interested in largest threshold with recall 1
            {
                bestMapping = mapping;
                theta = threshold;
                maxOverlap = overlap;
                bestMapping = mapping;
//                System.out.println("Works for " + sourceProperty + ", " + targetProperty + ", " + threshold + ", " + overlap);
            }
        }
        ExtendedClassifier cp = new ExtendedClassifier(measure, theta);
        cp.fMeasure = maxOverlap;
        cp.sourceProperty = sourceProperty;
        cp.targetProperty = targetProperty;
        cp.mapping = bestMapping;
        return cp;
    }

	/**
	 * @param source cache
	 * @param target cache
	 * @param sourceProperty
	 * @param targetProperty
	 * @param measure 
	 * @param threshold
	 * @return Mapping from source to target resources after applying 
	 * 		   the atomic mapper measure(sourceProperity, targetProperty)
	 */
	public Mapping executeAtomicMeasure(String sourceProperty, String targetProperty, String measure, double threshold) {
		String measureExpression = measure + "(x." + sourceProperty + ", y." + targetProperty + ")";
		Instruction inst = new Instruction(Instruction.Command.RUN, measureExpression, threshold + "", -1, -1, -1);
		ExecutionEngine ee = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, source, target, "?x", "?y");
		Plan plan = new Plan();
		plan.addInstruction(inst);
		return ee.execute(plan);
	}
	
	/**
	 * Looks first for the input metricExpression in the already constructed tree,
	 * if found the corresponding mapping is returned. 
	 * Otherwise, the SetConstraintsMapper is generate the mapping from the metricExpression.
	 * @param metricExpression
	 * @return Mapping corresponding to the input metric expression 
	 * @author sherif
	 */
	protected Mapping getMapingOfMetricExpression(String metricExpression) {
		Mapping map = null;
		if(RefinementNode.saveMapping){
			map = getMapingOfMetricFromTree( metricExpression,root);
		}
		if(map == null){
			Double threshold = Double.parseDouble(metricExpression.substring(metricExpression.lastIndexOf("|")+1, metricExpression.length()));
			Rewriter rw = RewriterFactory.getRewriter(RewriterFactoryType.DEFAULT);
			LinkSpecification ls = new LinkSpecification(metricExpression, threshold);
			LinkSpecification rwLs = rw.rewrite(ls);
			IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT, source, target);
			assert planner != null;
			NestedPlan plan = planner.plan(rwLs);
			ExecutionEngine engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, source, target,"?x", "?y");
			assert engine != null;
			Mapping resultMap = engine.execute(plan);
			map = resultMap.getSubMap(threshold);
		}
		return map;
	}
	
	/**
	 * @param string
	 * @return return mapping of the input metricExpression from the search tree 
	 * @author sherif
	 */
	protected Mapping getMapingOfMetricFromTree(String metricExpression, Tree<RefinementNode> r) {
		if(r!= null){
			if(r.getValue().metricExpression.equals(metricExpression)){
				return r.getValue().map;
			}
			if(r.getchildren() != null && r.getchildren().size() > 0){
				for(Tree<RefinementNode> c : r.getchildren()){
					Mapping map = getMapingOfMetricFromTree(metricExpression, c);
					if(map != null && map.size() != 0){
						return map;
					}
				}	
			}
		}
		return null;
	}
	
//	/** OLD CODE
//	 * Looks first for the input metricExpression in the already constructed tree,
//	 * if found the corresponding mapping is returned. 
//	 * Otherwise, the SetConstraintsMapper is generate the mapping from the metricExpression.
//	 * @param metricExpression
//	 * @return Mapping corresponding to the input metric expression 
//	 * @author sherif
//	 */
//	private Mapping getMapingOfMetric(String metricExpression) {
//		Mapping map = null;
//		if(RefinementNode.saveMapping){
//			map = getMapingOfMetricFromTree( metricExpression,root);
//		}
//		if(map == null){
//			//			logger.info("Generating mapping for: " + metricExpression);
//			SetConstraintsMapper mapper = SetConstraintsMapperFactory.getMapper("simple",
//					new KBInfo("?x"), new KBInfo("?y"), source, target, new LinearFilter(), 2);
//			String expression = metricExpression.substring(0, metricExpression.lastIndexOf("|"));
//			Double threshold = Double.parseDouble(metricExpression.substring(metricExpression.lastIndexOf("|")+1, metricExpression.length()));
//			map = mapper.getLinks(expression, threshold);
//		}
//		return map;
//	}
}
