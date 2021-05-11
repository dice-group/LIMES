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
package org.aksw.limes.core.io.mapping.writer;


import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;


public class RDFMappingWriterTest2 {

    AMapping mapping = MappingFactory.createDefaultMapping();

    @Before
    public void init() {
        mapping.add("http://example.test/s1", "http://example.test/o1", 1d);
        mapping.add("http://example.test/s2", "http://example.test/o2", 1d);
        mapping.add("http://example.test/s3", "http://example.test/o3", 1d);
    }

    @Test
    public void testReadMappingFromRDF() throws IOException {

        String outputFile = System.getProperty("user.home")+"/";
        outputFile += "test.rdf";

        (new RDFMappingWriter()).write(mapping, outputFile);

    }

}
