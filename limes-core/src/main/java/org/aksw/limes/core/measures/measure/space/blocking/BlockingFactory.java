/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.space.blocking;

import org.apache.log4j.Logger;



/**
 *
 * @author ngonga
 */
public class BlockingFactory {

    public static BlockingModule getBlockingModule(String props, String measureName, double threshold, int granularity) {
        if (measureName.toLowerCase().startsWith("euclidean")) {
            if (granularity > 1) {
        //        System.out.println("Granularity is " + granularity);
                //return new VariableGranularityBlocker(props, measureName, threshold, granularity);
                return new HR3Blocker(props, measureName, threshold, granularity);
            } else {
                return new EuclideanBlockingModule(props, measureName, threshold);
            }
        }
//     else if (measureName.toLowerCase().startsWith("datesim")) {
//    	 return new VariableGranularityBlocker(props, measureName, threshold, granularity);
//    } else if (measureName.toLowerCase().startsWith("daysim")) {
//    	return new VariableGranularityBlocker(props, measureName, threshold, granularity);
//    } else if (measureName.toLowerCase().startsWith("yearsim")) {
//    	return new VariableGranularityBlocker(props, measureName, threshold, granularity);
//    }
        return new EuclideanBlockingModule(props, measureName, threshold);
    }
}
