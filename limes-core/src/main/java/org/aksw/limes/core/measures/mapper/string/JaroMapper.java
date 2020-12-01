package org.aksw.limes.core.measures.mapper.string;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.exceptions.InvalidThresholdException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.mapper.pointsets.PropertyFetcher;
import org.aksw.limes.core.measures.measure.string.JaroMeasure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class JaroMapper extends AMapper {

    static Logger logger = LoggerFactory.getLogger(JaroMapper.class);

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
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
            double threshold) {
        if (threshold <= 0) {
            throw new InvalidThresholdException(threshold);
        }
        List<String> properties = PropertyFetcher.getProperties(expression, threshold);
        Map<String, Set<String>> sourceMap = getValueToUriMap(source, properties.get(0));
        Map<String, Set<String>> targetMap = getValueToUriMap(target, properties.get(1));
        return runWithoutPrefixFilter(sourceMap, targetMap, threshold);
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

    public AMapping runLenghtOnly(Map<String, Set<String>> sourceMap, Map<String, Set<String>> targetMap,
            double threshold) {
        JaroMeasure j = new JaroMeasure();
        Set<String> source = sourceMap.keySet();
        Set<String> target = targetMap.keySet();
        Map<Integer, Set<String>> sourceLengthIndex = getLengthIndex(source);
        Map<Integer, Set<String>> targetLengthIndex = getLengthIndex(target);
        AMapping result = MappingFactory.createDefaultMapping();
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

    public AMapping run(Map<String, Set<String>> sourceMap, Map<String, Set<String>> targetMap, double threshold) {
        Set<String> source = sourceMap.keySet();
        Set<String> target = targetMap.keySet();
        Map<Integer, Set<String>> sourceLengthIndex = getLengthIndex(source);
        Map<Integer, Set<String>> targetLengthIndex = getLengthIndex(target);

        AMapping result = MappingFactory.createDefaultMapping();
        double maxSourceLength, maxTargetLength, similarity, theta;
        List<Character> sourceMappingCharacters, targetMappingCharacters;
        Set<Character> sourcePrefix, targetPrefix;
        boolean passed;
        int halfLength, transpositions;
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
                                sourceMappingCharacters = JaroMeasure.getCommonCharacters(s, t, halfLength);
                                if (sourceMappingCharacters.size() >= theta) {
                                    // if everything maps
                                    targetMappingCharacters = JaroMeasure.getCommonCharacters(t, s, halfLength);// targetCharacterMap.get(t);
                                    transpositions = JaroMeasure.getTranspositions(sourceMappingCharacters,
                                            targetMappingCharacters);
                                    if (transpositions >= 0) {
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
        return result;
    }

    public AMapping runWithoutPrefixFilter(Map<String, Set<String>> sourceMap, Map<String, Set<String>> targetMap,
            double threshold) {
        Set<String> source = sourceMap.keySet();
        Set<String> target = targetMap.keySet();
        Map<Integer, Set<String>> sourceLengthIndex = getLengthIndex(source);
        Map<Integer, Set<String>> targetLengthIndex = getLengthIndex(target);

        AMapping result = MappingFactory.createDefaultMapping();
        double maxSourceLength, maxTargetLength, similarity, theta;
        List<Character> sourceMappingCharacters, targetMappingCharacters;
        int halfLength, transpositions;

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
                            sourceMappingCharacters = JaroMeasure.getCommonCharacters(s, t, halfLength);
                            if (sourceMappingCharacters.size() >= theta) {
                                targetMappingCharacters = JaroMeasure.getCommonCharacters(t, s, halfLength);
                                transpositions = JaroMeasure.getTranspositions(sourceMappingCharacters,
                                        targetMappingCharacters);
                                if (transpositions != -1) {
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
        /*
         * System.out.println(lengthFilterCount + " = " + ((double)
         * lengthFilterCount) / (source.size() * target.size()));
         * 
         * System.out.println( characterFilterCount + " = " + ((double)
         * characterFilterCount) / (source.size() * target.size()));
         */

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
