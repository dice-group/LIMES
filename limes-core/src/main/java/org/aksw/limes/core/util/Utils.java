package org.aksw.limes.core.util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.HashMap;
import java.util.List;

import org.aksw.limes.core.io.mapping.Mapping;
import org.apache.log4j.Logger;


/**
 *
 * @author ngonga
 */
public class Utils {

    /**
     * Computes the precision of the mapping computed with respect to the
     * mapping reference.
     *
     * @param reference
     * @param computed
     * @return Precision
     */
    static Logger logger = Logger.getLogger(Utils.class.getName());

    public static double getPrecision(Mapping reference, Mapping computed) {
        double size = (double) computed.size();
        double count = 0;
        for (String key : computed.getMap().keySet()) {
            for (String value : computed.getMap().get(key).keySet()) {
                if (reference.contains(key, value) || reference.contains(value, key)) {
                    count++;
                }
            }
        }
        return count / size;
    }

    /**
     * Computes the recall of the mapping computed with respect to the mapping
     * reference.
     *
     * @param reference
     * @param computed
     * @return Recall
     */
    public static double getRecall(Mapping reference, Mapping computed) {
        double size = (double) reference.size();
        double count = 0;
        for (String key : computed.getMap().keySet()) {
            for (String value : computed.getMap().get(key).keySet()) {
                if (reference.contains(key, value) || reference.contains(value, key)) {
                    count++;
                }
            }
        }
        return count / size;
    }

    /**
     * Computes the recall of the mapping computed with respect to the mapping
     * reference.
     *
     * @param reference
     * @param computed
     * @return Recall
     */
    public static double getFscore(Mapping reference, Mapping computed) {
        double sizeC = (double) computed.size();
        double sizeR = (double) reference.size();
        double count = 0;
        for (String key : computed.getMap().keySet()) {
            for (String value : computed.getMap().get(key).keySet()) {
                if (reference.contains(key, value) || reference.contains(value, key)) {
                    count++;
                }
            }
        }
        double p = count / sizeC;
        double r = count / sizeR;
        return 2 * p * r / (p + r);
    }

    /**
     * Computes all stats (i.e. precision, recall, f-score) of the mapping
     * computed with respect to the mapping reference.
     *
     * @param reference
     * @param computed
     * @return Precision, Recall and F-Score. The entries for the Hashmap are
     * "precision", "recall" and "fscore".
     */
    public static HashMap<String, Double> getPRF(Mapping reference, Mapping computed) {
        HashMap<String, Double> result = new HashMap<String, Double>();
        double sizeC = (double) computed.size();
        double sizeR = (double) reference.size();
        double count = 0;
        for (String key : computed.getMap().keySet()) {
            for (String value : computed.getMap().get(key).keySet()) {
                if (reference.contains(key, value) || reference.contains(value, key)) {
                    count++;
                }
            }
        }

        double p = count / sizeC;
        result.put("precision", p);
        double r = count / sizeR;
        result.put("recall", r);
        result.put("fscore", 2 * p * r / (p + r));

        return result;
    }

    /**
     * Splits camel case strings into lower case string separated with a " "
     *
     * @param s Input string in camel case
     * @return Split string
     */
    @SuppressWarnings("unused")
	private static String splitAtCamelCase(String s) {
        String regex = "([a-z])([A-Z])";
        String replacement = "$1 $2";
        return s.replaceAll(regex, replacement).toLowerCase();
    }

    public static double getStandardDeviation(List<Double> data) {
// sd is sqrt of sum of (values-mean) squared divided by n - 1 
// Calculate the mean 
        double mean = 0;
        final int n = data.size();
        if (n < 2) {
            return Double.NaN;
        }
        for (int i = 0; i < n; i++) {
            mean += data.get(i);
        }
        mean /= n;
       // calculate the sum of squares 
        double sum = 0;
        for (int i = 0; i < n; i++) {
            final double v = data.get(i) - mean;
            sum += v * v;
        }
    // Change to ( n - 1 ) to n if you have complete data instead of a sample. 
        return Math.sqrt(sum / (n - 1));
    }
    
    

    public static double getMean(List<Double> data) {
// sd is sqrt of sum of (values-mean) squared divided by n - 1 
// Calculate the mean 
        double mean = 0;
        final int n = data.size();
        if (n < 2) {
            return Double.NaN;
        }
        for (int i = 0; i < n; i++) {
            mean += data.get(i);
        }
        mean /= n;
        return mean;
    }
}
