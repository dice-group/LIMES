/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.space.blocking;

import java.io.Serializable;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class BlockingFactory implements Serializable {

    public static IBlockingModule getBlockingModule(String props, String measureName, double threshold, int granularity) {
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
