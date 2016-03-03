/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.atomic.fastngram;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ngonga
 */
public class NGramTokenizer implements Tokenizer {

    @Override
    public Set<String> tokenize(String s, int q) {
	if (s == null) {
	    s = "";
	}
	// remove double blanks
	while (s.contains("  ")) {
	    s = s.replaceAll("  ", " ");
	}
	s = s.trim();
	// for (int i = 1; i < q; i++) {
	while (s.length() < q) {
	    s = s + "_";
	}
	Set<String> tokens = new HashSet<String>();
	for (int i = 0; i < s.length() - q + 1; i++) {
	    tokens.add(s.substring(i, i + q));
	}
	return tokens;
    }
}
