package org.aksw.limes.core.measures.mapper.FuzzySetOperators;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.aksw.limes.core.exceptions.ParameterOutOfRangeException;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.FuzzyOperators.YagerSetOperations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class YagerSetOperationsTest {

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

	public YagerSetOperations yso = YagerSetOperations.INSTANCE;
	public static final double epsilon = 0.00001;

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
		intersectionp1.add("i3", "i3", 0.4);

		unionp1 = MappingFactory.createDefaultMapping();
		unionp1.add("i1", "i1", 0.8);
		unionp1.add("i2", "i2", 1);
		unionp1.add("i3", "i3", 1);
		unionp1.add("i4", "i4", 0.6);
		unionp1.add("i5", "i5", 0.9);
		unionp1.add("i6", "i6", 0.5);
		unionp1.add("i7", "i7", 0.6);
		unionp1.add("i8", "i8", 0.7);

		m1Minusm2p1 = MappingFactory.createDefaultMapping();
		m1Minusm2p1.add("i1", "i1", 0.8);
		m1Minusm2p1.add("i2", "i2", 0.8);
		m1Minusm2p1.add("i6", "i6", 0.1);
		m1Minusm2p1.add("i7", "i7", 0.6);

		m2Minusm1p1 = MappingFactory.createDefaultMapping();
		m2Minusm1p1.add("i3", "i3", 0.4);
		m2Minusm1p1.add("i4", "i4", 0.2);
		m2Minusm1p1.add("i5", "i5", 0.1);
		m2Minusm1p1.add("i8", "i8", 0.7);

		intersectionp10 = MappingFactory.createDefaultMapping();
		intersectionp10.add("i2", "i2", 0.1);
		intersectionp10.add("i3", "i3", 0.5);
		intersectionp10.add("i4", "i4", 0.195605);
		intersectionp10.add("i5", "i5", 0.390949);
		intersectionp10.add("i6", "i6", 0.181096);

		unionp10 = MappingFactory.createDefaultMapping();
		unionp10.add("i1", "i1", 0.8);
		unionp10.add("i2", "i2", 0.9);
		unionp10.add("i3", "i3", 0.900252);
		unionp10.add("i4", "i4", 0.400039);
		unionp10.add("i5", "i5", 0.505126);
		unionp10.add("i6", "i6", 0.300516);
		unionp10.add("i7", "i7", 0.6);
		unionp10.add("i8", "i8", 0.7);

		m1Minusm2p10 = MappingFactory.createDefaultMapping();
		m1Minusm2p10.add("i1", "i1", 0.8);
		m1Minusm2p10.add("i2", "i2", 0.892823);
		m1Minusm2p10.add("i3", "i3", 0.0997482);
		m1Minusm2p10.add("i4", "i4", 0.199922);
		m1Minusm2p10.add("i5", "i5", 0.390949);
		m1Minusm2p10.add("i6", "i6", 0.3);
		m1Minusm2p10.add("i7", "i7", 0.6);

		m2Minusm1p10 = MappingFactory.createDefaultMapping();
		m2Minusm1p10.add("i2", "i2", 0.0354039);
		m2Minusm1p10.add("i3", "i3", 0.5);
		m2Minusm1p10.add("i4", "i4", 0.399999);
		m2Minusm1p10.add("i5", "i5", 0.494874);
		m2Minusm1p10.add("i6", "i6", 0.199996);
		m2Minusm1p10.add("i8", "i8", 0.7);

	}

	// @Test
	// public void testPOptimizedIntersection() {
	// AMapping ref = MappingFactory.createDefaultMapping();
	// ref.add("i3", "i3", 1.0);
	// ref.add("i5", "i5", 1.0);
	//
	// AMapping a = MappingFactory.createDefaultMapping();
	// a.add("i3", "i3", 0.9);
	// a.add("i4", "i4", 0.7);
	// a.add("i6", "i6", 0.6);
	// a.add("i5", "i5", 0.6);
	//
	// AMapping b = MappingFactory.createDefaultMapping();
	// b.add("i3", "i3", 0.8);
	// b.add("i6", "i6", 0.7);
	// b.add("i5", "i5", 0.9);
	//
	// System.out.println(HamacherSetOperations.INSTANCE.intersection(a, b, ref));
	// }
	//
	// @Test
	// public void testPOptimizedUnion() {
	// AMapping ref = MappingFactory.createDefaultMapping();
	// ref.add("i3", "i3", 1.0);
	// ref.add("i5", "i5", 1.0);
	//
	// AMapping a = MappingFactory.createDefaultMapping();
	// a.add("i3", "i3", 0.9);
	// a.add("i4", "i4", 0.7);
	// a.add("i6", "i6", 0.2);
	// a.add("i5", "i5", 0.6);
	//
	// AMapping b = MappingFactory.createDefaultMapping();
	// b.add("i3", "i3", 0.8);
	// b.add("i6", "i6", 0.4);
	// b.add("i5", "i5", 0.9);
	//
	// HamacherSetOperations.INSTANCE.union(a, b, ref);
	// }

	@Test
	public void testm1Differencem2p1() {
		assertEquals(m1Minusm2p1, yso.difference(m1, m2, 1.0));
	}

	@Test
	public void testm2Differencem1p1() {
		assertEquals(m2Minusm1p1, yso.difference(m2, m1, 1.0));
	}

	@Test
	public void testIntersectionp1() {
		assertEquals(intersectionp1, yso.intersection(m1, m2, 1.0));
	}

	@Test
	public void testUnionp1() {
		assertEquals(unionp1, yso.union(m1, m2, 1.0));
	}

	@Test
	public void testm1Differencem2p10() {
		AMapping diff = yso.difference(m1, m2, 10.0);
		assertEquals(m1Minusm2p10.size(), diff.size());
		assertEquals(m1Minusm2p10.getMap().get("i2").get("i2"), diff.getMap().get("i2").get("i2"), epsilon);
		assertEquals(m1Minusm2p10.getMap().get("i3").get("i3"), diff.getMap().get("i3").get("i3"), epsilon);
		assertEquals(m1Minusm2p10.getMap().get("i4").get("i4"), diff.getMap().get("i4").get("i4"), epsilon);
		assertEquals(m1Minusm2p10.getMap().get("i5").get("i5"), diff.getMap().get("i5").get("i5"), epsilon);
		assertEquals(m1Minusm2p10.getMap().get("i6").get("i6"), diff.getMap().get("i6").get("i6"), epsilon);
		assertEquals(m1Minusm2p10.getMap().get("i7").get("i7"), diff.getMap().get("i7").get("i7"), epsilon);
	}

	@Test
	public void testm2Differencem1p10() {
		AMapping diff = yso.difference(m2, m1, 10.0);
		System.out.println(diff);
		System.out.println(m2Minusm1p10);
		assertEquals(m2Minusm1p10.size(), diff.size());
		assertEquals(m2Minusm1p10.getMap().get("i3").get("i3"), diff.getMap().get("i3").get("i3"), epsilon);
		assertEquals(m2Minusm1p10.getMap().get("i4").get("i4"), diff.getMap().get("i4").get("i4"), epsilon);
		assertEquals(m2Minusm1p10.getMap().get("i5").get("i5"), diff.getMap().get("i5").get("i5"), epsilon);
		assertEquals(m2Minusm1p10.getMap().get("i6").get("i6"), diff.getMap().get("i6").get("i6"), epsilon);
		assertEquals(m2Minusm1p10.getMap().get("i8").get("i8"), diff.getMap().get("i8").get("i8"), epsilon);
	}

	@Test
	public void testIntersectionp10() {
		AMapping intersection = yso.intersection(m1, m2, 10.0);
		assertEquals(intersectionp10.size(), intersection.size());
		assertEquals(intersectionp10.getMap().get("i3").get("i3"), intersection.getMap().get("i3").get("i3"), epsilon);
		assertEquals(intersectionp10.getMap().get("i4").get("i4"), intersection.getMap().get("i4").get("i4"), epsilon);
		assertEquals(intersectionp10.getMap().get("i5").get("i5"), intersection.getMap().get("i5").get("i5"), epsilon);
		assertEquals(intersectionp10.getMap().get("i6").get("i6"), intersection.getMap().get("i6").get("i6"), epsilon);
	}

	@Test
	public void testUnionp10() {
		AMapping union = yso.union(m1, m2, 10.0);
		assertEquals(unionp10.size(), union.size());
		assertEquals(unionp10.getMap().get("i1").get("i1"), union.getMap().get("i1").get("i1"), epsilon);
		assertEquals(unionp10.getMap().get("i2").get("i2"), union.getMap().get("i2").get("i2"), epsilon);
		assertEquals(unionp10.getMap().get("i3").get("i3"), union.getMap().get("i3").get("i3"), epsilon);
		assertEquals(unionp10.getMap().get("i4").get("i4"), union.getMap().get("i4").get("i4"), epsilon);
		assertEquals(unionp10.getMap().get("i5").get("i5"), union.getMap().get("i5").get("i5"), epsilon);
		assertEquals(unionp10.getMap().get("i6").get("i6"), union.getMap().get("i6").get("i6"), epsilon);
		assertEquals(unionp10.getMap().get("i7").get("i7"), union.getMap().get("i7").get("i7"), epsilon);
		assertEquals(unionp10.getMap().get("i8").get("i8"), union.getMap().get("i8").get("i8"), epsilon);
	}

	@Test
	public void testTNorm() {
		assertEquals(0.0, yso.tNorm(BigDecimal.valueOf(0.8), BigDecimal.valueOf(0.3), 0), epsilon);
		assertEquals(0.3, yso.tNorm(BigDecimal.valueOf(1), BigDecimal.valueOf(0.3), 0), epsilon);

		assertEquals(0.3, yso.tNorm(BigDecimal.valueOf(0.8), BigDecimal.valueOf(0.3), Double.POSITIVE_INFINITY),
				epsilon);

		assertEquals(0.3, yso.tNorm(BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.3), 23.1042), epsilon);
		assertEquals(0.470838, yso.tNorm(BigDecimal.valueOf(0.6), BigDecimal.valueOf(0.5), 5.0), epsilon);
	}

	@Test
	public void testTConorm() {
		assertEquals(1, yso.tConorm(BigDecimal.valueOf(0.8), BigDecimal.valueOf(0.3), 0), epsilon);
		assertEquals(0.3, yso.tConorm(BigDecimal.valueOf(0), BigDecimal.valueOf(0.3), 0), epsilon);

		assertEquals(0.8, yso.tConorm(BigDecimal.valueOf(0.8), BigDecimal.valueOf(0.3), Double.POSITIVE_INFINITY),
				epsilon);

		assertEquals(1, yso.tConorm(BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.3), 23.1042), epsilon);
		assertEquals(0.641938, yso.tConorm(BigDecimal.valueOf(0.6), BigDecimal.valueOf(0.5), 5.0), epsilon);
	}

	@Test
	public void testParameterToSmallTNorm() throws Exception {
		exceptions.expect(ParameterOutOfRangeException.class);
		yso.tNorm(BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.3), -0.1);
	}

	@Test
	public void testParameterToSmallTConorm() throws Exception {
		exceptions.expect(ParameterOutOfRangeException.class);
		yso.tConorm(BigDecimal.valueOf(1.0), BigDecimal.valueOf(0.3), -1.1);
	}

	@Test
	public void testAOutOfRangeTNorm() throws Exception {
		exceptions.expect(ParameterOutOfRangeException.class);
		yso.tNorm(BigDecimal.valueOf(2.0), BigDecimal.valueOf(0.3), 1.0);
	}

	@Test
	public void testAOutOfRangeTConorm() throws Exception {
		exceptions.expect(ParameterOutOfRangeException.class);
		yso.tConorm(BigDecimal.valueOf(2.0), BigDecimal.valueOf(0.3), 1.0);
	}

	@Test
	public void testBOutOfRangeTNorm() throws Exception {
		exceptions.expect(ParameterOutOfRangeException.class);
		yso.tNorm(BigDecimal.valueOf(1.0), BigDecimal.valueOf(-1.3), 1.0);
	}

	@Test
	public void testBOutOfRangeTConorm() throws Exception {
		exceptions.expect(ParameterOutOfRangeException.class);
		yso.tConorm(BigDecimal.valueOf(1.0), BigDecimal.valueOf(-1.3), 1.0);
	}
}
