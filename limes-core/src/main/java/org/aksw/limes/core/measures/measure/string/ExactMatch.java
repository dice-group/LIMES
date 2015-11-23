/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.string;

<<<<<<< HEAD
import org.aksw.limes.core.data.Instance;
=======
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
>>>>>>> 04f229403216e5956dd16f2b2e0519c2b5ae47d3
/**
 *
 * @author ngonga
 */
public class ExactMatch extends StringMeasure {

    public int getPrefixLength(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getMidLength(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getSizeFilteringThreshold(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getAlpha(int xTokensNumber, int yTokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getSimilarity(int overlap, int lengthA, int lengthB) {
        if(overlap == lengthA && lengthA == lengthB) return 1d;
        return 0d;
    }

    public boolean computableViaOverlap() {
        return true;
    }

    public double getSimilarity(Object a, Object b) {
        if((a+"").equals(b+"")) return 1d;
        return 0d;
    }

    public String getType() {
        return "string";
    }

    public double getSimilarity(Instance a, Instance b, String property1, String property2) {
        for(String source: a.getProperty(property1))
        {
            for(String target: b.getProperty(property2))
            {
                if(source.equals(target))
                    return 1d;
            }
        
        }
        return 0d;
    }

    public String getName() {
        return "exactMatch";
    }

    public double getRuntimeApproximation(double mappingSize) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
