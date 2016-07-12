/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.euclid;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.measure.Measure;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public interface ISelfConfigurator {
    public void computeMeasure(Cache source, Cache target, String parameters[]);

    public String getMeasure();

    public void setMeasure(Measure measure);

    public String getThreshold();

    public AMapping getResults();

}
