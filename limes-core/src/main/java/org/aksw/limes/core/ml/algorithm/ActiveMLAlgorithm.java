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

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.mapping.AMapping;

import java.lang.reflect.Constructor;

/**
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 */
public class ActiveMLAlgorithm extends AMLAlgorithm {

    public final static MLImplementationType ML_IMPLEMENTATION_TYPE = MLImplementationType.SUPERVISED_ACTIVE;

    /**
     * @param clazz
     *            the CoreMLAlgorithm class
     * @throws UnsupportedMLImplementationException
     *             if ML implementation is not supported
     */
    public ActiveMLAlgorithm(Class<? extends ACoreMLAlgorithm> clazz) throws UnsupportedMLImplementationException {

        try {
            final Constructor<? extends ACoreMLAlgorithm> ctor = clazz.getDeclaredConstructor();
            this.setMl(ctor.newInstance());
        } catch (final Exception e) {
            throw new UnsupportedMLImplementationException(this.getMl().getName());
        }

        if (!this.getMl().supports(ML_IMPLEMENTATION_TYPE)) {
            throw new UnsupportedMLImplementationException(this.getMl().getName());
        }

    }

    /**
     * @param size
     *            number of examples to return
     * @return the mapping
     * @throws UnsupportedMLImplementationException
     *             if ML implementation is not supported
     */
    public AMapping getNextExamples(int size) throws UnsupportedMLImplementationException {
        return this.getMl().getNextExamples(size);
    }

    /**
     * @return wrap with results
     * @throws UnsupportedMLImplementationException
     *             Exception
     */
    public MLResults activeLearn() throws UnsupportedMLImplementationException {
        return this.getMl().activeLearn();
    }

    /**
     * @param oracleMapping
     *            mapping from the oracle
     * @return wrap with results
     * @throws UnsupportedMLImplementationException
     *             if ML implementation is not supported
     */
    public MLResults activeLearn(AMapping oracleMapping) throws UnsupportedMLImplementationException {
        return this.getMl().activeLearn(oracleMapping);
    }

}
