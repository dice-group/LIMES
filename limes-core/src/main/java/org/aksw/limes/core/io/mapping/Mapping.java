package org.aksw.limes.core.io.mapping;

import java.util.HashMap;
import java.util.TreeSet;

import com.hp.hpl.jena.vocabulary.OWL;

/**
 * @author ngonga
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-24
 *
 */
public abstract class Mapping implements IMapping {

    protected HashMap<String, HashMap<String, Double>> map;
    protected HashMap<Double, HashMap<String, TreeSet<String>>> reversedMap;
    protected int size;
    protected String predicate = OWL.sameAs.getURI();

    public abstract double getConfidence(String key, String value);

    public abstract void add(String key, String value, double confidence);

    public abstract void add(String key, HashMap<String, Double> hashMap);

    public abstract int size();

    public abstract Mapping reverseSourceTarget();

    public abstract int getNumberofMappings();

    public abstract boolean contains(String key, String value);

    public abstract Mapping getBestOneToNMapping();

    /**
     * Get the predicate URI, which defaults to OWL.sameAs.
     * 
     * @return the predicate URI
     */
    public Mapping() {
	map = new HashMap<String, HashMap<String, Double>>();
	reversedMap = new HashMap<Double, HashMap<String, TreeSet<String>>>();
	size = 0;
    }

    public String getPredicateURI() {
	return OWL.sameAs.getURI();
    }

    public HashMap<String, HashMap<String, Double>> getMap() {
	return map;
    }

    public void setMap(HashMap<String, HashMap<String, Double>> map) {
	this.map = map;
    }

    public int getSize() {
	return size;
    }

    public void setSize(int size) {
	this.size = size;
    }

    public String getPredicate() {
	return predicate;
    }

    public void setPredicate(String mappingPredicate) {
	this.predicate = mappingPredicate;
    }

}
