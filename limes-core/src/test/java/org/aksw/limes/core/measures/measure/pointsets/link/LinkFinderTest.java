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
package org.aksw.limes.core.measures.measure.pointsets.link;


import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 */
public class LinkFinderTest {

    @Test
    public void test() {

        Point a1 = new Point("a1", Arrays.asList(new Double[]{1.0, 1.0}));
        Point b1 = new Point("b1", Arrays.asList(new Double[]{1.0, 2.0}));
        Point c1 = new Point("c1", Arrays.asList(new Double[]{2.0, 1.0}));
        Polygon A = new Polygon("A", Arrays.asList(new Point[]{a1, b1, c1}));

        Point a2 = new Point("a2", Arrays.asList(new Double[]{3.0, 1.0}));
        Point b2 = new Point("b2", Arrays.asList(new Double[]{3.0, 2.0}));
        Point c2 = new Point("c2", Arrays.asList(new Double[]{2.0, 2.0}));
        Point d2 = new Point("d2", Arrays.asList(new Double[]{2.0, 7.0}));
        Point e2 = new Point("e2", Arrays.asList(new Double[]{5.0, 2.0}));
        Point f2 = new Point("f2", Arrays.asList(new Double[]{2.0, 5.0}));
        Point g2 = new Point("g2", Arrays.asList(new Double[]{6.0, 7.0}));
        Point h2 = new Point("h2", Arrays.asList(new Double[]{5.0, 7.0}));
        Point i2 = new Point("i2", Arrays.asList(new Double[]{5.0, 6.0}));
        Polygon B = new Polygon("B", Arrays.asList(new Point[]{a2, b2, c2, d2, e2, f2, g2, h2, i2}));

        LinkFinder lf = new LinkFinder(A, B);
        for (PairSimilar<Point> p : lf.getlinkPairsList()) {
            System.out.println(p.a.label + "<-->" + p.b.label);
        }
    }

}
