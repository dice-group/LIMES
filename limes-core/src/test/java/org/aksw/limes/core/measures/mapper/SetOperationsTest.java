package org.aksw.limes.core.measures.mapper;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.Before;
import org.junit.Test;

public class SetOperationsTest {

	private AMapping a;
	private AMapping b;
	private AMapping intersection;
	private AMapping union;
	private AMapping difference;

	@Before
	public void prepareData() {
		a = MappingFactory.createDefaultMapping();
		b = MappingFactory.createDefaultMapping();
		intersection = MappingFactory.createDefaultMapping();
		union = MappingFactory.createDefaultMapping();
		difference = MappingFactory.createDefaultMapping();

		a.add("c", "c", 0.5);
		a.add("a", "z", 0.5);
		a.add("a", "d", 0.5);
		a.add("i", "i", 0.5);

		b.add("a", "c", 0.5);
		b.add("a", "b", 0.7);
		b.add("b", "y", 0.7);
		b.add("i", "i", 0.7);

		union.add("c", "c", 0.5);
		union.add("a", "z", 0.5);
		union.add("a", "d", 0.5);
		union.add("a", "c", 0.5);
		union.add("a", "b", 0.7);
		union.add("b", "y", 0.7);
		union.add("i", "i", 0.7);

		intersection.add("i", "i", 0.5);

		difference.add("c", "c", 0.5);
		difference.add("a", "z", 0.5);
		difference.add("a", "d", 0.5);
	}

	@Test
	public void testUnion() {
		assertEquals(union, CrispSetOperations.INSTANCE.union(a, b));
	}

	@Test
	public void testIntersection() {
		assertEquals(intersection, CrispSetOperations.INSTANCE.intersection(a, b));
	}

	@Test
	public void testDifference() {
		assertEquals(difference, CrispSetOperations.INSTANCE.difference(a, b));
	}

}
