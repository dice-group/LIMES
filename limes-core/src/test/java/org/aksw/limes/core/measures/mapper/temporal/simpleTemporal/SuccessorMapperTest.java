package org.aksw.limes.core.measures.mapper.temporal.simpleTemporal;

import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.planner.CanonicalPlanner;
import org.aksw.limes.core.execution.planning.planner.DynamicPlanner;
import org.aksw.limes.core.execution.planning.planner.HeliosPlanner;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SuccessorMapperTest {

    public Cache source = new MemoryCache();
    public Cache target = new MemoryCache();

    @Before
    public void setUp() {
        source = new MemoryCache();
        target = new MemoryCache();
        // create source cache
        source.addTriple("S1", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:21:04+02:00");
        source.addTriple("S1", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:22:04+02:00");
        source.addTriple("S1", "http://myOntology#MachineID", "26");
        source.addTriple("S1", "name", "kleanthi");

        source.addTriple("S2", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:21:04+02:00");
        source.addTriple("S2", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:22:04+02:00");
        source.addTriple("S2", "http://myOntology#MachineID", "26");
        source.addTriple("S2", "name", "abce");

        source.addTriple("S3", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:24:04+02:00");
        source.addTriple("S3", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:25:04+02:00");
        source.addTriple("S3", "http://myOntology#MachineID", "26");
        source.addTriple("S3", "name", "pony");

        source.addTriple("S4", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:31:04+02:00");
        source.addTriple("S4", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:32:04+02:00");
        source.addTriple("S4", "http://myOntology#MachineID", "27");
        source.addTriple("S4", "name", "ping");

        source.addTriple("S5", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T09:21:04+02:00");
        source.addTriple("S5", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T09:24:04+02:00");
        source.addTriple("S5", "http://myOntology#MachineID", "27");
        source.addTriple("S5", "name", "kleanthi");

        source.addTriple("S6", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:51:04+02:00");
        source.addTriple("S6", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T09:24:04+02:00");
        source.addTriple("S6", "http://myOntology#MachineID", "27");
        source.addTriple("S6", "name", "blabla");

        source.addTriple("S7", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:41:04+02:00");
        source.addTriple("S7", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:51:04+02:00");
        source.addTriple("S7", "http://myOntology#MachineID", "28");
        source.addTriple("S7", "name", "blabla");

        source.addTriple("S8", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:41:04+02:00");
        source.addTriple("S8", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:43:04+02:00");
        source.addTriple("S8", "http://myOntology#MachineID", "29");
        source.addTriple("S8", "name", "lolelele");

        source.addTriple("S9", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:21:04+02:00");
        source.addTriple("S9", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:34:04+02:00");
        source.addTriple("S9", "http://myOntology#MachineID", "29");
        source.addTriple("S9", "name", "mattttt");

        source.addTriple("S10", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T09:21:04+02:00");
        source.addTriple("S10", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T09:22:04+02:00");
        source.addTriple("S10", "http://myOntology#MachineID", "30");
        source.addTriple("S10", "name", "mattttt");

        source.addTriple("S11", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T09:21:04+02:00");
        source.addTriple("S11", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T09:22:04+02:00");
        source.addTriple("S11", "http://myOntology#MachineID", "30");
        source.addTriple("S11", "name", "mattttt");

        source.addTriple("S12", "http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime", "2015-05-20T08:31:04+02:00");
        source.addTriple("S12", "http://purl.org/NET/c4dm/timeline.owl#endsAtDateTime", "2015-05-20T08:45:04+02:00");
        source.addTriple("S12", "http://myOntology#MachineID", "30");
        source.addTriple("S12", "name", "mattttt");

        target = source;

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
                "tmp_successor(x.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime,y.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime)",
                0.5);
        DynamicPlanner p = new DynamicPlanner(source, target);
        ExecutionEngine e = new SimpleExecutionEngine(source, target, "?x", "?y");
        AMapping m = e.execute(ls, p);
        System.out.println(m);

    }

    @Test
    public void complexLS() {
        System.out.println("complexLS");
        LinkSpecification ls = new LinkSpecification(
                "OR(tmp_successor(x.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime|http://myOntology#MachineID,y.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime|http://myOntology#MachineID)|1.0,trigrams(x.name,y.name)|0.8)",
                1.0);
        ExecutionEngine e = new SimpleExecutionEngine(source, target, "?x", "?y");

        DynamicPlanner p = new DynamicPlanner(source, target);
        AMapping m = e.execute(ls, p);

        CanonicalPlanner p2 = new CanonicalPlanner();
        AMapping mm = e.execute(ls, p2);

        HeliosPlanner p3 = new HeliosPlanner(source, target);
        AMapping mmm = e.execute(ls, p3);

        assertTrue(m.equals(mm));
        assertTrue(mm.equals(mmm));

    }

    @Test
    public void complexLS2() {
        System.out.println("complexLS2");
        LinkSpecification ls = new LinkSpecification(
                "AND(tmp_successor(x.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime,y.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime)|1.0,trigrams(x.name,y.name)|0.3)",
                1.0);
        ExecutionEngine e = new SimpleExecutionEngine(source, target, "?x", "?y");

        DynamicPlanner p = new DynamicPlanner(source, target);
        AMapping m = e.execute(ls, p);

        CanonicalPlanner p2 = new CanonicalPlanner();
        AMapping mm = e.execute(ls, p2);

        HeliosPlanner p3 = new HeliosPlanner(source, target);
        AMapping mmm = e.execute(ls, p3);

        assertTrue(!m.equals(mm));
        assertTrue(!mm.equals(mmm));
        assertTrue(m.equals(mmm));
    }

    @Test
    public void complexLS3() {
        System.out.println("complexLS3");
        ExecutionEngine e = new SimpleExecutionEngine(source, target, "?x", "?y");
        DynamicPlanner p = new DynamicPlanner(source, target);

        LinkSpecification ls1 = new LinkSpecification(
                "tmp_successor(x.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime,y.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime)",
                1.0);
        LinkSpecification ls2 = new LinkSpecification("trigrams(x.name,y.name)", 0.8);
        AMapping m1 = e.execute(ls1, p);
        AMapping m2 = e.execute(ls2, p);
        System.out.println(m1);
        System.out.println(m2);

        LinkSpecification ls = new LinkSpecification(
                "MINUS(tmp_successor(x.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime,y.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime)|1.0,trigrams(x.name,y.name)|0.8)",
                1.0);
        AMapping m = e.execute(ls, p);

        CanonicalPlanner p2 = new CanonicalPlanner();
        AMapping mm = e.execute(ls, p2);

        HeliosPlanner p3 = new HeliosPlanner(source, target);
        AMapping mmm = e.execute(ls, p3);

        assertTrue(m.equals(mm));
        assertTrue(mm.equals(mmm));

    }

    @Test
    public void complexLS4() {
        System.out.println("complexLS4");
        ExecutionEngine e = new SimpleExecutionEngine(source, target, "?x", "?y");
        DynamicPlanner p = new DynamicPlanner(source, target);

        LinkSpecification ls1 = new LinkSpecification(
                "tmp_successor(x.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime,y.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime)",
                1.0);
        LinkSpecification ls2 = new LinkSpecification("trigrams(x.name,y.name)", 0.8);
        AMapping m1 = e.execute(ls1, p);
        AMapping m2 = e.execute(ls2, p);
        System.out.println(m1);
        System.out.println(m2);

        LinkSpecification ls = new LinkSpecification(
                "XOR(trigrams(x.name,y.name)|0.8,tmp_successor(x.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime,y.http://purl.org/NET/c4dm/timeline.owl#beginsAtDateTime)|1.0)",
                1.0);
        AMapping m = e.execute(ls, p);

        CanonicalPlanner p2 = new CanonicalPlanner();
        AMapping mm = e.execute(ls, p2);

        HeliosPlanner p3 = new HeliosPlanner(source, target);
        AMapping mmm = e.execute(ls, p3);

        assertTrue(m.equals(mm));
        assertTrue(!mm.equals(mmm));
        assertTrue(!m.equals(mmm));
    }

}
