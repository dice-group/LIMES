package org.aksw.limes.core.measures.mapper.string;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.measures.mapper.Mapper;
import org.aksw.limes.core.measures.mapper.PropertyFetcher;
import org.aksw.limes.core.measures.mapper.string.triefilter.LengthQuicksort;
import org.aksw.limes.core.measures.mapper.string.triefilter.TrieFilter;
import org.aksw.limes.core.measures.measure.string.RatcliffObershelpMeasure;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

public class RatcliffObershelpMapper extends Mapper {

    static Logger logger = Logger.getLogger("LIMES");


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
        List<String> properties = PropertyFetcher.getProperties(expression, threshold);
        Map<String, Set<String>> sourceMap = getValueToUriMap(source, properties.get(0));
        Map<String, Set<String>> targetMap = getValueToUriMap(target, properties.get(1));
        return getMapping(sourceMap, targetMap, threshold);
    }

    protected Mapping getMapping(Map<String, Set<String>> sourceMap, Map<String, Set<String>> targetMap, double threshold) {
        List<String> listA, listB;
        listA = new ArrayList<>(sourceMap.keySet());
        listB = new ArrayList<>(targetMap.keySet());
        RatcliffObershelpMeasure metric = new RatcliffObershelpMeasure();
        ConcurrentHashMap<String, Map<String, Double>> similarityBook = new ConcurrentHashMap<>(listA.size(), 1.0f);
        List<String> red, blue;
        red = listA;
        blue = listB;
        LengthQuicksort.sort(red);
        LengthQuicksort.sort(blue);
        // red is the list with the longest string
        boolean swapped = false;
        if (red.get(red.size() - 1).length() < blue.get(blue.size() - 1).length()) {
            List<String> temp = red;
            red = blue;
            blue = temp;
            swapped = true;
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
        return getUriToUriMapping(similarityBook, sourceMap, targetMap, swapped);
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
