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
package org.aksw.limes.core.io.mapping;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MappingTest {

    @Test
    public void testMappingEquals() {
        AMapping gold = MappingFactory.createDefaultMapping();
        gold.add("Potter", "Harry", 0.7);
        gold.add("Granger", "Hermione", 0.9);
        HashMap<String, Double> goldMap = new HashMap<String, Double>();
        goldMap.put("Ron", 0.4);
        goldMap.put("Fred", 0.4);
        goldMap.put("George", 0.4);
        gold.add("Weasley", goldMap);
        AMapping test = MappingFactory.createDefaultMapping();
        test.add("Granger","Hermione",  0.9);
        test.add("Potter","Harry", 0.7);
        HashMap<String,Double> testMap = new HashMap<String,Double>();
        testMap.put("Ron", 0.4);
        testMap.put("Fred", 0.4);
        testMap.put("George", 0.4);
        test.add("Weasley", testMap);
        assertEquals(gold,test);
        test.add("Potter","Harry", 0.9);
        assertNotEquals(gold,test);
    }
}
