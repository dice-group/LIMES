package org.aksw.limes.core.measures.mapper.pointsets;

import java.util.Arrays;
import java.util.List;

import org.aksw.limes.core.io.parser.Parser;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class PropertyFetcher {

    public static List<String> getProperties(String expression, double threshold) {
        // get property labels
        Parser p = new Parser(expression, threshold);
        return Arrays.asList(getPropertyLabel(p.getLeftTerm()), getPropertyLabel(p.getRightTerm()));
    }

    private static String getPropertyLabel(String term) {
        String propertyLabel;
        if (term.contains(".")) {
            String split[] = term.split("\\.");
            propertyLabel = split[1];
            if (split.length >= 2) {
                for (int part = 2; part < split.length; part++) {
                    propertyLabel += "." + split[part];
                }
            }
        } else {
            propertyLabel = term;
        }
        return propertyLabel;
    }
}
