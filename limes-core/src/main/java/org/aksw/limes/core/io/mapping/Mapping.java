package org.aksw.limes.core.io.mapping;

import java.util.HashMap;

import com.hp.hpl.jena.vocabulary.OWL;

/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-09
 *
 */
public abstract class Mapping implements IMapping {

    // FIXME why public fields?
    public HashMap<String, HashMap<String, Double>> map;
    // FIXME why public fields?
    public int size;

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
    public String getPredicateURI() {
	return OWL.sameAs.getURI();
    };

}
