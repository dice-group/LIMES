/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.string;

import org.aksw.limes.core.data.Instance;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.measures.measure.IMeasure;
import java.util.TreeSet;

/**
 *
 * @author ngonga
 */
public class TrigramMeasure extends StringMeasure {

    public double getSimilarity(int overlap, int lengthA, int lengthB) {
        return ((double) 2 * overlap) / (double) (lengthA + lengthB);
    }

    public String getType() {
        return "string";
    }

    // need to set back p1 to p2 and p2 to p1
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
        return "Trigram";
    }

    public double getSimilarity(Object a, Object b) {
        String p1 = "  "+ a + "  ";
        String p2 = "  "+ b + "  ";

        if(p1.length() == 4 && p2.length() == 4) return 1.0;
        if((p1.length() == 4 && p2.length() > 4) || (p2.length() == 4 && p1.length() > 4)) return 0.0;
        TreeSet<String> t1 = getTrigrams(p1);
        TreeSet<String> t2 = getTrigrams(p2);
        double counter = 0;
        for(String s: t1)
        {
            if(t2.contains(s)) counter++;
        }
        return 2*counter/(t1.size() + t2.size());
    }

    public TreeSet<String> getTrigrams(String a) {
        TreeSet<String> result = new TreeSet<String>();
        String copy = a;

            for (int i = 2; i < copy.length(); i++) {
                result.add(copy.substring(i-2, i));
            }
        return result;
    }

    public int getPrefixLength(int tokensNumber, double threshold) {
        int k = 1;
        if(threshold == 0) k = 0;
        
        return (tokensNumber - (int) Math.ceil((float) (tokensNumber * threshold / (2 - threshold))) + k);
    }

    public int getMidLength(int tokensNumber, double threshold) {
        int k = 1;
        if(threshold == 0) k = 0;
        
        return (tokensNumber - (int) Math.ceil((float) (tokensNumber * threshold)) + k);
    }

    public double getSizeFilteringThreshold(int tokensNumber, double threshold) {
        return (double) (threshold / (2 - threshold) * tokensNumber);
    }

    public int getAlpha(int xTokensNumber, int yTokensNumber, double threshold) {
        return (int) Math.ceil((float) (threshold / 2 * (xTokensNumber + yTokensNumber)));
    }

    public static void main(String args[])
    {
        MemoryCache source = new MemoryCache();
        MemoryCache target = new MemoryCache();
        source.addTriple("S1", "pub", "Perth Airport");
        source.addTriple("S1", "conf", "conf1");
        source.addTriple("S2", "pub", "test2");
        source.addTriple("S2", "conf", "conf2");

        target.addTriple("S1", "pub", "Perth International Airport");
        target.addTriple("S1", "conf", "conf1");
        target.addTriple("S3", "pub", "test3");
        target.addTriple("S3", "conf", "conf3");

        System.out.println(new TrigramMeasure().getSimilarity(source.getInstance("S1"), target.getInstance("S3"), "pub", "pub"));
    }

    public boolean computableViaOverlap() {
        return true;
    }

    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize/10000d;
    }
}
