package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It measures how far the algorithm retrieved correct results out of the all existed correct results.<br>
 * It is defined to be the ratio between the true positive to the total number of correct results whether retrieved or not
 *
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class Recall extends APRF implements IQualitativeMeasure {
    static Logger logger = LoggerFactory.getLogger(Recall.class);

    /** 
     * The method calculates the recall of the machine learning predictions compared to a gold standard
     * @param predictions The predictions provided by a machine learning algorithm
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs
     * @return double - This returns the calculated recall
     */
    @Override
    public double calculate(AMapping predictions, GoldStandard goldStandard) {
        if (predictions.size() == 0)
            return 0;
		return trueFalsePositive(predictions, goldStandard.referenceMappings, true)
				/ (double) ((MemoryMapping) goldStandard.referenceMappings).getNumberofPositiveMappings();
    }

}
