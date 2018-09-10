package org.aksw.limes.core.measures.measure.semantic.edgecounting.utils;

import java.util.ArrayList;
import java.util.List;

import edu.mit.jwi.item.ISynsetID;

public class LeastCommonSubsumerFinder {
    LeastCommonSubsumer lcs = null;
    
    public int getDepth() {
        if (lcs == null)
            return -1;
        return lcs.getDepth();
    }

    public int getSynsetsDistance() {
        if (lcs == null)
            return -1;
        return lcs.getSynsetsDistance();
    }

    public void getLeastCommonSubsumer(ArrayList<ArrayList<ISynsetID>> synset1Tree,
            ArrayList<ArrayList<ISynsetID>> synset2Tree) {

        if (synset1Tree == null || synset2Tree == null)
            return;

        if (synset1Tree.isEmpty() == true || synset2Tree.isEmpty() == true)
            return;

        lcs = new LeastCommonSubsumer();
        int path1Pos, path2Pos;

        for (List<ISynsetID> synset1HypernymPath : synset1Tree) {
            for (List<ISynsetID> synset2HypernymPath : synset2Tree) {

                path1Pos = 0;
                path2Pos = 0;
                while ((path1Pos < synset1HypernymPath.size()) && (path2Pos < synset2HypernymPath.size())
                        && (synset1HypernymPath.get(path1Pos).getOffset() == synset2HypernymPath.get(path2Pos)
                                .getOffset())) {
                    ++path1Pos;
                    ++path2Pos;
                }
                // if 0) there is no DCS available until now, 1) the new common
                // synset is located deeper in the hierarchy
                // than our current DCS or 2) the new common synset has the same
                // depth but a smaller distance between the two synsets
                int newPath = synset1HypernymPath.size() + synset2HypernymPath.size() - 2 * path1Pos;
                int oldPath = lcs.getPs1().size() + lcs.getPs2().size();

                if ((lcs.getPath() == null) || (path1Pos > lcs.getDepth())
                        || ((path1Pos == lcs.getDepth()) && (newPath < oldPath))) {
                    // we have found a new DCS

                    lcs.setPaths(synset1HypernymPath.subList(0, path1Pos),
                            synset1HypernymPath.subList(path1Pos, synset1HypernymPath.size()),
                            synset2HypernymPath.subList(path2Pos, synset2HypernymPath.size()));

                }

            }
        }
    }

    @Deprecated
    public void getLeastCommonSubsumerViaShortestPath(ArrayList<ArrayList<ISynsetID>> synset1Tree,
            ArrayList<ArrayList<ISynsetID>> synset2Tree) {

        if (synset1Tree == null || synset2Tree == null)
            return;

        if (synset1Tree.isEmpty() == true || synset2Tree.isEmpty() == true)
            return;

        lcs = new LeastCommonSubsumer();
        int path1Pos, path2Pos;

        for (List<ISynsetID> synset1HypernymPath : synset1Tree) {

            for (List<ISynsetID> synset2HypernymPath : synset2Tree) {

                path1Pos = 0;
                path2Pos = 0;
                while ((path1Pos < synset1HypernymPath.size()) && (path2Pos < synset2HypernymPath.size())
                        && (synset1HypernymPath.get(path1Pos).getOffset() == synset2HypernymPath.get(path2Pos)
                                .getOffset())) {
                    ++path1Pos;
                    ++path2Pos;
                }
                // if 0) there is no DCS available until now, 1) the new
                // distance between the synset is less than the existing one
                // or 2) the current distance of synsets is the same as the
                // existing one but the new common synset is deeper in the
                // hierarchy than our current DCS
                int newPath = synset1HypernymPath.size() + synset2HypernymPath.size() - 2 * path1Pos;
                int oldPath = lcs.getPs1().size() + lcs.getPs2().size();

                if ((lcs.getPath() == null) || (newPath < oldPath)
                        || ((path1Pos > lcs.getDepth()) && (newPath == oldPath))) {
                    // we have found a new DCS

                    lcs.setPaths(synset1HypernymPath.subList(0, path1Pos),
                            synset1HypernymPath.subList(path1Pos, synset1HypernymPath.size()),
                            synset2HypernymPath.subList(path2Pos, synset2HypernymPath.size()));

                }

            }
        }
    }

    private class LeastCommonSubsumer {

        protected List<ISynsetID> ps1;
        protected List<ISynsetID> ps2;

        protected List<ISynsetID> path;

        protected LeastCommonSubsumer() {
            this.ps1 = new ArrayList<ISynsetID>();
            this.ps2 = new ArrayList<ISynsetID>();
        }

        protected void setPaths(List<ISynsetID> p, List<ISynsetID> pathSynset1, List<ISynsetID> pathSynset2) {
            ps1 = pathSynset1;
            ps2 = pathSynset2;
            path = p;
        }

        protected List<ISynsetID> getPs1() {
            return ps1;
        }

        protected List<ISynsetID> getPs2() {
            return ps2;
        }

        protected int getDepth() {
            if (path != null) {
                return path.size();
            } else {
                return 0;
            }
        }

        protected int getSynsetsDistance() {
            return ps1.size() + ps2.size();
        }

        protected List<ISynsetID> getPath() {
            return this.path;
        }

    }
}
