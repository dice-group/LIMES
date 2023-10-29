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
package org.aksw.limes.core.io.cache;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser.DataSets;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MemoryCacheTest {

    public MemoryCache cache = new MemoryCache();


    @Before
    public void prepareData() {
        HybridCache tmp = (HybridCache) DataSetChooser.getData(DataSets.DRUGS).getSourceCache();
        tmp.getAllInstances().forEach(i -> cache.addInstance(i));
    }

    @Test
    public void testClone(){
        MemoryCache cloned = cache.clone();
        assertTrue(cloned.getClass() == cache.getClass());
        assertTrue(cloned != cache);
        assertTrue(cloned.getAllInstances() != cache.getAllInstances());
        assertEquals(cache, cloned);
    }
}
