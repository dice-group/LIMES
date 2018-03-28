package org.aksw.limes.core.gui;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.metric.Measure;
import org.aksw.limes.core.gui.model.metric.MetricParser;
import org.aksw.limes.core.gui.model.metric.Operator;
import org.aksw.limes.core.gui.model.metric.Output;
import org.aksw.limes.core.gui.model.metric.Property;
import org.aksw.limes.core.gui.util.SourceOrTarget;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.junit.Before;
import org.junit.Test;

import no.uib.cipr.matrix.sparse.LinkedSparseMatrix;

public class MetricParserTest {
	
	public Output testO1;
	public Output testO2;
	
	@Before
	public void initializeReference(){
		testO1 = new Output();
		testO2 = new Output();
		
		Operator or = new Operator("or");
		or.param1 = 0.7;
		or.param2 = 0.9;
		
		Measure jacc = new Measure("jaccard");
		Property x1 = new Property("x.rdfs:label", SourceOrTarget.SOURCE, false);
		Property y1 = new Property("y.rdfs:label", SourceOrTarget.TARGET, false);
		
		Measure cos = new Measure("cosine");
		Property x2 = new Property("x.rdfs:label", SourceOrTarget.SOURCE, false);
		Property y2 = new Property("y.rdfs:label", SourceOrTarget.TARGET, false);
		
		jacc.addChild(x1);
		jacc.addChild(y1);

		cos.addChild(x2);
		cos.addChild(y2);
		
		or.addChild(jacc);
		or.addChild(cos);
		
		testO1.addChild(or);

		Operator add = new Operator("add");
		add.param1 = 0.2;
		add.param2 = 0.8;
		
		Measure jaro = new Measure("jaro");
		Property a1 = new Property("a.foaf:name", SourceOrTarget.SOURCE, false);
		Property b1 = new Property("b.sg:publishedName", SourceOrTarget.TARGET, false);
		
		Measure overlap = new Measure("overlap");
		Property a2 = new Property("a.purl:identifier", SourceOrTarget.SOURCE, false);
		Property b2 = new Property("b.sg:hasPerson", SourceOrTarget.TARGET, false);
		
		jaro.addChild(a1);
		jaro.addChild(b1);

		overlap.addChild(a2);
		overlap.addChild(b2);
		
		add.addChild(jaro);
		add.addChild(overlap);
		
		testO2.addChild(add);
	}
	

	@Test
	public void testMetricParser(){
		Output o1 = MetricParser.parse("or(jaccard(x.rdfs:label,y.rdfs:label)|0.7,cosine(x.rdfs:label,y.rdfs:label)|0.9)", "x", new Config());
		assertEquals(testO1, o1);
	}
	
	@Test
	public void testWeighted(){
		Output o2 = MetricParser.parse("ADD(0.2* Jaro(a.foaf:name, b.sg:publishedName),0.8* Overlap(a.purl:identifier, b.sg:hasPerson))","a", new Config());
		assertEquals(testO2, o2);
	}

}
