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
package org.aksw.limes.core.measures.mapper.string.triefilter;

import java.util.List;

/**
 * Quicksort implementation to sort strings according to their lengths, not
 * alphabetically.
 */

public class LengthQuicksort {

    /**
     * Public class method to quicksort lists
     *
     * @param values
     *         list of strings
     */
    public static void sort(List<String> values) {
        if (values == null || values.size() == 0)
            return;
        quicksort(values, 0, values.size() - 1);
    }

    /**
     * Quicksort class method
     *
     * @param strings
     *         list of strings
     * @param low
     *         low index
     * @param high
     *         high index
     */
    private static void quicksort(List<String> strings, int low, int high) {
        int i = low, j = high;
        int pivot = strings.get(low + (high - low) / 2).length();
        while (i <= j) {
            while (strings.get(i).length() < pivot)
                i++;
            while (strings.get(j).length() > pivot)
                j--;
            if (i <= j) {
                exchange(strings, i, j);
                i++;
                j--;
            }
        }
        if (low < j)
            quicksort(strings, low, j);
        if (i < high)
            quicksort(strings, i, high);
    }

    /**
     * Swap elements
     *
     * @param strings
     *         list of strings
     * @param i
     *         index of element a to be swapped
     * @param j
     *         index of element b to be swapped
     */
    private static void exchange(List<String> strings, int i, int j) {
        String temp = strings.get(i);
        strings.set(i, strings.get(j));
        strings.set(j, temp);
    }
}