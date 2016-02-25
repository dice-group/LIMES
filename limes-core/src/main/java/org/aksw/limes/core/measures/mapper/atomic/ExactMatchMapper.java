/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.atomic;

import java.lang.String;
import java.util.*;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.Mapper;
import org.apache.log4j.Logger;

/**
 *
 * @author ngonga
 */
public class ExactMatchMapper extends Mapper {

    static Logger logger = Logger.getLogger("LIMES");

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

	logger.info("Starting ExactMatchMapper");
	String property1 = null, property2 = null;
	// get property labels
	Parser p = new Parser(expression, threshold);

	// get first property label
	String term1 = "?" + p.getTerm1();
	String term2 = "?" + p.getTerm2();
	String split[];
	String var;

	if (term1.contains(".")) {
	    split = term1.split("\\.");
	    var = split[0];
	    if (var.equals(sourceVar)) {
		property1 = split[1];
	    } else {
		property2 = split[1];
	    }
	} else {
	    property1 = term1;
	}

	// get second property label
	if (term2.contains(".")) {
	    split = term2.split("\\.");
	    var = split[0];
	    if (var.equals(sourceVar)) {
		property1 = split[1];
	    } else {
		property2 = split[1];
	    }
	} else {
	    property2 = term2;
	}

	// if no properties then terminate
	if (property1 == null || property2 == null) {
	    logger.fatal("Property values could not be read. Exiting");
	    System.exit(1);
	}

	if (!p.isAtomic()) {
	    logger.fatal("Mappers can only deal with atomic expression");
	    logger.fatal("Expression " + expression + " was given to a mapper to process");
	    System.exit(1);
	}

	Map<String, Set<String>> sourceIndex = index(source, property1);
	Map<String, Set<String>> targetIndex = index(target, property2);

	Mapping m = new MemoryMapping();

	if (sourceIndex.keySet().size() < targetIndex.keySet().size()) {
	    for (String value : sourceIndex.keySet()) {
		if (targetIndex.containsKey(value)) {
		    for (String sourceUri : sourceIndex.get(value)) {
			for (String targetUri : targetIndex.get(value)) {
			    m.add(sourceUri, targetUri, 1d);
			}
		    }
		}
	    }
	} else {
	    for (String value : targetIndex.keySet()) {
		if (sourceIndex.containsKey(value)) {
		    for (String sourceUri : sourceIndex.get(value)) {
			for (String targetUri : targetIndex.get(value)) {
			    m.add(sourceUri, targetUri, 1d);
			}
		    }
		}
	    }
	}
	return m;
    }

    public Map<String, Set<String>> index(Cache c, String property) {
	Map<String, Set<String>> index = new HashMap<String, Set<String>>();
	for (String uri : c.getAllUris()) {
	    TreeSet<String> values = c.getInstance(uri).getProperty(property);
	    for (String v : values) {
		if (!index.containsKey(v)) {
		    index.put(v, new HashSet<String>());
		}
		index.get(v).add(uri);
	    }
	}
	return index;
    }

    public String getName() {
	return "exactMatch";
    }

    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	// dummy
	return (sourceSize + targetSize) / 1000d;
    }

    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	// dummy
	return (sourceSize + targetSize) / 1000d;
    }
}
