/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.wombat;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.Tree;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MappingFactory.MappingType;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.MLResult;
import org.aksw.limes.core.ml.algorithm.euclid.LinearSelfConfigurator;
import org.aksw.limes.core.ml.setting.LearningSetting;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


/**
 *
 * @author sherif
 */
public class UnsupervisedCompleteWombat extends Wombat {
	protected static final String ALGORITHM_NAME = "Unsupervised Complete Wombat";
	
	static Logger logger = Logger.getLogger(UnsupervisedCompleteWombat.class.getName());
	private static int EXPERIMENT_MAX_TIME_IN_MINUTES = 10;
	public Tree<RefinementNode> root = null;
	private int iterationNr = 0;
	private Map<String, Mapping>  diffs;
	private RefinementNode bestSolution = null;
	private static final double BETA = 2.0;
	public static List<String> sourceUris; 
	public static List<String> targetUris;


	/**
	 * ** TODO 
	 * 1- Get relevant source and target resources from sample 
	 * 2- Sample source and target caches 
	 * 3- Run algorithm on samples of source and target 
	 * 4- Get mapping function 
	 * 5- Execute on the whole
	 */
	/**
	 * Constructor
	 *
	 * @param sourceCache
	 * @param targetCache
	 * @param examples
	 * @param minCoverage
	 */
	public UnsupervisedCompleteWombat(Cache sourceCache, Cache targetCache, Mapping examples, double minCoverage, Configuration configuration) {
    	super(sourceCache, targetCache, configuration);
		sourcePropertiesCoverageMap = LinearSelfConfigurator.getPropertyStats(sourceCache, minCoverage);
		targetPropertiesCoverageMap = LinearSelfConfigurator.getPropertyStats(targetCache, minCoverage);
		this.minCoverage = minCoverage;
		measures = new HashSet<>(Arrays.asList("jaccard", "trigrams"));	
		sourceUris = sourceCache.getAllUris(); 
		targetUris = targetCache.getAllUris();
	}



	/* (non-Javadoc)
	 * @see de.uni_leipzig.simba.lgg.LGG#getMapping()
	 */
	public Mapping getMapping() {
		if(bestSolution == null){
			bestSolution =  getBestSolution();
		}
		if(RefinementNode.saveMapping){
			return bestSolution.map;
		}
		return getMapingOfMetricExpression(bestSolution.metricExpression);
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
		List<ExtendedClassifier> classifiers = getAllInitialClassifiers();
		diffs = getClassifiersDiffPermutations(classifiers);
		createRefinementTreeRoot();
		Tree<RefinementNode> mostPromisingNode = findMostPromisingNode(root, false);
		logger.info("Most promising node: " + mostPromisingNode.getValue());
		iterationNr ++;
		long endTime = System.currentTimeMillis() + EXPERIMENT_MAX_TIME_IN_MINUTES * 60000; 
		while((mostPromisingNode.getValue().fMeasure) < MAX_FITNESS_THRESHOLD	 
				&& (System.currentTimeMillis() < endTime)
				&& root.size() <= MAX_TREE_SIZE
				&& iterationNr <= MAX_ITER_NR)
		{
			iterationNr++;
			mostPromisingNode = expandNode(mostPromisingNode);
			mostPromisingNode = findMostPromisingNode(root, false);
			if(mostPromisingNode.getValue().fMeasure == -Double.MAX_VALUE){
				break; // no better solution can be found
			}
			logger.info("Most promising node: " + mostPromisingNode.getValue());
		}
		RefinementNode bestSolution = findMostPromisingNode(root, true).getValue();
		logger.info("Overall Best Solution: " + bestSolution);
		if(!RefinementNode.saveMapping){
			bestSolution.map = getMapingOfMetricExpression(bestSolution.metricExpression);
		}
		return bestSolution;
	}

	/**
	 * @param c initial classifiers
	 * @return all permutations of x\y for each x,y in classifiers and x!=y
	 * @author sherif
	 */
	private Map<String, Mapping> getClassifiersDiffPermutations(List<ExtendedClassifier> c) {
		Map<String, Mapping> diffs = new HashMap<>();
		for(int i = 0 ; i < c.size() ; i++){
			for(int j = 0 ; j < c.size() ; j++){
				if(i != j ){
					Mapping m = MappingOperations.difference(c.get(i).mapping, c.get(j).mapping);
					String e = "MINUS(" + c.get(i).getMetricExpression() + "," + c.get(j).getMetricExpression() + ")|0.0"; 
					diffs.put(e ,m);	
				}
			}	
		}
		return diffs;
	}
	/**
	 * initiate the refinement tree as a root node  with set of 
	 * children nodes containing all permutations of x\y 
	 * for each x,y in classifiers and x!=y
	 * @return
	 * @author sherif
	 */
	private void createRefinementTreeRoot(){
		RefinementNode initialNode = new RefinementNode(-Double.MAX_VALUE, MappingFactory.createMapping(MappingType.DEFAULT), "");
		root = new Tree<RefinementNode>(null,initialNode, null);
		for( String diffExpr : diffs.keySet()){
			Mapping diffMapping = diffs.get(diffExpr);
			RefinementNode n = createNode(diffExpr,diffMapping);
			root.addChild(new Tree<RefinementNode>(root,n, null));
		}
		if(verbose){
			System.out.println("Tree size:" + root.size());
			root.print();
		}
	}


	/**
	 * 
	 * @param node Refinement node to be expanded
	 * @return The input tree node after expansion
	 * @author sherif
	 */
	private Tree<RefinementNode> expandNode(Tree<RefinementNode> node) {
		// Add children
		List<RefinementNode> childrenNodes = refine(node);
		for (RefinementNode n : childrenNodes) {
			if(!inRefinementTree(n.metricExpression)){
				node.addChild(new Tree<RefinementNode>(n));
			}
		}
		// Add sibling (if any)
		if(node.level() == 1){
			List<RefinementNode> siblingNodes = createConjunctionsWithDiffNodes(node);
			for (RefinementNode n : siblingNodes) {
				if(!inRefinementTree(n.metricExpression)){
					node.getParent().addChild(new Tree<RefinementNode>(n));
				}
			}
		}
		if(verbose){
			System.out.println("Tree size:" + root.size());
			root.print();
		}
		return node;
	}

	/**
	 * @param metricExpression
	 * @return true if the input metricExpression already contained 
	 * 			in one of the search tree nodes, false otherwise  
	 * @author sherif
	 */
	private boolean inRefinementTree(String metricExpression) {
		return inRefinementTree(metricExpression, root);
	}

	/**
	 * @param metricExpression
	 * @param treeRoot
	 * @return true if the input metricExpression already contained 
	 * 			in one of the search tree nodes, false otherwise  
	 * @author sherif
	 */
	private boolean inRefinementTree(String metricExpression, Tree<RefinementNode> treeRoot) {
		if(treeRoot == null){
			return false;
		}
		if(treeRoot.getValue().metricExpression.equals(metricExpression)){
			return true;
		}
		if(treeRoot.getchildren() != null){
			for(Tree<RefinementNode> n : treeRoot.getchildren()){
				if(inRefinementTree(metricExpression, n)){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Apply refinement operator 
	 * @param node
	 * @return list of all children
	 * @author sherif
	 */
	private List<RefinementNode> refine(final Tree<RefinementNode> node) {
		List<RefinementNode> result = new ArrayList<>();
		String 	childMetricExpr = new String();
		Mapping childMap = MappingFactory.createMapping(MappingType.DEFAULT);
		String 	nodeMetricExpr = node.getValue().metricExpression;

		if(isRoot(nodeMetricExpr)){
			for(String diffExpr : diffs.keySet()){
				Mapping diffMapping = diffs.get(diffExpr);
				result.add(createNode(diffExpr, diffMapping));
			}
			return result;
		}else if(isAtomic(nodeMetricExpr)){
			return createDisjunctionsWithDiffNodes(node);
		}else if(isDifference(nodeMetricExpr)){
			String firstMetricExpr = getSubMetricExpressions(nodeMetricExpr).get(0);
			Mapping firstMetricExprMapping = getMapingOfMetricExpression(firstMetricExpr);
			result.add(createNode(firstMetricExpr, firstMetricExprMapping));
			result.addAll(createDisjunctionsWithDiffNodes(node));
			return result;
		}else if(isConjunction(nodeMetricExpr)){
			childMetricExpr = new String();
			List<String> subMetricExpr = getSubMetricExpressions(nodeMetricExpr);
			result.add(createNode(subMetricExpr.get(0)));
			List<String> childSubMetricExpr = new ArrayList<>();
			for(int i = 0 ; i < subMetricExpr.size() ; i++){
				for(int j = 0 ; j < subMetricExpr.size() ; j++){
					if(i == j){
						for(RefinementNode n : refine(new Tree<RefinementNode>(createNode(subMetricExpr.get(i))))){
							childSubMetricExpr.add(n.metricExpression);
						}
					}else{
						childSubMetricExpr.add(subMetricExpr.get(i));
					}
				}
				childMetricExpr += "AND(" + childSubMetricExpr.get(0)+ "," + childSubMetricExpr.get(1) + ")|0.0";
				childMap = MappingOperations.intersection(getMapingOfMetricExpression(childSubMetricExpr.get(0)), getMapingOfMetricExpression(childSubMetricExpr.get(1)));
				for(int k = 2 ; k <childSubMetricExpr.size();  k++){
					childMetricExpr = "AND(" + childMetricExpr + "," + childSubMetricExpr.get(k) + ")|0.0";
					childMap = MappingOperations.intersection(childMap, getMapingOfMetricExpression(childSubMetricExpr.get(k)));
				}
				result.add(createNode(childMetricExpr, childMap));
				childMetricExpr = new String();
			}
			result.addAll(createDisjunctionsWithDiffNodes(node));
			return result;
		}else if(isDisjunction(nodeMetricExpr)){
			childMetricExpr = new String();
			List<String> subMetricExpr = getSubMetricExpressions(nodeMetricExpr);
			//			System.out.println("-------------subMetricExpr: "+ subMetricExpr);
			result.add(createNode(subMetricExpr.get(0)));
			List<String> childSubMetricExpr = new ArrayList<>();
			for(int i = 0 ; i < subMetricExpr.size() ; i++){
				for(int j = 0 ; j < subMetricExpr.size() ; j++){
					if(i == j){
						for(RefinementNode n : refine(new Tree<RefinementNode>(createNode(subMetricExpr.get(i))))){
							childSubMetricExpr.add(n.metricExpression);
						}
					}else{
						childSubMetricExpr.add(subMetricExpr.get(i));
					}
				}
				childMetricExpr += "OR(" + childSubMetricExpr.get(0)+ "," + childSubMetricExpr.get(1) + ")|0.0";
				childMap = MappingOperations.union(getMapingOfMetricExpression(childSubMetricExpr.get(0)), getMapingOfMetricExpression(childSubMetricExpr.get(1)));
				for(int k = 2 ; k <childSubMetricExpr.size();  k++){
					childMetricExpr = "OR(" + childMetricExpr + "," + childSubMetricExpr.get(k) + ")|0.0";
					childMap = MappingOperations.union(childMap, getMapingOfMetricExpression(childSubMetricExpr.get(k)));
				}
				result.add(createNode(childMetricExpr, childMap));
				childMetricExpr = new String();
			}
			result.addAll(createDisjunctionsWithDiffNodes(node));
			return result;
		}else{
			logger.error("Wrong metric expression: " + nodeMetricExpr);
			System.exit(1);
		}
		return result;
	}


	/**
	 * @param nodeMetricExpr
	 * @param nodeMapping
	 * @return list of nodes L ∪ A_i \ A_j | A_i ∈ P, A_j ∈ P, where P is the set if initial classifiers
	 * @author sherif
	 */
	private List<RefinementNode> createDisjunctionsWithDiffNodes(Tree<RefinementNode> node) {
		List<RefinementNode> result = new ArrayList<>();
		for(String diffExpr : diffs.keySet()){
			Mapping diffMapping = diffs.get(diffExpr);
			String childMetricExpr = "OR(" + node.getValue().metricExpression + "," + diffExpr + ")|0.0" ;
			Mapping nodeMaping = MappingFactory.createMapping(MappingType.DEFAULT);
			if(RefinementNode.saveMapping){
				nodeMaping = node.getValue().map;
			}else{
				nodeMaping = getMapingOfMetricExpression(node.getValue().metricExpression);
			}
			Mapping childMap = MappingOperations.union(nodeMaping, diffMapping);
			result.add(createNode(childMetricExpr, childMap));
		}
		return result;
	}

	/**
	 * @param nodeMetricExpr
	 * @param nodeMapping
	 * @return list of nodes L ∪ A_i \ A_j | A_i ∈ P, A_j ∈ P, where P is the set if initial classifiers
	 * @author sherif
	 */
	private List<RefinementNode> createConjunctionsWithDiffNodes(Tree<RefinementNode> node) {
		List<RefinementNode> result = new ArrayList<>();
		for(String diffExpr : diffs.keySet()){
			Mapping diffMapping = diffs.get(diffExpr);
			Mapping nodeMaping = MappingFactory.createMapping(MappingType.DEFAULT);
			if(RefinementNode.saveMapping){
				nodeMaping = node.getValue().map;
			}else{
				nodeMaping = getMapingOfMetricExpression(node.getValue().metricExpression);
			}
			String childMetricExpr = "AND(" + node.getValue().metricExpression + "," + diffExpr + ")|0.0" ;
			Mapping childMap = MappingOperations.intersection(nodeMaping, diffMapping);
			result.add(createNode(childMetricExpr, childMap));
		}
		return result;
	}


	/**
	 * @param nodeMetricExpr
	 * @return
	 * @author sherif
	 */
	private boolean isRoot(String nodeMetricExpr) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param nodeMetricExpr
	 * @return
	 * @author sherif
	 */
	private List<String> getSubMetricExpressions(String metricExpr) {
		//		System.out.println("metricExpr:" +metricExpr);
		List<String> result = new ArrayList<>();
		double threshold = Double.parseDouble(metricExpr.substring(metricExpr.lastIndexOf("|")+1, metricExpr.length()));;
		//		System.out.println("threshold: " +threshold);
		String metric = metricExpr.substring(0, metricExpr.lastIndexOf("|"));
		//		System.out.println("metric: " +metric);
		Parser p = new Parser(metric, threshold );
		result.add(p.getTerm1() + "|" + p.getCoef1());
		result.add(p.getTerm2() + "|" + p.getCoef2());
		return result;
	}





	private RefinementNode createNode(String metricExpr) {
		Mapping mapping = getMapingOfMetricExpression(metricExpr);
		return createNode(metricExpr, mapping);
	}

	private RefinementNode createNode(String metricExpr,Mapping mapping) {
		double fscore = new PseudoFMeasure().getPseudoFMeasure(mapping, new GoldStandard(null, sourceUris, targetUris), BETA);
		return createNode(metricExpr,mapping,fscore);
	}

	private RefinementNode createNode(String metricExpr,Mapping mapping, double fscore) {
		if(RefinementNode.saveMapping){
			return new RefinementNode(fscore, mapping, metricExpr);
		}
		return new RefinementNode(fscore, null, metricExpr);
	}


	/**
	 * @param l
	 * @return
	 * @author sherif
	 */
	private boolean isDisjunction(String l) {
		return l.startsWith("OR");
	}

	/**
	 * @param l
	 * @return
	 * @author sherif
	 */
	private boolean isConjunction(String l) {
		return l.startsWith("AND");
	}

	/**
	 * @param l
	 * @return
	 * @author sherif
	 */
	private boolean isDifference(String l) {
		return l.startsWith("MINUS");
	}

	/**
	 * @param l
	 * @return
	 * @author sherif
	 */
	private boolean isAtomic(String l) {
		if(!isDifference(l) && !isConjunction(l) && !isDisjunction(l))
			return true;
		return false;
	}

	/**
	 * Get the most promising node as the node with the best F-score
	 *  
	 * @param r the refinement search tree
	 * @param overall set true to get the best over all node (normally at the end of the algorithm)
	 * 				  if set to false you got only the best leaf
	 * @return most promising node from the input tree r
	 * @author sherif
	 */
	private Tree<RefinementNode> findMostPromisingNode(Tree<RefinementNode> r, boolean overall){
		// trivial case
		if(r.getchildren() == null || r.getchildren().size() == 0){
			return r;
		}
		// get the most promising child
		Tree<RefinementNode> mostPromisingChild = new Tree<RefinementNode>(new RefinementNode());
		for(Tree<RefinementNode> child : r.getchildren()){
			if(child.getValue().fMeasure >= 0){
				Tree<RefinementNode> promisingChild = findMostPromisingNode(child, overall);
				if( promisingChild.getValue().fMeasure > mostPromisingChild.getValue().fMeasure  ){
					mostPromisingChild = promisingChild;
				}else if((promisingChild.getValue().fMeasure == mostPromisingChild.getValue().fMeasure)
						&& (computeExpressionComplexity(promisingChild) < computeExpressionComplexity(mostPromisingChild))){
					mostPromisingChild = promisingChild;
				}
			}
		}
		if(overall){ // return the best leaf
			return mostPromisingChild;
		}else // return the best over all node 
			if((r.getValue().fMeasure > mostPromisingChild.getValue().fMeasure)
					|| (r.getValue().fMeasure == mostPromisingChild.getValue().fMeasure
					&& computeExpressionComplexity(r) < computeExpressionComplexity(mostPromisingChild))){
				return r;
			}else{
				return mostPromisingChild;
			}
	}

	/**
	 * @param node
	 * @return Complexity of the input node as the number of operators included in its metric expression
	 * @author sherif
	 */
	private int computeExpressionComplexity(Tree<RefinementNode> node) {
		String e = node.getValue().metricExpression;
		return  StringUtils.countMatches(e, "OR(") + 
				StringUtils.countMatches(e, "AND(") + 
				StringUtils.countMatches(e, "MINUS(");
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



	/**
	 * run conjunctive merge
	 */
	@Override
	public Mapping computePredictions() {
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





}
