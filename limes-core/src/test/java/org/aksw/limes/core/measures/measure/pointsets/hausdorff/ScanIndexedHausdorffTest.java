package org.aksw.limes.core.measures.measure.pointsets.hausdorff;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.datastrutures.Point;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.junit.Test;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 *
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
        // System.out.println(c.run(testSet, testSet, 0.1.0));

    }

}
