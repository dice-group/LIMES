/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.string;


import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.mapper.string.fastngram.NGramTokenizer;
import org.aksw.limes.core.measures.mapper.string.fastngram.Tokenizer;


/**
 * @author ngonga
 */
public class QGramSimilarity extends StringMeasure {

    Tokenizer tokenizer;
    int q = 3;

    public QGramSimilarity(int q) {
        tokenizer = new NGramTokenizer();
    }

    public QGramSimilarity() {
        tokenizer = new NGramTokenizer();
    }

    public static void main(String args[]) {
        System.out.println(new QGramSimilarity().getSimilarity("abcd", "abcde"));
    }

    public double getSimilarity(String x, String y) {
        Set<String> yTokens = tokenizer.tokenize(y, q);
        Set<String> xTokens = tokenizer.tokenize(x, q);
        return getSimilarity(xTokens, yTokens);
    }

    public double getSimilarity(Set<String> X, Set<String> Y) {
        double x = (double) X.size();
        double y = (double) Y.size();
        //create a kopy of X
        Set<String> K = new HashSet<String>();
        for (String s : X) {
            K.add(s);
        }
        K.retainAll(Y);
        double z = (double) K.size();
        return z / (x + y - z);
    }

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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean computableViaOverlap() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getSimilarity(Object object1, Object object2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getType() {
        return "string";
    }

    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        double value = 0;
        double sim = 0;
        for (String source : instance1.getProperty(property1)) {
            for (String target : instance2.getProperty(property2)) {
                sim = getSimilarity(source, target);
                if (sim > value) {
                    value = sim;
                }
            }
        }
        return sim;
    }

    public String getName() {
        return "qgrams";
    }

    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }
}
