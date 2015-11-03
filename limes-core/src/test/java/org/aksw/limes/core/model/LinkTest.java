package org.aksw.limes.core.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class LinkTest {

	@Test
	public void testObjectProperty() {
		
		Resource s = getSubject();
		
		// testing link creation with object property
		Property p1 = OWL.sameAs;
		Resource o1 = getObject();
		Link link1 = new Link(s, p1, o1);
		assertEquals("6b31752e0ecafde715a074944b0c7eddff292e68", link1.getHash());
		
	}
	
	@Test
	public void testDatatypeProperty() {
		
		Resource s = getSubject();
	
		// testing link creation with datatype property
		Property p2 = ResourceFactory.createProperty("http://dbpedia.org/property/populationTotal");
		RDFNode o2 = ResourceFactory.createTypedLiteral("549792", XSDDatatype.XSDinteger);
		Link link2 = new Link(s, p2, o2);
		assertEquals("124b00c45f63fc3b6fc82ae4a0d7c1a87f87267e", link2.getHash());
		
	}
	
	@Test
	public void testLinkEquality() {
		
		Resource s1 = getSubject();
		Resource s2 = getSubject();
		
		Property p1 = OWL.sameAs;
		Property p2 = ResourceFactory.createProperty("http://www.w3.org/2002/07/owl#sameAs");
		
		Resource o1 = getObject();
		Resource o2 = getObject();
		
		Link link1 = new Link(s1, p1, o1);
		Link link2 = new Link(s2, p2, o2);
		
		assertEquals(link1, link2);
		
	}

	private Resource getSubject() {
		return ResourceFactory.createResource("http://dbpedia.org/resource/Leipzig");
	}

	private Resource getObject() {
		return ResourceFactory.createResource("http://www.wikidata.org/entity/Q2079");
	}

}
