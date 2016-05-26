package org.aksw.limes.core.ml.algorithm.wombat;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.Tree;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;
import org.aksw.limes.core.ml.setting.LearningSetting;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version May 25, 2016
 * @deprecated use {@link WombatSimple} instead
 */
@Deprecated
public class UnsupervisedSimpleWombat extends Wombat {
    protected static final String ALGORITHM_NAME = "Unsupervised Simple Wombat";
    private static final double BETA = 2.0;
    public static long CHILDREN_PENALTY_WEIGHT = 1;
    public static long COMPLEXITY_PENALTY_WEIGHT = 1;
    public static List<String> sourceUris;
    public static List<String> targetUris;
    static Logger logger = Logger.getLogger(UnsupervisedSimpleWombat.class);
    public double penaltyWeight = 0.5d;
    public boolean STRICT = true;
    public boolean verbose = false;
    public List<ExtendedClassifier> classifiers = null;
    protected int iterationNr = 0;
    RefinementNode bestSolution = null;


    public UnsupervisedSimpleWombat(Cache sourceCache, Cache targetCache, AMapping examples, Configuration configuration) {
        super(sourceCache, targetCache, configuration);
        measures = new HashSet<>(Arrays.asList("jaccard", "trigrams"));
        sourceUris = sourceCache.getAllUris();
        targetUris = targetCache.getAllUris();
    }

    ;

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.lgg.LGG#getMapping()
     */
    public AMapping getMapping() {
        if (bestSolution == null) {
            bestSolution = getBestSolution();
        }
        return bestSolution.getMapping();
    }

    public String getMetricExpression() {
        if (bestSolution == null) {
            bestSolution = getBestSolution();
        }
        return bestSolution.metricExpression;
    }

    /**
     * @return RefinementNode containing the best over all solution
     * @author sherif
     */
    public RefinementNode getBestSolution() {
        classifiers = getAllInitialClassifiers();
        createRefinementTreeRoot();
        Tree<RefinementNode> mostPromisingNode = getMostPromisingNode(refinementTreeRoot, penaltyWeight);
        logger.info("Most promising node: " + mostPromisingNode.getValue());
        iterationNr++;
        while ((mostPromisingNode.getValue().getFMeasure()) < maxFitnessThreshold
                && refinementTreeRoot.size() <= maxRefineTreeSize
                && iterationNr <= maxIterationNumber) {
            iterationNr++;
            mostPromisingNode = expandNode(mostPromisingNode);
            mostPromisingNode = getMostPromisingNode(refinementTreeRoot, penaltyWeight);
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
     * initiate the refinement tree as a root node  with set of
     * children nodes containing all initial classifiers
     *
     * @return
     * @author sherif
     */
    protected void createRefinementTreeRoot() {
        RefinementNode initialNode = new RefinementNode(-Double.MAX_VALUE, MappingFactory.createDefaultMapping(), "");
        refinementTreeRoot = new Tree<RefinementNode>(null, initialNode, null);
        for (ExtendedClassifier c : classifiers) {
            RefinementNode n = createNode(c.getMetricExpression(), c.getMapping(), c.getfMeasure());
            refinementTreeRoot.addChild(new Tree<RefinementNode>(refinementTreeRoot, n, null));
        }
        if (verbose) {
            refinementTreeRoot.print();
        }
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
        AMapping mapping = MappingFactory.createDefaultMapping();
        for (ExtendedClassifier c : classifiers) {
            for (Operator op : Operator.values()) {
                if (node.getValue().getMetricExpression() != c.getMetricExpression()) { // do not create the same metricExpression again
                    if (op.equals(Operator.AND)) {
                        mapping = MappingOperations.intersection(node.getValue().getMapping(), c.getMapping());
                    } else if (op.equals(Operator.OR)) {
                        mapping = MappingOperations.union(node.getValue().getMapping(), c.getMapping());
                    } else if (op.equals(Operator.DIFF)) {
                        mapping = MappingOperations.difference(node.getValue().getMapping(), c.getMapping());
                    }
                    String metricExpr = op + "(" + node.getValue().getMetricExpression() + "," + c.getMetricExpression() + ")|0";
                    RefinementNode child = createNode(metricExpr, mapping);
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
     * Get the most promising node as the node with the best F-score
     *
     * @param r
     *         The whole refinement tree
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
                newFitness = promesyChild.getValue().getFMeasure() - penaltyWeight * computePenality(promesyChild);
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
     * @return
     * @author sherif
     */
    private double computePenality(Tree<RefinementNode> promesyChild) {
        long childrenCount = promesyChild.size() - 1;
        double childrenPenalty = (CHILDREN_PENALTY_WEIGHT * childrenCount) / refinementTreeRoot.size();
        long level = promesyChild.level();
        double complextyPenalty = (COMPLEXITY_PENALTY_WEIGHT * level) / refinementTreeRoot.depth();
        return childrenPenalty + complextyPenalty;
    }

    private RefinementNode createNode(String metricExpr, AMapping mapping) {
        double fscore = new PseudoFMeasure().getPseudoFMeasure(mapping, new GoldStandard(null, sourceUris, targetUris), BETA);
        return createNode(metricExpr, mapping, fscore);
    }


//	private RefinementNode createNode(String metricExpr) {
//		Mapping mapping = getMapingOfMetricExpression(metricExpr);
//		return createNode(metricExpr, mapping);
//	}

    private RefinementNode createNode(String metricExpr, AMapping mapping, double fscore) {
        if (RefinementNode.isSaveMapping()) {
            return new RefinementNode(fscore, mapping, metricExpr);
        }
        return new RefinementNode(fscore, null, metricExpr);
    }

    @Override
    public String getName() {
        return ALGORITHM_NAME;
    }

    @Override
    public MLModel learn(AMapping trainingData) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AMapping computePredictions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void init(LearningSetting parameters, AMapping trainingData) throws Exception {
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


    public enum Operator {
        AND, OR, DIFF
    }
}
