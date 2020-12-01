package org.aksw.limes.core.evaluation.oracle;

import org.aksw.limes.core.io.mapping.AMapping;

/**
 * Basic idea is that the interface can load reference data and act as a user in simulations
 * This data will be mostly given as a mapping
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */

public interface IOracle {
    /**
     * Returns true if the mapping contains the two URIs, else false
     *
     * @param uri1
     *         First instance in instance pair
     * @param uri2
     *         Second instance in instance pair
     * @return boolean - true if exist otherwise false
     */
    public boolean ask(String uri1, String uri2);

    public void loadData(AMapping m);

    public int size();

    public AMapping getMapping();

    public String getType();
}
