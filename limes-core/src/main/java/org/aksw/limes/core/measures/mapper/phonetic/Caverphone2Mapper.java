/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.measures.mapper.phonetic;

import org.aksw.limes.core.exceptions.InvalidThresholdException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.mapper.pointsets.PropertyFetcher;
import org.aksw.limes.core.measures.measure.phoneticmeasure.Caverphone2Measure;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;

public class Caverphone2Mapper extends AMapper {

    /**
     * Computes a mapping between a source and a target.
     *
     * @param source
     *            Source cache
     * @param target
     *            Target cache
     * @param sourceVar
     *            Variable for the source dataset
     * @param targetVar
     *            Variable for the target dataset
     * @param expression
     *            Expression to process.
     * @param threshold
     *            Similarity threshold
     * @return A mapping which contains links between the source instances and
     *         the target instances
     */
    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
                               double threshold) {
        if (threshold <= 0) {
            throw new InvalidThresholdException(threshold);
        }
        List<String> listA, listB;
        Map<String, List<Integer>> invListA, invListB;
        List<String> properties = PropertyFetcher.getProperties(expression, threshold);
        String property1 = properties.get(0);
        String property2 = properties.get(1);
        //if(property1.contains("#"))
        //property1=property1.substring(property1.indexOf("#")+1);
        //if(property2.contains("#"))
        //property2=property2.substring(property2.indexOf("#")+1);
        //System.out.println(" p1: "+property1);
        //System.out.println(" p2: "+property2);
        Map<String, Set<String>> sourceMap = getValueToUriMap(source, property1);
        Map<String, Set<String>> targetMap = getValueToUriMap(target, property2);
        listA = new ArrayList<>(sourceMap.keySet());
        listB = new ArrayList<>(targetMap.keySet());
        // create inverted lists (code=>index of original list)
        invListA = getInvertedList(listA);
        invListB = getInvertedList(listB);
        Deque<Triple<Integer, List<Integer>, List<Integer>>> similarityBook = new ArrayDeque<>();
        // construct trie from smaller list
        TrieNode trie = TrieNode.recursiveAddAll(invListB);
        int maxDistance = getMaxDistance(threshold);
        // iterate over other list
        for (Map.Entry<String, List<Integer>> entry : invListA.entrySet()) {
            // for each entry do trie search
            Deque<TrieSearchState> queue = new ArrayDeque<>();
            queue.add(new TrieSearchState(0, 0, trie));
            while (!queue.isEmpty()) {
                TrieSearchState current = queue.pop();
                Set<Map.Entry<Character, TrieNode>> childs = current.getNode().getChildren();
                if (childs.isEmpty() && !current.getNode().getReferences().isEmpty()) {
                    similarityBook.push(new MutableTriple<>(current.getDistance(), entry.getValue(),
                            current.getNode().getReferences()));
                }
                for (Map.Entry<Character, TrieNode> nodeEntry : childs) {
                    if (nodeEntry.getKey().equals(entry.getKey().charAt(current.getPosition()))) {
                        queue.push(new TrieSearchState(current.getDistance(), current.getPosition() + 1,
                                nodeEntry.getValue()));
                    } else if (current.getDistance() < maxDistance) {
                        queue.push(new TrieSearchState(current.getDistance() + 1, current.getPosition() + 1,
                                nodeEntry.getValue()));
                    }
                }
            }
        }
        AMapping result = MappingFactory.createDefaultMapping();
        while (!similarityBook.isEmpty()) {
            Triple<Integer, List<Integer>, List<Integer>> t = similarityBook.pop();
            for (Integer i : t.getMiddle()) {
                String a = listA.get(i);
                for (Integer j : t.getRight()) {
                    String b = listB.get(j);
                    for (String sourceUri : sourceMap.get(a)) {
                        for (String targetUri : targetMap.get(b)) {
                            result.add(sourceUri, targetUri,
                                    (1.0d - (t.getLeft().doubleValue() / (double)10)));
                        }
                    }
                }
            }
        }

        return result;
    }

    private Map<String, List<Integer>> getInvertedList(List<String> list) {
        Map<String, List<Integer>> result = new HashMap<>(list.size());
        for (int i = 0, listASize = list.size(); i < listASize; i++) {
            String s = list.get(i);
            if (!s.equals("")) {
                String code = Caverphone2Measure.getCode(s);
                List<Integer> ref;
                if (!result.containsKey(code)) {
                    ref = new LinkedList<>();
                    result.put(code, ref);
                } else {
                    ref = result.get(code);
                }
                ref.add(i);
            }
        }
        return result;
    }

    public String getName() {
        return "caverphone2";
    }

    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

    private int getMaxDistance(double threshold) {
        return new Double(Math.floor(6 * (1 - threshold))).intValue();
    }

    static class TrieNode {

        private Map<Character, TrieNode> children;
        private List<Integer> references;

        public TrieNode(List<Integer> references) {
            this.references = references;
            this.children = new HashMap<>();
        }

        public static TrieNode recursiveAddAll(Map<String, List<Integer>> code2References) {
            TrieNode root = new TrieNode(null);
            TrieNode.recursiveAddAll(root, code2References);
            return root;
        }

        public static void recursiveAddAll(TrieNode root, Map<String, List<Integer>> code2References) {
            for (Map.Entry<String, List<Integer>> entry : code2References.entrySet())
                TrieNode.recursiveAdd(root, entry.getKey(), entry.getValue());
        }

        private static void recursiveAdd(TrieNode node, String code, List<Integer> references) {
            if (code.length() > 1) {
                TrieNode.recursiveAdd(node.addChild(code.charAt(0), null), code.substring(1), references);} else if(code.length()==1) {
                node.addChild(code.charAt(0), references);}
        }

        public TrieNode addChild(char symbol, List<Integer> references) {
            TrieNode child;
            if (!this.children.containsKey(symbol)) {
                child = new TrieNode(references);
                this.children.put(symbol, child);
            } else {
                child = this.children.get(symbol);
            }
            return child;
        }

        public List<Integer> getReferences() {
            return this.references;
        }

        public Set<Map.Entry<Character, TrieNode>> getChildren() {
            return this.children.entrySet();
        }
    }

    static class TrieSearchState {
        private int distance;
        private int position;
        private TrieNode node;

        public TrieSearchState(int distance, int position, TrieNode node) {
            this.distance = distance;
            this.position = position;
            this.node = node;
        }

        public int getDistance() {
            return distance;
        }

        public int getPosition() {
            return position;
        }

        public TrieNode getNode() {
            return node;
        }
    }

}

