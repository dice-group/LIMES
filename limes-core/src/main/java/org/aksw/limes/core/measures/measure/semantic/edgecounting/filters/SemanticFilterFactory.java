package org.aksw.limes.core.measures.measure.semantic.edgecounting.filters;

import org.aksw.limes.core.exceptions.InvalidMeasureException;

public class SemanticFilterFactory {
    public static final String SHORTEST_PATH = "shortest_path";
    public static final String WUPALMER = "wupalmer";
    public static final String LCH = "lch";
    public static final String LI = "li";

    public static SemanticFilterType getFilterType(String expression) {
        String measure = expression.toLowerCase();
        if (measure.startsWith(SHORTEST_PATH)) {
            return SemanticFilterType.SHORTEST_PATH;
        }
        if (measure.startsWith(WUPALMER)) {
            return SemanticFilterType.WUPALMER;
        }
        if (measure.startsWith(LCH)) {
            return SemanticFilterType.LCH;
        }
        if (measure.startsWith(LI)) {
            return SemanticFilterType.LI;
        }
        throw new InvalidMeasureException(measure);
    }

    public static ASemanticFilter createFilter(SemanticFilterType type, double theta, double D) {
        switch (type) {
        case SHORTEST_PATH:
            return new ShortestPathFilter(theta, D);
        case WUPALMER:
            return new WuPalmerFilter(theta, D);
        case LCH:
            return new LCHFilter(theta, D);
        case LI:
            return new LiFilter(theta, D);
        default:
            throw new InvalidMeasureException(type.toString());
        }

    }
}
