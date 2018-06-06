package org.aksw.limes.core.measures.mapper.FuzzySetOperators;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.aksw.limes.core.exceptions.ParameterOutOfRangeException;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.FuzzyOperators.AlgebraicSetOperations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AlgebraicSetOperationsTest {

	public AMapping m1;
	public AMapping m2;
	public AMapping m1Minusm2;
	public AMapping m2Minusm1;
	public AMapping intersection;
	public AMapping union;

	public AlgebraicSetOperations aso = AlgebraicSetOperations.INSTANCE;

	@Rule
	public ExpectedException exceptions = ExpectedException.none();

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
		intersection.add("i2", "i2", 0.09);
		intersection.add("i3", "i3", 0.45);
		intersection.add("i4", "i4", 0.08);
		intersection.add("i5", "i5", 0.2);
		intersection.add("i6", "i6", 0.06);

		union = MappingFactory.createDefaultMapping();
		union.add("i1", "i1", 0.8);
		union.add("i2", "i2", 0.91);
		union.add("i3", "i3", 0.95);
		union.add("i4", "i4", 0.52);
		union.add("i5", "i5", 0.7);
		union.add("i6", "i6", 0.44);
		union.add("i7", "i7", 0.6);
		union.add("i8", "i8", 0.7);

		m1Minusm2 = MappingFactory.createDefaultMapping();
		m1Minusm2.add("i1", "i1", 0.8);
		m1Minusm2.add("i2", "i2", 0.81);
		m1Minusm2.add("i3", "i3", 0.05);
		m1Minusm2.add("i4", "i4", 0.12);
		m1Minusm2.add("i5", "i5", 0.2);
		m1Minusm2.add("i6", "i6", 0.24);
		m1Minusm2.add("i7", "i7", 0.6);

		m2Minusm1 = MappingFactory.createDefaultMapping();
		m2Minusm1.add("i2", "i2", 0.01);
		m2Minusm1.add("i3", "i3", 0.45);
		m2Minusm1.add("i4", "i4", 0.32);
		m2Minusm1.add("i5", "i5", 0.3);
		m2Minusm1.add("i6", "i6", 0.14);
		m2Minusm1.add("i8", "i8", 0.7);
	}

	@Test
	public void testm1Differencem2() {
		assertEquals(m1Minusm2, aso.difference(m1, m2, Double.NaN));
	}

	@Test
	public void testm2Differencem1() {
		assertEquals(m2Minusm1, aso.difference(m2, m1, Double.NaN));
	}

	@Test
	public void testIntersection() {
		assertEquals(intersection, aso.intersection(m1, m2, Double.NaN));
	}

	@Test
	public void testUnion() {
		assertEquals(union, aso.union(m1, m2, Double.NaN));
	}

	@Test
	public void testTNorm() {
		assertEquals(0.24, aso.tNorm(BigDecimal.valueOf(0.8), BigDecimal.valueOf(0.3), Double.NaN), 0);
		assertEquals(0.18, aso.tNorm(BigDecimal.valueOf(0.6), BigDecimal.valueOf(0.3), Double.NaN), 0);
		assertEquals(0.3, aso.tNorm(BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.3), Double.NaN), 0);
	}

	@Test
	public void testTConorm() {
		assertEquals(0.86, aso.tConorm(BigDecimal.valueOf(0.8), BigDecimal.valueOf(0.3), Double.NaN), 0);
		assertEquals(0.58, aso.tConorm(BigDecimal.valueOf(0.4), BigDecimal.valueOf(0.3), Double.NaN), 0);
		assertEquals(0.3, aso.tConorm(BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.3), Double.NaN), 0);
	}

	@Test
	public void testAOutOfRangeTNorm() throws Exception {
		exceptions.expect(ParameterOutOfRangeException.class);
		aso.tNorm(BigDecimal.valueOf(2.0), BigDecimal.valueOf(0.3), Double.NaN);
	}

	@Test
	public void testAOutOfRangeTConorm() throws Exception {
		exceptions.expect(ParameterOutOfRangeException.class);
		aso.tConorm(BigDecimal.valueOf(2.0), BigDecimal.valueOf(0.3), Double.NaN);
	}

	@Test
	public void testBOutOfRangeTNorm() throws Exception {
		exceptions.expect(ParameterOutOfRangeException.class);
		aso.tNorm(BigDecimal.valueOf(1.0), BigDecimal.valueOf(-1.3), Double.NaN);
	}

	@Test
	public void testBOutOfRangeTConorm() throws Exception {
		exceptions.expect(ParameterOutOfRangeException.class);
		aso.tConorm(BigDecimal.valueOf(1.0), BigDecimal.valueOf(-1.3), Double.NaN);
	}
}
