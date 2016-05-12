/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.wombat;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.Tree;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Recall;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.euclid.LinearSelfConfigurator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


/**
 *
 * @author sherif
 */
public class CompleteWombat extends Wombat {
	static Logger logger = Logger.getLogger(CompleteWombat.class.getName());

	
	private int iterationNr = 0;
	private Map<String, Mapping>  diffs;
	private RefinementNode bestSolution = null;
	public static boolean usePruning = false;

	// for evaluation
	public int pruneNodeCount = 0;
	public long pruningTime = 0;

	/**
	 * 1- Get relevant source and target resources from sample 
	 * 2- Sample source and target caches 
	 * 3- Run algorithm on samples of source and target 
	 * 4- Get mapping function 
	 * 5- Execute on the whole
	 */
	/**
	 * Constructor
	 *
	 * @param source
	 * @param target
	 * @param examples
	 * @param minCoverage
	 */
	public CompleteWombat(Cache source, Cache target, Mapping examples, double minCoverage) {
		sourcePropertiesCoverageMap = LinearSelfConfigurator.getPropertyStats(source, minCoverage);
		targetPropertiesCoverageMap = LinearSelfConfigurator.getPropertyStats(target, minCoverage);
		this.minCoverage = minCoverage;
		this.source = source;
		this.target = target;
		this.reference = examples;
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

	/* (non-Javadoc)
	 * @see de.uni_leipzig.simba.lgg.LGG#getMetricExpression()
	 */
	@Override
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
		RefinementNode.rMax = computeMaxRecall(classifiers);
		Tree<RefinementNode> mostPromisingNode = findMostPromisingNode(root, false);
		long time = System.currentTimeMillis();
		pruneTree(root, mostPromisingNode.getValue().fMeasure);
		pruningTime += System.currentTimeMillis() - time;
		logger.info("Most promising node: " + mostPromisingNode.getValue());
		iterationNr ++;
		while((mostPromisingNode.getValue().fMeasure) < MAX_FITNESS_THRESHOLD	 
				//				&& (root.size() - pruneNodeCount) <= MAX_TREE_SIZE
				//				&& root.size() <= MAX_TREE_SIZE
				&& iterationNr <= MAX_ITER_NR)
		{
			System.out.println("Running iteration number " + iterationNr);
			iterationNr++;
			mostPromisingNode = expandNode(mostPromisingNode);
			mostPromisingNode = findMostPromisingNode(root, false);
			time = System.currentTimeMillis();
			pruneTree(root, mostPromisingNode.getValue().fMeasure);
			pruningTime += System.currentTimeMillis() - time;
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
	 * @param classifiers
	 * @return maximum achievable recall as the recall of the mapping generated
	 * 			from disjunctions of all initial mappings
	 * @author sherif
	 */
	public double computeMaxRecall(List<ExtendedClassifier> classifiers) {
		Mapping unionMaping;
		unionMaping = classifiers.get(0).mapping;
		for (int i = 1; i < classifiers.size(); i++) {
			unionMaping = MappingOperations.union(unionMaping, classifiers.get(i).mapping);
		}
		return new Recall().calculate(unionMaping, new GoldStandard(reference));
	}

	/**
	 * @param r
	 * @param fMeasure
	 * @author sherif
	 */
	private void pruneTree(Tree<RefinementNode> r, double f) {
		if(!usePruning)
			return;
		if(r.getchildren() != null && r.getchildren().size()>0){
			for(Tree<RefinementNode> child : r.getchildren()){
				if(child.getValue().maxFMeasure < f){
					prune(child);
				}else{
					pruneTree( child, f);
				}
			}
		}
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
		RefinementNode initialNode = new RefinementNode(-Double.MAX_VALUE, new MemoryMapping(), "");
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
		Mapping childMap = new MemoryMapping();
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
			Mapping nodeMaping = new MemoryMapping();
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
			Mapping nodeMaping = new MemoryMapping();
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
		List<String> result = new ArrayList<>();
		double threshold = Double.parseDouble(metricExpr.substring(metricExpr.lastIndexOf("|")+1, metricExpr.length()));;
		String metric = metricExpr.substring(0, metricExpr.lastIndexOf("|"));
		Parser p = new Parser(metric, threshold );
		result.add(p.getTerm1() + "|" + p.getCoef1());
		result.add(p.getTerm2() + "|" + p.getCoef2());
		return result;
	}


	/**
	 * @param metricExpr
	 * @param map
	 * @return
	 * @author sherif
	 */
	private RefinementNode createNode(String metricExpr,Mapping map) {
		return new RefinementNode(map, metricExpr,reference);
	}


	/**
	 * @param childMetricExpr
	 * @return
	 * @author sherif
	 */
	private RefinementNode createNode(String metricExpr) {
		Mapping map = getMapingOfMetricExpression(metricExpr);
		return createNode(metricExpr,map);
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
			if(usePruning && child.getValue().maxFMeasure < mostPromisingChild.getValue().fMeasure){
				long time = System.currentTimeMillis();
				prune(child);
				pruningTime += System.currentTimeMillis() - time;
			}
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
	 * @param child
	 * @author sherif
	 */
	private void prune(Tree<RefinementNode> t) {
		pruneNodeCount ++;
		//		t.remove();
		t.getValue().metricExpression 	= "Pruned";
		t.getValue().precision 			= -Double.MAX_VALUE;
		t.getValue().recall				= -Double.MAX_VALUE;
		t.getValue().fMeasure 			= -Double.MAX_VALUE;
		t.getValue().maxFMeasure 		= -Double.MAX_VALUE;
		t.getValue().map 				= null;
		if(t.getchildren() != null && t.getchildren().size() > 0){
			for( Tree<RefinementNode> child : t.getchildren()){
				t.removeChild(child);
			}
		}
	}

	/**
	 * @param node
	 * @return Complexity of the input node as the number of operators included in its metric expression
	 * @author sherif
	 */
	private int computeExpressionComplexity(Tree<RefinementNode> node) {
		String e = node.getValue().metricExpression;
		return StringUtils.countMatches(e, "OR(") + StringUtils.countMatches(e, "AND(") + StringUtils.countMatches(e, "MINUS(");
	}





}
