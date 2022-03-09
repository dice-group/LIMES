package org.aksw.limes.core.measures.measure;

import static org.junit.Assert.*;

import org.aksw.limes.core.measures.measure.string.LessThanMeasure;
import org.junit.Test;

public class LessThanStringTest {

	@Test
	public void testLessThan() throws Exception {
		LessThanMeasure lessThan = new LessThanMeasure();

		String str1 = "a";
		String str2 = "b";
		
		System.out.println(lessThan.getSimilarity(str1, str2));

		assertEquals(lessThan.getSimilarity(str1, str2), 1d, 0d);

	}
}
