package org.aksw.limes.core.measures.measure.space;

import org.aksw.limes.core.measures.measure.AMeasure;

public abstract class ASpaceMeasure extends AMeasure implements ISpaceMeasure {
    protected static double D2R = Math.PI / 180;
    protected static double radius = 6371 ;
    int dimension = 2;

    public void setDimension(int n) {
        dimension = n;
    }

}
