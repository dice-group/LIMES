package org.aksw.limes.core.execution.engine;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.aksw.limes.core.execution.engine.DefaultExecutionEngine;
import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.execution.planning.planner.CannonicalPlanner;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.Mapping;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DefaultExecutionEngineTest {
    public Cache source = new MemoryCache();
    public Cache target = new MemoryCache();

    @Before
    public void setUp() {
	// create source cache
	source.addTriple("S1", "surname", "sandra");
	source.addTriple("S1", "name", "bullock");
	source.addTriple("S2", "surname", "lukas");
	source.addTriple("S2", "name", "duke");
	source.addTriple("S1", "age", "31");
	source.addTriple("S2", "age", "31");

	// create target cache
	target.addTriple("T1", "surname", "sandy");
	target.addTriple("T1", "name", "bullock");
	target.addTriple("T1", "alter", "31");
	target.addTriple("T2", "surname", "lukas");
	target.addTriple("T2", "name", "dorkas,love");
	target.addTriple("T2", "name", "12");

	System.out.println("Size of source: " + source.size());
	System.out.println("Size of target: " + target.size());

    }

    @After
    public void tearDown() {
	source = null;
	target = null;
    }
    @Test 
    public void testDefaultEngineWithNUllParameters(){
	LinkSpecification ls = new LinkSpecification("trigrams(x.surname, y.surname)",0.5);
	DefaultExecutionEngine ee = new DefaultExecutionEngine(null,null,null,null);
	CannonicalPlanner cp = new CannonicalPlanner();
	
	NestedPlan plan = cp.plan(ls);
	//problem at getMapping
	Mapping m = ee.execute(plan);
	
	assertTrue(m.getNumberofMappings() == 0);
	

    }
    @Test 
    public void testExecuteNUllPlan(){
	DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target, "?x", "?y");
	
	Mapping m = ee.execute(null);
	assertTrue(m.getNumberofMappings() == 0);

    }
    @Test 
    public void testRunNUllPlan(){
	DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target, "?x", "?y");
	
	Mapping m = ee.run(null);
	assertTrue(m.getNumberofMappings() == 0);

    }
    @Test 
    public void testAtomicLinkSpecificationRun(){
	LinkSpecification ls = new LinkSpecification("trigrams(x.surname, y.surname)",0.5);
	DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target, "?x", "?y");
	CannonicalPlanner cp = new CannonicalPlanner();
	
	NestedPlan plan = cp.plan(ls);
	Mapping m = ee.execute(plan);
	Mapping m2 = ee.run(plan);
	
	assertTrue(m2 == null);
	
	assertTrue(m2.getNumberofMappings() >= 0);
	
	assertTrue(m.getNumberofMappings() == m2.getNumberofMappings());

    }
    @Test 
    public void testUnionWithPlan(){
	LinkSpecification ls = new LinkSpecification("OR(trigrams(x.surname, y.surname)|0.5,qgrams(x.name, y.name)|0.5)",0.5);
	DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target, "?x", "?y");
	CannonicalPlanner cp = new CannonicalPlanner();
	
	NestedPlan plan = cp.plan(ls);
	Mapping m = ee.execute(plan);
	assertTrue(m.getNumberofMappings() >= 0);

    }
    @Test 
    public void testUnionWithInstrunctions(){
	DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target, "?x", "?y");
	NestedPlan plan = new NestedPlan();
	
	Instruction run1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.5", -1, -1, 0);
	Instruction run2 = new Instruction(Command.RUN, "qgrams(x.name, y.name)", "0.5", -1, -1, 1);
	// instructions for UNION command
	Instruction union = new Instruction(Command.UNION, "", "0.5", 0, 1, 2);
	plan.addInstruction(run1);
	plan.addInstruction(run2);
	plan.addInstruction(union);
	
	Mapping m = ee.run(plan);
	assertTrue(m.getNumberofMappings() >= 0);

    }
    @Test
    public void testSimpleUnion() {

	// instructions for RUN command
	Instruction run1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.5", -1, -1, 0);
	Instruction run2 = new Instruction(Command.RUN, "qgrams(x.name, y.name)", "0.5", -1, -1, 0);
	// instructions for UNION command
	Instruction union1 = new Instruction(Command.UNION, "", "0.5", -1, -1, 0);
	// instruction for FILTER command
	// Instruction filter1 = new Instruction(Command.FILTER, "", "0.6", -1,
	// -1, 0);

	// engine
	DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target, "?x", "?y");

	// execution plan: run steps independently
	Mapping mSource = ee.executeRun(run1);
	Mapping mTarget = ee.executeRun(run2);
	Mapping m = ee.executeUnion(mSource, mTarget);

	// execution plan: do it like a planner
	NestedPlan plan = new NestedPlan();
	NestedPlan leftChild = new NestedPlan();
	leftChild.instructionList = new ArrayList<Instruction>();
	leftChild.addInstruction(run1);
	NestedPlan rightChild = new NestedPlan();
	rightChild.instructionList = new ArrayList<Instruction>();
	rightChild.addInstruction(run2);
	plan.subPlans = new ArrayList<NestedPlan>();
	plan.subPlans.add(leftChild);
	plan.subPlans.add(rightChild);
	plan.operator = Command.UNION;
	plan.filteringInstruction = null;
	Mapping m2 = ee.execute(plan);

	assertTrue(mSource.getNumberofMappings() <= m.getNumberofMappings());
	assertTrue(mTarget.getNumberofMappings() <= m.getNumberofMappings());

	assertTrue(mSource.getNumberofMappings() <= m2.getNumberofMappings());
	assertTrue(mTarget.getNumberofMappings() <= m2.getNumberofMappings());

	assertTrue(m.getNumberofMappings() == m2.getNumberofMappings());

    }

    @Test
    public void testSimpleIntersection() {

	// instructions for RUN command
	Instruction run1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.5", -1, -1, 0);
	Instruction run2 = new Instruction(Command.RUN, "qgrams(x.name, y.name)", "0.5", -1, -1, 0);
	// instructions for UNION command
	Instruction union1 = new Instruction(Command.INTERSECTION, "", "0.5", -1, -1, 0);
	// instruction for FILTER command
	// Instruction filter1 = new Instruction(Command.FILTER, "", "0.6", -1,
	// -1, 0);
	// engine
	DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target, "?x", "?y");

	// execute instructions independently
	Mapping mSource = ee.executeRun(run1);
	Mapping mTarget = ee.executeRun(run2);
	Mapping m = ee.executeUnion(mSource, mTarget);

	// execution plan: do it like a planner
	NestedPlan plan = new NestedPlan();
	NestedPlan leftChild = new NestedPlan();
	leftChild.instructionList = new ArrayList<Instruction>();
	leftChild.addInstruction(run1);
	NestedPlan rightChild = new NestedPlan();
	rightChild.instructionList = new ArrayList<Instruction>();
	rightChild.addInstruction(run2);
	plan.subPlans = new ArrayList<NestedPlan>();
	plan.subPlans.add(leftChild);
	plan.subPlans.add(rightChild);
	plan.operator = Command.UNION;
	plan.filteringInstruction = null;
	// run whole plan
	Mapping m2 = ee.execute(plan);

	assertTrue(mSource.getNumberofMappings() >= m.getNumberofMappings());
	assertTrue(mTarget.getNumberofMappings() >= m.getNumberofMappings());

	assertTrue(mSource.getNumberofMappings() >= m2.getNumberofMappings());
	assertTrue(mTarget.getNumberofMappings() >= m2.getNumberofMappings());

	assertTrue(m.getNumberofMappings() == m2.getNumberofMappings());

    }

    @Test
    public void testSimpleDifference() {

	// instructions for RUN command
	Instruction run1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.5", -1, -1, 0);
	Instruction run2 = new Instruction(Command.RUN, "qgrams(x.name, y.name)", "0.5", -1, -1, 0);
	// instructions for UNION command
	Instruction union1 = new Instruction(Command.DIFF, "", "0.5", -1, -1, 0);
	// engine
	DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target, "?x", "?y");

	// execute instructions independently
	Mapping mSource = ee.executeRun(run1);
	Mapping mTarget = ee.executeRun(run2);
	Mapping m = ee.executeUnion(mSource, mTarget);

	// execution plan: do it like a planner
	NestedPlan plan = new NestedPlan();
	NestedPlan leftChild = new NestedPlan();
	leftChild.instructionList = new ArrayList<Instruction>();
	leftChild.addInstruction(run1);
	NestedPlan rightChild = new NestedPlan();
	rightChild.instructionList = new ArrayList<Instruction>();
	rightChild.addInstruction(run2);
	plan.subPlans = new ArrayList<NestedPlan>();
	plan.subPlans.add(leftChild);
	plan.subPlans.add(rightChild);
	plan.operator = Command.DIFF;
	plan.filteringInstruction = null;
	// run whole plan
	Mapping m2 = ee.execute(plan);

	assertTrue(mSource.getNumberofMappings() <= m.getNumberofMappings());

	assertTrue(mSource.getNumberofMappings() <= m2.getNumberofMappings());

	assertTrue(m.getNumberofMappings() == m2.getNumberofMappings());

    }

    @Test
    public void testSimpleXor() {

	// instructions for RUN command
	Instruction run1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.5", -1, -1, 0);
	Instruction run2 = new Instruction(Command.RUN, "qgrams(x.name, y.name)", "0.5", -1, -1, 0);
	// instructions for UNION command
	Instruction union1 = new Instruction(Command.XOR, "", "0.5", -1, -1, 0);
	// engine
	DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target, "?x", "?y");

	// execute instructions independently
	Mapping mSource = ee.executeRun(run1);
	Mapping mTarget = ee.executeRun(run2);
	Mapping m = ee.executeUnion(mSource, mTarget);

	// execution plan: do it like a planner
	NestedPlan plan = new NestedPlan();
	NestedPlan leftChild = new NestedPlan();
	leftChild.instructionList = new ArrayList<Instruction>();
	leftChild.addInstruction(run1);
	NestedPlan rightChild = new NestedPlan();
	rightChild.instructionList = new ArrayList<Instruction>();
	rightChild.addInstruction(run2);
	plan.subPlans = new ArrayList<NestedPlan>();
	plan.subPlans.add(leftChild);
	plan.subPlans.add(rightChild);
	plan.operator = Command.XOR;
	plan.filteringInstruction = null;
	Mapping m2 = ee.execute(plan);

	assertTrue(mSource.getNumberofMappings() <= m.getNumberofMappings());
	assertTrue(mTarget.getNumberofMappings() <= m.getNumberofMappings());

	assertTrue(mSource.getNumberofMappings() <= m2.getNumberofMappings());
	assertTrue(mTarget.getNumberofMappings() <= m2.getNumberofMappings());

	assertTrue(m.getNumberofMappings() == m2.getNumberofMappings());

    }
    /*
     * public static void testNestedPlanExecution() { // data Cache source = new
     * MemoryCache(); source.addTriple("S1", "surname", "sandra");
     * source.addTriple("S1", "name", "bullock"); source.addTriple("S2",
     * "surname", "lukas"); source.addTriple("S2", "name", "duke");
     * source.addTriple("S1", "age", "31"); source.addTriple("S2", "age", "31");
     * 
     * Cache target = new MemoryCache(); target.addTriple("T1", "surname",
     * "sandy"); target.addTriple("T1", "name", "bullock");
     * target.addTriple("T1", "alter", "31");
     * 
     * target.addTriple("T2", "surname", "luke"); target.addTriple("T2", "name",
     * "duke"); target.addTriple("T2", "alter", "30");
     * 
     * // spec String metric =
     * "AND(trigrams(x.name, y.name)|0.8,OR(cosine(x.surname, y.surname)|0.5, euclidean(x.age, y.alter)|0.7)|0.4)"
     * ; // String metric = "trigrams(x.name, y.name)"; LinkSpecification spec =
     * new LinkSpecification(metric, 0.3); HeliosPlanner cp = new
     * HeliosPlanner(target, target); ExecutionPlan plan = cp.plan(spec);
     * DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target,
     * "?x", "?y"); System.out.println("Nested Plan Result = " +
     * ee.execute(plan)); }
     */

}
