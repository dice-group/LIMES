/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.pointsets;

import org.aksw.limes.core.measures.measure.pointsets.average.NaiveAverage;
import org.aksw.limes.core.measures.measure.pointsets.frechet.NaiveFrechet;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.CentroidIndexedHausdorff;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.FastHausdorff;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.IndexedHausdorff;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.NaiveHausdorff;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.ScanIndexedHausdorff;
import org.aksw.limes.core.measures.measure.pointsets.link.NaiveLink;
import org.aksw.limes.core.measures.measure.pointsets.max.NaiveMax;
import org.aksw.limes.core.measures.measure.pointsets.mean.NaiveMean;
import org.aksw.limes.core.measures.measure.pointsets.min.NaiveMin;
import org.aksw.limes.core.measures.measure.pointsets.sumofmin.NaiveSumOfMin;
import org.aksw.limes.core.measures.measure.pointsets.surjection.FairSurjection;
import org.aksw.limes.core.measures.measure.pointsets.surjection.NaiveSurjection;

/**
 * Generates a SetMeasure implementation
 *
 * @author ngonga
 */
public class SetMeasureFactory {

    public enum Type {
	NAIVEHAUSDORFF, INDEXEDHAUSDORFF, FASTHAUSDORFF, CENTROIDHAUSDORFF, SCANHAUSDORFF, GEOMIN, GEOMAX, GEOAVG, GEOSUMMIN, GEOLINK, GEOQUINLAN, FRECHET, SURJECTION, FAIRSURJECTION, MEAN
    };

    public static IPointsetsMeasure getMeasure(Type type) {
	IPointsetsMeasure measure;
	if (type == Type.NAIVEHAUSDORFF) {
	    measure = new NaiveHausdorff();
	} else if (type == Type.FASTHAUSDORFF) {
	    measure = new FastHausdorff();
	} else if (type == Type.INDEXEDHAUSDORFF) {
	    measure = new IndexedHausdorff();
	} else if (type == Type.SCANHAUSDORFF) {
	    measure = new ScanIndexedHausdorff();
	} else if (type == Type.GEOMIN) {
	    measure = new NaiveMin();
	} else if (type == Type.GEOMAX) {
	    measure = new NaiveMax();
	} else if (type == Type.GEOAVG) {
	    measure = new NaiveAverage();
	} else if (type == Type.GEOSUMMIN) {
	    measure = new NaiveSumOfMin();
	} else if (type == Type.GEOLINK) {
	    measure = new NaiveLink();
	} else if (type == Type.FRECHET) {
	    measure = new NaiveFrechet();
	} else if (type == Type.SURJECTION) {
	    measure = new NaiveSurjection();
	} else if (type == Type.FAIRSURJECTION) {
	    measure = new FairSurjection();
	} else if (type == Type.MEAN) {
	    measure = new NaiveMean();
	} else {
	    measure = new CentroidIndexedHausdorff();
	}
	return measure;
    }

    public static Type getType(IPointsetsMeasure measure) {
	if (measure instanceof NaiveHausdorff) {
	    return Type.NAIVEHAUSDORFF;
	}
	if (measure instanceof FastHausdorff) {
	    return Type.FASTHAUSDORFF;
	}
	if (measure instanceof IndexedHausdorff) {
	    return Type.INDEXEDHAUSDORFF;
	}
	if (measure instanceof ScanIndexedHausdorff) {
	    return Type.SCANHAUSDORFF;
	}
	if (measure instanceof NaiveMin) {
	    return Type.GEOMIN;
	}
	if (measure instanceof NaiveMax) {
	    return Type.GEOMAX;
	}
	if (measure instanceof NaiveAverage) {
	    return Type.GEOAVG;
	}
	if (measure instanceof NaiveSumOfMin) {
	    return Type.GEOSUMMIN;
	}
	if (measure instanceof NaiveLink) {
	    return Type.GEOLINK;
	}
	if (measure instanceof NaiveFrechet) {
	    return Type.FRECHET;
	}
	if (measure instanceof NaiveSurjection) {
	    return Type.SURJECTION;
	}
	if (measure instanceof FairSurjection) {
	    return Type.FAIRSURJECTION;
	}
	if (measure instanceof CentroidIndexedHausdorff) {
	    return Type.CENTROIDHAUSDORFF;
	}
	if (measure instanceof NaiveMean) {
	    return Type.MEAN;
	}
	return null;
    }
}
