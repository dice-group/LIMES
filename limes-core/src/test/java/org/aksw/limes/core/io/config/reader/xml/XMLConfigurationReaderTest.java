package org.aksw.limes.core.io.config.reader.xml;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.preprocessing.functions.Concat;
import org.aksw.limes.core.io.preprocessing.functions.Split;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jan 15, 2016
 */
public class XMLConfigurationReaderTest {
    
    Map<String, String> prefixes = new HashMap<>();
    LinkedHashMap<String, Map<String, String>> functions = new LinkedHashMap<>();
    ArrayList<String> properties;
    KBInfo sourceInfo, targetInfo;
    Configuration testConf;

    @Before
    public void init() {
        prefixes.put("geos", "http://www.opengis.net/ont/geosparql#");
        prefixes.put("lgdo", "http://linkedgeodata.org/ontology/");
        prefixes.put("geom", "http://geovocab.org/geometry#");
        prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        HashMap<String, String> f = new HashMap<>();
        f.put("polygon", null);
        functions.put("geom:geometry/geos:asWKT", f);
        properties = new ArrayList<String>(Arrays.asList("geom:geometry/geos:asWKT"));
        
        sourceInfo = new KBInfo(
                "linkedgeodata",                                                  //String id
                "http://linkedgeodata.org/sparql",                                //String endpoint
                null,                                                             //String graph
                "?x",                                                             //String var
                properties, //List<String> properties
                new ArrayList<String>(),                                          //List<String> optionalProperties
                new ArrayList<String>(Arrays.asList("?x a lgdo:RelayBox")),       //ArrayList<String> restrictions
                functions,                                                        //Map<String, Map<String, String>> functions
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
                properties, //List<String> properties
                new ArrayList<String>(),                                          //List<String> optionalProperties
                new ArrayList<String>(Arrays.asList("?y a lgdo:RelayBox")),       //ArrayList<String> restrictions
                functions,                                                        //Map<String, Map<String, String>> functions
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
        testConf.setAcceptanceRelation("lgdo:near");       
        testConf.setVerificationRelation("lgdo:near");
        testConf.setAcceptanceThreshold(0.9); 
        testConf.setAcceptanceFile("lgd_relaybox_verynear.nt");
        testConf.setVerificationThreshold(0.5);
        testConf.setVerificationFile("lgd_relaybox_near.nt");
        testConf.setOutputFormat("TAB");
    }
    
    
    @Test
    public void testXmlReaderForMetric() {
        testConf.setMetricExpression("geo_hausdorff(x.polygon, y.polygon)");
        testConf.setExecutionRewriter("default");
        testConf.setExecutionPlanner("default");
        testConf.setExecutionEngine("default");

//        String file= System.getProperty("user.dir") + "/resources/lgd-lgd.xml";
        String file = Thread.currentThread().getContextClassLoader().getResource("lgd-lgd.xml").getPath();
        System.out.println(file);
        XMLConfigurationReader c = new XMLConfigurationReader(file);
        Configuration fileConf = c.read();

        assertEquals(testConf, fileConf);
        assertTrue(testConf.equals(fileConf));
    }
    
    @Test
    public void testXmlReaderForOptionalProperties() {
        testConf.setMetricExpression("geo_hausdorff(x.polygon, y.polygon)");
        testConf.setExecutionRewriter("default");
        testConf.setExecutionPlanner("default");
        testConf.setExecutionEngine("default");
        
        sourceInfo.setOptionalProperties(Arrays.asList("rdfs:label"));
        targetInfo.setOptionalProperties(Arrays.asList("rdfs:label"));

        String file= System.getProperty("user.dir") + "/resources/lgd-lgd-optional-properties.xml";
        System.out.println(file);
        XMLConfigurationReader c = new XMLConfigurationReader(file);
        Configuration fileConf = c.read();
        assertTrue(testConf.equals(fileConf));
    }
    
    @Test
    public void testXmlReaderMLAlgorithm() {

        List<LearningParameter> mlParameters = new ArrayList<>();
        LearningParameter lp = new LearningParameter();
        lp.setName("max execution time in minutes");
        lp.setValue(60);
        mlParameters.add(lp);
        lp = new LearningParameter();
        lp.setName("beta");
        lp.setValue(5.0);
        mlParameters.add(lp);

        testConf.setMlAlgorithmName("wombat simple");
        testConf.setMlImplementationType(MLImplementationType.UNSUPERVISED);
        testConf.setMlAlgorithmParameters(mlParameters);

//        String file = System.getProperty("user.dir") +"/resources/lgd-lgd-ml.xml";
        String file = Thread.currentThread().getContextClassLoader().getResource("lgd-lgd-ml.xml").getPath();
        System.out.println(file);
        XMLConfigurationReader c = new XMLConfigurationReader(file);
        Configuration fileConf = c.read();
        
        assertTrue(testConf.equals(fileConf));
    }
    
    @Test
    public void testNAryFunctions() {
        testConf.setMetricExpression("geo_hausdorff(x.polygon, y.polygon)");
        testConf.setExecutionRewriter("default");
        testConf.setExecutionPlanner("default");
        testConf.setExecutionEngine("default");
        prefixes.put("geopos","http://www.w3.org/2003/01/geo/wgs84_pos#");
        properties.add("geopos:lat");
        properties.add("geopos:long");

        LinkedHashMap<String, Map<String, String>> sourceFunctions = new LinkedHashMap<>();
        LinkedHashMap<String, Map<String, String>> targetFunctions = new LinkedHashMap<>();
        HashMap<String, String> f = new HashMap<>();
        f.put("polygon", null);
        sourceFunctions.put("geom:geometry/geos:asWKT", f);
        targetFunctions.put("geom:geometry/geos:asWKT", f);
        HashMap<String, String> f3 = new HashMap<>();
        f3.put("latlong", "concat(geopos:lat, geopos:long, "+Concat.GLUE_KEYWORD+",)");
        sourceFunctions.put("latlong", f3);
        HashMap<String, String> f4 = new HashMap<>();
        f4.put("poly1,poly2", "split(polygon, "+ Split.SPLIT_CHAR_KEYWORD +"\".\")");
        targetFunctions.put("poly1,poly2", f4);


        testConf.setPrefixes(prefixes);
        sourceInfo.setProperties(properties);
        sourceInfo.setFunctions(sourceFunctions);
        targetInfo.setProperties(properties);
        targetInfo.setFunctions(targetFunctions);
        testConf.setSourceInfo(sourceInfo);
        testConf.setTargetInfo(targetInfo);

        String file = Thread.currentThread().getContextClassLoader().getResource("naryfunctionstest.xml").getPath();
        XMLConfigurationReader c = new XMLConfigurationReader(file);
        Configuration fileConf = c.read();
        
        assertEquals(testConf, fileConf);
    }

    
    @Test
    public void test1() {
        //optimization time = -1000 -> 0
        //no selectivity from file -> 1.0
        //
        String filename = Thread.currentThread().getContextClassLoader().getResource("lgd-lgd.xml").getPath(); 
        System.out.println(filename);
        XMLConfigurationReader reader = new XMLConfigurationReader(filename);
        Configuration config = reader.read();
        
        
        
        assertTrue(config.getOptimizationTime() == 0);
        assertTrue(config.getExpectedSelectivity() == 1.0);
    }
    
    @Test
    public void test2() {
        String filename = Thread.currentThread().getContextClassLoader().getResource("lgd-lgd2.xml").getPath(); 
        System.out.println(filename);
        XMLConfigurationReader reader = new XMLConfigurationReader(filename);
        Configuration config = reader.read();
        
        assertTrue(config.getOptimizationTime() == 1000);
        assertTrue(config.getExpectedSelectivity() == 0.65);
    }

}
