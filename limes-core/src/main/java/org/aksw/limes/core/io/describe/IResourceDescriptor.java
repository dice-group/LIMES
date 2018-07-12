package org.aksw.limes.core.io.describe;

import org.apache.jena.rdf.model.Model;


/**
 * A description for a single resource.
 *
 * @author Cedric Richter
 */
public interface IResourceDescriptor {

    /**
     *
     * @return uri which relates to the query resource
     */
    String getURI();

    /**
     *
     * @return a model representing the description of a given resource
     */
    Model queryDescription();

}
