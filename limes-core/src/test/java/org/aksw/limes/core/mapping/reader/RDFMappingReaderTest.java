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
package org.aksw.limes.core.mapping.reader;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.reader.RDFMappingReader;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RDFMappingReaderTest {

    @Test
    public void rdfMappingThreeColTester() {
        AMapping testMap = MappingFactory.createDefaultMapping();

        testMap.add("http://linkedgeodata.org/triplify/node2806760713", "http://linkedgeodata.org/triplify/node2478449224", 1.0d);
        testMap.add("http://linkedgeodata.org/triplify/node2806760713", "http://linkedgeodata.org/triplify/node1387111642", 1.0d);
        testMap.add("http://linkedgeodata.org/triplify/node2806760713", "http://linkedgeodata.org/triplify/node2406512815", 1.0d);
        testMap.setPredicate("http://linkedgeodata.org/ontology/near");

//        String file = System.getProperty("user.dir") + "/resources/mapping-test.nt";
        String file = Thread.currentThread().getContextClassLoader().getResource("mapping-test.nt").getPath();

        RDFMappingReader r = new RDFMappingReader(file);
        AMapping readMap = r.read();

        assertTrue(readMap.equals(testMap));
    }

}
