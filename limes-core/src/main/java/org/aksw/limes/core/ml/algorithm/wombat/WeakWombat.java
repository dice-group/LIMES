/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.wombat;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.datastrutures.Tree;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MappingFactory.MappingType;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.MLResult;
import org.aksw.limes.core.ml.setting.LearningSetting;
import org.apache.log4j.Logger;



/**
 * @author sherif
 *
 */
public class WeakWombat extends Wombat {
	protected static final String ALGORITHM_NAME = "Weak Wombat";
	private static Logger logger = Logger.getLogger(WeakWombat.class.getName());

	protected double penaltyWeight = 0.5d;

	protected static long CHILDREN_PENALTY_WEIGHT = 1;
	protected static long COMPLEXITY_PENALTY_WEIGHT = 1;
	protected boolean STRICT = true;

	protected Set<String> measures;
	protected Mapping reference;

	protected List<ExtendedClassifier> classifiers = null;
	protected int iterationNr = 0;

	private RefinementNode bestSolution;


	/**
	 * Constructor
	 *
	 * @param sourceCache
	 * @param targetChache
	 * @param examples
	 * @param minCoverage
	 */
	public WeakWombat(Cache sourceCache, Cache targetChache, Mapping examples, Configuration configuration) {
		super(sourceCache, targetChache, configuration);
		this.sourceCache = sourceCache;
		this.targetCache = targetChache;
		measures = new HashSet<>(Arrays.asList(
				"jaccard",
				"trigrams"
				//"cosine",
				//"hausdorff"
				));
		reference = examples;
	}

	
	/* (non-Javadoc)
	 * @see de.uni_leipzig.simba.lgg.LGG#getMapping()
	 */
	public Mapping getMapping() {
		if(bestSolution == null){
			bestSolution =  getBestSolution();
		}
		return bestSolution.map;
	}
	

	public String getMetricExpression() {
		if(bestSolution == null){
			bestSolution =  getBestSolution();
		}
		return bestSolution.metricExpression;
	}
	
	

	/**
	 * @return RefinementNode containing the best over all solution
	 * @author sherif
	 */
	public RefinementNode getBestSolution(){
		classifiers = getAllInitialClassifiers();
		createRefinementTreeRoot();
		Tree<RefinementNode> mostPromisingNode = getMostPromisingNode(refinementTreeRoot, penaltyWeight);
		logger.info("Most promising node: " + mostPromisingNode.getValue());
		iterationNr ++;
		while((mostPromisingNode.getValue().fMeasure) < maxFitnessThreshold	 
				&& refinementTreeRoot.size() <= maxRefineTreeSize
				&& iterationNr <= maxIterationNumber)
		{
			iterationNr++;
			mostPromisingNode = expandNode(mostPromisingNode);
			mostPromisingNode = getMostPromisingNode(refinementTreeRoot, penaltyWeight);
			if(mostPromisingNode.getValue().recall == -Double.MAX_VALUE){
				break; // no better solution can be found
			}
			logger.info("Most promising node: " + mostPromisingNode.getValue());
		}
		RefinementNode bestSolution = getMostPromisingNode(refinementTreeRoot, 0).getValue();
		logger.info("Overall Best Solution: " + bestSolution);
		return bestSolution;
	}

	/**
	 * initiate the refinement tree as a root node  
	 * with one child node contains a union of all initial classifiers
	 * @return
	 * @author sherif
	 */
	private void createRefinementTreeRoot(){
		RefinementNode initialNode = new RefinementNode();
		refinementTreeRoot = new Tree<RefinementNode>(null,initialNode, null);
		Mapping unionMapping = classifiers.get(0).mapping;
		String unionMetricExpr =  classifiers.get(0).getMetricExpression();
		for (int i = 1; i < classifiers.size(); i++) {
			unionMapping = MappingOperations.union(unionMapping, classifiers.get(i).mapping);
			unionMetricExpr = "OR(" + unionMetricExpr + "," + classifiers.get(i).getMetricExpression() + ")|0";
		}
		RefinementNode n = new RefinementNode(unionMapping, unionMetricExpr,reference);
		refinementTreeRoot.addChild(new Tree<RefinementNode>(refinementTreeRoot,n, null));
		if(verbose){
			refinementTreeRoot.print();
		}
	}

	/**
	 * Expand an input refinement node by applying 
	 * all available operators to the input refinement 
	 * node's mapping with all other classifiers' mappings
	 *   
	 * @param node Refinement node to be expanded
	 * @return The input tree node after expansion
	 * @author sherif
	 */
	private Tree<RefinementNode> expandNode(Tree<RefinementNode> node) {
		Mapping map = MappingFactory.createMapping(MappingType.DEFAULT);
		for(ExtendedClassifier c : classifiers ){
			for(Operator op : Operator.values()){
				if(node.getValue().metricExpression != c.getMetricExpression()){ // do not create the same metricExpression again 
					if(op.equals(Operator.AND)){
						map = MappingOperations.intersection(node.getValue().map, c.mapping);
					}else if(op.equals(Operator.MINUS)){
						map = MappingOperations.difference(node.getValue().map, c.mapping);
					}
					String metricExpr = op + "(" + node.getValue().metricExpression + "," + c.getMetricExpression() +")|0";
					RefinementNode child = new RefinementNode(map, metricExpr,reference);
					node.addChild(new Tree<RefinementNode>(child));
				}
			}
		}
		if(verbose){
			refinementTreeRoot.print();
		}
		return node;
	}


	/**
	 * Get the most promising node as the node with the best recall then precision
	 *  
	 * @param r The whole refinement tree
	 * @param penaltyWeight 
	 * @return most promising node from the input tree r
	 * @author sherif
	 */
	private Tree<RefinementNode> getMostPromisingNode(Tree<RefinementNode> r, double penaltyWeight){
		// trivial case
		if(r.getchildren() == null || r.getchildren().size() == 0){
			return r;
		}
		// get mostPromesyChild of children
		Tree<RefinementNode> mostPromesyChild = new Tree<RefinementNode>(new RefinementNode());
		for(Tree<RefinementNode> child : r.getchildren()){
			if(child.getValue().recall >= 0){
				Tree<RefinementNode> promesyChild = getMostPromisingNode(child, penaltyWeight);
				double newFitness;
				newFitness = promesyChild.getValue().recall - penaltyWeight * computePenalty(promesyChild);
				if( newFitness > mostPromesyChild.getValue().recall  ){
					mostPromesyChild = promesyChild;
				}
			}
		}
		// return the argmax{root, mostPromesyChild}
		if(penaltyWeight > 0){
			return mostPromesyChild;
		}else if(r.getValue().fMeasure >= mostPromesyChild.getValue().fMeasure){
			return r;
		}else{
			return mostPromesyChild;
		}
	}


	/**
	 * @return childrenPenalty + complexityPenalty
	 * @author sherif
	 */
	private double computePenalty(Tree<RefinementNode> promesyChild) {
		long childrenCount = promesyChild.size() - 1;
		double childrenPenalty = (CHILDREN_PENALTY_WEIGHT * childrenCount) / refinementTreeRoot.size();
		long level = promesyChild.level();
		double complexityPenalty = (COMPLEXITY_PENALTY_WEIGHT * level) / refinementTreeRoot.depth();
		return  childrenPenalty + complexityPenalty;
	}


	@Override
	public String getName() {
		return ALGORITHM_NAME;
	}


	@Override
	public MLResult learn(Mapping trainingData) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Mapping computePredictions() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void init(LearningSetting parameters, Mapping trainingData) throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void terminate() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Set<String> parameters() {
		// TODO Auto-generated method stub
		return null;
	}




}
