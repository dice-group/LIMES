package org.aksw.limes.core.measures.mapper.string;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.exceptions.InvalidThresholdException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.mapper.pointsets.PropertyFetcher;
import org.aksw.limes.core.measures.measure.string.TrigramMeasure;

/**
 * @author Peggy Lucke
 */
public class MongeElkanMapper extends AMapper {

    // Tokens are divide by space
    private String split = " ";
    // underlying trigram measure provided
    private TrigramMeasure trigram = new TrigramMeasure();

    // Token divide by another character as space
    public void setSplit(String split) {
        this.split = split;
    }

    /**
     * @param sourceMap
     *            Texts to compare with target
     * @param targetMap
     *            Texts to compare with source
     * @param threshold
     *            is the minimum similarity of the results
     * @return all results of source compare with target together with the
     *         similarity of them
     */
    public AMapping getMapping(Map<String, Set<String>> sourceMap, Map<String, Set<String>> targetMap,
            double threshold) {
        if (threshold <= 0) {
            throw new InvalidThresholdException(threshold);
        }
        Iterator<String> sit = sourceMap.keySet().iterator();
        double resultDouble;
        Map<String, Map<String, Double>> similarityBook = new HashMap<>();
        while (sit.hasNext()) {
            Iterator<String> tit = targetMap.keySet().iterator();
            String sourceString = sit.next();
            HashMap<String, Double> resultB = new HashMap<>();
            while (tit.hasNext()) {
                String targetString = tit.next();
                resultDouble = oneMongeElkan(sourceString.split(split), targetString.split(split), threshold);
                if (threshold <= resultDouble) {
                    resultB.put(targetString, resultDouble);
                }
            }
            similarityBook.put(sourceString, resultB);
        }

        AMapping result = MappingFactory.createDefaultMapping();
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
        double result = trigram.getSimilarity(tokenA, tokenB);
        return result;
    }

    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
            double threshold) {

        List<String> properties = PropertyFetcher.getProperties(expression, threshold);
        Map<String, Set<String>> sourceMap = getValueToUriMap(source, properties.get(0));
        Map<String, Set<String>> targetMap = getValueToUriMap(target, properties.get(1));
        return getMapping(sourceMap, targetMap, threshold);
    }

    @Override
    public String getName() {
        return "monge-elkan";
    }

    @Override
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

}