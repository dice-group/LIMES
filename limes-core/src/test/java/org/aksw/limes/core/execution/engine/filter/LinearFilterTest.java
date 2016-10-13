package org.aksw.limes.core.execution.engine.filter;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LinearFilterTest {
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

        target.addTriple("T1", "surname", "pp");
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
    public void simpleFilter() {
        System.out.println("simpleFilter");
        SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");

        Plan plan = new Plan();
        plan.setInstructionList(new ArrayList<Instruction>());
        Instruction run1 = new Instruction(Command.RUN, "cosine(x.name, y.name)", "0.3", -1, -1, 0);
        plan.addInstruction(run1);
        AMapping m1 = ee.executeInstructions(plan);
        System.out.println("Size before: " + m1.getNumberofMappings());
        System.out.println(m1);

        LinearFilter f = new LinearFilter();
        AMapping m0 = f.filter(m1, 0.0);
        System.out.println("0 threshold: " + m0.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() == m0.getNumberofMappings());

        // restrict output
        AMapping m2 = f.filter(m1, 0.8);
        System.out.println("Higher threshold: " + m2.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() > m2.getNumberofMappings());

        // relax output
        AMapping m3 = f.filter(m1, 0.2);
        System.out.println("Lower threshold: " + m3.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() == m3.getNumberofMappings());

        // relax output
        AMapping m4 = f.filter(m1, -1.0);
        System.out.println("Negative threshold: " + m4.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() == m4.getNumberofMappings());
        assertTrue(m1 == m4);

        System.out.println("------------------------");

    }


    @Test
    public void complexFilterWithAtomicCondition1() {
        System.out.println("complexFilterWithAtomicCondition1");
        SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");

        Plan plan = new Plan();
        plan.setInstructionList(new ArrayList<Instruction>());
        Instruction run1 = new Instruction(Command.RUN, "cosine(x.name, y.name)", "0.3", -1, -1, 0);
        plan.addInstruction(run1);
        AMapping m1 = ee.executeInstructions(plan);
        System.out.println("Size before: " + m1.getNumberofMappings());
        System.out.println(m1);

        LinearFilter f = new LinearFilter();
        

        AMapping m2 = f.filter(m1, "overlap(x.name, y.name)", 0.8, source, target, "?x", "?y");
        System.out.println("Higher threshold: " + m2.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() > m2.getNumberofMappings());

        // relax output
        AMapping m3 = f.filter(m1, "overlap(x.name, y.name)", 0.2, source, target, "?x", "?y");
        System.out.println("Lower threshold: " + m3.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() == m3.getNumberofMappings());


        System.out.println("------------------------");

    }

    @Test
    public void complexFilterWithComplexcCondition1() {
        System.out.println("complexFilterWithComplexcCondition1");
        SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");

        Plan plan = new Plan();
        plan.setInstructionList(new ArrayList<Instruction>());
        Instruction run1 = new Instruction(Command.RUN, "cosine(x.name, y.name)", "0.3", -1, -1, 0);
        plan.addInstruction(run1);
        AMapping m1 = ee.executeInstructions(plan);
        System.out.println("Size before: " + m1.getNumberofMappings());
        System.out.println(m1);

        LinearFilter f = new LinearFilter();

        AMapping m01 = f.filter(m1, "OR(overlap(x.name, y.name)|0.5,qgrams(x.name, y.name)|0.8)", 0.0, source, target,
                "?x", "?y");
        System.out.println("threshold == 0: " + m01.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() == m01.getNumberofMappings());


        AMapping m2 = f.filter(m1, "OR(overlap(x.name, y.name)|0.8,qgrams(x.name, y.name)|0.9)", 0.95, source, target,
                "?x", "?y");
        System.out.println("Higher threshold: " + m2.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() > m2.getNumberofMappings());

        // relax filter
        AMapping m3 = f.filter(m1, "OR(overlap(x.name, y.name)|0.1, qgrams(x.name, y.name)|0.1)", 0.2, source, target,
                "?x", "?y");
        System.out.println("Lower threshold: " + m3.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() == m3.getNumberofMappings());

        System.out.println("------------------------");

    }

    @Test
    public void complexFilterWithAtomicCondition2() {
        System.out.println("complexFilterWithAtomicCondition2");
        SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");

        Plan plan = new Plan();
        plan.setInstructionList(new ArrayList<Instruction>());
        Instruction run1 = new Instruction(Command.RUN, "qgrams(x.name, y.name)", "0.3", -1, -1, 0);
        plan.addInstruction(run1);
        AMapping m1 = ee.executeInstructions(plan);
        System.out.println("Size before: " + m1.getNumberofMappings());
        System.out.println(m1);

        LinearFilter f = new LinearFilter();


        AMapping m02 = f.filter(m1, "overlap(x.name, y.name)", 0.3, 0.0, source, target, "?x", "?y");
        System.out.println("0 mainthreshold only: " + m02.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() != m02.getNumberofMappings());


        AMapping m2 = f.filter(m1, "overlap(x.name, y.name)", 0.8, 0.95, source, target, "?x", "?y");
        System.out.println("Higher threshold: " + m2.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() > m2.getNumberofMappings());

        // relax output
        AMapping m3 = f.filter(m1, "overlap(x.name, y.name)", 0.2, 0.4, source, target, "?x", "?y");
        System.out.println("Lower threshold: " + m3.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() != m3.getNumberofMappings());


        System.out.println("------------------------");

    }

    @Test
    public void complexFilterWithComplexcCondition2() {
        System.out.println("complexFilterWithComplexcCondition2");
        SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");

        Plan plan = new Plan();
        plan.setInstructionList(new ArrayList<Instruction>());
        Instruction run1 = new Instruction(Command.RUN, "qgrams(x.name, y.name)", "0.3", -1, -1, 0);
        plan.addInstruction(run1);
        AMapping m1 = ee.executeInstructions(plan);
        System.out.println("Size before: " + m1.getNumberofMappings());
        System.out.println(m1);

        //all thresholds of the condition are 0, all thresholds of filters are 0
        LinearFilter f = new LinearFilter();
       

        AMapping m02 = f.filter(m1, "MINUS(overlap(x.name, y.name)|0.7,qgrams(x.name, y.name)|0.1)", 0.0, 0.0, source,
                target, "?x", "?y");
        System.out.println("threshold and mainThreshold == 0: " + m02.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() == m02.getNumberofMappings());

        AMapping m03 = f.filter(m1, "MINUS(overlap(x.name, y.name)|0.7,qgrams(x.name, y.name)|0.1)", 0.9, 0.0, source,
                target, "?x", "?y");
        System.out.println("only mainThreshold == 0: " + m03.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() != m03.getNumberofMappings());


        AMapping m04 = f.filter(m1, "MINUS(overlap(x.name, y.name)|0.7,qgrams(x.name, y.name)|0.1)", 0.0, 0.9, source,
                target, "?x", "?y");
        System.out.println("onlythreshold == 0: " + m04.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() != m04.getNumberofMappings());

        AMapping m2 = f.filter(m1, "AND(overlap(x.name, y.name)|0.8,qgrams(x.name, y.name)|0.9)", 0.8, 0.95, source,
                target, "?x", "?y");
        System.out.println("Higher threshold: " + m2.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() > m2.getNumberofMappings());

        AMapping m21 = f.filter(m1, "AND(overlap(x.name, y.name)|0.8,qgrams(x.name, y.name)|0.9)", 0.2, 0.1, source,
                target, "?x", "?y");
        System.out.println("Higher threshold2: " + m21.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() > m21.getNumberofMappings());

        // relax output
        AMapping m3 = f.filter(m1, "AND(overlap(x.name, y.name)|0.1,qgrams(x.name, y.name)|0.1)", 0.2, 0.4, source,
                target, "?x", "?y");
        System.out.println("Lower threshold: " + m3.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() != m3.getNumberofMappings());

        
        AMapping m4 = f.filter(m1, "MINUS(AND(levenshtein(x.name, y.name)|0.1,jaro(x.name, y.name)|0.1)|0.0, OR(qgrams(x.name, y.name)|0.1,overlap(x.name, y.name)|0.1)|0.5)", 0.8, 0.4, source,
                target, "?x", "?y");
        System.out.println("Final touch " + m4.getNumberofMappings());
        System.out.println(m4);

        assertTrue(m1.getNumberofMappings() != m4.getNumberofMappings());
        System.out.println("------------------------");

    }

    @Test
    public void complexReverseFilterWithAtomicCondition() {
        System.out.println("complexReverseFilterWithAtomicCondition");
        SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");

        Plan plan = new Plan();
        plan.setInstructionList(new ArrayList<Instruction>());
        Instruction run1 = new Instruction(Command.RUN, "qgrams(x.name, y.name)", "0.3", -1, -1, 0);
        plan.addInstruction(run1);
        AMapping m1 = ee.executeInstructions(plan);
        System.out.println("Size before: " + m1.getNumberofMappings());
        System.out.println(m1);
        System.out.println("\n");


        LinearFilter f = new LinearFilter();
       
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        AMapping m02 = f.reversefilter(m1, "overlap(x.name, y.name)", 0.7, 0.0, source, target, "?x", "?y");
        System.out.println("non 0 threshold: " + m02.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() != m02.getNumberofMappings());
        System.out.println("\n");

        ////////////////////////////////////////////////////////////////////////////////////////////////////
        AMapping m2 = f.reversefilter(m1, "overlap(x.name, y.name)", 0.4, 0.6, source, target, "?x", "?y");
        System.out.println("Higher threshold: " + m2.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() > m2.getNumberofMappings());
        System.out.println("\n");

        // relax output
        AMapping m3 = f.reversefilter(m1, "overlap(x.name, y.name)", 0.2, 0.4, source, target, "?x", "?y");
        System.out.println("Lower threshold: " + m3.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() != m3.getNumberofMappings());
        System.out.println("\n");

        AMapping m4 = f.reversefilter(m1, "OR(AND(levenshtein(x.name, y.name)|0.1,jaro(x.name, y.name)|0.1)|0.0, OR(qgrams(x.name, y.name)|0.1,overlap(x.name, y.name)|0.1)|0.1)", 0.9, 0.4, source,
                target, "?x", "?y");
        System.out.println("Final touch " + m4.getNumberofMappings());
        System.out.println(m4);
        System.out.println("------------------------");

    }

    @Test
    public void filterWithCoEfficient() {
        System.out.println("filterWithCoEfficient");
        SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");

        Plan plan = new Plan();
        plan.setInstructionList(new ArrayList<Instruction>());
        Instruction run1 = new Instruction(Command.RUN, "levenshtein(x.name, y.name)", "0.13", -1, -1, 0);
        plan.addInstruction(run1);
        AMapping m1 = ee.executeInstructions(plan);
        System.out.println("Size before: " + m1.getNumberofMappings());
        System.out.println(m1);

        Plan plan2 = new Plan();
        plan2.setInstructionList(new ArrayList<Instruction>());
        Instruction run2 = new Instruction(Command.RUN, "qgrams(x.name, y.name)", "0.3", -1, -1, 0);
        plan2.addInstruction(run2);
        AMapping m2 = ee.executeInstructions(plan2);
        System.out.println("Size before: " + m2.getNumberofMappings());
        System.out.println(m2);

        LinearFilter f = new LinearFilter();
        AMapping m0 = f.filter(m1, m2, 0.8, 0.45, 0.2, "add");
        System.out.println("add: " + m0.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() >= m0.getNumberofMappings());
        assertTrue(m2.getNumberofMappings() >= m0.getNumberofMappings());

        AMapping m01 = f.filter(m1, m2, 0.8, 0.45, 0.2, "minus");
        System.out.println("add: " + m01.getNumberofMappings());
        assertTrue(m1.getNumberofMappings() >= m01.getNumberofMappings());
        assertTrue(m2.getNumberofMappings() >= m01.getNumberofMappings());

        System.out.println("------------------------");

    }
}
