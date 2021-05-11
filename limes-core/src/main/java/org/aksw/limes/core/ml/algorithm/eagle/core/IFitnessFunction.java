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
package org.aksw.limes.core.ml.algorithm.eagle.core;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.jgap.gp.IGPProgram;
/**
 * Basic interface for EAGLEs fitness functions
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 *
 */
public interface IFitnessFunction {
    /**
     * To calculate Mappings based on LS
     * @param sourceCache
     * @param targetCache
     * @param spec
     * @return
     */
    public AMapping getMapping(ACache sourceCache, ACache targetCache, LinkSpecification spec);

    public double calculateRawFitness(IGPProgram p);

    public double calculateRawMeasure(IGPProgram bestHere);
}
