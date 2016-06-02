package org.aksw.limes.core.measures.measure.temporal.simpleTemporal;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.temporal.TemporalMeasure;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConcurrentMeasure extends TemporalMeasure {
    private static final Logger logger = Logger.getLogger(ConcurrentMeasure.class.getName());

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

    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        String beginDate1 = this.getFirstProperty(property1);
        String beginDate2 = this.getFirstProperty(property2);

        String machineID1 = null;
        try {
            machineID1 = this.getSecondProperty(property1);
        } catch (IllegalArgumentException e) {
            logger.error("Concurrent events require both begin date and machine id.   Exiting..");
            System.exit(1);
        }
        String machineID2 = null;
        try {
            machineID2 = this.getSecondProperty(property2);
        } catch (IllegalArgumentException e) {
            logger.error("Concurrent events require both begin date and machine id.   Exiting..");
            System.exit(1);
        }

        String s1 = new String(instance1.getProperty(beginDate1).first() + "|" + instance1.getProperty(machineID1).first());
        String s2 = new String(instance2.getProperty(beginDate2).first() + "|" + instance2.getProperty(machineID2).first());

        return this.getSimilarity(s1, s2);

    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }

    @Override
    public String getName() {
        return "Concurrent";
    }

    @Override
    public String getType() {
        return "temporal";
    }

}
