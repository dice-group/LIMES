/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.aksw.limes.core.measures.mapper.atomic.fastngram;

import java.util.*;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.Mapper;
import org.aksw.limes.core.measures.measure.string.QGramSimilarity;
import org.aksw.limes.core.io.cache.Cache;
import org.apache.log4j.Logger;

/**
 *
 * @author ngonga
 */
public class FastNGram extends Mapper {

    static Logger logger = Logger.getLogger("LIMES");
    static int q = 3;

    public String getName() {
	return "FastNGram";
    }

    public static Mapping compute(Set<String> source, Set<String> target, int q, double threshold) {
	Index index = new Index(q);
	double kappa = (1 + threshold) / threshold;
	QGramSimilarity sim = new QGramSimilarity(q);
	Tokenizer tokenizer = new NGramTokenizer();
	Map<String, Set<String>> targetTokens = new HashMap<String, Set<String>>();
	Mapping result = new MemoryMapping();
	// index target
	for (String t : target) {
	    targetTokens.put(t, index.addString(t));
	}
	// run similarity computation

	for (String s : source) {
	    Set<Integer> allSizes = index.getAllSizes();
	    Set<String> sourceTokens = tokenizer.tokenize(s, q);
	    double sourceSize = (double) sourceTokens.size();
	    for (int size = (int) Math.ceil(sourceSize * threshold); size <= (int) Math
		    .floor(sourceSize / threshold); size++) {
		if (allSizes.contains(size)) {
		    // maps tokens to strings
		    Map<String, Set<String>> stringsOfSize = index.getStrings(size);
		    Map<String, Integer> countMap = new HashMap<String, Integer>();
		    for (String token : sourceTokens) {
			if (stringsOfSize.containsKey(token)) {
			    // take each string and add it to the count map
			    Set<String> candidates = stringsOfSize.get(token);
			    for (String candidate : candidates) {
				if (!countMap.containsKey(candidate)) {
				    countMap.put(candidate, 0);
				}
				countMap.put(candidate, countMap.get(candidate) + 1);
			    }
			}
		    }
		    // now apply filtering |X \cap Y| \geq \kappa(|X| + |Y|)
		    for (String candidate : countMap.keySet()) {
			double count = (double) countMap.get(candidate);
			if (kappa * count >= (sourceSize + size)) {
			    double similarity = sim.getSimilarity(targetTokens.get(candidate), sourceTokens);
			    if (similarity >= threshold) {
				result.add(s, candidate, similarity);
			    }
			}
		    }
		}
	    }

	}
	return result;
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
	Mapping mapping = new MemoryMapping();
	if (threshold <= 0) {
	    logger.warn("Wrong threshold setting. Returning empty mapping.");
	    return mapping;
	}
	String property1 = null, property2 = null;
	// get property labels
	Parser p = new Parser(expression, threshold);

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
	    logger.fatal("Property 1 = " + property1 + ", Property 2 = " + property2);
	    logger.fatal("Property values could not be read. Exiting");
	    System.exit(1);
	}

	if (!p.isAtomic()) {
	    logger.fatal("Mappers can only deal with atomic expression");
	    logger.fatal("Expression " + expression + " was given to a mapper to process");
	    System.exit(1);
	}

	/////////////////// This actually runs the algorithm
	Map<String, Set<String>> sourceMap = new HashMap<String, Set<String>>();
	ArrayList<String> sourceUris = source.getAllUris();
	Map<String, Set<String>> targetMap = new HashMap<String, Set<String>>();
	ArrayList<String> targetUris = target.getAllUris();

	// index source values
	for (String s : sourceUris) {
	    TreeSet<String> values = source.getInstance(s).getProperty(property1);
	    for (String v : values) {
		if (!sourceMap.containsKey(v)) {
		    sourceMap.put(v, new HashSet<String>());
		}
		sourceMap.get(v).add(s);
	    }
	}

	// index target values
	// logger.info("Indexing target values");
	for (String t : targetUris) {
	    TreeSet<String> values = target.getInstance(t).getProperty(property2);
	    for (String v : values) {
		if (!targetMap.containsKey(v)) {
		    targetMap.put(v, new HashSet<String>());
		}
		targetMap.get(v).add(t);
	    }
	}

	// run the algorithm
	// logger.info("Computing mappings");
	Mapping m = FastNGram.compute(sourceMap.keySet(), targetMap.keySet(), q, threshold);
	Mapping result = new MemoryMapping();
	for (String s : m.getMap().keySet()) {
	    for (String t : m.getMap().get(s).keySet()) {
		for (String sourceUri : sourceMap.get(s)) {
		    for (String targetUri : targetMap.get(t)) {
			result.add(sourceUri, targetUri, m.getConfidence(s, t));
		    }
		}
	    }
	}

	return result;

    }

    public double getRuntimeApproximation(int sourceSize, int targetSize, double threshold, Language language) {
	if (language.equals(Language.DE)) {
	    // error = 667.22
	    return 492.9 + 0.09 * sourceSize + 0.09 * targetSize - 1032.3 * threshold;
	} else {
	    // error = 5.45
	    return 59.82 + 0.01 * sourceSize + 0.01 * targetSize - 114.2 * threshold;
	}
    }

    public double getMappingSizeApproximation(int sourceSize, int targetSize, double threshold, Language language) {
	if (language.equals(Language.DE)) {
	    // error = 667.22
	    return 727.2 + 0.063 * sourceSize + 0.063 * targetSize - 1305.1 * threshold;
	} else {
	    // error = 5.45
	    return 8.2 + 0.001 * sourceSize + 0.001 * targetSize - 16.75 * threshold;
	}
    }

}
