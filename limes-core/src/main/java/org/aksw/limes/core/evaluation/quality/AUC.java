package org.aksw.limes.core.evaluation.quality;

import java.util.Set;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.model.Link;

/**
 * @author Mofeed Hassan <mounir@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @author Klaus Lyko <lyko@informatik.uni-leizig.de>
 * @version 2015-11-03
 */
public class AUC extends PRF implements QualitativeMeasure {

	@Override
	public double calculate(Mapping predictions, Mapping goldStandard) {
		/*
		 * Technical it calculates Area under curve values.
		 * Thus, we need some sort of time dependent measurements.
		 * So, the QualitiveMeasurement interface is too specific.
		 * Furthermore, its more a quantitive measure!
		 */

		return 0;
	}

}
