package org.aksw.limes.core.io.query;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.KBInfo;
import org.junit.Before;
import org.junit.Test;

public class SparqlQueryModuleTest {
    
    HashMap<String, String> prefixes;
    Map<String, Map<String, String>> functions;
    KBInfo kbInfo;
    
    @Before
    public void init() {
        prefixes = new HashMap<>();
        prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        prefixes.put("dbpo", "http://dbpedia.org/ontology/");
        
        functions = new HashMap<>();
        HashMap<String, String> f = new HashMap<>();
        f.put("label", null);
        functions.put("rdfs:label", f);
        f.put("comment", null);
        functions.put("rdfs:comment", f);
        
        kbInfo = new KBInfo(
                "DBpedia",                                                       //String id
                "http://dbpedia.org/sparql",                                     //String endpoint
                null,                                                            //String graph
                "?x",                                                            //String var
                new ArrayList<String>(Arrays.asList("rdfs:label")),              //List<String> properties
                null,                                                            //List<String> optionlProperties
                new ArrayList<String>(Arrays.asList("?x rdf:type dbpo:Drug")),   //ArrayList<String> restrictions
                functions,                                                       //Map<String, Map<String, String>> functions
                prefixes,                                                        //Map<String, String> prefixes
                1000,                                                            //int pageSize
                "sparql",                                                         //String type
                -1,                                                               //int minOffset
                -1                                                                //int maxoffset
        );
    }

    @Test
    public void fillCacheTest() {
        SparqlQueryModule sqm = new SparqlQueryModule(kbInfo);
        ACache cache = new HybridCache();
        sqm.fillCache(cache);
        assertTrue(cache.size() > 0);
    }
    
    
    @Test
    public void optionalPropertyTest() {
        kbInfo.setOptionalProperties(Arrays.asList("rdfs:comment"));
        SparqlQueryModule sqm = new SparqlQueryModule(kbInfo);
        ACache cache = new HybridCache();
        sqm.fillCache(cache);
        assertTrue(cache.size() > 0);
    }

}
