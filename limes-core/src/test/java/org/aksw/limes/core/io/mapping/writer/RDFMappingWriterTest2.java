package org.aksw.limes.core.io.mapping.writer;


import java.io.IOException;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.Before;
import org.junit.Test;


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
