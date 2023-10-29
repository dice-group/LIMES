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
package org.aksw.limes.core.measures.measure.resourcesets;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author Kevin Dreßler
 * @since 1.0
 */
public class SetJaccardMeasureTest {

    @Test
    public void testGetSimilarity() throws Exception {
        SetJaccardMeasure measure = new SetJaccardMeasure();
        Set<String> a = new HashSet<>();
        a.add("ball");
        a.add("soccer");
        a.add("socks");
        Set<String> b = new HashSet<>();
        b.add("ball");
        b.add("basket");
        b.add("socks");
        assertEquals(measure.getSimilarity(a, b), 0.5d, 0.d);
    }
}