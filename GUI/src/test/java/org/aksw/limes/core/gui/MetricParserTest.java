package org.aksw.limes.core.gui;

import org.aksw.limes.core.gui.model.metric.Measure;
import org.aksw.limes.core.gui.model.metric.MetricParser;
import org.aksw.limes.core.gui.model.metric.Operator;
import org.aksw.limes.core.gui.model.metric.Output;
import org.aksw.limes.core.gui.model.metric.Property;
import org.aksw.limes.core.gui.model.metric.Property.Origin;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MetricParserTest {
	
	public Output testO1;
	
	@Before
	public void initializeReference(){
		testO1 = new Output();
		
		Operator or = new Operator("or");
		or.param1 = 0.7;
		or.param2 = 0.9;
		
		Measure jacc = new Measure("jaccard");
		Property x1 = new Property("x.rdfs:label", Origin.SOURCE);
		Property y1 = new Property("y.rdfs:label", Origin.TARGET);
		
		Measure cos = new Measure("cosine");
		Property x2 = new Property("x.rdfs:label", Origin.SOURCE);
		Property y2 = new Property("y.rdfs:label", Origin.TARGET);
		
		jacc.addChild(x1);
		jacc.addChild(y1);

		cos.addChild(x2);
		cos.addChild(y2);
		
		or.addChild(jacc);
		or.addChild(cos);
		
		testO1.addChild(or);
	}
	

	@Test
	public void testMetricParser(){
		Output o1 = MetricParser.parse("or(jaccard(x.rdfs:label,y.rdfs:label)|0.7,cosine(x.rdfs:label,y.rdfs:label)|0.9)", "x");
		assertEquals(testO1, o1);
	}
}
