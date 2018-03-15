package org.aksw.limes.core.measures.measure.semantic.edgecounting.utils;

import java.util.List;

import edu.mit.jwi.item.ISynset;

public class LeastCommonSubsumer {
    protected ISynset s1;
    protected ISynset s2;

    protected List<ISynset> ps1;
    protected List<ISynset> ps2;

    protected List<ISynset> path;

    public LeastCommonSubsumer(ISynset synset1, ISynset synset2) {
        this.s1 = synset1;
        this.s2 = synset2;
    }

    public void setPaths(List<ISynset> p, List<ISynset> pathSynset1, List<ISynset> pathSynset2) {
        ps1 = pathSynset1;
        ps2 = pathSynset2;
        path = p;
    }

    public List<ISynset> getPs1() {
        return ps1;
    }

    public List<ISynset> getPs2() {
        return ps2;
    }

    public int getDepth() {
        if (path != null) {
            return path.size();
        } else {
            return 0;
        }
    }

    public int getSynsetsDistance() {
        return ps1.size() + ps2.size();
    }

    public ISynset getCommonSynset() {
        if ((path == null) || (path.size() == 0)) {
            return null;
        } else {
            return path.get(path.size() - 1);
        }
    }
}
