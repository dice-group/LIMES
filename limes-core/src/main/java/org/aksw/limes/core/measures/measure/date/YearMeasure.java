package org.aksw.limes.core.measures.measure.date;

<<<<<<< HEAD
=======
import org.aksw.limes.core.io.cache.Instance;
import org.junit.Assert;
import org.junit.Test;
>>>>>>> 04f229403216e5956dd16f2b2e0519c2b5ae47d3

/**
 * Computes the similarity of two dates based upon years within a decade. Every
 * date within 365 has a similarity of 1. Whereas two dates more then 10 years
 * (3650 days) apart will be assigned a similarity value of 0. That means every
 * year between two dates will substract 0.1. E.g. two dates within 2 years (365
 * - 730 days) have a similarity value of 0.8.
 * 
 * @author Klaus Lyko
 *
 */
public class YearMeasure extends DayMeasure {
    @Override
    protected double computeSimValue(long dayDifference) {

	if (dayDifference >= 3650)
	    return 0;
	else {
	    return (3650d - dayDifference) / 3650d;
	}
    }

}
