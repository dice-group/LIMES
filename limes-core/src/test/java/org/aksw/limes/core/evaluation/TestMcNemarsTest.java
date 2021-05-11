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
package org.aksw.limes.core.evaluation;

import org.aksw.limes.core.evaluation.qualititativeMeasures.McNemarsTest;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestMcNemarsTest {

    public AMapping a;
    public AMapping b;
    public AMapping ref;

    @Before
    public void prepareData() {
        a = MappingFactory.createDefaultMapping();
        b = MappingFactory.createDefaultMapping();
        ref = MappingFactory.createDefaultMapping();

        // 2 missing 1 wrong
        a.add("i1", "i1", 1.0);
        a.add("i2", "i2", 1.0);
        a.add("i3", "i3", 1.0);
        a.add("i5", "i5", 1.0);
        a.add("i7", "i7", 1.0);
        a.add("i8", "i10", 1.0);// wrong
        a.add("i9", "i9", 1.0);

        // 2 missing 4 wrong
        b.add("i1", "i4", 1.0);// wrong
        b.add("i2", "i2", 1.0);
        b.add("i3", "i", 1.0);// wrong
        b.add("i4", "i2", 1.0);// wrong
        b.add("i5", "i5", 1.0);
        b.add("i6", "i6", 1.0);
        b.add("i7", "i10", 1.0);// wrong

        ref.add("i1", "i1", 1.0);
        ref.add("i2", "i2", 1.0);
        ref.add("i3", "i3", 1.0);
        ref.add("i4", "i4", 1.0);
        ref.add("i5", "i5", 1.0);
        ref.add("i6", "i6", 1.0);
        ref.add("i7", "i7", 1.0);
        ref.add("i8", "i8", 1.0);
        ref.add("i9", "i9", 1.0);
    }

    @Test
    public void testGetSuccesses() {
        assertEquals(4, McNemarsTest.getSuccesses(a, b, ref));
    }

    @Test
    public void testGetFailures() {
        assertEquals(1, McNemarsTest.getSuccesses(b, a, ref));
    }

    @Test
    public void testCalculate() {
        assertEquals(0.000041145622817095884, McNemarsTest.calculate(86, 150), 0);
        assertEquals(0.055008833623266695, McNemarsTest.calculate(16, 6), 0);
    }

    @Test
    public void testCalculateMappings() {
        assertEquals(0.37109336952269756, McNemarsTest.calculate(a, b, ref), 0);
    }
}
