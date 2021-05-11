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
package org.aksw.limes.core.ml.algorithm;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.NotYetImplementedException;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;

import java.util.List;

public class ACIDS extends ACoreMLAlgorithm {

    @Override
    protected String getName() {
        return "ACIDS";
    }

    @Override
    protected void init(List<LearningParameter> lp, ACache source, ACache target) {
        super.init(lp, source, target);
        // TODO
        throw new NotYetImplementedException("ACIDS algorithm was not implemented into this version of LIMES.");
    }

    @Override
    protected MLResults learn(AMapping trainingData) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected MLResults learn(PseudoFMeasure pfm) throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException(this.getName());
    }

    @Override
    protected AMapping predict(ACache source, ACache target, MLResults mlModel) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean supports(MLImplementationType mlType) {
        return mlType == MLImplementationType.SUPERVISED_BATCH || mlType == MLImplementationType.SUPERVISED_ACTIVE;
    }

    @Override
    protected AMapping getNextExamples(int size) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected MLResults activeLearn(AMapping oracleMapping) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDefaultParameters() {
        // TODO Auto-generated method stub

    }

    @Override
    protected MLResults activeLearn() throws UnsupportedMLImplementationException {
        throw new UnsupportedMLImplementationException(this.getName());
    }


}
