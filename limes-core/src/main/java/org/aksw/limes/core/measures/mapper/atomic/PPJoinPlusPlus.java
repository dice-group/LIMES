/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.atomic;

/**
 *
 * @author ngonga
 */
import algorithms.ppjoinplus.Record;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;

import algorithms.StoppUhr;
import algorithms.Token;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.io.parser.Parser;

import java.util.ArrayList;

import org.aksw.limes.core.measures.mapper.Mapper;
import org.aksw.limes.core.measures.measure.Measure;
import org.aksw.limes.core.measures.measure.date.DayMeasure;
import org.aksw.limes.core.measures.measure.date.SimpleDateMeasure;
import org.aksw.limes.core.measures.measure.date.YearMeasure;
import org.aksw.limes.core.measures.measure.string.CosineMeasure;
import org.aksw.limes.core.measures.measure.string.IStringMeasure;
import org.aksw.limes.core.measures.measure.string.JaccardMeasure;
import org.aksw.limes.core.measures.measure.string.TrigramMeasure;
import org.apache.log4j.Logger;

class Position {

    Record record = null;
    int position = -1;

    public Position(Record record, int position) {
	this.record = record;
	this.position = position;
    }
}

class CandidateInfo {

    int currentOverlap = -1;
    int alpha = -1;

    public CandidateInfo(int currentOverlap, int alpha) {
	this.currentOverlap = currentOverlap;
	this.alpha = alpha;
    }
}

class PartitionResult {

    int l = -1;
    int r = -1;
    int f = -1;
    int diff = -1;

    /**
     *
     * @param l
     *            right bound of the left partition
     * @param r
     *            left bound of the right partition
     * @param f
     *            a flag indicating whether the token w is in the searching
     *            range
     * @param diff
     *            a flag indicating whether the probing token w is not found in
     *            a record
     */
    public PartitionResult(int l, int r, int f, int diff) {
	this.l = l;
	this.r = r;
	this.f = f;
	this.diff = diff;
    }
}

/**
 * Die Implementierung von PPJoin+ Algorithmus. Der Algorithmus bestimmt alle
 * Paare von Objekten, deren Ähnlichkeit aufgrund des verwendeten
 * Ähnlichkeitsmaßes über einem angegebenen Schwellwert (threshold) liegt. Der
 * Prozess der Duplikaterkennung mit PPJoin+ kann in drei Phasen gegliedert
 * werden. Das sind: die Tokenisierung der Eingabe (tokenizer), die Generierung
 * von Kandidatenpaaren und ihre Verifikation. Um nicht alle Objekte miteinander
 * vergleichen zu müssen, werden bei der Kandidatengenerierung drei
 * Filterstrategien eingesetzt: Präfix-Filterung (prefix filtering),
 * positionelle Filterung (positional filtering) und Suffix-Filterung (suffix
 * filtering). Sie reduzieren die Anzahl der Kandidaten, die schließlich
 * miteinander verglichen werden müssen. Diese Implementierung des Algorithmus
 * unterstützt drei Ähnlichkeitsmaße: Jaccard-, Cosine- und Trigram-Ähnlichkeit.
 * Siehe
 * <a href="http://www.cse.unsw.edu.au/~weiw/files/WWW08-PPJoin-Final.pdf">
 * detaillierte Beschreibung von PPJoin+</a>.
 *
 * @author Dawid Kotlarz
 * @version 1.0
 */
public class PPJoinPlusPlus extends Mapper {

    static Logger logger = Logger.getLogger("LIMES");
    private static int MAX_DEPTH;
    private static Mapping mapping = null;
    private static HashMap<Integer, String> sourceMap;
    private static HashMap<Integer, String> targetMap;
    private IStringMeasure measure;

    public String getName() {
	return "PPJoinPlusPlus";
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
	MAX_DEPTH = 2;
	mapping = new MemoryMapping();
	// logger.info("Starting PPJoinPlus");
	if (threshold < 0) {
	    throw new RuntimeException("Verification threshold must be >= 0");
	    // logger.info("Wrong threshold setting. Returning empty mapping.");
	    // return mapping;
	}
	String property1 = null, property2 = null;
	// get property labels
	Parser p = new Parser(expression, threshold);

	// get first property label
	String term1 = "?" + p.getTerm1();
	String term2 = "?" + p.getTerm2();
	String split[];
	String var;

	String property = "";
	if (term1.contains(".")) {
	    split = term1.split("\\.");
	    var = split[0];
	    property = split[1];
	    if (split.length >= 2) {
		for (int i = 2; i < split.length; i++) {
		    property = property + "." + split[i];
		}
	    }
	    if (var.equals(sourceVar)) {
		// property1 = split[1];
		property1 = property;
	    } else {
		// property2 = split[1];
		property2 = property;
	    }
	} else {
	    property1 = term1;
	}

	// get second property label
	if (term2.contains(".")) {
	    split = term2.split("\\.");
	    var = split[0];
	    property = split[1];
	    if (split.length >= 2) {
		for (int i = 2; i < split.length; i++) {
		    property = property + "." + split[i];
		}
	    }
	    if (var.equals(sourceVar)) {
		// property1 = split[1];
		property1 = property;
	    } else {
		// property2 = split[1];
		property2 = property;
	    }
	} else {
	    property2 = term2;
	}
	// if no properties then terminate
	if (property1 == null || property2 == null) {
	    logger.fatal("Property 1 = " + property1 + ", Property 2 = " + property2);
	    logger.fatal("Property values could not be read. Exiting");
	    // System.exit(1);
	}

	if (!p.isAtomic()) {
	    logger.fatal("Mappers can only deal with atomic expression");
	    logger.fatal("Expression " + expression + " was given to a mapper to process");
	    // System.exit(1);
	}

	// 3.1 fill objects from source in entry
	// logger.info("Filling objects from source knowledge base.");
	sourceMap = new HashMap<Integer, String>();
	ArrayList<String> uris = source.getAllUris();
	ArrayList<String> entries = new ArrayList<String>();
	Instance instance;
	int counter = 0;
	for (int i = 0; i < uris.size(); i++) {
	    instance = source.getInstance(uris.get(i));
	    for (String s : instance.getProperty(property1)) {
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
	    for (String s : instance.getProperty(property2)) {
		targetMap.put(counter, uris.get(i));
		entries.add(s);
		counter++;
	    }
	}

	String[] entryArray = new String[entries.size()];
	for (int i = 0; i < entries.size(); i++) {
	    entryArray[i] = entries.get(i);
	}

	Record[] records = tokenizer(entryArray);
	HashMap<Integer, LinkedList<Position>> index = new HashMap<Integer, LinkedList<Position>>(); // I

	if (threshold == 0) {
	} else {
	}

	measure = (IStringMeasure) this.getMeasure(p.getOperator());
	// logger.info("Beginninng comparison per se");
	if (measure != null) {
	    // logger.info("Using measure " + measure.getName());
	} else {
	    logger.fatal("Metric is null. Exiting.");
	    System.exit(1);
	}
	for (int i = 0; i < records.length; i++) {
	    HashMap<Record, CandidateInfo> candidates = new HashMap<Record, CandidateInfo>(); // A
	    Record currentRec = records[i]; // record x
	    int tokensNumber = currentRec.tokens.length; // |x|

	    currentRec.prefixLength = measure.getPrefixLength(tokensNumber, threshold);
	    currentRec.midPrefix = measure.getMidLength(tokensNumber, threshold);

	    for (int j = 0; j < currentRec.tokens.length && j < currentRec.prefixLength; j++) {
		Integer tokenID = currentRec.tokens[j].id;
		LinkedList<Position> l = index.get(tokenID);
		if (l != null) {
		    Iterator<Position> iter = l.iterator();
		    while (iter.hasNext()) {
			Position pos = iter.next();
			double sizeFilteringThreshold = measure.getSizeFilteringThreshold(tokensNumber, threshold);

			int tokensNumber2 = pos.record.tokens.length; // |y|
			if (tokensNumber2 >= sizeFilteringThreshold) { // size
								       // filtering
								       // on |y|
			    int alpha = measure.getAlpha(tokensNumber, tokensNumber2, threshold);
			    int ubound = 1 + Math.min(tokensNumber - j - 1, tokensNumber2 - pos.position - 1);

			    CandidateInfo cf = candidates.get(pos.record);
			    if (cf == null) {
				if (ubound >= alpha) {
				    // differs from paper because count starts
				    // at 0 not 1
				    int H_max = tokensNumber + tokensNumber2 - 2 * alpha - j - pos.position;
				    int H = suffixFilter(currentRec, j + 1, currentRec.tokens.length - 1, pos.record,
					    pos.position + 1, pos.record.tokens.length - 1, H_max, 1);
				    if (H <= H_max) {
					candidates.put(pos.record, new CandidateInfo(1, alpha));
				    } else {
					candidates.put(pos.record, new CandidateInfo(Integer.MIN_VALUE, alpha));
				    }
				}
			    } else {
				if (cf.currentOverlap + ubound >= alpha) {
				    if (cf.currentOverlap == 0) {
					int H_max = tokensNumber + tokensNumber2 - 2 * alpha - j - pos.position;
					int H = suffixFilter(currentRec, j + 1, currentRec.tokens.length - 1,
						pos.record, pos.position + 1, pos.record.tokens.length - 1, H_max, 1);
					if (H <= H_max) {
					    cf.currentOverlap++; // a++;
					} else {
					    cf.currentOverlap = Integer.MIN_VALUE;
					}
				    } else {
					cf.currentOverlap++; // a++;
				    }
				} else {
				    cf.currentOverlap = 0; // prune candidate
				}
			    }
			} else {
			    iter.remove();
			}
		    }

		    if (j < currentRec.midPrefix) {
			l.add(new Position(currentRec, j)); // index the current
							    // prefix
		    }
		} else {
		    if (j < currentRec.midPrefix) {
			LinkedList<Position> temp = new LinkedList<Position>();
			temp.add(new Position(currentRec, j));
			index.put(tokenID, temp);
		    }
		}
	    }
	    verification(currentRec, candidates);
	}
	// logger.info("Mapping carried out using " + comparisons + "
	// comparisons.");
	return mapping;
    }

    private int verification(Record currentRec, HashMap<Record, CandidateInfo> candidates) {
	int count = 0;
	String id1, id2;

	for (@SuppressWarnings("rawtypes")
	Map.Entry e : candidates.entrySet()) {
	    CandidateInfo value = (CandidateInfo) e.getValue();
	    if (value.currentOverlap > 0) {
		Record key = (Record) e.getKey();
		int overlap = value.currentOverlap;
		Token wx = currentRec.tokens[currentRec.prefixLength - 1];
		// Token wy = key.tokens[key.prefixLength - 1];
		Token wy = key.tokens[key.midPrefix - 1];
		int compRes = wx.compareTo(wy);
		if (compRes < 0) {
		    int ubound = value.currentOverlap + currentRec.tokens.length - currentRec.prefixLength;
		    if (ubound >= value.alpha) {
			overlap += overlap(currentRec, currentRec.prefixLength, key, value.currentOverlap);
		    }
		} else if (compRes > 0) {
		    int ubound = value.currentOverlap + key.tokens.length
			    - /*
			       * key.prefixLength
			       */ key.midPrefix;
		    if (ubound >= value.alpha) {
			overlap += overlap(currentRec, value.currentOverlap, key,
				/*
				 * key.prefixLength
				 */ key.midPrefix);
		    }
		} else { // Fehler in Pseudocode; dieser Fall falsch behandelt
			 // --> Duplikate fehlen!
		    int ubound = value.currentOverlap + Math.min(currentRec.tokens.length - currentRec.prefixLength,
			    key.tokens.length - /*
						 * key.prefixLength
						 */ key.midPrefix);
		    if (ubound >= value.alpha) {
			overlap += overlap(currentRec, currentRec.prefixLength, key,
				/*
				 * key.prefixLength
				 */ key.midPrefix);
		    }
		}
		if (overlap >= value.alpha) {
		    double similarity = measure.getSimilarity(overlap, currentRec.tokens.length, key.tokens.length);
		    // use border here instead. faster!
		    if ((sourceMap.containsKey(currentRec.id) && targetMap.containsKey(key.id))) {
			id1 = sourceMap.get(currentRec.id);
			id2 = targetMap.get(key.id);
			// CORRECT
			mapping.add(id1, id2, similarity);
			// mapping.add(id2, id1, similarity);
		    } else if (targetMap.containsKey(currentRec.id) && sourceMap.containsKey(key.id)) {
			// get uris for ids
			{
			    id1 = sourceMap.get(key.id);
			    id2 = targetMap.get(currentRec.id);

			    // CORRECT
			    mapping.add(id1, id2, similarity);
			    // mapping.add(id2, id1, similarity);
			}
			count++;
		    }
		}
		// count++;
	    }
	}
	return count;
    }

    /**
     * Returns measure that has PPJoinPlusPlus as mapper.
     *
     * @param name
     *            Name of the measure
     *            
     * @return Similarity measure of the given type
     */
    public Measure getMeasure(String name) {
	if (name.toLowerCase().startsWith("cosine")) {
	    return new CosineMeasure();
	} else if (name.toLowerCase().startsWith("jaccard")) {
	    return new JaccardMeasure();
	} else if (name.toLowerCase().startsWith("datesim")) {
	    return new SimpleDateMeasure();
	} else if (name.toLowerCase().startsWith("daysim")) {
	    return new DayMeasure();
	} else if (name.toLowerCase().startsWith("yearsim")) {
	    return new YearMeasure();
	} 
	//defaults: trigrams, overlap
	return new TrigramMeasure();
    }

    /**
     * Berechnet die Überlappung zwischen zwei Datensätzen mithilfe ihrer Tokens
     *
     * @param x
     *            erster Datensatz
     * @param beginnX
     *            Position des Anfangstokens vom ersten Datensatz
     * @param y
     *            zweiter Datensatz
     * @param beginnY
     *            Position des Anfangstokens vom zweiten Datensatz
     * @return Überlappung von x und y
     */
    public static int overlap(Record x, int beginnX, Record y, int beginnY) {
	int overlap = 0;
	for (int i = beginnX; i < x.tokens.length; i++) {

	    for (int j = beginnY; j < y.tokens.length; j++) {
		if (x.tokens[i].id == y.tokens[j].id) {
		    overlap++;
		    beginnY = j + 1;
		    break;
		}
	    }
	}
	return overlap;
    }

    private static int suffixFilter(Record x, int xBeginn, int xEnd, Record y, int yBeginn, int yEnd, int H_max,
	    int depth) {
	int xSize = xEnd - xBeginn + 1;
	int ySize = yEnd - yBeginn + 1;
	if (depth > MAX_DEPTH) {
	    return Math.abs(xSize - ySize);
	}
	if (ySize <= 0 || xSize <= 0) { // für |y|=0 ist mid=-1
	    return Math.max(Math.max(ySize, xSize), 0);
	}
	int mid = yBeginn + (int) Math.ceil(1.0 * ySize / 2) - 1; // index from
								  // 0 -> -1
	// int mid = (yBeginn + yEnd) / 2;
	Token w = y.tokens[mid];

	/*
	 * ---funktioniert nicht immer korrekt
	 * (java.lang.ArrayIndexOutOfBoundsException bei partition)---
	 *
	 * int o = (H_max - Math.abs( xSize - ySize)) / 2; //always divisible
	 * int ol, or; if( xSize < ySize){ ol = 1; or = 0; }else{ ol = 0; or =
	 * 1; } PartitionResult pr = partition( x, w, mid - o - Math.abs( xSize
	 * - ySize) * ol, mid + o + Math.abs( xSize - ySize) * or);
	 * /*-------------------------------
	 */
	PartitionResult pr = partition(x, w, xBeginn, xEnd);

	/*
	 * ---nicht nötig--- if( pr.f == 0){ return H_max + 1; }
	 * -------------------
	 */
	int xlSize = pr.l - xBeginn + 1;
	int xrSize = xEnd - pr.r + 1;
	int ylSize = mid - yBeginn;
	int yrSize = yEnd - mid;
	int H = Math.abs(xlSize - ylSize) + Math.abs(xrSize - yrSize) + pr.diff;
	if (H > H_max) {
	    return H;
	} else {
	    int Hl = suffixFilter(x, xBeginn, pr.l, y, yBeginn, mid - 1, H_max - Math.abs(xrSize - yrSize) - pr.diff,
		    depth + 1);
	    H = Hl + Math.abs(xrSize - yrSize) + pr.diff;
	    if (H <= H_max) {
		int Hr = suffixFilter(x, pr.r, xEnd, y, mid + 1, yEnd, H_max - Hl - pr.diff, depth + 1);
		return Hl + Hr + pr.diff;
	    } else {
		return H;
	    }
	}
    }

    private static PartitionResult partition(Record s, Token w, int l, int r) {
	/*
	 * ---funktioniert nicht immer korrekt--- if( s.tokens[l].compareTo(w) >
	 * 0 || s.tokens[r].compareTo(w) < 0){ return new PartitionResult( -1,
	 * -1, 0, 1); } /*------------------------------
	 */

	if (s.tokens[l].compareTo(w) > 0) {
	    return new PartitionResult(l - 1, l, 1, 1);
	}

	if (s.tokens[r].compareTo(w) < 0) {
	    return new PartitionResult(r, r + 1, 1, 1);
	}

	int p = binarySearch(s, l, r, w);

	if (s.tokens[p].compareTo(w) == 0) {
	    return new PartitionResult(p - 1, p + 1, 1, 0); // skip the token w
	} else {
	    return new PartitionResult(p - 1, p, 1, 1);
	}
    }

    /**
     * Binary search for the position of the first token in x that is no smaller
     * than w in the global ordering within x.tokens[l..r].
     *
     * @param x
     *            a record
     * @param l
     *            the left bound of searching range
     * @param r
     *            the right bound of searching range
     * @param w
     *            a token
     * @return the position of the first token in x that is no smaller than w
     */
    private static int binarySearch(Record x, int l, int r, Token w) {
	int p = (l + r) / 2;
	int c = x.tokens[p].compareTo(w);
	if (c == 0) {
	    return (p);
	}

	if (l == r) {
	    if (c < 0) {
		return (p + 1);
	    } else {
		return p;
	    }
	}
	if (c < 0) {
	    if (p < r) {
		return (binarySearch(x, p + 1, r, w));
	    } else {
		return p + 1;
	    }
	} else {
	    if (p > l) {
		return (binarySearch(x, l, p - 1, w));
	    } else {
		return p;
	    }
	}
    }

    private static Record[] tokenizer(String[] objects) {
	StoppUhr s = new StoppUhr();
	s.Starten();
	HashMap<String, Token> allTokens = new HashMap<String, Token>();
	Record[] records = new Record[objects.length];

	for (int i = 0; i < objects.length; i++) {

	    StringTokenizer st = new StringTokenizer(objects[i], " .,?!\t");
	    int tokensNumber = st.countTokens();
	    Record record = new Record(i, tokensNumber);
	    // HasTable of tokens in the record <Sting> and their record
	    // frequency <Integer>
	    HashMap<String, Integer> recordTokens = new HashMap<String, Integer>();

	    for (int j = 0; j < tokensNumber; j++) {
		String token = st.nextToken();
		if (recordTokens.containsKey(token)) {
		    Integer token_freq = recordTokens.get(token).intValue() + 1;
		    recordTokens.put(token, token_freq);
		    if (allTokens.containsKey(token + "." + token_freq)) { // character
									   // '.'
									   // is
									   // not
									   // allowed
									   // in
									   // tokens
			Token t = allTokens.get(token + "." + token_freq);
			t.df++;
			record.tokens[j] = t;
		    } else {
			Token t = new Token(allTokens.size(), 1);
			allTokens.put(token + "." + token_freq, t);
			record.tokens[j] = t;
		    }
		} else {
		    if (allTokens.containsKey(token)) {
			Token t = allTokens.get(token);
			t.df++;
			recordTokens.put(token, new Integer(1));
			record.tokens[j] = t;
		    } else {
			Token t = new Token(allTokens.size(), 1);
			allTokens.put(token, t);
			recordTokens.put(token, new Integer(1));
			record.tokens[j] = t;
		    }
		}
	    }
	    records[i] = record;
	}
	for (int i = 0; i < records.length; i++) {
	    Arrays.sort(records[i].tokens);
	}
	Arrays.sort(records);
	s.Stoppen();
	// logger.info("Tokenizing carried out in " + s.Laufzeit() + "ms.");
	return records;
    }

    public double getRuntimeApproximation(int sourceSize, int targetSize, double threshold, Language language) {
	if (language.equals(Language.DE)) {
	    // error = 667.22
	    return 16.27 + 5.1 * sourceSize + 4.9 * targetSize - 23.44 * threshold;
	} else {
	    // error = 5.45
	    return 0.62 + 0.001 * sourceSize + 0.001 * targetSize - 0.53 * threshold;
	}
    }

    public double getMappingSizeApproximation(int sourceSize, int targetSize, double threshold, Language language) {
	if (language.equals(Language.DE)) {
	    // error = 667.22
	    return 2333 + 0.14 * sourceSize + 0.14 * targetSize - 3905 * threshold;
	} else {
	    // error = 5.45
	    return -1.84 + 0.0006 * sourceSize + 0.0006 * targetSize;
	}
    }
}
