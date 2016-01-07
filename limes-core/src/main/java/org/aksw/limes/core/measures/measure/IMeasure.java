package org.aksw.limes.core.measures.measure;

import org.aksw.limes.core.io.cache.Instance;

public interface IMeasure {
    public double getSimilarity(Object a, Object b);    
    public double getSimilarity(Instance a, Instance b, String property1, String property2);
    public double getRuntimeApproximation(double mappingSize);
    public String getName();
    public String getType();

    
}
