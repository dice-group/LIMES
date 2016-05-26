package org.aksw.limes.core.mapping.reader;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.reader.RDFMappingReader;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RDFMappingReaderTest {

    @Test
    public void csvMappingThreeColTester() {
        AMapping testMap = MappingFactory.createDefaultMapping();
        testMap.add("http://dbpedia.org/resource/Berlin", "http://linkedgeodata.org/triplify/node240109189", 1.0d);
        testMap.setPredicate("http://www.w3.org/2002/07/owl#sameAs");

        RDFMappingReader r = new RDFMappingReader("/resources/mapping-test.nt");
        AMapping readMap = r.read();

        assertTrue(readMap.equals(testMap));
    }

}
