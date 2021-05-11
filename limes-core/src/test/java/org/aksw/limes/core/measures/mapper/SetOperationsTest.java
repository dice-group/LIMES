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
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SetOperationsTest {

    @Test
    public void test() {
        AMapping a = MappingFactory.createDefaultMapping();
        AMapping b = MappingFactory.createDefaultMapping();
        a.add("c", "c", 0.5);
        a.add("a", "z", 0.5);
        a.add("a", "d", 0.5);

        b.add("a", "c", 0.5);
        b.add("a", "b", 0.7);
        b.add("b", "y", 0.7);
        assertTrue(MappingOperations.union(a, b) != null);
        assertTrue(MappingOperations.intersection(a, b).size() == 0);
        assertTrue(MappingOperations.difference(a, b).size() != 0);
        //assertTrue(MappingOperations.xor(a, b).size() != 0);
    }

}
