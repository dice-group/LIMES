package org.aksw.limes.core.measures.measure.temporal.simpleTemporal;

import org.aksw.limes.core.io.cache.Instance;

public class PredecessorMeasure extends SimpleTemporalMeasure {

    @Override
    public double getSimilarity(Object a, Object b) {
	double sim = 0;

	if (new Double((Double) a) > (new Double((Double) b)))
	    sim = 1;
	else
	    sim = 0;

	return sim;
    }

    @Override
    public double getSimilarity(Instance a, Instance b, String property1, String property2) {
	double sim = 0;

	if (new Double(a.getProperty(property1).first()) > ((new Double(b.getProperty(property2).first()))))
	    sim = 1;
	else
	    sim = 0;

	return sim;
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
	return mappingSize / 1000d;
    }

    @Override
    public String getName() {
	return "Predecessor";
    }

    @Override
    public String getType() {
	return "temporal";
    }

}
