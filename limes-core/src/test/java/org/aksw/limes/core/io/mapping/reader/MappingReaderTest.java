package org.aksw.limes.core.io.mapping.reader;

import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.Before;
import org.junit.Test;

public class MappingReaderTest {
    AMapping refMap = MappingFactory.createDefaultMapping();
    
    @Before
    public void init() {
        refMap.add("http://linkedgeodata.org/triplify/node2806760713", "http://linkedgeodata.org/triplify/node2478449224",1d);
        refMap.add("http://linkedgeodata.org/triplify/node2806760713", "http://linkedgeodata.org/triplify/node1387111642",1d);
        refMap.add("http://linkedgeodata.org/triplify/node2806760713", "http://linkedgeodata.org/triplify/node2406512815",1d);
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
        assertTrue(map.equals(refMap));
    }
    


}
