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

import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.MapperTest;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.measures.measure.string.JaroWinklerMeasure;
import org.junit.Test;

import com.google.common.base.Stopwatch;

/**
 * @author Kevin Dreßler
 * @since 1.0
 */
public class JaroWinklerMapperTest extends MapperTest {

    @Test
    public void testGetMapping() {
        double theta = 0.8d;
        int sourceSize = 1;
        int targetSize = 1;
        JaroWinklerMapper jwm = new JaroWinklerMapper();
//        RatcliffObershelpMapper rom = new RatcliffObershelpMapper();
        Map<String, Set<String>> s = generateRandomMap(sourceSize);
        Map<String, Set<String>> t = generateRandomMap(targetSize);
        Stopwatch stopWatch = Stopwatch.createStarted();
        AMapping m1 = jwm.getMapping(s, t, theta);
//        AMapping m1 = rom.getMapping(s, t, theta);
//        stopWatch.getElapsedTime();
        stopWatch.start();
        AMapping m2 = bruteForce(s, t, theta, new JaroWinklerMeasure());
        stopWatch.elapsed(TimeUnit.MILLISECONDS);
        assertTrue(MappingOperations.difference(m1, m2).size() == 0);
    }
}