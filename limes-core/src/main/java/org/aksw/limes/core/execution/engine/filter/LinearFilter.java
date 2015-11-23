package org.aksw.limes.core.execution.engine.filter;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.measures.measure.MeasureProcessor;
import org.apache.log4j.Logger;

/**
 * Implements the linear filter class.
 *
 * @author ngonga
 * @author kleanthi
 */
public class LinearFilter implements Filter {

    static Logger logger = Logger.getLogger("LIMES");

    /**
     * Filter a mapping solely with respect to a threshold
     *
     * @param map
     *            Input mapping
     * @param threshold
     *            Similarity threshold
     * @return All mapping from map such that sim >= threshold
     */
    public Mapping filter(Mapping map, double threshold) {
	double sim = 0.0;
	if (threshold <= 0.0) {
	    return map;
	} else {
	    Mapping result = new MemoryMapping();
	    // run on all pairs and remove those whose similarity is below
	    // the threshold
	    for (String key : map.map.keySet()) {
		for (String value : map.map.get(key).keySet()) {
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
     * Filter a mapping with respect to an expression. If the expression is
     * null, then all the pairs with a similarity above threshold are returned
     */
    public Mapping filter(Mapping map, String condition, double threshold, Cache source, Cache target, String sourceVar,
	    String targetVar) {
	double sim = 0.0;
	Instance s, t;

	if (threshold <= 0.0) {
	    return map;
	}
	if (condition == null) {
	    return filter(map, threshold);
	} else {
	    Mapping result = new MemoryMapping();
	    // 2. run on all pairs and remove those
	    for (String key : map.map.keySet()) {
		s = source.getInstance(key);
		for (String value : map.map.get(key).keySet()) {
		    t = target.getInstance(value);
		    sim = MeasureProcessor.getSimilarity(s, t, condition, sourceVar, targetVar);
		    if (sim >= threshold) {
			result.add(s.getUri(), t.getUri(), sim);
		    }
		}
	    }
	    return result;
	}
    }

    /**
     * Implements a filter for the special case of linear combinations and
     * multiplications. The straight forward way would be to compute
     * filter(intersection(m1, m2), linear_combination_condition) leading to
     * re-computations. This implementation avoid that by reusing the
     * similarities that have already been computed
     */
    public Mapping filter(Mapping m1, Mapping m2, double coef1, double coef2, double threshold, String operation) {
	Mapping m = MappingOperations.intersection(m1, m2);
	Mapping result = new MemoryMapping();
	double sim;
	// we can be sure that each key in m is also in m1 and m2 as we used
	// intersection
	if (operation.equalsIgnoreCase("add")) {
	    for (String key : m.map.keySet()) {
		for (String value : m.map.get(key).keySet()) {
		    sim = coef1 * m1.getConfidence(key, value) + coef2 * m2.getConfidence(key, value);
		    if (sim >= threshold) {
			result.add(key, value, sim);
		    }
		}
	    }
	} else {
	    for (String key : m.map.keySet()) {
		for (String value : m.map.get(key).keySet()) {
		    sim = coef1 * coef2 * m1.getConfidence(key, value) * m2.getConfidence(key, value);
		    if (sim >= threshold) {
			result.add(key, value, sim);
		    }
		}
	    }
	}
	return result;
    }

}
