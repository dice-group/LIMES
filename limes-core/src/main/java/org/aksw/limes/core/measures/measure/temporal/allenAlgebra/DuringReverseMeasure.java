package org.aksw.limes.core.measures.measure.temporal.allenAlgebra;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.temporal.TemporalMeasure;

public class DuringReverseMeasure extends TemporalMeasure {
    // BB1 \\ (EE0 U EE1)
    @Override
    public double getSimilarity(Object a, Object b) {
	double sim = 0;
	String split1[] = ((String) a).split("\\|");
	String split2[] = ((String) b).split("\\|");

	if (new Double(split1[0]) < new Double(split2[0]) && new Double(split1[1]) > new Double(split2[1]))
	    sim = 1;
	else
	    sim = 0;

	return sim;
    }

    @Override
    public double getSimilarity(Instance a, Instance b, String property1, String property2) {
	double sim = 0;
	String beginDate1 = this.getFirstProperty(property1);
	String beginDate2 = this.getFirstProperty(property2);

	String endDate1 = this.getSecondProperty(property1);
	String endDate2 = this.getSecondProperty(property2);

	if (new Double(a.getProperty(beginDate1).first()) < ((new Double(b.getProperty(beginDate2).first())))
		&& new Double(a.getProperty(endDate1).first()) > ((new Double(b.getProperty(endDate2).first()))))
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
	return "DuringReverse";
    }

    @Override
    public String getType() {
	return "temporal";

    }

}
