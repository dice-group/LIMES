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
import org.aksw.limes.core.measures.measure.string.CosineMeasure;
import org.aksw.limes.core.measures.measure.string.ExactMatch;
import org.aksw.limes.core.measures.measure.string.JaccardMeasure;
import org.aksw.limes.core.measures.measure.string.Jaro;
import org.aksw.limes.core.measures.measure.string.Levenshtein;
import org.aksw.limes.core.measures.measure.string.QGramSimilarity;
import org.aksw.limes.core.measures.measure.string.TrigramMeasure;

public class MeasureFactory {

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
	} else if (name.toLowerCase().startsWith("exactmatch")) {
	    m = new ExactMatch();
	} else if (name.toLowerCase().startsWith("hausdorff")) {
	    m = new NaiveHausdorff();
	} else if (name.toLowerCase().startsWith("orthodromic")) {
	    // change this by implementing measure interface in
	    // orthodromicdistance class
	    m = new GeoDistance();
	} else if (name.toLowerCase().startsWith("symmetrichausdorff")) {
	    m = new SymmetricHausdorff();
	} else if (name.toLowerCase().startsWith("geomn")) {
	    m = new NaiveMin();
	} else if (name.toLowerCase().startsWith("geomx")) {
	    m = new NaiveMax();
	} else if (name.toLowerCase().startsWith("geoavg")) {
	    m = new NaiveAverage();
	} else if (name.toLowerCase().startsWith("geomean")) {
	    m = new NaiveMean();
	} else if (name.toLowerCase().startsWith("frechet")) {
	    m = new NaiveFrechet();
	} else if (name.toLowerCase().startsWith("geolink")) {
	    m = new NaiveLink();
	} else if (name.toLowerCase().startsWith("geosummn")) {
	    m = new NaiveSumOfMin();
	} else if (name.toLowerCase().startsWith("surjection")) {
	    m = new NaiveSurjection();
	} else if (name.toLowerCase().startsWith("fairsurjection")) {
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
	    am = new HR3();
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
	} else if (measure.toLowerCase().startsWith("successor")) {
	    am = new SuccessorMapper();
	} else if (measure.toLowerCase().startsWith("predecessor")) {
	    am = new PredecessorMapper();
	} else if (measure.toLowerCase().startsWith("concurrent")) {
	    am = new ConcurrentMapper();
	} else {
	    am = null;
	}
	return am;

    }
}