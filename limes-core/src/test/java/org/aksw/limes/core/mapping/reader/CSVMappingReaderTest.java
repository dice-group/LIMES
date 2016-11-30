package org.aksw.limes.core.mapping.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.reader.CSVMappingReader;
import org.junit.Before;
import org.junit.Test;

public class CSVMappingReaderTest {

    AMapping testMap = MappingFactory.createDefaultMapping();

    @Before
    public void init() {
	testMap.add("http://linkedgeodata.org/triplify/node2806760713", "http://linkedgeodata.org/triplify/node2478449224", 1.0d);
	testMap.add("http://linkedgeodata.org/triplify/node2806760713", "http://linkedgeodata.org/triplify/node1387111642", 1.0d);
	testMap.add("http://linkedgeodata.org/triplify/node2806760713", "http://linkedgeodata.org/triplify/node2406512815", 1.0d);
	testMap.setPredicate("http://linkedgeodata.org/ontology/near");
    }

    @Test
    public void csvMappingThreeColTester() {
	String file = System.getProperty("user.dir") + "/resources/mapping-3col-test.csv";
	CSVMappingReader r = new CSVMappingReader(file, ",");
	AMapping readMap = r.read();
	assertTrue(readMap.equals(testMap));
    }

    @Test
    public void csvMappingTwoColTester() {
	String file = System.getProperty("user.dir") + "/resources/mapping-2col-test.csv";
	CSVMappingReader r = new CSVMappingReader(file, ",");
	AMapping readMap = r.read();
	readMap.setPredicate("http://linkedgeodata.org/ontology/near");
	assertTrue(readMap.equals(testMap));
    }

    @Test
    public void csvMappingThreeColWithSimilarityTester() {
	AMapping refMap = MappingFactory.createDefaultMapping();
	refMap.add("http://dbpedia.org/resource/Berlin", "http://linkedgeodata.org/triplify/node240109189", 0.999d);

	String file = System.getProperty("user.dir") + "/resources/mapping-3col-sim-test.csv";
	CSVMappingReader r = new CSVMappingReader(file, ",");
	AMapping readMap = r.read();

	assertTrue(readMap.equals(refMap));
    }

    @Test
    public void csvMappingTestBugFix() {
	final String[] datasetsList = { DataSetChooser.DataSets.DBLPACM.toString(), DataSetChooser.DataSets.ABTBUY.toString(),
		DataSetChooser.DataSets.DBLPSCHOLAR.toString(), DataSetChooser.DataSets.AMAZONGOOGLEPRODUCTS.toString(),
		DataSetChooser.DataSets.DBPLINKEDMDB.toString(), DataSetChooser.DataSets.DRUGS.toString()};
	EvaluationData evalData = null;
	try {
	    for (String ds : datasetsList) {
		evalData = DataSetChooser.getData(ds);
		ACache source = evalData.getSourceCache();
		ACache target = evalData.getTargetCache();
		AMapping missing = MappingFactory.createDefaultMapping();
		evalData.getReferenceMapping().getMap().forEach((sourceURI, map2) -> {
		    map2.forEach((targetURI, value) -> {
			if (source.getInstance(sourceURI) == null || target.getInstance(targetURI) == null) {

			    if (source.getInstance("<" + sourceURI + ">") == null || target.getInstance("<" + targetURI + ">") == null) {
				missing.add(sourceURI, targetURI, 1.0);
			    }
			}

		    });
		});
		assertEquals(0, missing.size());
	    }
	} catch (Exception e) {
	    System.out.println(e.getMessage());
	}
    }

}
