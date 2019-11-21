package org.aksw.limes.core.measures.measure.semantic.edgecounting.factory;

import org.aksw.limes.core.exceptions.NullIndexerException;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.AEdgeCountingSemanticMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.indexing.AIndex;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.measures.LCHMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.measures.LiMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.measures.ShortestPathMeasure;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.measures.WuPalmerMeasure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the semantic measure factory class. For each semantic measure
 * name, the factory returns an object of the corresponding semantic measure.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 *
 * @version 1.0
 */
public class SemanticFactory {
    // Semantic edge-counting measures
    public static final String SHORTEST_PATH = "shortest_path";
    public static final String LCH = "lch";
    public static final String LI = "li";
    public static final String WUPALMER = "wupalmer";

    static Logger logger = LoggerFactory.getLogger(SemanticFactory.class);

    /**
     * Factory function for retrieving the desired semantic measure instance.
     *
     * @param measure,
     *            Type of the measure
     * @param Indexer,
     *            an index instance
     *
     * @return a specific measure instance
     *
     */
    public static AEdgeCountingSemanticMeasure createMeasure(SemanticType measure, AIndex Indexer) {
        if (Indexer == null) {
            throw new NullIndexerException("Cannot initialize " + measure + ". Index instance is null.");
        }

        if (measure == SemanticType.SHORTEST_PATH)
            return new ShortestPathMeasure(Indexer);
        else if (measure == SemanticType.LI)
            return new LiMeasure(Indexer);
        else if (measure == SemanticType.LCH)
            return new LCHMeasure(Indexer);
        else if (measure == SemanticType.WUPALMER)
            return new WuPalmerMeasure(Indexer);
        return null;
    }

    /**
     * Factory function for retrieving a semantic measure name from the set of
     * allowed types.
     *
     * @param measure,
     *            The name/type of the measure.
     * @return a specific semantic measure type
     */
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
