/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.measures.measure;

import org.aksw.limes.core.exceptions.InvalidMeasureException;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.execution.planning.planner.DynamicPlanner;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.measures.measure.phoneticmeasure.SoundexMeasure;
import org.aksw.limes.core.measures.measure.pointsets.average.NaiveAverageMeasure;
import org.aksw.limes.core.measures.measure.pointsets.frechet.NaiveFrechetMeasure;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.*;
import org.aksw.limes.core.measures.measure.pointsets.link.NaiveLinkMeasure;
import org.aksw.limes.core.measures.measure.pointsets.max.NaiveMaxMeasure;
import org.aksw.limes.core.measures.measure.pointsets.mean.NaiveMeanMeasure;
import org.aksw.limes.core.measures.measure.pointsets.min.NaiveMinMeasure;
import org.aksw.limes.core.measures.measure.pointsets.sumofmin.NaiveSumOfMinMeasure;
import org.aksw.limes.core.measures.measure.pointsets.surjection.FairSurjectionMeasure;
import org.aksw.limes.core.measures.measure.pointsets.surjection.NaiveSurjectionMeasure;
import org.aksw.limes.core.measures.measure.resourcesets.SetJaccardMeasure;
import org.aksw.limes.core.measures.measure.space.EuclideanMeasure;
import org.aksw.limes.core.measures.measure.space.GeoGreatEllipticMeasure;
import org.aksw.limes.core.measures.measure.space.GeoOrthodromicMeasure;
import org.aksw.limes.core.measures.measure.string.*;
import org.aksw.limes.core.measures.measure.temporal.allenAlgebra.EqualsMeasure;
import org.aksw.limes.core.measures.measure.temporal.allenAlgebra.OverlapsMeasure;
import org.aksw.limes.core.measures.measure.temporal.allenAlgebra.*;
import org.aksw.limes.core.measures.measure.temporal.simpleTemporal.ConcurrentMeasure;
import org.aksw.limes.core.measures.measure.temporal.simpleTemporal.PredecessorMeasure;
import org.aksw.limes.core.measures.measure.temporal.simpleTemporal.SuccessorMeasure;
import org.aksw.limes.core.measures.measure.topology.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MeasureFactoryTest {

    public ACache source = new MemoryCache();
    public ACache target = new MemoryCache();

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
        assertTrue(p.getAtomicRuntimeCosts("Trigrams", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("jaccard", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("exactmatch", 0.5) != 0.0d);
        assertTrue(p.getAtomicRuntimeCosts("soundex", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("mongeelkan", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("ratcliff", 0.5) != 0);

        assertTrue(p.getAtomicRuntimeCosts("euclidean", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("geo_orthodromic", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("geo_great_elliptic", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("Geo_Fast_Hausdorff", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("Geo_Symmetric_Hausdorff", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("Geo_Centroid_Indexed_Hausdorff", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("Geo_Scan_Indexed_Hausdorff", 0.5) != 0);

        assertTrue(p.getAtomicRuntimeCosts("geo_hausdorff", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("geo_naive_hausdorff", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("Geo_Indexed_Hausdorff", 0.5) != 0);
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
        assertTrue(p.getAtomicRuntimeCosts("geo_symmetric_hausdorff", 0.5) != 0);

        assertTrue(p.getAtomicRuntimeCosts("tmp_successor", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_predecessor", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_concurrent", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_before", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_after", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_meets", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_is_met_by", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_finishes", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_is_finished_by", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_starts", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_is_started_by", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_during", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_during_reverse", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_overlaps", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_is_overlapped_by", 0.5) != 0);
        assertTrue(p.getAtomicRuntimeCosts("tmp_equals", 0.5) != 0);

        assertTrue(p.getAtomicRuntimeCosts("Set_Jaccard", 0.5) != 0);

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
        assertTrue(p.getAtomicMappingSizes("mongeelkan", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("ratcliff", 0.5) != 0);

        assertTrue(p.getAtomicMappingSizes("euclidean", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("geo_orthodromic", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("geo_great_elliptic", 0.5) != 0);

        assertTrue(p.getAtomicMappingSizes("geo_hausdorff", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("geo_naive_hausdorff", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("Geo_Indexed_Hausdorff", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("Geo_Fast_Hausdorff", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("Geo_Symmetric_Hausdorff", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("Geo_Centroid_Indexed_Hausdorff", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("Geo_Scan_Indexed_Hausdorff", 0.5) != 0);

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
        assertTrue(p.getAtomicMappingSizes("geo_symmetric_hausdorff", 0.5) != 0);

        assertTrue(p.getAtomicMappingSizes("tmp_successor", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_predecessor", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_concurrent", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_before", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_after", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_meets", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_is_met_by", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_finishes", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_is_finished_by", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_starts", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_is_started_by", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_during", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_during_reverse", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_overlaps", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_is_overlapped_by", 0.5) != 0);
        assertTrue(p.getAtomicMappingSizes("tmp_equals", 0.5) != 0);

        assertTrue(p.getAtomicMappingSizes("Set_Jaccard", 0.5) != 0);

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
            assertTrue(MeasureFactory.getMeasureType("geo_symmetric_hausdorff") != null);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            assertTrue(MeasureFactory.getMeasureType("blabls") != null);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // @Test
    public void test() {
        MeasureType type = null;
        AMeasure measure = null;
        /////////////////////////////////////////////////////////////////////////
        String str = "jaro(x.name,y.name)";
        Instruction inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof JaroMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "qgrams(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof QGramSimilarityMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

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
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "overlap(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof TrigramMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "Trigrams(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof TrigramMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

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
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "levenshtein(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof LevenshteinMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "exactmatch(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof ExactMatchMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

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
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "mongeelkan(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof MongeElkanMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "RatcliffObershelp(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof RatcliffObershelpMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

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
            assertTrue(measure instanceof EuclideanMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "Geo_Orthodromic(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof GeoOrthodromicMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "Geo_Great_Elliptic(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof GeoGreatEllipticMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /////////////////////////////////////////////////

        str = "Geo_Hausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveHausdorffMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /////////////////////////////////////////////////

        str = "geo_naive_hausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveHausdorffMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_indexed_hausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof IndexedHausdorffMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_fast_hausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof FastHausdorffMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_symmetric_hausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof SymmetricHausdorffMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_centroid_indexed_hausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof CentroidIndexedHausdorffMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_scan_indexed_hausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof ScanIndexedHausdorffMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

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
            assertTrue(measure instanceof NaiveMaxMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_mean(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveMeanMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_min(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveMinMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_avg(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveAverageMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_frechet(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveFrechetMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_link(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveLinkMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_sum_of_min(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveSumOfMinMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_surjection(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof NaiveSurjectionMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_fairsurjection(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof FairSurjectionMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

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
            assertTrue(measure.getRuntimeApproximation(500) != 0);

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
            assertTrue(measure.getRuntimeApproximation(500) != 0);

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
            assertTrue(measure.getRuntimeApproximation(500) != 0);

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
            assertTrue(measure.getRuntimeApproximation(500) != 0);

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
            assertTrue(measure.getRuntimeApproximation(500) != 0);

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
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_is_met_by(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof IsMetByMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

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
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "tmp_is_finished_by(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof IsFinishedByMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

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
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_is_started_by(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof IsStartedByMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

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
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "tmp_during_reverse(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof DuringReverseMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

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
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_is_overlapped_by(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof IsOverlappedByMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

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
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /////////////////////////////////////////
        str = "top_contains(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof ContainsMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "top_covered_by(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof CoveredbyMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "top_covers(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof CoversMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "top_crosses(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof CrossesMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "top_disjoint(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof DisjointMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "top_equals(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof org.aksw.limes.core.measures.measure.topology.EqualsMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "top_intersects(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof IntersectsMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "top_overlaps(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof org.aksw.limes.core.measures.measure.topology.OverlapsMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "top_touches(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof TouchesMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "top_within(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof WithinMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // ////////////////////////////////
        str = "Set_Jaccard(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            measure = MeasureFactory.createMeasure(type);
            assertTrue(measure instanceof SetJaccardMeasure);
            assertTrue(measure.getRuntimeApproximation(500) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
