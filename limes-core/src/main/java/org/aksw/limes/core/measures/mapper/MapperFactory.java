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
import org.aksw.limes.core.measures.measure.MeasureType;


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
    public static final String GEO_HAUSDORFF = "geo_hausdorff";
    public static final String GEO_NAIVE_HAUSDORFF = "geo_naivehausdorff";
    public static final String GEO_INDEXED_HAUSDORFF = "geo_indexedhausdorff";
    public static final String GEO_FAST_HAUSDORFF = "geo_fasthausdorff";
    public static final String GEO_SYMMETRIC_HAUSDORFF = "geo_symmetrichausdorff";
    public static final String GEO_CENTROID_INDEXED_HAUSDORFF = "geo_centroidindexedhausdorff";
    public static final String GEO_SCAN_INDEXED_HAUSDORFF = "geo_scanhausdorff";

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
    // TODO use MeasureType instead of mesure name string
    public static Mapper createMapper(MeasureType type) throws InvalidMeasureException {
        switch (type) {
        case JARO:
            return new JaroMapper();
        case QGRAMS:
            return new FastNGramMapper();
        case COSINE:
        case OVERLAP:
        case TRIGRAM:
        case JACCARD:
            return new PPJoinPlusPlus();
        case LEVENSHTEIN:
            return new EDJoin();

        case EXACTMATCH:
            return new ExactMatchMapper();
        case SOUNDEX:
            return new SoundexMapper();
        ///////////////////////
        case EUCLIDEAN:
            return new HR3();
        ///////////////////////
        case GEO_ORTHODROMIC:
        case GEO_HAUSDORFF:
        case GEO_NAIVE_HAUSDORFF:
        case GEO_INDEXED_HAUSDORFF:
        case GEO_FAST_HAUSDORFF:
        case GEO_CENTROID_INDEXED_HAUSDORFF:
        case GEO_SCAN_INDEXED_HAUSDORFF:
            return new OrchidMapper();
        case GEO_SYMMETRIC_HAUSDORFF:
            return new SymmetricHausdorffMapper();
        ///////////////////////
        case GEO_MAX:
        case GEO_MEAN:
        case GEO_MIN:
        case GEO_AVG:
        case GEO_FRECHET:
        case GEO_LINK:
        case GEO_SUM_OF_MIN:
        case GEO_SURJECTION:
        case GEO_FAIR_SURJECTION:
            return new OrchidMapper();
        ///////////////////////
        case TMP_SUCCESSOR:
            return new SuccessorMapper();
        case TMP_PREDECESSOR:
            return new PredecessorMapper();
        case TMP_CONCURRENT:
            return new ConcurrentMapper();
        case TMP_AFTER:
            return new AfterMapper();
        case TMP_BEFORE:
            return new BeforeMapper();
        case TMP_MEETS:
            return new MeetsMapper();
        case TMP_IS_MET_BY:
            return new IsMetByMapper();
        case TMP_FINISHES:
            return new FinishesMapper();
        case TMP_IS_FINISHED_BY:
            return new IsFinishedByMapper();
        case TMP_STARTS:
            return new StartsMapper();
        case TMP_IS_STARTED_BY:
            return new IsStartedByMapper();
        case TMP_DURING:
            return new DuringMapper();
        case TMP_DURING_REVERSE:
            return new DuringReverseMapper();
        case TMP_OVERLAPS:
            return new OverlapsMapper();
        case TMP_IS_OVERLAPPED_BY:
            return new IsOverlappedByMapper();
        case TMP_EQUALS:
            return new EqualsMapper();
        default:
            throw new InvalidMeasureException(type.toString());
        }

    }

}
