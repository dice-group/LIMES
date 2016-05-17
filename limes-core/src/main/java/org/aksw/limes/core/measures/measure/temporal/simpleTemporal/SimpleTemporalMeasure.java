package org.aksw.limes.core.measures.measure.temporal.simpleTemporal;

import org.aksw.limes.core.measures.measure.Measure;

public abstract class SimpleTemporalMeasure extends Measure implements ISimpleTemporalMeasure {
    /**
     * Extract first property (beginDate) from metric expression.
     * 
     * @param expression,
     *            metric expression
     * 
     * @return first property of metric expression as string
     */
    protected String getFirstProperty(String properties) {
	int plusIndex = properties.indexOf("|");
	if (properties.indexOf("|") != -1) {
	    String p1 = properties.substring(0, plusIndex);
	    return p1;
	} else
	    return properties;
    }

    /**
     * Extract second property (endDate or machineID) from metric expression.
     * 
     * @param expression,
     *            the metric expression
     * 
     * @return second property of metric expression as string
     */
    protected String getSecondProperty(String properties) {
	int plusIndex = properties.indexOf("|");
	if (properties.indexOf("|") != -1) {
	    String p1 = properties.substring(plusIndex + 1, properties.length());
	    return p1;
	} else
	    return properties;
    }
}
