package org.aksw.limes.core.ml.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.datastrutures.Tree;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser.DataSets;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.wombat.AWombat;
import org.aksw.limes.core.ml.algorithm.wombat.LinkEntropy;
import org.aksw.limes.core.ml.algorithm.wombat.RefinementNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple implementation of the Wombat algorithm
 * Fast implementation, that is not complete
 * 
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Apr 3, 2017
 */
public class FuzzyWombatSimple extends AWombat {
    private static final double TRAINING_RATIO = 0.3;
    protected static Logger logger = LoggerFactory.getLogger(FuzzyWombatSimple.class);
    protected static final String ALGORITHM_NAME = "Fuzzy Wombat Simple";
    protected int activeLearningRate = 3;
    protected RefinementNode bestSolutionNode = null;
    protected List<ExtendedClassifier> classifiers = null;
    protected int iterationNr = 0;

    AMapping informativeExamples = MappingFactory.createDefaultMapping();



    /**
     * WombatSimple constructor.
     */
    public FuzzyWombatSimple() {
        super();
        isFuzzy = true;
    }

    @Override
    public String getName() {
        return ALGORITHM_NAME;
    }

    @Override
    public void init(List<LearningParameter> lp, ACache sourceCache, ACache targetCache) {
        isFuzzy = true;
        super.init(lp, sourceCache, targetCache);
        sourceUris = sourceCache.getAllUris();
        targetUris = targetCache.getAllUris();
    }

    @Override
    public MLResults learn(AMapping trainingData) {
        this.trainingData = trainingData;
        return learn();
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
     * @return wrap with results, null if no result found 
     */
    private MLResults learn() {
        if (bestSolutionNode == null) { // not to do learning twice
            bestSolutionNode = findBestSolution();
        }
        String bestMetricExpr = bestSolutionNode.getMetricExpression();
        if(!bestMetricExpr.equals("")){
            double threshold = Double.parseDouble(bestMetricExpr.substring(bestMetricExpr.lastIndexOf("|") + 1, bestMetricExpr.length()));
            AMapping bestMapping = bestSolutionNode.getMapping();
            LinkSpecification bestLS = new LinkSpecification(bestMetricExpr, threshold);
            double bestFMeasure = bestSolutionNode.getFMeasure();
            MLResults result = new MLResults(bestLS, bestMapping, bestFMeasure, null);
            return result;
        }
        // case no mapping found
        return null;
    }

    @Override
    protected MLResults learn(PseudoFMeasure pfm) {
        if(pfm != null){
            this.pseudoFMeasure = pfm;
        }else{ // use default PFM
            this.pseudoFMeasure = new PseudoFMeasure();
        }
        this.isUnsupervised = true;
        return learn();
    }

    @Override
    public AMapping predict(ACache source, ACache target, MLResults mlModel) {
        LinkSpecification ls = mlModel.getLinkSpecification();
        return getPredictions(ls, source, target);
    }

    @Override
    protected boolean supports(MLImplementationType mlType) {
        return mlType == MLImplementationType.SUPERVISED_BATCH || 
                mlType == MLImplementationType.UNSUPERVISED    || 
                mlType == MLImplementationType.SUPERVISED_ACTIVE;
    }

    @Override
    protected AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
        List<RefinementNode> bestNodes = getBestKNodes(refinementTreeRoot, activeLearningRate);
        AMapping intersectionMapping = MappingFactory.createDefaultMapping();
        AMapping unionMapping = MappingFactory.createDefaultMapping();

        for(RefinementNode sn : bestNodes){
            intersectionMapping = MappingOperations.intersection(intersectionMapping, sn.getMapping());
            unionMapping = MappingOperations.union(unionMapping, sn.getMapping());
        }
        AMapping posEntropyMapping = MappingOperations.difference(unionMapping, intersectionMapping);

        TreeSet<LinkEntropy> linkEntropy = new TreeSet<>();
        int entropyPos = 0, entropyNeg = 0;
        for(String s : posEntropyMapping.getMap().keySet()){
            for(String t : posEntropyMapping.getMap().get(s).keySet()){
                // compute Entropy(s,t)
                for(RefinementNode sn : bestNodes){
                    if(sn.getMapping().contains(s, t)){
                        entropyPos++;
                    }else{
                        entropyNeg++;
                    }
                }
                int entropy = entropyPos * entropyNeg;
                linkEntropy.add(new LinkEntropy(s, t, entropy));
            }
        }
        // get highestEntropyLinks
        List<LinkEntropy> highestEntropyLinks = new ArrayList<>();
        int i = 0;
        Iterator<LinkEntropy> itr = linkEntropy.descendingIterator();
        while(itr.hasNext() && i < size) {
            highestEntropyLinks.add(itr.next());
            i++;
        }
        AMapping result = MappingFactory.createDefaultMapping();
        for(LinkEntropy l: highestEntropyLinks){
            result.add(l.getSourceUri(), l.getTargetUri(), l.getEntropy());
        }
        return result;
    }

    @Override
    protected MLResults activeLearn(){
        return learn(new PseudoFMeasure());
    }

    @Override
    protected MLResults activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
        this.isUnsupervised = false;
        trainingData = MappingOperations.union(trainingData, oracleMapping);
        updateScores(refinementTreeRoot);
        bestSolutionNode = findBestSolution();
        String bestMetricExpr = bestSolutionNode.getMetricExpression();
        double threshold = Double.parseDouble(bestMetricExpr.substring(bestMetricExpr.lastIndexOf("|") + 1, bestMetricExpr.length()));
        AMapping bestMapping = bestSolutionNode.getMapping();
        LinkSpecification bestLS = new LinkSpecification(bestMetricExpr, threshold);
        double bestFMeasure = bestSolutionNode.getFMeasure();
        return new MLResults(bestLS, bestMapping, bestFMeasure, null);
    }

    /**
     * Create new RefinementNode using either real or pseudo-F-Measure
     *
     * @param mapping of the node 
     * @param metricExpr learning specifications
     * @return new RefinementNode
     */
    protected RefinementNode createNode(AMapping mapping, String metricExpr) {
        double f = fMeasure(mapping);
        return new RefinementNode(mapping, metricExpr, f);
    }



    /**
     * update precision, recall and F-Measure of the refinement tree r
     * based on either training data or PFM
     *  
     * @param r refinement tree
     */
    protected void updateScores(Tree<RefinementNode> r) {
        if (r.getchildren() == null || r.getchildren().size() == 0) {
            r.getValue().setfMeasure(fMeasure(r.getValue().getMapping()));
            r.getValue().setPrecision(precision(r.getValue().getMapping()));
            r.getValue().setRecall(recall(r.getValue().getMapping()));
            return;
        }
        for (Tree<RefinementNode> child : r.getchildren()) {
            if (child.getValue().getFMeasure() >= 0) {
                r.getValue().setfMeasure(fMeasure(r.getValue().getMapping()));
                r.getValue().setPrecision(precision(r.getValue().getMapping()));
                r.getValue().setRecall(recall(r.getValue().getMapping()));
                updateScores(child);
            }
        }
    }


    /**
     * @return RefinementNode containing the best over all solution
     */
    public RefinementNode findBestSolution() {
        classifiers = findInitialClassifiers();
        createRefinementTreeRoot();
        Tree<RefinementNode> mostPromisingNode = getMostPromisingNode(refinementTreeRoot, getOverAllPenaltyWeight() );
        logger.debug("Most promising node: " + mostPromisingNode.getValue());
        iterationNr++;
        while ((mostPromisingNode.getValue().getFMeasure()) < getMaxFitnessThreshold()
                && refinementTreeRoot.size() <= getMaxRefinmentTreeSize()
                && iterationNr <= getMaxIterationNumber()) {
            iterationNr++;
            mostPromisingNode = expandNode(mostPromisingNode);
            mostPromisingNode = getMostPromisingNode(refinementTreeRoot, getOverAllPenaltyWeight());
            if (mostPromisingNode.getValue().getFMeasure() == -Double.MAX_VALUE) {
                break; // no better solution can be found
            }
            logger.debug("Most promising node: " + mostPromisingNode.getValue());
        }
        RefinementNode bestSolution = getMostPromisingNode(refinementTreeRoot, 0).getValue();
        logger.debug("Overall Best Solution: " + bestSolution);
        return bestSolution;
    }







    /**
     * Get the most promising node as the node with the best F-score
     *
     * @param r  The whole refinement tree
     * @param penaltyWeight from 0 to 1
     * @return most promising node from the input tree r
     */
    protected Tree<RefinementNode> getMostPromisingNode(Tree<RefinementNode> r, double penaltyWeight) {
        // trivial case
        if (r.getchildren() == null || r.getchildren().size() == 0) {
            return r;
        }
        // get mostPromesyChild of children
        Tree<RefinementNode> mostPromisingChild = new Tree<RefinementNode>(new RefinementNode());
        for (Tree<RefinementNode> child : r.getchildren()) {
            if (child.getValue().getFMeasure() >= 0) {
                Tree<RefinementNode> promisingChild = getMostPromisingNode(child, penaltyWeight);
                double newFitness;
                newFitness = promisingChild.getValue().getFMeasure() - penaltyWeight * computePenalty(promisingChild);
                if (newFitness > mostPromisingChild.getValue().getFMeasure()) {
                    mostPromisingChild = promisingChild;
                }
            }
        }
        // return the argmax{root, mostPromesyChild}
        if (penaltyWeight > 0) {
            return mostPromisingChild;
        } else if (r.getValue().getFMeasure() >= mostPromisingChild.getValue().getFMeasure()) {
            return r;
        } else {
            return mostPromisingChild;
        }
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
            resultList.add(itr.next());
            i++;
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
        if (r.getchildren() == null || r.getchildren().size() == 0) {
            result.add(r.getValue());
            return result;
        }
        for (Tree<RefinementNode> child : r.getchildren()) {
            if (child.getValue().getFMeasure() >= 0) {
                result.add(r.getValue());
                return getSortedNodes(child, penaltyWeight, result);
            }
        }
        return null;
    }

    /**
     * @return children penalty + complexity penalty
     */
    private double computePenalty(Tree<RefinementNode> promesyChild) {
        long childrenCount = promesyChild.size() - 1;
        double childrenPenalty = (getChildrenPenaltyWeight() * childrenCount) / refinementTreeRoot.size();
        long level = promesyChild.level();
        double complexityPenalty = (getComplexityPenaltyWeight() * level) / refinementTreeRoot.depth();
        return childrenPenalty + complexityPenalty;
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
        AMapping map = MappingFactory.createDefaultMapping();
        for (ExtendedClassifier c : classifiers) {
            for (LogicOperator op : LogicOperator.values()) {
                if (node.getValue().getMetricExpression() != c.getMetricExpression()) { // do not create the same metricExpression again
                    if (op.equals(LogicOperator.AND)) {
                        map = MappingOperations.intersection(node.getValue().getMapping(), c.getMapping());
                    } else if (op.equals(LogicOperator.OR)) {
                        map = MappingOperations.union(node.getValue().getMapping(), c.getMapping());
                    } else if (op.equals(LogicOperator.MINUS)) {
                        map = MappingOperations.difference(node.getValue().getMapping(), c.getMapping());
                    }
                    String metricExpr = op + "(" + node.getValue().getMetricExpression() + "," + c.getMetricExpression() + ")|0";
                    RefinementNode child = createNode(map, metricExpr);
                    node.addChild(new Tree<RefinementNode>(child));
                }
            }
        }
        if (isVerbose()) {
            refinementTreeRoot.print();
        }
        return node;
    }



    /**
     * initiate the refinement tree as a root node  with set of
     * children nodes containing all initial classifiers
     *
     */
    protected void createRefinementTreeRoot() {
        RefinementNode initialNode = new RefinementNode(MappingFactory.createDefaultMapping(), "", -Double.MAX_VALUE);
        refinementTreeRoot = new Tree<RefinementNode>(null, initialNode, null);
        for (ExtendedClassifier c : classifiers) {
            RefinementNode n = new RefinementNode(c.getMapping(), c.getMetricExpression(), c.getfMeasure());
            refinementTreeRoot.addChild(new Tree<RefinementNode>(refinementTreeRoot, n, null));
        }
        if (isVerbose()) {
            refinementTreeRoot.print();
        }
    }


    public AMapping FindInformativeExamples(){
        if(informativeExamples.size() > 0){
            return informativeExamples;
        }else{
            AMapping exampleMap = MappingFactory.createDefaultMapping();
            double sumLeafWeight = 0d;
            for(Tree<RefinementNode> leaf : refinementTreeRoot.getLeaves()){
                double leafWeight = leaf.getValue().getFMeasure();
                sumLeafWeight += leafWeight;  
                HashMap<String, HashMap<String, Double>> leafMap = leaf.getValue().getMapping().getMap();
                for(String s : leafMap.keySet()){
                    for(String t : leafMap.get(s).keySet()){
                        if (exampleMap.contains(s, t)) {
                            exampleMap.getMap().get(s).put(t, exampleMap.getConfidence(s, t) + leafWeight);
                        }else{
                            exampleMap.add(s, t,  leafWeight);
                        }
                    }
                }
            }
            AMapping informativeExamples = MappingFactory.createDefaultMapping();
            for(String s : exampleMap.getMap().keySet()){
                for(String t : exampleMap.getMap().get(s).keySet()){
                    informativeExamples.add(s, t, exampleMap.getMap().get(s).get(t)/sumLeafWeight);
                }
            }
            return informativeExamples;
        }
    }

    public AMapping findMostInformativePositiveExamples(){
        AMapping infExMap = FindInformativeExamples();
        AMapping result = MappingFactory.createDefaultMapping();
        for(String s : infExMap.getMap().keySet()){
            for(String t : infExMap.getMap().get(s).keySet()){
                double c = infExMap.getConfidence(s, t);
                if(c > 0.5d){
                    result.add(s, t, c);
                }

            }
        }
        return result;
    }

    public AMapping findMostInformativeNegativeExamples(){
        AMapping infExMap = FindInformativeExamples();
        AMapping result = MappingFactory.createDefaultMapping();
        for(String s : infExMap.getMap().keySet()){
            for(String t : infExMap.getMap().get(s).keySet()){
                double c = infExMap.getConfidence(s, t);
                if(c < 0.5d){
                    result.add(s, t, c); 
                }

            }
        }
        return result;
    }

    public static void main(String []args){
        int epoch= 0;

        EvaluationData evalData = DataSetChooser.getData(DataSets.PERSON1);
        ACache sourceCache = evalData.getSourceCache();
        ACache targetCache = evalData.getTargetCache();

        AMapping referenceMapping = evalData.getReferenceMapping();
        AMapping trainingMapping = MappingFactory.createDefaultMapping();
        AMapping testMapping = MappingFactory.createDefaultMapping();
        int trainingMappingSize = (int) (referenceMapping.getSize() * TRAINING_RATIO);





    }

}




















