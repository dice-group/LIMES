/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.aksw.limes.core.io.query;

import org.aksw.limes.core.io.cache.Cache;

/**
 * Interface for query modules. SPARQL query module implemented so far.
 * Query from file and databases will be implemented soon.
 * @author ngonga
 */
public interface IQueryModule {
    public void fillCache(Cache c);
}
