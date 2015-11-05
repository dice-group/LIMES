/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.atomic.fastngram;

import java.util.Set;

/**
 *
 * @author ngonga
 */
public interface Tokenizer {
    Set<String> tokenize(String s, int q);
}
