package org.aksw.limes.core.measures.measure;

import org.aksw.limes.core.exceptions.InvalidMeasureException;
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
import org.aksw.limes.core.measures.measure.string.SoundexMeasure;
import org.aksw.limes.core.measures.measure.string.TrigramMeasure;
import org.aksw.limes.core.measures.measure.temporal.allenAlgebra.AfterMeasure;
import org.aksw.limes.core.measures.measure.temporal.allenAlgebra.BeforeMeasure;
import org.aksw.limes.core.measures.measure.temporal.allenAlgebra.DuringMeasure;
import org.aksw.limes.core.measures.measure.temporal.allenAlgebra.DuringReverseMeasure;
import org.aksw.limes.core.measures.measure.temporal.allenAlgebra.EqualsMeasure;
import org.aksw.limes.core.measures.measure.temporal.allenAlgebra.FinishesMeasure;
import org.aksw.limes.core.measures.measure.temporal.allenAlgebra.IsFinishedByMeasure;
import org.aksw.limes.core.measures.measure.temporal.allenAlgebra.IsMetByMeasure;
import org.aksw.limes.core.measures.measure.temporal.allenAlgebra.IsOverlappedByMeasure;
import org.aksw.limes.core.measures.measure.temporal.allenAlgebra.IsStartedByMeasure;
import org.aksw.limes.core.measures.measure.temporal.allenAlgebra.MeetsMeasure;
import org.aksw.limes.core.measures.measure.temporal.allenAlgebra.OverlapsMeasure;
import org.aksw.limes.core.measures.measure.temporal.allenAlgebra.StartsMeasure;
import org.aksw.limes.core.measures.measure.temporal.simpleTemporal.ConcurrentMeasure;
import org.aksw.limes.core.measures.measure.temporal.simpleTemporal.PredecessorMeasure;
import org.aksw.limes.core.measures.measure.temporal.simpleTemporal.SuccessorMeasure;
import org.apache.log4j.Logger;

/**
 * Implements the measure factory class. For each measure name, the factory
 * returns an object of the corresponding measure or the mapper. The same mapper
 * can correspond to more than one measure.
 *
 * @author Axel-C. Ngonga Ngomo <ngonga@informatik.uni-leipzig.de>
 * @author Mohamed Ahmed Sherif <msherif@informatik.uni-leipzig.de>
 * @author Kleanthi Georgala <georgala@informatik.uni-leipzig.de>
 * 
 * @version 1.0
 */
public class MeasureFactory {
    static Logger logger = Logger.getLogger(MeasureFactory.class);

    // String measures
    public static final String JARO = "jaro";
    public static final String QGRAMS = "qgrams";
    public static final String COSINE = "cosine";
    public static final String LEVENSHTEIN = "levenshtein";
    public static final String OVERLAP = "overlap";
    public static final String TRIGRAM = "trigram";
    public static final String JACCARD = "jaccard";
    public static final String EXACTMATCH = "exactmatch";
    public static final String SOUNDEX = "soundex";
    // number measures
    public static final String EUCLIDEAN = "euclidean";
    // Point-set measures
    public static final String GEO_ORTHODROMIC = "geo_orthodromic";
    // public static final String GEO_ELLIPTIC = "geo_elliptic";
    public static final String GEO_HAUSDORFF = "geo_hausdorff";
    public static final String GEO_FAIR_SURJECTION = "geo_fairsurjection";
    public static final String GEO_MAX = "geo_max";
    public static final String GEO_MEAN = "geo_mean";
    public static final String GEO_MIN = "geo_min";
    public static final String GEO_AVG = "geo_avg";
    public static final String GEO_FRECHET = "geo_frechet";
    public static final String GEO_LINK = "geo_link";
    public static final String GEO_SUM_OF_MIN = "geo_sum_of_min";
    public static final String GEO_SURJECTION = "geo_surjection";
    public static final String GEO_SYMMETRIC_HAUSDORFF = "geo_symmetrichausdorff";
    // Temporal measures
    public static final String TMP_SUCCESSOR = "tmp_successor";
    public static final String TMP_PREDECESSOR = "tmp_predecessor";
    public static final String TMP_CONCURRENT = "tmp_concurrent";
    public static final String TMP_BEFORE = "tmp_before";
    public static final String TMP_AFTER = "tmp_after";
    public static final String TMP_MEETS = "tmp_meets";
    public static final String TMP_IS_MET_BY = "tmp_ismetby";
    public static final String TMP_FINISHES = "tmp_finishes";
    public static final String TMP_IS_FINISHED_BY = "tmp_isfinishedby";
    public static final String TMP_STARTS = "tmp_starts";
    public static final String TMP_IS_STARTED_BY = "tmp_isstartedby";
    public static final String TMP_DURING = "tmp_during";
    public static final String TMP_DURING_REVERSE = "tmp_duringreverse";
    public static final String TMP_OVERLAPS = "tmp_overlaps";
    public static final String TMP_IS_OVERLAPPED_BY = "tmp_isoverlappedby";
    public static final String TMP_EQUALS = "tmp_equals";

    /**
     * Factory function for retrieving a measure name from the set of allowed
     * types.
     * 
     * @param name,
     *            The name/type of the measure.
     * @return a specific measure type
     */
    public static MeasureType getGeoMeasureType(String expression) {
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

    /**
     * Factory function for retrieving the desired measure instance.
     * 
     * @param type,
     *            Type of the measure
     * 
     * @return a specific measure instance
     * 
     */
    public static Measure getGeoMeasure(MeasureType type) {
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
        } else {
            measure = new CentroidIndexedHausdorff();
        }
        return measure;
    }

    /**
     * Factory function for retrieving the desired measure instance.
     * 
     * @param name,
     *            Name of the measure
     * 
     * @return a specific measure instance
     * @throws InvalidMeasureException
     * 
     * 
     */
    public static Measure createMeasure(String name) throws InvalidMeasureException {
        Measure m = null;

        if (name.toLowerCase().startsWith(COSINE)) {
            m = new CosineMeasure();
        } else if (name.toLowerCase().startsWith(EXACTMATCH)) {
            m = new ExactMatch();
        } else if (name.toLowerCase().startsWith(JACCARD)) {
            m = new JaccardMeasure();
        } else if (name.toLowerCase().startsWith(JARO)) {
            m = new Jaro();
        } else if (name.toLowerCase().startsWith(LEVENSHTEIN)) {
            m = new Levenshtein();
        } else if (name.toLowerCase().startsWith(OVERLAP)) {
            m = new TrigramMeasure();
        } else if (name.toLowerCase().startsWith(TRIGRAM)) {
            m = new TrigramMeasure();
        } else if (name.toLowerCase().startsWith(QGRAMS)) {
            m = new QGramSimilarity();
        } else if (name.toLowerCase().startsWith(SOUNDEX)) {
            m = new SoundexMeasure();
        } else if (name.toLowerCase().startsWith(EUCLIDEAN)) {
            m = new EuclideanMetric();
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
        } else if (name.toLowerCase().startsWith(TMP_SUCCESSOR)) {
            m = new SuccessorMeasure();
        } else if (name.toLowerCase().startsWith(TMP_PREDECESSOR)) {
            m = new PredecessorMeasure();
        } else if (name.toLowerCase().startsWith(TMP_CONCURRENT)) {
            m = new ConcurrentMeasure();
        } else if (name.toLowerCase().startsWith(TMP_BEFORE)) {
            m = new BeforeMeasure();
        } else if (name.toLowerCase().startsWith(TMP_AFTER)) {
            m = new AfterMeasure();
        } else if (name.toLowerCase().startsWith(TMP_MEETS)) {
            m = new MeetsMeasure();
        } else if (name.toLowerCase().startsWith(TMP_IS_MET_BY)) {
            m = new IsMetByMeasure();
        } else if (name.toLowerCase().startsWith(TMP_FINISHES)) {
            m = new FinishesMeasure();
        } else if (name.toLowerCase().startsWith(TMP_IS_FINISHED_BY)) {
            m = new IsFinishedByMeasure();
        } else if (name.toLowerCase().startsWith(TMP_STARTS)) {
            m = new StartsMeasure();
        } else if (name.toLowerCase().startsWith(TMP_IS_STARTED_BY)) {
            m = new IsStartedByMeasure();
        } else if (name.toLowerCase().startsWith(TMP_DURING_REVERSE)) {
            m = new DuringReverseMeasure();
        } else if (name.toLowerCase().startsWith(TMP_DURING)) {
            m = new DuringMeasure();
        } else if (name.toLowerCase().startsWith(TMP_OVERLAPS)) {
            m = new OverlapsMeasure();
        } else if (name.toLowerCase().startsWith(TMP_IS_OVERLAPPED_BY)) {
            m = new IsOverlappedByMeasure();
        } else if (name.toLowerCase().startsWith(TMP_EQUALS)) {
            m = new EqualsMeasure();
        } else {
            throw new InvalidMeasureException(name);
        }
        return m;
    }



   
}