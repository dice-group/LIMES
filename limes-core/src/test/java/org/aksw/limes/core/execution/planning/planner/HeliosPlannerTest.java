package org.aksw.limes.core.execution.planning.planner;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HeliosPlannerTest {
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

    public void ComplexPlanExtendedLS() {
	System.out.println("ComplexPlanExtendedLS");

	HeliosPlanner p = new HeliosPlanner(source, target);
	LinkSpecification ls = new LinkSpecification(
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

	CanonicalPlanner cp = new CanonicalPlanner();
	NestedPlan plan2 = cp.plan(ls);

	assertTrue(plan.equals(plan2) == true);
    }

    @Test
    public void PlanWithWrongOperator() {
	System.out.println("PlanWithWrongOperator");

	HeliosPlanner p = new HeliosPlanner(source, target);

	LinkSpecification ls = new LinkSpecification(
		"blabla(jaccard(x.surname, y.surname)|0.5,cosine(x.label,y.label)|0.6)", 0.8);
	System.out.println(ls.isAtomic());

	NestedPlan plan = p.plan(ls);
	assertTrue(plan != null);

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
    public void ComplexEqualExtended() {
	System.out.println("ComplexEqualExtended");

	HeliosPlanner p = new HeliosPlanner(source, target);

	LinkSpecification ls = new LinkSpecification(
		"AND(cosine(x.description,y.description)|0.3,OR(cosine(x.description,y.description)|0.5,cosine(x.title,y.name)|0.6)|0.7)",
		0.8);
	System.out.println(ls.isAtomic());

	NestedPlan plan = p.plan(ls);
	//////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////

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
	Instruction filter = new Instruction(Command.FILTER, "cosine(x.description,y.description)", "0.3", -1, -1, 0);
	filter.setMainThreshold("0.8");
	planNew.setFilteringInstruction(filter);
	planNew.addSubplan(plan3);
	planNew.setOperator(null);

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

	t.add("blabla");
	assertTrue(p.getFilterCosts(t, 500) != 0);

	t = new ArrayList<String>();
	t.add("cosine");
	assertTrue(p.getFilterCosts(t, 0) == 0);
	
	t = new ArrayList<String>();
	t.add("jarowinkler");
	assertTrue(p.getFilterCosts(t, 500) == 0);
	

    }
    
    @Test
    public void runtimeApproximation() {
	System.out.println("runtimeApproximation");
	HeliosPlanner p = new HeliosPlanner(source, target);

	assertTrue(p.getAtomicRuntimeCosts(null, 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("cosine", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("jaccard", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("jaro", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("euclidean", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("levenshtein", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("qgrams", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("hausdorff", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("datesim", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("overlap", 0.5) != 0);
	assertTrue(p.getAtomicRuntimeCosts("soundex", 0.5) != 0);

		
    }
    
    @Test
    public void mappingApproximation() {
	System.out.println("mappingApproximation");
	HeliosPlanner p = new HeliosPlanner(source, target);
	System.out.println(source.size());
	System.out.println(target.size());

	assertTrue(p.getAtomicMappingSizes(null, 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("cosine", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("jaccard", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("jaro", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("euclidean", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("levenshtein", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("qgrams", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("exactmatch", 0.5) != 0.0d);
	
	assertTrue(p.getAtomicMappingSizes("orthodromic", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("symmetrichausdorff", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("yearsim", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("geomn", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("geomx", 0.5) != 0);
	
	assertTrue(p.getAtomicMappingSizes("geoavg", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("geomean", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("frechet", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("geolink", 0.5) != 0);
	
	assertTrue(p.getAtomicMappingSizes("geosummn", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("surjection", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("fairsurjection", 0.5) != 0);

	assertTrue(p.getAtomicMappingSizes("hausdorff", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("datesim", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("overlap", 0.5) != 0);
	assertTrue(p.getAtomicMappingSizes("soundex", 0.5) != 0);

		
    }

}
