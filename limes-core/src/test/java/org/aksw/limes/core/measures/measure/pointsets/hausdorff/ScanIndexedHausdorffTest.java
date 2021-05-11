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
package org.aksw.limes.core.measures.measure.pointsets.hausdorff;


import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 */
public class ScanIndexedHausdorffTest {

    @Test
    public void test() {

        Point a1 = new Point("a1", Arrays.asList(new Double[]{1.0, 1.0}));
        Point b1 = new Point("b1", Arrays.asList(new Double[]{1.0, 2.0}));
        Point c1 = new Point("c1", Arrays.asList(new Double[]{2.0, 1.0}));
        Polygon A = new Polygon("A", Arrays.asList(new Point[]{a1, b1, c1}));

        Point a2 = new Point("a2", Arrays.asList(new Double[]{3.0, 1.0}));
        Point b2 = new Point("b2", Arrays.asList(new Double[]{3.0, 2.0}));
        Point c2 = new Point("c2", Arrays.asList(new Double[]{2.0, 2.0}));
        Polygon B = new Polygon("B", Arrays.asList(new Point[]{a2, b2, c2}));

        Set<Polygon> testSet = new HashSet<Polygon>();
        testSet.add(A);
        testSet.add(B);

        //ScanIndexedHausdorffMeasure c = new ScanIndexedHausdorffMeasure();
        // logger.info("{}",c.run(testSet, testSet, 0.1.0));

    }

}
