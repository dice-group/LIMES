package org.aksw.limes.core.measures.measure.string;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.io.cache.Instance;

public class DoubleMetaphoneMeasure extends StringMeasure {
	


    public static List<String> getCode(String string) {
    	ArrayList<String> map = new ArrayList<String>();
    	
    	EDoubleMetaphone ephone = new EDoubleMetaphone(string);
    	map.add(ephone.getDoubleMetaphoneRepresentation());
    	map.add(ephone.getDoubleMetaphoneRepresentation2());
    	
        return map;
    }

    private double getProximity(char[] s1, char[] s2) {
        int shorter;
        int longer;
        if (s1.length>s2.length) {
        	shorter = s2.length; 
        	longer = s1.length;
        }else {
        	shorter =  s1.length;
        	longer = s2.length;
        }
        double distance = 0d;
        for (int i = 0; i < shorter; i++)
            if (s1[i] != s2[i])
                distance += 1d;
        return (1.0d - (distance / (double) longer));
    }
    
    public double proximity(String s1, String s2) {
    	List<String> map1 = DoubleMetaphoneMeasure.getCode(s1);
    	List<String> map2 = DoubleMetaphoneMeasure.getCode(s2);
    	double proximity = 0d;
    	double check = 0d;
    	for (String k1: map1) {
    		for(String k2: map2) {
    			check = getProximity(k1.toCharArray(), k2.toCharArray());
    			if (check > proximity) {
    				proximity = check;
    			}
    		}
    	}
        return proximity;
    }

    @Override
    public int getPrefixLength(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMidLength(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getSizeFilteringThreshold(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAlpha(int xTokensNumber, int yTokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getSimilarity(int overlap, int lengthA, int lengthB) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean computableViaOverlap() {
        return false;
    }

    @Override
    public double getSimilarity(Object object1, Object object2) {
        return proximity(object1.toString(), object2.toString());
    }

    @Override
    public String getType() {
        return "string";
    }

    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        double value = 0;
        double sim = 0;
        for (String source : instance1.getProperty(property1)) {
            for (String target : instance2.getProperty(property2)) {
                sim = proximity(source, target);
                if (sim > value) {
                    value = sim;
                }
            }
        }
        return sim;
    }

    @Override
    public String getName() {
        return "doublemeta";
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }

}
