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
package org.aksw.limes.core.measures.measure;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MeasureProcessorTest {

    @Test
    public void getMeasures() {
        ACache source = new MemoryCache();
        ACache target = new MemoryCache();
        source.addTriple("S1", "pub", "test");
        source.addTriple("S1", "conf", "conf one");
        source.addTriple("S2", "pub", "test2");
        source.addTriple("S2", "conf", "conf2");

        target.addTriple("S1", "pub", "test");
        target.addTriple("S1", "conf", "conf one");
        target.addTriple("S3", "pub", "test1");
        target.addTriple("S3", "conf", "conf three");

        assertTrue(MeasureProcessor.getSimilarity(source.getInstance("S1"), target.getInstance("S3"),
                "ADD(0.5*trigram(x.conf, y.conf),0.5*cosine(y.conf, x.conf))", 0.4, "?x", "?y") > 0.0);

        assertTrue(MeasureProcessor
                .getMeasures("AND(jaccard(x.authors,y.authors)|0.4278,overlap(x.authors,y.authors)|0.4278)").isEmpty() == false);


    }
}
