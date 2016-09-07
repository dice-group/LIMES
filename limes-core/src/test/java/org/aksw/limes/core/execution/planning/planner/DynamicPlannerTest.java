package org.aksw.limes.core.execution.planning.planner;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DynamicPlannerTest {

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

        AMapping m = MappingFactory.createDefaultMapping();
        ExecutionEngine e = new SimpleExecutionEngine(source, target, "?x", "?y");
        DynamicPlanner p = new DynamicPlanner(source, target);
        LinkSpecification ls = new LinkSpecification();

        try {
            m = e.execute(ls, p);
        } catch (Exception e1) {
            System.err.println("Empty input ls.");
        }

        assertTrue(m.size() == 0);
    }

    @Test
    public void AtomicPlan() {
        System.out.println("AtomicPlan");
        AMapping m = MappingFactory.createDefaultMapping();

        ExecutionEngine e = new SimpleExecutionEngine(source, target, "?x", "?y");
        DynamicPlanner p = new DynamicPlanner(source, target);
        LinkSpecification ls = new LinkSpecification("jaccard(x.surname, y.surname)", 0.8);
        try {
            m = e.execute(ls, p);
        } catch (Exception e1) {
            System.err.println("Empty input ls.");
        }
        assertTrue(m.size() != 0);

        // atomic plans have instructions lists
        NestedPlan plan = p.getPlans().get(ls.toString());
        assertTrue(plan != null);
        assertTrue(plan.isAtomic() == true);

    }


    @Test
    public void ComplexPlanExtendedLS() {
        System.out.println("ComplexPlanExtendedLS");

        LinkSpecification ls = new LinkSpecification(
                "OR(jaccard(x.surname,y.name)|0.5941,OR(XOR(OR(XOR(trigrams(x.name,y.name)|0.7728,qgrams(x.surname,y.name)|0.6029)|0.7728,XOR(trigrams(x.name,y.name)|0.7728,qgrams(x.surname,y.name)|0.6029)|0.7728)|0.5807,OR(XOR(trigrams(x.name,y.name)|0.7728,qgrams(x.surname,y.name)|0.6029)|0.7728,trigrams(x.surname,y.name)|0.5919)|0.5807)|0.7728,trigrams(x.name,y.name)|0.7728)|0.5807)",
                0.8);

        DynamicPlanner p = new DynamicPlanner(source, target);

        LinkSpecification ls2 = p.normalize(ls);
        assertTrue(p.getPlans().get(ls2.toString()) != null);

    }

    @Test
    public void AtomicEqual() {
        System.out.println("AtomicEqual");

        DynamicPlanner p = new DynamicPlanner(source, target);
        
        LinkSpecification ls = new LinkSpecification("cosine(x.name,y.name)", 0.8);
        System.out.println(ls.isAtomic());
        ls = p.normalize(ls);
        ExecutionEngine ee = new SimpleExecutionEngine(source, source, "?x", "?y");
        ee.execute(ls, p);
        
        NestedPlan plan2 = new NestedPlan();
        Instruction run1 = new Instruction(Command.RUN, "cosine(x.name,y.name)", "0.8", -1, -1, 0);
        plan2.addInstruction(run1);

        System.out.println(p.getPlans());
        assertTrue(p.getPlans().get(ls.toString()).equals(plan2));
    }


    @Test
    public void filterCosts() {
        System.out.println("filterCosts");
        DynamicPlanner p = new DynamicPlanner(source, target);

        assertTrue(p.getFilterCosts(null, 500) == 0);
        List<String> t = new ArrayList<String>();
        t.add("cosine");
        assertTrue(p.getFilterCosts(t, 500) != 0);

        t = new ArrayList<String>();
        assertTrue(p.getFilterCosts(t, 500) == 0);

        // t.add("blabla");
        // assertTrue(p.getFilterCosts(t, 500) != 0);

        t = new ArrayList<String>();
        t.add("cosine");
        assertTrue(p.getFilterCosts(t, 0) == 0);

    }


}
