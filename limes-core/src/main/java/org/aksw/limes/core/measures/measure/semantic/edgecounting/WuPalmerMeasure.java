package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import java.util.List;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.LeastCommonSubsumerFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.jwi.item.ISynset;

public class WuPalmerMeasure extends AEdgeCountingSemanticMeasure {
    private static final Logger logger = LoggerFactory.getLogger(WuPalmerMeasure.class);

    public WuPalmerMeasure() {
        super();
    }

    double maxValue = 1;

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;

    }

    @Override
    public String getName() {
        return "wupalmer";
    }

    @Override
    public String getType() {
        return "semantic";
    }

    
    
    @Override
    public double getSimilarity(ISynset synset1, List<List<ISynset>> synset1Tree, ISynset synset2,
            List<List<ISynset>> synset2Tree) {
        
        if (synset1.getType() != synset2.getType()) {
            logger.info(synset1.getType() + " " + synset2.getType());
            return 0;
        }
        if (synset1Tree.isEmpty() == true || synset2Tree.isEmpty() == true) {
            logger.info("Empty trees");
            return 0;
        }
        if (synset1.getOffset() == synset2.getOffset()) {
            logger.info("Max value: " + maxValue);
            return maxValue;
        }

        LeastCommonSubsumerFinder finder = new LeastCommonSubsumerFinder();
        finder.getLeastCommonSubsumer(synset1Tree, synset2Tree);
        double depth = (double) finder.getDepth();
        if (depth == -1) {
            logger.info("lcs is null. Depth is -1.");
            return 0.0d;
        }
        double length = (double) finder.getSynsetsDistance();
        if (length == -1) {
            logger.info("lcs is null. Length is -1.");
            return 0.0d;
        }
        logger.info("LCS depth: " + depth);
        logger.info("Synsets distance: " + length);
        
        double sim = 0;
        sim = (double) (2.0 * (double) depth) / (double) (length + (2.0 * depth));
        logger.info("Similarity: " + sim);
        return sim;
    }

}
