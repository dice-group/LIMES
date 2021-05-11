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
package org.aksw.limes.core.io.mapping.reader;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MappingReaderTest {
    AMapping refMap = MappingFactory.createDefaultMapping();

    @Before
    public void init() {
        refMap.add("http://linkedgeodata.org/triplify/node2806760713", "http://linkedgeodata.org/triplify/node2478449224", 1d);
        refMap.add("http://linkedgeodata.org/triplify/node2806760713", "http://linkedgeodata.org/triplify/node1387111642", 1d);
        refMap.add("http://linkedgeodata.org/triplify/node2806760713", "http://linkedgeodata.org/triplify/node2406512815", 1d);
        refMap.setPredicate("http://linkedgeodata.org/ontology/near");
    }

    @Test
    public void testReadMappingFromRDF() {
//        String file = System.getProperty("user.dir") + "/resources/mapping-test.nt";
        String file = Thread.currentThread().getContextClassLoader().getResource("mapping-test.nt").getPath();
        RDFMappingReader r = new RDFMappingReader(file);
        AMapping map = r.read();
        assertTrue(map.equals(refMap));
    }

    @Test
    public void testReadMappingFromCSV() {
//        String file = System.getProperty("user.dir") + "/resources/mapping-3col-test.csv";
        String file = Thread.currentThread().getContextClassLoader().getResource("mapping-3col-test.csv").getPath();
        CSVMappingReader r = new CSVMappingReader(file);
        AMapping map = r.read();
        assertTrue(map.equals(refMap));
    }

    @Test
    public void testReadMappingFrom2ColumnsCSV() {
//        String file = System.getProperty("user.dir") + "/resources/mapping-2col-test.csv";
        String file = Thread.currentThread().getContextClassLoader().getResource("mapping-2col-test.csv").getPath();
        CSVMappingReader r = new CSVMappingReader(file);
        AMapping map = r.read();
        map.setPredicate("http://linkedgeodata.org/ontology/near");
        assertTrue(!map.equals(refMap));
    }


}
