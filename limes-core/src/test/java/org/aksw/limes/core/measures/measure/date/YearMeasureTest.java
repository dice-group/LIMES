package org.aksw.limes.core.measures.measure.date;

import static org.junit.Assert.*;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.date.YearMeasure;
import org.junit.Assert;
import org.junit.Test;

public class YearMeasureTest {

    @Test
    public void test() {

	YearMeasure m = new YearMeasure();
	Instance i1 = new Instance("i1");
	Instance i2 = new Instance("i2");
	i1.addProperty("date", "1999-07-16");
	i2.addProperty("date", "2000-07-13");

	double sim = m.getSimilarity(i1, i2, "date", "date");
	System.out.println("Sim of dates (" + i1.getProperty("date") + " - " + i2.getProperty("date") + ") ==" + sim);
	Assert.assertNotNull(sim);
	Assert.assertTrue("Similarity is ", Math.abs(0.9 - sim) <= 0.05);

    }

}
