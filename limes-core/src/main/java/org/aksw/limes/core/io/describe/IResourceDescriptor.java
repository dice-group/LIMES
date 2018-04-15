package org.aksw.limes.core.io.describe;

import org.apache.jena.rdf.model.Model;


public interface IResourceDescriptor {

    String getURI();

    Model queryDescription();

}
