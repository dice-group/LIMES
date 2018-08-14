package org.aksw.limes.core.measures.measure.semantic.edgecounting.filters;

import java.util.ArrayList;

public class WuPalmerFilter extends ASemanticFilter {

    public WuPalmerFilter(double t, double d) {
        super(t, d);
    }

    @Override
    public boolean filter(ArrayList<Integer> parameters) {
        if (parameters == null)
            return false;
        if (parameters.size() < 4)
            return false;

        score = (double) Math.abs(parameters.get(0) - parameters.get(1));

        double min = (double) Math.min(parameters.get(2), parameters.get(3));
        // 2 ∗ min ∗ (1 − θ) / θ
        limit = (double) ((2.0 * (double) min) * (1.0 - theta)) / (double) theta;
        
        return compare();
    }

}
