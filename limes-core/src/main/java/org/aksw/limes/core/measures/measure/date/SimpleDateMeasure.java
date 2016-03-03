package org.aksw.limes.core.measures.measure.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.space.ISpaceMeasure;

import org.apache.log4j.Logger;

/**
 * An implementation of the {@link IDateMeasure} to compute the similarity of
 * dates as a asymptotic mapping of the a in days to a similarity value in [0,
 * 1]. Whereas, let <i>dayDiff</i> be a difference of two dates measured in
 * number of days. The similarity value is computed as e^(-0.005*dayDiff). The
 * following table shows some similarity values...
 * <table>
 * <tr>
 * <th>Difference in number of days</th>
 * <th>sim value</th>
 * </tr>
 * <tr>
 * <td>0</td>
 * <td>1.0</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>0.9950124791926823</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>0.9900498337491681</td>
 * </tr>
 * <tr>
 * <td>5</td>
 * <td>0.9753099120283326</td>
 * </tr>
 * <tr>
 * <td>10</td>
 * <td>0.951229424500714</td>
 * </tr>
 * <tr>
 * <td>365 (1 year)</td>
 * <td>0.1612176441297768</td>
 * </tr>
 * <tr>
 * <td>730 (2 years)</td>
 * <td>0.02599112877875535</td>
 * </tr>
 * <tr>
 * <td>1095 (3 years)</td>
 * <td>0.004190228549984578</td>
 * </tr>
 * </table>
 *
 * For a more human-centric similarity implementation please see
 * {@link DayMeasure}
 * 
 * @version 0.1 This is a
 * @author Klaus Lyko
 *
 */
public class SimpleDateMeasure extends DateMeasure implements IDateMeasure, ISpaceMeasure {
    static Logger logger = Logger.getLogger("LIMES");
    public double dim = 1;

    @Override
    public double getSimilarity(Object a, Object b) {
	logger.debug("getSimilarity of " + a + " and " + b);
	Date d1 = extractDate(a.toString());
	Date d2 = extractDate(b.toString());
	long diff;
	if (d1 != null && d2 != null) {
	    diff = getDayDifference(d1, d2);
	    double sim = computeSimValue(diff);
	    // System.out.println("Similarity of Dates("+d1+" - "+d2+") ==
	    // "+sim);
	    logger.debug("Similarity of Dates(" + d1 + " - " + d2 + ") == " + sim);
	    return sim;
	} else
	    return 0d;

    }

    @Override
    public String getType() {
	return "date";
    }

    @Override
    public double getSimilarity(Instance a, Instance b, String property1, String property2) {
	Date d1 = null;
	Date d2 = null;
	long diff = 0;
	boolean set = false;
	for (String source : a.getProperty(property1)) {
	    for (String target : b.getProperty(property2)) {
		d1 = extractDate(source);
		d2 = extractDate(target);
		if (d1 != null && d2 != null) {
		    diff += getDayDifference(d1, d2);
		    set = true;
		}
	    }
	}
	if (set) {
	    double sim = computeSimValue(diff);
	    logger.debug("Similarity of Dates(" + d1 + " - " + d2 + ") == " + sim);
	    return sim;
	}
	return 0d;
    }

    /**
     * Computes similarity value of this {@link IDateMeasure} based upon the
     * difference of two dates in days. Other implementations should override
     * this method to provide their similarity implementation.
     * 
     * @param dayDifference
     * @return A similarity value in [0, 1].
     */
    protected double computeSimValue(long dayDifference) {
	return Math.pow(Math.E, (-0.01 * dayDifference));
    }

    @Override
    public String getName() {
	return "DayMeasure";
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
	return mappingSize / 1000d;
    }

    @Override
    public Date extractDate(String toParse) {
	DateFormat format;
	Date date = null;
	// date is only a year
	String regex = "^(\\d?\\d?\\d\\d)$";
	if (toParse.matches(regex)) {
	    format = new SimpleDateFormat("yyyy");
	    try {
		date = (Date) format.parse(toParse);
		return date;
	    } catch (ParseException e) {
		e.printStackTrace();
	    }
	}
	// date in format yyyy-mm-dd
	regex = "^(\\d?\\d\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$";
	if (toParse.matches(regex)) {
	    format = new SimpleDateFormat("yyyy-MM-dd");
	    try {
		date = (Date) format.parse(toParse);
		return date;
	    } catch (ParseException e) {
		e.printStackTrace();
	    }
	}
	// date in format yyyy/mm/dd
	regex = "^(\\d?\\d\\d\\d)/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])$";
	if (toParse.matches(regex)) {
	    format = new SimpleDateFormat("yyyy/MM/dd");
	    try {
		date = (Date) format.parse(toParse);
		return date;
	    } catch (ParseException e) {
		e.printStackTrace();
	    }
	}
	// date in format dd/mm/yyyy
	regex = "^(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/(\\d?\\d?\\d\\d)$";
	if (toParse.matches(regex)) {
	    format = new SimpleDateFormat("dd/MM/yyyy");
	    try {
		date = (Date) format.parse(toParse);
		return date;
	    } catch (ParseException e) {
		e.printStackTrace();
	    }
	}
	// date in format dd.mm.yyyy
	regex = "^(0?[1-9]|[12][0-9]|3[01])\\.(0?[1-9]|1[012])\\.\\d?\\d?\\d\\d$";
	if (toParse.matches(regex)) {
	    format = new SimpleDateFormat("dd.MM.yyyy");
	    try {
		date = (Date) format.parse(toParse);
		return date;
	    } catch (ParseException e) {
		e.printStackTrace();
	    }
	}
	// date in format something like dd/Jan/yyyy
	regex = "^(0?[1-9]|[12][0-9]|3[01])[/.-][a-zA-Z]{3}[/.-](\\d?\\d\\d\\d)$";
	if (toParse.matches(regex)) {
	    format = new SimpleDateFormat("dd/MMM/yyyy");
	    try {
		date = (Date) format.parse(toParse);
		return date;
	    } catch (ParseException e) {
	    }
	    format = new SimpleDateFormat("dd-MMM-yyyy");
	    try {
		date = (Date) format.parse(toParse);
		return date;
	    } catch (ParseException e) {
	    }
	    format = new SimpleDateFormat("dd.MMM.yyyy");
	    try {
		date = (Date) format.parse(toParse);
		return date;
	    } catch (ParseException e) {
	    }
	}
	// date in format something like dd/March/yyyy
	regex = "^(0?[1-9]|[12][0-9]|3[01])[/.-][a-zA-Z]{4,10}[/.-](\\d?\\d\\d\\d)$";
	if (toParse.matches(regex)) {
	    format = new SimpleDateFormat("dd/MMMM/yyyy");
	    try {
		date = (Date) format.parse(toParse);
		return date;
	    } catch (ParseException e) {
	    }
	    format = new SimpleDateFormat("dd-MMMM-yyyy");
	    try {
		date = (Date) format.parse(toParse);
		return date;
	    } catch (ParseException e) {
	    }
	    format = new SimpleDateFormat("dd.MMMM.yyyy");
	    try {
		date = (Date) format.parse(toParse);
		return date;
	    } catch (ParseException e) {
	    }
	}
	return date;
    }

    @Override
    public Long getDayDifference(Date d1, Date d2) {
	long diff = Math.abs(d1.getTime() - d2.getTime());// in seconds
	return Math.round((double) diff / (24. * 60. * 60. * 1000.));
    }

    @Override
    public void setDimension(int n) {
	logger.debug("Trying to set dimension to " + n);
	dim = n;
    }

    @Override
    public double getThreshold(int dimension, double simThreshold) {
	logger.debug("calling getThreshold(" + dimension + ", " + simThreshold + ")");
	return simThreshold;
    }
   

}
