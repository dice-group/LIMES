package org.aksw.limes.core.execution.engine;

import static org.junit.Assert.*;

import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.execution.planning.planner.CanonicalPlanner;
import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SimpleExecutionEngineTest {
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
	source = null;
	target = null;
    }
    @Test
    public void EmptyLink(){
	System.out.println("EmptyLink");
	SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
	Instruction run1 = new Instruction(Command.RUN, "", "0.3", -1, -1, 0);
	Mapping mSource = ee.executeRun(run1);
	
	assertTrue(mSource.toString().equals(""));
	System.out.println("---------------------------------");

    }
    @Test
    public void BasicUnion() {
	System.out.println("BasicUnion");
	SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
	Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
	Instruction run2 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 1);
	Instruction union = new Instruction(Command.UNION, "", "0.4", 0, 1, 2);

	Mapping mSource = ee.executeRun(run1);
	System.out.println("Source : " + mSource.getNumberofMappings());

	Mapping mUnion = ee.executeUnion(mSource, mSource);

	Plan plan2 = new Plan();
	plan2.addInstruction(run1);
	plan2.addInstruction(run2);
	plan2.addInstruction(union);
	Mapping mUnion2 = ee.execute(plan2);
	System.out.println("executeUnion with self: " + mUnion.getNumberofMappings());
	System.out.println("Union as set of Instructions with self: " + mUnion2.getNumberofMappings());

	//A U A = A
	assertTrue(mUnion.toString().equals(mUnion2.toString()));
	assertTrue(mUnion.getNumberofMappings() == mUnion2.getNumberofMappings());
	assertTrue(mUnion.getNumberofMappings() == mSource.getNumberofMappings());

	Mapping emptyMapping = new MemoryMapping();
	Mapping mEmpty = ee.executeUnion(mSource, emptyMapping);
	System.out.println("executeUnion with empty: " + mEmpty.getNumberofMappings());
	//A U 0 = A
	assertTrue(mSource.getNumberofMappings() == mEmpty.getNumberofMappings());

	
	Mapping totalEmpty = ee.executeUnion(emptyMapping,emptyMapping);
	System.out.println("executeUnion with totalEmpty with empty: " + totalEmpty.getNumberofMappings());
	assertTrue(totalEmpty.toString().equals(""));

	System.out.println("---------------------------------");
    }

    @Test
    public void basicIntersection() {
	System.out.println("basicIntersection");
	SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
	Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
	Instruction run2 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 1);
	Instruction intersection = new Instruction(Command.INTERSECTION, "", "0.4", 0, 1, 2);

	Mapping mSource = ee.executeRun(run1);
	System.out.println("Source : " + mSource.getNumberofMappings());
	Mapping mIntersection = ee.executeIntersection(mSource, mSource);

	Plan plan3 = new Plan();
	plan3.addInstruction(run1);
	plan3.addInstruction(run2);
	plan3.addInstruction(intersection);
	Mapping mIntersection2 = ee.execute(plan3);
	System.out.println("executeIntersection with self: " + mIntersection.getNumberofMappings());
	System.out.println("Intersection as set of Instructions with self: " + mIntersection2.getNumberofMappings());

	//A & A = A
	assertTrue(mIntersection.toString().equals(mIntersection2.toString()));
	assertTrue(mIntersection.getNumberofMappings() == mIntersection2.getNumberofMappings());
	assertTrue(mIntersection.getNumberofMappings() == mSource.getNumberofMappings());

	//A & 0 = 0
	Mapping emptyMapping = new MemoryMapping();
	Mapping mEmpty = ee.executeIntersection(mSource, emptyMapping);
	System.out.println("executeIntersection with empty: " + mEmpty.getNumberofMappings());
	assertTrue(mSource.getNumberofMappings() >= mEmpty.getNumberofMappings());
	
	Mapping totalEmpty = ee.executeIntersection(emptyMapping,emptyMapping);
	System.out.println("executeIntersection with totalEmpty with empty: " + totalEmpty.getNumberofMappings());
	assertTrue(totalEmpty.toString().equals(""));
	System.out.println("---------------------------------");
    }

    @Test
    public void basicDifference() {
	System.out.println("basicDifference");
	SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
	Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
	Instruction run2 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 1);
	Instruction difference = new Instruction(Command.DIFF, "", "0.4", 0, 1, 2);

	Mapping mSource = ee.executeRun(run1);
	System.out.println("Source : " + mSource.getNumberofMappings());
	Mapping mDifference = ee.executeDifference(mSource, mSource);

	Plan plan4 = new Plan();
	plan4.addInstruction(run1);
	plan4.addInstruction(run2);
	plan4.addInstruction(difference);
	Mapping mDifference2 = ee.execute(plan4);
	System.out.println("executeDifference with self: " + mDifference.getNumberofMappings());
	System.out.println("Difference as set of Instructions with self: " + mDifference2.getNumberofMappings());
	//A - A = 0
	assertTrue(mDifference.toString().equals(mDifference2.toString()));
	assertTrue(mDifference.getNumberofMappings() == mDifference2.getNumberofMappings());
	assertTrue(mDifference.getNumberofMappings() <= mSource.getNumberofMappings());
	assertTrue(mDifference.getNumberofMappings() == 0);
	
	//A - 0 = A
	Mapping emptyMapping = new MemoryMapping();
	Mapping mEmpty = ee.executeDifference(mSource, emptyMapping);
	System.out.println("mDifference with empty: " + mEmpty.getNumberofMappings());
	assertTrue(mSource.getNumberofMappings() == mEmpty.getNumberofMappings());
	assertTrue(mEmpty.toString().equals(mSource.toString()));

	
	Mapping totalEmpty = ee.executeIntersection(emptyMapping,emptyMapping);
	System.out.println("executeIntersection with totalEmpty with empty: " + totalEmpty.getNumberofMappings());
	assertTrue(totalEmpty.toString().equals(""));
	System.out.println("---------------------------------");
    }

    @Test
    public void basicXor() {
	System.out.println("basicXor");
	SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
	Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
	Instruction run2 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 1);
	Instruction xor = new Instruction(Command.XOR, "", "0.4", 0, 1, 2);

	Mapping mSource = ee.executeRun(run1);
	System.out.println("Source : " + mSource.getNumberofMappings());
	Mapping mXor = ee.executeExclusiveOr(mSource, mSource);

	Plan plan5 = new Plan();
	plan5.addInstruction(run1);
	plan5.addInstruction(run2);
	plan5.addInstruction(xor);
	Mapping mXor2 = ee.execute(plan5);
	System.out.println("executeExclusiveOr with self: " + mXor.getNumberofMappings());
	System.out.println("Xor as set of Instructions with self: " + mXor2.getNumberofMappings());

	//(A U A) - (A & A) = A - A = 0
	assertTrue(mXor.toString().equals(mXor2.toString()));
	assertTrue(mXor.getNumberofMappings() == mXor2.getNumberofMappings());
	assertTrue(mXor.getNumberofMappings() == 0);
	assertTrue(mXor.getNumberofMappings() <= mSource.getNumberofMappings());

	//(A U 0) - (A & 0) = A - 0 = A
	Mapping emptyMapping = new MemoryMapping();
	Mapping mEmpty = ee.executeExclusiveOr(mSource, emptyMapping);
	System.out.println("mXor with empty: " + mEmpty.getNumberofMappings());
	assertTrue(mSource.getNumberofMappings() == mEmpty.getNumberofMappings());
	assertTrue(mEmpty.toString().equals(mSource.toString()));

	Mapping totalEmpty = ee.executeExclusiveOr(emptyMapping,emptyMapping);
	System.out.println("executeExclusiveOr with totalEmpty with empty: " + totalEmpty.getNumberofMappings());
	assertTrue(totalEmpty.toString().equals(""));
	System.out.println("---------------------------------");
    }

    @Test
    public void testAtomicLinkSpecification() {
	System.out.println("testAtomicLinkSpecificationRun");
	LinkSpecification ls = new LinkSpecification("jaccard(x.surname, y.surname)", 0.3);
	SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
	IPlanner cp = new CanonicalPlanner();
	Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);

	// 1) run as a NestedPlan calling execute function
	NestedPlan plan = cp.plan(ls);
	Mapping m = ee.execute(plan);
	System.out.println("NestedPlan + execute function: " + m.getNumberofMappings());
	// 2) run Instruction by calling executeRun
	Mapping m2 = ee.executeRun(run1);
	System.out.println("run Instruction + executeRun: " + m2.getNumberofMappings());
	// 3) run as a Plan with ONLY instruction calling run function
	Plan plan2 = new Plan();
	plan2.addInstruction(run1);
	Mapping m3 = ee.execute(plan2);
	System.out.println("Plan (with Instruction) + execute: " + m3.getNumberofMappings());
	// 3) run as a Plan with ONLY instruction calling run function
	Plan plan3 = new NestedPlan();
	plan3.addInstruction(run1);
	Mapping m4 = ee.execute(plan3);
	System.out.println("nestedPlan (with Instruction) + execute: " + m4.getNumberofMappings());
	/////////////////////////////////////////////////////////////////////

	assertTrue(m.getNumberofMappings() == m2.getNumberofMappings());
	assertTrue(m2.getNumberofMappings() == m3.getNumberofMappings());
	assertTrue(m3.getNumberofMappings() == m4.getNumberofMappings());

	assertTrue(m.toString().equals(m2.toString()));
	assertTrue(m2.toString().equals(m3.toString()));
	assertTrue(m3.toString().equals(m4.toString()));

	assertTrue(m.getNumberofMappings() >= 0);


	System.out.println("---------------------------------");
    }

    @Test
    public void testUnion() {
	System.out.println("testUnion");
	LinkSpecification ls = new LinkSpecification("OR(qgrams(x.surname,y.surname)|0.4,jaro(x.name,y.name)|0.4)",
		0.4);
	SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
	IPlanner cp = new CanonicalPlanner();

	Instruction run1 = new Instruction(Command.RUN, "qgrams(x.surname,y.surname)", "0.4", -1, -1, 0);
	Instruction run2 = new Instruction(Command.RUN, "jaro(x.name,y.name)", "0.4", -1, -1, 1);
	Instruction union = new Instruction(Command.UNION, "", "", 0, 1, 2);
	Instruction filter = new Instruction(Command.FILTER, null, "0.4", 2, -1, -1);

	// 1) run as a NestedPlan calling execute function
	NestedPlan plan = cp.plan(ls);
	Mapping m = ee.execute(plan);
	System.out.println("NestedPlan + execute function: " + m.getNumberofMappings());
	// 2) execute runs independently
	Mapping mSource = ee.executeRun(run1);
	Mapping mTarget = ee.executeRun(run2);
	Mapping m2 = ee.executeUnion(mSource, mTarget);
	m2 = ee.executeFilter(filter, m2);
	System.out.println("Execute Instructions independently : " + m2.getNumberofMappings());
	// 3) run as a plan with ONLY instruction calling execute function
	Plan plan2 = new Plan();
	plan2.addInstruction(run1);
	plan2.addInstruction(run2);
	plan2.addInstruction(union);
	plan2.addInstruction(filter);
	Mapping m3 = ee.execute(plan2);
	System.out.println("NestedPlan (with Instructions) + execute: " + m3.getNumberofMappings());
	// 4) run as a nestedplan with ONLY instruction calling execute function
	Plan plan3 = new NestedPlan();
	plan3.addInstruction(run1);
	plan3.addInstruction(run2);
	plan3.addInstruction(union);
	plan3.addInstruction(filter);
	Mapping m4 = ee.execute(plan2);
	System.out.println("nestedPlan (with Instructions) + execute: " + m4.getNumberofMappings());
	/////////////////////////////////////////////////////////////////////
	System.out.println("Size of left child: " + mSource.size());
	System.out.println("Size of right child: " + mTarget.size());
	assertTrue(m.getNumberofMappings() == m2.getNumberofMappings());
	assertTrue(m2.getNumberofMappings() == m3.getNumberofMappings());
	assertTrue(m3.getNumberofMappings() == m4.getNumberofMappings());

	assertTrue(m.toString().equals(m2.toString()));
	assertTrue(m2.toString().equals(m3.toString()));
	assertTrue(m3.toString().equals(m4.toString()));

	if (mSource.size() == 0 && mTarget.size() == 0) {
	    assertTrue(m.getNumberofMappings() == 0);
	} else {
	    assertTrue(m.getNumberofMappings() >= 0);

	}

	assertTrue(mSource.getNumberofMappings() <= m.getNumberofMappings());
	assertTrue(mTarget.getNumberofMappings() <= m.getNumberofMappings());

	System.out.println("---------------------------------");

    }

    @Test
    public void testIntersection() {
	System.out.println("testIntersection");
	LinkSpecification ls = new LinkSpecification("AND(euclidean(x.age, y.age)|0.5,qgrams(x.name, y.name)|0.5)",
		0.5);
	// instructions for RUN command
	Instruction run1 = new Instruction(Command.RUN, "euclidean(x.age, y.age)", "0.5", -1, -1, 0);
	Instruction run2 = new Instruction(Command.RUN, "qgrams(x.name, y.name)", "0.5", -1, -1, 1);
	// instructions for UNION command
	Instruction intersection = new Instruction(Command.INTERSECTION, "", "", 0, 1, 2);
	Instruction filter = new Instruction(Command.FILTER, null, "0.5", 2, -1, -1);
	// engine
	SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
	IPlanner cp = new CanonicalPlanner();

	// 1) run as a NestedPlan calling execute function
	NestedPlan plan = cp.plan(ls);
	Mapping m = ee.execute(plan);
	System.out.println("NestedPlan + execute function: " + m.getNumberofMappings());
	// 2) execute runs independently
	Mapping mSource = ee.executeRun(run1);
	Mapping mTarget = ee.executeRun(run2);
	Mapping m2 = ee.executeIntersection(mSource, mTarget);
	m2 = ee.executeFilter(filter, m2);
	System.out.println("Execute Instructions independently : " + m2.getNumberofMappings());
	// 3) run as a plan with ONLY instruction calling execute function
	Plan plan2 = new Plan();
	plan2.addInstruction(run1);
	plan2.addInstruction(run2);
	plan2.addInstruction(intersection);
	plan2.addInstruction(filter);
	Mapping m3 = ee.execute(plan2);
	System.out.println("Plan (with Instructions) + execute: " + m3.getNumberofMappings());
	// 4) run as a nestedplan with ONLY instruction calling execute function
	Plan plan3 = new NestedPlan();
	plan3.addInstruction(run1);
	plan3.addInstruction(run2);
	plan3.addInstruction(intersection);
	plan3.addInstruction(filter);
	Mapping m4 = ee.execute(plan2);
	System.out.println("nestedPlan (with Instructions) + execute: " + m4.getNumberofMappings());

	/////////////////////////////////////////////////////////////////////
	System.out.println("Size of left child: " + mSource.size());
	System.out.println("Size of right child: " + mTarget.size());

	assertTrue(m.getNumberofMappings() == m2.getNumberofMappings());
	assertTrue(m2.getNumberofMappings() == m3.getNumberofMappings());
	assertTrue(m3.getNumberofMappings() == m4.getNumberofMappings());

	assertTrue(m.toString().equals(m2.toString()));
	assertTrue(m2.toString().equals(m3.toString()));
	assertTrue(m3.toString().equals(m4.toString()));

	if (mSource.size() == 0 || mTarget.size() == 0) {
	    assertTrue(m.getNumberofMappings() == 0);
	} else {
	    assertTrue(m.getNumberofMappings() >= 0);

	}

	System.out.println("---------------------------------");

    }

    @Test
    public void testDifference() {
	System.out.println("testDifference");
	LinkSpecification ls = new LinkSpecification(
		"MINUS(trigrams(x.surname, y.surname)|0.5,overlap(x.name, y.name)|0.5)", 0.5);
	// instructions for RUN command
	Instruction run1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.5", -1, -1, 0);
	Instruction run2 = new Instruction(Command.RUN, "overlap(x.name, y.name)", "0.5", -1, -1, 1);
	// instructions for UNION command
	Instruction difference = new Instruction(Command.DIFF, "", "", 0, 1, 2);
	Instruction filter = new Instruction(Command.FILTER, null, "0.5", 2, -1, -1);
	IPlanner cp = new CanonicalPlanner();

	// engine
	SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");

	/// 1) run as a NestedPlan calling execute function
	NestedPlan plan = cp.plan(ls);
	Mapping m = ee.execute(plan);
	System.out.println("NestedPlan + execute function: " + m.getNumberofMappings());
	// 2) execute runs independently
	Mapping mSource = ee.executeRun(run1);
	Mapping mTarget = ee.executeRun(run2);
	Mapping m2 = ee.executeDifference(mSource, mTarget);
	m2 = ee.executeFilter(filter, m2);
	System.out.println("Execute Instructions independently : " + m2.getNumberofMappings());
	// 3) run as a plan with ONLY instruction calling execute function
	Plan plan2 = new Plan();
	plan2.addInstruction(run1);
	plan2.addInstruction(run2);
	plan2.addInstruction(difference);
	plan2.addInstruction(filter);
	Mapping m3 = ee.execute(plan2);
	System.out.println("Plan (with Instructions) + execute: " + m3.getNumberofMappings());
	// 4) run as a nestedplan with ONLY instruction calling execute function
	Plan plan3 = new NestedPlan();
	plan3.addInstruction(run1);
	plan3.addInstruction(run2);
	plan3.addInstruction(difference);
	plan3.addInstruction(filter);
	Mapping m4 = ee.execute(plan2);
	System.out.println("nestedPlan (with Instructions) + execute: " + m4.getNumberofMappings());

	/////////////////////////////////////////////////////////////////////
	System.out.println("Size of left child: " + mSource.size());
	System.out.println("Size of right child: " + mTarget.size());
	assertTrue(m.getNumberofMappings() == m2.getNumberofMappings());
	assertTrue(m2.getNumberofMappings() == m3.getNumberofMappings());
	assertTrue(m3.getNumberofMappings() == m4.getNumberofMappings());

	assertTrue(m.toString().equals(m2.toString()));
	assertTrue(m2.toString().equals(m3.toString()));
	assertTrue(m3.toString().equals(m4.toString()));

	if (mSource.size() == 0 && mTarget.size() == 0) {
	    assertTrue(m.getNumberofMappings() == 0);
	} else {
	    if (!mSource.toString().equals(mTarget.toString()))
		assertTrue(m.getNumberofMappings() >= 0);
	    else
		assertTrue(m.getNumberofMappings() == 0);
	}
	assertTrue(mSource.getNumberofMappings() >= m.getNumberofMappings());

	System.out.println("---------------------------------");

    }

    @Test
    public void testXor() {
	System.out.println("testXor");
	LinkSpecification ls = new LinkSpecification(
		"XOR(trigrams(x.surname, y.surname)|0.5,soundex(x.name, y.name)|0.5)", 0.5);
	// instructions for RUN command
	Instruction run1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.5", -1, -1, 0);
	Instruction run2 = new Instruction(Command.RUN, "soundex(x.name, y.name)", "0.5", -1, -1, 1);
	// instructions for UNION command
	Instruction xor = new Instruction(Command.XOR, "", "", 0, 1, 2);
	Instruction filter = new Instruction(Command.FILTER, null, "0.5", 2, -1, -1);

	// engine
	SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");

	IPlanner cp = new CanonicalPlanner();

	/// 1) run as a NestedPlan calling execute function
	NestedPlan plan = cp.plan(ls);
	Mapping m = ee.execute(plan);
	System.out.println("NestedPlan + execute function: " + m.getNumberofMappings());
	// 2) execute runs independently
	Mapping mSource = ee.executeRun(run1);
	Mapping mTarget = ee.executeRun(run2);
	Mapping m2 = ee.executeExclusiveOr(mSource, mTarget);
	m2 = ee.executeFilter(filter, m2);
	System.out.println("run Instruction + execute*: " + m2.getNumberofMappings());
	// 3) run as a plan with ONLY instruction calling execute function
	Plan plan2 = new Plan();
	plan2.addInstruction(run1);
	plan2.addInstruction(run2);
	plan2.addInstruction(xor);
	plan2.addInstruction(filter);
	Mapping m3 = ee.execute(plan2);
	System.out.println("Plan (with Instructions) + execute: " + m3.getNumberofMappings());
	// 4) run as a nestedplan with ONLY instruction calling execute function
	Plan plan3 = new NestedPlan();
	plan3.addInstruction(run1);
	plan3.addInstruction(run2);
	plan3.addInstruction(xor);
	plan3.addInstruction(filter);
	Mapping m4 = ee.execute(plan2);
	System.out.println("nestedPlan (with Instructions) + execute: " + m4.getNumberofMappings());

	/////////////////////////////////////////////////////////////////////
	System.out.println("Size of left child: " + mSource.size());
	System.out.println("Size of right child: " + mTarget.size());
	assertTrue(m.getNumberofMappings() == m2.getNumberofMappings());
	assertTrue(m2.getNumberofMappings() == m3.getNumberofMappings());
	assertTrue(m3.getNumberofMappings() == m4.getNumberofMappings());

	assertTrue(m.toString().equals(m2.toString()));
	assertTrue(m2.toString().equals(m3.toString()));
	assertTrue(m3.toString().equals(m4.toString()));

	if (mSource.size() == 0 && mTarget.size() == 0) {
	    assertTrue(m.getNumberofMappings() == 0);
	} else {
	    if (!mSource.toString().equals(mTarget.toString()))
		assertTrue(m.getNumberofMappings() >= 0);
	    else
		assertTrue(m.getNumberofMappings() == 0);
	}


	System.out.println("---------------------------------");

    }

}
