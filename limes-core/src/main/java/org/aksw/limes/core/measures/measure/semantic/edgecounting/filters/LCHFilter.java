package org.aksw.limes.core.measures.measure.semantic.edgecounting.filters;

import java.util.ArrayList;

public class LCHFilter extends ASemanticFilter {

    public LCHFilter(double t, double d) {
        super(t, d);
    }

    public double logOfBase(int base, int num) {
        return Math.log(num) / Math.log(base);
    }

    @Override
    public boolean filter(ArrayList<Integer> parameters) {
        if (parameters == null)
            return false;
        if (parameters.size() < 2)
            return false;

        score = (double) Math.abs(parameters.get(0) - parameters.get(1));
        // 2^{log(2∗D)∗(1−θ)}
        double C = (double) (((double) this.logOfBase(2, 2 * (int) D)) * ((double) (1.0 - theta)));
        limit = (double) Math.pow(2, C);

        
        return compare();
    }

}
