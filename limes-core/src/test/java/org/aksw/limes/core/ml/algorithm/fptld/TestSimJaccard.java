package org.aksw.limes.core.ml.algorithm.fptld;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.fptld.fitness.SimFuzzyJaccard;
import org.junit.Before;
import org.junit.Test;

public class TestSimJaccard {

	public AMapping m1;
	public AMapping m2;
	public static final double gold = BigDecimal.valueOf(1.4)
			.divide(BigDecimal.valueOf(5.1), MappingOperations.SCALE, RoundingMode.HALF_UP).doubleValue();

	@Before
	public void prepareData() {
		m1 = MappingFactory.createDefaultMapping();
		m1.add("i1", "i1", 0.8);
		m1.add("i2", "i2", 0.9);
		m1.add("i3", "i3", 0.5);
		m1.add("i4", "i4", 0.2);
		m1.add("i5", "i5", 0.4);
		m1.add("i6", "i6", 0.3);
		m1.add("i7", "i7", 0.6);

		m2 = MappingFactory.createDefaultMapping();
		m2.add("i2", "i2", 0.1);
		m2.add("i3", "i3", 0.9);
		m2.add("i4", "i4", 0.4);
		m2.add("i5", "i5", 0.5);
		m2.add("i6", "i6", 0.2);
		m2.add("i8", "i8", 0.7);
	}

	@Test
	public void testGetSimilarity() {
		double test = SimFuzzyJaccard.INSTANCE.getSimilarity(m1, m2);
		assertEquals(gold, test, 0.0);
	}

}
