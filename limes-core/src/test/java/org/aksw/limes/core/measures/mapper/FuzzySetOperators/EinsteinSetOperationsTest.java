package org.aksw.limes.core.measures.mapper.FuzzySetOperators;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.FuzzyOperators.EinsteinSetOperations;
import org.junit.Before;
import org.junit.Test;

public class EinsteinSetOperationsTest {

	public AMapping m1;
	public AMapping m2;
	public AMapping m1Minusm2;
	public AMapping m2Minusm1;
	public AMapping intersection;
	public AMapping union;

	public EinsteinSetOperations eso = EinsteinSetOperations.INSTANCE;

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

		intersection = MappingFactory.createDefaultMapping();
		intersection.add("i2", "i2", 0.082568807);
		intersection.add("i3", "i3", 0.428571429);
		intersection.add("i4", "i4", 0.054054054);
		intersection.add("i5", "i5", 0.153846154);
		intersection.add("i6", "i6", 0.038461538);

		union = MappingFactory.createDefaultMapping();
		union.add("i1", "i1", 0.8);
		union.add("i2", "i2", 0.917431193);
		union.add("i3", "i3", 0.965517241);
		union.add("i4", "i4", 0.555555556);
		union.add("i5", "i5", 0.75);
		union.add("i6", "i6", 0.471698113);
		union.add("i7", "i7", 0.6);
		union.add("i8", "i8", 0.7);

		m1Minusm2 = MappingFactory.createDefaultMapping();
		m1Minusm2.add("i1", "i1", 0.8);
		m1Minusm2.add("i2", "i2", 0.801980198);
		m1Minusm2.add("i3", "i3", 0.034482759);
		m1Minusm2.add("i4", "i4", 0.090909091);
		m1Minusm2.add("i5", "i5", 0.153846154);
		m1Minusm2.add("i6", "i6", 0.210526316);
		m1Minusm2.add("i7", "i7", 0.6);

		m2Minusm1 = MappingFactory.createDefaultMapping();
		m2Minusm1.add("i2", "i2", 0.005524862);
		m2Minusm1.add("i3", "i3", 0.428571429);
		m2Minusm1.add("i4", "i4", 0.285714286);
		m2Minusm1.add("i5", "i5", 0.25);
		m2Minusm1.add("i6", "i6", 0.112903226);
		m2Minusm1.add("i8", "i8", 0.7);
	}

	@Test
	public void testm1Differencem2() {
		assertEquals(m1Minusm2, eso.difference(m1, m2));
	}

	@Test
	public void testm2Differencem1() {
		assertEquals(m2Minusm1, eso.difference(m2, m1));
	}

	@Test
	public void testIntersection() {
		assertEquals(intersection, eso.intersection(m1, m2));
	}

	@Test
	public void testUnion() {
		assertEquals(union, eso.union(m1, m2));
	}

	@Test
	public void testTNorm() {
		assertEquals(0.210526316,
				eso.tNorm(BigDecimal.valueOf(0.8), BigDecimal.valueOf(0.3)), 0);
		assertEquals(0.140625,
				eso.tNorm(BigDecimal.valueOf(0.6), BigDecimal.valueOf(0.3)), 0);
		assertEquals(0.3,
				eso.tNorm(BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.3)), 0);
	}

	@Test
	public void testTConorm() {
		assertEquals(0.887096774,
				eso.tConorm(BigDecimal.valueOf(0.8), BigDecimal.valueOf(0.3)),
				0);
		assertEquals(0.625,
				eso.tConorm(BigDecimal.valueOf(0.4), BigDecimal.valueOf(0.3)),
				0);
		assertEquals(0.3,
				eso.tConorm(BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.3)),
				0);
	}
}
