package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.util.datetime.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class of atomic Allen's relations. The main idea behind this
 * approach is to represent each Allen's Algebra relation as a Boolean
 * combination of atomic relations. By computing each of the atomic relations
 * only once and only if needed, we can decrease the overall runtime of the
 * computation of a given set of Allen relations. Each event s can be described
 * using two time points b(s) and e(s). To compose the atomic interval
 * relations, we define all possible binary relations between the begin and end
 * points of two event resources s = (b(s), e(s)) and t = (b(t), e(t)).
 * 
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public abstract class AAtomicAllenAlgebraMapper {
    protected static final Logger logger = LoggerFactory.getLogger(AAtomicAllenAlgebraMapper.class);

    /**
     * Constructor of AtomicAllenAlgebraMapper class.
     */
    public AAtomicAllenAlgebraMapper() {
    }

    /**
     * Extract first property (beginDate) from metric expression.
     *
     * @param expression,
     *            The metric expression
     * @return first property of metric expression as string
     */
    protected static String getBeginProperty(String expression) {
        // expression = x.beginDate1|endDate1
        expression = expression.substring(expression.indexOf(".") + 1, expression.length());
        // expression beginDate1|endDate1
        int plusIndex = expression.indexOf("|");
        if (expression.indexOf("|") != -1) {
            // p1 = beginDate1
            String p1 = expression.substring(0, plusIndex);
            return p1;
        } else
            return expression;
    }

    /**
     * Extract second property (endDate) from metric expression.
     *
     * @param expression,
     *            The metric expression
     * @return first property of metric expression as string
     * @throws IllegalArgumentException
     *             if endDate property is not declared
     */
    protected static String getEndProperty(String expression) throws IllegalArgumentException {
        // expression = x.beginDate1|endDate1
        expression = expression.substring(expression.indexOf(".") + 1, expression.length());
        // expression beginDate1|endDate1
        int plusIndex = expression.indexOf("|");
        if (expression.indexOf("|") != -1) {
            // p1 = endDate1
            String p1 = expression.substring(plusIndex + 1, expression.length());
            return p1;
        } else
            throw new IllegalArgumentException();
    }

    /**
     * Returns the epoch value of an input time stamp
     * 
     * 
     * @param timeStamp,
     *            the time stamp
     * 
     * @return the epoch value of the corresponding time stamp
     */
    protected static long getEpoch(String timeStamp) {

        Date date = DateTimeFormat.getDate(timeStamp);
        long epoch = date.getTime();

        return epoch;

    }

    /**
     * Orders a cache of instances based on their begin date property. For each
     * instance, it retrieves its begin date property, converts its value to an
     * epoch (string) using the SimpleDateFormat function and places the
     * instance inside the corresponding set("bucket") of instances.
     * 
     * 
     * @param cache,
     *            The cache of instances
     * @param expression,
     *            The metric expression
     * @param kbType,
     *            source or target
     * 
     * @return blocks, a map of sets with unique begin dates as keys and set of
     *         instances (string representation) as values
     */
    protected static TreeMap<Long, Set<String>> orderByBeginDate(ACache cache, String expression, String kbType) {
        TreeMap<Long, Set<String>> blocks = new TreeMap<Long, Set<String>>();
        Parser p = new Parser(expression, 1.0d);
        String property = null;
        if (kbType.equalsIgnoreCase("source"))
            property = getBeginProperty(p.getLeftTerm());
        else
            property = getBeginProperty(p.getRightTerm());

        for (Instance instance : cache.getAllInstances()) {
            TreeSet<String> time = instance.getProperty(property);

            for (String value : time) {
                // 2015-04-22T11:29:51+02:00
                Date date = DateTimeFormat.getDate(value);
                long epoch = date.getTime();
                if (!blocks.containsKey(epoch)) {
                    Set<String> l = new HashSet<String>();
                    l.add(instance.getUri());
                    blocks.put(epoch, l);
                } else {
                    blocks.get(epoch).add(instance.getUri());
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
     * @param kbType
     *            TODO
     *
     * @param cache,
     *            The cache of instances
     * @param expression,
     *            The metric expression
     * @param kbType,
     *            source or target
     * 
     * @return blocks, a map of sets with unique end dates as keys and set of
     *         instances (string representation) as values
     */
    protected static TreeMap<Long, Set<String>> orderByEndDate(ACache cache, String expression, String kbType) {
        TreeMap<Long, Set<String>> blocks = new TreeMap<Long, Set<String>>();
        Parser p = new Parser(expression, 1.0d);
        String property = null;
        if (kbType.equalsIgnoreCase("source"))
            property = getEndProperty(p.getLeftTerm());
        else
            property = getEndProperty(p.getRightTerm());

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
     * Maps each source event to its set of concurrent target events.
     *
     * @param sources,
     *            The set of source instances ordered by begin/end date
     * @param targets,
     *            The set of target instances ordered by begin/end date
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
     * Maps each source event to its set of predecessor target events.
     *
     * @param sources,
     *            The set of source instances ordered by begin/end date
     * @param targets,
     *            The set of target instances ordered by begin/end date
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

    /**
     * Returns the set of concurrent target events for each source instance.
     *
     * @param source,
     *            The source cache
     * @param target,
     *            The target cache
     * @param expression,
     *            The metric expression
     * @return concurrentEvents, set of concurrent target events for each source
     *         instance.
     */
    public abstract TreeMap<String, Set<String>> getConcurrentEvents(ACache source, ACache target, String expression);

    /**
     * Returns the set of predecessor target events for each source instance.
     *
     * @param source,
     *            The source cache
     * @param target,
     *            The target cache
     * @param expression,
     *            The metric expression
     * @return predecessorEvents, set of predecessor target events for each
     *         source instance.
     */
    public abstract TreeMap<String, Set<String>> getPredecessorEvents(ACache source, ACache target, String expression);

    /**
     * Returns the name of the atomic Allen's temporal mapper.
     *
     * @return Mapper name as a string
     */
    public abstract String getName();
}
