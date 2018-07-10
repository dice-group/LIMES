package org.aksw.limes.core.io.config.writer;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
import org.junit.Test;


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
