package org.aksw.limes.core.ml;

import java.util.HashMap;

import org.aksw.limes.core.io.mapping.Mapping;

/**
 * Set of links sharing the same predicate URI. The so-called
 * "new version of Mapping".
 * 
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-09
 *
 */
public class Linkset extends Mapping {

	private String predicateURI;

	public Linkset(String predicateURI) {
		// TODO Auto-generated constructor stub
		super();
		this.predicateURI = predicateURI;
	}

	@Override
	public String getPredicate() {
		return predicateURI;
	}

	@Override
	public double getConfidence(String key, String value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void add(String key, String value, double confidence) {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(String key, HashMap<String, Double> hashMap) {
		// TODO Auto-generated method stub

	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Linkset reverseSourceTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberofMappings() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean contains(String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Linkset getBestOneToNMapping() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mapping getSubMap(double threshold) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
