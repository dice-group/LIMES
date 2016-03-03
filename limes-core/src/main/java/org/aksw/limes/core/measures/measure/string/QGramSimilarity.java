/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.string;


import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.mapper.atomic.fastngram.NGramTokenizer;
import org.aksw.limes.core.measures.mapper.atomic.fastngram.Tokenizer;

import java.util.HashSet;
import java.util.Set;


/**
 *
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

    public static void main(String args[]) {
        System.out.println(new QGramSimilarity().getSimilarity("abcd", "abcde"));
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

    public double getSimilarity(Object a, Object b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getType() {
        return "string";
    }

    public double getSimilarity(Instance a, Instance b, String property1, String property2) {
        double value = 0;
        double sim = 0;
        for (String source : a.getProperty(property1)) {
            for (String target : b.getProperty(property2)) {
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
        return mappingSize/1000d;
    }
}
