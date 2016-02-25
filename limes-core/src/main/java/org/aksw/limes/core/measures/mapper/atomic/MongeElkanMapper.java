package org.aksw.limes.core.measures.mapper.atomic;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.Mapper;
import org.aksw.limes.core.measures.measure.string.TrigramMeasure;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author Peggy Lucke
 *
 */
public class MongeElkanMapper extends Mapper {

    static Logger logger = Logger.getLogger("LIMES");

    // Tokens are divide by space
    private String split = " ";

    // Token divide by another character as space
    public void setSplit(String split) {
	this.split = split;
    }

    private Map<String, Set<String>> getValueToUriMap(Cache c, String property) {
	Map<String, Set<String>> result = new HashMap<>();
	List<String> uris = c.getAllUris();
	for (String uri : uris) {
	    Set<String> values = c.getInstance(uri).getProperty(property);
	    for (String value : values) {
		if (!result.containsKey(value)) {
		    result.put(value, new HashSet<String>());
		}
		result.get(value).add(uri);
	    }
	}
	return result;
    }

    /**
     * @param source
     *            Texts to compare with target
     * @param target
     *            Texts to compare with source
     * @param threshold
     *            is the minimum similarity of the results
     * @return all results of source compare with target together with the
     *         similarity of them
     */
    public Map<String, Map<String, Double>> mongeElkan(Set<String> source, Set<String> target, double threshold) {
	Iterator<String> sit = source.iterator();// As
	double resultDouble;
	// (A,B)+result
	Map<String, Map<String, Double>> result = new HashMap<>();
	while (sit.hasNext()) {// ein A
	    Iterator<String> tit = target.iterator();// Bs
	    String sourceString = sit.next();
	    HashMap<String, Double> resultB = new HashMap<>();
	    while (tit.hasNext()) {// ein B --> schneller machen, wenn B = vorh.
				   // B
		String targetString = tit.next();
		resultDouble = oneMongeElkan(sourceString.split(split), targetString.split(split), threshold);
		if (threshold <= resultDouble) {
		    resultB.put(targetString, resultDouble);
		}
	    }
	    result.put(sourceString, resultB);
	}
	return result;
    }

    /*
     * compare one text with another
     */
    private double oneMongeElkan(String[] sourceToken, String[] targetToken, double threshold) {
	double simB = 0;
	double result = 0;
	float maxNumber = sourceToken.length;
	/*
	 * the minimum of the result to reach the threshold
	 */
	float treshMin = (float) (maxNumber * threshold);
	for (String sourceString : sourceToken) {// ein a
	    double maxSim = 0;
	    for (String targetString : targetToken) {// ein b
		double sim = tokenSim(sourceString, targetString);
		if (maxSim < sim) {
		    maxSim = sim;
		}
		if (maxSim == 1) {
		    break;
		}
	    }
	    maxNumber -= 1 - maxSim;
	    /*
	     * add 0.0001 for rounding errors. if the similarity of all source
	     * tokens with the target tokens don't reach the minimum threshold,
	     * there are no result, so break the algorithm.
	     */
	    if (treshMin > maxNumber + 0.0001) {
		result = 0;
		break;
	    }
	    simB += maxSim;
	}
	if (simB != 0) {
	    result = simB / sourceToken.length;
	}
	return result;
    }

    /*
     * use the Trigramm Algorithm to compare the tokens
     */
    private double tokenSim(String tokenA, String tokenB) {
	TrigramMeasure trigram = new TrigramMeasure();
	double result = trigram.getSimilarity(tokenA, tokenB);
	return result;
    }

    @Override
    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression,
	    double threshold) {
	logger.info("Running MongeElkanMapper");

	List<String> properties = PropertyFetcher.getProperties(expression, threshold);
	Map<String, Set<String>> sourceMap = getValueToUriMap(source, properties.get(0));
	Map<String, Set<String>> targetMap = getValueToUriMap(target, properties.get(1));
	Map<String, Map<String, Double>> similarityBook = mongeElkan(sourceMap.keySet(), targetMap.keySet(), threshold);
	logger.info("Similarity Book has " + String.valueOf(similarityBook.size()) + " entries.");
	Mapping result = new MemoryMapping();
	for (String s : similarityBook.keySet()) {
	    for (String t : similarityBook.get(s).keySet()) {
		for (String sourceUri : sourceMap.get(s)) {
		    for (String targetUri : targetMap.get(t)) {
			result.add(sourceUri, targetUri, similarityBook.get(s).get(t));
		    }
		}
	    }
	}
	return result;
    }

    @Override
    public String getName() {
	return "monge-elkan";
    }

    @Override
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return -1d;
    }

    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return -1d;
    }
}