/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.io.config.writer;

import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;


public class RDFConfigurationWriterTest {
    private static final String SYSTEM_DIR = System.getProperty("user.dir");

    @Test
    public void testRDFConfigurationWriter() {
        Configuration conf = new Configuration();

        conf.addPrefix("geom", "http://geovocab.org/geometry#");
        conf.addPrefix("geos", "http://www.opengis.net/ont/geosparql#");
        conf.addPrefix("lgdo", "http://linkedgeodata.org/ontology/");
        //conf.addPrefix("http", "http");

        KBInfo src = new KBInfo();
        src.setId("linkedgeodata");
        src.setEndpoint("http://linkedgeodata.org/sparql");
        src.setVar("?x");
        src.setPageSize(2000);
        src.setRestrictions(
                new ArrayList<String>(
                        Arrays.asList(new String[]{"?x a lgdo:RelayBox"})
                )
        );
        src.setProperties(
                Arrays.asList(new String[]{"geom:geometry/geos:asWKT RENAME polygon"})
        );
        conf.setSourceInfo(src);

        KBInfo target = new KBInfo();
        target.setId("linkedgeodata");
        target.setEndpoint("http://linkedgeodata.org/sparql");
        target.setVar("?y");
        target.setPageSize(2000);
        target.setRestrictions(
                new ArrayList<String>(
                        Arrays.asList(new String[]{"?x a lgdo:RelayBox"})
                )
        );
        target.setProperties(
                Arrays.asList(new String[]{"geom:geometry/geos:asWKT RENAME polygon"})
        );
        conf.setTargetInfo(target);

        conf.setMetricExpression("geo_hausdorff(x.polygon, y.polygon)");

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

        String filePath = SYSTEM_DIR + "/resources/RDFWriterTestConfig.ttl";

        RDFConfigurationWriter writer = new RDFConfigurationWriter();
        try {
            writer.write(conf, filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(filePath);
        assertTrue(file.exists());
        file.delete(); // cleanup
    }

}
