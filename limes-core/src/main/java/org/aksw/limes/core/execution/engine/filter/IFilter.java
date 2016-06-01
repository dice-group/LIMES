package org.aksw.limes.core.execution.engine.filter;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;

/**
 * IFilter implements the filter interface.
 * 
 * @author Axel-C. Ngonga Ngomo <ngonga@informatik.uni-leipzig.de>
 * @author Kleanthi Georgala <georgala@informatik.uni-leipzig.de>
 * @version 1.0
 * 
 * 
 */

public interface IFilter {
    /**
     * Naive filter function for mapping using a threshold as filtering
     * criterion.
     *
     * @param map,
     *            Map bearing the results of Link Specification
     * @param threshold,
     *            Value of threshold
     * @return AMapping, Filtered mapping that satisfies sim >= threshold
     */
    public AMapping filter(AMapping map, double threshold);

    /**
     * Filter function for mapping using a condition and a threshold as
     * filtering criterion.
     *
     * @param map,
     *            Map bearing the results of Link Specification
     * @param condition,
     *            the condition for filtering
     * @param threshold,
     *            Value of threshold
     * @param source,
     *            Source Knowledge base
     * @param target,
     *            Target Knowledge base
     * @param sourceVar,
     *            Source property
     * @param targetVar,
     *            Target property
     * @return AMapping, Filtered mapping that satisfies both the condition and
     *         the threshold
     */
    public AMapping filter(AMapping map, String condition, double threshold, Cache source, Cache target,
            String sourceVar, String targetVar);

    /**
     * Filter function for mapping using a condition and two thresholds as
     * filtering criterion.
     *
     * @param map,
     *            Map bearing the results of Link Specification
     * @param condition,
     *            the condition for filtering
     * @param threshold,
     *            Value of the first threshold
     * @param mainThreshold,
     *            Value of second threshold
     * @param source,
     *            Source Knowledge base
     * @param target,
     *            Target Knowledge base
     * @param sourceVar,
     *            Source property
     * @param targetVar,
     *            Target property
     * @return AMapping, Filtered mapping that satisfies both the condition and
     *         the thresholds
     */
    public AMapping filter(AMapping map, String condition, double threshold, double mainThreshold, Cache source,
            Cache target, String sourceVar, String targetVar);

    /**
     * Reverse filter function for mapping using a condition and two thresholds
     * as filtering criterion.
     *
     * @param map,
     *            Map bearing the results of Link Specification
     * @param condition,
     *            the condition for filtering
     * @param threshold,
     *            Value of the first threshold
     * @param mainThreshold,
     *            Value of second threshold
     * @param source,
     *            Source Knowledge base
     * @param target,
     *            Target Knowledge base
     * @param sourceVar,
     *            Source property
     * @param targetVar,
     *            Target property
     * @return Mapping, Filtered mapping that satisfies both the condition and
     *         the thresholds
     */
    public AMapping reversefilter(AMapping map, String condition, double threshold, double mainThreshold, Cache source,
            Cache target, String sourceVar, String targetVar);

    /**
     * Filter for linear combinations when operation is set to "add", given the
     * expression a*sim1 + b*sim2 >= t or multiplication given the expression
     * (a*sim1)*(b*sim2) >= t, which is not likely to be used.
     *
     * @param map1,
     *            Map bearing the results of sim1 >= (t-b)/a for add, sim1 >=
     *            t/(a*b) for mult
     * @param map2,
     *            Map bearing the results of sim2 >= (t-a)/b for add, sim2 >=
     *            t/(a*b) for mult
     * @param coef1,
     *            Value of first coefficient
     * @param coef2,
     *            Value of second coefficient
     * @param threshold,
     *            Value of t
     * @return Mapping, Filtered mapping that satisfies a*sim1 + b*sim2 >= t for
     *         add, (a*sim1)*(b*sim2) >= t for mult
     */
    public AMapping filter(AMapping map1, AMapping map2, double coef1, double coef2, double threshold,
            String operation);
}
