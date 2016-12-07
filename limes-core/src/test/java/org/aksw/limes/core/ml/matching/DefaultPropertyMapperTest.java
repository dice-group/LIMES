package org.aksw.limes.core.ml.matching;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.query.ModelRegistry;
import org.aksw.limes.core.io.query.QueryModuleFactory;
import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.aksw.limes.core.measures.measure.MeasureProcessor;
import org.aksw.limes.core.ml.algorithm.matching.DefaultPropertyMapper;
import org.apache.jena.rdf.model.Model;
import org.junit.Before;
import org.junit.Test;

public class DefaultPropertyMapperTest {

	KBInfo sKB = new KBInfo();
	KBInfo tKB = new KBInfo();
	Model sModel;
	Model tModel;

	@Before
	public void init() {
		String base = "/datasets/";
		sKB.setEndpoint(LabelBasedClassMapperTest.class.getResource(base + "Persons1/person11.nt").getPath());
		sKB.setGraph(null);
		sKB.setPageSize(1000);
		sKB.setId("person11");
		tKB.setEndpoint(LabelBasedClassMapperTest.class.getResource(base + "Persons1/person12.nt").getPath());
		tKB.setGraph(null);
		tKB.setPageSize(1000);
		tKB.setId("person12");
		QueryModuleFactory.getQueryModule("nt", sKB);
		QueryModuleFactory.getQueryModule("nt", tKB);
		sModel = ModelRegistry.getInstance().getMap().get(sKB.getEndpoint());
		tModel = ModelRegistry.getInstance().getMap().get(tKB.getEndpoint());
	}

	@Test
	public void testGetPropertyMapping() {
		DefaultPropertyMapper mapper = new DefaultPropertyMapper(sModel, tModel);
		AMapping m = mapper.getPropertyMapping(sKB.getEndpoint(), tKB.getEndpoint(),
				"http://www.okkam.org/ontology_person1.owl#Person", "http://www.okkam.org/ontology_person2.owl#Person");
		AMapping correctMapping = MappingFactory.createDefaultMapping();
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#surname",
				"http://www.okkam.org/ontology_person2.owl#surname", 61.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#date_of_birth",
				"http://www.okkam.org/ontology_person2.owl#phone_numer", 5.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#given_name",
				"http://www.okkam.org/ontology_person2.owl#given_name", 141.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#age",
				"http://www.okkam.org/ontology_person2.owl#age", 24.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#phone_numer",
				"http://www.okkam.org/ontology_person2.owl#date_of_birth", 15.0);
		assertEquals(correctMapping, m);
	}
}
