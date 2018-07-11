package org.aksw.limes.core.measures.measure.customGraphs.description.impl;

import org.aksw.limes.core.measures.measure.customGraphs.description.IDescriptionGraphView;
import org.apache.jena.rdf.model.RDFNode;

/**
 * Interface IGraphLoaded implements IDescriptionGraphView that gives all the information on node.
 *
 * @author Cedric Richter
 */
public interface IGraphLoaded extends IDescriptionGraphView {

    public void onStatement(String src, String property, RDFNode object);

}
