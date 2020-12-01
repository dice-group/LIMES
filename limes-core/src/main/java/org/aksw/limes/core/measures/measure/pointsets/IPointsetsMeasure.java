/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.pointsets;

import java.util.Set;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.aksw.limes.core.measures.measure.IMeasure;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public interface IPointsetsMeasure extends IMeasure {

    public double computeDistance(Polygon X, Polygon Y, double threshold);

    public AMapping run(Set<Polygon> source, Set<Polygon> target, double threshold);

    public int getComputations();

    public String getName();
}
