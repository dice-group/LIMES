package org.aksw.limes.core.measures.measure.semantic.edgecounting.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.jwi.item.ISynset;

public class ShortestPathFinder {
    private static final Logger logger = LoggerFactory.getLogger(ShortestPathFinder.class);

    public static int shortestPath(List<List<ISynset>> synset1Tree, List<List<ISynset>> synset2Tree) {
        int path = Integer.MAX_VALUE;


        if (synset1Tree == null || synset2Tree == null)
            return -1;

        if (synset1Tree.isEmpty() == true || synset2Tree.isEmpty() == true)
            return -1;


        int path1Pos, path2Pos;

        logger.info("# paths for 1st: " + synset1Tree.size());
        logger.info("# paths for 2nd: " + synset2Tree.size());

        for (List<ISynset> synset1HypernymPath : synset1Tree) {
            logger.info("Size of synset tree1: "+synset1HypernymPath.size());
            for (List<ISynset> synset2HypernymPath : synset2Tree) {
                logger.info("Size of synset tree2: "+synset2HypernymPath.size());
                path1Pos = 0;
                path2Pos = 0;
                while ((path1Pos < synset1HypernymPath.size()) && (path2Pos < synset2HypernymPath.size())
                        && (synset1HypernymPath.get(path1Pos).getOffset() == synset2HypernymPath.get(path2Pos)
                                .getOffset())) {
                    ++path1Pos;
                    ++path2Pos;
                }
                logger.info("path1Pos "+path1Pos);
                int newPath = synset1HypernymPath.size() + synset2HypernymPath.size() - 2 * path1Pos;
                logger.info("newPath = " + newPath);
                logger.info("oldPath = " + path);
                if (newPath < path) {
                    path = newPath;
                }

            }
        }
        logger.info("Shortest path " + path);
        return path;

    }

}
