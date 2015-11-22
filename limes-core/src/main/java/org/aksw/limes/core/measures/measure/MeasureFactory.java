package org.aksw.limes.core.measures.measure;

import org.apache.log4j.Logger;
import org.aksw.limes.core.measures.mapper.IMapper;
import org.aksw.limes.core.measures.mapper.atomic.EDJoin;
import org.aksw.limes.core.measures.mapper.atomic.ExactMatchMapper;
import org.aksw.limes.core.measures.mapper.atomic.JaroMapper;
import org.aksw.limes.core.measures.mapper.atomic.JaroWinklerMapper;
import org.aksw.limes.core.measures.mapper.atomic.MongeElkanMapper;
import org.aksw.limes.core.measures.mapper.atomic.OrchidMapper;
import org.aksw.limes.core.measures.mapper.atomic.PPJoinPlusPlus;
import org.aksw.limes.core.measures.mapper.atomic.RatcliffObershelpMapper;
import org.aksw.limes.core.measures.mapper.atomic.SoundexMapper;
import org.aksw.limes.core.measures.mapper.atomic.SymmetricHausdorffMapper;
import org.aksw.limes.core.measures.mapper.atomic.TotalOrderBlockingMapper;
import org.aksw.limes.core.measures.mapper.atomic.fastngram.FastNGram;
import org.aksw.limes.core.measures.measure.IMeasure;
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
import org.aksw.limes.core.measures.measure.string.JaroWinkler;
import org.aksw.limes.core.measures.measure.string.Levenshtein;
import org.aksw.limes.core.measures.measure.string.QGramSimilarity;
import org.aksw.limes.core.measures.measure.string.RatcliffObershelpMeasure;
import org.aksw.limes.core.measures.measure.string.TrigramMeasure;

public class MeasureFactory {

    static Logger logger = Logger.getLogger("LIMES");

    public static IMeasure getMeasure(String name) {
	IMeasure m;
	if (name.toLowerCase().startsWith("cosine")) {
	    m = new CosineMeasure();
	} else if (name.toLowerCase().startsWith("jaccard")) {
	    m = new JaccardMeasure();
	} else if (name.toLowerCase().startsWith("jarowinkler")) {
	    m = new JaroWinkler();
	} else if (name.toLowerCase().startsWith("jaro")) {
	    m = new Jaro();
	} else if (name.toLowerCase().startsWith("ratcliff")) {
	    m = new RatcliffObershelpMeasure();
	} else if (name.toLowerCase().startsWith("euclidean")) {
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
	} else if (name.toLowerCase().startsWith("datesim")) {
	    m = new SimpleDateMeasure();
	} else if (name.toLowerCase().startsWith("daysim")) {
	    m = new DayMeasure();
	} else if (name.toLowerCase().startsWith("yearsim")) {
	    m = new YearMeasure();
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

	// System.out.println("Got measure "+m.getName()+" for name
	// <"+name+">");
	return m;
    }

    /**
     * Returns measures of a particular type. If measure with name "name" and
     * type "type" is not found, the default measure for the given type is
     * returned, e.g., trigram similarity for strings. To get the defaukt
     * measure of a given type, simply use getMeasure("", type).
     *
     * @param name
     *            Name of the measure
     * @param type
     *            Type of the measure
     * @return Similarity measure of the given type
     */
    public static IMeasure getMeasure(String name, String type) {
	if (type.equals("string")) {
	    if (name.toLowerCase().startsWith("cosine")) {
		return new CosineMeasure();
	    } else if (name.toLowerCase().startsWith("jaccard")) {
		return new JaccardMeasure();
	    }
	    // default
	    return new TrigramMeasure();

	} else if (type.equals("spatial")) {
	    if (name.toLowerCase().startsWith("geo")) {
		return new GeoDistance();
	    } else if (name.toLowerCase().startsWith("euclidean")) {
		return new EuclideanMetric();
	    }
	    // default
	    return new EuclideanMetric();
	} else if (type.equals("date")) {
	    if (name.toLowerCase().startsWith("datesim")) {
		return new SimpleDateMeasure();
	    } else if (name.toLowerCase().startsWith("daysim")) {
		return new DayMeasure();
	    } else if (name.toLowerCase().startsWith("yearsim")) {
		return new YearMeasure();
	    }
	    // default
	    return new SimpleDateMeasure();
	}
	// default of all
	return new TrigramMeasure();
    }

    /**
     * Get mapper to measure
     *
     * @param measure
     * @return
     */
    public static IMapper getMapper(String measure) {
	IMapper am;
	if (measure.toLowerCase().startsWith("leven")) {
	    am = new EDJoin();
	} else if (measure.toLowerCase().startsWith("qgrams")) {
	    am = new FastNGram();
	} else if (measure.toLowerCase().startsWith("jarowinkler")) {
	    am = new JaroWinklerMapper();
	} else if (measure.toLowerCase().startsWith("jaro")) {
	    am = new JaroMapper();
	} else if (measure.toLowerCase().startsWith("trigrams")) {
	    am = new PPJoinPlusPlus();
	} else if (measure.toLowerCase().startsWith("soundex")) {
	    am = new SoundexMapper();
	} else if (measure.toLowerCase().startsWith("ratcliff")) {
	    am = new RatcliffObershelpMapper();
	} else if (measure.toLowerCase().startsWith("monge")) {
	    am = new MongeElkanMapper();
	} else if (measure.toLowerCase().startsWith("exactmatch")) {
	    am = new ExactMatchMapper();
	} else if (measure.toLowerCase().startsWith("euclid")) {
	    am = new TotalOrderBlockingMapper();
	} else if (measure.toLowerCase().startsWith("jaccard")) {
	    am = new PPJoinPlusPlus();
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
	} // logger.warn("Could not find mapper for " + measure + ". Using
	  // default mapper.");
	else {
	    am = new PPJoinPlusPlus();
	}
	// System.out.println("Got mapper with name <"+am.getName()+"> for
	// expression <"+measure+">");
	return am;
    }

}
