package org.aksw.limes.core.measures.measure;

import java.util.ArrayList;
import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.aksw.limes.core.exceptions.InvalidThresholdException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.mapper.MapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class MeasureProcessor {

    private static final String ADD = "ADD";
    private static final String XOR = "XOR";
    private static final String MAX = "MAX";
    private static final String MIN = "MIN";
    private static final String AND = "AND";
    private static final String OR = "OR";
    static Logger logger = LoggerFactory.getLogger(MeasureProcessor.class.getName());

    /**
     * Computes a list that contains all measures used in a given expression.
     *
     * @param expression,
     *            The input metric expression
     * @return List of all measures included in the input expression
     */
    public static List<String> getMeasures(String expression) {
        List<String> results = new ArrayList<String>();
        Parser p = new Parser(expression, 0);
        if (p.isAtomic()) {
            results.add(p.getOperator());
        } else {
            results.addAll(getMeasures(p.getLeftTerm()));
            results.addAll(getMeasures(p.getRightTerm()));
        }
        return results;
    }

    /*
     * When computing similarities using a metric that has PPJoinPlusPlus as
     * mapper, the results returned by the measure (measure.getSimilarity) and
     * by the mapper (mapper.getMapping) are different. PPJoinPlusPlus has a
     * bug. However, MeasureProcessor.getSimilarity is used ONLY by the Helios
     * and the Dynamic Planner, because they include filters with metric
     * expressions. Canonical planner computes filters without a metric
     * expressions, hence this function is never called. In order to make sure
     * that all results returned by all planners are comparable with equal size,
     * we create a Caches for source and target with one instance each and
     * instead of using measure.getSimilarity as before, we use the
     * corresponding mapper. Be aware that EDJoin and QGramsSimilarity do not
     * work with Caches of one instance.
     */

    /**
     * Returns similarity between two instances given a metric expression.
     *
     * @param sourceInstance,
     *            the source instance
     * @param targetInstance,
     *            the target instance
     * @param expression,
     *            the metric expression
     * @param threshold,
     *            the threshold
     * @param sourceVar,
     *            the source variable
     * @param targetVar,
     *            the target variable
     * @return the similarity of sourceInstance and targetInstance
     */
    public static double getSimilarity(Instance sourceInstance, Instance targetInstance, String expression,
            double threshold, String sourceVar, String targetVar) {

        Parser p = new Parser(expression, threshold);
        try {
            if (p.isAtomic()) {

                AMapper mapper = null;

                MeasureType type = MeasureFactory.getMeasureType(p.getOperator());
                mapper = MapperFactory.createMapper(type);
                ACache source = new HybridCache();
                ACache target = new HybridCache();
                source.addInstance(sourceInstance);
                target.addInstance(targetInstance);

                String property1 = null, property2 = null;

                String term1 = "?" + p.getLeftTerm();
                String term2 = "?" + p.getRightTerm();
                String split[];
                String var;

                String property = "";
                if (term1.contains(".")) {
                    split = term1.split("\\.");
                    var = split[0];
                    property = split[1];
                    if (split.length >= 2) {
                        for (int i = 2; i < split.length; i++) {
                            property = property + "." + split[i];
                        }
                    }
                    if (var.equals(sourceVar)) {
                        // property1 = split[1];
                        property1 = property;
                    } else {
                        // property2 = split[1];
                        property2 = property;
                    }
                } else {
                    property1 = term1;
                }

                // get second property label
                if (term2.contains(".")) {
                    split = term2.split("\\.");
                    var = split[0];
                    property = split[1];
                    if (split.length >= 2) {
                        for (int i = 2; i < split.length; i++) {
                            property = property + "." + split[i];
                        }
                    }
                    if (var.equals(sourceVar)) {
                        property1 = property;
                    } else {
                        property2 = property;
                    }
                } else {
                    property2 = term2;
                }

                // if no properties then terminate
                if (property1 == null || property2 == null) {
                    logger.error(MarkerFactory.getMarker("FATAL"), "Property values could not be read. Exiting");
                    throw new RuntimeException();
                } else {
                    double similarity = 0.0d;
                    if (threshold <= 0) {
                        throw new InvalidThresholdException(threshold);
                    }
                    AMapping m = mapper.getMapping(source, target, sourceVar, targetVar, expression, threshold);
                    for (String s : m.getMap().keySet()) {
                        for (String t : m.getMap().get(s).keySet()) {
                            similarity = m.getConfidence(s, t);

                        }
                    }
                    if (similarity >= threshold)
                        return similarity;
                    else
                        return 0.0d;
                }
            } else {
                if (p.getOperator().equalsIgnoreCase(MAX) | p.getOperator().equalsIgnoreCase(OR)
                        | p.getOperator().equalsIgnoreCase(XOR)) {
                    double parentThreshold = p.getThreshold();
                    double firstChild = getSimilarity(sourceInstance, targetInstance, p.getLeftTerm(),
                            p.getThreshold1(), sourceVar, targetVar);
                    double secondChild = getSimilarity(sourceInstance, targetInstance, p.getRightTerm(),
                            p.getThreshold2(), sourceVar, targetVar);

                    // parentThreshold is 0 and (s,t) are not part of the union
                    if (firstChild < p.getThreshold1() && secondChild < p.getThreshold2())
                        return 0;
                    else {
                        // find max value between or terms
                        double maxSimilarity = Math.max(firstChild, secondChild);
                        if (maxSimilarity >= parentThreshold)
                            return maxSimilarity;
                        else
                            return 0;
                    }
                }
                if (p.getOperator().equalsIgnoreCase(MIN) | p.getOperator().equalsIgnoreCase(AND)) {
                    double parentThreshold = p.getThreshold();
                    double firstChild = getSimilarity(sourceInstance, targetInstance, p.getLeftTerm(),
                            p.getThreshold1(), sourceVar, targetVar);
                    double secondChild = getSimilarity(sourceInstance, targetInstance, p.getRightTerm(),
                            p.getThreshold2(), sourceVar, targetVar);

                    // parentThreshold is 0 and (s,t) are not part of the
                    // intersection
                    if (firstChild < p.getThreshold1() && secondChild < p.getThreshold2())
                        return 0;
                    else {
                        // find min value between or terms
                        double minSimilarity = Math.min(firstChild, secondChild);
                        if (minSimilarity >= parentThreshold)
                            return minSimilarity;
                        else
                            return 0;
                    }
                }
                if (p.getOperator().equalsIgnoreCase(ADD)) {
                    double parentThreshold = p.getThreshold();
                    double firstChild = p.getLeftCoefficient() * getSimilarity(sourceInstance, targetInstance,
                            p.getLeftTerm(), p.getThreshold1(), sourceVar, targetVar);
                    double secondChild = p.getRightCoefficient() * getSimilarity(sourceInstance, targetInstance,
                            p.getRightTerm(), p.getThreshold2(), sourceVar, targetVar);

                    if (firstChild < p.getThreshold1() && secondChild < p.getThreshold2())
                        return 0;
                    else {
                        if (firstChild + secondChild >= parentThreshold)
                            return firstChild + secondChild;
                        else
                            return 0;
                    }

                } else {
                    double parentThreshold = p.getThreshold();
                    double firstChild = getSimilarity(sourceInstance, targetInstance, p.getLeftTerm(),
                            p.getThreshold1(), sourceVar, targetVar);
                    double secondChild = getSimilarity(sourceInstance, targetInstance, p.getRightTerm(),
                            p.getThreshold2(), sourceVar, targetVar);
                    // the second similarity must be 0 in order for the instance
                    // to
                    // have a change to be included at the final result
                    if (secondChild == 0) {
                        if (firstChild >= p.getThreshold1()) {
                            if (firstChild >= parentThreshold) {
                                return firstChild;
                            } else // similarity smaller than the parent
                                   // threshold
                                return 0;
                        } else// similarity smaller than the left child
                              // threshold
                            return 0;
                    } else // current (s,t) are included in the mapping of the
                           // right
                           // child
                        return 0;
                }
            }
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;

    }

    /**
     * Returns the approximation of the runtime for a certain expression.
     *
     * @param measureExpression,
     *            The input metric expression
     * @param mappingSize,
     *            The size of the mapping returned by the metric expression
     * @return Runtime approximation of the measure expression
     */
    public static double getCosts(String measureExpression, double mappingSize) {
        List<String> measures = getMeasures(measureExpression);
        double runtime = 0;
        for (int i = 0; i < measures.size(); i++) {
            MeasureType type = MeasureFactory.getMeasureType(measures.get(i));
            runtime = runtime + MeasureFactory.createMeasure(type).getRuntimeApproximation(mappingSize);
        }
        return runtime;
    }
}
