/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.wombat;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.ml.algorithm.euclid.SimpleClassifier;

/**
 *
 * @author ngonga
 */
public class ExtendedClassifier extends SimpleClassifier {
    
    public Mapping mapping;
    
    public ExtendedClassifier(String measure, double threshold){
        super(measure, threshold);
    }
    
    public ExtendedClassifier(String measure, double threshold, String sourceProperty, String targetProperty){
        super(measure, threshold, sourceProperty, targetProperty);
    }
    
}
