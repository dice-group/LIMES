package org.aksw.limes.core.measures.measure.string;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Utility functions used for testing
 *
 * @author Swante Scholz
 */
public class MyTestUtil {
    
    static double getMean(Collection<Double> data) {
        double sum = 0.0;
        for (double a : data) {
            sum += a;
        }
        return sum / data.size();
    }
    
    static double getVariance(Collection<Double> data) {
        double mean = getMean(data);
        double temp = 0;
        for (double a : data) {
            temp += (a - mean) * (a - mean);
        }
        return temp / (data.size() - 1);
    }
    
    static double getStdDeviation(Collection<Double> data) {
        return Math.sqrt(getVariance(data));
    }
    
    static public double getMedian(Collection<Double> data) {
        List<Double> list = new ArrayList<>(data);
        Collections.sort(list);
        
        if (list.size() % 2 == 0) {
            return (list.get((list.size() / 2) - 1) + list.get(list.size() / 2)) / 2.0;
        }
        return list.get(list.size() / 2);
    }
    
}
