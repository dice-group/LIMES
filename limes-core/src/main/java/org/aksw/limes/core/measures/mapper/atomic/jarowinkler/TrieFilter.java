package org.aksw.limes.core.measures.mapper.atomic.jarowinkler;

import org.aksw.limes.core.measures.measure.string.TrieFilterableStringMeasure;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class TrieFilter implements Runnable {

    private double threshold;
    private List<String> listA, listB;
    private List<Pair<List<String>, List<String>>> matchCandidateListPairs;
    private Map<String, Map<String, Double>> tempResult;
    private final Map<String, Map<String, Double>> result;
    private TrieFilterableStringMeasure metric;
    private TrieNode trieRoot;
    private boolean swapped;
    private int minLenInA, maxLenInA;

    public TrieFilter(Pair<List<String>, List<String>> lists, Map<String, Map<String, Double>> result,
	    TrieFilterableStringMeasure metric, double threshold) {
	this.threshold = threshold;
	this.result = result;
	this.metric = metric;
	this.matchCandidateListPairs = new LinkedList<>();
	this.tempResult = new HashMap<>();
	this.trieRoot = new TrieNode();
	this.listA = lists.getLeft();
	this.listB = lists.getRight();
	this.swapped = false;
	List<String> _swap;
	// due to certain conditions left can have larger strings than right
	// it is uncommon, but possible, so check & swap if necessary
	if (listA.get(listA.size() - 1).length() > listB.get(listB.size() - 1).length()) {
	    _swap = listA;
	    listA = listB;
	    listB = _swap;
	    swapped = true;
	}
	this.minLenInA = listA.get(0).length();
	this.maxLenInA = listA.get(listA.size() - 1).length();
    }

    @Override
    public void run() {
	// construct trie from red part
	for (String s : listA) {
	    trieRoot.addChild(s, s);
	}
	// construct a map of partitions from blue part
	Map<String, List<String>> partitions = new HashMap<>();
	for (String s : listB) {
	    char[] key = s.toCharArray();
	    Arrays.sort(key);
	    String skey = String.valueOf(key);
	    if (partitions.get(skey) == null)
		partitions.put(skey, new LinkedList<String>());
	    partitions.get(skey).add(s);
	}
	// iterate through the map
	for (String key : partitions.keySet()) {
	    trieSearch(key, partitions.get(key));
	}
	reducePairsToResultMap();
	// insert results back to mapper
	synchronized (this.result) {
	    for (String s : tempResult.keySet()) {
		if (this.result.containsKey(s)) {
		    this.result.get(s).putAll(this.tempResult.get(s));
		} else {
		    this.result.put(s, this.tempResult.get(s));
		}
	    }
	}
    }

    @SuppressWarnings("unchecked")
    private void reducePairsToResultMap() {
	double currentSim;
	HashMap<String, Double> similarityTable = new HashMap<>();
	for (Pair<List<String>, List<String>> filteredPair : matchCandidateListPairs) {
	    for (String a : swapped ? filteredPair.getRight() : filteredPair.getLeft()) {
		similarityTable.clear();
		for (String b : !swapped ? filteredPair.getRight() : filteredPair.getLeft()) {
		    currentSim = metric.proximity(a, b);
		    if (currentSim >= threshold)
			similarityTable.put(b, currentSim);
		}
		if (similarityTable.size() > 0) {
		    // noinspection unchecked
		    tempResult.put(a, (HashMap<String, Double>) (similarityTable.clone()));
		}
	    }
	}
    }

    @SuppressWarnings("unchecked")
    private void trieSearch(String b, List<String> partition) {

	List<String> matchCandidateList = new LinkedList<>();
	int bLen = b.length();
	int matches = 0;
	Stack<Character> referenceStack = new Stack<>();
	char[] charArray = b.toCharArray();
	for (int i = charArray.length - 1; i >= 0; i--) {
	    char c = charArray[i];
	    referenceStack.push(c);
	}
	Stack<MutableTriple<Stack<Character>, TrieNode, Integer>> searchStack = new Stack<>();
	// set inital algorithm stack
	for (Character key : trieRoot.children.keySet()) {
	    // noinspection unchecked
	    searchStack.add(new MutableTriple<>((Stack<Character>) referenceStack.clone(), trieRoot.children.get(key),
		    matches));
	}
	// until the stack is empty, pop and evaluate
	while (!searchStack.isEmpty()) {
	    MutableTriple<Stack<Character>, TrieNode, Integer> current = searchStack.pop();
	    int maxPossibleMatches = Math.min(current.getLeft().size(), maxLenInA - current.getMiddle().getLevel() + 1)
		    + current.getRight();
	    if (maxPossibleMatches >= metric.characterMatchLowerBound(bLen, minLenInA, threshold)) {
		int currentOrder = current.getLeft().isEmpty() ? -1
			: new Character(current.getMiddle().key).compareTo(current.getLeft().peek());
		if (currentOrder <= 0) {
		    if (currentOrder == 0) {
			current.getLeft().pop();
			current.setRight(current.getRight() + 1);
			if (current.getRight() >= metric.characterMatchLowerBound(bLen, current.getMiddle().getLevel(),
				threshold) && current.getMiddle().data != null && current.getMiddle().data.size() > 0
				&& metric.characterFrequencyUpperBound(current.getMiddle().getLevel(), b.length(),
					current.getRight()) >= threshold) {
			    matchCandidateList.addAll(current.getMiddle().data);
			}
		    }
		    for (Character key : current.getMiddle().children.keySet()) {
			// noinspection unchecked
			searchStack.push(new MutableTriple<>((Stack<Character>) current.getLeft().clone(),
				current.getMiddle().children.get(key), current.getRight().intValue()));
		    }
		} else {
		    current.getLeft().pop();
		    searchStack.push(current);
		}
	    }
	}
	if (matchCandidateList.size() > 0)
	    matchCandidateListPairs
		    .add(new MutablePair<List<String>, List<String>>(partition, new LinkedList<>(matchCandidateList)));
    }

    class TrieNode {

	public List<String> data;
	public char key;
	public TrieNode parent;
	public HashMap<Character, TrieNode> children;

	public boolean isRoot() {
	    return parent == null;
	}

	public TrieNode(char key) {
	    this.key = key;
	    this.data = null;
	    this.children = new HashMap<>();
	}

	public TrieNode() {
	    this.key = ' ';
	    this.data = null;
	    this.children = new HashMap<>();
	}

	public TrieNode addChild(String key, String data) {
	    char[] sortedKey = key.toCharArray();
	    Arrays.sort(sortedKey);
	    TrieNode currentNode = this;
	    for (char c : sortedKey) {
		if (!currentNode.children.containsKey(c)) {
		    currentNode.children.put(c, new TrieNode(c));
		    currentNode.children.get(c).parent = currentNode;
		}
		currentNode = currentNode.children.get(c);
	    }
	    if (currentNode.data == null) {
		currentNode.data = new LinkedList<>();
	    }
	    currentNode.data.add(data);
	    return currentNode;
	}

	public int getLevel() {
	    if (this.isRoot())
		return 0;
	    else
		return parent.getLevel() + 1;
	}

	@Override
	public String toString() {
	    return data != null ? data.toString() : "[data null]";
	}
    }
}
