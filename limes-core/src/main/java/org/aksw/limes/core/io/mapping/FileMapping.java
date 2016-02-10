package org.aksw.limes.core.io.mapping;

import java.util.HashMap;

/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 12, 2015
 */
public class FileMapping extends Mapping {

    public HashMap<String, HashMap<String, Double>> map;

    public int getNumberofMappings() {
	// TODO Auto-generated method stub
	return 0;
    }

    public FileMapping reverseSourceTarget() {
	// TODO Auto-generated method stub
	return null;
    }

    public int size() {
	// TODO Auto-generated method stub
	return 0;
    }

    public void add(String key, String value, Double double1) {
	// TODO Auto-generated method stub
	
    }

    public void add(String key, HashMap<String, Double> hashMap) {
	// TODO Auto-generated method stub
	
    }

    public double getConfidence(String key, String value) {
	// TODO Auto-generated method stub
	return 0.0d;
    }

    @Override
    public void add(String key, String value, double sim) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public boolean contains(String key, String value) {
	// TODO Auto-generated method stub
	return false;
    }

	@Override
	public Mapping getBestOneToNMapping() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mapping getSubMap(double threshold) {
		// TODO Auto-generated method stub
		return null;
	}



}
