package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.aksw.limes.core.datastrutures.Tree;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MappingFactory.MappingType;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.wombat.AWombat;
import org.aksw.limes.core.ml.algorithm.wombat.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.wombat.RefinementNode;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;
import org.aksw.limes.core.ml.setting.LearningParameters;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class WombatSimple extends AWombat {
    protected static final String ALGORITHM_NAME = "Wombat Simple";

    protected static Logger logger = Logger.getLogger(WombatSimple.class.getName());

    protected RefinementNode bestSolutionNode = null;
    protected List<ExtendedClassifier> classifiers = null;
    protected int iterationNr = 0;



    protected WombatSimple() {
        super();
    }

    @Override
    protected String getName() {
        return ALGORITHM_NAME;
    }

    @Override
    protected void init(LearningParameters lp, Cache sourceCache, Cache targetCache) {
        super.init(lp, sourceCache, targetCache);
        sourceUris = sourceCache.getAllUris();
        targetUris = targetCache.getAllUris();
    }

    @Override
    protected MLModel learn(AMapping trainingData) {
        this.trainingData = trainingData;
        return learn();
    }

    private MLModel learn() {
        if (bestSolutionNode == null) { // not to do learning twice
            bestSolutionNode = findBestSolution();
        }
        String bestMetricExpr = bestSolutionNode.getMetricExpression();
        double threshold = Double.parseDouble(bestMetricExpr.substring(bestMetricExpr.lastIndexOf("|") + 1, bestMetricExpr.length()));
        AMapping bestMapping = bestSolutionNode.getMapping();
        LinkSpecification bestLS = new LinkSpecification(bestMetricExpr, threshold);
        double bestFMeasure = bestSolutionNode.getFMeasure();
        MLModel result = new MLModel(bestLS, bestMapping, bestFMeasure, null);
        return result;
    }

    @Override
    protected MLModel learn(PseudoFMeasure pfm) {
        if(pfm != null){
            this.pseudoFMeasure = pfm;
        }else{ // use default PFM
            this.pseudoFMeasure = new PseudoFMeasure();
        }
        this.isUnsupervised = true;
        return learn();
    }

    @Override
    protected AMapping predict(Cache source, Cache target, MLModel mlModel) {
        LinkSpecification ls = mlModel.getLinkSpecification();
        return getPredictions(ls, source, target);
    }

    @Override
    protected boolean supports(MLImplementationType mlType) {
        return mlType == MLImplementationType.SUPERVISED_BATCH || mlType == MLImplementationType.UNSUPERVISED;
    }

    @Override
    protected AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException(this.getName());
    }

    @Override
    protected MLModel activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException(this.getName());
    }

    /**
     * @return RefinementNode containing the best over all solution
     * @author sherif
     */
    public RefinementNode findBestSolution() {
        classifiers = findInitialClassifiers();
        createRefinementTreeRoot();
        Tree<RefinementNode> mostPromisingNode = getMostPromisingNode(refinementTreeRoot, overallPenaltyWeight);
        logger.info("Most promising node: " + mostPromisingNode.getValue());
        iterationNr++;
        while ((mostPromisingNode.getValue().getFMeasure()) < maxFitnessThreshold
                && refinementTreeRoot.size() <= maxRefineTreeSize
                && iterationNr <= maxIterationNumber) {
            iterationNr++;
            mostPromisingNode = expandNode(mostPromisingNode);
            mostPromisingNode = getMostPromisingNode(refinementTreeRoot, overallPenaltyWeight);
            if (mostPromisingNode.getValue().getFMeasure() == -Double.MAX_VALUE) {
                break; // no better solution can be found
            }
            logger.info("Most promising node: " + mostPromisingNode.getValue());
        }
        RefinementNode bestSolution = getMostPromisingNode(refinementTreeRoot, 0).getValue();
        logger.info("Overall Best Solution: " + bestSolution);
        return bestSolution;
    }


    /**
     * @return initial classifiers
     */
    public List<ExtendedClassifier> findInitialClassifiers() {
        logger.info("Geting all initial classifiers ...");
        List<ExtendedClassifier> initialClassifiers = new ArrayList<>();
        for (String p : sourcePropertiesCoverageMap.keySet()) {
            for (String q : targetPropertiesCoverageMap.keySet()) {
                for (String m : measures) {
                    ExtendedClassifier cp = findInitialClassifier(p, q, m);
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
     * @param sourceCache
     *         Source cache
     * @param targetCache
     *         Target cache
     * @param sourceProperty
     *         Property of source to use
     * @param targetProperty
     *         Property of target to use
     * @param measure
     *         Measure to be used
     * @param trainingMap
     * @param reference
     *         Reference mapping
     * @return Best simple classifier
     */
    private ExtendedClassifier findInitialClassifier(String sourceProperty, String targetProperty, String measure) {
        double maxOverlap = 0;
        double theta = 1.0;
        AMapping bestMapping = MappingFactory.createDefaultMapping();
        for (double threshold = 1d; threshold > minPropertyCoverage; threshold = threshold * propertyLearningRate) {
            AMapping mapping = executeAtomicMeasure(sourceProperty, targetProperty, measure, threshold);
            double overlap = recall(mapping);
            if (maxOverlap < overlap) { //only interested in largest threshold with recall 1
                bestMapping = mapping;
                theta = threshold;
                maxOverlap = overlap;
                bestMapping = mapping;
            }
        }
        ExtendedClassifier cp = new ExtendedClassifier(measure, theta);
        cp.setfMeasure(maxOverlap);
        cp.sourceProperty = sourceProperty;
        cp.targetProperty = targetProperty;
        cp.setMapping(bestMapping);
        return cp;
    }


    /**
     * Get the most promising node as the node with the best F-score
     *
     * @param r  The whole refinement tree
     * @param penaltyWeight
     * @return most promising node from the input tree r
     * @author sherif
     */
    protected Tree<RefinementNode> getMostPromisingNode(Tree<RefinementNode> r, double penaltyWeight) {
        // trivial case
        if (r.getchildren() == null || r.getchildren().size() == 0) {
            return r;
        }
        // get mostPromesyChild of children
        Tree<RefinementNode> mostPromesyChild = new Tree<RefinementNode>(new RefinementNode());
        for (Tree<RefinementNode> child : r.getchildren()) {
            if (child.getValue().getFMeasure() >= 0) {
                Tree<RefinementNode> promesyChild = getMostPromisingNode(child, penaltyWeight);
                double newFitness;
                newFitness = promesyChild.getValue().getFMeasure() - penaltyWeight * computePenalty(promesyChild);
                if (newFitness > mostPromesyChild.getValue().getFMeasure()) {
                    mostPromesyChild = promesyChild;
                }
            }
        }
        // return the argmax{root, mostPromesyChild}
        if (penaltyWeight > 0) {
            return mostPromesyChild;
        } else if (r.getValue().getFMeasure() >= mostPromesyChild.getValue().getFMeasure()) {
            return r;
        } else {
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
        return childrenPenalty + complexityPenalty;
    }

    /**
     * Expand an input refinement node by applying
     * all available operators to the input refinement
     * node's mapping with all other classifiers' mappings
     *
     * @param node
     *         Refinement node to be expanded
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
        if (verbose) {
            refinementTreeRoot.print();
        }
        return node;
    }



    /**
     * initiate the refinement tree as a root node  with set of
     * children nodes containing all initial classifiers
     *
     * @return
     * @author sherif
     */
    protected void createRefinementTreeRoot() {
        RefinementNode initialNode = new RefinementNode(-Double.MAX_VALUE, MappingFactory.createMapping(MappingType.DEFAULT), "");
        refinementTreeRoot = new Tree<RefinementNode>(null, initialNode, null);
        for (ExtendedClassifier c : classifiers) {
            RefinementNode n = new RefinementNode(c.getfMeasure(), c.getMapping(), c.getMetricExpression());
            refinementTreeRoot.addChild(new Tree<RefinementNode>(refinementTreeRoot, n, null));
        }
        if (verbose) {
            refinementTreeRoot.print();
        }
    }

}
