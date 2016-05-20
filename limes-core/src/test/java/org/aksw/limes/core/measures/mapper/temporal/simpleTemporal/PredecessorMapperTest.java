package org.aksw.limes.core.measures.mapper.temporal.simpleTemporal;

import static org.junit.Assert.*;

import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.planner.CanonicalPlanner;
import org.aksw.limes.core.execution.planning.planner.DynamicPlanner;
import org.aksw.limes.core.execution.planning.planner.HeliosPlanner;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.Mapping;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PredecessorMapperTest {

    public Cache source = new MemoryCache();
    public Cache target = new MemoryCache();

    @Before
    public void setUp() {
	source = new MemoryCache();
	target = new MemoryCache();
	// create source cache
	source.addTriple("S1", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:21:04+02:00");
	source.addTriple("S1", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:21:04+02:00");
	source.addTriple("S1", "http://myOntology#MachineID", "26");
	source.addTriple("S1", "name", "kleanthi");

	source.addTriple("S2", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:21:04+02:00");
	source.addTriple("S2", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:21:04+02:00");
	source.addTriple("S2", "http://myOntology#MachineID", "26");
	source.addTriple("S2", "name", "abce");

	source.addTriple("S3", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:24:04+02:00");
	source.addTriple("S3", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:24:04+02:00");
	source.addTriple("S3", "http://myOntology#MachineID", "26");
	source.addTriple("S3", "name", "pony");

	source.addTriple("S4", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:31:04+02:00");
	source.addTriple("S4", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:31:04+02:00");
	source.addTriple("S4", "http://myOntology#MachineID", "27");
	source.addTriple("S4", "name", "ping");

	source.addTriple("S5", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:21:04+02:00");
	source.addTriple("S5", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:21:04+02:00");
	source.addTriple("S5", "http://myOntology#MachineID", "27");
	source.addTriple("S5", "name", "kleanthi");

	source.addTriple("S6", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-06-20T08:21:04+02:00");
	source.addTriple("S6", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-06-20T08:21:04+02:00");
	source.addTriple("S6", "http://myOntology#MachineID", "27");
	source.addTriple("S6", "name", "blabla");

	source.addTriple("S7", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-06-20T08:21:04+02:00");
	source.addTriple("S7", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-06-20T08:21:04+02:00");
	source.addTriple("S7", "http://myOntology#MachineID", "28");
	source.addTriple("S7", "name", "blabla");

	source.addTriple("S8", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-06-20T08:21:04+02:00");
	source.addTriple("S8", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-06-20T08:21:04+02:00");
	source.addTriple("S8", "http://myOntology#MachineID", "29");
	source.addTriple("S8", "name", "lolelele");

	source.addTriple("S9", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T09:21:04+02:00");
	source.addTriple("S9", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T09:21:04+02:00");
	source.addTriple("S9", "http://myOntology#MachineID", "29");
	source.addTriple("S9", "name", "mattttt");

	source.addTriple("S10", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T09:21:04+02:00");
	source.addTriple("S10", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T09:21:04+02:00");
	source.addTriple("S10", "http://myOntology#MachineID", "30");
	source.addTriple("S10", "name", "mattttt");

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	target.addTriple("T1", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:21:04+02:00");
	target.addTriple("T1", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:21:04+02:00");
	target.addTriple("T1", "http://myOntology#MachineID", "26");
	target.addTriple("T1", "name", "kleanthi");

	target.addTriple("T2", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:21:04+02:00");
	target.addTriple("T2", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:21:04+02:00");
	target.addTriple("T2", "http://myOntology#MachineID", "26");
	target.addTriple("T2", "name", "abce");

	target.addTriple("T3", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:24:04+02:00");
	target.addTriple("T3", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:24:04+02:00");
	target.addTriple("T3", "http://myOntology#MachineID", "26");
	target.addTriple("T3", "name", "pony");

	target.addTriple("T4", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:31:04+02:00");
	target.addTriple("T4", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:31:04+02:00");
	target.addTriple("T4", "http://myOntology#MachineID", "27");
	target.addTriple("T4", "name", "ping");

	target.addTriple("T5", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:21:04+02:00");
	target.addTriple("T5", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:21:04+02:00");
	target.addTriple("T5", "http://myOntology#MachineID", "27");
	target.addTriple("T5", "name", "kleanthi");

	target.addTriple("T6", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-06-20T08:21:04+02:00");
	target.addTriple("T6", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-06-20T08:21:04+02:00");
	target.addTriple("T6", "http://myOntology#MachineID", "27");
	target.addTriple("T6", "name", "blabla");

	target.addTriple("T7", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-06-20T08:21:04+02:00");
	target.addTriple("T7", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-06-20T08:21:04+02:00");
	target.addTriple("T7", "http://myOntology#MachineID", "28");
	target.addTriple("T7", "name", "blabla");

	target.addTriple("T8", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-06-20T08:21:04+02:00");
	target.addTriple("T8", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-06-20T08:21:04+02:00");
	target.addTriple("T8", "http://myOntology#MachineID", "29");
	target.addTriple("T8", "name", "lolelele");

	target.addTriple("T9", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T09:21:04+02:00");
	target.addTriple("T9", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T09:21:04+02:00");
	target.addTriple("T9", "http://myOntology#MachineID", "29");
	target.addTriple("T9", "name", "mattttt");

	target.addTriple("T10", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T09:21:04+02:00");
	target.addTriple("T10", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T09:21:04+02:00");
	target.addTriple("T10", "http://myOntology#MachineID", "30");
	target.addTriple("T10", "name", "mattttt");

    }

    @After
    public void tearDown() {
	source = null;
	target = null;
    }

    @Test
    public void simpleLS() {
	System.out.println("simpleLS");
	LinkSpecification ls = new LinkSpecification(
		"tmp_predecessor(x.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime,y.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime)",
		0.5);
	DynamicPlanner p = new DynamicPlanner(source, target);
	ExecutionEngine e = new SimpleExecutionEngine(source, target, "?x", "?y");
	Mapping m = e.execute(ls, p);
	System.out.println(m);

    }

    @Test
    public void complexLS() {
	System.out.println("complexLS");
	LinkSpecification ls = new LinkSpecification(
		"OR(tmp_predecessor(x.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime|http://myOntology#MachineID,y.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime|http://myOntology#MachineID)|1.0,trigrams(x.name,y.name)|0.8)",
		1.0);
	ExecutionEngine e = new SimpleExecutionEngine(source, target, "?x", "?y");

	DynamicPlanner p = new DynamicPlanner(source, target);
	Mapping m = e.execute(ls, p);

	CanonicalPlanner p2 = new CanonicalPlanner();
	Mapping mm = e.execute(ls, p2);

	HeliosPlanner p3 = new HeliosPlanner(source, target);
	Mapping mmm = e.execute(ls, p3);

	assertTrue(m.equals(mm));
	assertTrue(mm.equals(mmm));

    }

    @Test
    public void complexLS2() {
	System.out.println("complexLS2");
	LinkSpecification ls = new LinkSpecification(
		"AND(tmp_predecessor(x.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime,y.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime)|1.0,trigrams(x.name,y.name)|0.3)",
		1.0);
	ExecutionEngine e = new SimpleExecutionEngine(source, target, "?x", "?y");

	DynamicPlanner p = new DynamicPlanner(source, target);
	Mapping m = e.execute(ls, p);

	CanonicalPlanner p2 = new CanonicalPlanner();
	Mapping mm = e.execute(ls, p2);

	HeliosPlanner p3 = new HeliosPlanner(source, target);
	Mapping mmm = e.execute(ls, p3);

	assertTrue(m.equals(mm));
	assertTrue(mm.equals(mmm));
    }

    @Test
    public void complexLS3() {
	System.out.println("complexLS3");
	ExecutionEngine e = new SimpleExecutionEngine(source, target, "?x", "?y");
	DynamicPlanner p = new DynamicPlanner(source, target);

	LinkSpecification ls1 = new LinkSpecification(
		"tmp_predecessor(x.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime|http://myOntology#MachineID,y.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime|http://myOntology#MachineID)",
		1.0);
	LinkSpecification ls2 = new LinkSpecification("trigrams(x.name,y.name)", 0.8);
	Mapping m1 = e.execute(ls1, p);
	Mapping m2 = e.execute(ls2, p);
	System.out.println(m1);
	System.out.println(m2);

	LinkSpecification ls = new LinkSpecification(
		"MINUS(tmp_predecessor(x.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime|http://myOntology#MachineID,y.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime|http://myOntology#MachineID)|1.0,trigrams(x.name,y.name)|0.8)",
		1.0);
	Mapping m = e.execute(ls, p);

	CanonicalPlanner p2 = new CanonicalPlanner();
	Mapping mm = e.execute(ls, p2);

	HeliosPlanner p3 = new HeliosPlanner(source, target);
	Mapping mmm = e.execute(ls, p3);

	assertTrue(m.equals(mm));
	assertTrue(mm.equals(mmm));
    }

    @Test
    public void complexLS4() {
	System.out.println("complexLS4");
	ExecutionEngine e = new SimpleExecutionEngine(source, target, "?x", "?y");
	DynamicPlanner p = new DynamicPlanner(source, target);

	LinkSpecification ls1 = new LinkSpecification(
		"tmp_predecessor(x.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime,y.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime)",
		1.0);
	LinkSpecification ls2 = new LinkSpecification("trigrams(x.name,y.name)", 0.8);
	Mapping m1 = e.execute(ls1, p);
	Mapping m2 = e.execute(ls2, p);
	System.out.println(m1);
	System.out.println(m2);

	LinkSpecification ls = new LinkSpecification(
		"XOR(trigrams(x.name,y.name)|0.8,tmp_predecessor(x.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime,y.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime)|1.0)",
		1.0);
	Mapping m = e.execute(ls, p);

	CanonicalPlanner p2 = new CanonicalPlanner();
	Mapping mm = e.execute(ls, p2);

	HeliosPlanner p3 = new HeliosPlanner(source, target);
	Mapping mmm = e.execute(ls, p3);

	assertTrue(m.equals(mm));
	assertTrue(mm.equals(mmm));

    }

}
