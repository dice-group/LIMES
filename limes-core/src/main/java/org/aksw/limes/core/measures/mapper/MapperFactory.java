package org.aksw.limes.core.measures.mapper;

import org.aksw.limes.core.exceptions.InvalidMeasureException;
import org.aksw.limes.core.measures.mapper.pointsets.OrchidMapper;
import org.aksw.limes.core.measures.mapper.pointsets.SymmetricHausdorffMapper;
import org.aksw.limes.core.measures.mapper.space.HR3;
import org.aksw.limes.core.measures.mapper.string.EDJoin;
import org.aksw.limes.core.measures.mapper.string.ExactMatchMapper;
import org.aksw.limes.core.measures.mapper.string.JaroMapper;
import org.aksw.limes.core.measures.mapper.string.PPJoinPlusPlus;
import org.aksw.limes.core.measures.mapper.string.SoundexMapper;
import org.aksw.limes.core.measures.mapper.string.fastngram.FastNGramMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex.AfterMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex.BeforeMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex.DuringMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex.DuringReverseMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex.EqualsMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex.FinishesMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex.IsFinishedByMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex.IsMetByMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex.IsOverlappedByMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex.IsStartedByMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex.MeetsMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex.OverlapsMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.complex.StartsMapper;
import org.aksw.limes.core.measures.mapper.temporal.simpleTemporal.ConcurrentMapper;
import org.aksw.limes.core.measures.mapper.temporal.simpleTemporal.PredecessorMapper;
import org.aksw.limes.core.measures.mapper.temporal.simpleTemporal.SuccessorMapper;

public class MapperFactory {
    
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
     * Factory function for retrieving the desired mapper instance given an
     * input measure name.
     * 
     * @param measure,
     *            Name of the measure
     * 
     * @return a specific mapper instance
     * @throws InvalidMeasureException
     * 
     */
    //TODO use MeasureType instead of mesure name string 
    public static Mapper createMapper(String measure) throws InvalidMeasureException {
        Mapper am = null;
        // String measures
        if (measure.toLowerCase().startsWith(COSINE)) {
            am = new PPJoinPlusPlus();
        } else if (measure.toLowerCase().startsWith(EXACTMATCH)) {
            am = new ExactMatchMapper();
        } else if (measure.toLowerCase().startsWith(JACCARD)) {
            am = new PPJoinPlusPlus();
        } else if (measure.toLowerCase().startsWith(JARO)) {
            am = new JaroMapper();
        } else if (measure.toLowerCase().startsWith(LEVENSHTEIN)) {
            am = new EDJoin();
        } else if (measure.toLowerCase().startsWith(OVERLAP)) {
            am = new PPJoinPlusPlus();
        } else if (measure.toLowerCase().startsWith(QGRAMS)) {
            am = new FastNGramMapper();
        } else if (measure.toLowerCase().startsWith(TRIGRAM)) {
            am = new PPJoinPlusPlus();
        } else if (measure.toLowerCase().startsWith(SOUNDEX)) {
            am = new SoundexMapper();
        } else if (measure.toLowerCase().startsWith(EUCLIDEAN)) {
            am = new HR3();
        } else 
            
            //point set measures
            if (measure.toLowerCase().startsWith(GEO_HAUSDORFF)) {
            am = new OrchidMapper();
        } else if (measure.toLowerCase().startsWith(GEO_ORTHODROMIC)) {
            // the hausdorff distance is the same as the orthodromic distance
            // for single points
            am = new OrchidMapper();
        } else if (measure.toLowerCase().startsWith(GEO_SYMMETRIC_HAUSDORFF)) {
            am = new SymmetricHausdorffMapper();
        } else if (measure.toLowerCase().startsWith(GEO_MIN)) {
            am = new OrchidMapper();
        } else if (measure.toLowerCase().startsWith(GEO_MAX)) {
            am = new OrchidMapper();
        } else if (measure.toLowerCase().startsWith(GEO_AVG)) {
            am = new OrchidMapper();
        } else if (measure.toLowerCase().startsWith(GEO_MEAN)) {
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
        } else 
            
            // temporal measures
            if (measure.toLowerCase().startsWith(TMP_SUCCESSOR)) {     
            am = new SuccessorMapper();
        } else if (measure.toLowerCase().startsWith(TMP_PREDECESSOR)) {
            am = new PredecessorMapper();
        } else if (measure.toLowerCase().startsWith(TMP_CONCURRENT)) {
            am = new ConcurrentMapper();
        } else if (measure.toLowerCase().startsWith(TMP_BEFORE)) {
            am = new BeforeMapper();
        } else if (measure.toLowerCase().startsWith(TMP_AFTER)) {
            am = new AfterMapper();
        } else if (measure.toLowerCase().startsWith(TMP_MEETS)) {
            am = new MeetsMapper();
        } else if (measure.toLowerCase().startsWith(TMP_IS_MET_BY)) {
            am = new IsMetByMapper();
        } else if (measure.toLowerCase().startsWith(TMP_FINISHES)) {
            am = new FinishesMapper();
        } else if (measure.toLowerCase().startsWith(TMP_IS_FINISHED_BY)) {
            am = new IsFinishedByMapper();
        } else if (measure.toLowerCase().startsWith(TMP_STARTS)) {
            am = new StartsMapper();
        } else if (measure.toLowerCase().startsWith(TMP_IS_STARTED_BY)) {
            am = new IsStartedByMapper();
        } else if (measure.toLowerCase().startsWith(TMP_DURING_REVERSE)) {
            am = new DuringReverseMapper();
        } else if (measure.toLowerCase().startsWith(TMP_DURING)) {
            am = new DuringMapper();
        } else if (measure.toLowerCase().startsWith(TMP_OVERLAPS)) {
            am = new OverlapsMapper();
        } else if (measure.toLowerCase().startsWith(TMP_IS_OVERLAPPED_BY)) {
            am = new IsOverlappedByMapper();
        } else if (measure.toLowerCase().startsWith(TMP_EQUALS)) {
            am = new EqualsMapper();
        } else {
            throw new InvalidMeasureException(measure);
        }
        return am;

    }

}
