package org.aksw.limes.core.execution.engine.filter;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;

/**
 * Implements the filter interface.
 * 
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */

public interface IFilter {
    /**
     * Naive filter function for mapping using a threshold as filtering
     * criterion.
     *
     * @param map
     *            a map bearing the results of Link Specification
     * @param threshold
     *            the value of threshold
     * @return a filtered mapping that satisfies sim {@literal >}= threshold
     */
    public AMapping filter(AMapping map, double threshold);

    /**
     * Filter function for mapping using a condition and a threshold as
     * filtering criterion.
     *
     * @param map
     *            a map bearing the results of Link Specification
     * @param condition
     *            the condition for filtering
     * @param threshold
     *            value of threshold
     * @param source
     *            source knowledge base
     * @param target
     *            target knowledge base
     * @param sourceVar
     *            source property
     * @param targetVar
     *            target property
     * @return a filtered mapping that satisfies both the condition and the
     *         threshold
     */
    public AMapping filter(AMapping map, String condition, double threshold, Cache source, Cache target,
            String sourceVar, String targetVar);

    /**
     * Filter function for mapping using a condition and two thresholds as
     * filtering criterion.
     *
     * @param map
     *            a map bearing the results of Link Specification
     * @param condition
     *            the condition for filtering
     * @param threshold
     *            value of the first threshold
     * @param mainThreshold
     *            value of second threshold
     * @param source
     *            source knowledge base
     * @param target
     *            target knowledge base
     * @param sourceVar
     *            source property
     * @param targetVar
     *            target property
     * @return a filtered mapping that satisfies both the condition and the
     *         thresholds
     */
    public AMapping filter(AMapping map, String condition, double threshold, double mainThreshold, Cache source,
            Cache target, String sourceVar, String targetVar);

    /**
     * Reverse filter function for mapping using a condition and two thresholds
     * as filtering criterion.
     *
     * @param map
     *            a map bearing the results of Link Specification
     * @param condition
     *            the condition for filtering
     * @param threshold
     *            value of the first threshold
     * @param mainThreshold
     *            value of second threshold
     * @param source
     *            source knowledge base
     * @param target
     *            target knowledge base
     * @param sourceVar
     *            source property
     * @param targetVar
     *            target property
     * @return a filtered mapping that satisfies both the condition and the
     *         thresholds
     */
    public AMapping reversefilter(AMapping map, String condition, double threshold, double mainThreshold, Cache source,
            Cache target, String sourceVar, String targetVar);

    /**
     * Filter for linear combinations when operation is set to "add", given the
     * expression a*sim1 + b*sim2 {@literal >}= t or multiplication given the
     * expression (a*sim1)*(b*sim2) {@literal >}= t, which is not likely to be
     * used.
     *
     * @param map1
     *            a Map bearing the results of sim1 {@literal >}= (t-b)/a for
     *            add, sim1 {@literal >}= t/(a*b) for mult
     * @param map2
     *            a map bearing the results of sim2 {@literal >}= (t-a)/b for
     *            add, sim2 {@literal >}= t/(a*b) for mult
     * @param coef1
     *            value of first coefficient
     * @param coef2
     *            value of second coefficient
     * @param threshold
     *            value of threshold
     * @param operation
     *            a mathematical operation
     * @return a filtered mapping that satisfies a*sim1 + b*sim2
     *         {@literal >}= t for add, (a*sim1)*(b*sim2) {@literal >}= t for
     *         mult
     */
    public AMapping filter(AMapping map1, AMapping map2, double coef1, double coef2, double threshold,
            String operation);
}
