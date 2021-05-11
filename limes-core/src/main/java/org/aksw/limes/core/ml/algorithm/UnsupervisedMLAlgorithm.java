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
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;

import java.lang.reflect.Constructor;

/**
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 */
public class UnsupervisedMLAlgorithm extends AMLAlgorithm {

    public final static MLImplementationType ML_IMPLEMENTATION_TYPE = MLImplementationType.UNSUPERVISED;

    /**
     * @param clazz the core ML algorithm class
     * @throws UnsupportedMLImplementationException Exception
     */
    public UnsupervisedMLAlgorithm(Class<? extends ACoreMLAlgorithm> clazz) throws UnsupportedMLImplementationException {

        try {
            Constructor<? extends ACoreMLAlgorithm> ctor = clazz.getDeclaredConstructor();
            setMl(ctor.newInstance());
        } catch (Exception e) {
            throw new UnsupportedMLImplementationException(getMl().getName());
        }

        if (!getMl().supports(ML_IMPLEMENTATION_TYPE)) {
            throw new UnsupportedMLImplementationException(getMl().getName());
        }

    }

    /**
     * @param pfm the pseudo f-measure
     * @return wrap with results
     * @throws UnsupportedMLImplementationException Exception
     */
    public MLResults learn(PseudoFMeasure pfm) throws UnsupportedMLImplementationException {
        return getMl().learn(pfm);
    }


}
