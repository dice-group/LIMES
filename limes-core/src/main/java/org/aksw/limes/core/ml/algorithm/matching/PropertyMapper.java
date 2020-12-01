/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.matching;

import org.aksw.limes.core.io.mapping.AMapping;
import org.apache.jena.rdf.model.Model;

/**
 *
 * @author ngonga
 */
public interface PropertyMapper {
    public AMapping getPropertyMapping(String endpoint1, String endpoint2, String classExpression1, String classExpression2);
    public void setSourceModel(Model sourceModel);
    public void setTargetModel(Model targetModel);
}
