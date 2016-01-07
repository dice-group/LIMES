/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.aksw.limes.core.measures.measure.space;

import org.aksw.limes.core.measures.measure.IMeasure;

/**
 *
 * @author ngonga
 */
public interface ISpaceMeasure extends IMeasure{
    public void setDimension(int n);
    public double getThreshold(int dimension, double simThreshold);
}
