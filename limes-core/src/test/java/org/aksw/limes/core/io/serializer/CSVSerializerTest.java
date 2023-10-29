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
package org.aksw.limes.core.io.serializer;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

import java.util.HashMap;
import java.util.Map.Entry;


public class CSVSerializerTest {

    public static void main(String args[]) {
        AMapping m = MappingFactory.createDefaultMapping();
        m.add("foo:a", "foo:b", 1d);
        m.add("aa", "bb", 1d);
        m.add("foo:aaaa", "foo:bb", 0.8d);

        ISerializer serial = SerializerFactory.createSerializer("csv");

        String fileName = System.getProperty("user.home") + "/";
        fileName += "test";
        serial.open(fileName);
        String predicate = "foo:sameAs";
        HashMap<String, String> prefixes = new HashMap<String, String>();
        prefixes.put("foo", "http://example.com/");
        serial.setPrefixes(prefixes);
        for (String uri1 : m.getMap().keySet()) {
            for (Entry<String, Double> e : m.getMap().get(uri1).entrySet()) {
                serial.printStatement(uri1, predicate, e.getKey(), e.getValue());
            }
        }
        serial.close();
    }

}
