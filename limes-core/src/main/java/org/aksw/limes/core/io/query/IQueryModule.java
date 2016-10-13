/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.aksw.limes.core.io.query;

import org.aksw.limes.core.io.cache.ACache;

/**
 * Interface for query modules
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 12, 2016
 */
public interface IQueryModule {


    /**
     * @param cache Cache object to be filled
     */
    public void fillCache(ACache cache);
}
