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
package org.aksw.limes.core.util;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class RandomStringGenerator implements DataGenerator {

    int minLength, maxLength;
    double mean = 0d;
    double stdDev = 0d;

    public RandomStringGenerator(int minLength, int maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    /**
     * Generates strings that are in (a-zA-Z)*
     *
     * @param size
     *         Size of the corpus that is to be generated
     * @return Corpus
     */
    public ACache generateData(int size) {
        ACache c = new MemoryCache();
        String s;
        List<Double> lengths = new ArrayList<Double>();
        while (c.size() < size) {
            s = generateString();
            lengths.add((double) s.length());
            c.addTriple(s, DataGenerator.LABEL, s);
        }
        stdDev = Utils.getStandardDeviation(lengths);
        mean = Utils.getMean(lengths);

        return c;
    }

    public String generateString() {
        String s = "";
        int length = minLength + (int) (Math.random() * (maxLength - minLength));
        for (int j = 0; j < length; j++) {
            s = s + (char) (97 + (int) 26 * Math.random());
        }
        return s;
    }

    public String getName() {
        return "randomString";
    }

    public double getMean() {
        return mean;
    }

    public double getStandardDeviation() {
        return stdDev;
    }

}
