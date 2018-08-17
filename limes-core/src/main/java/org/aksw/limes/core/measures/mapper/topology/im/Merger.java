package org.aksw.limes.core.measures.mapper.topology.im;

import org.aksw.limes.core.io.mapping.AMapping;

import java.util.*;

public class Merger implements Runnable {

    private AMapping m;
    private List<Map<String, Set<String>>> localResults = new ArrayList<>();

    public Merger(List<Map<String, Set<String>>> results, AMapping m) {
        this.m = m;
        // copy over entries to local list
        synchronized (results) {
            for (Iterator<Map<String, Set<String>>> iterator = results.listIterator(); iterator.hasNext();) {
                localResults.add(iterator.next());
                iterator.remove();
            }
        }
    }

    @Override
    public void run() {
        // merge back to m
        for (Map<String, Set<String>> result : localResults) {
            for (String s : result.keySet()) {
                for (String t : result.get(s)) {
                    if (GridSizeHeuristics.swap)
                        m.add(t, s, 1.0d);
                    else
                        m.add(s, t, 1.0d);
                }
            }
        }
    }
}
