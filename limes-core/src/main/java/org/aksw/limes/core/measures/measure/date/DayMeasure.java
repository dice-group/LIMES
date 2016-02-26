package org.aksw.limes.core.measures.measure.date;

/**
 * An implementation of the {@link IDateMeasure} to compute the similarity of
 * dates based on days. As we have to compute a similarity <i>sim</i> as
 * <i>0<=sim<=1</i> this {@link IMeasure} computes the similarity of two Dates
 * d1 and d2 based on their difference <i>dayDifference(d1, d2)</i> in number of
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
    public double getRuntimeApproximation(double mappingSize) {
	return mappingSize / 1000d;
    }

}
