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

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a quality measure for unsupervised ML algorihtms, dubbed pseudo F-Measure.
 * Thereby, not relying on any gold standard. The basic idea is to measure the quality of the
 * a given Mapping by calc. how close it is to an assumed 1-to-1 Mapping between source and
 * target.
 *
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author mofeed hassan
 * @version 1.0
 * @since 1.0
 */
public class PseudoRecall extends APseudoPRF {
    static Logger logger = LoggerFactory.getLogger(PseudoRecall.class);

    public PseudoRecall() {
    }

    /**
     * Use this constructor to toggle between symmetric precision (true) and the older asymmetric
     * Pseudo-Precision (false)
     *
     * @param symmetricPrecision sets/resets the symmetric precision flag
     */
    public PseudoRecall(final boolean symmetricPrecision) {
        this();
        this.setSymmetricPrecision(symmetricPrecision);
    }


    /**
     * The method calculates the pseudo recall of the machine learning predictions compared to a gold standard , which is how many of the s and t
     * were mapped.
     * @param predictions The predictions provided by a machine learning algorithm.
     * @param goldStandard It contains the gold standard (reference mapping) combined with the source and target URIs.
     * @return double - This returns the calculated pseudo recall.
     */

    public double calculate(AMapping predictions, GoldStandard goldStandard) {
        AMapping res = predictions;
        if (useOneToOneMapping) {
            res = predictions.getBestOneToNMapping();
        }
        double q = res.getMap().keySet().size();
        Set<String> values = new HashSet<String>();
        for (String s : res.getMap().keySet()) {
            for (String t : res.getMap().get(s).keySet()) {
                values.add(t);
            }
        }
        double reference = (double) (goldStandard.sourceUris.size() + goldStandard.targetUris.size());
        return (q + values.size()) / reference;
    }
}
