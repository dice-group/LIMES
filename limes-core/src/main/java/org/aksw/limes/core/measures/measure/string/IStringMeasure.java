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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.aksw.limes.core.measures.measure.string;

import org.aksw.limes.core.measures.measure.IMeasure;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public interface IStringMeasure extends IMeasure {
    /**
     * Length of prefix to consider when mapping the input string with other
     * strings.
     *
     * @param tokensNumber
     *         Size of input string in
     * @param threshold
     *         Similarity threshold
     * @return Prefix length
     */
    public int getPrefixLength(int tokensNumber, double threshold);

    /**
     * Theshold for the length of the tokens to be indexed
     *
     * @param tokensNumber
     *         Number of tokens of current input
     * @param threshold
     *         Similarity threshold
     * @return Length of tokens to be indexed
     */
    public int getMidLength(int tokensNumber, double threshold);

    public double getSizeFilteringThreshold(int tokensNumber, double threshold);

    /**
     * Threshold for the positional filtering
     *
     * @param xTokensNumber
     *         Size of the first input string
     * @param yTokensNumber
     *         Size of the first input string
     * @param threshold
     *         Similarity threshold
     * @return Threshold for positional filtering
     */
    public int getAlpha(int xTokensNumber, int yTokensNumber, double threshold);

    /**
     * Returns the similarity of two strings given their length and the overlap.
     * Useful when these values are known so that no computation of known values
     * have to be carried out anew
     *
     * @param overlap
     *         Overlap of strings A and B
     * @param lengthA
     *         Length of A
     * @param lengthB
     *         Length of B
     * @return Similarity of A and B
     */
    public double getSimilarity(int overlap, int lengthA, int lengthB);

    /**
     * Returns true if this similarity function can be computed just via the
     * getSimilarity(overlag, lengthA, lengthB)
     *
     * @return True if it's possible, else false;
     */
    public boolean computableViaOverlap();

}
