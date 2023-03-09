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
package org.aksw.limes.core.measures.measure.topology.cobalt.diagonal;

import org.aksw.limes.core.measures.measure.AMeasure;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CobaltDiagonalDisjointMeasureTest {

    @Test
    public void testGetSimilarity() throws Exception {
        AMeasure measure = new CobaltDiagonalDisjointMeasure();
        assertEquals(1.0d, measure.getSimilarity("POLYGON ((-10 -10, 0 10, 10 10, 10 0, -10 -10))","POLYGON ((-20 -20, -30 -30, -25 -25, -20 -20))"), 0d);

        //False positive of cobalt
        assertEquals(1.0d, measure.getSimilarity("POLYGON ((-10 -10, 0 10, 10 10, 10 0, -10 -10))","POLYGON ((-20 -20, -10 0, 0 0, 0 -10, -20 -20))"), 0d);

        assertEquals(0.0d, measure.getSimilarity("POLYGON ((-10 -10, 0 10, 10 10, 10 0, -10 -10))","POLYGON ((-10 -10, 0 10, 10 10, 10 0, -10 -10))"), 0d);

    }
}