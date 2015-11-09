package org.aksw.limes.core.execution.engine;


import org.aksw.limes.core.execution.engine.DefaultExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.planning.plan.ExecutionPlan;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.execution.planning.planner.HeliosPlanner;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.Mapping;



public class DefaultExecutionEngineTest {

    /**
     * Implements the test
     *
     */
    public static void test() {
	// data
	Cache source = new MemoryCache();
	source.addTriple("S1", "surname", "sandra");
	source.addTriple("S1", "name", "bullock");
	source.addTriple("S2", "surname", "lukas");
	source.addTriple("S2", "name", "duke");
	source.addTriple("S1", "age", "31");
	source.addTriple("S1", "age", "31");

	Cache target = new MemoryCache();
	target.addTriple("T1", "surname", "sandy");
	target.addTriple("T1", "name", "bullock");
	target.addTriple("T1", "alter", "31");

	target.addTriple("T2", "surname", "lukas");
	target.addTriple("T2", "name", "dorkas,love");
	target.addTriple("T2", "name", "12");

	// instructions
	Instruction i1 = new Instruction(Command.RUN, "levenshtein(x.surname, y.surname)", "0.5", -1, -1, 0);
	Instruction i2 = new Instruction(Command.RUN, "levenshtein(x.name, y.name)", "0.5", -1, -1, 1);
	Instruction i3 = new Instruction(Command.UNION, "", "0.5", 0, 1, 2);

	// execution plan
	ExecutionPlan plan = new ExecutionPlan();
	plan.addInstruction(i1);
	plan.addInstruction(i2);
	plan.addInstruction(i3);

	// engine
	DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target, "?x", "?y");
	// test run method
	System.out.println(source);
	System.out.println(target);
	Mapping m1 = ee.executeRun(i1);
	System.out.println(m1);
	Mapping m2 = ee.executeRun(i2);
	System.out.println(m2);
	Mapping m3 = ee.executeUnion(i3, m1, m2);
	System.out.println(m3);

	System.out.println(ee.execute(plan));

    }

    public static void testNestedPlanExecution() {
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
    }

    public static void main(String args[]) {
	// test();
	// System.out.println("_______________________");
	testNestedPlanExecution();
    }
}
