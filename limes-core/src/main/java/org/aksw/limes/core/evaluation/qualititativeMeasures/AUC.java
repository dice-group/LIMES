package org.aksw.limes.core.evaluation.qualititativeMeasures;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quantitative measure representing the area under the curve of ROC (see <a
 * href=
 * 'https://en.wikipedia.org/wiki/Receiver_operating_characteristic#Area_under_the_curve'>
 * here</a>).
 *
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 * @author Klaus Lyko (lyko@informatik.uni-leizig.de)
 * @version 1.0
 */
public class AUC extends APRF implements IQualitativeMeasure {
    static Logger logger = LoggerFactory.getLogger(AUC.class);

    @Override
    public double calculate(AMapping predictions, GoldStandard goldStandard) {
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
