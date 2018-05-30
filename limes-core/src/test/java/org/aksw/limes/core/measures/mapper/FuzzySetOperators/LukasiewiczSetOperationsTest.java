package org.aksw.limes.core.measures.mapper.FuzzySetOperators;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.FuzzyOperators.LukasiewiczSetOperations;
import org.junit.Before;
import org.junit.Test;

public class LukasiewiczSetOperationsTest {

	public AMapping m1;
	public AMapping m2;
	public AMapping m1Minusm2;
	public AMapping m2Minusm1;
	public AMapping intersection;
	public AMapping union;

	public LukasiewiczSetOperations lso = LukasiewiczSetOperations.INSTANCE;

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
		m2.add("i1", "i1", 0.0);
		m2.add("i2", "i2", 0.1);
		m2.add("i3", "i3", 0.9);
		m2.add("i4", "i4", 0.4);
		m2.add("i5", "i5", 0.5);
		m2.add("i6", "i6", 0.2);
		m2.add("i8", "i8", 0.7);

		intersection = MappingFactory.createDefaultMapping();
		intersection.add("i3", "i3", 0.4);

		union = MappingFactory.createDefaultMapping();
		union.add("i1", "i1", 0.8);
		union.add("i2", "i2", 1.0);
		union.add("i3", "i3", 1.0);
		union.add("i4", "i4", 0.6);
		union.add("i5", "i5", 0.9);
		union.add("i6", "i6", 0.5);
		union.add("i7", "i7", 0.6);
		union.add("i8", "i8", 0.7);

		m1Minusm2 = MappingFactory.createDefaultMapping();
		m1Minusm2.add("i1", "i1", 0.8);
		m1Minusm2.add("i2", "i2", 0.8);
		m1Minusm2.add("i6", "i6", 0.1);
		m1Minusm2.add("i7", "i7", 0.6);

		m2Minusm1 = MappingFactory.createDefaultMapping();
		m2Minusm1.add("i3", "i3", 0.4);
		m2Minusm1.add("i4", "i4", 0.2);
		m2Minusm1.add("i5", "i5", 0.1);
		m2Minusm1.add("i8", "i8", 0.7);
	}

	@Test
	public void testm1Differencem2() {
		assertEquals("m1/m2 is wrong!", m1Minusm2, lso.difference(m1, m2));
	}

	@Test
	public void testm2Differencem1() {
		assertEquals("m2/m1 is wrong!", m2Minusm1, lso.difference(m2, m1));
	}

	@Test
	public void testIntersection() {
		assertEquals("Lukasiewicz intersection is wrong!", intersection, lso.intersection(m1, m2));
	}

	@Test
	public void testUnion() {
		assertEquals("Lukasiewicz union is wrong!", union, lso.union(m1, m2));
	}

	@Test
	public void testTNorm() {
		assertEquals("Lukasiewicz t-norm is wrong", 0.1, lso.tNorm(0.8, 0.3), 0);
		assertEquals("Lukasiewicz t-norm is wrong", 0.0, lso.tNorm(0.6, 0.3), 0);
	}

	@Test
	public void testTConorm() {
		assertEquals("Lukasiewicz t-conorm is wrong", 1.0, lso.tConorm(0.8, 0.3), 0);
		assertEquals("Lukasiewicz t-conorm is wrong", 0.7, lso.tConorm(0.4, 0.3), 0);
	}
}
