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


public class CSVMappingWriterTest {

    AMapping mapping = MappingFactory.createDefaultMapping();

    @Before
    public void init() {
        mapping.add("foo:a", "foo:b", 1d);
        mapping.add("aa", "bb", 1d);
        mapping.add("foo:aaaa", "foo:bb", 0.8d);
    }

    @Test
    public void testReadMappingFromRDF() throws IOException {

        String outputFile   = System.getProperty("user.home")+"/";
        outputFile += "test";

        (new CSVMappingWriter()).write(mapping, outputFile);
   

    }

}
