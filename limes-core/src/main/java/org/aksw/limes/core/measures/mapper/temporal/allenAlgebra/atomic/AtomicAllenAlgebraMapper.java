package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.parser.Parser;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Abstract class of atomic Allen's Algebra atomic Relations. The main idea
 * behind this approach is to represent each Allen's Algebra Relation as a
 * Boolean combination of atomic relations. By computing each of the atomic
 * relations only once and only if needed, we can decrease the overall runtime
 * of the computation of a given set of Allen relations. Each event s can be
 * described using two time points b(s) and e(s). To compose the atomic interval
 * relations, we define all possible binary relations between the begin and end
 * points of two event resources s = (b(s), e(s)) and t = (b(t), e(t)).
 */
public abstract class AtomicAllenAlgebraMapper {
    protected static final Logger logger = Logger.getLogger(AtomicAllenAlgebraMapper.class.getName());

    public AtomicAllenAlgebraMapper() {
    }

    /**
     * Extract first property (beginDate) from metric expression.
     *
     * @param expression,
     *         metric expression
     * @return first property of metric expression as string
     */
    protected static String getBeginProperty(String properties) {
        properties = properties.substring(properties.indexOf(".") + 1, properties.length());
        int plusIndex = properties.indexOf("|");
        if (properties.indexOf("|") != -1) {
            String p1 = properties.substring(0, plusIndex);
            return p1;
        } else
            return properties;
    }

    /**
     * Extract second property (endDate) from metric expression.
     *
     * @param expression,
     *         metric expression
     * @return first property of metric expression as string
     * @throws IllegalArgumentException
     */
    protected static String getEndProperty(String properties) throws IllegalArgumentException {
        properties = properties.substring(properties.indexOf(".") + 1, properties.length());
        int plusIndex = properties.indexOf("|");
        if (properties.indexOf("|") != -1) {
            String p1 = properties.substring(plusIndex + 1, properties.length());
            return p1;
        } else
            throw new IllegalArgumentException();
    }

    /**
     * Orders a cache of instances based on their begin date property. For each
     * instance, it retrieves its begin date property, converts its value to an
     * epoch (string) using the SimpleDateFormat function and places the
     * instance inside the corresponding set("bucket") of instances.
     *
     * @param cache,
     *         the cache of instances
     * @param expression,
     *         the metric expression
     * @return blocks, a map of sets with unique begin dates as keys and set of
     * instances (string representation) as values
     */
    protected static TreeMap<Long, Set<String>> orderByBeginDate(Cache cache, String expression) {
        TreeMap<Long, Set<String>> blocks = new TreeMap<Long, Set<String>>();
        Parser p = new Parser(expression, 0.0d);
        String property = getBeginProperty(p.getLeftTerm());

        for (Instance instance : cache.getAllInstances()) {
            TreeSet<String> time = instance.getProperty(property);

            for (String value : time) {
                try {
                    // 2015-04-22T11:29:51+02:00
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    Date date = df.parse(value);
                    long epoch = date.getTime();
                    if (!blocks.containsKey(epoch)) {
                        Set<String> l = new HashSet<String>();
                        l.add(instance.getUri());
                        blocks.put(epoch, l);
                    } else {
                        blocks.get(epoch).add(instance.getUri());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }
        return blocks;
    }

    /**
     * Orders a cache of instances based on their end date property. For each
     * instance, it retrieves its end date property, converts its value to an
     * epoch (string) using the SimpleDateFormat function and places the
     * instance inside the corresponding set("bucket") of instances.
     *
     * @param cache,
     *         the cache of instances
     * @param expression,
     *         the metric expression
     * @return blocks, a map of sets with unique end dates as keys and set of
     * instances (string representation) as values
     */
    protected static TreeMap<Long, Set<String>> orderByEndDate(Cache cache, String expression) {
        TreeMap<Long, Set<String>> blocks = new TreeMap<Long, Set<String>>();
        Parser p = new Parser(expression, 0.0d);
        String property = null;
        try {
            property = getEndProperty(p.getLeftTerm());
        } catch (IllegalArgumentException e1) {
            logger.error("Missing end property in " + p.getLeftTerm() + ". Exiting..");
            System.exit(1);
        }

        for (Instance instance : cache.getAllInstances()) {
            TreeSet<String> time = instance.getProperty(property);

            for (String value : time) {
                try {
                    // 2015-04-22T11:29:51+02:00
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    Date date = df.parse(value);
                    long epoch = date.getTime();
                    if (!blocks.containsKey(epoch)) {
                        Set<String> l = new HashSet<String>();
                        l.add(instance.getUri());
                        blocks.put(epoch, l);
                    } else {
                        blocks.get(epoch).add(instance.getUri());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }
        return blocks;
    }

    /**
     * Returns the set of concurrent target events for each source instance.
     *
     * @param sources,
     *         set of source instances ordered by begin/end date
     * @param targets,
     *         set of target instances ordered by begin/end date
     * @return concurrentEvents, the map of concurrent events
     */
    protected static TreeMap<String, Set<String>> mapConcurrent(TreeMap<Long, Set<String>> sources,
                                                                TreeMap<Long, Set<String>> targets) {
        TreeMap<String, Set<String>> concurrentEvents = new TreeMap<String, Set<String>>();

        for (Map.Entry<Long, Set<String>> sourceEntry : sources.entrySet()) {

            Long sourceTimeStamp = sourceEntry.getKey();
            Set<String> sourceInstances = sourceEntry.getValue();

            Set<String> tempTargets = targets.get(sourceTimeStamp);
            if (tempTargets != null) {
                for (String sourceInstance : sourceInstances) {
                    concurrentEvents.put(sourceInstance, tempTargets);
                }
            }
        }

        return concurrentEvents;

    }

    /**
     * Returns the set of predecessor target events for each source instance.
     *
     * @param sources,
     *         set of source instances ordered by begin/end date
     * @param targets,
     *         set of target instances ordered by begin/end date
     * @return concurrentEvents, the map of predecessor events
     */
    protected static TreeMap<String, Set<String>> mapPredecessor(TreeMap<Long, Set<String>> sources,
                                                                 TreeMap<Long, Set<String>> targets) {
        TreeMap<String, Set<String>> concurrentEvents = new TreeMap<String, Set<String>>();

        for (Map.Entry<Long, Set<String>> sourceEntry : sources.entrySet()) {

            Long sourceTimeStamp = sourceEntry.getKey();
            Set<String> sourceInstances = sourceEntry.getValue();

            SortedMap<Long, Set<String>> tempTargets = targets.tailMap(sourceTimeStamp);

            if (tempTargets != null) {

                Set<String> subTargets = new TreeSet<String>();
                for (Map.Entry<Long, Set<String>> targetEntry : tempTargets.entrySet()) {
                    Long targetTimeStamp = targetEntry.getKey();

                    if (!targetTimeStamp.equals(sourceTimeStamp)) {
                        subTargets.addAll(targetEntry.getValue());
                    }
                }
                if (!subTargets.isEmpty()) {
                    for (String sourceInstance : sourceInstances) {
                        concurrentEvents.put(sourceInstance, subTargets);

                    }
                }

            }
        }

        return concurrentEvents;

    }

    public abstract TreeMap<String, Set<String>> getConcurrentEvents(Cache source, Cache target, String expression);

    public abstract TreeMap<String, Set<String>> getPredecessorEvents(Cache source, Cache target, String expression);

    public abstract String getName();
}
