package org.aksw.limes.core.measures.measure.string;

import org.aksw.limes.core.io.cache.Instance;
import org.apache.commons.codec.language.ColognePhonetic;

public class KoelnPhoneticMeasure extends StringMeasure {

    public static final int codeLength = 4;
    
    private static boolean charAt(String s, int i, char ... attr) {
    	if ((s.length()>i && i > -1)) {
    		char check = s.charAt(i);
    		for(char c : attr) {
    			if (c == check)
    				return true;
    		}
    	}
    	return false;
    }
    
    public static String getCode(String string) {
    	
    	ColognePhonetic co = new ColognePhonetic();
    	
    	return co.encode(string);
    	
    	/*
    	string = string.toUpperCase();
    	String code = "";
    	
    	for(int i = 0; i<string.length(); i++) {
    		switch (string.charAt(i)) {
			case 'A':
			case 'E':
			case 'I':
			case 'J':
			case 'O':
			case 'U':
			case 'Y':
				code += '0';
				break;
			
			case 'H':
				break;
			case 'B':
				code += '1';
				break;
			case 'P':
				if (!(charAt(string, i+1, 'H')))
					code +='1';
				else
					code +='3';
				break;
			case 'D':
			case 'T':
				if ((charAt(string, i+1, 'C', 'S', 'Z')))
					code +='8';
				else
					code +='2';
				
				break;
			case 'F':
			case 'V':
			case 'W':
				code += '3';
				break;
			case 'G':
			case 'K':
			case 'Q':
				code +='4';
				break;
			case 'C':
				if(charAt(string, i-1, 'S', 'Z'))
					code +='8';
				else if (i <3)
					if (charAt(string, i+1, 'A', 'H', 'K', 'L', 'O', 'Q', 'R', 'U', 'X'))
						code +='4';
					else
						code +='8';
				else if(charAt(string, i+1, 'A', 'H', 'K', 'O', 'Q', 'U', 'X'))
					code +='4';
				else
					code +='8';
				break;
			case 'X':
				if (charAt(string, i-1, 'C', 'K', 'Q'))
					code +="48";
				else
					code +='8';
				break;
			case 'L':
				code += '5';
				break;
			case 'M':
			case 'N':
				code += '6';
				break;
			case 'R':
				code += '7';
				break;	
			case 'S':
			case 'Z':
				code += '8';
				break;
			default:
				break;
			}
    	}
    	if(code.charAt(0)==0) {
    		code = code.replaceAll("0", "");
    		code = '0' + code;
    	}else {
    		code = code.replaceAll("0", "");
    	}
    	if(code.equals("")) {
    		code = "0";
    	}
    	return code;*/
    }

    public double proximity(String s1, String s2) {
        char[] c1, c2;
        c1 = KoelnPhoneticMeasure.getCode(s1).toCharArray();
        c2 = KoelnPhoneticMeasure.getCode(s2).toCharArray();
        double distance = 0d;
        for (int i = 0; i < c1.length; i++)
            if (c1[i] != c2[i])
                distance += 1d;
        return (1.0d - (distance / (double) c1.length));
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
        return "koeln";
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }

}
