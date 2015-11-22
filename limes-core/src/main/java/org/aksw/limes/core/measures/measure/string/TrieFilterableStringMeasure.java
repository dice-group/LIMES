package org.aksw.limes.core.measures.measure.string;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.LinkedList;

/**
 * Created by kvn on 28/09/15.
 */
public interface TrieFilterableStringMeasure extends IStringMeasure {

    double characterFrequencyUpperBound(int l1, int l2, int m);

    int characterMatchLowerBound(int l1, int l2, double threshold);

    int lengthUpperBound(int l1, double threshold);

    int lengthLowerBound(int l1, double threshold);

    LinkedList<ImmutableTriple<Integer, Integer, Integer>> getPartitionBounds(int maxSize, double threshold);

    double proximity(String a, String b);
}
