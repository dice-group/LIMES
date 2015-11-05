package org.aksw.limes.core.measures.measure.date;

import org.aksw.limes.core.data.Instance;
import org.junit.Assert;
import org.junit.Test;


/**
 * Computes the similarity of two dates based upon years within a decade.
 * Every date within 365 has a similarity of 1. Whereas two dates more then 10 years (3650 days) apart
 * will be assigned a similarity value of 0. That means every year between two dates will substract 0.1. 
 * E.g. two dates within 2 years (365 - 730 days) have a similarity value of 0.8.
 * @author Klaus Lyko
 *
 */
public class YearMeasure extends DayMeasure{
	@Override
	protected double computeSimValue(long dayDifference) {
//		System.out.println("Computing year similarity for daydiff:"+dayDifference);
 	   if(dayDifference >= 3650)
				return 0;
			else {
				return(3650d-dayDifference) / 3650d;
			}
	}
	
	@Test
	public void testSimComputation() {
		YearMeasure m = new YearMeasure();
		Instance i1 = new Instance("i1");
		Instance i2 = new Instance("i2");
		i1.addProperty("date", "1999-07-16");
		i2.addProperty("date", "2000-07-13");
		
		double sim = m.getSimilarity(i1, i2, "date", "date");
		System.out.println("Sim of dates ("+i1.getProperty("date")+" - "+i2.getProperty("date")+") =="+ sim );
		Assert.assertNotNull(sim);
		Assert.assertTrue("Similarity is ", Math.abs(0.9-sim)<=0.05);
	}
}
