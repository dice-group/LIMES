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
package org.aksw.limes.core.measures.measure.pointsets.hausdorff;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;

import java.util.Set;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 21, 2016
 */
public class CentroidIndexedHausdorffMeasure extends IndexedHausdorffMeasure {

    public CentroidIndex sourceIndex;
    public IndexedHausdorffMeasure ih = new IndexedHausdorffMeasure();
    boolean verbose = false;

    /**
     * Constructor
     */
    public CentroidIndexedHausdorffMeasure() {
        ih = new IndexedHausdorffMeasure();
    }

    /**
     * @param source polygons
     * @param target polygons
     */
    public void computeIndexes(Set<Polygon> source, Set<Polygon> target) {
        sourceIndex = new CentroidIndex();
        sourceIndex.index(source);
        targetIndex = new CentroidIndex();
        targetIndex.index(target);
        ih.targetIndex = targetIndex;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.aksw.limes.core.measures.measure.pointsets.hausdorff.IndexedHausdorff
     * #run(java.util.Set, java.util.Set, double)
     */
    @Override
    public AMapping run(Set<Polygon> source, Set<Polygon> target, double threshold) {
        // first run indexing
        AMapping m = MappingFactory.createDefaultMapping();
        targetIndex = new CentroidIndex();
        sourceIndex = new CentroidIndex();
        // long begin = System.currentTimeMillis();
        targetIndex.index(target);
        sourceIndex.index(source);
        ih.targetIndex = targetIndex;

        double d;
        for (Polygon s : source) {
            for (Polygon t : target) {
                d = pointToPointDistance(sourceIndex.centroids.get(s.uri).center,
                        ((CentroidIndex) targetIndex).centroids.get(t.uri).center);
                if (d - (sourceIndex.centroids.get(s.uri).radius
                        + ((CentroidIndex) targetIndex).centroids.get(t.uri).radius) <= threshold) {
                    d = computeDistance(s, t, threshold);
                    if (d <= threshold) {
                        m.add(s.uri, t.uri, d);
                    }
                }
            }
        }
        return m;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.aksw.limes.core.measures.measure.pointsets.hausdorff.IndexedHausdorff
     * #computeDistance(org.aksw.limes.core.measures.mapper.atomic.hausdorff.
     * Polygon, org.aksw.limes.core.measures.mapper.atomic.hausdorff.Polygon,
     * double)
     */
    @Override
    public double computeDistance(Polygon X, Polygon Y, double threshold) {
        // centroid distance check
        double d = pointToPointDistance(sourceIndex.centroids.get(X.uri).center,
                ((CentroidIndex) targetIndex).centroids.get(Y.uri).center);
        if (d - (sourceIndex.centroids.get(X.uri).radius
                + ((CentroidIndex) targetIndex).centroids.get(Y.uri).radius) > threshold) {
            return threshold + 1;
        }
        return ih.computeDistance(X, Y, threshold);
    }
}
