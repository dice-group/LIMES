package org.aksw.limes.core.measures.mapper;

import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.exceptions.InvalidMeasureException;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.measures.mapper.IMapper.Language;
import org.aksw.limes.core.measures.mapper.pointsets.OrchidMapper;
import org.aksw.limes.core.measures.mapper.pointsets.SymmetricHausdorffMapper;
import org.aksw.limes.core.measures.mapper.space.HR3Mapper;
import org.aksw.limes.core.measures.mapper.string.EDJoinMapper;
import org.aksw.limes.core.measures.mapper.string.ExactMatchMapper;
import org.aksw.limes.core.measures.mapper.string.JaroMapper;
import org.aksw.limes.core.measures.mapper.string.MongeElkanMapper;
import org.aksw.limes.core.measures.mapper.string.PPJoinPlusPlus;
import org.aksw.limes.core.measures.mapper.string.RatcliffObershelpMapper;
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
import org.aksw.limes.core.measures.mapper.topology.ContainsMapper;
import org.aksw.limes.core.measures.mapper.topology.CoveredbyMapper;
import org.aksw.limes.core.measures.mapper.topology.CoversMapper;
import org.aksw.limes.core.measures.mapper.topology.CrossesMapper;
import org.aksw.limes.core.measures.mapper.topology.DisjointMapper;
import org.aksw.limes.core.measures.mapper.topology.IntersectsMapper;
import org.aksw.limes.core.measures.mapper.topology.TouchesMapper;
import org.aksw.limes.core.measures.mapper.topology.WithinMapper;
import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MapperFactoryTest {
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
    public void test() {
        MeasureType type = null;
        AMapper mapper = null;
        /////////////////////////////////////////////////////////////////////////
        String str = "jaro(x.name,y.name)";
        Instruction inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof JaroMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
            assertTrue(mapper.getMappingSizeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "qgrams(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof FastNGramMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
            assertTrue(mapper.getMappingSizeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);

        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "cosine(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof PPJoinPlusPlus);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
            assertTrue(mapper.getMappingSizeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "overlap(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof PPJoinPlusPlus);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
            assertTrue(mapper.getMappingSizeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "trigrams(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof PPJoinPlusPlus);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
            assertTrue(mapper.getMappingSizeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "jaccard(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof PPJoinPlusPlus);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
            assertTrue(mapper.getMappingSizeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "levenshtein(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof EDJoinMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
            assertTrue(mapper.getMappingSizeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "exactmatch(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof ExactMatchMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
            assertTrue(mapper.getMappingSizeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "soundex(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof SoundexMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
            assertTrue(mapper.getMappingSizeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "mongeelkan(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof MongeElkanMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
            assertTrue(mapper.getMappingSizeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "ratcliff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof RatcliffObershelpMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
            assertTrue(mapper.getMappingSizeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /////////////////////////////////////////////////
        str = "euclidean(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof HR3Mapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
            assertTrue(mapper.getMappingSizeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_orthodromic(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
            assertTrue(mapper.getMappingSizeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "Geo_Great_Elliptic(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
            assertTrue(mapper.getMappingSizeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /////////////////////////////////////////////////////////
        str = "geo_hausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_naive_hausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_indexed_hausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_fast_hausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_symmetric_hausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof SymmetricHausdorffMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_centroid_indexed_hausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_scan_indexed_hausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /////////////////////////////////////////////////
        str = "geo_max(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_mean(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_min(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_avg(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_frechet(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_link(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_sum_of_min(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_surjection(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "geo_fairsurjection(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ///////////////////////////////
        str = "tmp_successor(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof SuccessorMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_predecessor(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof PredecessorMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_concurrent(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof ConcurrentMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_after(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof AfterMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_before(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof BeforeMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_meets(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof MeetsMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_is_met_by(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof IsMetByMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_finishes(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof FinishesMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "tmp_is_finished_by(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof IsFinishedByMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_starts(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof StartsMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_is_started_by(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof IsStartedByMapper);
            assertTrue(mapper.getRuntimeApproximation(source.size(), target.size(), 0.6, Language.EN) != 0);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "tmp_during(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof DuringMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "tmp_during_reverse(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof DuringReverseMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "tmp_overlaps(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OverlapsMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_is_overlapped_by(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof IsOverlappedByMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "tmp_equals(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof EqualsMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //////////////////////////////////////////////
        str = "Top_Contains(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof ContainsMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "TOP_CROSSES(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof CrossesMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "top_intersects(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof IntersectsMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "top_overlaps(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof org.aksw.limes.core.measures.mapper.topology.OverlapsMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "top_equals(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof org.aksw.limes.core.measures.mapper.topology.EqualsMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "TOP_DISJOINT(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof DisjointMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "TOP_COVERS(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof CoversMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "TOP_WITHIN(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof WithinMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "TOP_COVERED_BY(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof CoveredbyMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "TOP_TOUCHES(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof TouchesMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
