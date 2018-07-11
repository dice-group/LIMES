package org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster;

import java.util.Map;

/**
 *
 *
 * @author Cedric Richter
 */

public class APStateView {

    private APState parent;

    private APState.IterationId current;
    private APState.IterationId previous;

    public APStateView(APState parent, APState.IterationId current, APState.IterationId previous) {
        this.parent = parent;
        this.current = current;
        this.previous = previous;
    }

    public double readA(String s1, String s2){
        Double act = parent.readA(current, s1, s2);
        if(act == null) {
            act = parent.readA(previous, s1, s2);
            return act==null?0.0:act;
        }
        return act;
    }

    public double readR(String s1, String s2){
        Double act = parent.readR(current, s1, s2);
        if(act == null) {
            act = parent.readR(previous, s1, s2);
            return act==null?0.0:act;
        }
        return act;
    }

    public double readOldR(String s1, String s2){
        Double r = parent.readR(previous, s1, s2);
        if(r == null)
            return 0;
        return r;
    }

    public void putA( String s1, String s2, double d) {
        parent.putA(current, s1, s2, d);
    }

    public void putR(String s1, String s2, double d) {
        parent.putR(current, s1, s2, d);
    }

    public Map<String, Double> getNeighbours(String s){
        return parent.getNeighbours(s);
    }


}
