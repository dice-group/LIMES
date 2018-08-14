package org.aksw.limes.core.measures.measure.semantic.edgecounting.filters;

import java.util.ArrayList;

public class LiFilter extends ASemanticFilter {
    private double a = 0.2;
    private double b = 0.6;

    public LiFilter(double t, double d) {
        super(t,d);
    }

    @Override
    public boolean filter(ArrayList<Integer> parameters) {
        if (parameters == null)
            return false;
        if (parameters.size() < 4)
            return false;
        
        score = (double) Math.abs(parameters.get(0)-parameters.get(1));
        double min = (double) Math.min(parameters.get(2), parameters.get(3));
        
        // ln(e^{2∗β∗min} − 1) − lnθ − ln(e2∗β∗min + 1) / α

        // e^(2∗β∗y)
        double temp = Math.pow(Math.E, 2.0 * b * min);
        // ln(e^(2∗β∗y) − 1)
        double x1 = Math.log(temp - 1.0);
        
        // lnθ
        double x2 = Math.log(theta);
        
        // ln(e^(2∗β∗y) + 1)
        double x3 = Math.log(temp + 1.0);
        

        limit = (double) (x1 - x2 - x3) / a;

        
        return compare();
    }

}
