package org.aksw.limes.core.evaluation.oracle;

import org.aksw.limes.core.io.mapping.Mapping;

/** Basic idea is that the interface can load reference data and act as a user in simulations
 * This data will be mostly given as a mapping 
 * @author ngonga
 * @author Mofeed Hassan
 * @version 1.0
 */

public interface IOracle {
    /** Returns true if the mapping contains the two URIs, else false
     * 
     * @param uri1 First instance in instance pair
     * @param uri2 Second instance in instance pair
     * @return
     */
    public boolean ask(String uri1, String uri2);
    public void loadData(Mapping m);
    public int size();
    public Mapping getMapping();
    public String getType();
}
