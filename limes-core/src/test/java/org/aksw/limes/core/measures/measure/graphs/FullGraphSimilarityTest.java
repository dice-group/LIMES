package org.aksw.limes.core.measures.measure.graphs;


import org.aksw.limes.core.controller.Controller;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.mapping.AMapping;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class FullGraphSimilarityTest {
    @Test
    public void execute() throws Exception {

        Configuration conf = new Configuration();

        conf.addPrefix("geom", "http://geovocab.org/geometry#");
        conf.addPrefix("geos", "http://www.opengis.net/ont/geosparql#");
        conf.addPrefix("lgdo", "http://linkedgeodata.org/ontology/");
        conf.addPrefix("http", "http");

        KBInfo src = new KBInfo();
        src.setPrefixes(conf.getPrefixes());
        src.setId("linkedgeodata");
        src.setEndpoint("http://linkedgeodata.org/sparql");
        src.setVar("?x");
        src.setPageSize(2000);
        src.setRestrictions(
                new ArrayList<String>(
                        Arrays.asList(new String[]{"?x a lgdo:RelayBox"})
                )
        );
        XMLConfigurationReader.processProperty(src, "geom:geometry/geos:asWKT RENAME polygon");

        conf.setSourceInfo(src);

        KBInfo target = new KBInfo();
        target.setPrefixes(conf.getPrefixes());
        target.setId("linkedgeodata");
        target.setEndpoint("http://linkedgeodata.org/sparql");
        target.setVar("?y");
        target.setPageSize(2000);
        target.setRestrictions(
                new ArrayList<String>(
                        Arrays.asList(new String[]{"?x a lgdo:RelayBox"})
                )
        );
        XMLConfigurationReader.processProperty(target, "geom:geometry/geos:asWKT RENAME polygon");
        conf.setTargetInfo(target);

        conf.setMetricExpression("graph_wls(x,y)");

        conf.setAcceptanceFile("lgd_relaybox_verynear.nt");
        conf.setAcceptanceThreshold(0.9);
        conf.setAcceptanceRelation("lgdo:near");

        conf.setVerificationFile("lgd_relaybox_near.nt");
        conf.setVerificationThreshold(0.5);
        conf.setVerificationRelation("lgdo:near");

        conf.setExecutionEngine("default");
        conf.setExecutionPlanner("default");
        conf.setExecutionRewriter("default");

        conf.setOutputFormat("TAB");

        AMapping mapping = Controller.getMapping(conf).getAcceptanceMapping();
        //ILIMESRunner run = new InMemLIMESRunner();

        //System.out.println(
          //      run.execute(conf).queryResult()
        //);

    }

}
