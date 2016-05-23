package org.aksw.limes.core.ml.algorithm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.datastrutures.Tree;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.io.mapping.MappingFactory.MappingType;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.wombat.AWombat;
import org.aksw.limes.core.ml.algorithm.wombat.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.wombat.RefinementNode;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;
import org.aksw.limes.core.ml.setting.LearningParameters;
import org.apache.log4j.Logger;

public class WombatSimple extends AWombat {
	protected static final String ALGORITHM_NAME = "Wombat Simple";

	protected static Logger logger = Logger.getLogger(WombatSimple.class.getName());
	

	
	// local fields
	protected Tree<RefinementNode> refinementTreeRoot = null;
	protected Map<String, Double> sourcePropertiesCoverageMap; //coverage map for latter computations
	protected Map<String, Double> targetPropertiesCoverageMap; //coverage map for latter computations
	protected Set<String> measures = new HashSet<>(Arrays.asList("jaccard","trigrams","cosine","ngrams"));
	
	protected Mapping reference;
	protected RefinementNode bestSolutionNode = null; 
	protected List<ExtendedClassifier> classifiers = null;
	protected int iterationNr = 0;
	private double penaltyWeight = 0.5d;;
	
	
	protected WombatSimple() {
		super();
	}



	@Override
	protected String getName() {
		return ALGORITHM_NAME;
	}

	@Override
	protected void init(LearningParameters lp, Cache source, Cache target) {
		super.init(lp, source, target);
		// TODO Auto-generated method stub
	}

	@Override
	protected MLModel learn(Mapping trainingData) {
		if(bestSolutionNode == null){ // not to do learning twice
			bestSolutionNode =  getBestSolution();
		}
		String bestMetricExpr = bestSolutionNode.metricExpression;
		double threshold = Double.parseDouble(bestMetricExpr.substring(bestMetricExpr.lastIndexOf("|")+1, bestMetricExpr.length()));
		Mapping bestMapping = bestSolutionNode.map;
		LinkSpecification bestLS = new LinkSpecification(bestMetricExpr, threshold);
		double bestFMeasure = bestSolutionNode.fMeasure;
		MLModel result= new MLModel(bestLS, bestMapping, bestFMeasure, null);
		return result;
	}

	@Override
	protected MLModel learn(PseudoFMeasure pfm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Mapping predict(Cache source, Cache target, MLModel mlModel) {
		if(bestSolutionNode == null){
			bestSolutionNode =  getBestSolution();
		}
		return bestSolutionNode.map;
	}

	@Override
	protected boolean supports(MLImplementationType mlType) {
		return mlType == MLImplementationType.SUPERVISED_BATCH || mlType == MLImplementationType.UNSUPERVISED;
	}

	@Override
	protected Mapping getNextExamples(int size) throws UnsupportedMLImplementationException {
		throw new UnsupportedMLImplementationException(this.getName());
	}

	@Override
	protected MLModel activeLearn(Mapping oracleMapping) throws UnsupportedMLImplementationException {
		throw new UnsupportedMLImplementationException(this.getName());
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
				//				&& root.size() <= MAX_TREE_SIZE
				&& iterationNr <= maxIterationNumber)
		{
			iterationNr++;
			mostPromisingNode = expandNode(mostPromisingNode);
			mostPromisingNode = getMostPromisingNode(refinementTreeRoot, penaltyWeight);
			if(mostPromisingNode.getValue().fMeasure == -Double.MAX_VALUE){
				break; // no better solution can be found
			}
			logger.info("Most promising node: " + mostPromisingNode.getValue());
		}
		RefinementNode bestSolution = getMostPromisingNode(refinementTreeRoot, 0).getValue();
		logger.info("Overall Best Solution: " + bestSolution);
		return bestSolution;
	}

	/**
	 * Get the most promising node as the node with the best F-score
	 *  
	 * @param r The whole refinement tree
	 * @param penaltyWeight 
	 * @return most promising node from the input tree r
	 * @author sherif
	 */
	protected Tree<RefinementNode> getMostPromisingNode(Tree<RefinementNode> r, double penaltyWeight){
		// trivial case
		if(r.getchildren() == null || r.getchildren().size() == 0){
			return r;
		}
		// get mostPromesyChild of children
		Tree<RefinementNode> mostPromesyChild = new Tree<RefinementNode>(new RefinementNode());
		for(Tree<RefinementNode> child : r.getchildren()){
			if(child.getValue().fMeasure >= 0){
				Tree<RefinementNode> promesyChild = getMostPromisingNode(child, penaltyWeight);
				double newFitness;
				newFitness = promesyChild.getValue().fMeasure - penaltyWeight * computePenalty(promesyChild);
				if( newFitness > mostPromesyChild.getValue().fMeasure  ){
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
	 * @return children penalty + complexity penalty
	 * @author sherif
	 */
	private double computePenalty(Tree<RefinementNode> promesyChild) {
		long childrenCount = promesyChild.size() - 1;
		double childrenPenalty = (childrenPenaltyWeit * childrenCount) / refinementTreeRoot.size();
		long level = promesyChild.level();
		double complexityPenalty = (complexityPenaltyWeit * level) / refinementTreeRoot.depth();
		return  childrenPenalty + complexityPenalty;
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
		Mapping map = new MemoryMapping();
		for(ExtendedClassifier c : classifiers ){
			for(LogicOperator op : LogicOperator.values()){
				if(node.getValue().metricExpression != c.getMetricExpression()){ // do not create the same metricExpression again 
					if(op.equals(LogicOperator.AND)){
						map = MappingOperations.intersection(node.getValue().map, c.mapping);
					}else if(op.equals(LogicOperator.OR)){
						map = MappingOperations.union(node.getValue().map, c.mapping);
					}else if(op.equals(LogicOperator.MINUS)){
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
	 * initiate the refinement tree as a root node  with set of 
	 * children nodes containing all initial classifiers
	 * @return
	 * @author sherif
	 */
	protected void createRefinementTreeRoot(){
		RefinementNode initialNode = new RefinementNode(-Double.MAX_VALUE, MappingFactory.createMapping(MappingType.DEFAULT), "");
		refinementTreeRoot = new Tree<RefinementNode>(null,initialNode, null);
		for(ExtendedClassifier c : classifiers){
			RefinementNode n = new RefinementNode(c.fMeasure, c.mapping, c.getMetricExpression());
			refinementTreeRoot.addChild(new Tree<RefinementNode>(refinementTreeRoot,n, null));
		}
		if(verbose){
			refinementTreeRoot.print();
		}
	}

}
