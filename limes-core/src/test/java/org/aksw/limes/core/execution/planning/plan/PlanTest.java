package org.aksw.limes.core.execution.planning.plan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PlanTest {
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
	public void addInstruction() {

		Plan plan = new Plan();
		Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);

		plan.addInstruction(run1);
		plan.addInstruction(null);

		assertTrue(plan.size() == 1);


	}

	@Test
	public void removeInstruction() {

		Plan plan = new Plan();
		Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
		plan.addInstruction(run1);
		plan.addInstruction(null);
		plan.addInstruction(null);

		plan.removeInstruction(run1);
		plan.removeInstruction(null);
		assertTrue(plan.size() == 0);

		plan.removeInstruction(-1);

	}

	@Test
	public void removeInstruction2() {

		Plan plan = new Plan();
		Instruction run1 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
		Instruction run2 = new Instruction(Command.RUN, "cosine(x.name, y.name)", "0.3", -1, -1, 1);
		Instruction union = new Instruction(Command.UNION, "", "0.3", 0, 1, 2);
		Instruction instersection = new Instruction(Command.INTERSECTION, "", "0.3", 0, 1, 2);

		plan.addInstruction(run1);
		plan.addInstruction(run2);
		plan.addInstruction(union);

		SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y", 0, 1.0);
		AMapping mUnion = ee.executeInstructions(plan);

		plan.removeInstruction(union);
		plan.addInstruction(instersection);

		AMapping mIntersection = ee.executeInstructions(plan);

		assertTrue(!mUnion.toString().equals(mIntersection.toString()));
		assertTrue(plan.getInstructionList().contains(union) == false);



	}

	@Test
	public void removeNonExistingInstruction() {

		Plan plan = new Plan();
		Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
		plan.addInstruction(run1);

		Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
		plan.removeInstruction(run2);
		assertTrue(plan.size() != 0);

		plan.removeInstruction(-1);

	}

	@Test
	public void removeInstructionWithIndex() {

		Plan plan = new Plan();
		Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
		plan.addInstruction(run1);


		plan.removeInstruction(0);
		assertTrue(plan.size() == 0);

		plan.addInstruction(run1);

		plan.removeInstruction(20);
		assertTrue(plan.size() != 0);

		plan.removeInstruction(-1);

	}

	@Test
	public void isEmpty() {

		Plan plan = new Plan();
		Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
		plan.addInstruction(run1);
		assertTrue(plan.isEmpty() == false);

		plan.removeInstruction(null);
		assertTrue(plan.isEmpty() == false);


	}

	@Test
	public void getInstructionList() {

		Plan plan = new Plan();
		Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
		Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
		plan.addInstruction(run1);
		plan.addInstruction(run2);
		List<Instruction> list = plan.getInstructionList();


		assertTrue(list.size() == plan.size());

		assertTrue(list.contains(run1) == true);


		Instruction run3 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
		assertTrue(list.contains(run3) == true);

		Instruction run4 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
		assertTrue(list.contains(run4) == false);

		list.add(run4);

		assertTrue(list.size() == plan.getInstructionList().size());



	}

	@Test
	public void Clone() {
		Plan plan1 = new Plan();

		Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
		Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
		plan1.addInstruction(run1);
		plan1.addInstruction(run2);


		plan1.setMappingSize(10);
		plan1.setRuntimeCost(1000d);
		plan1.setSelectivity(0.1d);

		//////////////////////////////////////////////////////////////////////////////////////////
		Plan clonePlan = plan1.clone();
		// check plan itself
		assertEquals(plan1.hashCode(), clonePlan.hashCode());

		// check instructionList
		assertEquals(plan1.getInstructionList().hashCode(),
				clonePlan.getInstructionList().hashCode());
		for (int i = 0; i < plan1.getInstructionList().size(); i++) {

			Instruction inst = plan1.getInstructionList().get(i);
			Instruction instClone = clonePlan.getInstructionList().get(i);

			assertEquals(inst.hashCode(), instClone.hashCode());
		}

	}

	@Test
	public void Equal() {
		Plan plan1 = new Plan();

		Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
		Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
		plan1.addInstruction(run1);
		plan1.addInstruction(run2);

		//////////////////////////////////////////////////////////////////////////////////////////
		Plan plan2 = new Plan();
		Instruction run3 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
		Instruction run4 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
		plan2.addInstruction(run3);
		plan2.addInstruction(run4);

		///////////////////////////////////////////////////////////////////////////////////////////
		assertTrue(plan1.equals(plan2));

		NestedPlan plan3 = null;
		assertTrue(!plan1.equals(plan3));
		///////////////////////////////////////////////////////////////////////////////////////////


	}
}
