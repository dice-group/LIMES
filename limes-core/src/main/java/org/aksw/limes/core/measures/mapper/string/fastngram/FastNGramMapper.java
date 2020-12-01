/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.aksw.limes.core.measures.mapper.string.fastngram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.aksw.limes.core.exceptions.InvalidThresholdException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.measure.string.QGramSimilarityMeasure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class FastNGramMapper extends AMapper {

    static Logger logger = LoggerFactory.getLogger(FastNGramMapper.class);
    static int q = 3;

    public static AMapping compute(Set<String> source, Set<String> target, int q, double threshold) {
        Index index = new Index(q);
        double kappa = (1 + threshold) / threshold;
        QGramSimilarityMeasure sim = new QGramSimilarityMeasure(q);
        ITokenizer tokenizer = new NGramTokenizer();
        Map<String, Set<String>> targetTokens = new HashMap<String, Set<String>>();
        AMapping result = MappingFactory.createDefaultMapping();
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
        AMapping tempMapping = MappingFactory.createDefaultMapping();
        for (String key : result.getMap().keySet()) {
            for (String value : result.getMap().get(key).keySet()) {
                double confidence = result.getConfidence(key, value);
                if (confidence >= threshold) {
                    tempMapping.add(key, value, confidence);
                }
            }
        }
        result = tempMapping;
        return result;
    }

    public String getName() {
        return "FastNGram";
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
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
            double threshold) {

        if (threshold <= 0) {
            throw new InvalidThresholdException(threshold);
        }
        String property1 = null, property2 = null;
        // get property labels
        Parser p = new Parser(expression, threshold);

        // get first property label
        String term1 = "?" + p.getLeftTerm();
        String term2 = "?" + p.getRightTerm();
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
            logger.error(MarkerFactory.getMarker("FATAL"), "Property 1 = " + property1 + ", Property 2 = " + property2);
            logger.error(MarkerFactory.getMarker("FATAL"), "Property values could not be read. Exiting");
            throw new RuntimeException();
        }

        if (!p.isAtomic()) {
            logger.error(MarkerFactory.getMarker("FATAL"), "Mappers can only deal with atomic expression");
            logger.error(MarkerFactory.getMarker("FATAL"),
                    "Expression " + expression + " was given to a mapper to process");
            throw new RuntimeException();
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
        AMapping m = FastNGramMapper.compute(sourceMap.keySet(), targetMap.keySet(), q, threshold);
        AMapping result = MappingFactory.createDefaultMapping();
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
