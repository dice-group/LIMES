package org.aksw.limes.core.io.config.reader.xml;

import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;


/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Jan 15, 2016
 */
public class XMLConfigurationReaderTest {
    @Test
    public void testXmlReader() {
        HashMap<String, String> prefixes = new HashMap<>();
        prefixes.put("geos", "http://www.opengis.net/ont/geosparql#");
        prefixes.put("lgdo", "http://linkedgeodata.org/ontology/");
        prefixes.put("geom", "http://geovocab.org/geometry#");

        Map<String, Map<String, String>> functions = new HashMap<>();
        HashMap<String, String> f = new HashMap<String, String>();
        f.put("polygon", null);
        functions.put("geom:geometry/geos:asWKT", f);

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
                new HashMap<String, String>()        //MLAlgorithmParameters
        );

        XMLConfigurationReader c = new XMLConfigurationReader("/resources/lgd-lgd.xml");
        Configuration fileConf = c.read();
        System.out.println(fileConf);

        assertTrue(testConf.equals(fileConf));
    }

}
