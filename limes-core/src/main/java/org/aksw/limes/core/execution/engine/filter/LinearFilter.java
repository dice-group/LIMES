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
public class LinearFilter implements IFilter {

    static Logger logger = Logger.getLogger("LIMES");

    /**
     * Filter a mapping solely with respect to a threshold.
     *
     * @param map
     *            Input mapping
     * @param threshold
     *            Similarity threshold
     * @return result, all links from map such that sim >= threshold
     */
    public Mapping filter(Mapping map, double threshold) {
	double sim = 0.0;
	if (threshold <= 0.0) {
	    return map;
	} else {
	    Mapping result = new MemoryMapping();
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
     * Filter a mapping with respect to an expression and a threshold.
     * 
     * @param map
     *            Input mapping
     * @param threshold
     *            Similarity threshold
          * @param source,
     *            Source cache
     * @param target,
     *            Target cache
     * @param sourceVar,
     *            Source variable
     * @param targetVar,
     *            Target variable
     *            
     * @return results, all links from map such that the expression and the
     *         threshold holds
     */
    public Mapping filter(Mapping map, String condition, double threshold, Cache source, Cache target, String sourceVar,
	    String targetVar) {
	double sim = 0.0;
	Instance s, t;
	
	if (condition == null) {
	    System.err.println("Null condition in filter function (LinearFilter). Exiting..");
	    System.exit(1);
	}
	if(threshold == 0.0d)
	    return map;
	Mapping result = new MemoryMapping();
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
     * 
     * Filter a mapping with respect to an expression and two thresholds. Used
     * by HELIOS/DYNAMIC planner in case of an AND optimization strategy. The
     * input mapping produced by executing one of the children specifications of
     * a specification that has AND as operator, will be filtered by using the
     * expression and the threshold of the other child. In order for a link to
     * be included in the output mapping, it must also pass the parent
     * specification threshold.
     * 
     *
     * @param map
     *            Input mapping
     * @param threshold
     *            Similarity threshold
     * @param mainThreshold
     *            Parent similarity threshold
     * @param source,
     *            Source cache
     * @param target,
     *            Target cache
     * @param sourceVar,
     *            Source variable
     * @param targetVar,
     *            Target variable
     * 
     * @return results, all links from map such that the expression, the
     *         threshold and the mainThreshold holds
     * 
     */
    public Mapping filter(Mapping map, String condition, double threshold, double mainThreshold, Cache source,
	    Cache target, String sourceVar, String targetVar) {
	double sim = 0.0;
	Instance s, t;
	Mapping result = new MemoryMapping();

	if (condition == null) {
	    System.err.println("Null condition in extended filter function (LinearFilter). Exiting..");
	    System.exit(1);
	}
	if(threshold == 0.0d && mainThreshold == 0.0d)
	   return map;
	// 2. run on all pairs and remove those
	for (String key : map.getMap().keySet()) {
	    s = source.getInstance(key);
	    for (String value : map.getMap().get(key).keySet()) {
		t = target.getInstance(value);
		sim = MeasureProcessor.getSimilarity(s, t, condition, threshold, sourceVar, targetVar);
		// result must pass the filter threshold first!
		if (sim >= threshold) {
		    double sim2 = map.getMap().get(s.getUri()).get(t.getUri());
		    double minSimilarity = Math.min(sim, sim2);
		    // min similarity because of AND operator
		    // check if min sim passes the bigger threshold
		    if (minSimilarity >= mainThreshold) {
			result.add(s.getUri(), t.getUri(), minSimilarity);
		    }
		}

	    }
	}
	return result;

    }

    /**
     * Implements a filter for the special case of linear combinations and
     * multiplications. The straight forward way would be to compute
     * filter(intersection(m1, m2), linear_combination_condition) leading to
     * re-computations. This implementation avoid that by reusing the
     * similarities that have already been computed
     * 
     * 
     * @param map1
     *            First input mapping
     * @param map2
     *            Second input mapping
     * @param coef1
     *            First co-efficient
     * @param coef2
     *            Second co-efficient
     * @param threshold
     *            Similarity threshold
     * @param threshold
     *            Operation to be applied on input mappings
     * 
     * @return results, all links from map such that the expression, the
     *         threshold and the mainThreshold holds
     */
    public Mapping filter(Mapping m1, Mapping m2, double coef1, double coef2, double threshold, String operation) {
	Mapping m = MappingOperations.intersection(m1, m2);
	Mapping result = new MemoryMapping();
	double sim;
	// we can be sure that each key in m is also in m1 and m2 as we used
	// intersection
	if (operation.equalsIgnoreCase("add")) {
	    for (String key : m.getMap().keySet()) {
		for (String value : m.getMap().get(key).keySet()) {
		    sim = coef1 * m1.getConfidence(key, value) + coef2 * m2.getConfidence(key, value);
		    if (sim >= threshold) {
			result.add(key, value, sim);
		    }
		}
	    }
	} else {
	    for (String key : m.getMap().keySet()) {
		for (String value : m.getMap().get(key).keySet()) {
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
