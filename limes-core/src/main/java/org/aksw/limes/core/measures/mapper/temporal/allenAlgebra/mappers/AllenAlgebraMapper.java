package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.mappers;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.Instance;

public abstract class AllenAlgebraMapper implements IAllenAlgebraMapper {

    private ArrayList<Integer> requiredAtomicRelations = new ArrayList<Integer>();
    /**
     * Returns the set of atomic relations.
     * 
     * @return requiredAtomicRelations
     */
    public ArrayList<Integer> getRequiredAtomicRelations() {
	return requiredAtomicRelations;
    }
  
    /**
     * Performs union between two sets of uris.
     * 
     * @param set1,
     *            first set of uris
     * 
     * @param set2,
     *            second set of uris
     * 
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
     * 
     * @param set2,
     *            second set of uris
     * 
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
     * 
     * @param set2,
     *            second set of uris
     * 
     * @return the difference of set1 and set2
     */
    protected static Set<String> difference(Set<String> set1, Set<String> set2) {
	Set<String> temp = new HashSet<String>(set1);
	temp.removeAll(new HashSet<String>(set2));
	return temp;

    }
   

    

}
