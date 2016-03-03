/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 */
package org.aksw.limes.core.measures.mapper.atomic;

import algorithms.edjoin.Record;
import algorithms.edjoin.QGram;
import algorithms.StoppUhr;
import algorithms.edjoin.MismatchingQGram;

import java.util.ArrayList;
import java.util.HashMap;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.Mapper;
import org.apache.log4j.Logger;

import algorithms.Token;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author ngonga
 */
public class EDJoin extends Mapper {

    static Logger logger = Logger.getLogger("LIMES");
    private static int Q = -1;
    private static Mapping mapping = null;
    private static HashMap<Integer, String> sourceMap;
    private static HashMap<Integer, String> targetMap;
    @SuppressWarnings("unused")
    private int comparisons = 0;

    public String getName() {
	return "EDJoin";
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
    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression,
	    double threshold) {
	if (Q <= 1) {
	    Q = 3;
	}
	// convert similarity in distance threshold
	threshold = (1 - threshold) / threshold;

	this.comparisons = 0;
	mapping = new MemoryMapping();
	if (threshold < 0) {
	    logger.info("Wrong threshold setting. Returning empty mapping.");
	    return mapping;
	}

	// get property labels
	Parser p = new Parser(expression, threshold);

	List<String> properties = PropertyFetcher.getProperties(expression, threshold);
	// if no properties then terminate
	if (properties.get(0) == null || properties.get(1) == null) {
	    logger.fatal("Property 1 = " + properties.get(0) + ", Property 2 = " + properties.get(1));
	    logger.fatal("Property values could not be read. Exiting");
	    System.exit(1);
	}

	// if expression is not atomic terminate
	if (!p.isAtomic()) {
	    logger.fatal("Mappers can only deal with atomic expression");
	    logger.fatal("Expression " + expression + " was given to a mapper to process");
	    System.exit(1);
	}

	// 3.1 fill objects from source in entry. This is for indexing and
	// sorting
	// later on
	// logger.info("Filling objects from source knowledge base.");
	sourceMap = new HashMap<Integer, String>();
	ArrayList<String> uris = source.getAllUris();
	ArrayList<String> entries = new ArrayList<String>();
	Instance instance;
	int counter = 0;
	for (int i = 0; i < uris.size(); i++) {
	    instance = source.getInstance(uris.get(i));
	    for (String s : instance.getProperty(properties.get(0))) {
		sourceMap.put(counter, uris.get(i));
		entries.add(s);
		counter++;
	    }
	}

	// 3.2 fill objects from target in entries
	// logger.info("Filling objects from target knowledge base.");
	targetMap = new HashMap<Integer, String>();
	uris = target.getAllUris();
	for (int i = 0; i < uris.size(); i++) {
	    instance = target.getInstance(uris.get(i));
	    for (String s : instance.getProperty(properties.get(1))) {
		targetMap.put(counter, uris.get(i));
		entries.add(s);
		counter++;
	    }
	}

	// transform entries into array so that tokenizer will work
	// we will use entries later on as they do not lead to RAM errors
	String[] entryArray = new String[entries.size()];
	for (int i = 0; i < entries.size(); i++) {
	    entryArray[i] = entries.get(i);
	}

	// Begin EdJoin. First run the tokenization
	Record[] records = qTokenizer(entryArray, Q);
	HashMap<Integer, LinkedList<EdPosition>> index = new HashMap<Integer, LinkedList<EdPosition>>(); // I
	int count = 0;
	// run the core of EdJoin
	String id1, id2;
	for (int i = 0; i < records.length; i++) {
	    /*
	     * if the length of a record is smaller than q, then we cannot use
	     * the q-gram approach and have to go for comparison without q-gram
	     * filtering
	     */
	    if (records[i].qGrams.length == 0 && records[i].s.length() > 0) {
		String x = records[i].s;
		for (int j = i + 1; j < records.length; j++) {
		    if ((sourceMap.containsKey(records[i].id) && targetMap.containsKey(records[j].id))
			    || (targetMap.containsKey(records[i].id) && sourceMap.containsKey(records[j].id))) {
			String y = records[j].s;

			// length filtering
			if (Math.abs(x.length() - y.length()) <= threshold) {
			    int ed = editDistance(x, y);
			    if (ed <= threshold) {
				if ((sourceMap.containsKey(records[i].id) && targetMap.containsKey(records[j].id))) {
				    id1 = sourceMap.get(records[i].id);
				    id2 = targetMap.get(records[j].id);
				    // CORRECT
				    mapping.add(id1, id2, 1.0 / (1 + (double) ed));
				} else // should not be necessary
				{
				    // get uris for ids
				    {
					id1 = sourceMap.get(records[j].id);
					id2 = targetMap.get(records[i].id);

					// CORRECT
					mapping.add(id1, id2, 1.0 / (1 + (double) ed));
					// mapping.add(id2, id1, similarity);
				    }
				}
				count++;
			    }
			} else {
			    break;
			}
		    }
		}
	    } else {
		HashMap<Integer, Record> candidates = new HashMap<Integer, Record>(); // A
		Record currentRec = records[i]; // record x
		int prefixLength = calcPrefixLen(currentRec, (int) threshold, Q);
		for (int j = 0; j < prefixLength; j++) {
		    Integer tokenID = currentRec.qGrams[j].token.id; // w
		    Integer loc = currentRec.qGrams[j].loc; // locx
		    LinkedList<EdPosition> l = index.get(tokenID);
		    if (l != null) {
			Iterator<EdPosition> iter = l.iterator();
			while (iter.hasNext()) {
			    EdPosition pos = iter.next();
			    if (pos.record.qGrams.length >= currentRec.qGrams.length - threshold
				    && candidates.get(pos.record.id) == null) {
				if (Math.abs(loc - pos.EdPosition) <= threshold) {
				    candidates.put(pos.record.id, pos.record);
				}
			    }
			}
			l.add(new EdPosition(currentRec, loc)); // index the
								// current
								// prefix
		    } else {
			l = new LinkedList<EdPosition>();
			l.add(new EdPosition(currentRec, loc));
			index.put(tokenID, l);
		    }
		}
		if (candidates.size() > 0) {
		    count = count + verification(currentRec, candidates, entries, Q, (int) threshold);
		}
	    }
	}

	return mapping;
    }

    private static Record[] qTokenizer(String[] objects, int q) {
	StoppUhr s = new StoppUhr();
	s.Starten();
	HashMap<String, Token> allTokens = new HashMap<String, Token>();
	Record[] records = new Record[objects.length];

	for (int i = 0; i < objects.length; i++) {

	    int qGramsNumber = objects[i].length() - q + 1;
	    Record record = new Record(i, Math.max(0, qGramsNumber), objects[i]);

	    for (int j = 0; j < qGramsNumber; j++) {
		String token = objects[i].substring(j, j + q);
		if (allTokens.containsKey(token)) {
		    Token t = allTokens.get(token);
		    t.df++;
		    record.qGrams[j] = new QGram(t, j);
		} else {
		    Token t = new Token(allTokens.size(), 1);
		    allTokens.put(token, t);
		    record.qGrams[j] = new QGram(t, j);
		}
	    }
	    records[i] = record;
	}

	for (int i = 0; i < records.length; i++) {
	    Arrays.sort(records[i].qGrams);
	}
	Arrays.sort(records);
	s.Stoppen();
	return records;
    }

    private static int verification(Record currentRec, HashMap<Integer, Record> candidates, ArrayList<String> objects,
	    int q, int threshold) {
	int count = 0;
	String id1, id2;
	Iterator<Record> iter = candidates.values().iterator();
	while (iter.hasNext()) {
	    Record y = iter.next();
	    compareQGramsResult compResult = compareQGrams(currentRec, y, threshold);

	    /*
	     * count filtering
	     */
	    if (compResult.e1 <= q * threshold) {
		int e2 = minEditErrors(compResult.Q, q);
		/*
		 * location-based mismatch filtering
		 */
		if (e2 <= threshold) {
		    int e3 = contentFilter(objects.get(currentRec.id), objects.get(y.id), compResult.Q, threshold, q);
		    if (e3 <= 2 * threshold) {
			int ed = editDistance(objects.get(currentRec.id), objects.get(y.id));
			if (ed <= threshold) {
			    if ((sourceMap.containsKey(currentRec.id) && targetMap.containsKey(y.id))) {
				id1 = sourceMap.get(currentRec.id);
				id2 = targetMap.get(y.id);
				// CORRECT
				mapping.add(id1, id2, 1.0 / (1 + (double) ed));
			    } else if (targetMap.containsKey(currentRec.id) && sourceMap.containsKey(y.id)) {
				// get uris for ids
				{
				    id1 = sourceMap.get(y.id);
				    id2 = targetMap.get(currentRec.id);
				    // CORRECT
				    mapping.add(id1, id2, 1.0 / (1 + (double) ed));
				}
				count++;
			    }
			}
		    }
		}
	    }
	}
	return count;
    }

    private static int calcPrefixLen(Record x, int tau, int q) {
	if (x.qGrams.length <= tau + 1) {
	    return x.qGrams.length;
	}
	int left = tau + 1;
	int right;
	if (x.qGrams.length < q * tau + 1) {
	    right = x.qGrams.length;
	} else {
	    right = q * tau + 1;
	}
	int mid;
	while (left < right) {
	    mid = (left + right) / 2;
	    MismatchingQGram[] temp = new MismatchingQGram[mid];
	    for (int i = 0; i < mid; i++) {
		temp[i] = new MismatchingQGram(x.qGrams[i]);
	    }
	    int err = minEditErrors(temp, q);
	    if (err <= tau) {
		left = mid + 1;
	    } else {
		right = mid;
	    }
	}
	return left;
    }

    private static compareQGramsResult compareQGrams(Record x, Record y, int tau) {
	LinkedList<MismatchingQGram> mismatchingQgrams = new LinkedList<MismatchingQGram>();
	QGram[] xQGrams = x.qGrams;
	QGram[] yQGrams = y.qGrams;
	int i = 0;
	int j = 0;
	int epsilon = 0;
	while (i < xQGrams.length && j < yQGrams.length) {
	    if (xQGrams[i].token.id == yQGrams[j].token.id) {
		if (Math.abs(xQGrams[i].loc - yQGrams[j].loc) <= tau) {
		    i++;
		    j++;
		} else {
		    if (xQGrams[i].loc < yQGrams[j].loc) {
			if (i == 0 || xQGrams[i].token.id != xQGrams[i - 1].token.id || j == 0
				|| xQGrams[i].token.id != yQGrams[j - 1].token.id
				|| Math.abs(xQGrams[i].loc - yQGrams[j - 1].loc) > tau) {
			    mismatchingQgrams.add(new MismatchingQGram(xQGrams[i]));
			}
			epsilon++;
			i++;
		    } else {
			j++;
		    }
		}
	    } else {
		if (xQGrams[i].compareTo(yQGrams[j]) < 0) { // <-- Fehler in
							    // Pseudocode
		    if (i == 0 || xQGrams[i].token.id != xQGrams[i - 1].token.id || j == 0
			    || xQGrams[i].token.id != yQGrams[j - 1].token.id
			    || Math.abs(xQGrams[i].loc - yQGrams[j - 1].loc) > tau) {
			mismatchingQgrams.add(new MismatchingQGram(xQGrams[i]));
		    }
		    epsilon++;
		    i++;
		} else {
		    j++;
		}
	    }
	}
	while (i < xQGrams.length) {
	    if (i == 0 || xQGrams[i].token.id != xQGrams[i - 1].token.id || j == 0
		    || xQGrams[i].token.id != yQGrams[j - 1].token.id
		    || Math.abs(xQGrams[i].loc - yQGrams[j - 1].loc) > tau) {
		mismatchingQgrams.add(new MismatchingQGram(xQGrams[i]));
	    }
	    epsilon++;
	    i++;
	}
	return new compareQGramsResult(mismatchingQgrams.toArray(new MismatchingQGram[mismatchingQgrams.size()]),
		epsilon);
    }

    private static int minEditErrors(MismatchingQGram[] Q, int q) {
	Arrays.sort(Q);
	int cnt = 0;
	int loc = 0;
	for (int i = 0; i < Q.length; i++) {
	    if (Q[i].loc > loc) {
		cnt++;
		loc = Q[i].loc + q - 1;
	    }
	}
	return cnt;
    }

    private static int contentFilter(String x, String y, MismatchingQGram[] Q, int tau, int q) {
	if (Q.length == 0) {
	    return 0;
	}
	// Build a condensed suffix sum list for Q;
	LinkedList<SuffixSumListEntry> condensedSuffixSumList = new LinkedList<SuffixSumListEntry>();
	int cnt = 1;
	int loc = Q[Q.length - 1].loc + 1;
	for (int i = Q.length - 1; i >= 0; i--) {
	    if (Q[i].loc <= loc) {
		condensedSuffixSumList.addFirst(new SuffixSumListEntry(Q[i].loc, cnt++));
		loc = Q[i].loc - q;
	    }
	}

	Iterator<SuffixSumListEntry> iter = condensedSuffixSumList.iterator();
	int j = 0;
	int i = 1;
	int epsilon;
	while (i < Q.length) {
	    if (Q[i].loc - Q[i - 1].loc > 1) {
		epsilon = L1Distance(x, y, Q[j].loc, Q[i - 1].loc + q - 1) + sumRightErrs(Q[i - 1].loc + q, iter);
		if (epsilon > 2 * tau) {
		    return 2 * tau + 1;
		}
		j = i;
	    }
	    i++;
	}
	return L1Distance(x, y, Q[j].loc, Q[i - 1].loc + q - 1) + sumRightErrs(Q[i - 1].loc + q, iter);
    }

    private static int L1Distance(String x, String y, int lo, int hi) {
	HashMap<Character, Frequency> frequencyTable = new HashMap<Character, Frequency>();
	char c;
	Frequency f;
	int i;
	for (i = lo; i <= hi; i++) {
	    c = x.charAt(i);
	    if ((f = frequencyTable.get(c)) != null) {
		f.value++;
	    } else {
		frequencyTable.put(c, new Frequency(1));
	    }
	}

	for (i = lo; i < y.length() && i <= hi; i++) {
	    c = y.charAt(i);
	    if ((f = frequencyTable.get(c)) != null) {
		f.value--;
	    } else {
		frequencyTable.put(c, new Frequency(-1));
	    }
	}

	Iterator<Frequency> iter = frequencyTable.values().iterator();
	int L1 = 0;
	while (iter.hasNext()) {
	    L1 += Math.abs(iter.next().value);
	}
	return L1;
    }

    private static int sumRightErrs(int loc, Iterator<SuffixSumListEntry> iter) {
	SuffixSumListEntry entry;
	while (iter.hasNext()) {
	    entry = iter.next();
	    if (entry.loc >= loc) {
		return entry.errors;
	    }
	}
	return 0;
    }

    /**
     * Berechnet die Edit-Distanz zwischen zwei Zeichenketten.
     *
     * @param x
     *            erste Zeichenkette
     * @param y
     *            zweite Zeichenkette
     * @return Edit-Distanz
     */
    public static int editDistance(String x, String y) {
	int d[][];
	int n, m, i, j, cost;

	n = x.length();
	m = y.length();
	if (n == 0) {
	    return m;
	}

	if (m == 0) {
	    return n;
	}

	d = new int[n + 1][m + 1];

	for (i = 0; i <= n; i++) {
	    d[i][0] = i;
	}

	for (j = 0; j <= m; j++) {
	    d[0][j] = j;
	}

	for (i = 1; i <= n; i++) {
	    for (j = 1; j <= m; j++) {
		if (x.charAt(i - 1) == y.charAt(j - 1)) {
		    cost = 0;
		} else {
		    cost = 1;
		}

		d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
	    }
	}
	return d[n][m];
    }

    private static int min(int a, int b, int c) {
	int min;
	min = a;
	if (b < min) {
	    min = b;
	}
	if (c < min) {
	    min = c;
	}
	return min;
    }

    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	if (language.equals(Language.DE)) {
	    return 20.06 + 0.0064 * sourceSize + 0.0052 * targetSize - 22.65 * theta;
	} // result of random string
	else {
	    return 30.5 + 0.009 * (sourceSize + targetSize) - 57.8 * theta;
	}
    }

    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	if (language.equals(Language.DE)) {
	    return 0.69 + 0.005 * sourceSize + 0.005 * targetSize - 22.58 * theta;
	} // result of random string
	else {
	    return 0.008 * (sourceSize + targetSize) - 67.7 * theta;
	    // return 11.64 + 0.0014 * sourceSize + 0.0014 * targetSize - 22.42
	    // * theta;
	}
    }

    public double getSelectivity(int sourceSize, int targetSize, double threshold, Language language) {
	return getMappingSizeApproximation(sourceSize, targetSize, threshold, language)
		/ (double) (sourceSize * targetSize);
    }
}

class compareQGramsResult {

    MismatchingQGram[] Q = null;
    int e1 = 0;

    compareQGramsResult(MismatchingQGram[] qGrams, int e) {
	this.Q = qGrams;
	this.e1 = e;
    }
}

class SuffixSumListEntry {

    int loc = -1;
    int errors = -1;

    public SuffixSumListEntry(int l, int e) {
	loc = l;
	errors = e;
    }
}

class Frequency {

    int value = 0;

    public Frequency(int freq) {
	value = freq;
    }
}

class EdPosition {

    Record record = null;
    int EdPosition = -1;

    public EdPosition(Record record, int EdPosition) {
	this.record = record;
	this.EdPosition = EdPosition;
    }
}
