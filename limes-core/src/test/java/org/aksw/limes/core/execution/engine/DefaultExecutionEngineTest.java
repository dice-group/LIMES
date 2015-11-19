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
	Instruction run1 = new Instruction(Command.RUN, "levenshtein(x.surname, y.surname)", "0.5", -1, -1, 0);
	Instruction run2 = new Instruction(Command.RUN, "qgrams(x.name, y.name)", "0.5", -1, -1, 1);
	// instructions for UNION command
	Instruction union1 = new Instruction(Command.UNION, "", "0.5", -1, -1, 0);
	// instruction for FILTER command 
	Instruction filter1 = new Instruction(Command.FILTER, "", "0.5", -1, -1, 0);
	
	// engine
	DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target, "?x", "?y");

	// Instruction intersection1 = new Instruction(Command.INTERSECTION, "",
	// "0.5", 0, 1, 2);
	// Instruction intersection2 = new Instruction(Command.INTERSECTION, "",
	// "0.0", 0, 1, 2);

	// Instruction diff1 = new Instruction(Command.DIFF, "", "0.5", 0, 1,
	// 2);

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
	plan.filteringInstruction = filter1;

	// execute instructions independently
	Mapping mSource = ee.executeRun(run1);
	Mapping mTarget = ee.executeRun(run2);
	Mapping mUnion1 = ee.executeUnion(union1, mSource, mTarget);
	mUnion1 = ee.executeFilter(filter1, mUnion1);
	
	// run whole plan
	Mapping mUnion2 = ee.execute(plan);

	assertTrue(mSource.size <= mUnion1.size);
	assertTrue(mTarget.size <= mUnion1.size);

	assertTrue(mSource.size <= mUnion2.size);
	assertTrue(mTarget.size <= mUnion2.size);

	assertTrue(mUnion1.size == mUnion2.size);

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
