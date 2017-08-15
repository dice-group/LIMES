package org.aksw.limes.core.ml.matching;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.query.ModelRegistry;
import org.aksw.limes.core.io.query.QueryModuleFactory;
import org.aksw.limes.core.ml.algorithm.matching.LabelBasedClassMapper;
import org.apache.jena.rdf.model.Model;
import org.junit.Before;
import org.junit.Test;

public class LabelBasedClassMapperTest {

	KBInfo sKB = new KBInfo();
	KBInfo tKB = new KBInfo();
    Model sModel;
    Model tModel;

    @Before
    public void init() {
    	String base = "/datasets/";
    	sKB.setEndpoint(LabelBasedClassMapperTest.class.getResource(base+"Persons1/person11.nt").getPath());
    	sKB.setGraph(null);
    	sKB.setPageSize(1000);
    	sKB.setId("person11");
		tKB.setEndpoint(LabelBasedClassMapperTest.class.getResource(base+"Persons1/person12.nt").getPath());
		tKB.setGraph(null);
		tKB.setPageSize(1000);
		tKB.setId("person12");
    	QueryModuleFactory.getQueryModule("nt", sKB);
    	QueryModuleFactory.getQueryModule("nt", tKB);
    	sModel = ModelRegistry.getInstance().getMap().get(sKB.getEndpoint());
    	tModel = ModelRegistry.getInstance().getMap().get(tKB.getEndpoint());
    }
    
    @Test
    public void testGetEntityMapping(){
		LabelBasedClassMapper mapper = new LabelBasedClassMapper(sModel, tModel);
		AMapping m = mapper.getEntityMapping(sKB.getEndpoint(), tKB.getEndpoint());
		AMapping correctMapping = MappingFactory.createDefaultMapping();
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#State","http://www.okkam.org/ontology_person2.owl#Person" ,0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#State","http://www.okkam.org/ontology_person2.owl#Address" ,0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#Person","http://www.okkam.org/ontology_person2.owl#Person",1.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#Person","http://www.okkam.org/ontology_person2.owl#Address",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#Address","http://www.okkam.org/ontology_person2.owl#Person",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#Address","http://www.okkam.org/ontology_person2.owl#Address",1.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#Suburb","http://www.okkam.org/ontology_person2.owl#Person",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#Suburb","http://www.okkam.org/ontology_person2.owl#Address",0.0);
		assertEquals(correctMapping.getMap(),m.getMap());
    }
}
