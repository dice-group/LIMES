package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.Mapping;

/**
 * An Interface specifies calculate method signature common for all qualitative measures
 * @author Mofeed Hassan <mounir@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 1.0
 *
 */
public interface IQualitativeMeasure {
	
	public double calculate(Mapping predictions, GoldStandard goldStandard);

}
