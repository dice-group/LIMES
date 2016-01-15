package org.aksw.limes.core.execution.planning.plan;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NestedPlanTest {


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
    public void addInstruction() {
	System.out.println("addInstruction");

	Plan plan = new NestedPlan();
	Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
	System.out.println("Size before: " + plan.size());

	plan.addInstruction(run1);
	plan.addInstruction(null);

	assertTrue(plan.size() == 1);
	System.out.println("Size after: " + plan.size());

	System.out.println("------------------------");

    }

    @Test
    public void removeInstruction() {
	System.out.println("removeInstruction");

	Plan plan = new NestedPlan();
	Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
	plan.addInstruction(run1);
	plan.addInstruction(null);
	plan.addInstruction(null);
	System.out.println("Size before: " + plan.size());

	plan.removeInstruction(run1);
	plan.removeInstruction(null);
	System.out.println("Size after: " + plan.size());
	assertTrue(plan.size() == 0);

	plan.removeInstruction(-1);
	System.out.println("------------------------");

    }

    @Test
    public void removeInstruction2() {
	System.out.println("removeInstruction2");

	NestedPlan plan = new NestedPlan();
	Instruction run1 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
	Instruction run2 = new Instruction(Command.RUN, "cosine(x.name, y.name)", "0.3", -1, -1, 1);
	Instruction union = new Instruction(Command.UNION, "", "0.3", 0, 1, 2);
	Instruction instersection = new Instruction(Command.INTERSECTION, "", "0.3", 0, 1, 2);

	plan.addInstruction(run1);
	plan.addInstruction(run2);
	plan.addInstruction(union);

	System.out.println("Plan size with Union: " + plan.size());
	SimpleExecutionEngine ee = new SimpleExecutionEngine(source, target, "?x", "?y");
	Mapping mUnion = ee.execute(plan);
	System.out.println("Size of Mapping with Union: " + mUnion.size());

	plan.removeInstruction(union);
	plan.addInstruction(instersection);

	System.out.println("Plan size with Intersection: " + plan.size());
	Mapping mIntersection = ee.execute(plan);
	System.out.println("Size of Mapping with Intersection: " + mIntersection.size());
	
	assertTrue(!mUnion.toString().equals(mIntersection.toString()));
	assertTrue(plan.getInstructionList().contains(union) == false);

	
	System.out.println("------------------------");

    }
    @Test
    public void removeNonExistingInstruction() {
	System.out.println("removeNonExistingInstruction");

	Plan plan = new NestedPlan();
	Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
	plan.addInstruction(run1);
	System.out.println("Size before: " + plan.size());

	Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
	plan.removeInstruction(run2);
	System.out.println("Size after: " + plan.size());
	assertTrue(plan.size() != 0);

	plan.removeInstruction(-1);
	System.out.println("------------------------");

    }
    @Test
    public void removeInstructionWithIndex() {
	System.out.println("removeInstructionWithIndex");

	Plan plan = new NestedPlan();
	Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
	plan.addInstruction(run1);
	System.out.println("Size before: " + plan.size());

	
	plan.removeInstruction(0);
	System.out.println("Size after: " + plan.size());
	assertTrue(plan.size() == 0);

	plan.addInstruction(run1);
	System.out.println("Size before: " + plan.size());
	
	plan.removeInstruction(20);
	System.out.println("Size after: " + plan.size());
	assertTrue(plan.size() != 0);
	
	plan.removeInstruction(-1);
	System.out.println("------------------------");

    }
    @Test
    public void isEmpty() {
	System.out.println("isEmpty");

	Plan plan = new NestedPlan();
	Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
	plan.addInstruction(run1);
	System.out.println("Size before: " + plan.size());
	assertTrue(plan.isEmpty() == false);

	plan.removeInstruction(null);
	assertTrue(plan.isEmpty() == false);
	
	plan.removeInstruction(run1);
	assertTrue(plan.isEmpty() == true);

	System.out.println("------------------------");

    }
    @Test
    public void getInstructionList() {
	System.out.println("getInstructionList");

	Plan plan = new NestedPlan();
	Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
	Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
	plan.addInstruction(run1);
	plan.addInstruction(run2);
	List<Instruction> list = plan.getInstructionList();
	System.out.println("Size before: " + plan.size());
	System.out.println("Size of Instruction list " + list.size());

	assertTrue(list.size() == plan.size());
	assertTrue(list.contains(run1) == true);
	
	
	Instruction run3 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
	assertTrue(list.contains(run3) == true);
	
	Instruction run4 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
	assertTrue(list.contains(run4) == false);
	list.add(run4);
	System.out.println(list);
	System.out.println(plan.getInstructionList());
	assertTrue(list.size() == plan.getInstructionList().size());
	
	Plan subPlan = new Plan();
	Instruction subrun1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
	subPlan.addInstruction(subrun1);
	plan.setSubPlans(new ArrayList<NestedPlan>());
	plan.getSubPlans().add((NestedPlan) subPlan);
	
	System.out.println("X = "+plan.getInstructionList().size());
	System.out.println("Z = "+list.size());
	System.out.println(plan.getInstructionList());
	System.out.println(list);

	
	System.out.println("X = "+plan.getInstructionList().size());
	System.out.println("Z = "+list.size());
	//System.out.println(subPlan.getInstructionList().size());
	System.out.println(list.size() - plan.getInstructionList().size() );
	assertTrue(list.size() - plan.getInstructionList().size() == 0);
	

	System.out.println("------------------------");

    }
    @Test
    public void isAtomic() {
	System.out.println("isAtomic");

	Plan plan = new NestedPlan();
	Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
	plan.addInstruction(run1);
	
	assertTrue(plan.isAtomic() == true);

	plan.setSubPlans(new ArrayList<NestedPlan>());
	assertTrue(plan.isAtomic() == true);
	
	plan.getSubPlans().add(new NestedPlan());
	assertTrue(plan.isAtomic() == false);

	System.out.println("------------------------");

    }


}
