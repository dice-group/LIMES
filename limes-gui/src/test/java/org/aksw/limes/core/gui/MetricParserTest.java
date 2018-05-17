package org.aksw.limes.core.gui;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.metric.Measure;
import org.aksw.limes.core.gui.model.metric.MetricParser;
import org.aksw.limes.core.gui.model.metric.Operator;
import org.aksw.limes.core.gui.model.metric.Output;
import org.aksw.limes.core.gui.model.metric.Property;
import org.aksw.limes.core.gui.util.SourceOrTarget;
import org.junit.Before;
import org.junit.Test;

public class MetricParserTest {

	public Output testO1;
	public Output testO2;

	@Before
	public void initializeReference() {
		this.testO1 = new Output();
		this.testO2 = new Output();

		final Operator or = new Operator("or");
		or.param1 = 0.7;
		or.param2 = 0.9;

		final Measure jacc = new Measure("jaccard");
		final Property x1 = new Property("x.rdfs:label", SourceOrTarget.SOURCE, false);
		final Property y1 = new Property("y.rdfs:label", SourceOrTarget.TARGET, false);

		final Measure cos = new Measure("cosine");
		final Property x2 = new Property("x.rdfs:label", SourceOrTarget.SOURCE, false);
		final Property y2 = new Property("y.rdfs:label", SourceOrTarget.TARGET, false);

		jacc.addChild(x1);
		jacc.addChild(y1);

		cos.addChild(x2);
		cos.addChild(y2);

		or.addChild(jacc);
		or.addChild(cos);

		this.testO1.addChild(or);

		final Operator add = new Operator("add");
		add.param1 = 0.2;
		add.param2 = 0.8;

		final Measure jaro = new Measure("jaro");
		final Property a1 = new Property("a.foaf:name", SourceOrTarget.SOURCE, false);
		final Property b1 = new Property("b.sg:publishedName", SourceOrTarget.TARGET, false);

		final Measure overlap = new Measure("overlap");
		final Property a2 = new Property("a.purl:identifier", SourceOrTarget.SOURCE, false);
		final Property b2 = new Property("b.sg:hasPerson", SourceOrTarget.TARGET, false);

		jaro.addChild(a1);
		jaro.addChild(b1);

		overlap.addChild(a2);
		overlap.addChild(b2);

		add.addChild(jaro);
		add.addChild(overlap);

		this.testO2.addChild(add);
	}

	@Test
	public void testMetricParser() {
		final Output o1 = MetricParser.parse(
				"or(jaccard(x.rdfs:label,y.rdfs:label)|0.7,cosine(x.rdfs:label,y.rdfs:label)|0.9)", "x", new Config());
		assertEquals(this.testO1, o1);
	}

	@Test
	public void testWeighted() {
		final Output o2 = MetricParser.parse(
				"ADD(0.2* Jaro(a.foaf:name, b.sg:publishedName),0.8* Overlap(a.purl:identifier, b.sg:hasPerson))", "a",
				new Config());
		assertEquals(this.testO2, o2);
	}

}
