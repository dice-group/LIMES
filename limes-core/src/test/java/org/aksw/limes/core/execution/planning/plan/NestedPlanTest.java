package org.aksw.limes.core.execution.planning.plan;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NestedPlanTest {

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
        System.out.println("addInstruction");

        Plan plan = new NestedPlan();
        plan.setInstructionList(new ArrayList<Instruction>());
        Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        System.out.println("Size before: " + plan.size());
        System.out.println(plan.getInstructionList());
        plan.addInstruction(run1);
        plan.addInstruction(null);
        System.out.println(plan.getInstructionList());
        assertTrue(plan.size() == 1);
        System.out.println("Size after: " + plan.size());

        System.out.println("------------------------");

    }

    @Test
    public void removeInstruction() {
        System.out.println("removeInstruction");

        NestedPlan plan = new NestedPlan();
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
        AMapping mUnion = ee.executeStatic(plan);
        System.out.println("Size of Mapping with Union: " + mUnion.size());

        plan.removeInstruction(union);
        plan.addInstruction(instersection);

        System.out.println("Plan size with Intersection: " + plan.size());
        AMapping mIntersection = ee.executeStatic(plan);
        System.out.println("Size of Mapping with Intersection: " + mIntersection.size());

        assertTrue(!mUnion.toString().equals(mIntersection.toString()));
        assertTrue(plan.getInstructionList().contains(union) == false);

        System.out.println("------------------------");

    }

    @Test
    public void removeNonExistingInstruction() {
        System.out.println("removeNonExistingInstruction");

        NestedPlan plan = new NestedPlan();
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

        NestedPlan plan = new NestedPlan();
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
        System.out.println(run1);
        System.out.println(plan.getInstructionList().isEmpty());
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

        NestedPlan plan = new NestedPlan();
        Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        plan.addInstruction(run1);
        plan.addInstruction(run2);
        List<Instruction> list = plan.getInstructionList();
        System.out.println("Size before: " + plan.size());
        System.out.println("Size of Instruction list " + list.size());

        assertTrue(list.size() == plan.size());

        Instruction run3 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        list.add(run3);
        assertTrue(list.size() != plan.getInstructionList().size());
        //always set instruction list 
        plan.setInstructionList(list);
        assertTrue(list.size() == plan.getInstructionList().size());

        NestedPlan subPlan = new NestedPlan();
        Instruction subrun1 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan.addInstruction(subrun1);
        plan.setSubPlans(new ArrayList<NestedPlan>());
        plan.getSubPlans().add(subPlan);


        NestedPlan subsubPlan = new NestedPlan();
        Instruction subsubrun1 = new Instruction(Command.RUN, "overlap(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan.addInstruction(subsubrun1);
        subPlan.setSubPlans(new ArrayList<NestedPlan>());
        subPlan.getSubPlans().add(subsubPlan);

        System.out.println("X = " + plan.getInstructionList().size());
        System.out.println(plan.getInstructionList());

        assertTrue(plan.getInstructionList().size() == 5);

        System.out.println("------------------------");

    }

    @Test
    public void isAtomic() {
        System.out.println("isAtomic");

        NestedPlan plan = new NestedPlan();
        Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        plan.addInstruction(run1);

        assertTrue(plan.isAtomic() == true);

        plan.setSubPlans(new ArrayList<NestedPlan>());
        assertTrue(plan.isAtomic() == true);

        plan.getSubPlans().add(new NestedPlan());
        assertTrue(plan.isAtomic() == false);

        System.out.println("------------------------");

    }

    @Test
    public void size() {
        System.out.println("size");

        NestedPlan plan = new NestedPlan();
        Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run3 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);

        plan.addInstruction(run1);
        plan.addInstruction(run2);

        System.out.println("Size before: " + plan.size());
        List<Instruction> list = plan.getInstructionList();
        list.add(run3);
        plan.setInstructionList(list);
        System.out.println("Size after: " + plan.size());

        NestedPlan subPlan = new NestedPlan();
        Instruction subrun1 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan.addInstruction(subrun1);
        plan.setSubPlans(new ArrayList<NestedPlan>());
        plan.getSubPlans().add(subPlan);


        NestedPlan subsubPlan = new NestedPlan();
        Instruction subsubrun1 = new Instruction(Command.RUN, "overlap(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan.addInstruction(subsubrun1);
        subPlan.setSubPlans(new ArrayList<NestedPlan>());
        subPlan.getSubPlans().add(subsubPlan);

        assertTrue(plan.size() == 5);
        assertTrue(plan.getInstructionList().size() == 5);

        System.out.println("------------------------");

    }

    @Test
    public void addSubPlan() {
        System.out.println("addSubPlan");

        NestedPlan plan = new NestedPlan();
        Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run3 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);

        plan.addInstruction(run1);
        plan.addInstruction(run2);

        List<Instruction> list = plan.getInstructionList();
        list.add(run3);
        plan.setInstructionList(list);

        NestedPlan subPlan = new NestedPlan();
        Instruction subrun1 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan.addInstruction(subrun1);
        plan.addSubplan(subPlan);
        plan.addSubplan(null);


        NestedPlan subsubPlan = new NestedPlan();
        Instruction subsubrun1 = new Instruction(Command.RUN, "overlap(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan.addInstruction(subsubrun1);
        subPlan.addSubplan(subsubPlan);
        subPlan.addSubplan(null);

        assertTrue(plan.size() == 5);
        assertTrue(plan.getInstructionList().size() == 5);

        System.out.println("------------------------");


    }

    @Test
    public void getAllMeasures() {
        System.out.println("getAllMeasures");

        NestedPlan plan = new NestedPlan();
        Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run3 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);

        plan.addInstruction(run1);
        plan.addInstruction(run2);

        List<Instruction> list = plan.getInstructionList();
        list.add(run3);
        plan.setInstructionList(list);

        NestedPlan subPlan = new NestedPlan();
        Instruction subrun1 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan.addInstruction(subrun1);
        plan.addSubplan(subPlan);
        plan.addSubplan(null);


        NestedPlan subsubPlan = new NestedPlan();
        Instruction subsubrun1 = new Instruction(Command.RUN, "overlap(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan.addInstruction(subsubrun1);
        subPlan.addSubplan(subsubPlan);
        subPlan.addSubplan(null);

        assertTrue(plan.size() == 5);
        assertTrue(plan.getInstructionList().size() == 5);

        List<String> allM = plan.getAllMeasures();
        assertTrue(allM != null);
        assertTrue(allM.isEmpty() == false);
        System.out.println(allM);


        ///////////////////////////////////////////////
        NestedPlan plan2 = new NestedPlan();
        List<String> allM2 = plan2.getAllMeasures();
        assertTrue(allM2.isEmpty() == true);
        System.out.println(allM2);
        //////////////////////////////////////////////

        System.out.println("------------------------");


    }

    @Test
    public void String() {
        System.out.println("String");

        NestedPlan plan = new NestedPlan();
        Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run3 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);

        plan.addInstruction(run1);
        plan.addInstruction(run2);

        List<Instruction> list = plan.getInstructionList();
        list.add(run3);
        plan.setInstructionList(list);

        NestedPlan subPlan = new NestedPlan();
        Instruction subrun1 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan.addInstruction(subrun1);
        plan.addSubplan(subPlan);
        plan.addSubplan(null);


        NestedPlan subsubPlan = new NestedPlan();
        Instruction subsubrun1 = new Instruction(Command.RUN, "overlap(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan.addInstruction(subsubrun1);
        subPlan.addSubplan(subsubPlan);
        subPlan.addSubplan(null);

        assertTrue(plan.size() == 5);
        assertTrue(plan.getInstructionList().size() == 5);

        String allM = plan.toString();
        assertTrue(allM != null);
        assertTrue(allM.isEmpty() == false);
        System.out.println(allM);


        ///////////////////////////////////////////////
    /*NestedPlan plan2 = new NestedPlan();
	String allM2 = plan2.toString();
	assertTrue(allM2.equals("Empty plan"));
	System.out.println(allM2);*/
        //////////////////////////////////////////////


        System.out.println("------------------------");


    }

    @Test
    public void Clone() {
        System.out.println("Clone");
        NestedPlan plan1 = new NestedPlan();

        Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        plan1.addInstruction(run1);
        plan1.addInstruction(run2);

        Instruction filter = new Instruction(Command.FILTER, null, "0.3", -1, -1, 0);
        plan1.setFilteringInstruction(filter);

        plan1.setMappingSize(10);
        plan1.setRuntimeCost(1000d);
        plan1.setSelectivity(0.1d);

        NestedPlan subPlan1 = new NestedPlan();
        Instruction subrun1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan1.addInstruction(subrun1);
        plan1.setSubPlans(new ArrayList<NestedPlan>());
        plan1.getSubPlans().add(subPlan1);

        NestedPlan subPlan2 = new NestedPlan();
        Instruction subrun2 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan2.addInstruction(subrun2);
        plan1.getSubPlans().add(subPlan2);

        NestedPlan subPlan21 = new NestedPlan();
        Instruction subrun21 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan21.addInstruction(subrun21);
        subPlan1.setSubPlans(new ArrayList<NestedPlan>());
        subPlan1.getSubPlans().add(subPlan21);
        //////////////////////////////////////////////////////////////////////////////////////////
        NestedPlan clonePlan = plan1.clone();
        // check plan itself
        System.out.println("Plan hashCode: " + plan1.hashCode());
        System.out.println("PlanClone hashCode: " + clonePlan.hashCode());
        assertTrue(plan1.hashCode() != clonePlan.hashCode());
        System.out.println("\n");

        // check instructionList
        System.out.println("InstructionList hashCode: " + plan1.getInstructionList().hashCode());
        System.out.println("InstructionListClone hashCode: " + clonePlan.getInstructionList().hashCode());
        assertTrue(plan1.getInstructionList().hashCode() != clonePlan.getInstructionList().hashCode());
        for (int i = 0; i < plan1.getInstructionList().size(); i++) {

            Instruction inst = plan1.getInstructionList().get(i);
            Instruction instClone = clonePlan.getInstructionList().get(i);

            System.out.println(inst);
            System.out.println("----Instruction hashCode: " + inst.hashCode());
            System.out.println(instClone);
            System.out.println("----InstructionClone hashCode: " + instClone.hashCode());
            assertTrue(inst.hashCode() != instClone.hashCode());
        }
        System.out.println("\n");
        // check filtering instruction
        System.out.println("FilteringInstruction hashCode: " + plan1.getFilteringInstruction().hashCode());
        System.out.println("FilteringInstructionClone hashCode: " + clonePlan.getFilteringInstruction().hashCode());
        assertTrue(plan1.getFilteringInstruction().hashCode() != clonePlan.getFilteringInstruction().hashCode());
        System.out.println("\n");

        // check subplans list
        System.out.println("SubPlans hashCode: " + plan1.getSubPlans().hashCode());
        System.out.println("SubPlansClone hashCode: " + clonePlan.getSubPlans().hashCode());
        assertTrue(plan1.getSubPlans().hashCode() != clonePlan.getSubPlans().hashCode());
        for (int i = 0; i < plan1.getSubPlans().size(); i++) {

            NestedPlan p = plan1.getSubPlans().get(i);
            NestedPlan pClone = clonePlan.getSubPlans().get(i);

            System.out.println("----SubPlan hashCode: " + p.hashCode());
            System.out.println("----SubPlanClone hashCode: " + pClone.hashCode());
            assertTrue(p.hashCode() != pClone.hashCode());
        }

    }

    @Test
    public void Equal() {
        System.out.println("Equal");
        NestedPlan plan1 = new NestedPlan();

        Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        plan1.addInstruction(run1);
        plan1.addInstruction(run2);
        Instruction filter1 = new Instruction(Command.FILTER, null, "0.3", -1, -1, 0);
        plan1.setFilteringInstruction(filter1);

        NestedPlan subPlan1 = new NestedPlan();
        Instruction subrun1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan1.addInstruction(subrun1);
        plan1.setSubPlans(new ArrayList<NestedPlan>());
        plan1.getSubPlans().add(subPlan1);

        NestedPlan subPlan2 = new NestedPlan();
        Instruction subrun2 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan2.addInstruction(subrun2);
        plan1.getSubPlans().add(subPlan2);

        NestedPlan subsubPlan1 = new NestedPlan();
        Instruction subsubrun1 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan1.addInstruction(subsubrun1);
        subPlan1.setSubPlans(new ArrayList<NestedPlan>());
        subPlan1.getSubPlans().add(subsubPlan1);
        //////////////////////////////////////////////////////////////////////////////////////////
        NestedPlan plan2 = new NestedPlan();
        Instruction run3 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run4 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        plan2.addInstruction(run3);
        plan2.addInstruction(run4);
        Instruction filter2 = new Instruction(Command.FILTER, null, "0.3", -1, -1, 0);
        plan2.setFilteringInstruction(filter2);

        NestedPlan subPlan3 = new NestedPlan();
        Instruction subrun3 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan3.addInstruction(subrun3);
        plan2.setSubPlans(new ArrayList<NestedPlan>());
        plan2.getSubPlans().add(subPlan3);

        NestedPlan subPlan4 = new NestedPlan();
        Instruction subrun4 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan4.addInstruction(subrun4);
        plan2.getSubPlans().add(subPlan4);

        NestedPlan subsubPlan2 = new NestedPlan();
        Instruction subsubrun2 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan2.addInstruction(subsubrun2);
        subPlan3.setSubPlans(new ArrayList<NestedPlan>());
        subPlan3.getSubPlans().add(subsubPlan2);


        ///////////////////////////////////////////////////////////////////////////////////////////
        assertTrue(plan1.equals(plan2));

        NestedPlan plan3 = null;
        assertTrue(!plan1.equals(plan3));
        ///////////////////////////////////////////////////////////////////////////////////////////
        System.out.println("------------------------");


    }

    @Test
    public void EqualExcludeInstruction() {
        System.out.println("EqualExcludeInstruction");
        NestedPlan plan1 = new NestedPlan();

        Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        plan1.addInstruction(run1);
        plan1.addInstruction(run2);
        Instruction filter1 = new Instruction(Command.FILTER, null, "0.3", -1, -1, 0);
        plan1.setFilteringInstruction(filter1);

        NestedPlan subPlan1 = new NestedPlan();
        Instruction subrun1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan1.addInstruction(subrun1);
        plan1.setSubPlans(new ArrayList<NestedPlan>());
        plan1.getSubPlans().add(subPlan1);

        NestedPlan subPlan2 = new NestedPlan();
        Instruction subrun2 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan2.addInstruction(subrun2);
        plan1.getSubPlans().add(subPlan2);

        NestedPlan subsubPlan1 = new NestedPlan();
        Instruction subsubrun1 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan1.addInstruction(subsubrun1);
        subPlan1.setSubPlans(new ArrayList<NestedPlan>());
        subPlan1.getSubPlans().add(subsubPlan1);
        //////////////////////////////////////////////////////////////////////////////////////////
        NestedPlan plan2 = new NestedPlan();
        Instruction filter2 = new Instruction(Command.FILTER, null, "0.3", -1, -1, 0);
        plan2.setFilteringInstruction(filter2);

        NestedPlan subPlan3 = new NestedPlan();
        Instruction subrun3 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan3.addInstruction(subrun3);
        plan2.setSubPlans(new ArrayList<NestedPlan>());
        plan2.getSubPlans().add(subPlan3);

        NestedPlan subPlan4 = new NestedPlan();
        Instruction subrun4 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan4.addInstruction(subrun4);
        plan2.getSubPlans().add(subPlan4);

        NestedPlan subsubPlan2 = new NestedPlan();
        Instruction subsubrun2 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan2.addInstruction(subsubrun2);
        subPlan3.setSubPlans(new ArrayList<NestedPlan>());
        subPlan3.getSubPlans().add(subsubPlan2);


        ///////////////////////////////////////////////////////////////////////////////////////////
        assertTrue(!plan1.equals(plan2));
        System.out.println("------------------------");


    }

    @Test
    public void EqualExcludeSubPlan() {
        System.out.println("EqualExcludeSubPlan");
        NestedPlan plan1 = new NestedPlan();

        Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        plan1.addInstruction(run1);
        plan1.addInstruction(run2);
        Instruction filter1 = new Instruction(Command.FILTER, null, "0.3", -1, -1, 0);
        plan1.setFilteringInstruction(filter1);

        NestedPlan subPlan1 = new NestedPlan();
        Instruction subrun1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan1.addInstruction(subrun1);
        plan1.setSubPlans(new ArrayList<NestedPlan>());
        plan1.getSubPlans().add(subPlan1);

        NestedPlan subPlan2 = new NestedPlan();
        Instruction subrun2 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan2.addInstruction(subrun2);
        plan1.getSubPlans().add(subPlan2);

        NestedPlan subsubPlan1 = new NestedPlan();
        Instruction subsubrun1 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan1.addInstruction(subsubrun1);
        subPlan1.setSubPlans(new ArrayList<NestedPlan>());
        subPlan1.getSubPlans().add(subsubPlan1);
        //////////////////////////////////////////////////////////////////////////////////////////
        NestedPlan plan2 = new NestedPlan();
        Instruction run3 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run4 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);

        plan2.addInstruction(run3);
        plan2.addInstruction(run4);
        Instruction filter2 = new Instruction(Command.FILTER, null, "0.3", -1, -1, 0);
        plan2.setFilteringInstruction(filter2);

        NestedPlan subPlan3 = new NestedPlan();
        Instruction subrun3 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan3.addInstruction(subrun3);
        plan2.setSubPlans(new ArrayList<NestedPlan>());
        plan2.getSubPlans().add(subPlan3);

        NestedPlan subsubPlan2 = new NestedPlan();
        Instruction subsubrun2 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan2.addInstruction(subsubrun2);
        subPlan3.setSubPlans(new ArrayList<NestedPlan>());
        subPlan3.getSubPlans().add(subsubPlan2);


        ///////////////////////////////////////////////////////////////////////////////////////////
        assertTrue(!plan1.equals(plan2));
        System.out.println("------------------------");


    }

    @Test
    public void EqualExcludeFilter() {
        System.out.println("EqualExcludeFilter");
        NestedPlan plan1 = new NestedPlan();

        Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        plan1.addInstruction(run1);
        plan1.addInstruction(run2);
        Instruction filter1 = new Instruction(Command.FILTER, null, "0.3", -1, -1, 0);
        plan1.setFilteringInstruction(filter1);

        NestedPlan subPlan1 = new NestedPlan();
        Instruction subrun1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan1.addInstruction(subrun1);
        plan1.setSubPlans(new ArrayList<NestedPlan>());
        plan1.getSubPlans().add(subPlan1);

        NestedPlan subPlan2 = new NestedPlan();
        Instruction subrun2 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan2.addInstruction(subrun2);
        plan1.getSubPlans().add(subPlan2);

        NestedPlan subsubPlan1 = new NestedPlan();
        Instruction subsubrun1 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan1.addInstruction(subsubrun1);
        subPlan1.setSubPlans(new ArrayList<NestedPlan>());
        subPlan1.getSubPlans().add(subsubPlan1);
        //////////////////////////////////////////////////////////////////////////////////////////
        NestedPlan plan2 = new NestedPlan();
        Instruction run3 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run4 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        plan2.addInstruction(run3);
        plan2.addInstruction(run4);

        NestedPlan subPlan3 = new NestedPlan();
        Instruction subrun3 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan3.addInstruction(subrun3);
        plan2.setSubPlans(new ArrayList<NestedPlan>());
        plan2.getSubPlans().add(subPlan3);

        NestedPlan subPlan4 = new NestedPlan();
        Instruction subrun4 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan4.addInstruction(subrun4);
        plan2.getSubPlans().add(subPlan4);

        NestedPlan subsubPlan2 = new NestedPlan();
        Instruction subsubrun2 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan2.addInstruction(subsubrun2);
        subPlan3.setSubPlans(new ArrayList<NestedPlan>());
        subPlan3.getSubPlans().add(subsubPlan2);


        ///////////////////////////////////////////////////////////////////////////////////////////
        assertTrue(!plan1.equals(plan2));


        System.out.println("------------------------");


    }

    @Test
    public void EqualExcludeSubSubPlan() {
        System.out.println("EqualSubSubPlan");
        NestedPlan plan1 = new NestedPlan();

        Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        plan1.addInstruction(run1);
        plan1.addInstruction(run2);
        Instruction filter1 = new Instruction(Command.FILTER, null, "0.3", -1, -1, 0);
        plan1.setFilteringInstruction(filter1);

        NestedPlan subPlan1 = new NestedPlan();
        Instruction subrun1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan1.addInstruction(subrun1);
        plan1.setSubPlans(new ArrayList<NestedPlan>());
        plan1.getSubPlans().add(subPlan1);

        NestedPlan subPlan2 = new NestedPlan();
        Instruction subrun2 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan2.addInstruction(subrun2);
        plan1.getSubPlans().add(subPlan2);

        NestedPlan subsubPlan1 = new NestedPlan();
        Instruction subsubrun1 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan1.addInstruction(subsubrun1);
        subPlan1.setSubPlans(new ArrayList<NestedPlan>());
        subPlan1.getSubPlans().add(subsubPlan1);
        //////////////////////////////////////////////////////////////////////////////////////////
        NestedPlan plan2 = new NestedPlan();
        Instruction run3 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run4 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        plan2.addInstruction(run3);
        plan2.addInstruction(run4);
        Instruction filter2 = new Instruction(Command.FILTER, null, "0.3", -1, -1, 0);
        plan2.setFilteringInstruction(filter2);

        NestedPlan subPlan3 = new NestedPlan();
        Instruction subrun3 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan3.addInstruction(subrun3);
        plan2.setSubPlans(new ArrayList<NestedPlan>());
        plan2.getSubPlans().add(subPlan3);

        NestedPlan subPlan4 = new NestedPlan();
        Instruction subrun4 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan4.addInstruction(subrun4);
        plan2.getSubPlans().add(subPlan4);


        ///////////////////////////////////////////////////////////////////////////////////////////
        assertTrue(!plan1.equals(plan2));


        System.out.println("------------------------");


    }

    @Test
    public void EqualExcludeOperator() {
        System.out.println("EqualExcludeOperator");
        NestedPlan plan1 = new NestedPlan();

        Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        plan1.addInstruction(run1);
        plan1.addInstruction(run2);
        Instruction filter1 = new Instruction(Command.FILTER, null, "0.3", -1, -1, 0);
        plan1.setFilteringInstruction(filter1);
        plan1.setOperator(Command.INTERSECTION);

        NestedPlan subPlan1 = new NestedPlan();
        Instruction subrun1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan1.addInstruction(subrun1);
        plan1.setSubPlans(new ArrayList<NestedPlan>());
        plan1.getSubPlans().add(subPlan1);

        NestedPlan subPlan2 = new NestedPlan();
        Instruction subrun2 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan2.addInstruction(subrun2);
        plan1.getSubPlans().add(subPlan2);

        NestedPlan subsubPlan1 = new NestedPlan();
        Instruction subsubrun1 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan1.addInstruction(subsubrun1);
        subPlan1.setSubPlans(new ArrayList<NestedPlan>());
        subPlan1.getSubPlans().add(subsubPlan1);
        //////////////////////////////////////////////////////////////////////////////////////////
        NestedPlan plan2 = new NestedPlan();
        Instruction run3 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run4 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        plan2.addInstruction(run3);
        plan2.addInstruction(run4);
        Instruction filter2 = new Instruction(Command.FILTER, null, "0.3", -1, -1, 0);
        plan2.setFilteringInstruction(filter2);

        NestedPlan subPlan3 = new NestedPlan();
        Instruction subrun3 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan3.addInstruction(subrun3);
        plan2.setSubPlans(new ArrayList<NestedPlan>());
        plan2.getSubPlans().add(subPlan3);

        NestedPlan subPlan4 = new NestedPlan();
        Instruction subrun4 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan4.addInstruction(subrun4);
        plan2.getSubPlans().add(subPlan4);

        NestedPlan subsubPlan2 = new NestedPlan();
        Instruction subsubrun2 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan2.addInstruction(subsubrun2);
        subPlan3.setSubPlans(new ArrayList<NestedPlan>());
        subPlan3.getSubPlans().add(subsubPlan2);


        ///////////////////////////////////////////////////////////////////////////////////////////
        assertTrue(!plan1.equals(plan2));

        System.out.println("------------------------");


    }

    @Test
    public void EqualDifferentOperatorInSubPlan() {
        System.out.println("EqualDifferentOperatorInSubPlan");
        NestedPlan plan1 = new NestedPlan();

        Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        plan1.addInstruction(run1);
        plan1.addInstruction(run2);
        Instruction filter1 = new Instruction(Command.FILTER, null, "0.3", -1, -1, 0);
        plan1.setFilteringInstruction(filter1);

        NestedPlan subPlan1 = new NestedPlan();
        Instruction subrun1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan1.addInstruction(subrun1);
        plan1.setSubPlans(new ArrayList<NestedPlan>());
        plan1.getSubPlans().add(subPlan1);

        NestedPlan subPlan2 = new NestedPlan();
        Instruction subrun2 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan2.addInstruction(subrun2);
        subPlan2.setOperator(Command.INTERSECTION);
        plan1.getSubPlans().add(subPlan2);


        NestedPlan subsubPlan1 = new NestedPlan();
        Instruction subsubrun1 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan1.addInstruction(subsubrun1);
        subPlan2.setSubPlans(new ArrayList<NestedPlan>());
        subPlan2.getSubPlans().add(subsubPlan1);

        NestedPlan subsubPlan2 = new NestedPlan();
        Instruction subsubrun2 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan2.addInstruction(subsubrun2);
        subPlan2.getSubPlans().add(subsubPlan2);
        //////////////////////////////////////////////////////////////////////////////////////////
        NestedPlan plan2 = new NestedPlan();
        Instruction run3 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run4 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        plan2.addInstruction(run3);
        plan2.addInstruction(run4);
        Instruction filter2 = new Instruction(Command.FILTER, null, "0.3", -1, -1, 0);
        plan2.setFilteringInstruction(filter2);

        NestedPlan subPlan3 = new NestedPlan();
        Instruction subrun3 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan3.addInstruction(subrun3);
        plan2.setSubPlans(new ArrayList<NestedPlan>());
        plan2.getSubPlans().add(subPlan3);

        NestedPlan subPlan4 = new NestedPlan();
        Instruction subrun4 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan4.addInstruction(subrun4);
        subPlan4.setOperator(Command.DIFF);
        plan2.getSubPlans().add(subPlan4);

        NestedPlan subsubPlan3 = new NestedPlan();
        Instruction subsubrun3 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan3.addInstruction(subsubrun3);
        subPlan4.setSubPlans(new ArrayList<NestedPlan>());
        subPlan4.getSubPlans().add(subsubPlan3);

        NestedPlan subsubPlan4 = new NestedPlan();
        Instruction subsubrun4 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan4.addInstruction(subsubrun4);
        subPlan4.getSubPlans().add(subsubPlan4);


        ///////////////////////////////////////////////////////////////////////////////////////////
        assertTrue(!plan1.equals(plan2));

        System.out.println("------------------------");


    }

    @Test
    public void EqualDifferentSubPlans() {
        System.out.println("EqualDifferentSubPlans");
        NestedPlan plan1 = new NestedPlan();

        Instruction run1 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run2 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        plan1.addInstruction(run1);
        plan1.addInstruction(run2);
        Instruction filter1 = new Instruction(Command.FILTER, null, "0.3", -1, -1, 0);
        plan1.setFilteringInstruction(filter1);

        NestedPlan subPlan1 = new NestedPlan();
        Instruction subrun1 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan1.addInstruction(subrun1);
        plan1.setSubPlans(new ArrayList<NestedPlan>());
        plan1.getSubPlans().add(subPlan1);

        NestedPlan subPlan2 = new NestedPlan();
        Instruction subrun2 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan2.addInstruction(subrun2);
        subPlan2.setOperator(Command.INTERSECTION);
        plan1.getSubPlans().add(subPlan2);


        NestedPlan subsubPlan1 = new NestedPlan();
        Instruction subsubrun1 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan1.addInstruction(subsubrun1);
        subPlan2.setSubPlans(new ArrayList<NestedPlan>());
        subPlan2.getSubPlans().add(subsubPlan1);

        NestedPlan subsubPlan2 = new NestedPlan();
        Instruction subsubrun2 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan2.addInstruction(subsubrun2);
        subPlan2.getSubPlans().add(subsubPlan2);

        //////////////////////////////////////////////////////////////////////////////////////////
        NestedPlan plan2 = new NestedPlan();
        Instruction run3 = new Instruction(Command.RUN, "jaccard(x.surname, y.surname)", "0.3", -1, -1, 0);
        Instruction run4 = new Instruction(Command.RUN, "cosine(x.surname, y.surname)", "0.3", -1, -1, 0);
        plan2.addInstruction(run3);
        plan2.addInstruction(run4);
        Instruction filter2 = new Instruction(Command.FILTER, null, "0.3", -1, -1, 0);
        plan2.setFilteringInstruction(filter2);

        NestedPlan subPlan3 = new NestedPlan();
        Instruction subrun3 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan3.addInstruction(subrun3);
        plan2.setSubPlans(new ArrayList<NestedPlan>());
        plan2.getSubPlans().add(subPlan3);

        NestedPlan subPlan4 = new NestedPlan();
        Instruction subrun4 = new Instruction(Command.RUN, "leven(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan4.addInstruction(subrun4);
        subPlan4.setOperator(Command.INTERSECTION);
        plan2.getSubPlans().add(subPlan4);


        NestedPlan subsubPlan4 = new NestedPlan();
        Instruction subsubrun4 = new Instruction(Command.RUN, "trigrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subPlan4.setSubPlans(new ArrayList<NestedPlan>());
        subsubPlan4.addInstruction(subsubrun4);
        subPlan4.getSubPlans().add(subsubPlan4);

        NestedPlan subsubPlan3 = new NestedPlan();
        Instruction subsubrun3 = new Instruction(Command.RUN, "qgrams(x.surname, y.surname)", "0.3", -1, -1, 0);
        subsubPlan3.addInstruction(subsubrun3);
        subPlan4.getSubPlans().add(subsubPlan3);

        ///////////////////////////////////////////////////////////////////////////////////////////
        assertTrue(!plan1.equals(plan2));

        System.out.println("------------------------");


    }
}
