package org.aksw.limes.core.measures.mapper.atomic;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.Mapper;
import org.aksw.limes.core.measures.mapper.atomic.jarowinkler.LengthQuicksort;
import org.aksw.limes.core.measures.mapper.atomic.jarowinkler.TrieFilter;
import org.aksw.limes.core.measures.measure.string.RatcliffObershelpMeasure;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RatcliffObershelpMapper extends Mapper {

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

	logger.info("Running RatcliffObershelpMapper");

	List<String> listA, listB;
	List<String> properties = PropertyFetcher.getProperties(expression, threshold);
	Map<String, Set<String>> sourceMap = getValueToUriMap(source, properties.get(0));
	Map<String, Set<String>> targetMap = getValueToUriMap(target, properties.get(1));
	listA = new ArrayList<>(sourceMap.keySet());
	listB = new ArrayList<>(targetMap.keySet());
	RatcliffObershelpMeasure metric = new RatcliffObershelpMeasure();

	ConcurrentHashMap<String, Map<String, Double>> similarityBook;

	similarityBook = new ConcurrentHashMap<>(listA.size(), 1.0f);

	List<String> red, blue;
	red = listA;
	blue = listB;
	LengthQuicksort.sort(red);
	LengthQuicksort.sort(blue);
	// red is the list with the longest string
	if (red.get(red.size() - 1).length() < blue.get(blue.size() - 1).length()) {
	    List<String> temp = red;
	    red = blue;
	    blue = temp;
	}

	List<Pair<List<String>, List<String>>> tempPairs = new LinkedList<>();
	// generate length filtered partitions
	if (metric.lengthUpperBound(1, threshold) != -1) {
	    List<ImmutableTriple<Integer, Integer, Integer>> sliceBoundaries = metric
		    .getPartitionBounds(blue.get(blue.size() - 1).length(), threshold);
	    for (ImmutableTriple<Integer, Integer, Integer> sliceBoundary : sliceBoundaries) {
		MutablePair<List<String>, List<String>> m = new MutablePair<>();
		m.setLeft(new LinkedList<String>());
		m.setRight(new LinkedList<String>());
		for (String s : red)
		    if (s.length() >= sliceBoundary.getMiddle() && s.length() <= sliceBoundary.getRight())
			m.getLeft().add(s);
		    else if (s.length() > sliceBoundary.getRight())
			break;
		for (String s : blue)
		    if (s.length() == sliceBoundary.getLeft())
			m.getRight().add(s);
		    else if (s.length() > sliceBoundary.getLeft())
			break;
		if (m.getRight().size() > 0 && m.getLeft().size() > 0)
		    tempPairs.add(m);
	    }
	} else {
	    MutablePair<List<String>, List<String>> m = new MutablePair<>();
	    m.setLeft(red);
	    m.setRight(blue);
	    tempPairs.add(m);
	}

	int poolSize = Runtime.getRuntime().availableProcessors();
	poolSize = poolSize > tempPairs.size() ? tempPairs.size() : poolSize;

	logger.info("Partitioned into " + String.valueOf(tempPairs.size()) + " sets.");
	logger.info("Initializing Threadpool for " + String.valueOf(Runtime.getRuntime().availableProcessors())
		+ " threads.");

	// create thread pool, one thread per partition
	ExecutorService executor = Executors.newFixedThreadPool(poolSize);
	// executor = Executors.newFixedThreadPool(1);
	for (Pair<List<String>, List<String>> tempPair : tempPairs) {
	    Runnable worker = new TrieFilter(tempPair, similarityBook, new RatcliffObershelpMeasure(), threshold);
	    executor.execute(worker);
	}
	executor.shutdown();
	while (!executor.isTerminated()) {
	    try {
		Thread.sleep(500);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
	logger.info("Similarity Book has " + String.valueOf(similarityBook.size()) + " entries.");
	Mapping result = new MemoryMapping();
	for (String s : similarityBook.keySet()) {
	    for (String t : similarityBook.get(s).keySet()) {
		for (String sourceUri : sourceMap.get(s)) {
		    for (String targetUri : targetMap.get(t)) {
			result.add(sourceUri, targetUri, similarityBook.get(s).get(t));
		    }
		}
	    }
	}
	return result;
    }

    @Override
    public String getName() {
	return "ratcliff-obershelp";
    }

    @Override
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return -1d;
    }

    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return -1d;
    }
}
