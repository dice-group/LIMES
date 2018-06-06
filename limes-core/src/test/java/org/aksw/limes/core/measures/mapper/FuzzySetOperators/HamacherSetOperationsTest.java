package org.aksw.limes.core.measures.mapper.FuzzySetOperators;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.aksw.limes.core.exceptions.ParameterOutOfRangeException;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.FuzzyOperators.HamacherSetOperations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HamacherSetOperationsTest {

	public AMapping m1;
	public AMapping m2;
	public AMapping m1Minusm2p1;
	public AMapping m2Minusm1p1;
	public AMapping intersectionp1;
	public AMapping unionp1;

	public AMapping m1Minusm2p10;
	public AMapping m2Minusm1p10;
	public AMapping intersectionp10;
	public AMapping unionp10;

	public HamacherSetOperations hso = HamacherSetOperations.INSTANCE;

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

		intersectionp1 = MappingFactory.createDefaultMapping();
		intersectionp1.add("i2", "i2", 0.09);
		intersectionp1.add("i3", "i3", 0.45);
		intersectionp1.add("i4", "i4", 0.08);
		intersectionp1.add("i5", "i5", 0.2);
		intersectionp1.add("i6", "i6", 0.06);

		unionp1 = MappingFactory.createDefaultMapping();
		unionp1.add("i1", "i1", 0.8);
		unionp1.add("i2", "i2", 0.91);
		unionp1.add("i3", "i3", 0.95);
		unionp1.add("i4", "i4", 0.52);
		unionp1.add("i5", "i5", 0.7);
		unionp1.add("i6", "i6", 0.44);
		unionp1.add("i7", "i7", 0.6);
		unionp1.add("i8", "i8", 0.7);

		m1Minusm2p1 = MappingFactory.createDefaultMapping();
		m1Minusm2p1.add("i1", "i1", 0.8);
		m1Minusm2p1.add("i2", "i2", 0.81);
		m1Minusm2p1.add("i3", "i3", 0.05);
		m1Minusm2p1.add("i4", "i4", 0.12);
		m1Minusm2p1.add("i5", "i5", 0.2);
		m1Minusm2p1.add("i6", "i6", 0.24);
		m1Minusm2p1.add("i7", "i7", 0.6);

		m2Minusm1p1 = MappingFactory.createDefaultMapping();
		m2Minusm1p1.add("i2", "i2", 0.01);
		m2Minusm1p1.add("i3", "i3", 0.45);
		m2Minusm1p1.add("i4", "i4", 0.32);
		m2Minusm1p1.add("i5", "i5", 0.3);
		m2Minusm1p1.add("i6", "i6", 0.14);
		m2Minusm1p1.add("i8", "i8", 0.7);

		intersectionp10 = MappingFactory.createDefaultMapping();
		intersectionp10.add("i2", "i2", 0.049723757);
		intersectionp10.add("i3", "i3", 0.310344828);
		intersectionp10.add("i4", "i4", 0.015037594);
		intersectionp10.add("i5", "i5", 0.054054054);
		intersectionp10.add("i6", "i6", 0.009933775);

		unionp10 = MappingFactory.createDefaultMapping();
		unionp10.add("i1", "i1", 0.8);
		unionp10.add("i2", "i2", 0.952631579);
		unionp10.add("i3", "i3", 0.990909091);
		unionp10.add("i4", "i4", 0.733333333);
		unionp10.add("i5", "i5", 0.9);
		unionp10.add("i6", "i6", 0.65);
		unionp10.add("i7", "i7", 0.6);
		unionp10.add("i8", "i8", 0.7);

		m1Minusm2p10 = MappingFactory.createDefaultMapping();
		m1Minusm2p10.add("i1", "i1", 0.8);
		m1Minusm2p10.add("i2", "i2", 0.743119266);
		m1Minusm2p10.add("i3", "i3", 0.00990099);
		m1Minusm2p10.add("i4", "i4", 0.030927835);
		m1Minusm2p10.add("i5", "i5", 0.054054054);
		m1Minusm2p10.add("i6", "i6", 0.10619469);
		m1Minusm2p10.add("i7", "i7", 0.6);

		m2Minusm1p10 = MappingFactory.createDefaultMapping();
		m2Minusm1p10.add("i2", "i2", 0.001206273);
		m2Minusm1p10.add("i3", "i3", 0.310344828);
		m2Minusm1p10.add("i4", "i4", 0.153846154);
		m2Minusm1p10.add("i5", "i5", 0.107142857);
		m2Minusm1p10.add("i6", "i6", 0.044303797);
		m2Minusm1p10.add("i8", "i8", 0.7);
	}

	@Test
	public void testm1Differencem2p1() {
		assertEquals(m1Minusm2p1, hso.difference(m1, m2, 1.0));
	}

	@Test
	public void testm2Differencem1p1() {
		assertEquals(m2Minusm1p1, hso.difference(m2, m1, 1.0));
	}

	@Test
	public void testIntersectionp1() {
		assertEquals(intersectionp1, hso.intersection(m1, m2, 1.0));
	}

	@Test
	public void testUnionp1() {
		assertEquals(unionp1, hso.union(m1, m2, 0.0));
	}

	@Test
	public void testm1Differencem2p10() {
		assertEquals(m1Minusm2p10, hso.difference(m1, m2, 10.0));
	}

	@Test
	public void testm2Differencem1p10() {
		assertEquals(m2Minusm1p10, hso.difference(m2, m1, 10.0));
	}

	@Test
	public void testIntersectionp10() {
		assertEquals(intersectionp10, hso.intersection(m1, m2, 10.0));
	}

	@Test
	public void testUnionp10() {
		assertEquals(unionp10, hso.union(m1, m2, 10.0));
	}

	@Test
	public void testTNorm() {
		assertEquals(0.24, hso.tNorm(BigDecimal.valueOf(0.8), BigDecimal.valueOf(0.3), 1.0), 0);
		assertEquals(0.18, hso.tNorm(BigDecimal.valueOf(0.6), BigDecimal.valueOf(0.3), 1.0), 0);
		assertEquals(0.3, hso.tNorm(BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.3), 1.0), 0);

		assertEquals(0.3, hso.tNorm(BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.3), 23.1042), 0);
		assertEquals(0.166666667, hso.tNorm(BigDecimal.valueOf(0.6), BigDecimal.valueOf(0.5), 5.0), 0);
	}

	@Test
	public void testTConorm() {
		assertEquals(0.86, hso.tConorm(BigDecimal.valueOf(0.8), BigDecimal.valueOf(0.3), 0.0), 0);
		assertEquals(0.58, hso.tConorm(BigDecimal.valueOf(0.4), BigDecimal.valueOf(0.3), 0.0), 0);
		assertEquals(0.3, hso.tConorm(BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.3), 0.0), 0);

		assertEquals(0.3, hso.tConorm(BigDecimal.valueOf(0.0), BigDecimal.valueOf(0.3), 23.42), 0);
		assertEquals(1.0, hso.tConorm(BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.9), 3.42), 0);

	}

	@Test
	public void testParameterToSmallTNorm() throws Exception {
		exceptions.expect(ParameterOutOfRangeException.class);
		hso.tNorm(BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.3), -0.1);
	}

	@Test
	public void testParameterToSmallTConorm() throws Exception {
		exceptions.expect(ParameterOutOfRangeException.class);
		hso.tConorm(BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.3), -1.1);
	}

	@Test
	public void testAOutOfRangeTNorm() throws Exception {
		exceptions.expect(ParameterOutOfRangeException.class);
		hso.tNorm(BigDecimal.valueOf(2.0), BigDecimal.valueOf(0.3), 1.0);
	}

	@Test
	public void testAOutOfRangeTConorm() throws Exception {
		exceptions.expect(ParameterOutOfRangeException.class);
		hso.tConorm(BigDecimal.valueOf(2.0), BigDecimal.valueOf(0.3), 1.0);
	}

	@Test
	public void testBOutOfRangeTNorm() throws Exception {
		exceptions.expect(ParameterOutOfRangeException.class);
		hso.tNorm(BigDecimal.valueOf(1.0), BigDecimal.valueOf(-1.3), 1.0);
	}

	@Test
	public void testBOutOfRangeTConorm() throws Exception {
		exceptions.expect(ParameterOutOfRangeException.class);
		hso.tConorm(BigDecimal.valueOf(1.0), BigDecimal.valueOf(-1.3), 1.0);
	}
}
