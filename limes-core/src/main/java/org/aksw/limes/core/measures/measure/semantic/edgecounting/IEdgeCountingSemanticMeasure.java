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
package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import edu.mit.jwi.item.ISynset;
import org.aksw.limes.core.measures.measure.IMeasure;

/**
 * Implements the edge-counting semantic string similarity interface.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public interface IEdgeCountingSemanticMeasure extends IMeasure {

    /**
     * Calculates the semantic similarity between two concepts.
     *
     * @param synset1,
     *            the first input concept
     * @param synset2,
     *            the second input concept
     * @return the semantic similarity of two concepts
     */

    public double getSimilarityBetweenConcepts(ISynset synset1, ISynset synset2);

}