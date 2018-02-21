package org.aksw.limes.core.io.config.reader.xml;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
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
    Map<String, Map<String, String>> functions = new HashMap<>();
    KBInfo sourceInfo, targetInfo;
    Configuration testConf;

    @Before
    public void init() {
        prefixes.put("geos", "http://www.opengis.net/ont/geosparql#");
        prefixes.put("lgdo", "http://linkedgeodata.org/ontology/");
        prefixes.put("geom", "http://geovocab.org/geometry#");
        prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        prefixes = Collections.unmodifiableMap(prefixes);
        HashMap<String, String> f = new HashMap<>();
        f.put("polygon", null);
        functions.put("geom:geometry/geos:asWKT", f);
        functions = Collections.unmodifiableMap(functions);
        
        sourceInfo = new KBInfo(
                "linkedgeodata",                                                  //String id
                "http://linkedgeodata.org/sparql",                                //String endpoint
                null,                                                             //String graph
                "?x",                                                             //String var
                new ArrayList<String>(Arrays.asList("geom:geometry/geos:asWKT")), //List<String> properties
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
                new ArrayList<String>(Arrays.asList("geom:geometry/geos:asWKT")), //List<String> properties
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
        XMLConfigurationReader c = new XMLConfigurationReader(file);
        Configuration fileConf = c.read();

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

        testConf.setMlAlgorithmName("wombat simple");
        testConf.setMlImplementationType(MLImplementationType.SUPERVISED_BATCH);
        testConf.setTrainingDataFile("trainingData.nt");
        testConf.setMlAlgorithmParameters(mlParameters);

//        String file = System.getProperty("user.dir") +"/resources/lgd-lgd-ml.xml";
        String file = Thread.currentThread().getContextClassLoader().getResource("lgd-lgd-ml.xml").getPath();
        XMLConfigurationReader c = new XMLConfigurationReader(file);
        Configuration fileConf = c.read();
        
        assertTrue(testConf.equals(fileConf));
    }

}
