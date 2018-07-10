package org.aksw.limes.core.io.mapping;

import java.util.HashMap;

/**
 * @author Mohamed Sherif {@literal <}sherif {@literal @} informatik.uni-leipzig.de{@literal >}
 * @version Nov 12, 2015
 */
public class HybridMapping extends AMapping {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4230353331396453801L;

	public HybridMapping reverseSourceTarget() {
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
    public AMapping getBestOneToNMapping() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AMapping getSubMap(double threshold) {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public int getNumberofPositiveMappings() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AMapping getOnlyPositiveExamples() {
		// TODO Auto-generated method stub
		return null;
	}

}
