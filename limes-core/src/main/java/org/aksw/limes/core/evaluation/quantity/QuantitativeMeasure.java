package org.aksw.limes.core.evaluation.quantity;

import org.aksw.limes.core.io.mapping.Mapping;

/**
 * @author Mofeed Hassan <mounir@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-03
 *
 */
public interface QuantitativeMeasure {
	
	public double calculate(Mapping predictions, Mapping goldStandard);

}
