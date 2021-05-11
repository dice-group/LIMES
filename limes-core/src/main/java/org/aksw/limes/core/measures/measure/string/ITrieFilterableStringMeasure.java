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
package org.aksw.limes.core.measures.measure.string;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.LinkedList;

/**
 * Created by kvn on 28/09/15.
 */
public interface ITrieFilterableStringMeasure extends IStringMeasure {

    double characterFrequencyUpperBound(int l1, int l2, int m);

    int characterMatchLowerBound(int l1, int l2, double threshold);

    int lengthUpperBound(int l1, double threshold);

    int lengthLowerBound(int l1, double threshold);

    LinkedList<ImmutableTriple<Integer, Integer, Integer>> getPartitionBounds(int maxSize, double threshold);

    double proximity(String a, String b);
}
