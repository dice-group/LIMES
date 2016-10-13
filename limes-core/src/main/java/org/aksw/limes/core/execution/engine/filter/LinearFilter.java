package org.aksw.limes.core.execution.engine.filter;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.measures.measure.MeasureProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the linear filter class.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class LinearFilter implements IFilter {

    static Logger logger = LoggerFactory.getLogger(LinearFilter.class);

    /**
     * Naive filter function for mapping using a threshold as filtering
     * criterion. The output mapping includes set of links from the initial
     * mapping that have a similarity above the input threshold.
     *
     * @param map
     *            Map bearing the results of Link Specification
     * @param threshold
     *            Value of threshold
     * @return a filtered mapping that satisfies sim {@literal >}= threshold
     */
    public AMapping filter(AMapping map, double threshold) {
        double sim = 0.0;
        if (threshold <= 0.0) {
            return map;
        } else {
            AMapping result = MappingFactory.createDefaultMapping();
            // run on all pairs and remove those whose similarity is below
            // the threshold
            for (String key : map.getMap().keySet()) {
                for (String value : map.getMap().get(key).keySet()) {
                    sim = map.getConfidence(key, value);
                    if (sim >= threshold) {
                        result.add(key, value, sim);
                    }
                }
            }
            return result;
        }
    }

    /**
     * Filter function for mapping using a condition and a threshold as
     * filtering criterion. The output mapping includes set of links from the
     * initial mapping whose similarity based on the input condition is above
     * the input threshold.
     *
     * @param map
     *            Map bearing the results of Link Specification
     * @param condition
     *            The condition for filtering
     * @param threshold
     *            Value of threshold
     * @param source
     *            Source knowledge base
     * @param target
     *            Target knowledge base
     * @param sourceVar
     *            Source property
     * @param targetVar
     *            Target property
     * @return a filtered mapping that satisfies both the condition and the
     *         threshold
     */
    public AMapping filter(AMapping map, String condition, double threshold, ACache source, ACache target,
            String sourceVar, String targetVar) {
        double sim = 0.0;
        Instance s, t;

        if (condition == null) {
            logger.error("Null condition in filter function (LinearFilter). Exiting..");
            throw new RuntimeException();
        }

        AMapping result = MappingFactory.createDefaultMapping();
        // 2. run on all pairs and remove those
        for (String key : map.getMap().keySet()) {
            s = source.getInstance(key);
            for (String value : map.getMap().get(key).keySet()) {
                t = target.getInstance(value);
                sim = MeasureProcessor.getSimilarity(s, t, condition, threshold, sourceVar, targetVar);
                if (sim >= threshold) {
                    result.add(s.getUri(), t.getUri(), sim);
                }
            }

        }
        return result;
    }

    /**
     * Filter function for mapping using a condition and two thresholds as
     * filtering criterion. Used by HELIOS/DYNAMIC planner in case of an AND
     * optimization strategy. The input mapping produced by executing one of the
     * children specifications of a specification with an AND operator, will be
     * filtered using the expression and the threshold of the other child. In
     * order for a link to be included in the output mapping, it must also
     * fulfill the input condition and have an initial similarity above the
     * mainThreshold.
     *
     * @param map
     *            map bearing the results of Link Specification
     * @param condition
     *            The condition for filtering
     * @param threshold
     *            Value of the first threshold
     * @param mainThreshold
     *            Value of second threshold
     * @param source
     *            Source knowledge base
     * @param target
     *            Target knowledge base
     * @param sourceVar
     *            Source property
     * @param targetVar
     *            Target property
     * @return a filtered mapping that satisfies both the condition and the
     *         thresholds
     */
    public AMapping filter(AMapping map, String condition, double threshold, double mainThreshold, ACache source,
            ACache target, String sourceVar, String targetVar) {
        double sim = 0.0;
        Instance s, t;
        AMapping result = MappingFactory.createDefaultMapping();
        if (condition == null) {
            logger.info("Null condition in extended filter function (LinearFilter). Exiting..");
            throw new RuntimeException();
        }

        for (String key : map.getMap().keySet()) {
            s = source.getInstance(key);
            for (String value : map.getMap().get(key).keySet()) {
                t = target.getInstance(value);
                sim = MeasureProcessor.getSimilarity(s, t, condition, threshold, sourceVar, targetVar);
                // result must pass the filter threshold first!
                if (sim >= threshold) {
                    double sim2 = map.getConfidence(key, value);
                    double minSimilarity = Math.min(sim, sim2);
                    // min similarity because of AND operator
                    // check if min sim passes the bigger threshold
                    if (minSimilarity >= mainThreshold) {
                        result.add(key, value, minSimilarity);
                    }
                }

            }
        }
        return result;

    }

    /**
     * Reverse filter function for mapping using a condition and two thresholds
     * as filtering criterion. Used by DYNAMIC planner in case of an MINUS
     * optimization strategy. The input mapping produced by executing the left
     * child of a specification with a MINUS operator, will be filtered by the
     * expression and the threshold of the right child. In order for a link to
     * be included in the output mapping, it must not fulfill the input
     * condition and its initial similarity must be above the mainThreshold.
     *
     * @param map
     *            Map bearing the results of Link Specification
     * @param condition
     *            The condition for filtering
     * @param threshold
     *            Value of the first threshold
     * @param mainThreshold
     *            Value of second threshold
     * @param source
     *            Source knowledge base
     * @param target
     *            Target knowledge base
     * @param sourceVar
     *            Source property
     * @param targetVar
     *            Target property
     * @return a filtered mapping that satisfies both the condition and the
     *         thresholds
     */
    public AMapping reversefilter(AMapping map, String condition, double threshold, double mainThreshold, ACache source,
            ACache target, String sourceVar, String targetVar) {

        double sim = 0.0;
        Instance s, t;
        AMapping result = MappingFactory.createDefaultMapping();
        if (condition == null) {
            System.err.println("Null condition in extended reverse filter function (LinearFilter). Exiting..");
            throw new RuntimeException();
        }

        // 2. run on all pairs and remove those
        for (String key : map.getMap().keySet()) {
            s = source.getInstance(key);
            for (String value : map.getMap().get(key).keySet()) {
                t = target.getInstance(value);
                sim = MeasureProcessor.getSimilarity(s, t, condition, threshold, sourceVar, targetVar);

                // similarity of s and t must be 0 to be accepted
                if (sim == 0) {
                    double sim2 = map.getConfidence(key, value);
                    if (sim2 >= mainThreshold) {
                        result.add(key, value, sim2);
                    }
                }
            }
        }
        return result;

    }

    /**
     * Filter for linear combinations when operation is set to "add", given the
     * expression a*sim1 + b*sim2 {@literal >}= t or multiplication given the
     * expression (a*sim1)*(b*sim2) {@literal >}= t, which is not likely to be
     * used. Implements a filter for the special case of linear combinations and
     * multiplications. The straight forward way would be to compute
     * filter(intersection(m1, m2), linear_combination_condition) leading to
     * re-computations. This implementation avoid that by reusing the
     * similarities that have already been computed.
     *
     * @param map1
     *            Map bearing the results of sim1 {@literal >}= (t-b)/a for add,
     *            sim1 {@literal >}= t/(a*b) for mult
     * @param map2
     *            Map bearing the results of sim2 {@literal >}= (t-a)/b for add,
     *            sim2 {@literal >}= t/(a*b) for mult
     * @param coef1
     *            Value of first coefficient
     * @param coef2
     *            Value of second coefficient
     * @param threshold
     *            Value of threshold
     * @param operation
     *            Mathematical operation
     * @return a filtered mapping that satisfies a*sim1 + b*sim2 {@literal >}= t
     *         for add, (a*sim1)*(b*sim2) {@literal >}= t for mult
     */
    public AMapping filter(AMapping map1, AMapping map2, double coef1, double coef2, double threshold,
            String operation) {
        AMapping m = MappingOperations.intersection(map1, map2);
        AMapping result = MappingFactory.createDefaultMapping();
        double sim;
        // we can be sure that each key in m is also in m1 and m2 as we used
        // intersection
        if (operation.equalsIgnoreCase("add")) {
            for (String key : m.getMap().keySet()) {
                for (String value : m.getMap().get(key).keySet()) {
                    sim = coef1 * map1.getConfidence(key, value) + coef2 * map2.getConfidence(key, value);
                    if (sim >= threshold) {
                        result.add(key, value, sim);
                    }
                }
            }
        } else {
            for (String key : m.getMap().keySet()) {
                for (String value : m.getMap().get(key).keySet()) {
                    sim = coef1 * coef2 * map1.getConfidence(key, value) * map2.getConfidence(key, value);
                    if (sim >= threshold) {
                        result.add(key, value, sim);
                    }
                }
            }
        }
        return result;
    }

}
