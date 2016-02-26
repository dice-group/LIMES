package org.aksw.limes.core.evaluation.quantity;

import org.aksw.limes.core.io.mapping.Mapping;

/**
 * Quantitative measure representing the area under the curve of ROC (see <a
 * href=
 * 'https://en.wikipedia.org/wiki/Receiver_operating_characteristic#Area_under_the_curve'>
 * here</a>).
 * 
 * @author Mofeed Hassan <mounir@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Klaus Lyko <lyko@informatik.uni-leizig.de>
 * @version 2016-02-26
 */
public class AUC extends PRF implements QuantitativeMeasure {

	@Override
	public double calculate(Mapping predictions, Mapping goldStandard) {
		/*
		 * Technical it calculates Area under curve values. Thus, we need some
		 * sort of time dependent measurements. So, the QualitiveMeasurement
		 * interface is too specific. Furthermore, its more a quantitive
		 * measure!
		 * 
		 * => Answer: see header.
		 */

		return 0;
	}

}
