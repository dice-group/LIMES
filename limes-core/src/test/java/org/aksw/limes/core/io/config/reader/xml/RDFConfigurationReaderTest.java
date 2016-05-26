package org.aksw.limes.core.io.config.reader.xml;

import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.config.reader.rdf.RDFConfigurationReader;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertTrue;


/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Jan 15, 2016
 */
public class RDFConfigurationReaderTest {
    Map<String, String> prefixes = new HashMap<>();
    Map<String, Map<String, String>> functions = new HashMap<>();

    @Before
    public void init() {
        prefixes.put("geos", "http://www.opengis.net/ont/geosparql#");
        prefixes.put("lgdo", "http://linkedgeodata.org/ontology/");
        prefixes.put("geom", "http://geovocab.org/geometry#");
        prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        prefixes.put("limes", "http://limes.sf.net/ontology/");
        prefixes = Collections.unmodifiableMap(prefixes);
        HashMap<String, String> f = new HashMap<String, String>();
        f.put("polygon", null);
        functions.put("geom:geometry/geos:asWKT", f);
        functions = Collections.unmodifiableMap(functions);
    }


    @Test
    public void testRDFReaderForMLAgorithm() {

        KBInfo sourceInfo = new KBInfo(
                "linkedgeodata",                                                    //String id
                "http://linkedgeodata.org/sparql",                                    //String endpoint
                null,                                                                //String graph
                "?x",                                                                //String var
                new ArrayList<String>(Arrays.asList("geom:geometry/geos:asWKT")),    //List<String> properties
                new ArrayList<String>(),                                            //List<String> optionalProperties
                new ArrayList<String>(Arrays.asList("?x a lgdo:RelayBox")),        //ArrayList<String> restrictions
                functions,                                                        //Map<String, Map<String, String>> functions
                prefixes,                                                            //Map<String, String> prefixes
                2000,                                                                //int pageSize
                "sparql"                                                            //String type
        );

        KBInfo targetInfo = new KBInfo(
                "linkedgeodata",                                                    //String id
                "http://linkedgeodata.org/sparql",                                    //String endpoint
                null,                                                                //String graph
                "?y",                                                                //String var
                new ArrayList<String>(Arrays.asList("geom:geometry/geos:asWKT")),    //List<String> properties
                new ArrayList<String>(),                                            //List<String> optionalProperties
                new ArrayList<String>(Arrays.asList("?y a lgdo:RelayBox")),        //ArrayList<String> restrictions
                functions,                                                        //Map<String, Map<String, String>> functions
                prefixes,                                                            //Map<String, String> prefixes
                2000,                                                                //int pageSize
                "sparql"                                                            //String type
        );

        Map<String, String> mlParameters = new HashMap<>();
        mlParameters.put("max execution time in minutes", "60");

        Configuration testConf = new Configuration(
                sourceInfo,
                targetInfo,
                new String(),                        //metricExpression
                "lgdo:near",                        //acceptanceRelation
                "lgdo:near",                        //verificationRelation
                0.9,                                //acceptanceThreshold
                "lgd_relaybox_verynear.nt",            //acceptanceFile
                0.5,                                //verificationThreshold
                "lgd_relaybox_near.nt",                //verificationFile
                prefixes,                            //prefixes
                "TAB",                                //outputFormat
                "Simple",                            //executionPlan
                2,                                    //granularity
                "wombat simple",                    //MLAlgorithmName
                mlParameters                        //MLAlgorithmParameters
        );

        RDFConfigurationReader c = new RDFConfigurationReader("/resources/lgd-lgd-ml.ttl");
        Configuration fileConf = c.read();
        assertTrue(testConf.equals(fileConf));
    }

    @Test
    public void testRDFReaderForMetric() {

        KBInfo sourceInfo = new KBInfo(
                "linkedgeodata",                                                    //String id
                "http://linkedgeodata.org/sparql",                                    //String endpoint
                null,                                                                //String graph
                "?x",                                                                //String var
                new ArrayList<String>(Arrays.asList("geom:geometry/geos:asWKT")),    //List<String> properties
                new ArrayList<String>(),                                            //List<String> optionalProperties
                new ArrayList<String>(Arrays.asList("?x a lgdo:RelayBox")),        //ArrayList<String> restrictions
                functions,                                                        //Map<String, Map<String, String>> functions
                prefixes,                                                            //Map<String, String> prefixes
                2000,                                                                //int pageSize
                "sparql"                                                            //String type
        );

        KBInfo targetInfo = new KBInfo(
                "linkedgeodata",                                                    //String id
                "http://linkedgeodata.org/sparql",                                    //String endpoint
                null,                                                                //String graph
                "?y",                                                                //String var
                new ArrayList<String>(Arrays.asList("geom:geometry/geos:asWKT")),    //List<String> properties
                new ArrayList<String>(),                                            //List<String> optionalProperties
                new ArrayList<String>(Arrays.asList("?y a lgdo:RelayBox")),        //ArrayList<String> restrictions
                functions,                                                        //Map<String, Map<String, String>> functions
                prefixes,                                                            //Map<String, String> prefixes
                2000,                                                                //int pageSize
                "sparql"                                                            //String type
        );

        Configuration testConf = new Configuration(
                sourceInfo,
                targetInfo,
                "hausdorff(x.polygon, y.polygon)",    //metricExpression
                "lgdo:near",                        //acceptanceRelation
                "lgdo:near",                        //verificationRelation
                0.9,                                //acceptanceThreshold
                "lgd_relaybox_verynear.nt",            //acceptanceFile
                0.5,                                //verificationThreshold
                "lgd_relaybox_near.nt",                //verificationFile
                prefixes,                            //prefixes
                "TAB",                                //outputFormat
                "Simple",                            //executionPlan
                2,                                    //granularity
                new String(),                        //MLAlgorithmName
                new HashMap<>()                        //MLAlgorithmParameters
        );
        RDFConfigurationReader c = new RDFConfigurationReader("/resources/lgd-lgd.ttl");
        Configuration fileConf = c.read();
        assertTrue(testConf.equals(fileConf));
    }


}
