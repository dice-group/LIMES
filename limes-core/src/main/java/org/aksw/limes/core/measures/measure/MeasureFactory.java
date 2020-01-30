package org.aksw.limes.core.measures.measure;

import org.aksw.limes.core.exceptions.InvalidMeasureException;
import org.aksw.limes.core.exceptions.NullIndexerException;
import org.aksw.limes.core.measures.measure.phoneticmeasure.DoubleMetaphoneMeasure;
import org.aksw.limes.core.measures.measure.phoneticmeasure.KoelnPhoneticMeasure;
import org.aksw.limes.core.measures.measure.phoneticmeasure.SoundexMeasure;
import org.aksw.limes.core.measures.measure.pointsets.average.NaiveAverageMeasure;
import org.aksw.limes.core.measures.measure.pointsets.frechet.NaiveFrechetMeasure;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.CentroidIndexedHausdorffMeasure;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.FastHausdorffMeasure;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.IndexedHausdorffMeasure;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.NaiveHausdorffMeasure;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.ScanIndexedHausdorffMeasure;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.SymmetricHausdorffMeasure;
import org.aksw.limes.core.measures.measure.pointsets.link.NaiveLinkMeasure;
import org.aksw.limes.core.measures.measure.pointsets.max.NaiveMaxMeasure;
import org.aksw.limes.core.measures.measure.pointsets.mean.NaiveMeanMeasure;
import org.aksw.limes.core.measures.measure.pointsets.min.NaiveMinMeasure;
import org.aksw.limes.core.measures.measure.pointsets.sumofmin.NaiveSumOfMinMeasure;
import org.aksw.limes.core.measures.measure.pointsets.surjection.FairSurjectionMeasure;
import org.aksw.limes.core.measures.measure.pointsets.surjection.NaiveSurjectionMeasure;
import org.aksw.limes.core.measures.measure.resourcesets.SetJaccardMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.AIndex;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.memory.MemoryIndex;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.measures.LCHMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.measures.LiMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.measures.ShortestPathMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.measures.WuPalmerMeasure;
import org.aksw.limes.core.measures.measure.space.EuclideanMeasure;
import org.aksw.limes.core.measures.measure.space.GeoGreatEllipticMeasure;
import org.aksw.limes.core.measures.measure.space.GeoOrthodromicMeasure;
import org.aksw.limes.core.measures.measure.space.ManhattanMeasure;
import org.aksw.limes.core.measures.measure.string.CosineMeasure;
import org.aksw.limes.core.measures.measure.string.ExactMatchMeasure;
import org.aksw.limes.core.measures.measure.string.JaccardMeasure;
import org.aksw.limes.core.measures.measure.string.JaroMeasure;
import org.aksw.limes.core.measures.measure.string.JaroWinklerMeasure;
import org.aksw.limes.core.measures.measure.string.LevenshteinMeasure;
import org.aksw.limes.core.measures.measure.string.MongeElkanMeasure;
import org.aksw.limes.core.measures.measure.string.QGramSimilarityMeasure;
import org.aksw.limes.core.measures.measure.string.RatcliffObershelpMeasure;
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
import org.aksw.limes.core.measures.measure.topology.ContainsMeasure;
import org.aksw.limes.core.measures.measure.topology.CoveredbyMeasure;
import org.aksw.limes.core.measures.measure.topology.CoversMeasure;
import org.aksw.limes.core.measures.measure.topology.CrossesMeasure;
import org.aksw.limes.core.measures.measure.topology.DisjointMeasure;
import org.aksw.limes.core.measures.measure.topology.IntersectsMeasure;
import org.aksw.limes.core.measures.measure.topology.TouchesMeasure;
import org.aksw.limes.core.measures.measure.topology.WithinMeasure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the measure factory class. For each measure name, the factory
 * returns an object of the corresponding measure.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 *
 * @version 1.0
 */
public class MeasureFactory {
    static Logger logger = LoggerFactory.getLogger(MeasureFactory.class);

    // String measures
    public static final String COSINE = "cosine";
    public static final String EXACTMATCH = "exactmatch";
    public static final String JACCARD = "jaccard";
    public static final String JARO = "jaro";
    public static final String JAROWINKLER = "jarowinkler";
    public static final String LEVENSHTEIN = "levenshtein";
    public static final String MONGEELKAN = "mongeelkan";
    public static final String OVERLAP = "overlap";
    public static final String QGRAMS = "qgrams";
    public static final String RATCLIFF = "ratcliff";
    public static final String SOUNDEX = "soundex";
    public static final String DOUBLEMETA = "doublemeta";
    public static final String KOELN = "koeln";
    public static final String TRIGRAM = "trigram";
    public static final String META = "meta";
    public static final String NYSIIS = "nysiis";
    public static final String CAVERPHONE1 = "caverphone1";
    public static final String CAVERPHONE2 = "caverphone2";
    public static final String REFINEDSOUNDEX = "refinedsoundex";
    public static final String MATCHRATING = "matchrating";
    public static final String DAITCHMOKOTOFF = "daitchmokotoff";
    // vector space measures
    public static final String EUCLIDEAN = "euclidean";
    public static final String MANHATTAN = "manhattan";
    public static final String GEO_ORTHODROMIC = "geo_orthodromic";
    public static final String GEO_GREAT_ELLIPTIC = "geo_great_elliptic";

    // Point-set measures
    public static final String GEO_CENTROID_INDEXED_HAUSDORFF = "geo_centroid_indexed_hausdorff";
    public static final String GEO_FAST_HAUSDORFF = "geo_fast_hausdorff";
    public static final String GEO_HAUSDORFF = "geo_hausdorff";
    public static final String GEO_INDEXED_HAUSDORFF = "geo_indexed_hausdorff";
    public static final String GEO_NAIVE_HAUSDORFF = "geo_naive_hausdorff";
    public static final String GEO_SCAN_INDEXED_HAUSDORFF = "geo_scan_indexed_hausdorff";
    public static final String GEO_SYMMETRIC_HAUSDORFF = "geo_symmetric_hausdorff";

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
    public static final String TMP_CONCURRENT = "tmp_concurrent";
    public static final String TMP_PREDECESSOR = "tmp_predecessor";
    public static final String TMP_SUCCESSOR = "tmp_successor";

    public static final String TMP_AFTER = "tmp_after";
    public static final String TMP_BEFORE = "tmp_before";
    public static final String TMP_DURING = "tmp_during";
    public static final String TMP_DURING_REVERSE = "tmp_during_reverse";
    public static final String TMP_EQUALS = "tmp_equals";
    public static final String TMP_FINISHES = "tmp_finishes";
    public static final String TMP_IS_FINISHED_BY = "tmp_is_finished_by";
    public static final String TMP_IS_MET_BY = "tmp_is_met_by";
    public static final String TMP_IS_OVERLAPPED_BY = "tmp_is_overlapped_by";
    public static final String TMP_IS_STARTED_BY = "tmp_is_started_by";
    public static final String TMP_MEETS = "tmp_meets";
    public static final String TMP_OVERLAPS = "tmp_overlaps";
    public static final String TMP_STARTS = "tmp_starts";

    // Topological measures
    public static final String TOP_CONTAINS = "top_contains";
    public static final String TOP_COVERED_BY = "top_covered_by";
    public static final String TOP_COVERS = "top_covers";
    public static final String TOP_CROSSES = "top_crosses";
    public static final String TOP_DISJOINT = "top_disjoint";
    public static final String TOP_EQUALS = "top_equals";
    public static final String TOP_INTERSECTS = "top_intersects";
    public static final String TOP_OVERLAPS = "top_overlaps";
    public static final String TOP_TOUCHES = "top_touches";
    public static final String TOP_WITHIN = "top_within";

    // Resource set measures
    public static final String SET_JACCARD = "set_jaccard";
    // Semantic edge-counting measures
    public static final String SHORTEST_PATH = "shortest_path";
    public static final String LCH = "lch";
    public static final String LI = "li";
    public static final String WUPALMER = "wupalmer";

    /**
     * Factory function for retrieving a measure name from the set of allowed
     * types.
     *
     * @param expression,
     *            The name/type of the measure.
     * @return a specific measure type
     */
    public static MeasureType getMeasureType(String expression) {
        String measure = expression.toLowerCase();

        if (measure.startsWith(COSINE)) {
            return MeasureType.COSINE;
        }
        if (measure.startsWith(EXACTMATCH)) {
            return MeasureType.EXACTMATCH;
        }
        if (measure.startsWith(JACCARD)) {
            return MeasureType.JACCARD;
        }
        // DO NOT CHANGE THE ORDER OF THE FOLLOWING TWO
        if (measure.startsWith(JAROWINKLER))
            return MeasureType.JAROWINKLER;

        if (measure.startsWith(JARO)) {
            return MeasureType.JARO;
        }

        if (measure.startsWith(LEVENSHTEIN)) {
            return MeasureType.LEVENSHTEIN;
        }
        if (measure.startsWith(MONGEELKAN)) {
            return MeasureType.MONGEELKAN;
        }
        if (measure.startsWith(OVERLAP)) {
            return MeasureType.OVERLAP;
        }
        if (measure.startsWith(QGRAMS)) {
            return MeasureType.QGRAMS;
        }
        if (measure.startsWith(RATCLIFF)) {
            return MeasureType.RATCLIFF;
        }
        if (measure.startsWith(SOUNDEX)) {
            return MeasureType.SOUNDEX;
        }
        if (measure.startsWith(DOUBLEMETA)) {
            return MeasureType.DOUBLEMETA;
        }
        if (measure.startsWith(KOELN)) {
            return MeasureType.KOELN;
        }
        if (measure.startsWith(META)) {
            return MeasureType.META;
        }
        if (measure.startsWith(REFINEDSOUNDEX)) {
            return MeasureType.REFINEDSOUNDEX;
        }
        if (measure.startsWith(NYSIIS)) {
            return MeasureType.NYSIIS;
        }
        if (measure.startsWith(MATCHRATING)) {
            return MeasureType.MATCHRATING;
        }
        if (measure.startsWith(CAVERPHONE1)) {
            return MeasureType.CAVERPHONE1;
        }
        if (measure.startsWith(CAVERPHONE2)) {
            return MeasureType.CAVERPHONE2;
        }
        if (measure.startsWith(DAITCHMOKOTOFF)) {
            return MeasureType.DAITCHMOKOTOFF;
        }
        if (measure.startsWith(TRIGRAM)) {
            return MeasureType.TRIGRAM;
        }
        ////////////////////////////
        if (measure.startsWith(EUCLIDEAN)) {
            return MeasureType.EUCLIDEAN;
        }
        if (measure.startsWith(MANHATTAN)) {
            return MeasureType.MANHATTAN;
        }
        if (measure.startsWith(GEO_ORTHODROMIC)) {
            return MeasureType.GEO_ORTHODROMIC;
        }

        if (measure.startsWith(GEO_GREAT_ELLIPTIC)) {
            return MeasureType.GEO_GREAT_ELLIPTIC;
        }
        /////////////////////////////
        if (measure.startsWith(GEO_CENTROID_INDEXED_HAUSDORFF)) {
            return MeasureType.GEO_CENTROID_INDEXED_HAUSDORFF;
        }
        if (measure.startsWith(GEO_FAST_HAUSDORFF)) {
            return MeasureType.GEO_FAST_HAUSDORFF;
        }
        if (measure.startsWith(GEO_HAUSDORFF)) {
            return MeasureType.GEO_HAUSDORFF;
        }
        if (measure.startsWith(GEO_INDEXED_HAUSDORFF)) {
            return MeasureType.GEO_INDEXED_HAUSDORFF;
        }
        if (measure.startsWith(GEO_NAIVE_HAUSDORFF)) {
            return MeasureType.GEO_NAIVE_HAUSDORFF;
        }
        if (measure.startsWith(GEO_SCAN_INDEXED_HAUSDORFF)) {
            return MeasureType.GEO_SCAN_INDEXED_HAUSDORFF;
        }
        if (measure.startsWith(GEO_SYMMETRIC_HAUSDORFF)) {
            return MeasureType.GEO_SYMMETRIC_HAUSDORFF;
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
            return MeasureType.GEO_NAIVE_SURJECTION;
        }
        if (measure.startsWith(GEO_FAIR_SURJECTION)) {
            return MeasureType.GEO_FAIR_SURJECTION;
        }
        if (measure.startsWith(GEO_FAIR_SURJECTION)) {
            return MeasureType.GEO_FAIR_SURJECTION;
        }
        ////////////////////////////////////////////
        if (measure.startsWith(TMP_CONCURRENT)) {
            return MeasureType.TMP_CONCURRENT;
        }
        if (measure.startsWith(TMP_PREDECESSOR)) {
            return MeasureType.TMP_PREDECESSOR;
        }
        if (measure.startsWith(TMP_SUCCESSOR)) {
            return MeasureType.TMP_SUCCESSOR;
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
        } ////////////////////////////////////////////////////
        if (measure.startsWith(TOP_CONTAINS)) {
            return MeasureType.TOP_CONTAINS;
        }
        if (measure.startsWith(TOP_COVERED_BY)) {
            return MeasureType.TOP_COVERED_BY;
        }
        if (measure.startsWith(TOP_COVERS)) {
            return MeasureType.TOP_COVERS;
        }
        if (measure.startsWith(TOP_CROSSES)) {
            return MeasureType.TOP_CROSSES;
        }
        if (measure.startsWith(TOP_DISJOINT)) {
            return MeasureType.TOP_DISJOINT;
        }
        if (measure.startsWith(TOP_EQUALS)) {
            return MeasureType.TOP_EQUALS;
        }
        if (measure.startsWith(TOP_INTERSECTS)) {
            return MeasureType.TOP_INTERSECTS;
        }
        if (measure.startsWith(TOP_OVERLAPS)) {
            return MeasureType.TOP_OVERLAPS;
        }
        if (measure.startsWith(TOP_TOUCHES)) {
            return MeasureType.TOP_TOUCHES;
        }
        if (measure.startsWith(TOP_WITHIN)) {
            return MeasureType.TOP_WITHIN;
        }

        ////////////////////////////////////////////////////
        if (measure.startsWith(SET_JACCARD)) {
            return MeasureType.SET_JACCARD;
        }

        ////////////////////////////////////////////////////
        if (measure.startsWith(SHORTEST_PATH)) {
            return MeasureType.SHORTEST_PATH;
        }
        if (measure.startsWith(LCH)) {
            return MeasureType.LCH;
        }
        if (measure.startsWith(LI)) {
            return MeasureType.LI;
        }
        if (measure.startsWith(WUPALMER)) {
            return MeasureType.WUPALMER;
        }
        throw new InvalidMeasureException(measure);
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
    public static AMeasure createMeasure(MeasureType type) {

        switch (type) {
        case COSINE:
            return new CosineMeasure();
        case EXACTMATCH:
            return new ExactMatchMeasure();
        case JACCARD:
            return new JaccardMeasure();
        // DO NOT CHANGE THE ORDER OF THE FOLLOWING TWO
        case JAROWINKLER:
            return new JaroWinklerMeasure();
        case JARO:
            return new JaroMeasure();
        case LEVENSHTEIN:
            return new LevenshteinMeasure();
        case MONGEELKAN:
            return new MongeElkanMeasure();
        case OVERLAP:
            return new TrigramMeasure();
        case QGRAMS:
            return new QGramSimilarityMeasure();
        case RATCLIFF:
            return new RatcliffObershelpMeasure();
        case SOUNDEX:
            return new SoundexMeasure();
        case DOUBLEMETA:
            return new DoubleMetaphoneMeasure();
        case KOELN:
            return new KoelnPhoneticMeasure();
        case TRIGRAM:
            return new TrigramMeasure();
        ////////////////////////////////////////////

        case EUCLIDEAN:
            return new EuclideanMeasure();
        case MANHATTAN:
            return new ManhattanMeasure();
        case GEO_GREAT_ELLIPTIC:
            return new GeoGreatEllipticMeasure();
        case GEO_ORTHODROMIC:
            return new GeoOrthodromicMeasure();
        ///////////////////////
        case GEO_CENTROID_INDEXED_HAUSDORFF:
            return new CentroidIndexedHausdorffMeasure();
        case GEO_FAST_HAUSDORFF:
            return new FastHausdorffMeasure();
        case GEO_HAUSDORFF:
            return new NaiveHausdorffMeasure();
        case GEO_INDEXED_HAUSDORFF:
            return new IndexedHausdorffMeasure();
        case GEO_NAIVE_HAUSDORFF:
            return new NaiveHausdorffMeasure();
        case GEO_SCAN_INDEXED_HAUSDORFF:
            return new ScanIndexedHausdorffMeasure();
        case GEO_SYMMETRIC_HAUSDORFF:
            return new SymmetricHausdorffMeasure();
        ///////////////////////
        case GEO_MAX:
            return new NaiveMaxMeasure();
        case GEO_MEAN:
            return new NaiveMeanMeasure();
        case GEO_MIN:
            return new NaiveMinMeasure();
        case GEO_AVG:
            return new NaiveAverageMeasure();
        case GEO_FRECHET:
            return new NaiveFrechetMeasure();
        case GEO_LINK:
            return new NaiveLinkMeasure();
        case GEO_SUM_OF_MIN:
            return new NaiveSumOfMinMeasure();
        case GEO_NAIVE_SURJECTION:
            return new NaiveSurjectionMeasure();
        case GEO_FAIR_SURJECTION:
            return new FairSurjectionMeasure();
        ///////////////////////
        case TMP_CONCURRENT:
            return new ConcurrentMeasure();
        case TMP_PREDECESSOR:
            return new PredecessorMeasure();
        case TMP_SUCCESSOR:
            return new SuccessorMeasure();

        case TMP_AFTER:
            return new AfterMeasure();
        case TMP_BEFORE:
            return new BeforeMeasure();
        case TMP_DURING_REVERSE:
            return new DuringReverseMeasure();
        case TMP_DURING:
            return new DuringMeasure();
        case TMP_EQUALS:
            return new EqualsMeasure();
        case TMP_FINISHES:
            return new FinishesMeasure();
        case TMP_IS_FINISHED_BY:
            return new IsFinishedByMeasure();
        case TMP_IS_MET_BY:
            return new IsMetByMeasure();
        case TMP_IS_OVERLAPPED_BY:
            return new IsOverlappedByMeasure();
        case TMP_IS_STARTED_BY:
            return new IsStartedByMeasure();
        case TMP_MEETS:
            return new MeetsMeasure();
        case TMP_OVERLAPS:
            return new OverlapsMeasure();
        case TMP_STARTS:
            return new StartsMeasure();
        ///////////////////////
        case TOP_CONTAINS:
            return new ContainsMeasure();
        case TOP_COVERED_BY:
            return new CoveredbyMeasure();
        case TOP_COVERS:
            return new CoversMeasure();
        case TOP_CROSSES:
            return new CrossesMeasure();
        case TOP_DISJOINT:
            return new DisjointMeasure();
        case TOP_EQUALS:
            return new org.aksw.limes.core.measures.measure.topology.EqualsMeasure();
        case TOP_INTERSECTS:
            return new IntersectsMeasure();
        case TOP_OVERLAPS:
            return new org.aksw.limes.core.measures.measure.topology.OverlapsMeasure();
        case TOP_TOUCHES:
            return new TouchesMeasure();
        case TOP_WITHIN:
            return new WithinMeasure();

        ///////////////////////
        case SET_JACCARD:
            return new SetJaccardMeasure();

        ///////////////////////
        case SHORTEST_PATH:
            AIndex IndexerSP = createIndexer();
            if (IndexerSP == null) {
                throw new NullIndexerException("Cannot initialize " + SHORTEST_PATH + ". Index instance is null.");
            }
            return new ShortestPathMeasure(IndexerSP);
        case LCH:
            AIndex IndexerLCH = createIndexer();
            if (IndexerLCH == null) {
                throw new NullIndexerException("Cannot initialize " + LCH + ". Index instance is null.");
            }
            return new LCHMeasure(IndexerLCH);
        case LI:
            AIndex IndexerLi = createIndexer();
            if (IndexerLi == null) {
                throw new NullIndexerException("Cannot initialize " + LI + ". Index instance is null.");
            }
            return new LiMeasure(IndexerLi);
        case WUPALMER:
            AIndex IndexerWP = createIndexer();
            if (IndexerWP == null) {
                throw new NullIndexerException("Cannot initialize " + WUPALMER + ". Index instance is null.");
            }
            return new WuPalmerMeasure(IndexerWP);
        default:
            throw new InvalidMeasureException(type.toString());
        }

    }

    public static AIndex createIndexer() {
        AIndex Indexer = new MemoryIndex();
        Indexer.preIndex();
        return Indexer;
    }

}