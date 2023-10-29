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
package org.aksw.limes.core.util;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Class to help creating sets.
 *
 * @author Klaus Lyko
 */
public class SetUtilities {
//	/**
//	 * Generates all possible sets of elements T.
//	 * Warning this function is in O(2^n)!
//	 * @param originalSet
//	 * @return Set<Set<T>>
//	 */
//	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
//	    Set<Set<T>> sets = new HashSet<Set<T>>();
//	    if (originalSet.isEmpty()) {
//	    	sets.add(new HashSet<T>());
//	    	return sets;
//	    }
//	    List<T> list = new ArrayList<T>(originalSet);
//	    T head = list.get(0);
//	    Set<T> rest = new HashSet<T>(list.subList(1, list.size())); 
//	    for (Set<T> set : powerSet(rest)) {
//	    	Set<T> newSet = new HashSet<T>();
//	    	newSet.add(head);
//	    	newSet.addAll(set);
//	    	sets.add(newSet);
//	    	sets.add(set);
//	    }		
//	    return sets;
//	}
//	

    /**
     * Generates all possible sets of elements T of the given size.
     *
     * @param originalSet
     *         Set of elements to generate powerset upon.
     * @param size
     *         Size the generated sets should have.
     * @return All possible sets of elements T of size size.
     */
    public static <T> Set<Set<T>> sizeRestrictPowerSet(Set<T> originalSet, int size) {
        Set<Set<T>> result = new HashSet<Set<T>>();
        if (size >= originalSet.size()) {
            HashSet<T> set = new HashSet<T>();
            set.addAll(originalSet);
            result.add(set);
            return result;
        }
        //size = 1
        for (T elem : originalSet) {
            HashSet<T> set = new HashSet<T>();
            set.add(elem);
            result.add(set);
        }
        int actSize = 1;
        while (actSize < size) {
            Set<Set<T>> result2 = new HashSet<Set<T>>();
            for (Set<T> set : result) {
                // create new set for each number
                for (T elem : originalSet) {
                    if (!set.contains(elem)) {
                        HashSet<T> newSet = new HashSet<T>();
                        newSet.addAll(set);
                        newSet.add(elem);
                        result2.add(newSet);
                    }
                }
            }
            result = result2;
            actSize++;
        }
        return result;
    }


//	/**
//	 * Generates all possible sets of elements T of the given size.
//	 * Warning it works in O(n^2)
//	 * @param originalSet Set of elements to generate powerset upon.
//	 * @param size Size the generated sets should have.
//	 * @return All possible sets of elements T of size size.
//	 */
//	public static <T> Set<Set<T>> sizePowerSets(Set<T> originalSet, int size) {
//		Set<Set<T>> result = new HashSet<Set<T>>();
//		for(Set<T> set : powerSet(originalSet)) {
//			if(set.size() == size)
//				result.add(set);
//		}
//		return result;
//	}

    @Test
    public void testsizeRestrictPowerSet() {
        Set<Integer> allInts = new HashSet<Integer>();
        for (int i = 0; i < 7; i++)
            allInts.add(i);
        Set<Set<Integer>> result = sizeRestrictPowerSet(allInts, 3);
        for (Set<Integer> set : result) {
            System.out.println(set);
        }
        assertTrue(result.size() == 35);// binom(7, 3) = 35
    }

}
