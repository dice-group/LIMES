/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.measures.mapper.string;


import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.measure.string.JaroMeasure;
import org.aksw.limes.core.util.RandomStringGenerator;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        logger.info("{}","Brute force: " + (end - begin));
        logger.info("{}","Mapping size : " + (m1.getNumberofMappings()));

        begin = System.currentTimeMillis();
        AMapping m2 = run(sourceMap, targetMap, threshold);
        end = System.currentTimeMillis();
        logger.info("{}","Approach: " + (end - begin));
        logger.info("{}","Mapping size : " + (m2.getNumberofMappings()));
        logger.info("{}","Mapping size : " + (MappingOperations.difference(m1, m2)));
    }*/

    private void deduplicationTest(JaroMapper jm, int sourceSize, double threshold) {
        Map<String, Set<String>> sourceMap = generateRandomMap(sourceSize);

        long begin = System.currentTimeMillis();
        // Mapping m1 = bruteForce(sourceMap, sourceMap, threshold);
        long end = System.currentTimeMillis();
        // logger.info("{}","Brute force: " + (end - begin));
        // logger.info("{}","Mapping size : " + (m1.getNumberofMappings()));

        begin = System.currentTimeMillis();
        AMapping m2 = jm.runLenghtOnly(sourceMap, sourceMap, threshold);
        end = System.currentTimeMillis();
        logger.info("{}","Length-Only: " + (end - begin));
        logger.info("{}","Mapping size : " + (m2.getNumberofMappings()));
        // logger.info("{}","Mapping size : " + (SetOperations.difference(m1,
        // m2)));

        begin = System.currentTimeMillis();
        m2 = jm.runWithoutPrefixFilter(sourceMap, sourceMap, threshold);
        end = System.currentTimeMillis();
        logger.info("{}","Without Prefix Filtering: " + (end - begin));
        logger.info("{}","Mapping size : " + (m2.getNumberofMappings()));
        // logger.info("{}","Mapping size : " + (SetOperations.difference(m1,
        // m2)));

        begin = System.currentTimeMillis();
//	Mapping m3 = jm.run(sourceMap, sourceMap, threshold);
        end = System.currentTimeMillis();
        logger.info("{}","Full approach: " + (end - begin));
        logger.info("{}","Mapping size : " + (m2.getNumberofMappings()));
        // logger.info("{}","Mapping size : " + (SetOperations.difference(m1,
        // m3)));
    }

}
