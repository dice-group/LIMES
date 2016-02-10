/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.aksw.limes.core.io.query;

import org.aksw.limes.core.io.cache.Cache;

/**
 * Interface for query modules
 * @author ngonga
 */
public interface IQueryModule {
	
    /**
     * @param cache
     */
    public void fillCache(Cache c);
}
