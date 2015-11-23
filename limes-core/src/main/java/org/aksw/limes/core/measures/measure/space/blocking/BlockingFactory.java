/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.space.blocking;




/**
 *
 * @author ngonga
 */
public class BlockingFactory {

    public static BlockingModule getBlockingModule(String props, String measureName, double threshold, int granularity) {
        if (measureName.toLowerCase().startsWith("euclidean")) {
            if (granularity > 1) {
                return new HR3Blocker(props, measureName, threshold, granularity);
            } else {
                return new EuclideanBlockingModule(props, measureName, threshold);
            }
        }

        return new EuclideanBlockingModule(props, measureName, threshold);
    }
}
