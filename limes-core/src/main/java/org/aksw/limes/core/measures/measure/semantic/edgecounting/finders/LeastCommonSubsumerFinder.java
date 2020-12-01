package org.aksw.limes.core.measures.measure.semantic.edgecounting.finders;

import java.util.ArrayList;
import java.util.List;

import edu.mit.jwi.item.ISynsetID;

/**
 * Implements the Least Common Subsumer (LSO) class. The LSO of two concepts c1
 * and c2 is "the most specific concept which is an ancestor of both c1 and c2".
 * 
 * 
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 *
 */
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

    /**
     * Computes the LSO between two concepts using their hypernym paths. For
     * each pair of hypernym paths, it traverses both the paths simultaneously,
     * starting from the root and until they share no more common concepts. Then
     * it calculates the path length between the two concepts, as the number of
     * the concepts that they do not have in common. In case 1) there is no LSO
     * found until now, or 2) the new common concept is located deeper in the
     * hierarchy, or 3) the new common concept has the same depth as the
     * previous one but a smaller distance between the two concepts, the
     * algorithm sets as the new common concept as the LSO.
     * 
     * 
     * @param synset1Tree,
     *            the hypernym paths of the first concept
     * @param synset2Tree,
     *            the hypernym paths of the second concept
     */
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
                // if 0) there is no LSO available until now, 1) the new common
                // concept is located deeper in the hierarchy
                // than our current LSO or 2) the new common concept has the
                // same
                // depth but a smaller distance between the two concepts
                int newPath = synset1HypernymPath.size() + synset2HypernymPath.size() - 2 * path1Pos;
                int oldPath = lcs.getPs1().size() + lcs.getPs2().size();

                if ((lcs.getPath() == null) || (path1Pos > lcs.getDepth())
                        || ((path1Pos == lcs.getDepth()) && (newPath < oldPath))) {
                    // we have found a new LSO

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
