package org.aksw.limes.core.util.datetime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Enum class of the allowed date and time formats. Allowed formats are devised
 * from https://www.w3.org/TR/NOTE-datetime
 * 
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */

public enum DateTimeFormat {

    // allowed values for timezone(3X): Z or +hh:mm or -hh:mm
    // milliseconds+timezone
    FORMAT1("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
    // milliseconds+no timezone
    FORMAT2("yyyy-MM-dd'T'HH:mm:ss.SSS"),

    // no milliseconds+timesize
    FORMAT3("yyyy-MM-dd'T'HH:mm:ssXXX"),
    // no milliseconds+no timezone
    FORMAT4("yyyy-MM-dd'T'HH:mm:ss"),

    // hours and minutes+timezone
    FORMAT5("yyyy-MM-dd'T'HH:mmXXX"),
    // hours and minutes+no timezone
    FORMAT6("yyyy-MM-dd'T'HH:mm"),

    FORMAT7("yyyy-MM-dd"), 
    FORMAT8("yyyy-MM"), 
    FORMAT9("yyyy");

    private static final Logger logger = LoggerFactory.getLogger(DateTimeFormat.class);

    private String pattern;

    DateTimeFormat(String value) {
        pattern = value;
    }

    public String getPattern() {
        return this.pattern;
    }

    /**
     * Returns the Date value of a time stamp
     *
     * @param timeStamp,
     *            the input time stamp
     * @return the Date value of the time stamp
     * 
     */
    public static Date getDate(String timeStamp) {
        SimpleDateFormat df = null;
        Date date = null;

        for (DateTimeFormat pat : DateTimeFormat.values()) {
            try {
                logger.info("Before: " + date);
                df = new SimpleDateFormat(pat.getPattern());
                // exception -> date remains null
                date = df.parse(timeStamp);
                logger.info("After: " + date.toString());
                // no exception -> date gets a value
                break;
            } catch (ParseException ignored) {
            }
        }
        if (date == null) {
            logger.error("Couldn't parse date: " + timeStamp);
            throw new RuntimeException();
        }

        return date;
    }

}
