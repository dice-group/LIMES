package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.io.mapping.Mapping;

public abstract class PseudoPRF extends PRF{ 
	public abstract double calculate(Mapping predictions, GoldStandard goldStandard);
	public boolean symmetricPrecision = true;
	boolean use1To1Mapping = false;

	/**
	 * @return the use1To1Mapping
	 */
	public boolean isUse1To1Mapping() {
		return use1To1Mapping;
	}
	public void setUse1To1Mapping(boolean use1To1Mapping) {
		this.use1To1Mapping = use1To1Mapping;
	}
}
