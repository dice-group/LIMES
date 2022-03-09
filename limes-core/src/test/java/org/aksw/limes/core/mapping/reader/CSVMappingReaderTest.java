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

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.reader.CSVMappingReader;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CSVMappingReaderTest {

    private static final Logger logger = LoggerFactory.getLogger(CSVMappingReaderTest.class);
    AMapping testMap = MappingFactory.createDefaultMapping();

    @Before
    public void init() {
        testMap.add("http://linkedgeodata.org/triplify/node2806760713",
                "http://linkedgeodata.org/triplify/node2478449224", 1.0d);
        testMap.add("http://linkedgeodata.org/triplify/node2806760713",
                "http://linkedgeodata.org/triplify/node1387111642", 1.0d);
        testMap.add("http://linkedgeodata.org/triplify/node2806760713",
                "http://linkedgeodata.org/triplify/node2406512815", 1.0d);
        testMap.setPredicate("http://linkedgeodata.org/ontology/near");
    }

    @Test
    public void testCsvMapping3Columns() {
        // String file = System.getProperty("user.dir") +
        // "/resources/mapping-3col-test.csv";
        String file = Thread.currentThread().getContextClassLoader().getResource("mapping-3col-test.csv").getPath();
        CSVMappingReader r = new CSVMappingReader(file, ",");
        AMapping readMap = r.read();
        assertTrue(readMap.equals(testMap));
    }

    @Test
    public void testCsvMapping2Columns() {
        // String file = System.getProperty("user.dir") +
        // "/resources/mapping-2col-test.csv";
        String file = Thread.currentThread().getContextClassLoader().getResource("mapping-2col-test.csv").getPath();
        CSVMappingReader r = new CSVMappingReader(file, ",");
        AMapping readMap = r.read();
        readMap.setPredicate("http://linkedgeodata.org/ontology/near");
        assertTrue(!readMap.equals(testMap));
    }

    @Test
    public void testCsvMapping3ColumnsWithSimilarity() {
        AMapping refMap = MappingFactory.createDefaultMapping();
        refMap.add("http://dbpedia.org/resource/Berlin", "http://linkedgeodata.org/triplify/node240109189", 0.999d);

        // String file = System.getProperty("user.dir") +
        // "/resources/mapping-3col-sim-test.csv";
        String file = Thread.currentThread().getContextClassLoader().getResource("mapping-3col-sim-test.csv").getPath();
        CSVMappingReader r = new CSVMappingReader(file, ",");
        AMapping readMap = r.read();

        assertTrue(readMap.equals(refMap));
    }

    @Test
    public void testCsvMappingBugFix() {
        final String[] datasetsList = { DataSetChooser.DataSets.DBLPACM.toString(),
                DataSetChooser.DataSets.ABTBUY.toString(), DataSetChooser.DataSets.DBLPSCHOLAR.toString(),
                DataSetChooser.DataSets.AMAZONGOOGLEPRODUCTS.toString(),
                DataSetChooser.DataSets.DBPLINKEDMDB.toString(), DataSetChooser.DataSets.DRUGS.toString() };
        EvaluationData evalData = null;
        try {
            for (String ds : datasetsList) {
                evalData = DataSetChooser.getData(ds);
                ACache source = evalData.getSourceCache();
                ACache target = evalData.getTargetCache();
                AMapping missing = MappingFactory.createDefaultMapping();
                evalData.getReferenceMapping().getMap().forEach((sourceURI, map2) -> {
                    map2.forEach((targetURI, value) -> {
                        if (source.getInstance(sourceURI) == null || target.getInstance(targetURI) == null) {

                            if (source.getInstance("<" + sourceURI + ">") == null
                                    || target.getInstance("<" + targetURI + ">") == null) {
                                missing.add(sourceURI, targetURI, 1.0);
                            }
                        }

                    });
                });
                assertEquals(0, missing.size());
            }
        } catch (Exception e) {
            logger.info("{}",e.getMessage());
        }
    }

}
