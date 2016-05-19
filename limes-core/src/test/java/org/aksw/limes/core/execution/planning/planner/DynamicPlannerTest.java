package org.aksw.limes.core.execution.planning.planner;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DynamicPlannerTest {

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
    public void EmptyPlan() {
	System.out.println("EmptyPlan");

	Mapping m = new MemoryMapping();
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
	Mapping m = new MemoryMapping();

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

	ExecutionEngine e = new SimpleExecutionEngine(source, target, "?x", "?y");
	DynamicPlanner p = new DynamicPlanner(source, target);
	Mapping m = e.execute(ls, p);

	LinkSpecification ls2 = p.normalize(ls);
	assertTrue(p.getPlans().get(ls2.toString()) != null);

    }

    @Test
    public void AtomicEqual() {
	System.out.println("AtomicEqual");

	DynamicPlanner p = new DynamicPlanner(source, target);

	LinkSpecification ls = new LinkSpecification("cosine(x.name,y.name)", 0.8);
	System.out.println(ls.isAtomic());

	ExecutionEngine e = new SimpleExecutionEngine(source, target, "?x", "?y");
	Mapping m = e.execute(ls, p);

	NestedPlan plan2 = new NestedPlan();
	Instruction run1 = new Instruction(Command.RUN, "cosine(x.name,y.name)", "0.8", -1, -1, 0);
	plan2.addInstruction(run1);

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

    @Test
    public void runtimeApproximation() {
	System.out.println("runtimeApproximation");
	DynamicPlanner p = new DynamicPlanner(source, target);

	assertTrue(p.getAtomicRuntimeCosts("jaro", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("qgrams", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("cosine", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("levenshtein", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("overlap", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("trigram", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("jaccard", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("exactmatch", 0.5) != 0.0d);
	assertTrue(p.getAtomicRuntimeCosts("soundex", 0.5) != 0);

	assertTrue(p.getAtomicRuntimeCosts("euclidean", 0.5) != 0);

	assertTrue(p.getAtomicRuntimeCosts("geo_orthodromic", 0.5) != 0);
	// assertTrue(p.getAtomicRuntimeCosts("geo_elliptic", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("geo_hausdorff", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("geo_fairsurjection", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("geo_max", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("geo_mean", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("geo_min", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("geo_avg", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("geo_frechet", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("geo_link", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("geo_sum_of_min", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("geo_surjection", 0.5) != 0);
	// assertTrue(p.getAtomicRuntimeCosts("geo_quinlan", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("geo_symmetrichausdorff", 0.5) != 0);

	assertTrue(p.getAtomicRuntimeCosts("tmp_successor", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("tmp_predecessor", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("tmp_concurrent", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("tmp_before", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("tmp_after", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("tmp_meets", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("tmp_ismetby", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("tmp_finishes", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("tmp_isfinishedby", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("tmp_starts", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("tmp_isstartedby", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("tmp_during", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("tmp_duringreverse", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("tmp_overlaps", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("tmp_isoverlappedby", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("tmp_equals", 0.5) != 0);

    }

    @Test
    public void mappingApproximation() {
	System.out.println("mappingApproximation");
	DynamicPlanner p = new DynamicPlanner(source, target);
	System.out.println(source.size());
	System.out.println(target.size());

	assertTrue(p.getAtomicMappingSizes("jaro", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("qgrams", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("cosine", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("levenshtein", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("overlap", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("trigram", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("jaccard", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("exactmatch", 0.5) != 0.0d);
	assertTrue(p.getAtomicMappingSizes("soundex", 0.5) != 0);

	assertTrue(p.getAtomicMappingSizes("euclidean", 0.5) != 0);

	assertTrue(p.getAtomicMappingSizes("geo_orthodromic", 0.5) != 0);
	// assertTrue(p.getAtomicMappingSizes("geo_elliptic", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("geo_hausdorff", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("geo_fairsurjection", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("geo_max", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("geo_mean", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("geo_min", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("geo_avg", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("geo_frechet", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("geo_link", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("geo_sum_of_min", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("geo_surjection", 0.5) != 0);
	// assertTrue(p.getAtomicMappingSizes("geo_quinlan", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("geo_symmetrichausdorff", 0.5) != 0);

	assertTrue(p.getAtomicMappingSizes("tmp_successor", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("tmp_predecessor", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("tmp_concurrent", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("tmp_before", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("tmp_after", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("tmp_meets", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("tmp_ismetby", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("tmp_finishes", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("tmp_isfinishedby", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("tmp_starts", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("tmp_isstartedby", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("tmp_during", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("tmp_duringreverse", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("tmp_overlaps", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("tmp_isoverlappedby", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("tmp_equals", 0.5) != 0);

    }

}
