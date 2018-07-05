package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It can be defined as the ratio of the retrieved correct results relative to the total number of the retrieved results,i.e. Tp/(Tp+Fp).
 *
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class Precision extends APRF implements IQualitativeMeasure {
    static Logger logger = LoggerFactory.getLogger(Precision.class);

    /** 
     * The method calculates the precision of the machine learning predictions compared to a gold standard
     * @param predictions The predictions provided by a machine learning algorithm
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs
     * @return double - This returns the calculated precision
     */
    @Override
    public double calculate(AMapping predictions, GoldStandard goldStandard) {
        if (predictions.size() == 0)
            return 0;
		return trueFalsePositive(predictions, goldStandard.referenceMappings, true)
				/ (double) ((MemoryMapping) predictions).getNumberofPositiveMappings();
    }

}
