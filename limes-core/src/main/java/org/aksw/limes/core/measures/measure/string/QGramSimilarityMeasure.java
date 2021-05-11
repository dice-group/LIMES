/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.string;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.mapper.string.fastngram.ITokenizer;
import org.aksw.limes.core.measures.mapper.string.fastngram.NGramTokenizer;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class QGramSimilarityMeasure extends StringMeasure {

    ITokenizer tokenizer;
    int q = 3;

    public QGramSimilarityMeasure(int q) {
        tokenizer = new NGramTokenizer();
    }

    public QGramSimilarityMeasure() {
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
        // create a kopy of X
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
        double max = 0;
        double sim = 0;
        for (String source : instance1.getProperty(property1)) {
            for (String target : instance2.getProperty(property2)) {
                sim = getSimilarity(source, target);
                if (sim > max) {
                    max = sim;
                }
            }
        }
        return max;
    }

    public String getName() {
        return "qgrams";
    }

    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }
}
