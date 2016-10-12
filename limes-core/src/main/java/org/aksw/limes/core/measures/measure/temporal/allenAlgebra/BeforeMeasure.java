package org.aksw.limes.core.measures.measure.temporal.allenAlgebra;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.temporal.TemporalMeasure;

/**
 * Implements the temporal before measure class.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class BeforeMeasure extends TemporalMeasure {

    // EB1
    /**
     * {@inheritDoc}
     */
    @Override
    public double getSimilarity(Object object1, Object object2) {
        double sim = 0;
        String split1[] = ((String) object1).split("\\|");
        String split2[] = ((String) object2).split("\\|");

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Date date1, date2;
        long endDate1 = 0, beginDate2 = 0;
        try {
            date1 = df.parse(split1[1]);
            endDate1 = date1.getTime();
        } catch (ParseException e) {
            System.err.println("Exiting..");
        }
        try {
            date2 = df.parse(split2[0]);
            beginDate2 = date2.getTime();
        } catch (ParseException e) {
            System.err.println("Exiting..");
        }

        if (endDate1 < beginDate2)
            sim = 1;
        else
            sim = 0;

        return sim;
    }

    /**
     * Returns the similarity between two instances given their begin and end
     * dates. If the first instance has a end date lower than the begin date of
     * the second instance, then their similarity is 1, and 0 otherwise.
     * 
     *
     * @return The similarity of the instances
     */
    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        String endDate1 = null;
            endDate1 = this.getSecondProperty(property1);
        String beginDate2 = this.getFirstProperty(property2);

        String s1 = new String("*" + "|" + instance1.getProperty(endDate1).first());
        String s2 = new String(instance2.getProperty(beginDate2).first());

        return this.getSimilarity(s1, s2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Before";
    }

    @Override
    public String getType() {
        return "temporal";

    }

}
