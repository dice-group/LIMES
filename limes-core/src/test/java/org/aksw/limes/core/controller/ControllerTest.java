package org.aksw.limes.core.controller;

import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.query.FileQueryModule;
import org.apache.commons.cli.CommandLine;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

/**
 * @author Kevin Dreßler
 */
public class ControllerTest {

    @Test
    public void test() {
        CommandLine cl = Controller.parseCommandLine(Arrays.asList("./resources/lgd-lgd.xml").toArray(new String[1]));
        Configuration config = Controller.getConfig(cl);
        config.setOutputFormat("N3");
        Mapping[] mappings = Controller.getMapping(config);
        Map<String, String> oldAccepted = getOldLIMESMapping(System.getProperty("user.dir") + "/resources/lgd_relaybox_near.nt");
        Map<String, String> oldReview = getOldLIMESMapping(System.getProperty("user.dir") + "/resources/lgd_relaybox_verynear.nt");
        for (String s : oldReview.keySet()) {
            assertTrue(mappings[0].contains(s,oldReview.get(s)));
        }
        for (String s : oldAccepted.keySet()) {
            assertTrue(mappings[1].contains(s,oldAccepted.get(s)));
        }
    }

    private static Map<String, String> getOldLIMESMapping(String filePath) {
        KBInfo kb = new KBInfo();
        HashMap<String, String> result = new HashMap<>();
        kb.setType("N3");
        kb.setVar("?x");
        kb.setPrefixes(new HashMap<>());
        kb.getPrefixes().put("owl", "http://www.w3.org/2002/07/owl#");
        kb.setRestrictions(new ArrayList<>(Arrays.asList("")));
        kb.setProperties(new ArrayList<>(Arrays.asList("owl:sameAs")));
        kb.setEndpoint(filePath);
        kb.afterPropertiesSet();
        FileQueryModule fqm = new FileQueryModule(kb);
        HybridCache hc = new HybridCache();
        fqm.fillCache(hc);
        for (String uri : hc.getAllUris()) {
            for (String value : hc.getInstance(uri).getProperty("owl:sameAs")) {
                result.put(uri, value);
            }
        }
        return result;
    }
}