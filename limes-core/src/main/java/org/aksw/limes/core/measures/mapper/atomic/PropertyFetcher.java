/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.atomic;


import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.io.parser.Parser;

/**
 *
 * @author ngonga
 */
public class PropertyFetcher {

    public static List<String> getProperties(String expression, double threshold) {
        //0. get properties
        String property1, property2;
        //get property labels
        Parser p = new Parser(expression, threshold);
        //get first property label
        String term1 = p.getTerm1();
        if (term1.contains(".")) {
            String split[] = term1.split("\\.");
            property1 = split[1];
            if (split.length >= 2) {
                for (int part = 2; part < split.length; part++) {
                    property1 += "." + split[part];
                }
            }
        } else {
            property1 = term1;
        }

        //get second property label
        String term2 = p.getTerm2();
        if (term2.contains(".")) {
            String split[] = term2.split("\\.");
            property2 = split[1];
            if (split.length >= 2) {
                for (int part = 2; part < split.length; part++) {
                    property2 += "." + split[part];
                }
            }
        } else {
            property2 = term2;
        }
        List<String> result = new ArrayList<String>();
        result.add(property1);
        result.add(property2);

        return result;
    }
}
