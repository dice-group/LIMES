package org.aksw.limes.core.measures.measure.date;

import java.util.Date;

import org.aksw.limes.core.measures.measure.IMeasure;

/**
 * Interface for date measures.
 * 
 * @author Klaus Lyko
 *
 */
public interface IDateMeasure extends IMeasure {
    /**
     * Parse String as a Date.
     * 
     * @param s
     *            String representation of a Date.
     * @return Date instance or null if s can't be parsed to a Date.
     */
    public Date extractDate(String toParse);

    /**
     * Compute difference of both Dates in Days.
     * 
     * @param d1
     * @param d2
     * @return Number of days between two Dates.
     */
    public Long getDayDifference(Date d1, Date d2);

    
}
