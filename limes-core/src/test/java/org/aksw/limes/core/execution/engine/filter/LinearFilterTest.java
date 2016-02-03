package org.aksw.limes.core.execution.engine.filter;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LinearFilterTest {
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
    public void simpleFilter() {
	System.out.println("simpleFilter");
	SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");

	Plan plan = new Plan();
	plan.setInstructionList(new ArrayList<Instruction>());
	Instruction run1 = new Instruction(Command.RUN, "cosine(x.name, y.name)", "0.3", -1, -1, 0);
	plan.addInstruction(run1);
	Mapping m1 = ee.execute(plan);
	System.out.println("Size before: " + m1.getNumberofMappings());
	System.out.println(m1);

	// restrict output
	LinearFilter f = new LinearFilter();
	Mapping m0 = f.filter(m1, 0.0);
	System.out.println("0 threshold: " + m0.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() == m0.getNumberofMappings());
	assertTrue(m1 == m0);
	
	Mapping m2 = f.filter(m1, 0.8);
	System.out.println("Higher threshold: " + m2.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() > m2.getNumberofMappings());

	// relax output
	Mapping m3 = f.filter(m1, 0.2);
	System.out.println("Lower threshold: " + m3.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() == m3.getNumberofMappings());

	System.out.println("------------------------");

    }
    @Test
    public void complexFilterWithNullCondition() {
	System.out.println("complexFilterWithNullCondition");
	SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");

	Plan plan = new Plan();
	plan.setInstructionList(new ArrayList<Instruction>());
	Instruction run1 = new Instruction(Command.RUN, "cosine(x.name, y.name)", "0.3", -1, -1, 0);
	plan.addInstruction(run1);
	Mapping m1 = ee.execute(plan);
	System.out.println("Size before: " + m1.getNumberofMappings());
	System.out.println(m1);

	
	LinearFilter f = new LinearFilter();

	Mapping m4 = f.filter(m1, null, 0.2, source, target, "?x", "?y");
	System.out.println("Null condition: " + m4.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() == m4.getNumberofMappings());
	
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
	Mapping m1 = ee.execute(plan);
	System.out.println("Size before: " + m1.getNumberofMappings());
	System.out.println(m1);

	
	LinearFilter f = new LinearFilter();
	Mapping m0 = f.filter(m1, "overlap(x.name, y.name)", 0.0, source, target, "?x", "?y");
	System.out.println("0 threshold: " + m0.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() == m0.getNumberofMappings());
	assertTrue(m1 == m0);
	
	Mapping m2 = f.filter(m1, "overlap(x.name, y.name)", 0.8, source, target, "?x", "?y");
	System.out.println("Higher threshold: " + m2.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() > m2.getNumberofMappings());

	// relax output
	Mapping m3 = f.filter(m1, "overlap(x.name, y.name)", 0.2, source, target, "?x", "?y");
	System.out.println("Lower threshold: " + m3.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() == m3.getNumberofMappings());

	Mapping m4 = f.filter(m1, null, 0.2, source, target, "?x", "?y");
	System.out.println("Null condition: " + m4.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() == m4.getNumberofMappings());
	
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
	Mapping m1 = ee.execute(plan);
	System.out.println("Size before: " + m1.getNumberofMappings());
	System.out.println(m1);
	
	LinearFilter f = new LinearFilter();
	Mapping m0 = f.filter(m1, "OR(overlap(x.name, y.name)|0.0,qgrams(x.name, y.name)|0.0)", 0.0, source, target, "?x", "?y");
	System.out.println("0 threshold: " + m0.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() == m0.getNumberofMappings());
	assertTrue(m1 == m0);
	
	Mapping m2 = f.filter(m1, "OR(overlap(x.name, y.name)|0.8,qgrams(x.name, y.name)|0.9)", 0.95, source, target, "?x", "?y");
	System.out.println("Higher threshold: " + m2.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() > m2.getNumberofMappings());
	
	Mapping m21 = f.filter(m1, "OR(overlap(x.name, y.name)|0.8,qgrams(x.name, y.name)|0.9)", 0.2, source, target, "?x", "?y");
	System.out.println("Higher threshold2: " + m21.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() == m21.getNumberofMappings());

	// relax output
	Mapping m3 = f.filter(m1, "OR(overlap(x.name, y.name)|0.1,qgrams(x.name, y.name)|0.1)", 0.2, source, target, "?x", "?y");
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
	Instruction run1 = new Instruction(Command.RUN, "cosine(x.name, y.name)", "0.3", -1, -1, 0);
	plan.addInstruction(run1);
	Mapping m1 = ee.execute(plan);
	System.out.println("Size before: " + m1.getNumberofMappings());
	System.out.println(m1);

	
	LinearFilter f = new LinearFilter();
	Mapping m0 = f.filter(m1, "overlap(x.name, y.name)", 0.0, 0.0, source, target, "?x", "?y");
	System.out.println("0 threshold: " + m0.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() == m0.getNumberofMappings());
	assertTrue(m1 != m0);
	
	Mapping m2 = f.filter(m1, "overlap(x.name, y.name)", 0.8, 0.95, source, target, "?x", "?y");
	System.out.println("Higher threshold: " + m2.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() > m2.getNumberofMappings());

	// relax output
	Mapping m3 = f.filter(m1, "overlap(x.name, y.name)", 0.2, 0.4, source, target, "?x", "?y");
	System.out.println("Lower threshold: " + m3.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() == m3.getNumberofMappings());

	Mapping m4 = f.filter(m1, null, 0.2, source, target, "?x", "?y");
	System.out.println("Null condition: " + m4.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() == m4.getNumberofMappings());
	
	System.out.println("------------------------");

    }
    
    @Test
    public void complexFilterWithComplexcCondition2() {
	System.out.println("complexFilterWithComplexcCondition2");
	SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");

	Plan plan = new Plan();
	plan.setInstructionList(new ArrayList<Instruction>());
	Instruction run1 = new Instruction(Command.RUN, "cosine(x.name, y.name)", "0.3", -1, -1, 0);
	plan.addInstruction(run1);
	Mapping m1 = ee.execute(plan);
	System.out.println("Size before: " + m1.getNumberofMappings());
	System.out.println(m1);
	
	LinearFilter f = new LinearFilter();
	Mapping m0 = f.filter(m1, "AND(overlap(x.name, y.name)|0.0,qgrams(x.name, y.name)|0.0)", 0.0, 0.0, source, target, "?x", "?y");
	System.out.println("0 threshold: " + m0.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() == m0.getNumberofMappings());
	assertTrue(m1 != m0);
	
	Mapping m2 = f.filter(m1, "AND(overlap(x.name, y.name)|0.8,qgrams(x.name, y.name)|0.9)", 0.8, 0.95, source, target, "?x", "?y");
	System.out.println("Higher threshold: " + m2.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() > m2.getNumberofMappings());
	
	Mapping m21 = f.filter(m1, "AND(overlap(x.name, y.name)|0.8,qgrams(x.name, y.name)|0.9)", 0.2, 0.1, source, target, "?x", "?y");
	System.out.println("Higher threshold2: " + m21.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() > m21.getNumberofMappings());

	// relax output
	Mapping m3 = f.filter(m1, "AND(overlap(x.name, y.name)|0.1,qgrams(x.name, y.name)|0.1)", 0.2, 0.4, source, target, "?x", "?y");
	System.out.println("Lower threshold: " + m3.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() == m3.getNumberofMappings());

	
	System.out.println("------------------------");

    }
    
    @Test
    public void filterWithCoEfficient() {
	System.out.println("filterWithCoEfficient");
	SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");

	Plan plan = new Plan();
	plan.setInstructionList(new ArrayList<Instruction>());
	Instruction run1 = new Instruction(Command.RUN, "leven(x.name, y.name)", "0.13", -1, -1, 0);
	plan.addInstruction(run1);
	Mapping m1 = ee.execute(plan);
	System.out.println("Size before: " + m1.getNumberofMappings());
	System.out.println(m1);
	
	Plan plan2 = new Plan();
	plan2.setInstructionList(new ArrayList<Instruction>());
	Instruction run2 = new Instruction(Command.RUN, "qgrams(x.name, y.name)", "0.3", -1, -1, 0);
	plan2.addInstruction(run2);
	Mapping m2 = ee.execute(plan2);
	System.out.println("Size before: " + m2.getNumberofMappings());
	System.out.println(m2);
	
	
	LinearFilter f = new LinearFilter();
	Mapping m0 = f.filter(m1, m2, 0.8, 0.45, 0.2, "add");
	System.out.println("add: " + m0.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() >= m0.getNumberofMappings());
	assertTrue(m2.getNumberofMappings() >= m0.getNumberofMappings());

	Mapping m01 = f.filter(m1, m2, 0.8, 0.45, 0.2, "minus");
	System.out.println("add: " + m01.getNumberofMappings());
	assertTrue(m1.getNumberofMappings() >= m01.getNumberofMappings());
	assertTrue(m2.getNumberofMappings() >= m01.getNumberofMappings());
	
	System.out.println("------------------------");

    }
}

