package org.aksw.limes.core.measures.measure;

import org.apache.log4j.Logger;
import org.aksw.limes.core.measures.mapper.Mapper;
import org.aksw.limes.core.measures.mapper.atomic.EDJoin;
import org.aksw.limes.core.measures.mapper.atomic.ExactMatchMapper;
import org.aksw.limes.core.measures.mapper.atomic.JaroMapper;
import org.aksw.limes.core.measures.mapper.atomic.OrchidMapper;
import org.aksw.limes.core.measures.mapper.atomic.PPJoinPlusPlus;
import org.aksw.limes.core.measures.mapper.atomic.RatcliffObershelpMapper;
import org.aksw.limes.core.measures.mapper.atomic.SoundexMapper;
import org.aksw.limes.core.measures.mapper.atomic.SymmetricHausdorffMapper;
import org.aksw.limes.core.measures.mapper.atomic.TotalOrderBlockingMapper;
import org.aksw.limes.core.measures.mapper.atomic.fastngram.FastNGram;
import org.aksw.limes.core.measures.measure.date.DayMeasure;
import org.aksw.limes.core.measures.measure.date.SimpleDateMeasure;
import org.aksw.limes.core.measures.measure.date.YearMeasure;
import org.aksw.limes.core.measures.measure.pointsets.average.NaiveAverage;
import org.aksw.limes.core.measures.measure.pointsets.frechet.NaiveFrechet;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.NaiveHausdorff;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.SymmetricHausdorff;
import org.aksw.limes.core.measures.measure.pointsets.link.NaiveLink;
import org.aksw.limes.core.measures.measure.pointsets.max.NaiveMax;
import org.aksw.limes.core.measures.measure.pointsets.mean.NaiveMean;
import org.aksw.limes.core.measures.measure.pointsets.min.NaiveMin;
import org.aksw.limes.core.measures.measure.pointsets.sumofmin.NaiveSumOfMin;
import org.aksw.limes.core.measures.measure.pointsets.surjection.FairSurjection;
import org.aksw.limes.core.measures.measure.pointsets.surjection.NaiveSurjection;
import org.aksw.limes.core.measures.measure.space.EuclideanMetric;
import org.aksw.limes.core.measures.measure.space.GeoDistance;
import org.aksw.limes.core.measures.measure.string.CosineMeasure;
import org.aksw.limes.core.measures.measure.string.ExactMatch;
import org.aksw.limes.core.measures.measure.string.JaccardMeasure;
import org.aksw.limes.core.measures.measure.string.Jaro;
import org.aksw.limes.core.measures.measure.string.Levenshtein;
import org.aksw.limes.core.measures.measure.string.QGramSimilarity;
import org.aksw.limes.core.measures.measure.string.TrigramMeasure;

public class MeasureFactory {

    static Logger logger = Logger.getLogger("LIMES");

    public static Measure getMeasure(String name) {
	Measure m;
	if (name == null) {
	    m = new TrigramMeasure();
	    return m;
	}
	if (name.toLowerCase().startsWith("cosine")) {
	    m = new CosineMeasure();
	} else if (name.toLowerCase().startsWith("jaccard")) {
	    m = new JaccardMeasure();
	} // else if (name.toLowerCase().startsWith("jarowinkler")) {problematic
	  // m = new JaroWinkler();}
	else if (name.toLowerCase().startsWith("jaro")) {
	    m = new Jaro();
	} // else if (name.toLowerCase().startsWith("ratcliff")) { //problem
	  // m = new RatcliffObershelpMeasure(); }
	else if (name.toLowerCase().startsWith("euclidean")) {
	    m = new EuclideanMetric();
	} else if (name.toLowerCase().startsWith("levens")) {
	    m = new Levenshtein();
	} else if (name.toLowerCase().startsWith("qgrams")) {
	    m = new QGramSimilarity();
	} else if (name.toLowerCase().startsWith("exactmatch")) {// NO
								 // getRuntimeApproximation
	    m = new ExactMatch();
	} else if (name.toLowerCase().startsWith("hausdorff")) {
	    m = new NaiveHausdorff();
	} else if (name.toLowerCase().startsWith("orthodromic")) {// NO
								  // getRuntimeApproximation
	    // change this by implementing measure interface in
	    // orthodromicdistance class
	    m = new GeoDistance();
	} else if (name.toLowerCase().startsWith("symmetrichausdorff")) {// NO
									 // getRuntimeApproximation
	    m = new SymmetricHausdorff();
	} else if (name.toLowerCase().startsWith("datesim")) {
	    m = new SimpleDateMeasure();
	} else if (name.toLowerCase().startsWith("daysim")) {// NO
							     // getRuntimeApproximation
	    m = new DayMeasure();
	} else if (name.toLowerCase().startsWith("yearsim")) {// NO
							      // getRuntimeApproximation
	    m = new YearMeasure();
	} else if (name.toLowerCase().startsWith("geomn")) {// NO
							    // getRuntimeApproximation
	    m = new NaiveMin();
	} else if (name.toLowerCase().startsWith("geomx")) {// NO
							    // getRuntimeApproximation
	    m = new NaiveMax();
	} else if (name.toLowerCase().startsWith("geoavg")) {// NO
							     // getRuntimeApproximation
	    m = new NaiveAverage();
	} else if (name.toLowerCase().startsWith("geomean")) {// NO
							      // getRuntimeApproximation
	    m = new NaiveMean();
	} else if (name.toLowerCase().startsWith("frechet")) {// NO
							      // getRuntimeApproximation
	    m = new NaiveFrechet();
	} else if (name.toLowerCase().startsWith("geolink")) {// NO
							      // getRuntimeApproximation
	    m = new NaiveLink();
	} else if (name.toLowerCase().startsWith("geosummn")) {// NO
							       // getRuntimeApproximation
	    m = new NaiveSumOfMin();
	} else if (name.toLowerCase().startsWith("surjection")) {// NO
								 // getRuntimeApproximation
	    m = new NaiveSurjection();
	} else if (name.toLowerCase().startsWith("fairsurjection")) {// NO
								     // getRuntimeApproximation
	    m = new FairSurjection();
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
	if (measure.toLowerCase().startsWith("leven")) {
	    am = new EDJoin();
	} else if (measure.toLowerCase().startsWith("qgrams")) {
	    am = new FastNGram();
	} // else if (measure.toLowerCase().startsWith("jarowinkler")) {
	  // //problematic
	  // am = new JaroWinklerMapper(); }
	else if (measure.toLowerCase().startsWith("jaro")) {
	    am = new JaroMapper();
	} else if (measure.toLowerCase().startsWith("trigrams")) {
	    am = new PPJoinPlusPlus();
	} else if (measure.toLowerCase().startsWith("jaccard")) {
	    am = new PPJoinPlusPlus();
	} else if (measure.toLowerCase().startsWith("overlap")) {
	    am = new PPJoinPlusPlus();
	} else if (measure.toLowerCase().startsWith("cosine")) {
	    am = new PPJoinPlusPlus();
	} else if (measure.toLowerCase().startsWith("soundex")) {
	    am = new SoundexMapper();
	} else if (measure.toLowerCase().startsWith("ratcliff")) {
	    am = new RatcliffObershelpMapper();
	} // else if (measure.toLowerCase().startsWith("monge")) { //problem in
	  // getMappingSizeApproximation
	  // am = new MongeElkanMapper();}
	  else if (measure.toLowerCase().startsWith("exactmatch")) {
	    am = new ExactMatchMapper();
	} else if (measure.toLowerCase().startsWith("euclid")) {
	    am = new TotalOrderBlockingMapper();
	} else if (measure.toLowerCase().startsWith("hausdorff")) {
	    am = new OrchidMapper();
	} else if (measure.toLowerCase().startsWith("orthodromic")) {
	    // the hausdorff distance is the same as the orthodromic distance
	    // for single points
	    am = new OrchidMapper();
	} else if (measure.toLowerCase().startsWith("symmetrichausdorff")) {
	    am = new SymmetricHausdorffMapper();
	} else if (measure.toLowerCase().startsWith("datesim")) {
	    am = new PPJoinPlusPlus();
	} else if (measure.toLowerCase().startsWith("daysim")) {
	    am = new PPJoinPlusPlus();
	} else if (measure.toLowerCase().startsWith("yearsim")) {
	    am = new PPJoinPlusPlus();
	} else if (measure.toLowerCase().startsWith("geomin")) {
	    am = new OrchidMapper();
	} else if (measure.toLowerCase().startsWith("geomax")) {
	    am = new OrchidMapper();
	} else if (measure.toLowerCase().startsWith("geosumofmin")) {
	    am = new OrchidMapper();
	} else if (measure.toLowerCase().startsWith("frechet")) {
	    am = new OrchidMapper();
	} else if (measure.toLowerCase().startsWith("link")) {
	    am = new OrchidMapper();
	} else if (measure.toLowerCase().startsWith("surjection")) {
	    am = new OrchidMapper();
	} else if (measure.toLowerCase().startsWith("fairsurjection")) {
	    am = new OrchidMapper();
	} else {
	    am = null;
	}

	return am;
    }

}
