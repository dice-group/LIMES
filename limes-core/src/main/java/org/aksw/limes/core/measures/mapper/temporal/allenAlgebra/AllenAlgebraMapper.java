package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.measures.mapper.AMapper;

/**
 * Abstract class of Allen's temporal relations mapper. It computes basic
 * functions between uris. For more information about Allen's Algebra @see
 * https://en.wikipedia.org/wiki/Allen's_interval_algebra
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public abstract class AllenAlgebraMapper extends AMapper implements IAllenAlgebraMapper {
    /**
     * List of atomic relations required to compute a complex Allen temporal
     * relation.
     * 
     */
    private ArrayList<Integer> requiredAtomicRelations = new ArrayList<Integer>();

    /**
     * Performs union between two sets of uris.
     *
     * @param set1,
     *            first set of uris
     * @param set2,
     *            second set of uris
     * @return the union of set1 and set2
     */
    protected static Set<String> union(Set<String> set1, Set<String> set2) {
        Set<String> temp = new HashSet<String>(set1);
        temp.addAll(new HashSet<String>(set2));
        return temp;

    }

    /**
     * Performs intersection between two sets of uris.
     *
     * @param set1,
     *            first set of uris
     * @param set2,
     *            second set of uris
     * @return the intersection of set1 and set2
     */
    protected static Set<String> intersection(Set<String> set1, Set<String> set2) {
        Set<String> temp = new HashSet<String>(set1);
        temp.retainAll(new HashSet<String>(set2));
        return temp;

    }

    /**
     * Performs difference between two sets of uris.
     *
     * @param set1,
     *            first set of uris
     * @param set2,
     *            second set of uris
     * @return the difference of set1 and set2
     */
    protected static Set<String> difference(Set<String> set1, Set<String> set2) {
        Set<String> temp = new HashSet<String>(set1);
        temp.removeAll(new HashSet<String>(set2));
        return temp;

    }

    /**
     * Returns the set of atomic relations.
     *
     * @return requiredAtomicRelations
     */
    public ArrayList<Integer> getRequiredAtomicRelations() {
        return requiredAtomicRelations;
    }
}
