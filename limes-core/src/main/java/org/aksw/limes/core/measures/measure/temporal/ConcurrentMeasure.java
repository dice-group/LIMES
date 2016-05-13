package org.aksw.limes.core.measures.measure.temporal;

import org.aksw.limes.core.io.cache.Instance;

public class ConcurrentMeasure extends TemporalMeasure {

    @Override
    public double getSimilarity(Object a, Object b) {
	double sim = 0;
	String split1[] = ((String) a).split("\\|");
	String split2[] = ((String) b).split("\\|");

	if (Double.valueOf(split1[0]).equals(Double.valueOf(split2[0]))
		&& Double.valueOf(split1[1]).equals(Double.valueOf(split2[1])))
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

	String machineID1 = this.getSecondProperty(property1);
	String machineID2 = this.getSecondProperty(property2);

	if (new Double(a.getProperty(beginDate1).first())
		== ((new Double(b.getProperty(beginDate2).first())))
		&& new Double(a.getProperty(machineID1).first())
			== ((new Double(b.getProperty(machineID2).first()))))
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
	return "Concurrent";
    }

    @Override
    public String getType() {
	return "temporal";
    }

}
