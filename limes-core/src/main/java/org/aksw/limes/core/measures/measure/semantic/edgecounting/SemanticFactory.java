package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SemanticFactory {
    // Semantic edge-counting measures
    public static final String SHORTEST_PATH = "shortest_path";
    public static final String LCH = "lch";
    public static final String LI = "li";
    public static final String WUPALMER = "wupalmer";

    static Logger logger = LoggerFactory.getLogger(SemanticFactory.class);

    public static AEdgeCountingSemanticMeasure createMeasure(SemanticType measure, double threshold,
            boolean preindex, boolean filtering) {
        if (measure == SemanticType.SHORTEST_PATH)
            return new ShortestPathMeasure(threshold, preindex, filtering);
        else if (measure == SemanticType.LI)
            return new LiMeasure(threshold, preindex, filtering);
        else if (measure == SemanticType.LCH)
            return new LCHMeasure(threshold, preindex, filtering);
        else if (measure == SemanticType.WUPALMER)
            return new WuPalmerMeasure(threshold, preindex, filtering);
        return null;
    }

    public static SemanticType getMeasureType(String measure) {

        if (measure.startsWith(SHORTEST_PATH)) {
            return SemanticType.SHORTEST_PATH;
        }
        if (measure.startsWith(LCH)) {
            return SemanticType.LCH;
        }
        if (measure.startsWith(LI)) {
            return SemanticType.LI;
        }
        if (measure.startsWith(WUPALMER)) {
            return SemanticType.WUPALMER;
        }
        return null;
    }
}
