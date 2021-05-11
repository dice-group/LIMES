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
package org.aksw.limes.core.measures.mapper.resourcesets;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Kevin Dreßler
 * @since 1.0
 */
public class SetJaccardMapperTest {

    @Test
    public void testGetMapping() throws Exception {
        SetJaccardMapper mapper = new SetJaccardMapper();
        ACache s = new MemoryCache();
        s.addTriple("spielberg","movies","ET");
        s.addTriple("spielberg","movies","birds");
        s.addTriple("spielberg","movies","snails");
        ACache t = new MemoryCache();
        t.addTriple("spilberg","movies","ET");
        t.addTriple("spilberg","movies","birds");
        t.addTriple("spilberg","movies","rats");
        AMapping mapping1 = mapper.getMapping(s, t, "?x", "?y", "set_jaccard(x.movies, y.movies)", 0.4d);
        AMapping mapping2 = MappingFactory.createDefaultMapping();
        mapping2.add("spielberg", "spilberg", 0.5d);
        assertEquals(mapping2, mapping1);
    }
}