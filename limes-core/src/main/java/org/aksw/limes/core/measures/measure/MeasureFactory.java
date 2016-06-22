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
import org.aksw.limes.core.measures.measure.string.JaroWinkler;
import org.aksw.limes.core.measures.measure.string.Levenshtein;
import org.aksw.limes.core.measures.measure.string.OverlapMeasure;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the measure factory class. For each measure name, the factory
 * returns an object of the corresponding measure.
 *
 * @author Axel-C. Ngonga Ngomo <ngonga@informatik.uni-leipzig.de>
 * @author Mohamed Ahmed Sherif <msherif@informatik.uni-leipzig.de>
 * @author Kleanthi Georgala <georgala@informatik.uni-leipzig.de>
 * 
 * @version 1.0
 */
public class MeasureFactory {
    static Logger logger = LoggerFactory.getLogger(MeasureFactory.class);

    // String measures
    public static final String JARO = "jaro";
    public static final String QGRAMS = "qgrams";
    public static final String COSINE = "cosine";
    public static final String LEVENSHTEIN = "levenshtein";
    public static final String OVERLAP = "overlap";
    public static final String TRIGRAMS = "trigram";
    public static final String JACCARD = "jaccard";
    public static final String EXACTMATCH = "exactmatch";
    public static final String SOUNDEX = "soundex";
    public static final String JAROWINKLER = "jarowinkler";
    // number measures
    public static final String EUCLIDEAN = "euclidean";
    // Point-set measures
    public static final String GEO_ORTHODROMIC = "geo_orthodromic";
    public static final String GEO_HAUSDORFF = "geo_hausdorff";
    public static final String GEO_NAIVE_HAUSDORFF = "geo_naivehausdorff";
    public static final String GEO_INDEXED_HAUSDORFF = "geo_indexedhausdorff";
    public static final String GEO_FAST_HAUSDORFF = "geo_fasthausdorff";
    public static final String GEO_SYMMETRIC_HAUSDORFF = "geo_symmetrichausdorff";
    public static final String GEO_CENTROID_INDEXED_HAUSDORFF = "geo_centroidindexedhausdorff";
    public static final String GEO_SCAN_INDEXED_HAUSDORFF = "geo_scanindexedhausdorff";

    public static final String GEO_MAX = "geo_max";
    public static final String GEO_MEAN = "geo_mean";
    public static final String GEO_MIN = "geo_min";
    public static final String GEO_AVG = "geo_avg";
    public static final String GEO_FRECHET = "geo_frechet";
    public static final String GEO_LINK = "geo_link";
    public static final String GEO_SUM_OF_MIN = "geo_sum_of_min";
    public static final String GEO_SURJECTION = "geo_surjection";
    public static final String GEO_FAIR_SURJECTION = "geo_fairsurjection";

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
     * @throws InvalidMeasureException
     */
    public static MeasureType getMeasureType(String expression) throws InvalidMeasureException {
        String measure = expression.toLowerCase();

        if (measure.startsWith(JAROWINKLER))
            return MeasureType.JAROWINKLER;
        
        if (measure.startsWith(JARO)) {
            return MeasureType.JARO;
        }
        if (measure.startsWith(QGRAMS)) {
            return MeasureType.QGRAMS;
        }
        if (measure.startsWith(COSINE)) {
            return MeasureType.COSINE;
        }
        if (measure.startsWith(LEVENSHTEIN)) {
            return MeasureType.LEVENSHTEIN;
        }
        if (measure.startsWith(OVERLAP)) {
            return MeasureType.OVERLAP;
        }
        if (measure.startsWith(TRIGRAMS)) {
            return MeasureType.TRIGRAMS;
        }
        if (measure.startsWith(JACCARD)) {
            return MeasureType.JACCARD;
        }
        if (measure.startsWith(EXACTMATCH)) {
            return MeasureType.EXACTMATCH;
        }
        if (measure.startsWith(SOUNDEX)) {
            return MeasureType.SOUNDEX;
        } ////////////////////////////
        if (measure.startsWith(EUCLIDEAN)) {
            return MeasureType.EUCLIDEAN;
        }
        /////////////////////////////
        if (measure.startsWith(GEO_ORTHODROMIC)) {
            return MeasureType.GEO_ORTHODROMIC;
        }
        if (measure.startsWith(GEO_HAUSDORFF)) {
            return MeasureType.GEO_HAUSDORFF;
        }
        if (measure.startsWith(GEO_NAIVE_HAUSDORFF)) {
            return MeasureType.GEO_NAIVE_HAUSDORFF;
        }
        if (measure.startsWith(GEO_INDEXED_HAUSDORFF)) {
            return MeasureType.GEO_INDEXED_HAUSDORFF;
        }
        if (measure.startsWith(GEO_FAST_HAUSDORFF)) {
            return MeasureType.GEO_FAST_HAUSDORFF;
        }
        if (measure.startsWith(GEO_SYMMETRIC_HAUSDORFF)) {
            return MeasureType.GEO_SYMMETRIC_HAUSDORFF;
        }
        if (measure.startsWith(GEO_CENTROID_INDEXED_HAUSDORFF)) {
            return MeasureType.GEO_CENTROID_INDEXED_HAUSDORFF;
        }
        if (measure.startsWith(GEO_SCAN_INDEXED_HAUSDORFF)) {
            return MeasureType.GEO_SCAN_INDEXED_HAUSDORFF;
        }
        //////////////////////////
        if (measure.startsWith(GEO_MAX)) {
            return MeasureType.GEO_MAX;
        }
        if (measure.startsWith(GEO_MEAN)) {
            return MeasureType.GEO_MEAN;
        }
        if (measure.startsWith(GEO_MIN)) {
            return MeasureType.GEO_MIN;
        }
        if (measure.startsWith(GEO_AVG)) {
            return MeasureType.GEO_AVG;
        }
        if (measure.startsWith(GEO_FRECHET)) {
            return MeasureType.GEO_FRECHET;
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
        }
        if (measure.startsWith(GEO_FAIR_SURJECTION)) {
            return MeasureType.GEO_FAIR_SURJECTION;
        }
        ////////////////////////////////////////////
        if (measure.startsWith(TMP_SUCCESSOR)) {
            return MeasureType.TMP_SUCCESSOR;
        }
        if (measure.startsWith(TMP_PREDECESSOR)) {
            return MeasureType.TMP_PREDECESSOR;
        }
        if (measure.startsWith(TMP_CONCURRENT)) {
            return MeasureType.TMP_CONCURRENT;
        }
        if (measure.startsWith(TMP_AFTER)) {
            return MeasureType.TMP_AFTER;
        }
        if (measure.startsWith(TMP_BEFORE)) {
            return MeasureType.TMP_BEFORE;
        }
        if (measure.startsWith(TMP_DURING_REVERSE)) {
            return MeasureType.TMP_DURING_REVERSE;
        }
        if (measure.startsWith(TMP_DURING)) {
            return MeasureType.TMP_DURING;
        }

        if (measure.startsWith(TMP_EQUALS)) {
            return MeasureType.TMP_EQUALS;
        }
        if (measure.startsWith(TMP_FINISHES)) {
            return MeasureType.TMP_FINISHES;
        }
        if (measure.startsWith(TMP_IS_FINISHED_BY)) {
            return MeasureType.TMP_IS_FINISHED_BY;
        }
        if (measure.startsWith(TMP_IS_MET_BY)) {
            return MeasureType.TMP_IS_MET_BY;
        }
        if (measure.startsWith(TMP_IS_OVERLAPPED_BY)) {
            return MeasureType.TMP_IS_OVERLAPPED_BY;
        }
        if (measure.startsWith(TMP_IS_STARTED_BY)) {
            return MeasureType.TMP_IS_STARTED_BY;
        }
        if (measure.startsWith(TMP_MEETS)) {
            return MeasureType.TMP_MEETS;
        }
        if (measure.startsWith(TMP_OVERLAPS)) {
            return MeasureType.TMP_OVERLAPS;
        }
        if (measure.startsWith(TMP_STARTS)) {
            return MeasureType.TMP_STARTS;
        } else
            throw new InvalidMeasureException(measure);
    }

    /**
     * Factory function for retrieving the desired measure instance.
     * 
     * @param type,
     *            Type of the measure
     * 
     * @return a specific measure instance
     * @throws InvalidMeasureException
     * 
     */
    public static Measure createMeasure(MeasureType type) throws InvalidMeasureException {

        switch (type) {
        case JAROWINKLER:
            return new JaroWinkler();
        case JARO:
            return new Jaro();
        case QGRAMS:
            return new QGramSimilarity();
        case COSINE:
            return new CosineMeasure();
        case LEVENSHTEIN:
            return new Levenshtein();
        case OVERLAP:
            return new TrigramMeasure();
        case TRIGRAMS:
            return new TrigramMeasure();
        case JACCARD:
            return new JaccardMeasure();
        case EXACTMATCH:
            return new ExactMatch();
        case SOUNDEX:
            return new SoundexMeasure();
        ///////////////////////
        case EUCLIDEAN:
            return new EuclideanMetric();
        ///////////////////////
        case GEO_ORTHODROMIC:
            return new GeoDistance();
        case GEO_HAUSDORFF:
            return new NaiveHausdorff();
        case GEO_NAIVE_HAUSDORFF:
            return new NaiveHausdorff();
        case GEO_INDEXED_HAUSDORFF:
            return new IndexedHausdorff();
        case GEO_FAST_HAUSDORFF:
            return new FastHausdorff();
        case GEO_SYMMETRIC_HAUSDORFF:
            return new SymmetricHausdorff();
        case GEO_CENTROID_INDEXED_HAUSDORFF:
            return new CentroidIndexedHausdorff();
        case GEO_SCAN_INDEXED_HAUSDORFF:
            return new ScanIndexedHausdorff();
        ///////////////////////
        case GEO_MAX:
            return new NaiveMax();
        case GEO_MEAN:
            return new NaiveMean();
        case GEO_MIN:
            return new NaiveMin();
        case GEO_AVG:
            return new NaiveAverage();
        case GEO_FRECHET:
            return new NaiveFrechet();
        case GEO_LINK:
            return new NaiveLink();
        case GEO_SUM_OF_MIN:
            return new NaiveSumOfMin();
        case GEO_SURJECTION:
            return new NaiveSurjection();
        case GEO_FAIR_SURJECTION:
            return new FairSurjection();
        ///////////////////////
        case TMP_SUCCESSOR:
            return new SuccessorMeasure();
        case TMP_PREDECESSOR:
            return new PredecessorMeasure();
        case TMP_CONCURRENT:
            return new ConcurrentMeasure();
        case TMP_AFTER:
            return new AfterMeasure();
        case TMP_BEFORE:
            return new BeforeMeasure();
        case TMP_MEETS:
            return new MeetsMeasure();
        case TMP_IS_MET_BY:
            return new IsMetByMeasure();
        case TMP_FINISHES:
            return new FinishesMeasure();
        case TMP_IS_FINISHED_BY:
            return new IsFinishedByMeasure();
        case TMP_STARTS:
            return new StartsMeasure();
        case TMP_IS_STARTED_BY:
            return new IsStartedByMeasure();
        case TMP_DURING:
            return new DuringMeasure();
        case TMP_DURING_REVERSE:
            return new DuringReverseMeasure();
        case TMP_OVERLAPS:
            return new OverlapsMeasure();
        case TMP_IS_OVERLAPPED_BY:
            return new IsOverlappedByMeasure();
        case TMP_EQUALS:
            return new EqualsMeasure();
        default:
            throw new InvalidMeasureException(type.toString());
        }

    }

}