package org.aksw.limes.core.measures.measure;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.aksw.limes.core.exceptions.InvalidMeasureException;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.Mapper;
import org.aksw.limes.core.measures.mapper.string.PPJoinPlusPlus;

/**
 *
 * @author ngonga
 */
public class MeasureProcessor {

    static Logger logger = Logger.getLogger(MeasureProcessor.class.getName());

    /**
     * Computes a list that contains all measures used in a given expression
     * 
     * @param expression
     *            Expression
     * @return List of all measures used
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
        if (p.isAtomic()) {
            Measure measure = null;
            try {
                measure = MeasureFactory.getMeasure(p.getOperator());
            } catch (InvalidMeasureException e) {
                e.printStackTrace();
                System.err.println("Exiting..");
                System.exit(1);
            }
            Mapper mapper = null;
            try {
                mapper = MeasureFactory.getMapper(p.getOperator());
            } catch (InvalidMeasureException e) {
                e.printStackTrace();
                System.err.println("Exiting..");
                System.exit(1);
            }
            Cache source = new HybridCache();
            Cache target = new HybridCache();
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
                logger.fatal("Property values could not be read. Exiting");
                System.exit(1);
            } else {

                //if (mapper instanceof PPJoinPlusPlus) {
                    Mapping m = mapper.getMapping(source, target, sourceVar, targetVar, expression, threshold);
                    for (String s : m.getMap().keySet()) {
                        for (String t : m.getMap().get(s).keySet()) {
                            return m.getMap().get(s).get(t);
                        }
                    }
                /*} else {
                    // logger.info(mapper);
                    double similarity = measure.getSimilarity(sourceInstance, targetInstance, property1, property2);
                    if (similarity >= threshold)
                        return similarity;
                    else
                        return 0;
                }*/

            }
        } else {
            if (p.getOperator().equalsIgnoreCase("MAX") | p.getOperator().equalsIgnoreCase("OR")
                    | p.getOperator().equalsIgnoreCase("XOR")) {
                double parentThreshold = p.getThreshold();
                double firstChild = getSimilarity(sourceInstance, targetInstance, p.getLeftTerm(), p.getThreshold1(),
                        sourceVar, targetVar);
                double secondChild = getSimilarity(sourceInstance, targetInstance, p.getRightTerm(), p.getThreshold2(),
                        sourceVar, targetVar);

                double maxSimilarity = Math.max(firstChild, secondChild);
                // find max value between or terms
                if (maxSimilarity >= parentThreshold)
                    return maxSimilarity;
                else
                    return 0;
            }
            if (p.getOperator().equalsIgnoreCase("MIN") | p.getOperator().equalsIgnoreCase("AND")) {
                double parentThreshold = p.getThreshold();
                double firstChild = getSimilarity(sourceInstance, targetInstance, p.getLeftTerm(), p.getThreshold1(),
                        sourceVar, targetVar);
                double secondChild = getSimilarity(sourceInstance, targetInstance, p.getRightTerm(), p.getThreshold2(),
                        sourceVar, targetVar);

                if (firstChild < p.getThreshold1() && secondChild < p.getThreshold2())
                    return 0;
                double minSimilarity = Math.min(firstChild, secondChild);
                // find min value between or terms
                if (minSimilarity >= parentThreshold)
                    return minSimilarity;
                else
                    return 0;
            }
            if (p.getOperator().equalsIgnoreCase("ADD")) {
                double parentThreshold = p.getThreshold();
                double firstChild = p.getLeftCoefficient() * getSimilarity(sourceInstance, targetInstance,
                        p.getLeftTerm(), p.getThreshold1(), sourceVar, targetVar);
                double secondChild = p.getRightCoefficient() * getSimilarity(sourceInstance, targetInstance,
                        p.getRightTerm(), p.getThreshold2(), sourceVar, targetVar);

                if (firstChild + secondChild >= parentThreshold)
                    return firstChild + secondChild;
                else
                    return 0;

            } else {// perform MINUS as usual
                // logger.warn("Not sure what to do with operator " + p.op + ".
                // Using MAX.");
                double parentThreshold = p.getThreshold();
                double firstChild = getSimilarity(sourceInstance, targetInstance, p.getLeftTerm(), p.getThreshold1(),
                        sourceVar, targetVar);
                double secondChild = getSimilarity(sourceInstance, targetInstance, p.getRightTerm(), p.getThreshold2(),
                        sourceVar, targetVar);
                // the second similarity must be 0 in order for the instance to
                // have a change to be included at the final result
                if (secondChild < p.getThreshold2()) {
                    if (firstChild >= parentThreshold)
                        return firstChild;
                    else
                        return 0;
                } else
                    return 0;
            }
        }
        return 0;

    }

    /**
     * Returns the approximation of the runtime for a certain expression
     * 
     * @param measureExpression
     *            Expression
     * @param mappingSize
     *            Size of the mapping to process
     * @return Runtime approximation
     */
    public static double getCosts(String measureExpression, double mappingSize) {
        List<String> measures = getMeasures(measureExpression);
        double runtime = 0;
        for (int i = 0; i < measures.size(); i++)
            try {
                runtime = runtime + MeasureFactory.getMeasure(measures.get(i)).getRuntimeApproximation(mappingSize);
            } catch (InvalidMeasureException e) {
                e.printStackTrace();
                System.err.println("Exiting..");
                System.exit(1);
            }
        return runtime;
    }
}
