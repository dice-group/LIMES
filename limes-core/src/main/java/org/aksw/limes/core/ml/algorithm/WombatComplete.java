package org.aksw.limes.core.ml.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.datastrutures.Tree;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.classifier.ExtendedClassifier;
import org.aksw.limes.core.ml.algorithm.wombat.AWombat;
import org.aksw.limes.core.ml.algorithm.wombat.ExtendedRefinementNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The complete Wombat algorithm (slow implementation)
 *
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 15, 2016
 */
public class WombatComplete extends AWombat {
    protected static final String ALGORITHM_NAME = "Wombat Complete";

    protected static Logger logger = LoggerFactory.getLogger(WombatComplete.class);

    protected static boolean usePruning = false;

    protected ExtendedRefinementNode bestSolutionNode = null;
    protected List<ExtendedClassifier> classifiers = null;
    protected int iterationNr = 0;
    protected Map<String, AMapping> diffs;

    // for evaluation
    protected int pruneNodeCount = 0;
    protected long pruningTime = 0;

    private double maxRecall = 0;

    private Tree<ExtendedRefinementNode> refinementTreeRoot = null;


    /**
     * WombatComplete constructor.
     */
    protected WombatComplete() {
        super();
    }

    @Override
    protected String getName() {
        return ALGORITHM_NAME;
    }

    @Override
    protected void init(List<LearningParameter> lp, ACache sourceCache, ACache targetCache) {
        super.init(lp, sourceCache, targetCache);
        sourceUris = sourceCache.getAllUris();
        targetUris = targetCache.getAllUris();
    }

    @Override
    protected MLResults learn(AMapping trainingData) {
        this.trainingData = trainingData;
        return learn();
    }

    /**
     * @return wrap with results
     */
    private MLResults learn() {
        if (bestSolutionNode == null) { // not to do learning twice
            bestSolutionNode = findBestSolution();
        }
        String bestMetricExpr = bestSolutionNode.getMetricExpression();
        double threshold = Double.parseDouble(bestMetricExpr.substring(bestMetricExpr.lastIndexOf("|") + 1, bestMetricExpr.length()));
        AMapping bestMapping = bestSolutionNode.getMapping();
        LinkSpecification bestLS = new LinkSpecification(bestMetricExpr, threshold);
        double bestFMeasure = bestSolutionNode.getFMeasure();
        return new MLResults(bestLS, bestMapping, bestFMeasure, null);
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
    protected boolean supports(MLImplementationType mlType) {
        return mlType == MLImplementationType.SUPERVISED_BATCH || mlType == MLImplementationType.UNSUPERVISED;
    }

    @Override
    protected AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException(this.getName());
    }

    @Override
    protected MLResults activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException(this.getName());
    }



    /**
     * @return ExtendedRefinementNode containing the best over all solution
     * @author sherif
     */
    public ExtendedRefinementNode findBestSolution() {
        List<ExtendedClassifier> classifiers = findInitialClassifiers();
        computeMaxRecall(classifiers);
        diffs = computeClassifiersDiffPermutations(classifiers);
        createRefinementTreeRoot();
        Tree<ExtendedRefinementNode> mostPromisingNode = findMostPromisingNode(refinementTreeRoot, false);
        long time = System.currentTimeMillis();
        pruneTree(refinementTreeRoot, mostPromisingNode.getValue().getFMeasure());
        pruningTime += System.currentTimeMillis() - time;
        logger.debug("Most promising node: " + mostPromisingNode.getValue());
        iterationNr++;
        while ((mostPromisingNode.getValue().getFMeasure()) < getMaxFitnessThreshold()
                && (refinementTreeRoot.size() - pruneNodeCount) <= getMaxRefinmentTreeSize()
                && iterationNr <= getMaxIterationNumber()) {
            logger.debug("Running iteration number " + iterationNr);
            iterationNr++;
            mostPromisingNode = expandNode(mostPromisingNode);
            mostPromisingNode = findMostPromisingNode(refinementTreeRoot, false);
            time = System.currentTimeMillis();
            pruneTree(refinementTreeRoot, mostPromisingNode.getValue().getFMeasure());
            pruningTime += System.currentTimeMillis() - time;
            if (mostPromisingNode.getValue().getFMeasure() == -Double.MAX_VALUE) {
                break; // no better solution can be found
            }
            logger.debug("Most promising node: " + mostPromisingNode.getValue());
        }
        ExtendedRefinementNode bestSolution = findMostPromisingNode(refinementTreeRoot, true).getValue();
        logger.debug("Overall Best Solution: " + bestSolution);
        if (!saveMapping()) {
            bestSolution.setMap(getMappingOfMetricExpression(bestSolution.getMetricExpression(), refinementTreeRoot));
        }
        return bestSolution;
    }

    /**
     * @param r tree of refinement nodes
     * @param f f-measure
     * @author sherif
     */
    private void pruneTree(Tree<ExtendedRefinementNode> r, double f) {
        if (!usePruning)
            return;
        if (r.getchildren() != null && r.getchildren().size() > 0) {
            for (Tree<ExtendedRefinementNode> child : r.getchildren()) {
                if (child.getValue().getMaxFMeasure() < f) {
                    prune(child);
                } else {
                    pruneTree(child, f);
                }
            }
        }
    }


    /**
     * @param c
     *         initial classifiers
     * @return all permutations of x\y for each x,y in classifiers and x!=y
     * @author sherif
     */
    private Map<String, AMapping> computeClassifiersDiffPermutations(List<ExtendedClassifier> c) {
        Map<String, AMapping> diffs = new HashMap<>();
        for (int i = 0; i < c.size(); i++) {
            for (int j = 0; j < c.size(); j++) {
                if (i != j) {
                    AMapping m = MappingOperations.difference(c.get(i).getMapping(), c.get(j).getMapping());
                    String e = "MINUS(" + c.get(i).getMetricExpression() + "," + c.get(j).getMetricExpression() + ")|0.0";
                    diffs.put(e, m);
                }
            }
        }
        return diffs;
    }

    /**
     * Get the most promising node as the node with the best F-score
     *
     * @param r
     *         the refinement search tree
     * @param overall
     *         set true to get the best over all node (normally at the end of the algorithm)
     *         if set to false you got only the best leaf
     * @return most promising node from the input tree r
     * @author sherif
     */
    private Tree<ExtendedRefinementNode> findMostPromisingNode(Tree<ExtendedRefinementNode> r, boolean overall) {
        // trivial case
        if (r.getchildren() == null || r.getchildren().size() == 0) {
            return r;
        }
        // get the most promising child
        Tree<ExtendedRefinementNode> mostPromisingChild = new Tree<>(new ExtendedRefinementNode());
        for (Tree<ExtendedRefinementNode> child : r.getchildren()) {
            if (usePruning && child.getValue().getMaxFMeasure() < mostPromisingChild.getValue().getFMeasure()) {
                long time = System.currentTimeMillis();
                prune(child);
                pruningTime += System.currentTimeMillis() - time;
            }
            if (child.getValue().getFMeasure() >= 0) {
                Tree<ExtendedRefinementNode> promisingChild = findMostPromisingNode(child, overall);
                if (promisingChild.getValue().getFMeasure() > mostPromisingChild.getValue().getFMeasure()) {
                    mostPromisingChild = promisingChild;
                } else if ((promisingChild.getValue().getFMeasure() == mostPromisingChild.getValue().getFMeasure())
                        && (computeExpressionComplexity(promisingChild) < computeExpressionComplexity(mostPromisingChild))) {
                    mostPromisingChild = promisingChild;
                }
            }
        }
        if (overall) { // return the best leaf
            return mostPromisingChild;
        } else // return the best over all node
            if ((r.getValue().getFMeasure() > mostPromisingChild.getValue().getFMeasure())
                    || (r.getValue().getFMeasure() == mostPromisingChild.getValue().getFMeasure()
                    && computeExpressionComplexity(r) < computeExpressionComplexity(mostPromisingChild))) {
                return r;
            } else {
                return mostPromisingChild;
            }
    }


    /**
     * @param node tree of refinement nodes
     * @return Complexity of the input node as the number of operators included in its metric expression
     * @author sherif
     */
    private int computeExpressionComplexity(Tree<ExtendedRefinementNode> node) {
        String e = node.getValue().getMetricExpression();
        return StringUtils.countMatches(e, "OR(") + StringUtils.countMatches(e, "AND(") + StringUtils.countMatches(e, "MINUS(");
    }


    /**
     * @param t tree of refinement nodes
     * @author sherif
     */
    private void prune(Tree<ExtendedRefinementNode> t) {
        pruneNodeCount++;
        //      t.remove();
        t.getValue().setMetricExpression("Pruned");
        t.getValue().setfMeasure(-Double.MAX_VALUE);
        t.getValue().setMaxFMeasure(-Double.MAX_VALUE);
        t.getValue().setMap(null);
        t.getchildren().removeIf(x->true);
    }


    /**
     * @param node
     *         Refinement node to be expanded
     * @return The input tree node after expansion
     * @author sherif
     */
    private Tree<ExtendedRefinementNode> expandNode(Tree<ExtendedRefinementNode> node) {
        // Add children
        List<ExtendedRefinementNode> childrenNodes = refine(node);
        for (ExtendedRefinementNode n : childrenNodes) {
            if (!inRefinementTree(n.getMetricExpression())) {
                node.addChild(new Tree<ExtendedRefinementNode>(n));
            }
        }
        // Add sibling (if any)
        if (node.level() == 1) {
            List<ExtendedRefinementNode> siblingNodes = createConjunctionsWithDiffNodes(node);
            for (ExtendedRefinementNode n : siblingNodes) {
                if (!inRefinementTree(n.getMetricExpression())) {
                    node.getParent().addChild(new Tree<ExtendedRefinementNode>(n));
                }
            }
        }
        if (isVerbose()) {
            System.out.println("Tree size:" + refinementTreeRoot.size());
            refinementTreeRoot.print();
        }
        return node;
    }


    /**
     * Apply refinement operator
     *
     * @param node
     * @return list of all children
     * @author sherif
     */
    private List<ExtendedRefinementNode> refine(final Tree<ExtendedRefinementNode> node) {
        List<ExtendedRefinementNode> result = new ArrayList<>();
        String nodeMetricExpr = node.getValue().getMetricExpression();
        // is it the root of the tree?
        if (node.getParent() == null) {
            for (String diffExpr : diffs.keySet()) {
                AMapping diffMapping = diffs.get(diffExpr);
                result.add(createNode(diffMapping, diffExpr));
            }
            return result;
        } else if (isAtomic(nodeMetricExpr)) {
            return createDisjunctionsWithDiffNodes(node);
        } else if (isDifference(nodeMetricExpr)) {
            String firstMetricExpr = getSubMetricExpressions(nodeMetricExpr).get(0);
            AMapping firstMetricExprMapping = getMappingOfMetricExpression(firstMetricExpr, refinementTreeRoot);
            result.add(createNode(firstMetricExprMapping, firstMetricExpr));
            result.addAll(createDisjunctionsWithDiffNodes(node));
            return result;
        } else if (isConjunction(nodeMetricExpr)) {
            return applyConOrDisjunction(node);
        } else if (isDisjunction(nodeMetricExpr)) {
            return applyConOrDisjunction(node, true);
        } else {
            logger.error("Wrong metric expression: " + nodeMetricExpr);
            throw new RuntimeException();
        }
    }

    private List<ExtendedRefinementNode> applyConOrDisjunction (Tree<ExtendedRefinementNode> node) {
        return applyConOrDisjunction(node, false);
    }

    private List<ExtendedRefinementNode> applyConOrDisjunction (Tree<ExtendedRefinementNode> node, boolean useDisjunction) {
        List<ExtendedRefinementNode> result = new ArrayList<>();
        AMapping childMap;
        String childMetricExpr = "";
        String nodeMetricExpr = node.getValue().getMetricExpression();
        List<String> subMetricExpr = getSubMetricExpressions(nodeMetricExpr);
        result.add(createNode(subMetricExpr.get(0)));
        List<String> childSubMetricExpr = new ArrayList<>();
        String operator = useDisjunction ? "OR" : "AND";
        for (int i = 0; i < subMetricExpr.size(); i++) {
            for (int j = 0; j < subMetricExpr.size(); j++) {
                if (i == j) {
                    for (ExtendedRefinementNode n : refine(new Tree<>(createNode(subMetricExpr.get(i))))) {
                        childSubMetricExpr.add(n.getMetricExpression());
                    }
                } else {
                    childSubMetricExpr.add(subMetricExpr.get(i));
                }
            }
            childMetricExpr += operator + "(" + childSubMetricExpr.get(0) + "," + childSubMetricExpr.get(1) + ")|0.0";
            if (useDisjunction) {
                childMap = MappingOperations.intersection(getMappingOfMetricExpression(childSubMetricExpr.get(0), refinementTreeRoot), getMappingOfMetricExpression(childSubMetricExpr.get(1), refinementTreeRoot));
            } else {
                childMap = MappingOperations.union(getMappingOfMetricExpression(childSubMetricExpr.get(0), refinementTreeRoot), getMappingOfMetricExpression(childSubMetricExpr.get(1), refinementTreeRoot));
            }

            for (int k = 2; k < childSubMetricExpr.size(); k++) {
                childMetricExpr = operator + "(" + childMetricExpr + "," + childSubMetricExpr.get(k) + ")|0.0";
                childMap = MappingOperations.intersection(childMap, getMappingOfMetricExpression(childSubMetricExpr.get(k), refinementTreeRoot));
            }
            result.add(createNode(childMap, childMetricExpr));
            childMetricExpr = "";
        }
        result.addAll(createDisjunctionsWithDiffNodes(node));
        return result;
    }


    /**
     * @param node
     * @return list of nodes L \cup A_i \ A_j | A_i \in P, A_j \in P, where P is the set if initial classifiers
     */
    private List<ExtendedRefinementNode> createDisjunctionsWithDiffNodes(Tree<ExtendedRefinementNode> node) {
        List<ExtendedRefinementNode> result = new ArrayList<>();
        for (String diffExpr : diffs.keySet()) {
            AMapping diffMapping = diffs.get(diffExpr);
            String childMetricExpr = "OR(" + node.getValue().getMetricExpression() + "," + diffExpr + ")|0.0";
            AMapping nodeMaping;
            if (saveMapping()) {
                nodeMaping = node.getValue().getMapping();
            } else {
                nodeMaping = getMappingOfMetricExpression(node.getValue().getMetricExpression(), refinementTreeRoot);
            }
            AMapping childMap = MappingOperations.union(nodeMaping, diffMapping);
            result.add(createNode(childMap, childMetricExpr));
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
     * @param metricExpr
     */
    private List<String> getSubMetricExpressions(String metricExpr) {
        List<String> result = new ArrayList<>();
        double threshold = Double.parseDouble(metricExpr.substring(metricExpr.lastIndexOf("|") + 1, metricExpr.length()));
        ;
        String metric = metricExpr.substring(0, metricExpr.lastIndexOf("|"));
        Parser p = new Parser(metric, threshold);
        result.add(p.getLeftTerm() + "|" + p.getLeftCoefficient());
        result.add(p.getRightTerm() + "|" + p.getRightCoefficient());
        return result;
    }


    /**
     * @param node
     * @return list of nodes L \cup A_i \ A_j | A_i \in P, A_j \in P, where P is the set if initial classifiers
     */
    private List<ExtendedRefinementNode> createConjunctionsWithDiffNodes(Tree<ExtendedRefinementNode> node) {
        List<ExtendedRefinementNode> result = new ArrayList<>();
        for (String diffExpr : diffs.keySet()) {
            AMapping diffMapping = diffs.get(diffExpr);
            AMapping nodeMaping = MappingFactory.createDefaultMapping();
            if (saveMapping()) {
                nodeMaping = node.getValue().getMapping();
            } else {
                nodeMaping = getMappingOfMetricExpression(node.getValue().getMetricExpression(), refinementTreeRoot);
            }
            String childMetricExpr = "AND(" + node.getValue().getMetricExpression() + "," + diffExpr + ")|0.0";
            AMapping childMap = MappingOperations.intersection(nodeMaping, diffMapping);
            result.add(createNode(childMap, childMetricExpr));
        }
        return result;
    }

    /**
     * @param metricExpression
     * @return true if the input metricExpression already contained
     * in one of the search tree nodes, false otherwise
     * @author sherif
     */
    private boolean inRefinementTree(String metricExpression) {
        return inRefinementTree(metricExpression, refinementTreeRoot);
    }

    /**
     * @param metricExpression
     * @param treeRoot
     * @return true if the input metricExpression already contained
     * in one of the search tree nodes, false otherwise
     * @author sherif
     */
    private boolean inRefinementTree(String metricExpression, Tree<ExtendedRefinementNode> treeRoot) {
        if (treeRoot == null) {
            return false;
        }
        if (treeRoot.getValue().getMetricExpression().equals(metricExpression)) {
            return true;
        }
        if (treeRoot.getchildren() != null) {
            for (Tree<ExtendedRefinementNode> n : treeRoot.getchildren()) {
                if (inRefinementTree(metricExpression, n)) {
                    return true;
                }
            }
        }
        return false;
    }





    /**
     * initiate the refinement tree as a root node  with set of
     * children nodes containing all permutations of x\y
     * for each x,y in classifiers and x!=y
     *
     * @return
     * @author sherif
     */
    private void createRefinementTreeRoot() {
        ExtendedRefinementNode initialNode = new ExtendedRefinementNode();
        refinementTreeRoot = new Tree<>(null, initialNode, null);
        for (String diffExpr : diffs.keySet()) {
            AMapping diffMapping = diffs.get(diffExpr);
            ExtendedRefinementNode n = createNode(diffMapping, diffExpr);
            refinementTreeRoot.addChild(new Tree<>(refinementTreeRoot, n, null));
        }
        if (isVerbose()) {
            System.out.println("Tree size:" + refinementTreeRoot.size());
            refinementTreeRoot.print();
        }
    }

    /**
     * @param classifiers initial learned classifiers
     * @return maximum achievable recall as the recall of the mapping generated
     * from disjunctions of all initial mappings
     */
    private void computeMaxRecall(List<ExtendedClassifier> classifiers) {
        AMapping unionMaping;
        unionMaping = classifiers.get(0).getMapping();
        for (int i = 1; i < classifiers.size(); i++) {
            unionMaping = MappingOperations.union(unionMaping, classifiers.get(i).getMapping());
        }
        maxRecall = recall(unionMaping);
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
        if (!isDifference(l) && !isConjunction(l) && !isDisjunction(l))
            return true;
        return false;
    }

    @Override
    protected MLResults activeLearn() throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException(this.getName());
    }

    /**
     * @param metricExpr learning specifications
     * @return new ExtendedRefinementNode
     */
    protected ExtendedRefinementNode createNode(String metricExpr) {
        AMapping map = null;
        if(saveMapping()){
            map = getMappingOfMetricExpression(metricExpr, refinementTreeRoot);
        }
        return createNode(map, metricExpr);
    }

    /**
     * Create new ExtendedRefinementNode using either real or pseudo-F-Measure
     *
     * @param mapping of the node
     * @param metricExpr learning specifications
     * @return new ExtendedRefinementNode
     */
    protected ExtendedRefinementNode createNode(AMapping mapping, String metricExpr) {
        if(!saveMapping()){
            mapping = null;
        }
        if (isUnsupervised) {
            return new ExtendedRefinementNode(fMeasure(mapping), mapping, metricExpr);
        }
        return new ExtendedRefinementNode(fMeasure(mapping), mapping, metricExpr, trainingData, getBeta(), maxRecall);
    }

}
