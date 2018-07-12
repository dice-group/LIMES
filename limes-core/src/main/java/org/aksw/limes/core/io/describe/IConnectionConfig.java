package org.aksw.limes.core.io.describe;

/**
 * A connection config for a given dataset
 *
 * @author Cedric Richter
 */
public interface IConnectionConfig {

    /**
     *
     * @return delay for querying new resources
     */
    public int getRequestDelayInMs();

}
