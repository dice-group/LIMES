package org.aksw.limes.core.mapping.reader;

import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.reader.CSVMappingReader;
import org.junit.Test;

public class CSVMappingReaderTest {

    @Test
    public void csvMappingThreeColTester() {
        AMapping testMap = MappingFactory.createDefaultMapping();
        
        testMap.add("http://linkedgeodata.org/triplify/node2806760713","http://linkedgeodata.org/triplify/node2478449224",1.0d);
        testMap.add("http://linkedgeodata.org/triplify/node2806760713","http://linkedgeodata.org/triplify/node1387111642",1.0d);
        testMap.add("http://linkedgeodata.org/triplify/node2806760713","http://linkedgeodata.org/triplify/node2406512815",1.0d);
        testMap.setPredicate("http://linkedgeodata.org/ontology/near");

        String file = System.getProperty("user.dir") + "/resources/mapping-3col-test.csv";
        CSVMappingReader r = new CSVMappingReader(file, ",");
        AMapping readMap = r.read();

        assertTrue(readMap.equals(testMap));
    }

    @Test
    public void csvMappingThreeColWithSimilarityTester() {
        AMapping testMap = MappingFactory.createDefaultMapping();
        testMap.add("http://dbpedia.org/resource/Berlin", "http://linkedgeodata.org/triplify/node240109189", 0.999d);
        
        String file = System.getProperty("user.dir") + "/resources/mapping-3col-sim-test.csv";
        CSVMappingReader r = new CSVMappingReader(file, ",");
        AMapping readMap = r.read();

        assertTrue(readMap.equals(testMap));
    }

    @Test
    public void csvMappingTwoColTester() {
        AMapping testMap = MappingFactory.createDefaultMapping();
        testMap.add("http://dbpedia.org/resource/Berlin", "http://linkedgeodata.org/triplify/node240109189", 1d);

        String file = System.getProperty("user.dir") + "/resources/mapping-2col-test.csv";
        CSVMappingReader r = new CSVMappingReader(file, ",");
        AMapping readMap = r.read();

        assertTrue(readMap.equals(testMap));
    }

}
