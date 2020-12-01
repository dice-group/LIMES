/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.string.fastngram;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class Index {

    private Map<Integer, Map<String, Set<String>>> sizeTokenIndex;
    private ITokenizer tokenizer;
    private int q = 3;

    public Index() {
        sizeTokenIndex = new HashMap<Integer, Map<String, Set<String>>>();
        tokenizer = new NGramTokenizer();
    }

    /**
     * Constructor for similarities others than trigrams
     *
     * @param _q
     *            value of n for n-grams
     */
    public Index(int _q) {
        sizeTokenIndex = new HashMap<Integer, Map<String, Set<String>>>();
        tokenizer = new NGramTokenizer();
        q = _q;
    }

    /**
     * Tokenizes a string and adds it to the index
     *
     * @param s
     *            String to index
     * @return The number of tokens generated for s
     */
    public Set<String> addString(String s) {
        // update token index
        Set<String> tokens = tokenizer.tokenize(s, q);
        int size = tokens.size();
        if (!sizeTokenIndex.containsKey(size)) {
            sizeTokenIndex.put(size, new HashMap<String, Set<String>>());
        }
        Map<String, Set<String>> tokenIndex = sizeTokenIndex.get(size);
        for (String token : tokens) {
            if (!tokenIndex.containsKey(token)) {
                tokenIndex.put(token, new HashSet<String>());
            }
            tokenIndex.get(token).add(s);
        }
        return tokens;
    }

    /**
     * Returns all strings to a given token
     *
     * @param size,
     *            Size of token
     * @param token
     *            Input token
     * @return All strings that contain this token
     */
    public Set<String> getStrings(int size, String token) {
        if (sizeTokenIndex.containsKey(size)) {
            if (sizeTokenIndex.get(size).containsKey(token)) {
                return sizeTokenIndex.get(size).get(token);
            } else {
                return new HashSet<String>();
            }
        } else {
            return new HashSet<String>();
        }
    }

    /**
     * Returns all strings of size size
     *
     * @param size
     *            Size requirement
     * @return All strings which consist of "size" different tokens
     */
    public Map<String, Set<String>> getStrings(int size) {
        if (sizeTokenIndex.containsKey(size)) {
            return sizeTokenIndex.get(size);
        } else {
            return new HashMap<String, Set<String>>();
        }
    }

    public Set<Integer> getAllSizes() {
        return sizeTokenIndex.keySet();
    }
}
