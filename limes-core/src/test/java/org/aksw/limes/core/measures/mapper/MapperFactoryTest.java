package org.aksw.limes.core.measures.mapper;

import static org.junit.Assert.*;

import org.aksw.limes.core.exceptions.InvalidMeasureException;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
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
import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.junit.Test;

public class MapperFactoryTest {

    @Test
    public void test() {
        MeasureType type = null;
        Mapper mapper = null;
        /////////////////////////////////////////////////////////////////////////
        String str = "jaro(x.name,y.name)";
        Instruction inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof JaroMapper);
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
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "levenshtein(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof EDJoin);
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
            assertTrue(mapper instanceof HR3);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /////////////////////////////////////////////////
        str = "geo_orthodromic(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        str = "geo_hausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        str = "geo_naivehausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        str = "geo_indexedhausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        str = "geo_fasthausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        str = "geo_symmetrichausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof SymmetricHausdorffMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        str = "geo_centroidindexedhausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        str = "geo_scanindexedhausdorff(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof OrchidMapper);
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
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_ismetby(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof IsMetByMapper);
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
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        str = "tmp_isfinishedby(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof IsFinishedByMapper);
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
        } catch (InvalidMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        str = "tmp_isstartedby(x.name,y.name)";
        inst = new Instruction(Command.RUN, str, "0.6", -1, -1, 0);
        try {
            type = MeasureFactory.getMeasureType(inst.getMeasureExpression());
            mapper = MapperFactory.createMapper(type);
            assertTrue(mapper instanceof IsStartedByMapper);
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
        
        str = "tmp_duringreverse(x.name,y.name)";
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
        str = "tmp_isoverlappedby(x.name,y.name)";
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
    
    }

}
