package org.aksw.limes.core.measures.measure;

import org.aksw.limes.core.measures.mapper.Mapper;
import org.aksw.limes.core.measures.mapper.pointsets.OrchidMapper;
import org.aksw.limes.core.measures.mapper.pointsets.SymmetricHausdorffMapper;
import org.aksw.limes.core.measures.mapper.space.HR3;
import org.aksw.limes.core.measures.mapper.string.EDJoin;
import org.aksw.limes.core.measures.mapper.string.ExactMatchMapper;
import org.aksw.limes.core.measures.mapper.string.JaroMapper;
import org.aksw.limes.core.measures.mapper.string.PPJoinPlusPlus;
import org.aksw.limes.core.measures.mapper.string.RatcliffObershelpMapper;
import org.aksw.limes.core.measures.mapper.string.SoundexMapper;
import org.aksw.limes.core.measures.mapper.string.fastngram.FastNGram;
import org.aksw.limes.core.measures.mapper.temporal.ConcurrentMapper;
import org.aksw.limes.core.measures.mapper.temporal.PredecessorMapper;
import org.aksw.limes.core.measures.mapper.temporal.SuccessorMapper;
import org.aksw.limes.core.measures.measure.pointsets.GeoDistance;
import org.aksw.limes.core.measures.measure.pointsets.average.NaiveAverage;
import org.aksw.limes.core.measures.measure.pointsets.frechet.NaiveFrechet;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.CentroidIndexedHausdorff;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.FastHausdorff;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.IndexedHausdorff;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.NaiveHausdorff;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.ScanIndexedHausdorff;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.SymmetricHausdorff;
import org.aksw.limes.core.measures.measure.pointsets.link.NaiveLink;
import org.aksw.limes.core.measures.measure.pointsets.max.NaiveMax;
import org.aksw.limes.core.measures.measure.pointsets.mean.NaiveMean;
import org.aksw.limes.core.measures.measure.pointsets.min.NaiveMin;
import org.aksw.limes.core.measures.measure.pointsets.sumofmin.NaiveSumOfMin;
import org.aksw.limes.core.measures.measure.pointsets.surjection.FairSurjection;
import org.aksw.limes.core.measures.measure.pointsets.surjection.NaiveSurjection;
import org.aksw.limes.core.measures.measure.space.EuclideanMetric;
import org.aksw.limes.core.measures.measure.string.CosineMeasure;
import org.aksw.limes.core.measures.measure.string.ExactMatch;
import org.aksw.limes.core.measures.measure.string.JaccardMeasure;
import org.aksw.limes.core.measures.measure.string.Jaro;
import org.aksw.limes.core.measures.measure.string.Levenshtein;
import org.aksw.limes.core.measures.measure.string.QGramSimilarity;
import org.aksw.limes.core.measures.measure.string.TrigramMeasure;
import org.aksw.limes.core.measures.measure.temporal.ConcurrentMeasure;
import org.aksw.limes.core.measures.measure.temporal.PredecessorMeasure;
import org.aksw.limes.core.measures.measure.temporal.SuccessorMeasure;


public class MeasureFactory {

	public enum MeasureType { //TODO add other measures
	    GEO_NAIVE_HAUSDORFF, GEO_INDEXED_HAUSDORFF,GEO_FAST_HAUSDORFF, 
	    GEO_CENTROIDHAUSDORFF, GEO_SCAN_HAUSDORFF,GEO_MIN,GEO_MAX, 
	    GEO_AVG, GEO_SUM_OF_MIN, GEO_LINK, GEO_QUINLAN, GEO_FRECHET, 
	    GEO_SURJECTION, GEO_FAIR_SURJECTION, GEO_MEAN   
	};
	
	// String measures
	public static final String JARO 				= "jaro";
	public static final String QGRAMS 				= "qgrams";
	public static final String COSINE 				= "cosine";
	public static final String LEVENSHTEIN 			= "levenshtein";
	public static final String OVERLAP 				= "overlap";
	public static final String TRIGRAMS 			= "trigrams";
	public static final String JACCARD 				= "jaccard";
	public static final String EXACTMATCH			= "exactmatch";
	
	// number measures
	public static final String EUCLIDEAN 			= "euclidean";

	// time measures
	public static final String DATESIM 				= "datesim";
	public static final String YEARSIM 				= "yearsim";
	private static final String DAYSIM 				= "daysim";
	
	// Point-set measures
	public static final String GEO_ORTHODROMIC 		= "geo_orthodromic";
	public static final String GEO_ELLIPTIC 		= "geo_elliptic";
	public static final String GEO_HAUSDORFF 		= "geo_hausdorff";
	public static final String GEO_FAIR_SURJECTION 	= "geo_fairsurjection";	
	public static final String GEO_MAX 				= "geo_max";
	public static final String GEO_MEAN 			= "geo_mean";
	public static final String GEO_MIN 				= "geo_min";
	public static final String GEO_AVG 				= "geo_avg";
	public static final String GEO_FRECHET 			= "geo_frechet";
	public static final String GEO_LINK 			= "geo_link";
	public static final String GEO_SUM_OF_MIN 		= "geo_sum_of_min";
	public static final String GEO_SURJECTION 		= "geo_surjection";
	public static final String GEO_QUINLAN 			= "geo_quinlan";
	public static final String GEO_SYMMETRIC_HAUSDORFF="geo_symmetrichausdorff";

	// Temporal measures
	public static final String TMP_SUCCESSOR 		= "tmp_successor";
	public static final String TMP_PREDECESSOR 		= "tmp_predecessor";
	public static final String TMP_CONCURRENT 		= "tmp_concurrent";

	// others
	public static final String RATCLIFF 			= "RATCLIFF2";
	public static final String SOUNDEX 				= "soundex";
	public static final String MONGE 				= "monge";
	
	public static MeasureType getTypeFromExpression(String expression) {
		String measure = expression.toLowerCase();
		if (measure.startsWith(GEO_MEAN)) {
			return MeasureType.GEO_MEAN;
		}
		if (measure.startsWith(GEO_MIN)) {
			return MeasureType.GEO_MIN;
		}
		if (measure.startsWith(GEO_MAX)) {
			return MeasureType.GEO_MAX;
		}
		if (measure.startsWith(GEO_FRECHET)) {
			return MeasureType.GEO_FRECHET;
		}
		if (measure.startsWith(GEO_AVG)) {
			return MeasureType.GEO_AVG;
		}
		if (measure.startsWith(GEO_LINK)) {
			return MeasureType.GEO_LINK;
		}
		if (measure.startsWith(GEO_QUINLAN)) {
			return MeasureType.GEO_QUINLAN;
		}
		if (measure.startsWith(GEO_SUM_OF_MIN)) {
			return MeasureType.GEO_SUM_OF_MIN;
		}
		if (measure.startsWith(GEO_SURJECTION)) {
			return MeasureType.GEO_SURJECTION;
		}
		if (measure.startsWith(GEO_FAIR_SURJECTION)) {
			return MeasureType.GEO_FAIR_SURJECTION;
		} else {
			return MeasureType.GEO_INDEXED_HAUSDORFF;
		}
	}
	
    public static Measure getMeasure(MeasureType type) {
        Measure measure;
        if (type == MeasureType.GEO_NAIVE_HAUSDORFF) {
            measure = new NaiveHausdorff();
        } else if (type == MeasureType.GEO_FAST_HAUSDORFF) {
            measure = new FastHausdorff();
        } else if (type == MeasureType.GEO_INDEXED_HAUSDORFF) {
            measure = new IndexedHausdorff();
        } else if (type == MeasureType.GEO_SCAN_HAUSDORFF) {
            measure = new ScanIndexedHausdorff();
        } else if (type == MeasureType.GEO_MIN) {
            measure = new NaiveMin();
        } else if (type == MeasureType.GEO_MAX) {
            measure = new NaiveMax();
        } else if (type == MeasureType.GEO_AVG) {
            measure = new NaiveAverage();
        } else if (type == MeasureType.GEO_SUM_OF_MIN) {
            measure = new NaiveSumOfMin();
        } else if (type == MeasureType.GEO_LINK) {
            measure = new NaiveLink();
        } else if (type == MeasureType.GEO_FRECHET) {
            measure = new NaiveFrechet();
        } else if (type == MeasureType.GEO_SURJECTION) {
            measure = new NaiveSurjection();
        } else if (type == MeasureType.GEO_FAIR_SURJECTION) {
            measure = new FairSurjection();
        } else if (type == MeasureType.GEO_MEAN) {
            measure = new NaiveMean();
        } 
        else {
            measure = new CentroidIndexedHausdorff();
        }
        return measure;
    }

	public static Measure getMeasure(String name) {
		Measure m;
		if (name == null) {
			m = new TrigramMeasure();
			return m;
		}
		if (name.toLowerCase().startsWith(COSINE)) {
			m = new CosineMeasure();
		} else if (name.toLowerCase().startsWith(JACCARD)) {
			m = new JaccardMeasure();
		}  //else if (name.toLowerCase().startsWith(JAROWINKLER)) {problematic
		// m = new JaroWinkler();}
		else if (name.toLowerCase().startsWith(JARO)) {
			m = new Jaro();
		} // else if (name.toLowerCase().startsWith(RATCLIFF2)) { //problem
		// m = new RatcliffObershelpMeasure(); }
		else if (name.toLowerCase().startsWith(EUCLIDEAN)) {
			m = new EuclideanMetric();
		} else if (name.toLowerCase().startsWith(LEVENSHTEIN)) {
			m = new Levenshtein();
		} else if (name.toLowerCase().startsWith(QGRAMS)) {
			m = new QGramSimilarity();
		} else if (name.toLowerCase().startsWith(EXACTMATCH)) {
			m = new ExactMatch();
		} else if (name.toLowerCase().startsWith(GEO_HAUSDORFF)) {
			m = new NaiveHausdorff();
		} else if (name.toLowerCase().startsWith(GEO_ORTHODROMIC)) {
			// change this by implementing measure interface in
			// orthodromicdistance class
			m = new GeoDistance();
		} else if (name.toLowerCase().startsWith(GEO_SYMMETRIC_HAUSDORFF)) {
			m = new SymmetricHausdorff();
		} else if (name.toLowerCase().startsWith(GEO_MIN)) {
			m = new NaiveMin();
		} else if (name.toLowerCase().startsWith(GEO_MAX)) {
			m = new NaiveMax();
		} else if (name.toLowerCase().startsWith(GEO_AVG)) {
			m = new NaiveAverage();
		} else if (name.toLowerCase().startsWith(GEO_MEAN)) {
			m = new NaiveMean();
		} else if (name.toLowerCase().startsWith(GEO_FRECHET)) {
			m = new NaiveFrechet();
		} else if (name.toLowerCase().startsWith(GEO_LINK)) {
			m = new NaiveLink();
		} else if (name.toLowerCase().startsWith(GEO_SUM_OF_MIN)) {
			m = new NaiveSumOfMin();
		} else if (name.toLowerCase().startsWith(GEO_SURJECTION)) {
			m = new NaiveSurjection();
		} else if (name.toLowerCase().startsWith(GEO_FAIR_SURJECTION)) {
			m = new FairSurjection();
		}else if (name.toLowerCase().startsWith(TMP_SUCCESSOR)) {
			m = new SuccessorMeasure();
		} else if (name.toLowerCase().startsWith(TMP_PREDECESSOR)) {
			m = new PredecessorMeasure();
		} else if (name.toLowerCase().startsWith(TMP_CONCURRENT)) {
			m = new ConcurrentMeasure();
		} else {
			m = new TrigramMeasure();
		}
		return m;
	}

	/**
	 * Get mapper to measure
	 *
	 * @param measure,
	 *            name of measure
	 * @return am, mapper corresponding to measure
	 */
	public static Mapper getMapper(String measure) {
		Mapper am;
		if (measure.toLowerCase().startsWith(LEVENSHTEIN)) {
			am = new EDJoin();
		} else if (measure.toLowerCase().startsWith(QGRAMS)) {
			am = new FastNGram();
		} // else if (measure.toLowerCase().startsWith(JAROWINKLER)) {
		// //problematic
		// am = new JaroWinklerMapper(); }
		else if (measure.toLowerCase().startsWith(JARO)) {
			am = new JaroMapper();
		} else if (measure.toLowerCase().startsWith(TRIGRAMS)) {
			am = new PPJoinPlusPlus();
		} else if (measure.toLowerCase().startsWith(JACCARD)) {
			am = new PPJoinPlusPlus();
		} else if (measure.toLowerCase().startsWith(OVERLAP)) {
			am = new PPJoinPlusPlus();
		} else if (measure.toLowerCase().startsWith(COSINE)) {
			am = new PPJoinPlusPlus();
		} else if (measure.toLowerCase().startsWith(SOUNDEX)) {
			am = new SoundexMapper();
		} else if (measure.toLowerCase().startsWith(RATCLIFF)) {
			am = new RatcliffObershelpMapper();
		} // else if (measure.toLowerCase().startsWith(MONGE)) { //problem in
		// getMappingSizeApproximation
		// am = new MongeElkanMapper();}
		else if (measure.toLowerCase().startsWith(EXACTMATCH)) {
			am = new ExactMatchMapper();
		} else if (measure.toLowerCase().startsWith("euclid")) {
			am = new HR3();
		} else if (measure.toLowerCase().startsWith(GEO_HAUSDORFF)) {
			am = new OrchidMapper();
		} else if (measure.toLowerCase().startsWith(GEO_ORTHODROMIC)) {
			// the hausdorff distance is the same as the orthodromic distance
			// for single points
			am = new OrchidMapper();
		} else if (measure.toLowerCase().startsWith(GEO_SYMMETRIC_HAUSDORFF)) {
			am = new SymmetricHausdorffMapper();
		} else if (measure.toLowerCase().startsWith(DATESIM)) {
			am = new PPJoinPlusPlus();
		} else if (measure.toLowerCase().startsWith(DAYSIM)) {
			am = new PPJoinPlusPlus();
		} else if (measure.toLowerCase().startsWith(YEARSIM)) {
			am = new PPJoinPlusPlus();
		} else if (measure.toLowerCase().startsWith(GEO_MIN)) {
			am = new OrchidMapper();
		} else if (measure.toLowerCase().startsWith(GEO_MAX)) {
			am = new OrchidMapper();
		} else if (measure.toLowerCase().startsWith(GEO_SUM_OF_MIN)) {
			am = new OrchidMapper();
		} else if (measure.toLowerCase().startsWith(GEO_FRECHET)) {
			am = new OrchidMapper();
		} else if (measure.toLowerCase().startsWith(GEO_LINK)) {
			am = new OrchidMapper();
		} else if (measure.toLowerCase().startsWith(GEO_SURJECTION)) {
			am = new OrchidMapper();
		} else if (measure.toLowerCase().startsWith(GEO_FAIR_SURJECTION)) {
			am = new OrchidMapper();
		} else if (measure.toLowerCase().startsWith(TMP_SUCCESSOR)) {
			am = new SuccessorMapper();
		} else if (measure.toLowerCase().startsWith(TMP_PREDECESSOR)) {
			am = new PredecessorMapper();
		} else if (measure.toLowerCase().startsWith(TMP_CONCURRENT)) {
			am = new ConcurrentMapper();
		} else {
			am = null;
		}
		return am;

	}
}