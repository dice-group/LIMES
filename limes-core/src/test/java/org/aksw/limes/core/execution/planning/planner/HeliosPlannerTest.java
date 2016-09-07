package org.aksw.limes.core.execution.planning.planner;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.ls.ExtendedLinkSpecification;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HeliosPlannerTest {
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
    public void EmptyPlan() {
        System.out.println("EmptyPlan");

        HeliosPlanner p = new HeliosPlanner(source, target);
        LinkSpecification ls = new LinkSpecification();
        NestedPlan plan = p.plan(ls);
        assertTrue(plan.isEmpty() == true);
    }

    @Test
    public void AtomicPlan() {
        System.out.println("AtomicPlan");

        HeliosPlanner p = new HeliosPlanner(source, target);
        LinkSpecification ls = new LinkSpecification("jaccard(x.surname, y.surname)", 0.8);
        NestedPlan plan = p.plan(ls);
        assertTrue(plan.isEmpty() == false);
        assertTrue(plan.isAtomic() == true);

        // atomic plans have instructions lists
        assertTrue(plan.getInstructionList().isEmpty() == false);
        // atomic plans don't have subplans
        assertTrue(plan.getSubPlans() == null);
        // atomic plans don't have filteringinstructions
        assertTrue(plan.getFilteringInstruction() == null);

    }

    @Test
    public void ComplexPlanLS() {
        System.out.println("ComplexPlanLS");

        HeliosPlanner p = new HeliosPlanner(source, target);
        LinkSpecification ls = new LinkSpecification(
                "AND(jaccard(x.title,y.name)|0.5941,OR(XOR(OR(XOR(trigrams(x.description,y.description)|0.7728,qgrams(x.title,y.name)|0.6029)|0.7728,XOR(trigrams(x.description,y.description)|0.7728,qgrams(x.title,y.name)|0.6029)|0.7728)|0.5807,OR(XOR(trigrams(x.description,y.description)|0.7728,qgrams(x.title,y.name)|0.6029)|0.7728,trigrams(x.title,y.name)|0.5919)|0.5807)|0.7728,trigrams(x.description,y.description)|0.7728)|0.5807)",
                0.8);
        NestedPlan plan = p.plan(ls);
        assertTrue(plan.isEmpty() == false);
        assertTrue(plan.isAtomic() == false);

        // atomic plans have instructions lists = the instructions lists of
        // their subplans if any
        assertTrue(plan.getInstructionList().isEmpty() == false);
        assertTrue(plan.getInstructionList().size() == 8);

        // atomic plans have subplans
        assertTrue(plan.getSubPlans().isEmpty() == false);
        // atomic plans have filteringinstructions
        assertTrue(plan.getFilteringInstruction() != null);

        CanonicalPlanner cp = new CanonicalPlanner();
        NestedPlan plan2 = cp.plan(ls);

        assertTrue(plan.equals(plan2) == false);
    }

    @Test
    public void ComplexPlanExtendedLS() {
        System.out.println("ComplexPlanExtendedLS");

        HeliosPlanner p = new HeliosPlanner(source, target);
        ExtendedLinkSpecification ls = new ExtendedLinkSpecification(
                "OR(jaccard(x.title,y.name)|0.5941,OR(XOR(OR(XOR(trigrams(x.description,y.description)|0.7728,qgrams(x.title,y.name)|0.6029)|0.7728,XOR(trigrams(x.description,y.description)|0.7728,qgrams(x.title,y.name)|0.6029)|0.7728)|0.5807,OR(XOR(trigrams(x.description,y.description)|0.7728,qgrams(x.title,y.name)|0.6029)|0.7728,trigrams(x.title,y.name)|0.5919)|0.5807)|0.7728,trigrams(x.description,y.description)|0.7728)|0.5807)",
                0.8);
        NestedPlan plan = p.plan(ls);
        assertTrue(plan.isEmpty() == false);
        assertTrue(plan.isAtomic() == false);

        // atomic plans have instructions lists = the instructions lists of
        // their subplans if any
        assertTrue(plan.getInstructionList().isEmpty() == false);
        assertTrue(plan.getInstructionList().size() > 9);

        // atomic plans have subplans
        assertTrue(plan.getSubPlans().isEmpty() == false);
        // atomic plans have filteringinstructions
        assertTrue(plan.getFilteringInstruction() != null);

    }

    @Test
    public void AtomicEqual() {
        System.out.println("AtomicEqual");

        HeliosPlanner p = new HeliosPlanner(source, target);

        LinkSpecification ls = new LinkSpecification("cosine(x.label,y.label)", 0.8);
        System.out.println(ls.isAtomic());

        NestedPlan plan = p.plan(ls);

        NestedPlan plan2 = new NestedPlan();
        Instruction run1 = new Instruction(Command.RUN, "cosine(x.label,y.label)", "0.8", -1, -1, 0);
        plan2.addInstruction(run1);

        assertTrue(plan.equals(plan2));
    }

    @Test
    public void ComplexEqual() {
        System.out.println("ComplexEqual");

        HeliosPlanner p = new HeliosPlanner(source, target);

        LinkSpecification ls = new LinkSpecification(
                "OR(cosine(x.description,y.description)|0.3,OR(cosine(x.description,y.description)|0.5,cosine(x.title,y.name)|0.6)|0.7)",
                0.8);
        System.out.println(ls.isAtomic());

        NestedPlan plan = p.plan(ls);
        //////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////

        NestedPlan plan2 = new NestedPlan();
        Instruction run1 = new Instruction(Command.RUN, "cosine(x.description,y.description)", "0.3", -1, -1, 0);
        plan2.addInstruction(run1);
        //////////////////////////////////////////////////////////////////////////////////
        NestedPlan plan3 = new NestedPlan();

        NestedPlan plan31 = new NestedPlan();
        Instruction run2 = new Instruction(Command.RUN, "cosine(x.description,y.description)", "0.5", -1, -1, 0);
        plan31.addInstruction(run2);
        NestedPlan plan32 = new NestedPlan();
        Instruction run3 = new Instruction(Command.RUN, "cosine(x.title,y.name)", "0.6", -1, -1, 0);
        plan32.addInstruction(run3);

        plan3.setSubPlans(new ArrayList<NestedPlan>());
        plan3.addSubplan(plan31);
        plan3.addSubplan(plan32);

        plan3.setOperator(Command.UNION);
        plan3.setFilteringInstruction(new Instruction(Command.FILTER, null, "0.7", -1, -1, 0));

        /////////////////////////////////////////////////////////////////////////////////////
        NestedPlan planNew = new NestedPlan();
        planNew.setSubPlans(new ArrayList<NestedPlan>());
        planNew.addSubplan(plan2);
        planNew.addSubplan(plan3);
        planNew.setOperator(Command.UNION);
        planNew.setFilteringInstruction(new Instruction(Command.FILTER, null, "0.8", -1, -1, 0));

        assertTrue(plan.equals(planNew));
    }


    @Test
    public void filterCosts() {
        System.out.println("filterCosts");
        HeliosPlanner p = new HeliosPlanner(source, target);

        assertTrue(p.getFilterCosts(null, 500) == 0);
        List<String> t = new ArrayList<String>();
        t.add("cosine");
        assertTrue(p.getFilterCosts(t, 500) != 0);

        t = new ArrayList<String>();
        assertTrue(p.getFilterCosts(t, 500) == 0);

        //t.add("blabla");
        //assertTrue(p.getFilterCosts(t, 500) != 0);

        t = new ArrayList<String>();
        t.add("cosine");
        assertTrue(p.getFilterCosts(t, 0) == 0);

    }


}
