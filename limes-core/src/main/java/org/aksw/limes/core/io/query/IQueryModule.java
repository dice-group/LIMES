/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.aksw.limes.core.io.query;

import org.aksw.limes.core.io.cache.Cache;

/**
 * Interface for query modules
 *
 * @author ngonga <ngonga@informatik.uni-leipzig.de>
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Jul 8, 2016
 */
public interface IQueryModule {


    /**
     * @param cache Cache object to be filled
     */
    public void fillCache(Cache cache);
}
