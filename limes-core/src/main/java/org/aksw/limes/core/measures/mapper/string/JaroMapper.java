/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.string;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.Mapper;
import org.aksw.limes.core.measures.mapper.PropertyFetcher;

import java.util.*;

import org.aksw.limes.core.measures.measure.string.Jaro;
import org.apache.log4j.Logger;

/**
 *
 * @author ngonga
 */
public class JaroMapper extends Mapper {

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
    @Override
    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression,
	    double threshold) {
	logger.info("Running JaroMapper");
	List<String> properties = PropertyFetcher.getProperties(expression, threshold);
	Map<String, Set<String>> sourceMap = getValueToUriMap(source, properties.get(0));
	Map<String, Set<String>> targetMap = getValueToUriMap(target, properties.get(1));
	return runWithoutPrefixFilter(sourceMap, targetMap, threshold);
    }

    public Map<String, Set<String>> getValueToUriMap(Cache c, String property) {
	Map<String, Set<String>> result = new HashMap<String, Set<String>>();
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
    @Override
    public String getName() {
	return "jaro";
    }
    @Override
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return 1000d;
    }
    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return 1000d;
    }

    private double getMaxComparisonLength(double length, double threshold, double maxLength) {
	double l = maxLength * length / (((3 * threshold - 1)) * length - maxLength);
	if (l < 0) {
	    return Double.MAX_VALUE;
	}
	return l;
    }

    public Mapping runLenghtOnly(Map<String, Set<String>> sourceMap, Map<String, Set<String>> targetMap,
	    double threshold) {
	Jaro j = new Jaro();
	Set<String> source = sourceMap.keySet();
	Set<String> target = targetMap.keySet();
	Map<Integer, Set<String>> sourceLengthIndex = getLengthIndex(source);
	Map<Integer, Set<String>> targetLengthIndex = getLengthIndex(target);
	Mapping result = new MemoryMapping();
	double maxSourceLength, maxTargetLength;

	for (Integer sourceLength : sourceLengthIndex.keySet()) {
	    for (Integer targetLength : targetLengthIndex.keySet()) {
		maxSourceLength = getMaxComparisonLength((double) sourceLength, threshold,
			Math.min(sourceLength, targetLength));
		maxTargetLength = getMaxComparisonLength((double) targetLength, threshold,
			Math.min(sourceLength, targetLength));
		// length-aware filter
		if (sourceLength <= maxTargetLength && targetLength <= maxSourceLength) {
		    for (String s : sourceLengthIndex.get(sourceLength)) {
			for (String t : targetLengthIndex.get(targetLength)) {
			    // if everything maps
			    double similarity = j.getSimilarity(s, t);
			    if (similarity >= threshold) {
				for (String sourceUri : sourceMap.get(s)) {
				    for (String targetUri : targetMap.get(t)) {
					result.add(sourceUri, targetUri, similarity);
				    }
				}
			    }
			}
		    }
		}
	    }
	}
	return result;
    }

    public Mapping run(Map<String, Set<String>> sourceMap, Map<String, Set<String>> targetMap, double threshold) {
	Set<String> source = sourceMap.keySet();
	Set<String> target = targetMap.keySet();
	Map<Integer, Set<String>> sourceLengthIndex = getLengthIndex(source);
	Map<Integer, Set<String>> targetLengthIndex = getLengthIndex(target);

	Mapping result = new MemoryMapping();
	double maxSourceLength, maxTargetLength, similarity, theta;
	List<Character> sourceMappingCharacters, targetMappingCharacters;
	Set<Character> sourcePrefix, targetPrefix;
	boolean passed;
	int halfLength, transpositions, lengthFilterCount = 0, prefixFilterCount = 0, characterFilterCount = 0;
	int sourcePrefixLength, targetPrefixLength;
	// length-aware filter
	for (Integer sourceLength : sourceLengthIndex.keySet()) {
	    for (Integer targetLength : targetLengthIndex.keySet()) {
		maxTargetLength = getMaxComparisonLength((double) sourceLength, threshold,
			Math.min(sourceLength, targetLength));
		maxSourceLength = getMaxComparisonLength((double) targetLength, threshold,
			Math.min(sourceLength, targetLength));

		if (sourceLength <= maxSourceLength && targetLength <= maxTargetLength) {
		    theta = (3 * threshold - 1) * sourceLength * targetLength / (2 * (sourceLength + targetLength));
		    halfLength = ((int) (Math.min(sourceLength, targetLength))) / 2;
		    sourcePrefixLength = sourceLength - (int) theta;
		    targetPrefixLength = targetLength - (int) theta;
		    for (String s : sourceLengthIndex.get(sourceLength)) {
			sourcePrefix = getCharSet(s, sourcePrefixLength);
			for (String t : targetLengthIndex.get(targetLength)) {
			    lengthFilterCount++;
			    passed = false;
			    // prefix filtering
			    targetPrefix = getCharSet(t, targetPrefixLength);
			    if (sourcePrefix.isEmpty() || targetPrefix.isEmpty()) {
				passed = true;
			    } else {
				passed = contains(sourcePrefix, targetPrefix);
			    }

			    // character-based filtering
			    if (passed) {
				prefixFilterCount++;
				sourceMappingCharacters = Jaro.getCommonCharacters(s, t, halfLength);
				if (sourceMappingCharacters.size() >= theta) {
				    // if everything maps
				    targetMappingCharacters = Jaro.getCommonCharacters(t, s, halfLength);// targetCharacterMap.get(t);
				    transpositions = Jaro.getTranspositions(sourceMappingCharacters,
					    targetMappingCharacters);
				    if (transpositions >= 0) {
					characterFilterCount++;
					similarity = ((sourceMappingCharacters.size() / (float) sourceLength)
						+ (targetMappingCharacters.size() / (float) targetLength)
						+ ((sourceMappingCharacters.size() - transpositions)
							/ (float) sourceMappingCharacters.size()))
						/ 3.0;
					if (similarity >= threshold) {
					    for (String sourceUri : sourceMap.get(s)) {
						for (String targetUri : targetMap.get(t)) {
						    result.add(sourceUri, targetUri, similarity);
						}
					    }
					}
				    }
				}
			    }
			}
		    }
		}
	    }
	}
	System.out.println(lengthFilterCount + " = " + ((double) lengthFilterCount) / (source.size() * target.size()));
	System.out.println(prefixFilterCount + " = " + ((double) prefixFilterCount) / (source.size() * target.size()));
	System.out.println(
		characterFilterCount + " = " + ((double) characterFilterCount) / (source.size() * target.size()));
	return result;
    }

    public Mapping runWithoutPrefixFilter(Map<String, Set<String>> sourceMap, Map<String, Set<String>> targetMap,
	    double threshold) {
	Set<String> source = sourceMap.keySet();
	Set<String> target = targetMap.keySet();
	Map<Integer, Set<String>> sourceLengthIndex = getLengthIndex(source);
	Map<Integer, Set<String>> targetLengthIndex = getLengthIndex(target);

	Mapping result = new MemoryMapping();
	double maxSourceLength, maxTargetLength, similarity, theta;
	List<Character> sourceMappingCharacters, targetMappingCharacters;
	int halfLength, transpositions, lengthFilterCount = 0, characterFilterCount = 0;

	// length-aware filter
	for (Integer sourceLength : sourceLengthIndex.keySet()) {
	    for (Integer targetLength : targetLengthIndex.keySet()) {
		theta = (3 * threshold - 1) * sourceLength * targetLength / (2 * (sourceLength + targetLength));
		halfLength = ((int) (Math.min(sourceLength, targetLength))) / 2;
		maxTargetLength = getMaxComparisonLength((double) sourceLength, threshold,
			Math.min(sourceLength, targetLength));
		maxSourceLength = getMaxComparisonLength((double) targetLength, threshold,
			Math.min(sourceLength, targetLength));
		if (sourceLength <= maxSourceLength && targetLength <= maxTargetLength) {
		    for (String s : sourceLengthIndex.get(sourceLength)) {
			for (String t : targetLengthIndex.get(targetLength)) {
			    lengthFilterCount++;
			    sourceMappingCharacters = Jaro.getCommonCharacters(s, t, halfLength);
			    if (sourceMappingCharacters.size() >= theta) {
				targetMappingCharacters = Jaro.getCommonCharacters(t, s, halfLength);
				transpositions = Jaro.getTranspositions(sourceMappingCharacters,
					targetMappingCharacters);
				if (transpositions != -1) {
				    characterFilterCount++;
				    similarity = ((sourceMappingCharacters.size() / (float) sourceLength)
					    + (targetMappingCharacters.size() / (float) targetLength)
					    + (sourceMappingCharacters.size() - transpositions)
						    / (float) sourceMappingCharacters.size())
					    / 3.0;
				    if (similarity >= threshold) {
					for (String sourceUri : sourceMap.get(s)) {
					    for (String targetUri : targetMap.get(t)) {
						result.add(sourceUri, targetUri, similarity);
					    }
					}
				    }
				}
			    }
			}
		    }
		}
	    }
	}
	System.out.println(lengthFilterCount + " = " + ((double) lengthFilterCount) / (source.size() * target.size()));

	System.out.println(
		characterFilterCount + " = " + ((double) characterFilterCount) / (source.size() * target.size()));
	return result;
    }

    private boolean contains(Set<Character> source, Set<Character> target) {
	for (Character s : source) {
	    if (target.contains(s)) {
		return true;
	    }
	}
	return false;
    }

    private Map<Integer, Set<String>> getLengthIndex(Set<String> strings) {
	Map<Integer, Set<String>> result = new HashMap<Integer, Set<String>>();
	for (String s : strings) {
	    Integer i = new Integer(s.length());
	    if (!result.containsKey(i)) {
		result.put(i, new HashSet<String>());
	    }
	    result.get(i).add(s);
	}
	return result;
    }

    /**
     * Returns the set of characters contained in a string
     *
     * @param s
     *            Input String
     * @return Set of characters contained in it
     */
    private Set<Character> getCharSet(String s, int length) {
	Set<Character> result = new HashSet<Character>();
	char[] characters = s.toCharArray();
	for (int i = 0; (i < characters.length) && (i < length); i++) {
	    result.add(characters[i]);
	}
	return result;
    }

}
