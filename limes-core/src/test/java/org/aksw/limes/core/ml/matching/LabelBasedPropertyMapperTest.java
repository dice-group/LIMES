package org.aksw.limes.core.ml.matching;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.query.ModelRegistry;
import org.aksw.limes.core.io.query.QueryModuleFactory;
import org.aksw.limes.core.ml.algorithm.matching.LabelBasedPropertyMapper;
import org.apache.jena.rdf.model.Model;
import org.junit.Before;
import org.junit.Test;

public class LabelBasedPropertyMapperTest {

	KBInfo sKB = new KBInfo();
	KBInfo tKB = new KBInfo();
    Model sModel;
    Model tModel;
    AMapping correctMapping;

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
		correctMapping = MappingFactory.createDefaultMapping();
		fillCorrectMapping();
    	
    }
    
    @Test
    public void testGetPropertyMapping(){
		LabelBasedPropertyMapper mapper = new LabelBasedPropertyMapper(sModel, tModel);
		AMapping m = mapper.getPropertyMapping(sKB.getEndpoint(), tKB.getEndpoint(), "http://www.okkam.org/ontology_person1.owl#Person", "http://www.okkam.org/ontology_person2.owl#Person");
		assertEquals(correctMapping.getMap(),m.getMap());
    }
    
    public void fillCorrectMapping(){
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#surname","http://www.okkam.org/ontology_person2.owl#soc_sec_id",0.095238097012043);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#surname","http://www.okkam.org/ontology_person2.owl#has_address",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#surname","http://www.okkam.org/ontology_person2.owl#surname",1.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#surname","http://www.okkam.org/ontology_person2.owl#phone_numer",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#surname","http://www.okkam.org/ontology_person2.owl#given_name",0.380952388048172);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#surname","http://www.okkam.org/ontology_person2.owl#date_of_birth",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#surname","http://www.okkam.org/ontology_person2.owl#age",0.1428571492433548);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#surname","http://www.w3.org/1999/02/22-rdf-syntax-ns#type",0.13333334028720856);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#date_of_birth","http://www.okkam.org/ontology_person2.owl#soc_sec_id",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#date_of_birth","http://www.okkam.org/ontology_person2.owl#has_address",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#date_of_birth","http://www.okkam.org/ontology_person2.owl#surname",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#date_of_birth","http://www.okkam.org/ontology_person2.owl#phone_numer",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#date_of_birth","http://www.okkam.org/ontology_person2.owl#given_name",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#date_of_birth","http://www.okkam.org/ontology_person2.owl#date_of_birth",1.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#date_of_birth","http://www.okkam.org/ontology_person2.owl#age",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#date_of_birth","http://www.w3.org/1999/02/22-rdf-syntax-ns#type",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#soc_sec_id","http://www.okkam.org/ontology_person2.owl#soc_sec_id",1.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#soc_sec_id","http://www.okkam.org/ontology_person2.owl#has_address",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#soc_sec_id","http://www.okkam.org/ontology_person2.owl#surname",0.095238097012043);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#soc_sec_id","http://www.okkam.org/ontology_person2.owl#phone_numer",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#soc_sec_id","http://www.okkam.org/ontology_person2.owl#given_name",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#soc_sec_id","http://www.okkam.org/ontology_person2.owl#date_of_birth",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#soc_sec_id","http://www.okkam.org/ontology_person2.owl#age",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#soc_sec_id","http://www.w3.org/1999/02/22-rdf-syntax-ns#type",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#given_name","http://www.okkam.org/ontology_person2.owl#soc_sec_id",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#given_name","http://www.okkam.org/ontology_person2.owl#has_address",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#given_name","http://www.okkam.org/ontology_person2.owl#surname",0.380952388048172);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#given_name","http://www.okkam.org/ontology_person2.owl#phone_numer",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#given_name","http://www.okkam.org/ontology_person2.owl#given_name",1.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#given_name","http://www.okkam.org/ontology_person2.owl#date_of_birth",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#given_name","http://www.okkam.org/ontology_person2.owl#age",0.11764705926179886);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#given_name","http://www.w3.org/1999/02/22-rdf-syntax-ns#type",0.1111111119389534);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#age","http://www.okkam.org/ontology_person2.owl#soc_sec_id",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#age","http://www.okkam.org/ontology_person2.owl#has_address",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#age","http://www.okkam.org/ontology_person2.owl#surname",0.1428571492433548);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#age","http://www.okkam.org/ontology_person2.owl#phone_numer",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#age","http://www.okkam.org/ontology_person2.owl#given_name",0.11764705926179886);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#age","http://www.okkam.org/ontology_person2.owl#date_of_birth",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#age","http://www.okkam.org/ontology_person2.owl#age",1.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#age","http://www.w3.org/1999/02/22-rdf-syntax-ns#type",0.1818181872367859);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#has_address","http://www.okkam.org/ontology_person2.owl#soc_sec_id",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#has_address","http://www.okkam.org/ontology_person2.owl#has_address",1.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#has_address","http://www.okkam.org/ontology_person2.owl#surname",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#has_address","http://www.okkam.org/ontology_person2.owl#phone_numer",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#has_address","http://www.okkam.org/ontology_person2.owl#given_name",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#has_address","http://www.okkam.org/ontology_person2.owl#date_of_birth",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#has_address","http://www.okkam.org/ontology_person2.owl#age",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#has_address","http://www.w3.org/1999/02/22-rdf-syntax-ns#type",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#phone_numer","http://www.okkam.org/ontology_person2.owl#soc_sec_id",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#phone_numer","http://www.okkam.org/ontology_person2.owl#has_address",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#phone_numer","http://www.okkam.org/ontology_person2.owl#surname",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#phone_numer","http://www.okkam.org/ontology_person2.owl#phone_numer",1.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#phone_numer","http://www.okkam.org/ontology_person2.owl#given_name",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#phone_numer","http://www.okkam.org/ontology_person2.owl#date_of_birth",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#phone_numer","http://www.okkam.org/ontology_person2.owl#age",0.0);
		correctMapping.add("http://www.okkam.org/ontology_person1.owl#phone_numer","http://www.w3.org/1999/02/22-rdf-syntax-ns#type",0.0);
		correctMapping.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.okkam.org/ontology_person2.owl#soc_sec_id",0.0);
		correctMapping.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.okkam.org/ontology_person2.owl#has_address",0.0);
		correctMapping.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.okkam.org/ontology_person2.owl#surname",0.13333334028720856);
		correctMapping.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.okkam.org/ontology_person2.owl#phone_numer",0.0);
		correctMapping.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.okkam.org/ontology_person2.owl#given_name",0.1111111119389534);
		correctMapping.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.okkam.org/ontology_person2.owl#date_of_birth",0.0);
		correctMapping.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.okkam.org/ontology_person2.owl#age",0.1818181872367859);
		correctMapping.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",1.0);
    }
    
    
}
