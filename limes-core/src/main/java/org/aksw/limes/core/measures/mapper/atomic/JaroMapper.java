/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.atomic;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.IMapper;
import org.aksw.limes.core.measures.mapper.SetOperations;

import java.util.*;

import org.aksw.limes.core.measures.measure.string.Jaro;
import org.aksw.limes.core.util.RandomStringGenerator;
import org.apache.log4j.Logger;

/**
 *
 * @author ngonga
 */
public class JaroMapper implements IMapper{

    static Logger logger = Logger.getLogger("LIMES");

    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression,
	    double threshold) {
	logger.info("Running JaroMapper");
	List<String> properties = PropertyFetcher.getProperties(expression, threshold);
	Map<String, Set<String>> sourceMap = getValueToUriMap(source, properties.get(0));
	Map<String, Set<String>> targetMap = getValueToUriMap(target, properties.get(1));
	return runWithoutPrefixFilter(sourceMap, targetMap, threshold);
	// return bruteForce(sourceMap, targetMap, threshold);
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

    public String getName() {
	return "jaro";
    }

    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return 1000d;
    }

    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return 1000;
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
	double maxSourceLength, maxTargetLength, theta;

	for (Integer sourceLength : sourceLengthIndex.keySet()) {
	    for (Integer targetLength : targetLengthIndex.keySet()) {
		theta = (3 * threshold - 1) * sourceLength * targetLength / (2 * (sourceLength + targetLength));
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
	Jaro j = new Jaro();
	Set<String> source = sourceMap.keySet();
	Set<String> target = targetMap.keySet();
	Map<Integer, Set<String>> sourceLengthIndex = getLengthIndex(source);
	Map<Integer, Set<String>> targetLengthIndex = getLengthIndex(target);
	// Map<String, Set<Character>> sourceCharacterMap = getChars(source);
	// Map<String, Set<Character>> targetCharacterMap = getChars(target);
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
	Jaro j = new Jaro();
	Set<String> source = sourceMap.keySet();
	Set<String> target = targetMap.keySet();
	Map<Integer, Set<String>> sourceLengthIndex = getLengthIndex(source);
	Map<Integer, Set<String>> targetLengthIndex = getLengthIndex(target);
	// Map<String, Set<Character>> sourceCharacterMap = getChars(source);
	// Map<String, Set<Character>> targetCharacterMap = getChars(target);
	Mapping result = new MemoryMapping();
	double maxSourceLength, maxTargetLength, similarity, theta;
	List<Character> sourceMappingCharacters, targetMappingCharacters;
	int halfLength, transpositions, lengthFilterCount = 0, prefixFilterCount = 0, characterFilterCount = 0;

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
	// System.out.println(prefixFilterCount
	// + " = " + ((double) prefixFilterCount) / (source.size()
	// * target.size()));
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
    private Set<Character> getCharSet(String s) {
	Set<Character> result = new HashSet<Character>();
	char[] characters = s.toCharArray();
	for (int i = 0; i < characters.length; i++) {
	    result.add(characters[i]);
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

    private Mapping bruteForce(Map<String, Set<String>> sourceMap, Map<String, Set<String>> targetMap,
	    double threshold) {
	Jaro j = new Jaro();
	Mapping m = new MemoryMapping();
	double sim;
	for (String s : sourceMap.keySet()) {
	    for (String t : targetMap.keySet()) {
		sim = j.getSimilarity(s, t);
		if (sim >= threshold) {
		    for (String sourceUris : sourceMap.get(s)) {
			for (String targetUris : targetMap.get(t)) {
			    m.add(s, t, sim);
			}
		    }
		}
	    }
	}
	return m;
    }

    private void test(int sourceSize, int targetSize, double threshold) {
	Map<String, Set<String>> sourceMap = generateRandomMap(sourceSize);
	Map<String, Set<String>> targetMap = generateRandomMap(targetSize);

	long begin = System.currentTimeMillis();
	Mapping m1 = bruteForce(sourceMap, targetMap, threshold);
	long end = System.currentTimeMillis();
	System.out.println("Brute force: " + (end - begin));
	System.out.println("Mapping size : " + (m1.getNumberofMappings()));

	begin = System.currentTimeMillis();
	Mapping m2 = run(sourceMap, targetMap, threshold);
	end = System.currentTimeMillis();
	System.out.println("Approach: " + (end - begin));
	System.out.println("Mapping size : " + (m2.getNumberofMappings()));
	System.out.println("Mapping size : " + (SetOperations.difference(m1, m2)));
    }

    public Map<String, Set<String>> generateRandomMap(int size) {
	Map<String, Set<String>> map = new HashMap<String, Set<String>>();
	RandomStringGenerator rsg = new RandomStringGenerator(5, 20);
	while (map.size() < size) {
	    String s = rsg.generateString();
	    Set<String> set = new HashSet<String>();
	    set.add(s);
	    map.put(s, set);
	}
	return map;
    }

    private Map<String, Set<Character>> getChars(Set<String> source) {
	Map<String, Set<Character>> map = new HashMap<String, Set<Character>>();
	for (String s : source) {
	    map.put(s, getCharSet(s));
	}
	return map;
    }
}
