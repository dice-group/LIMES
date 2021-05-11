/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
