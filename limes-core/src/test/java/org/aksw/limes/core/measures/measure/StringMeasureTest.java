package org.aksw.limes.core.measures.measure;

import static org.junit.Assert.*;
import org.aksw.limes.core.measures.measure.string.TrigramMeasure;
import org.junit.Test;

public class StringMeasureTest {

	@Test
	public void testTrigram() throws Exception {
		TrigramMeasure trigram = new TrigramMeasure();

		String str1 = "Impasto Pizza & Cafe";
		String str2 = "Mpoukia & Sychorio";


		assertEquals(trigram.getSimilarity(str1, str2), 0.05d, 0.d);

	}

}
