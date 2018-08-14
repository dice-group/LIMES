package org.aksw.limes.core.measures.measure.semantic.edgecounting.filters;

import java.util.ArrayList;

public abstract class ASemanticFilter {

    protected double theta;
    protected double D;
    public double score = 0.0;
    public double limit = 0.0;
    
    public ASemanticFilter(double t, double d){
        theta = t;
        D = d;
    }
    
    public abstract boolean filter(ArrayList<Integer> parameters);
    
    public boolean compare(){
        return (score <= limit);
    }
}
