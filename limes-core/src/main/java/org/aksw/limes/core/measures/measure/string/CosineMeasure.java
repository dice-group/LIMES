/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.string;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;

import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;

/**
 *
 * @author ngonga
 */
public class CosineMeasure extends StringMeasure {

    public double getSimilarityChar(Object a, Object b) {
	String p1 = a + "";
	String p2 = b + "";

	String r1 = "";
	for (int i = 0; i < p1.length(); i++) {
	    r1 = r1 + " " + p1.charAt(i);
	}
	r1 = r1.trim();
	// System.out.println("<"+r1+">");

	String r2 = "";
	for (int i = 0; i < p2.length(); i++) {
	    r2 = r2 + " " + p2.charAt(i);
	}
	r2 = r2.trim();
	// System.out.println("<"+r2+">");
	return new CosineSimilarity().getSimilarity(r1, r2);
    }

    public double getSimilarity(Object a, Object b) {
	return new CosineSimilarity().getSimilarity(a + "", b + "");
    }

    public String getType() {
	return "string";
    }

    public double getSimilarity(Instance a, Instance b, String property1, String property2) {
	double sim = 0;
	double max = 0;
	for (String p1 : a.getProperty(property1)) {
	    for (String p2 : b.getProperty(property2)) {
		sim = getSimilarity(p1, p2);
		if (max < sim) {
		    max = sim;
		}
	    }
	}
	return max;
    }

    public String getName() {
	return "Cosine";
    }

    public double getSimilarity(int overlap, int lengthA, int lengthB) {
	return overlap / Math.sqrt(lengthA * lengthB);
    }

    public boolean computableViaOverlap() {
	return true;
    }

    public int getPrefixLength(int tokensNumber, double threshold) {
	int k = 1;
	if (threshold == 0) {
	    k = 0;
	}
	return (tokensNumber - (int) Math.ceil((float) (tokensNumber * threshold * threshold)) + k);
    }

    public int getMidLength(int tokensNumber, double threshold) {
	int k = 1;
	if (threshold == 0) {
	    k = 0;
	}
	return (tokensNumber - (int) Math.ceil((float) (tokensNumber * threshold)) + k);
    }

    public double getSizeFilteringThreshold(int tokensNumber, double threshold) {
	return Math.ceil((double) (threshold * threshold * tokensNumber));
    }

    /**
     * Threshold for the positional filtering
     *
     * @param xTokensNumber
     *            Size of the first input string
     * @param yTokensNumber
     *            Size of the first input string
     * @param threshold
     *            Similarity threshold
     * @return Threshold for positional filtering
     */
    public int getAlpha(int xTokensNumber, int yTokensNumber, double threshold) {
	return (int) Math.ceil((float) (threshold * Math.sqrt(xTokensNumber * yTokensNumber)));
    }

    public double getRuntimeApproximation(double mappingSize) {
	return mappingSize / 1000d;
    }

}
