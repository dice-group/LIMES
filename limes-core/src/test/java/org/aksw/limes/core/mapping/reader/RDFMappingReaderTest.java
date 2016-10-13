package org.aksw.limes.core.mapping.reader;

import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.reader.RDFMappingReader;
import org.junit.Test;

public class RDFMappingReaderTest {

    @Test
    public void rdfMappingThreeColTester() {
        AMapping testMap = MappingFactory.createDefaultMapping();
        
        testMap.add("http://linkedgeodata.org/triplify/node2806760713","http://linkedgeodata.org/triplify/node2478449224",1.0d);
        testMap.add("http://linkedgeodata.org/triplify/node2806760713","http://linkedgeodata.org/triplify/node1387111642",1.0d);
        testMap.add("http://linkedgeodata.org/triplify/node2806760713","http://linkedgeodata.org/triplify/node2406512815",1.0d);
        testMap.setPredicate("http://linkedgeodata.org/ontology/near");

        String file = System.getProperty("user.dir") + "/resources/mapping-test.nt";
        RDFMappingReader r = new RDFMappingReader(file);
        AMapping readMap = r.read();

        assertTrue(readMap.equals(testMap));
    }

}
