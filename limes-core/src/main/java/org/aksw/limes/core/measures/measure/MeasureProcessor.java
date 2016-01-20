package org.aksw.limes.core.measures.measure;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.Mapper;
import org.aksw.limes.core.measures.mapper.atomic.EDJoin;
import org.aksw.limes.core.measures.measure.string.QGramSimilarity;

/**
 *
 * @author ngonga
 */
public class MeasureProcessor {

    static Logger logger = Logger.getLogger("LIMES");

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
	    results.addAll(getMeasures(p.getTerm1()));
	    results.addAll(getMeasures(p.getTerm2()));
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
    public static double getSimilarity(Instance sourceInstance, Instance targetInstance, String expression,
	    double threshold, String sourceVar, String targetVar) {

	Parser p = new Parser(expression, threshold);
	if (p.isAtomic()) {
	    Measure measure = MeasureFactory.getMeasure(p.getOperator());
	    Mapper mapper = MeasureFactory.getMapper(p.getOperator());
	    Cache source = new HybridCache();
	    Cache target = new HybridCache();
	    source.addInstance(sourceInstance);
	    target.addInstance(targetInstance);

	    String property1 = null, property2 = null;

	    String term1 = "?" + p.getTerm1();
	    String term2 = "?" + p.getTerm2();
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
		if (mapper instanceof EDJoin || measure instanceof QGramSimilarity)
		    return measure.getSimilarity(sourceInstance, targetInstance, property1, property2);

		Mapping m = mapper.getMapping(source, target, sourceVar, targetVar, expression, threshold);
		for (String s : m.getMap().keySet()) {
		    for (String t : m.getMap().get(s).keySet()) {
			return m.getMap().get(s).get(t);
		    }
		}
	    }
	} else {
	    if (p.getOperator().equalsIgnoreCase("MAX") | p.getOperator().equalsIgnoreCase("OR")
		    | p.getOperator().equalsIgnoreCase("XOR")) {
		double parentThreshold = p.getThreshold();
		double firstChild = getSimilarity(sourceInstance, targetInstance, p.getTerm1(), p.getThreshold1(),
			sourceVar, targetVar);
		double secondChild = getSimilarity(sourceInstance, targetInstance, p.getTerm2(), p.getThreshold2(),
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
		double firstChild = getSimilarity(sourceInstance, targetInstance, p.getTerm1(), p.getThreshold1(),
			sourceVar, targetVar);
		double secondChild = getSimilarity(sourceInstance, targetInstance, p.getTerm2(), p.getThreshold2(),
			sourceVar, targetVar);

		double minSimilarity = Math.min(firstChild, secondChild);
		if (firstChild == 0 && secondChild == 0)
		    return 0;
		if (minSimilarity >= parentThreshold)
		    return minSimilarity;
		else
		    return 0;
	    }
	    if (p.getOperator().equalsIgnoreCase("ADD")) {
		double parentThreshold = p.getThreshold();
		double firstChild = p.getCoef1() * getSimilarity(sourceInstance, targetInstance, p.getTerm1(),
			p.getThreshold1(), sourceVar, targetVar);
		double secondChild = p.getCoef2() * getSimilarity(sourceInstance, targetInstance, p.getTerm2(),
			p.getThreshold2(), sourceVar, targetVar);
		if (firstChild + secondChild >= parentThreshold)
		    return firstChild + secondChild;
		else
		    return 0;

	    } else {// perform MINUS as usual
		// logger.warn("Not sure what to do with operator " + p.op + ".
		// Using MAX.");
		double parentThreshold = p.getThreshold();
		double firstChild = getSimilarity(sourceInstance, targetInstance, p.getTerm1(), p.getThreshold1(),
			sourceVar, targetVar);
		double secondChild = getSimilarity(sourceInstance, targetInstance, p.getTerm2(), p.getThreshold2(),
			sourceVar, targetVar);
		// the second similarity must be 0 in order for the instance to
		// have a change to be included at the final result
		if (secondChild == 0) {
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
	    runtime = runtime + MeasureFactory.getMeasure(measures.get(i)).getRuntimeApproximation(mappingSize);
	return runtime;
    }
}
