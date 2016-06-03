package org.aksw.limes.core.measures.measure;

import org.aksw.limes.core.exceptions.InvalidMeasureException;
import org.aksw.limes.core.execution.planning.planner.DynamicPlanner;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

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

}
