/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.classifier;

import org.aksw.limes.core.io.mapping.AMapping;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class ExtendedClassifier extends SimpleClassifier {

    protected AMapping mapping;

    public ExtendedClassifier(String measure, double threshold) {
        super(measure, threshold);
    }

    public ExtendedClassifier(String measure, double threshold, String sourceProperty, String targetProperty) {
        super(measure, threshold, sourceProperty, targetProperty);
    }

    public AMapping getMapping() {
        return mapping;
    }

    public void setMapping(AMapping mapping) {
        this.mapping = mapping;
    }

}
