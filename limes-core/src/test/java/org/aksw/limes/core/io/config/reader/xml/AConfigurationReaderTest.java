package org.aksw.limes.core.io.config.reader.xml;

import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

import java.util.*;

public class AConfigurationReaderTest {
    Map<String, String> prefixes;
    LinkedHashMap<String, Map<String, String>> functions;
    KBInfo sourceInfo, targetInfo;
    Configuration testConf;

    @Before
    public void init() {
        prefixes = new HashMap<>();
        prefixes.put("geos", "http://www.opengis.net/ont/geosparql#");
        prefixes.put("lgdo", "http://linkedgeodata.org/ontology/");
        prefixes.put("geom", "http://geovocab.org/geometry#");
        prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        prefixes.put("limes", "http://limes.sf.net/ontology/");

        functions = new LinkedHashMap<>();
        Map<String, String> f = new LinkedHashMap<>();
        f.put("polygon", null);
        functions.put("geom:geometry/geos:asWKT", f);

        sourceInfo = new KBInfo(
                "linkedgeodata",                                                  //String id
                "http://linkedgeodata.org/sparql",                                //String endpoint
                null,                                                             //String graph
                "?x",                                                             //String var
                new ArrayList<>(Arrays.asList("geom:geometry/geos:asWKT")), //List<String> properties
                new ArrayList<>(),                                          //List<String> optionalProperties
                new ArrayList<>(Arrays.asList("?x a lgdo:RelayBox")),       //ArrayList<String> restrictions
                functions,                                                        //LinkedHashMap<String, Map<String, String>> functions
                prefixes,                                                         //Map<String, String> prefixes
                2000,                                                             //int pageSize
                "sparql",                                                         //String type
                -1,                                                               //int minOffset
                -1                                                                //int maxoffset
        );

        targetInfo = new KBInfo(
                "linkedgeodata",                                                  //String id
                "http://linkedgeodata.org/sparql",                                //String endpoint
                null,                                                             //String graph
                "?y",                                                             //String var
                new ArrayList<>(Arrays.asList("geom:geometry/geos:asWKT")), //List<String> properties
                new ArrayList<>(),                                          //List<String> optionalProperties
                new ArrayList<>(Arrays.asList("?y a lgdo:RelayBox")),       //ArrayList<String> restrictions
                functions,                                                        //LinkedHashMap<String, Map<String, String>> functions
                prefixes,                                                         //Map<String, String> prefixes
                2000,                                                             //int pageSize
                "sparql",                                                         //String type
                -1,                                                               //int minOffset
                -1                                                                //int maxoffset
        );

        testConf = new Configuration();
        testConf.setPrefixes(prefixes);
        testConf.setSourceInfo(sourceInfo);
        testConf.setTargetInfo(targetInfo);

    }

    @Test
    public void testReplaceWithPrefixProp() {
        testConf.getSourceInfo().addProperty("http://www.opengis.net/ont/geosparql#test");
        AConfigurationReader.replaceURIsWithPrefixes(testConf.getSourceInfo());
        assertTrue(testConf.getSourceInfo().getProperties().contains("geos:test"));
    }

    @Test
    public void testReplaceWithPrefixOptionalProp() {
        testConf.getSourceInfo().addOptionalProperty("http://www.opengis.net/ont/geosparql#test");
        AConfigurationReader.replaceURIsWithPrefixes(testConf.getSourceInfo());
        assertTrue(testConf.getSourceInfo().getOptionalProperties().contains("geos:test"));
    }

    @Test
    public void testReplaceWithPrefixRestriction() {
        testConf.getSourceInfo().addRestriction("http://www.opengis.net/ont/geosparql#test");
        AConfigurationReader.replaceURIsWithPrefixes(testConf.getSourceInfo());
        assertTrue(testConf.getSourceInfo().getRestrictions().contains("geos:test"));
    }

    @Test
    public void testReplaceWithPrefixFunc() {
        LinkedHashMap<String, Map<String, String>> x = testConf.getSourceInfo().getFunctions();
        HashMap<String, String> y = new HashMap<>();
        y.put("http://www.opengis.net/ont/geosparql#test", "toLower");
        x.put("http://www.opengis.net/ont/geosparql#test", y);
        testConf.getSourceInfo().setFunctions(x);
        AConfigurationReader.replaceURIsWithPrefixes(testConf.getSourceInfo());
        assertTrue(testConf.getSourceInfo().getFunctions().containsKey("geos:test"));
    }

    @Test
    public void testReplaceWithPrefixPropError() {
        testConf.getPrefixes().remove("geos");
        testConf.getSourceInfo().addProperty("http://www.opengis.net/ont/geosparql#test");
        assertThrows(IllegalArgumentException.class, () -> AConfigurationReader.replaceURIsWithPrefixes(testConf.getSourceInfo()));
    }

    @Test
    public void testReplaceWithPrefixOptionalPropError() {
        testConf.getPrefixes().remove("geos");
        testConf.getSourceInfo().addOptionalProperty("http://www.opengis.net/ont/geosparql#test");
        assertThrows(IllegalArgumentException.class, () -> AConfigurationReader.replaceURIsWithPrefixes(testConf.getSourceInfo()));
    }

    @Test
    public void testReplaceWithPrefixRestrictionError() {
        testConf.getPrefixes().remove("geos");
        testConf.getSourceInfo().addRestriction("http://www.opengis.net/ont/geosparql#test");
        assertThrows(IllegalArgumentException.class, () -> AConfigurationReader.replaceURIsWithPrefixes(testConf.getSourceInfo()));
    }

    @Test
    public void testReplaceWithPrefixFuncError() {
        testConf.getPrefixes().remove("geos");
        LinkedHashMap<String, Map<String, String>> x = testConf.getSourceInfo().getFunctions();
        HashMap<String, String> y = new HashMap<>();
        y.put("http://www.opengis.net/ont/geosparql#test", "toLower");
        x.put("http://www.opengis.net/ont/geosparql#test", y);
        testConf.getSourceInfo().setFunctions(x);
        assertThrows(IllegalArgumentException.class, () -> AConfigurationReader.replaceURIsWithPrefixes(testConf.getSourceInfo()));
    }

}
