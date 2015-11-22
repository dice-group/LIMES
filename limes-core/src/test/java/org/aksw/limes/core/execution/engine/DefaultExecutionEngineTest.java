package org.aksw.limes.core.execution.engine;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.aksw.limes.core.execution.engine.DefaultExecutionEngine;
import org.aksw.limes.core.execution.planning.plan.ExecutionPlan;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.MemoryCache;
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
	source.addTriple("S1", "age", "31");

	// create target cache
	target.addTriple("T1", "surname", "sandy");
	target.addTriple("T1", "name", "bullock");
	target.addTriple("T1", "alter", "31");
	target.addTriple("T2", "surname", "lukas");
	target.addTriple("T2", "name", "dorkas,love");
	target.addTriple("T2", "name", "12");
	
	System.out.println("Size of source: "+source.size());
	System.out.println("Size of target: "+target.size());

    }

    @After
    public void tearDown() {
	source = null;
	target = null;
    }

    @Test
    public void testSimpleUnion() {

	// instructions for RUN command
	Instruction run1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.5");
	Instruction run2 = new Instruction(Command.RUN, "qgrams(x.name, y.name)", "0.5");
	// instructions for UNION command
	Instruction union1 = new Instruction(Command.UNION, "", "0.5");
	// instruction for FILTER command 
	//Instruction filter1 = new Instruction(Command.FILTER, "", "0.6", -1, -1, 0);
	
	// engine
	DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target, "?x", "?y");

	
	// execution plan
	ExecutionPlan plan = new ExecutionPlan();
	ExecutionPlan leftChild = new ExecutionPlan();
	leftChild.instructionList = new ArrayList<Instruction>();
	leftChild.addInstruction(run1);
	ExecutionPlan rightChild = new ExecutionPlan();
	rightChild.instructionList = new ArrayList<Instruction>();
	rightChild.addInstruction(run2);
	plan.subPlans = new ArrayList<ExecutionPlan>();
	plan.subPlans.add(leftChild);
	plan.subPlans.add(rightChild);
	plan.operator = Command.UNION;
	plan.filteringInstruction = null;

	// execute instructions independently
	Mapping mSource = ee.executeRun(leftChild);
	Mapping mTarget = ee.executeRun(rightChild);
	Mapping m = ee.executeUnion(mSource, mTarget);
	//mUnion1 = ee.executeFilter(filter1, mUnion1);
	
	// run whole plan
	Mapping m2 = ee.execute(plan);

	assertTrue(mSource.size <= m.size);
	assertTrue(mTarget.size <= m.size);

	assertTrue(mSource.size <= m2.size);
	assertTrue(mTarget.size <= m2.size);

	assertTrue(m.size == m2.size);

    }
    @Test
    public void testSimpleIntersection() {

	// instructions for RUN command
	Instruction run1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.5");
	Instruction run2 = new Instruction(Command.RUN, "qgrams(x.name, y.name)", "0.5");
	// instructions for UNION command
	Instruction union1 = new Instruction(Command.INTERSECTION, "", "0.5");
	// instruction for FILTER command 
	//Instruction filter1 = new Instruction(Command.FILTER, "", "0.6", -1, -1, 0);
	// engine
	DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target, "?x", "?y");
	// execution plan
	ExecutionPlan plan = new ExecutionPlan();
	ExecutionPlan leftChild = new ExecutionPlan();
	leftChild.instructionList = new ArrayList<Instruction>();
	leftChild.addInstruction(run1);
	ExecutionPlan rightChild = new ExecutionPlan();
	rightChild.instructionList = new ArrayList<Instruction>();
	rightChild.addInstruction(run2);
	plan.subPlans = new ArrayList<ExecutionPlan>();
	plan.subPlans.add(leftChild);
	plan.subPlans.add(rightChild);
	plan.operator = Command.UNION;
	plan.filteringInstruction = null;

	// execute instructions independently
	Mapping mSource = ee.executeRun(leftChild);
	Mapping mTarget = ee.executeRun(rightChild);
	Mapping m = ee.executeUnion(mSource, mTarget);
	
	// run whole plan
	Mapping m2 = ee.execute(plan);

	assertTrue(mSource.size >= m.size);
	assertTrue(mTarget.size >= m.size);

	assertTrue(mSource.size >= m2.size);
	assertTrue(mTarget.size >= m2.size);

	assertTrue(m.size == m2.size);

    }
    
    @Test
    public void testSimpleDifference() {

	// instructions for RUN command
	Instruction run1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.5");
	Instruction run2 = new Instruction(Command.RUN, "qgrams(x.name, y.name)", "0.5");
	// instructions for UNION command
	Instruction union1 = new Instruction(Command.DIFF, "", "0.5");
	// engine
	DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target, "?x", "?y");
	// execution plan
	ExecutionPlan plan = new ExecutionPlan();
	ExecutionPlan leftChild = new ExecutionPlan();
	leftChild.instructionList = new ArrayList<Instruction>();
	leftChild.addInstruction(run1);
	ExecutionPlan rightChild = new ExecutionPlan();
	rightChild.instructionList = new ArrayList<Instruction>();
	rightChild.addInstruction(run2);
	plan.subPlans = new ArrayList<ExecutionPlan>();
	plan.subPlans.add(leftChild);
	plan.subPlans.add(rightChild);
	plan.operator = Command.DIFF;
	plan.filteringInstruction = null;
	
	// execute instructions independently
	Mapping mSource = ee.executeRun(leftChild);
	Mapping mTarget = ee.executeRun(rightChild);
	Mapping m = ee.executeUnion(mSource, mTarget);
	
	// run whole plan
	Mapping m2 = ee.execute(plan);

	assertTrue(mSource.size <= m.size);

	assertTrue(mSource.size <= m2.size);

	assertTrue(m.size == m2.size);

    }
    @Test
    public void testSimpleXor() {

	// instructions for RUN command
	Instruction run1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.5");
	Instruction run2 = new Instruction(Command.RUN, "qgrams(x.name, y.name)", "0.5");
	// instructions for UNION command
	Instruction union1 = new Instruction(Command.XOR, "", "0.5");
	// engine
	DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target, "?x", "?y");
	// execution plan
	ExecutionPlan plan = new ExecutionPlan();
	ExecutionPlan leftChild = new ExecutionPlan();
	leftChild.instructionList = new ArrayList<Instruction>();
	leftChild.addInstruction(run1);
	ExecutionPlan rightChild = new ExecutionPlan();
	rightChild.instructionList = new ArrayList<Instruction>();
	rightChild.addInstruction(run2);
	plan.subPlans = new ArrayList<ExecutionPlan>();
	plan.subPlans.add(leftChild);
	plan.subPlans.add(rightChild);
	plan.operator = Command.XOR;
	plan.filteringInstruction = null;
	
	// execute instructions independently
	Mapping mSource = ee.executeRun(leftChild);
	Mapping mTarget = ee.executeRun(rightChild);
	Mapping m = ee.executeUnion( mSource, mTarget);
	
	// run whole plan
	Mapping m2 = ee.execute(plan);

	assertTrue(mSource.size <= m.size);
	assertTrue(mTarget.size <= m.size);

	assertTrue(mSource.size <= m2.size);
	assertTrue(mTarget.size <= m2.size);

	assertTrue(m.size == m2.size);

    }
    /*public static void testNestedPlanExecution() {
	// data
	Cache source = new MemoryCache();
	source.addTriple("S1", "surname", "sandra");
	source.addTriple("S1", "name", "bullock");
	source.addTriple("S2", "surname", "lukas");
	source.addTriple("S2", "name", "duke");
	source.addTriple("S1", "age", "31");
	source.addTriple("S2", "age", "31");

	Cache target = new MemoryCache();
	target.addTriple("T1", "surname", "sandy");
	target.addTriple("T1", "name", "bullock");
	target.addTriple("T1", "alter", "31");

	target.addTriple("T2", "surname", "luke");
	target.addTriple("T2", "name", "duke");
	target.addTriple("T2", "alter", "30");

	// spec
	String metric = "AND(trigrams(x.name, y.name)|0.8,OR(cosine(x.surname, y.surname)|0.5, euclidean(x.age, y.alter)|0.7)|0.4)";
	// String metric = "trigrams(x.name, y.name)";
	LinkSpecification spec = new LinkSpecification(metric, 0.3);
	HeliosPlanner cp = new HeliosPlanner(target, target);
	ExecutionPlan plan = cp.plan(spec);
	DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target, "?x", "?y");
	System.out.println("Nested Plan Result = " + ee.execute(plan));
    }*/

   
}
