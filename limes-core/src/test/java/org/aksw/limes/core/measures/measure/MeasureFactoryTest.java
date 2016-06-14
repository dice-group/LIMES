package org.aksw.limes.core.measures.measure;

import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.exceptions.InvalidMeasureException;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.execution.planning.planner.DynamicPlanner;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.MemoryCache;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MeasureFactoryTest {

    public Cache source = new MemoryCache();
    public Cache target = new MemoryCache();

    @Before
    public void setUp() {
        source = new MemoryCache();
        target = new MemoryCache();
        // create source cache
        source.addTriple("S1", "surname", "georgala");
        source.addTriple("S1", "name", "kleanthi");
        source.addTriple("S1", "age", "26");

        source.addTriple("S2", "surname", "sandra");
        source.addTriple("S2", "name", "lukas");
        source.addTriple("S2", "age", "13");

        source.addTriple("S3", "surname", "depp");
        source.addTriple("S3", "name", "johny");
        source.addTriple("S3", "age", "52");

        source.addTriple("S4", "surname", "swift");
        source.addTriple("S4", "name", "taylor,maria");
        source.addTriple("S4", "age", "25");

        source.addTriple("S5", "surname", "paok");
        source.addTriple("S5", "name", "ole");
        source.addTriple("S5", "age", "56");

        target.addTriple("T1", "surname", "georg");
        target.addTriple("T1", "name", "klea");
        target.addTriple("T1", "age", "26");

        target.addTriple("T2", "surname", "sandra");
        target.addTriple("T2", "name", "lukas");
        target.addTriple("T2", "age", "13");

        target.addTriple("T3", "surname", "derp");
        target.addTriple("T3", "name", "johnny");
        target.addTriple("T3", "age", "52");

        target.addTriple("T4", "surname", "swift");
        target.addTriple("T4", "name", "taylor");
        target.addTriple("T4", "age", "25");

        target.addTriple("T5", "surname", "paok");
        target.addTriple("T5", "name", "oleole");
        target.addTriple("T5", "age", "56");

    }

    @After
    public void tearDown() {

    }

    @Test
    public void runtimeApproximation() {
        System.out.println("runtimeApproximation");
        DynamicPlanner p = new DynamicPlanner(source, target);

        assertTrue(p.getAtomicRuntimeCosts("jaro", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("qgrams", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("cosine", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("levenshtein", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("overlap", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("trigram", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("jaccard", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("exactmatch", 0.5) != 0.0d);
        assertTrue(p.getAtomicRuntimeCosts("soundex", 0.5) != 0);

        assertTrue(p.getAtomicRuntimeCosts("euclidean", 0.5) != 0);

        assertTrue(p.getAtomicRuntimeCosts("geo_orthodromic", 0.5) != 0);
        // assertTrue(p.getAtomicRuntimeCosts("geo_elliptic", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("geo_hausdorff", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("geo_fairsurjection", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("geo_max", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("geo_mean", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("geo_min", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("geo_avg", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("geo_frechet", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("geo_link", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("geo_sum_of_min", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("geo_surjection", 0.5) != 0);
        // assertTrue(p.getAtomicRuntimeCosts("geo_quinlan", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("geo_symmetrichausdorff", 0.5) != 0);

        assertTrue(p.getAtomicRuntimeCosts("tmp_successor", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_predecessor", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_concurrent", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_before", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_after", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_meets", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_ismetby", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_finishes", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_isfinishedby", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_starts", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_isstartedby", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_during", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_duringreverse", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_overlaps", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_isoverlappedby", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_equals", 0.5) != 0);

    }

    @Test
    public void mappingApproximation() {
        System.out.println("mappingApproximation");
        DynamicPlanner p = new DynamicPlanner(source, target);

        assertTrue(p.getAtomicMappingSizes("jaro", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("qgrams", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("cosine", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("levenshtein", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("overlap", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("trigram", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("jaccard", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("exactmatch", 0.5) != 0.0d);
        assertTrue(p.getAtomicMappingSizes("soundex", 0.5) != 0);

        assertTrue(p.getAtomicMappingSizes("euclidean", 0.5) != 0);

        assertTrue(p.getAtomicMappingSizes("geo_orthodromic", 0.5) != 0);
        // assertTrue(p.getAtomicMappingSizes("geo_elliptic", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("geo_hausdorff", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("geo_fairsurjection", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("geo_max", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("geo_mean", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("geo_min", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("geo_avg", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("geo_frechet", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("geo_link", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("geo_sum_of_min", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("geo_surjection", 0.5) != 0);
        // assertTrue(p.getAtomicMappingSizes("geo_quinlan", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("geo_symmetrichausdorff", 0.5) != 0);

        assertTrue(p.getAtomicMappingSizes("tmp_successor", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_predecessor", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_concurrent", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_before", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_after", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_meets", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_ismetby", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_finishes", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_isfinishedby", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_starts", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_isstartedby", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_during", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_duringreverse", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_overlaps", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_isoverlappedby", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_equals", 0.5) != 0);

    }

    @Test
    public void measureType() {
        System.out.println("mappingApproximation");

        try {
            assertTrue(MeasureFactory.getMeasureType("geo_orthodromic") != null);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            assertTrue(MeasureFactory.getMeasureType("geo_hausdorff") != null);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            assertTrue(MeasureFactory.getMeasureType("geo_fairsurjection") != null);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            assertTrue(MeasureFactory.getMeasureType("geo_max") != null);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            assertTrue(MeasureFactory.getMeasureType("geo_mean") != null);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            assertTrue(MeasureFactory.getMeasureType("geo_min") != null);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            assertTrue(MeasureFactory.getMeasureType("geo_avg") != null);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            assertTrue(MeasureFactory.getMeasureType("geo_frechet") != null);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            assertTrue(MeasureFactory.getMeasureType("geo_link") != null);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            assertTrue(MeasureFactory.getMeasureType("geo_sum_of_min") != null);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            assertTrue(MeasureFactory.getMeasureType("geo_surjection") != null);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            assertTrue(MeasureFactory.getMeasureType("geo_symmetrichausdorff") != null);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    public void test() {
        MeasureType type = null;
        Measure measure = null;
        /////////////////////////////////////////////////////////////////////////
        String str = "jaro(x.name,y.name)";
        Instruction inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof Jaro);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "qgrams(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof QGramSimilarity);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "cosine(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof CosineMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "overlap(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof OverlapMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "trigrams(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof TrigramMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "jaccard(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof JaccardMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "levenshtein(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof Levenshtein);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "exactmatch(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof ExactMatch);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "soundex(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof SoundexMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /////////////////////////////////////////////////
        str = "euclidean(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof EuclideanMetric);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /////////////////////////////////////////////////
        str = "geo_orthodromic(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof GeoDistance);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_hausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveHausdorff);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_naivehausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveHausdorff);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_indexedhausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof IndexedHausdorff);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_fasthausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof FastHausdorff);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_symmetrichausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof SymmetricHausdorff);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_centroidindexedhausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof CentroidIndexedHausdorff);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_scanindexedhausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof ScanIndexedHausdorff);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /////////////////////////////////////////////////
        str = "geo_max(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveMax);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_mean(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveMean);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_min(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveMin);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_avg(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveAverage);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_frechet(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveFrechet);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_link(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveLink);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_sum_of_min(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveSumOfMin);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_surjection(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveSurjection);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_fairsurjection(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof FairSurjection);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ///////////////////////////////
        str = "tmp_successor(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof SuccessorMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_predecessor(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof PredecessorMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_concurrent(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof ConcurrentMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_after(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof AfterMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_before(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof BeforeMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_meets(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof MeetsMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_ismetby(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof IsMetByMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_finishes(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof FinishesMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "tmp_isfinishedby(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof IsFinishedByMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_starts(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof StartsMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_isstartedby(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof IsStartedByMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "tmp_during(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof DuringMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "tmp_duringreverse(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof DuringReverseMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "tmp_overlaps(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof OverlapsMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_isoverlappedby(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof IsOverlappedByMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "tmp_equals(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof EqualsMeasure);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
