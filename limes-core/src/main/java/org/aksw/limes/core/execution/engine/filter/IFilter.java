package org.aksw.limes.core.execution.engine.filter;


import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;

/**
 * Implements the filter interface.
 *
 * @author ngonga
 * @author kleanthi
 */
public interface IFilter {
    /**
     * Naive filter for mapping using a threshold as filtering criterion
     * 
     * @param map,
     *            Map bearing the results of Link Specification
     * @param threshold,
     *            Value of t
     * @return Mapping, that satisfies sim >= threshold
     */
    public Mapping filter(Mapping map, double threshold);

    /**
     * Filter for mapping using a condition and a threshold as filtering
     * criterion
     * 
     * @param map,
     *            Map bearing the results of Link Specification
     * @param condition,
     *            the condition for filtering
     * @param threshold,
     *            Value of t
     * @param source,
     *            source Knowledge base
     * @param target,
     *            target Knowledge base
     * @param sourceVar,
     *            source property
     * @param targetVar,
     *            target property
     * @return Mapping, that satisfies condition >= threshold
     */
    public Mapping filter(Mapping map, String condition, double threshold, Cache source, Cache target, String sourceVar,
	    String targetVar);
    /**
     * Filter for mapping using a condition and two thresholds as filtering
     * criterion
     * 
     * @param map,
     *            Map bearing the results of Link Specification
     * @param condition,
     *            the condition for filtering
     * @param threshold,
     *            Value of t, first threshold
     * @param mainThreshold,
     *            Value of t2, second threshold
     * @param source,
     *            source Knowledge base
     * @param target,
     *            target Knowledge base
     * @param sourceVar,
     *            source property
     * @param targetVar,
     *            target property
     * @return Mapping, that satisfies condition >= threshold
     */
    public Mapping filter(Mapping map, String condition, double threshold, double mainThreshold, Cache source, Cache target, String sourceVar,
	    String targetVar);
    /**
     * Filter for linear combinations when operation is set to "add", given the
     * expression a*sim1 + b*sim2 >= t or multiplication given the expression
     * (a*sim1)*(b*sim2) >= t, which is not likely to be used
     * 
     * @param map1,
     *            Map bearing the results of sim1 >= (t-b)/a for add, sim1 >=
     *            t/(a*b) for mult
     * @param map2,
     *            Map bearing the results of sim2 >= (t-a)/b for add, sim2 >=
     *            t/(a*b) for mult
     * @param coef1,
     *            Value of a
     * @param coef2,
     *            Value of b
     * @param threshold,
     *            Value of t
     * @return Mapping that satisfies a*sim1 + b*sim2 >= t for add,
     *         (a*sim1)*(b*sim2) >= t for mult
     */
    public Mapping filter(Mapping map1, Mapping map2, double coef1, double coef2, double threshold, String operation);
}
