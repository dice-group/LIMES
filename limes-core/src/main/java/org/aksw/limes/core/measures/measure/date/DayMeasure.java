package org.aksw.limes.core.measures.measure.date;

import org.aksw.limes.core.data.Instance;
import org.junit.Assert;
import org.junit.Test;


/**
 * An implementation of the {@link DateMeasure} to compute the similarity of
 * dates based on days. As we have to compute a similarity <i>sim</i> as
 * <i>0<=sim<=1</i> this {@link Measure} computes the similarity of two Dates d1
 * and d2 based on their difference <i>dayDifference(d1, d2)</i> in number of
 * days as (365 - dayDifference(d1, d2) ) / 365. Which means if d1 and d2
 * reference the same day it is 1; if d1 and d2 are more than a year apart from
 * each other it is 0; and two dates half a year apart should have a similarity
 * value around 0.5.
 *
 * @author Klaus Lyko
 *
 */
public class DayMeasure extends SimpleDateMeasure {

    @Override
    protected double computeSimValue(long dayDifference) {
        if (dayDifference >= 365) {
            return 0;
        } else {
            return (365d - dayDifference) / 365d;
        }
    }

    @Test
    public void testSimComputation() {
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
