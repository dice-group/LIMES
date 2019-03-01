/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.string.fastngram;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class NGramTokenizer implements ITokenizer {

    @Override
    public Set<String> tokenize(String s, int q) {
        if (s == null) {
            s = "";
        }
        String pad = String.join("", Collections.nCopies(q-1, " "));
        s = pad + s + pad;
        Set<String> tokens = new HashSet<>();
        for (int i = 0; i < s.length() - q + 1; i++) {
            tokens.add(s.substring(i, i + q));
        }
        return tokens;
    }
}
