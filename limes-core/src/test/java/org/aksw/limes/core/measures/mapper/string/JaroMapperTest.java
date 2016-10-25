package org.aksw.limes.core.measures.mapper.string;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.measure.string.JaroMeasure;
import org.aksw.limes.core.util.RandomStringGenerator;
import org.junit.Test;

public class JaroMapperTest extends JaroMapper {

    @Test
    public void test() {
        JaroMapper jm = new JaroMapper();
        deduplicationTest(jm, 1000, 1.0);
    }

    /**
     * Returns the set of characters contained in a string
     *
     * @param s
     *         Input String
     * @return Set of characters contained in it
     */
    protected Set<Character> getCharSet(String s) {
        Set<Character> result = new HashSet<>();
        char[] characters = s.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            result.add(characters[i]);
        }
        return result;
    }
   
    private AMapping bruteForce(Map<String, Set<String>> sourceMap, Map<String, Set<String>> targetMap,
                                double threshold) {
        JaroMeasure j = new JaroMeasure();
        AMapping m = MappingFactory.createDefaultMapping();
        double sim;
        for (String s : sourceMap.keySet()) {
            for (String t : targetMap.keySet()) {
                sim = j.getSimilarity(s, t);
                if (sim >= threshold) {
                    for (@SuppressWarnings("unused")
                    String sourceUris : sourceMap.get(s)) {
                        for (@SuppressWarnings("unused")
                        String targetUris : targetMap.get(t)) {
                            m.add(s, t, sim);
                        }
                    }
                }
            }
        }
        return m;
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

    
    /*private void test(int sourceSize, int targetSize, double threshold) {
        Map<String, Set<String>> sourceMap = generateRandomMap(sourceSize);
        Map<String, Set<String>> targetMap = generateRandomMap(targetSize);

        long begin = System.currentTimeMillis();
        AMapping m1 = bruteForce(sourceMap, targetMap, threshold);
        long end = System.currentTimeMillis();
        System.out.println("Brute force: " + (end - begin));
        System.out.println("Mapping size : " + (m1.getNumberofMappings()));

        begin = System.currentTimeMillis();
        AMapping m2 = run(sourceMap, targetMap, threshold);
        end = System.currentTimeMillis();
        System.out.println("Approach: " + (end - begin));
        System.out.println("Mapping size : " + (m2.getNumberofMappings()));
        System.out.println("Mapping size : " + (MappingOperations.difference(m1, m2)));
    }*/
    
    private void deduplicationTest(JaroMapper jm, int sourceSize, double threshold) {
        Map<String, Set<String>> sourceMap = generateRandomMap(sourceSize);

        long begin = System.currentTimeMillis();
        // Mapping m1 = bruteForce(sourceMap, sourceMap, threshold);
        long end = System.currentTimeMillis();
        // System.out.println("Brute force: " + (end - begin));
        // System.out.println("Mapping size : " + (m1.getNumberofMappings()));

        begin = System.currentTimeMillis();
        AMapping m2 = jm.runLenghtOnly(sourceMap, sourceMap, threshold);
        end = System.currentTimeMillis();
        System.out.println("Length-Only: " + (end - begin));
        System.out.println("Mapping size : " + (m2.getNumberofMappings()));
        // System.out.println("Mapping size : " + (SetOperations.difference(m1,
        // m2)));

        begin = System.currentTimeMillis();
        m2 = jm.runWithoutPrefixFilter(sourceMap, sourceMap, threshold);
        end = System.currentTimeMillis();
        System.out.println("Without Prefix Filtering: " + (end - begin));
        System.out.println("Mapping size : " + (m2.getNumberofMappings()));
        // System.out.println("Mapping size : " + (SetOperations.difference(m1,
        // m2)));

        begin = System.currentTimeMillis();
//	Mapping m3 = jm.run(sourceMap, sourceMap, threshold);
        end = System.currentTimeMillis();
        System.out.println("Full approach: " + (end - begin));
        System.out.println("Mapping size : " + (m2.getNumberofMappings()));
        // System.out.println("Mapping size : " + (SetOperations.difference(m1,
        // m3)));
    }

}
