/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.atomic;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.io.parser.Parser;

import org.aksw.limes.core.measures.mapper.Mapper;
import org.aksw.limes.core.measures.measure.metricfactory.SimpleMetricFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * DEPRECATED
 * 
 * @author ngonga
 */
public class TotalOrderMapper extends Mapper {

    public String getName() {
	return "TotalOrderMapper";
    }

    /**
     * Computes a mapping between a source and a target.
     *
     * @param source
     *            Source cache
     * @param target
     *            Target cache
     * @param sourceVar
     *            Variable for the source dataset
     * @param targetVar
     *            Variable for the target dataset
     * @param expression
     *            Expression to process.
     * @param threshold
     *            Similarity threshold
     * @return A mapping which contains links between the source instances and
     *         the target instances
     */
    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression,
	    double threshold) {

	ArrayList<Double> valueList = new ArrayList<Double>();

	// 0. get properties
	String property1, property2;
	// get property labels
	Parser p = new Parser(expression, threshold);
	// get first property label
	String term1 = p.getTerm1();
	if (term1.contains(".")) {
	    property1 = term1.split("\\.")[1];
	} else {
	    property1 = term1;
	}

	// get second property label
	String term2 = p.getTerm2();
	if (term2.contains(".")) {
	    property2 = term2.split("\\.")[1];
	} else {
	    property2 = term2;
	}

	// 1. sort elements of target
	HashMap<Double, String> valueToUri = new HashMap<Double, String>();
	for (String key : target.getAllUris()) {
	    for (String value : target.getInstance(key).getProperty(property2)) {
		// data type should play a role here
		valueToUri.put(new Double(value), key);
		valueList.add(new Double(value));
	    }
	}

	SimpleMetricFactory factory = new SimpleMetricFactory("", "");
	factory.setExpression(expression);

	Comparator<Double> comparator = Collections.reverseOrder();
	Collections.sort(valueList, comparator);
	// 2. search for closest match for each element of source
	for (String key : source.getAllUris()) {
	    for (String value : source.getInstance(key).getProperty(property1)) {
		int index = binarySearch(valueList, new Double(value));
		// 3. go to the left until sim is below threshold
		for (int i = index; i >= 0; i--) {
		    if (factory.getSimilarity(key, valueList.get(i) + "") >= threshold) {

		    }
		}
		// 4. go to the left until sim is below threshold
		for (int i = index + 1; i < valueList.size(); i++) {

		}
	    }
	}

	// 4. go to the right until sim is below threshold
	return new MemoryMapping();
    }

    public static int binarySearch(ArrayList<Double> a, Double x) {
	int low = 0;
	int high = a.size() - 1;
	int mid;

	while (low <= high) {
	    mid = (low + high) / 2;

	    if (a.get(mid).compareTo(x) < 0) {
		low = mid + 1;
	    } else if (a.get(mid).compareTo(x) > 0) {
		high = mid - 1;
	    } else {
		return mid;
	    }
	}
	return low;
    }

    public double getRuntimeApproximation(int sourceSize, int targetSize, double threshold, Language language) {
	if (language.equals(Language.DE)) {
	    // error = 667.22
	    return 16.27 + 5.1 * sourceSize + 4.9 * targetSize - 23.44 * threshold;
	} else {
	    // error = 5.45
	    return 0.62 + 0.001 * sourceSize + 0.001 * targetSize - 0.53 * threshold;
	}
    }

    public double getMappingSizeApproximation(int sourceSize, int targetSize, double threshold, Language language) {
	if (language.equals(Language.DE)) {
	    // error = 667.22
	    return 2333 + 0.14 * sourceSize + 0.14 * targetSize - 3905 * threshold;
	} else {
	    // error = 5.45
	    return -1.84 + 0.0006 * sourceSize + 0.0006 * targetSize;
	}
    }

    public double getSelectivity(int sourceSize, int targetSize, double threshold, Language language) {
	return getMappingSizeApproximation(sourceSize, targetSize, threshold, language)
		/ (double) (sourceSize * targetSize);
    }
}
