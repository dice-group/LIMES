/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.aksw.limes.core.ml.algorithm.matching;

import org.aksw.limes.core.io.mapping.AMapping;

/**
 *
 * @author ngonga
 */
public interface OntologyClassMapper {
      public AMapping getEntityMapping(String endpoint1,
            String endpoint2, String namespace1, String namespace2);

}
