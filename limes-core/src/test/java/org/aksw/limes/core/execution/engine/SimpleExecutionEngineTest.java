package org.aksw.limes.core.execution.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.execution.engine.filter.LinearFilter;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.execution.planning.planner.CanonicalPlanner;
import org.aksw.limes.core.execution.planning.planner.DynamicPlanner;
import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SimpleExecutionEngineTest {
	public ACache source = new MemoryCache();
	public ACache target = new MemoryCache();

	public AMapping lukUnion = MappingFactory.createDefaultMapping();
	public AMapping lukIntersection = MappingFactory.createDefaultMapping();
	public AMapping lukDifference = MappingFactory.createDefaultMapping();

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

		preparelukasiewiczGoldData();
	}

	private void preparelukasiewiczGoldData(){
		lukUnion.add("S4", "T4", 1.0);
		lukUnion.add("S5", "T5", 1.0);
		lukUnion.add("S1", "T1", 0.5);
		lukUnion.add("S2", "T2", 1.0);

		lukIntersection.add("S4", "T4", 0.6666666666666666);
		lukIntersection.add("S2", "T2", 1.0);

		lukDifference.add("S4", "T4", 0.33333333333333337);
		lukDifference.add("S5", "T5", 1.0);
		lukDifference.add("S1", "T1", 0.5);
	}

	@After
	public void tearDown() {
		source = null;
		target = null;
	}

	@Test
	public void negativeThreshold() {
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		Instruction run1 = new Instruction(Command.RUN, "cosine", "-0.3", -1, -1, 0);
		try {
			ee.executeRun(run1);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}
	@Test
	public void emptyMeasure() {
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		Instruction run1 = new Instruction(Command.RUN, "", "0.3", -1, -1, 0);
		try {
			ee.executeRun(run1);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}
	@Test
	public void bufferTest() {
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		Instruction run1 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.9", -1, -1, 0);
		Instruction run2 = new Instruction(Command.RUN, "trigrams(x.name, y.name)", "0.4", -1, -1, 1);
		Instruction union = new Instruction(Command.UNION, "", "0.4", 0, 1, 15);

		Plan smallPlan1 = new Plan();
		smallPlan1.addInstruction(run1);
		AMapping m1 = ee.executeInstructions(smallPlan1);
		assertTrue(ee.buffer.get(0).equals(m1));

		Plan smallPlan2 = new Plan();
		smallPlan2.addInstruction(run2);
		AMapping m2 = ee.executeInstructions(smallPlan2);
		assertTrue(ee.buffer.get(1).equals(m2));

		Plan plan2 = new Plan();
		plan2.addInstruction(run1);
		plan2.addInstruction(run2);
		plan2.addInstruction(union);
		AMapping mUnion2 = ee.executeInstructions(plan2);
		assertTrue(ee.buffer.get(15).equals(mUnion2));

		assertTrue(ee.buffer.get(10).getMap().isEmpty());
		assertTrue(ee.buffer.size() == 16);

	}

	@Test
	public void bufferTest2() {
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.9", -1, -1, 0);
		Instruction run2 = new Instruction(Command.RUN, "trigrams(x.name, y.name)", "0.4", -1, -1, 1);
		Instruction union = new Instruction(Command.UNION, "", "0.4", 0, 1, 0);

		Plan smallPlan1 = new Plan();
		smallPlan1.addInstruction(run1);
		AMapping m1 = ee.executeInstructions(smallPlan1);
		assertTrue(ee.buffer.get(0).equals(m1));

		Plan smallPlan2 = new Plan();
		smallPlan2.addInstruction(run2);
		AMapping m2 = ee.executeInstructions(smallPlan2);
		assertTrue(ee.buffer.get(1).equals(m2));

		Plan plan2 = new Plan();
		plan2.addInstruction(run1);
		plan2.addInstruction(run2);
		plan2.addInstruction(union);
		AMapping mUnion2 = ee.executeInstructions(plan2);
		assertTrue(ee.buffer.size() == 3);

		assertTrue(ee.buffer.get(2).equals(mUnion2));


	}

	@Test
	public void BasicUnion() {
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
		Instruction run2 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 1);
		Instruction union = new Instruction(Command.UNION, "", "0.4", 0, 1, 2);

		AMapping mSource = ee.executeRun(run1);

		AMapping mUnion = ee.executeUnion(mSource, mSource);

		Plan plan2 = new Plan();
		plan2.addInstruction(run1);
		plan2.addInstruction(run2);
		plan2.addInstruction(union);
		AMapping mUnion2 = ee.executeInstructions(plan2);

		// A U A = A
		assertTrue(mUnion.toString().equals(mUnion2.toString()));
		assertTrue(mUnion.getNumberofMappings() == mUnion2.getNumberofMappings());
		assertTrue(mUnion.getNumberofMappings() == mSource.getNumberofMappings());

		AMapping emptyMapping = MappingFactory.createDefaultMapping();
		AMapping mEmpty = ee.executeUnion(mSource, emptyMapping);
		// A U 0 = A
		assertTrue(mSource.getNumberofMappings() == mEmpty.getNumberofMappings());

		AMapping totalEmpty = ee.executeUnion(emptyMapping, emptyMapping);
		assertTrue(totalEmpty.toString().equals(""));

	}

	@Test
	public void basicIntersection() {
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
		Instruction run2 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 1);
		Instruction intersection = new Instruction(Command.INTERSECTION, "", "0.4", 0, 1, 2);

		AMapping mSource = ee.executeRun(run1);
		AMapping mIntersection = ee.executeIntersection(mSource, mSource);

		Plan plan3 = new Plan();
		plan3.addInstruction(run1);
		plan3.addInstruction(run2);
		plan3.addInstruction(intersection);
		AMapping mIntersection2 = ee.executeInstructions(plan3);

		// A & A = A
		assertTrue(mIntersection.toString().equals(mIntersection2.toString()));
		assertTrue(mIntersection.getNumberofMappings() == mIntersection2.getNumberofMappings());
		assertTrue(mIntersection.getNumberofMappings() == mSource.getNumberofMappings());

		// A & 0 = 0
		AMapping emptyMapping = MappingFactory.createDefaultMapping();
		AMapping mEmpty = ee.executeIntersection(mSource, emptyMapping);
		assertTrue(mSource.getNumberofMappings() >= mEmpty.getNumberofMappings());

		AMapping totalEmpty = ee.executeIntersection(emptyMapping, emptyMapping);
		assertTrue(totalEmpty.toString().equals(""));
	}

	@Test
	public void basicDifference() {
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
		Instruction run2 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 1);
		Instruction difference = new Instruction(Command.DIFF, "", "0.4", 0, 1, 2);

		AMapping mSource = ee.executeRun(run1);
		AMapping mDifference = ee.executeDifference(mSource, mSource);

		Plan plan4 = new Plan();
		plan4.addInstruction(run1);
		plan4.addInstruction(run2);
		plan4.addInstruction(difference);
		AMapping mDifference2 = ee.executeInstructions(plan4);
		// A - A = 0
		assertTrue(mDifference.toString().equals(mDifference2.toString()));
		assertTrue(mDifference.getNumberofMappings() == mDifference2.getNumberofMappings());
		assertTrue(mDifference.getNumberofMappings() <= mSource.getNumberofMappings());
		assertTrue(mDifference.getNumberofMappings() == 0);

		// A - 0 = A
		AMapping emptyMapping = MappingFactory.createDefaultMapping();
		AMapping mEmpty = ee.executeDifference(mSource, emptyMapping);
		assertTrue(mSource.getNumberofMappings() == mEmpty.getNumberofMappings());
		assertTrue(mEmpty.toString().equals(mSource.toString()));

		AMapping totalEmpty = ee.executeIntersection(emptyMapping, emptyMapping);
		assertTrue(totalEmpty.toString().equals(""));
	}

	@Test
	public void basicXor() {
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
		Instruction run2 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 1);
		Instruction xor = new Instruction(Command.XOR, "", "0.4", 0, 1, 2);
		AMapping mSource = ee.executeRun(run1);

		LinearFilter f = new LinearFilter();
		AMapping mleft = ee.executeUnion(mSource, mSource);
		mleft = f.filter(mleft, 0.4);

		AMapping mright = ee.executeIntersection(mSource, mSource);
		mright = f.filter(mright, 0.4);
		AMapping mXor = ee.executeDifference(mleft, mright);

		Plan plan5 = new Plan();
		plan5.addInstruction(run1);
		plan5.addInstruction(run2);
		plan5.addInstruction(xor);
		AMapping mXor2 = ee.executeInstructions(plan5);

		// (A U A) - (A & A) = A - A = 0
		assertTrue(mXor.toString().equals(mXor2.toString()));
		assertTrue(mXor.getNumberofMappings() == mXor2.getNumberofMappings());
		assertTrue(mXor.getNumberofMappings() == 0);
		assertTrue(mXor.getNumberofMappings() <= mSource.getNumberofMappings());

		// (A U 0) - (A & 0) = A - 0 = A
		AMapping emptyMapping = MappingFactory.createDefaultMapping();
		f = new LinearFilter();
		mleft = ee.executeUnion(mSource, emptyMapping);
		mleft = f.filter(mleft, 0.4);

		mright = ee.executeIntersection(mSource, emptyMapping);
		mright = f.filter(mright, 0.4);
		AMapping mEmpty = ee.executeDifference(mleft, mright);
		assertTrue(mSource.getNumberofMappings() == mEmpty.getNumberofMappings());
		assertTrue(mEmpty.toString().equals(mSource.toString()));

		f = new LinearFilter();
		mleft = ee.executeUnion(emptyMapping, emptyMapping);
		mleft = f.filter(mleft, 0.4);

		mright = ee.executeIntersection(emptyMapping, emptyMapping);
		mright = f.filter(mright, 0.4);
		AMapping totalEmpty = ee.executeDifference(mleft, mright);
		assertTrue(totalEmpty.toString().equals(""));
	}

	@Test
	public void testAtomicLinkSpecification() {
		LinkSpecification ls = new LinkSpecification("jaccard(x.surname, y.surname)", 0.3);
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		IPlanner cp = new CanonicalPlanner();
		Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);

		// 1) run as a NestedPlan calling execute function
		NestedPlan plan = cp.plan(ls);
		AMapping m = ee.executeStatic(plan);
		// 2) run Instruction by calling executeRun
		AMapping m2 = ee.executeRun(run1);
		// 3) run as a Plan with ONLY instruction calling run function
		Plan plan2 = new Plan();
		plan2.addInstruction(run1);
		AMapping m3 = ee.executeInstructions(plan2);
		// 3) run as a Plan with ONLY instruction calling run function
		NestedPlan plan3 = new NestedPlan();
		plan3.addInstruction(run1);
		AMapping m4 = ee.executeStatic(plan3);
		/////////////////////////////////////////////////////////////////////

		assertTrue(m.getNumberofMappings() == m2.getNumberofMappings());
		assertTrue(m2.getNumberofMappings() == m3.getNumberofMappings());
		assertTrue(m3.getNumberofMappings() == m4.getNumberofMappings());

		assertTrue(m.toString().equals(m2.toString()));
		assertTrue(m2.toString().equals(m3.toString()));
		assertTrue(m3.toString().equals(m4.toString()));

		assertTrue(m.getNumberofMappings() >= 0);

	}
	@Test
	public void testMax(){
		LinkSpecification ls = new LinkSpecification("OR(qgrams(x.surname,y.surname)|0.2,trigrams(x.name,y.name)|0.8)",
				0.6);

		LinkSpecification ls2 = new LinkSpecification("MAX(qgrams(x.surname,y.surname),trigrams(x.name,y.name))",
				0.6);
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		IPlanner cp = new CanonicalPlanner();
		AMapping m = ee.execute(ls, cp);

		ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		cp = new CanonicalPlanner();
		AMapping m2 = ee.execute(ls2, cp);

		assertTrue(m.equals(m2));


	}

	@Test
	public void testMin(){
		LinkSpecification ls = new LinkSpecification("AND(qgrams(x.surname,y.surname)|0.2,trigrams(x.name,y.name)|0.8)",
				0.6);

		LinkSpecification ls2 = new LinkSpecification("MIN(qgrams(x.surname,y.surname),trigrams(x.name,y.name))",
				0.6);
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		IPlanner cp = new DynamicPlanner(source, target);
		AMapping m = ee.execute(ls, cp);

		ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		cp = new DynamicPlanner(source, target);
		AMapping m2 = ee.execute(ls2, cp);



		assertTrue(!m.equals(m2));

	}


	@Test
	public void testUnion() {
		LinkSpecification ls = new LinkSpecification("OR(qgrams(x.surname,y.surname)|0.4,trigrams(x.name,y.name)|0.4)",
				0.4);
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		IPlanner cp = new CanonicalPlanner();

		Instruction run1 = new Instruction(Command.RUN, "qgrams(x.surname,y.surname)", "0.4", -1, -1, 0);
		Instruction run2 = new Instruction(Command.RUN, "trigrams(x.name,y.name)", "0.4", -1, -1, 1);
		Instruction union = new Instruction(Command.UNION, "", "", 0, 1, 2);
		Instruction filter = new Instruction(Command.FILTER, null, "0.4", 2, -1, -1);

		// 1) run as a NestedPlan calling execute function
		NestedPlan plan = cp.plan(ls);
		AMapping m = ee.executeStatic(plan);

		// 2) execute runs independently
		AMapping mSource = ee.executeRun(run1);
		AMapping mTarget = ee.executeRun(run2);
		AMapping m2 = ee.executeUnion(mSource, mTarget);
		m2 = ee.executeFilter(filter, m2);

		// 3) run as a plan with ONLY instruction calling execute function
		Plan plan2 = new Plan();
		plan2.addInstruction(run1);
		plan2.addInstruction(run2);
		plan2.addInstruction(union);
		plan2.addInstruction(filter);
		AMapping m3 = ee.executeInstructions(plan2);

		// 4) run as a nestedplan with ONLY instruction calling execute function
		NestedPlan plan3 = new NestedPlan();
		plan3.addInstruction(run1);
		plan3.addInstruction(run2);
		plan3.addInstruction(union);
		plan3.addInstruction(filter);
		AMapping m4 = ee.executeStatic(plan3);

		/////////////////////////////////////////////////////////////////////
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


	}

	@Test
	public void testIntersection() {
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
		AMapping m = ee.executeStatic(plan);
		// 2) execute runs independently
		AMapping mSource = ee.executeRun(run1);
		AMapping mTarget = ee.executeRun(run2);
		AMapping m2 = ee.executeIntersection(mSource, mTarget);
		m2 = ee.executeFilter(filter, m2);
		// 3) run as a plan with ONLY instruction calling execute function
		Plan plan2 = new Plan();
		plan2.addInstruction(run1);
		plan2.addInstruction(run2);
		plan2.addInstruction(intersection);
		plan2.addInstruction(filter);
		AMapping m3 = ee.executeInstructions(plan2);
		// 4) run as a nestedplan with ONLY instruction calling execute function
		NestedPlan plan3 = new NestedPlan();
		plan3.addInstruction(run1);
		plan3.addInstruction(run2);
		plan3.addInstruction(intersection);
		plan3.addInstruction(filter);
		AMapping m4 = ee.executeStatic(plan3);

		/////////////////////////////////////////////////////////////////////

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


	}

	@Test
	public void testDifference() {
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
		AMapping m = ee.executeStatic(plan);
		// 2) execute runs independently
		AMapping mSource = ee.executeRun(run1);
		AMapping mTarget = ee.executeRun(run2);
		AMapping m2 = ee.executeDifference(mSource, mTarget);
		m2 = ee.executeFilter(filter, m2);
		// 3) run as a plan with ONLY instruction calling execute function
		Plan plan2 = new Plan();
		plan2.addInstruction(run1);
		plan2.addInstruction(run2);
		plan2.addInstruction(difference);
		plan2.addInstruction(filter);
		AMapping m3 = ee.executeInstructions(plan2);
		// 4) run as a nestedplan with ONLY instruction calling execute function
		NestedPlan plan3 = new NestedPlan();
		plan3.addInstruction(run1);
		plan3.addInstruction(run2);
		plan3.addInstruction(difference);
		plan3.addInstruction(filter);
		AMapping m4 = ee.executeStatic(plan3);

		/////////////////////////////////////////////////////////////////////
		assertTrue(m.getNumberofMappings() == m2.getNumberofMappings());
		assertTrue(m2.getNumberofMappings() == m3.getNumberofMappings());
		assertTrue(m3.getNumberofMappings() == m4.getNumberofMappings());

		assertTrue(m.toString().equals(m2.toString()));
		assertTrue(m2.toString().equals(m3.toString()));
		assertTrue(m3.toString().equals(m4.toString()));

		if (mSource.size() == 0 && mTarget.size() == 0) {
			assertTrue(m.getNumberofMappings() == 0);
		} else {
			if (!mSource.toString().equals(mTarget.toString())) {
				assertTrue(m.getNumberofMappings() >= 0);
			} else {
				assertTrue(m.getNumberofMappings() == 0);
			}
		}
		assertTrue(mSource.getNumberofMappings() >= m.getNumberofMappings());


	}

	@Test
	public void testXor() {
		LinkSpecification ls = new LinkSpecification(
				"XOR(trigrams(x.surname, y.surname)|0.5,soundex(x.name, y.name)|0.5)", 0.5);
		// instructions for RUN command
		Instruction run1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.5", -1, -1, 0);
		Instruction run2 = new Instruction(Command.RUN, "soundex(x.name, y.name)", "0.5", -1, -1, 1);
		// instructions for UNION command
		Instruction xor = new Instruction(Command.XOR, "", "0.5", 0, 1, 2);
		Instruction filter = new Instruction(Command.FILTER, null, "0.5", 2, -1, -1);

		// engine
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");

		IPlanner cp = new CanonicalPlanner();

		/// 1) run as a NestedPlan calling execute function
		NestedPlan plan = cp.plan(ls);
		AMapping m = ee.executeStatic(plan);

		// 4) run as a nestedplan with ONLY instruction calling execute function
		Plan plan3 = new NestedPlan();
		plan3.addInstruction(run1);
		plan3.addInstruction(run2);
		plan3.addInstruction(xor);
		plan3.addInstruction(filter);
		AMapping m4 = ee.executeInstructions(plan3);

		/////////////////////////////////////////////////////////////////////

		assertTrue(m.getNumberofMappings() == m4.getNumberofMappings());

		assertTrue(m.toString().equals(m4.toString()));



	}

	@Test
	public void extraTest() {
		LinkSpecification ls = new LinkSpecification("OR(qgrams(x.surname,y.surname)|0.4,trigrams(x.name,y.name)|0.4)",
				0.4);
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		IPlanner cp = new CanonicalPlanner();


		Plan plan = cp.plan(ls);
		ee.executeInstructions(plan);


	}

	@Test
	public void lukasiewiczUnion() {
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		Instruction run1 = new Instruction(Command.RUN, "qgrams(x.surname,y.surname)", "0.4", -1, -1, 0);
		Instruction run2 = new Instruction(Command.RUN, "trigrams(x.name,y.name)", "0.4", -1, -1, 1);
		Instruction union = new Instruction(Command.LUKASIEWICZTCO, "", "0.0", 0, 1, 2);

		AMapping a = ee.executeRun(run1);
		AMapping b = ee.executeRun(run2);

		AMapping mUnion = ee.executeUnion(a, b, Command.LUKASIEWICZTCO);

		Plan plan = new Plan();
		plan.addInstruction(run1);
		plan.addInstruction(run2);
		plan.addInstruction(union);
		AMapping mUnion2 = ee.executeInstructions(plan);

		assertEquals(mUnion, mUnion2);
		assertEquals(lukUnion, mUnion);

		AMapping emptyMapping = MappingFactory.createDefaultMapping();
		AMapping mEmpty = ee.executeUnion(mUnion, emptyMapping);
		// 0 is identity element
		assertEquals(mUnion, mEmpty);

		AMapping totalEmpty = ee.executeUnion(emptyMapping, emptyMapping);
		assertEquals(emptyMapping, totalEmpty);

	}

	@Test
	public void lukasiewiczIntersection() {
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		Instruction run1 = new Instruction(Command.RUN, "qgrams(x.surname,y.surname)", "0.4", -1, -1, 0);
		Instruction run2 = new Instruction(Command.RUN, "trigrams(x.name,y.name)", "0.4", -1, -1, 1);
		Instruction intersection = new Instruction(Command.LUKASIEWICZT, "", "0.4", 0, 1, 2);

		AMapping a = ee.executeRun(run1);
		AMapping b = ee.executeRun(run2);

		AMapping mIntersection = ee.executeIntersection(a, b, Command.LUKASIEWICZT);

		Plan plan = new Plan();
		plan.addInstruction(run1);
		plan.addInstruction(run2);
		plan.addInstruction(intersection);
		AMapping mIntersection2 = ee.executeInstructions(plan);

		assertEquals(mIntersection, mIntersection2);
		assertEquals(lukIntersection, mIntersection);

		AMapping emptyMapping = MappingFactory.createDefaultMapping();
		AMapping mEmpty = ee.executeIntersection(mIntersection, emptyMapping);
		assertEquals(mEmpty, emptyMapping);
	}

	@Test
	public void lukasiewiczDifference() {
		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
		Instruction run1 = new Instruction(Command.RUN, "qgrams(x.surname,y.surname)", "0.4", -1, -1, 0);
		Instruction run2 = new Instruction(Command.RUN, "trigrams(x.name,y.name)", "0.4", -1, -1, 1);
		Instruction difference = new Instruction(Command.LUKASIEWICZDIFF, "", "0.4", 0, 1, 2);

		AMapping a = ee.executeRun(run1);
		AMapping b = ee.executeRun(run2);

		AMapping mDifference = ee.executeDifference(a, b, Command.LUKASIEWICZDIFF);

		Plan plan = new Plan();
		plan.addInstruction(run1);
		plan.addInstruction(run2);
		plan.addInstruction(difference);
		AMapping mDifference2 = ee.executeInstructions(plan);

		assertEquals(mDifference, mDifference2);
		assertEquals(lukDifference, mDifference);
	}
}
