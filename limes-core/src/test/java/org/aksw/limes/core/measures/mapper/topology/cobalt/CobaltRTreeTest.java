package org.aksw.limes.core.measures.mapper.topology.cobalt;

import org.junit.Test;
import org.locationtech.jts.geom.Envelope;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class CobaltRTreeTest {

    @Test
    public void testSearch() {
        ArrayList<RTree.Entry> entries = new ArrayList<>();
        entries.add(new RTree.Entry("1", new Envelope(0, 10, 0, 10), null));
        entries.add(new RTree.Entry("2", new Envelope(0, 50, 0, 50), null));
        entries.add(new RTree.Entry("3", new Envelope(20, 30, 20, 30), null));
        entries.add(new RTree.Entry("4", new Envelope(10, 20, 0, 10), null));
        entries.add(new RTree.Entry("5", new Envelope(50, 50, 60, 60), null));
        RTree rTree = RTree.buildSTR(entries);
        List<String> results = rTree.search(new Envelope(10, 15, 10, 20)).stream().map(RTree.Entry::getUri).collect(Collectors.toList());
        assertTrue(results.contains("1"));
        assertTrue(results.contains("2"));
        assertFalse(results.contains("3"));
        assertTrue(results.contains("4"));
        assertFalse(results.contains("5"));
    }

    @Test
    public void testSearchExcept() {
        ArrayList<RTree.Entry> entries = new ArrayList<>();
        entries.add(new RTree.Entry("1", new Envelope(0, 10, 0, 10), null));
        entries.add(new RTree.Entry("2", new Envelope(0, 50, 0, 50), null));
        entries.add(new RTree.Entry("3", new Envelope(20, 30, 20, 30), null));
        entries.add(new RTree.Entry("4", new Envelope(10, 20, 0, 10), null));
        entries.add(new RTree.Entry("5", new Envelope(50, 50, 60, 60), null));
        RTree rTree = RTree.buildSTR(entries);
        List<String> results = rTree.searchExcept(new Envelope(10, 15, 10, 20)).stream().map(RTree.Entry::getUri).collect(Collectors.toList());
        assertFalse(results.contains("1"));
        assertFalse(results.contains("2"));
        assertTrue(results.contains("3"));
        assertFalse(results.contains("4"));
        assertTrue(results.contains("5"));
    }

}
