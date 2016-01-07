package org.aksw.limes.core.measures.measure.date;

import static org.junit.Assert.*;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.date.DayMeasure;
import org.junit.Assert;
import org.junit.Test;

public class DayMeasureTest {

    @Test
    public void test() {
	DayMeasure m = new DayMeasure();
        Instance i1 = new Instance("i1");
        Instance i2 = new Instance("i2");
        i1.addProperty("date", "01.01.2013");
        i2.addProperty("date", "15.06.2013");

        double sim = m.getSimilarity(i1, i2, "date", "date");
        long days = (long) (365 - (sim * 365));
        System.out.println("Sim of dates (" + i1.getProperty("date") + " - " + i2.getProperty("date") + ") ==" + sim + " (what  equals " + days + " days)");
        Assert.assertNotNull(sim);
        Assert.assertTrue("Similarity is ", Math.abs(0.5 - sim) <= 0.1);
    
    }

}
