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
package org.aksw.limes.core.measures.mapper;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.measure.AMeasure;
import org.aksw.limes.core.util.RandomStringGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Kevin Dreßler
 * @version %I%, %G%
 * @since 1.0
 */
public class MapperTest {
    public static AMapping bruteForce(Map<String, Set<String>> sourceMap, Map<String, Set<String>> targetMap,
                                      double threshold, AMeasure measure) {
        AMapping m = MappingFactory.createDefaultMapping();
        double sim;
        for (String s : sourceMap.keySet()) {
            for (String t : targetMap.keySet()) {
                sim = measure.getSimilarity(s, t);
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
}
