package org.aksw.limes.core.measures.measure.temporal.simpleTemporal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.temporal.TemporalMeasure;

/**
 * Implements the temporal concurrent measure class.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public class ConcurrentMeasure extends TemporalMeasure {

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
        long epoch1 = 0, epoch2 = 0;
        try {
            date1 = df.parse(split1[0]);
            epoch1 = date1.getTime();
        } catch (ParseException e) {
            System.err.println("Exiting..");
        }
        try {
            date2 = df.parse(split2[0]);
            epoch2 = date2.getTime();
        } catch (ParseException e) {
            System.err.println("Exiting..");

        }
        if (epoch1 == epoch2 && split1[1].equals(split2[1]))
            sim = 1;
        else
            sim = 0;

        return sim;
    }

    /**
     * Returns the similarity between two instances given their begin dates and
     * their machine ids. If the instances have the same begin date and the same
     * machine ids, then their similarity is 1, and 0 otherwise.
     * 
     *
     * @return The similarity of the instances
     */
    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        String beginDate1 = this.getFirstProperty(property1);
        String beginDate2 = this.getFirstProperty(property2);

        String machineID1 = null;
        machineID1 = this.getSecondProperty(property1);
        String machineID2 = null;
        machineID2 = this.getSecondProperty(property2);

        String s1 = new String(
                instance1.getProperty(beginDate1).first() + "|" + instance1.getProperty(machineID1).first());
        String s2 = new String(
                instance2.getProperty(beginDate2).first() + "|" + instance2.getProperty(machineID2).first());

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
        return "Concurrent";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "temporal";
    }

}
