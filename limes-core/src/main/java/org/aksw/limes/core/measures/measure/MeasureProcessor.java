package org.aksw.limes.core.measures.measure;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.aksw.limes.core.data.Instance;
import org.aksw.limes.core.io.parser.Parser;

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

    public static double getSimilarity(Instance sourceInstance, Instance targetInstance, String expression,
	    String sourceVar, String targetVar) {
	Parser p = new Parser(expression, 0);

	if (p.isAtomic()) {
	    Measure measure = MeasureFactory.getMeasure(p.getOperator());
	    // get property name
	    // 0. get properties
	    // get property labels
	    // get first property label
	    String property1 = null, property2 = null;
	    // get property labels

	    // get first property label
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
		    property1 = property;
		} else {
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
		    // property1 = split[1];
		    property1 = property;
		} else {
		    // property2 = split[1];
		    property2 = property;
		}
	    } else {
		property2 = term2;
	    }

	    // if no properties then terminate
	    if (property1 == null || property2 == null) {
		logger.fatal("Property values could not be read. Exiting");
	    } else
		return measure.getSimilarity(sourceInstance, targetInstance, property1, property2);
	} else {
	    if (p.getOperator().equalsIgnoreCase("MAX") | p.getOperator().equalsIgnoreCase("OR") | p.getOperator().equalsIgnoreCase("XOR")) {
		return Math.max(getSimilarity(sourceInstance, targetInstance, p.getTerm1(), sourceVar, targetVar),
			getSimilarity(sourceInstance, targetInstance, p.getTerm2(), sourceVar, targetVar));
	    }
	    if (p.getOperator().equalsIgnoreCase("MIN") | p.getOperator().equalsIgnoreCase("AND")) {
		return Math.min(getSimilarity(sourceInstance, targetInstance, p.getTerm1(), sourceVar, targetVar),
			getSimilarity(sourceInstance, targetInstance, p.getTerm2(), sourceVar, targetVar));
	    }
	    if (p.getOperator().equalsIgnoreCase("ADD")) {
		return p.getCoef1() * getSimilarity(sourceInstance, targetInstance, p.getTerm1(), sourceVar, targetVar)
			+ p.getCoef2() * getSimilarity(sourceInstance, targetInstance, p.getTerm2(), sourceVar, targetVar);
	    } else {
		logger.warn("Not sure what to do with operator " + p.getOperator() + ". Using MAX.");
		return Math.max(getSimilarity(sourceInstance, targetInstance, p.getTerm1(), sourceVar, targetVar),
			getSimilarity(sourceInstance, targetInstance, p.getTerm2(), sourceVar, targetVar));
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
