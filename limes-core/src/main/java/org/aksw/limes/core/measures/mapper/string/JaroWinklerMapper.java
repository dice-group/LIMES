package org.aksw.limes.core.measures.mapper.string;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.Mapper;
import org.aksw.limes.core.measures.mapper.pointsets.PropertyFetcher;
import org.aksw.limes.core.measures.mapper.string.triefilter.LengthQuicksort;
import org.aksw.limes.core.measures.mapper.string.triefilter.TrieFilter;
import org.aksw.limes.core.measures.measure.string.JaroWinkler;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Mapper for bounded Jaro-Winkler distances using an efficient length-partitioning- and trie-pruning-based
 * approach in parallel.
 */
public class JaroWinklerMapper extends Mapper {

    static Logger logger = Logger.getLogger(JaroWinklerMapper.class.getName());

    /**
     * Computes a mapping between a source and a target.
     *
     * @param source
     *         Source cache
     * @param target
     *         Target cache
     * @param sourceVar
     *         Variable for the source dataset
     * @param targetVar
     *         Variable for the target dataset
     * @param expression
     *         Expression to process.
     * @param threshold
     *         Similarity threshold
     * @return A mapping which contains links between the source instances and
     * the target instances
     */
    @Override
    public AMapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression,
                               double threshold) {
        logger.info("Running JaroWinklerMapper");
        List<String> properties = PropertyFetcher.getProperties(expression, threshold);
        // generate value to uri maps
        Map<String, Set<String>> sourceMap = getValueToUriMap(source, properties.get(0));
        Map<String, Set<String>> targetMap = getValueToUriMap(target, properties.get(1));
        return getMapping(sourceMap, targetMap, threshold);
    }

    protected AMapping getMapping(Map<String, Set<String>> sourceMap, Map<String, Set<String>> targetMap, double threshold) {
        List<String> listA, listB;
        // get lists of strings to match
        listA = new ArrayList<>(sourceMap.keySet());
        listB = new ArrayList<>(targetMap.keySet());
        // sort lists
        LengthQuicksort.sort(listA);
        LengthQuicksort.sort(listB);
        // swap lists iff the largest string in listB is larger than the largest string in listA
        boolean swapped = false;
        if (listA.get(listA.size() - 1).length() < listB.get(listB.size() - 1).length()) {
            List<String> temp = listA;
            listA = listB;
            listB = temp;
            swapped = true;
        }
        // set up partitioning of lists of strings based on strings lengths
        JaroWinkler metric = new JaroWinkler();
        List<Pair<List<String>, List<String>>> partitions = new LinkedList<>();
        // only attempt to partition iff it makes sense mathematically, that is, the upper bound is well defined
        // (cf. "On the efficient execution of bounded Jaro-Winkler distances")
        if (metric.lengthUpperBound(1, threshold) != -1) {
            List<ImmutableTriple<Integer, Integer, Integer>> sliceBoundaries = metric
                    .getPartitionBounds(listB.get(listB.size() - 1).length(), threshold);
            for (ImmutableTriple<Integer, Integer, Integer> sliceBoundary : sliceBoundaries) {
                MutablePair<List<String>, List<String>> m = new MutablePair<>();
                m.setLeft(new LinkedList<>());
                m.setRight(new LinkedList<>());
                for (String s : listA)
                    if (s.length() >= sliceBoundary.getMiddle() && s.length() <= sliceBoundary.getRight())
                        m.getLeft().add(s);
                    else if (s.length() > sliceBoundary.getRight())
                        break;
                for (String s : listB)
                    if (s.length() == sliceBoundary.getLeft())
                        m.getRight().add(s);
                    else if (s.length() > sliceBoundary.getLeft())
                        break;
                if (m.getRight().size() > 0 && m.getLeft().size() > 0)
                    partitions.add(m);
            }
            // else, we have just one big partition
        } else {
            MutablePair<List<String>, List<String>> m = new MutablePair<>();
            m.setLeft(listA);
            m.setRight(listB);
            partitions.add(m);
        }

        // setting up parallel execution of matching
        logger.info("Partitioned into " + String.valueOf(partitions.size()) + " sets.");
        logger.info("Initializing Threadpool for " + String.valueOf(Runtime.getRuntime().availableProcessors())
                + " threads.");
        ConcurrentHashMap<String, Map<String, Double>> similarityBook = new ConcurrentHashMap<>(listA.size(), 1.0f);
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        // instantiate and queue up workers
        for (Pair<List<String>, List<String>> tempPair : partitions) {
            Runnable worker = new TrieFilter(tempPair, similarityBook, metric.clone(), threshold);
            executor.execute(worker);
        }
        // wait for threads in pool
        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // return result
        //logger.info("Similarity Book has " + String.valueOf(similarityBook.size()) + " entries.");
        AMapping mapping = getUriToUriMapping(similarityBook, sourceMap, targetMap, swapped);
        
        return mapping;
    }

    @Override
    public String getName() {
        return "jaro-winkler";
    }

    @Override
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }
}
