package org.aksw.limes.core.measures.measure.semantic.edgecounting.filters;

import java.util.ArrayList;

public class ShortestPathFilter extends ASemanticFilter {

    public ShortestPathFilter(double t, double d) {
        super(t, d);
    }

    @Override
    public boolean filter(ArrayList<Integer> parameters) {
        if (parameters == null)
            return false;
        if (parameters.size() < 2)
            return false;

        score = (double) Math.abs(parameters.get(0) - parameters.get(1));
        // 2 ∗ D ∗ (1 − θ)
        limit = 2.0 * (double) D * (1.0 - theta);
        
        return compare();
    }

}
