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
package org.aksw.limes.core.io.query;

import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.KBInfo;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertTrue;

public class FileQueryModuleTest {

    @Test
    public void fillCacheTest() {
        HashMap<String, String> prefixes = new HashMap<>();
        prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        prefixes.put("dbpo", "http://dbpedia.org/ontology/");

        LinkedHashMap<String, Map<String, String>> functions = new LinkedHashMap<>();

        KBInfo kbinfo = new KBInfo(
                "DBpedia",                                                            //String id
//                "resources/ibuprofen.nt",                                            //String endpoint
                Thread.currentThread().getContextClassLoader().getResource("ibuprofen.nt").getPath(),
                null,                                                                //String graph
                "?x",                                                                //String var
                new ArrayList<String>(Arrays.asList("rdfs:label","dbpo:abstract")),                    //List<String> properties
                null,                    //List<String> optionlProperties
                new ArrayList<String>(Arrays.asList("?x rdf:type dbpo:Drug")),        //ArrayList<String> restrictions
                functions,                                                        //LinkedHashMap<String, Map<String, String>> functions
                prefixes,                                                            //Map<String, String> prefixes
                1000,                                                                //int pageSize
                "N3",                                                                //String type
                -1,                                                               //int minOffset
                -1                                                                //int maxoffset
        );
        FileQueryModule fqm = new FileQueryModule(kbinfo);
        HybridCache cache = new HybridCache();
        fqm.fillCache(cache);

        assertTrue(cache.size() > 0);
        for(String abs: cache.getAllInstances().get(0).getProperty("dbpo:abstract")){
            if(abs.contains("@en")){
                System.out.println(abs);
            }
        }
    }

}
