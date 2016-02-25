package org.aksw.limes.core.measures.mapper.atomic;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.Mapper;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.log4j.Logger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Kevin Dreßler
 */
public class SoundexMapper extends Mapper {

    static final int codeLength = 6;

    static class TrieNode {

	private Map<Character, TrieNode> children;
	private List<Integer> references;

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
	    if (code.length() > 1)
		TrieNode.recursiveAdd(node.addChild(code.charAt(0), null), code.substring(1), references);
	    else
		node.addChild(code.charAt(0), references);
	}

	public TrieNode(List<Integer> references) {
	    this.references = references;
	    this.children = new HashMap<>();
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

    static Logger logger = Logger.getLogger("LIMES");

    private Map<String, Set<String>> getValueToUriMap(Cache c, String property) {
	Map<String, Set<String>> result = new HashMap<String, Set<String>>();
	List<String> uris = c.getAllUris();
	for (String uri : uris) {
	    Set<String> values = c.getInstance(uri).getProperty(property);
	    for (String value : values) {
		if (!result.containsKey(value)) {
		    result.put(value, new HashSet<String>());
		}
		result.get(value).add(uri);
	    }
	}
	return result;
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
    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression,
	    double threshold) {

	logger.info("Running SoundexMapper with code length " + String.valueOf(codeLength) + "\n Example: "
		+ getCode("Beispielswortwelcheslangist"));

	List<String> listA, listB;
	Map<String, List<Integer>> invListA, invListB;
	List<String> properties = PropertyFetcher.getProperties(expression, threshold);
	Map<String, Set<String>> sourceMap = getValueToUriMap(source, properties.get(0));
	Map<String, Set<String>> targetMap = getValueToUriMap(target, properties.get(1));
	listA = new ArrayList<String>(sourceMap.keySet());
	listB = new ArrayList<String>(targetMap.keySet());
	invListA = new HashMap<>();
	invListB = new HashMap<>();
	// create inverted lists (code=>index of original list)
	for (int i = 0, listASize = listA.size(); i < listASize; i++) {
	    String s = listA.get(i);
	    String code = getCode(s);
	    List<Integer> ref;
	    if (!invListA.containsKey(code)) {
		ref = new LinkedList<Integer>();
		invListA.put(code, ref);
	    } else {
		ref = invListA.get(code);
	    }
	    ref.add(i);
	}
	for (int i = 0, listBSize = listB.size(); i < listBSize; i++) {
	    String s = listB.get(i);
	    String code = getCode(s);
	    List<Integer> ref;
	    if (!invListB.containsKey(code)) {
		ref = new LinkedList<Integer>();
		invListB.put(code, ref);
	    } else {
		ref = invListB.get(code);
	    }
	    ref.add(i);
	}
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
	Mapping result = new MemoryMapping();
	while (!similarityBook.isEmpty()) {
	    Triple<Integer, List<Integer>, List<Integer>> t = similarityBook.pop();
	    for (Integer i : t.getMiddle()) {
		String a = listA.get(i);
		for (Integer j : t.getRight()) {
		    String b = listB.get(j);
		    for (String sourceUri : sourceMap.get(a)) {
			for (String targetUri : targetMap.get(b)) {
			    result.add(sourceUri, targetUri,
				    (1.0d - (t.getLeft().doubleValue() / (double) codeLength)));
			}
		    }
		}
	    }
	}
	return result;
    }

    private int getCode(char x) {
	switch (x) {
	case 'B':
	case 'F':
	case 'P':
	case 'V':
	    return 1;
	case 'C':
	case 'G':
	case 'J':
	case 'K':
	case 'Q':
	case 'S':
	case 'X':
	case 'Z':
	    return 2;
	case 'D':
	case 'T':
	    return 3;
	case 'L':
	    return 4;
	case 'M':
	case 'N':
	    return 5;
	case 'R':
	    return 6;
	default:
	    return -1;
	}
    }

    private String getCode(String string) {
	char[] in = string.toUpperCase().toCharArray();
	char[] out = new char[codeLength];
	int i = 0;
	int j = 0;
	while (i < in.length && j < codeLength) {
	    if (in[i] != 'A' && in[i] != 'E' && in[i] != 'I' && in[i] != 'O' && in[i] != 'U' && in[i] != 'Y'
		    && in[i] != 'H' && in[i] != 'W') {
		// consonants are added to output
		if (j == 0) {
		    out[j] = in[i];
		    j++;
		} else {
		    int t = getCode(in[i]);
		    if (t > 0) {
			out[j] = String.valueOf(t).charAt(0);
			j++;
		    }
		}
		// double consonants are skipped
		if (i < in.length - 1 && in[i] == in[i + 1])
		    i++;
		// double consonants with 'h' or 'w' inbetween are skipped too
		else if (i < in.length - 2 && in[i] == in[i + 2] && (in[i + 1] == 'H' || in[i + 1] == 'W'))
		    i += 2;
	    }
	    i++;
	}
	while (j < codeLength) {
	    out[j] = '0';
	    j++;
	}
	return String.valueOf(out);
    };

    public String getName() {
	return "soundex";
    }

    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return 1000d;
    }

    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return 1000d;
    }

    private int getMaxDistance(double threshold) {
	return new Double(Math.floor(codeLength * (1 - threshold))).intValue();
    };

}
