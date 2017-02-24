package org.aksw.limes.core.measures.mapper.temporal.simpleTemporal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.AMapper;

/**
 * Abstract class of simple temporal relations mapper.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public abstract class SimpleTemporalMapper extends AMapper implements ISimpleTemporalMapper {

    /**
     * Extracts first property (beginDate) from metric expression.
     *
     * @param expression,
     *            The metric expression
     * @return first property of metric expression as string
     */
    protected String getFirstProperty(String expression) {
        expression = expression.substring(expression.indexOf(".") + 1, expression.length());
        int plusIndex = expression.indexOf("|");
        if (expression.indexOf("|") != -1) {
            String p1 = expression.substring(0, plusIndex);
            return p1;
        } else
            return expression;
    }

    /**
     * Extracts second property (machineID) from metric expression.
     *
     * @param expression,
     *            The metric expression
     * @return second property of metric expression as string
     */
    protected String getSecondProperty(String expression) {
        expression = expression.substring(expression.indexOf(".") + 1, expression.length());
        int plusIndex = expression.indexOf("|");
        if (expression.indexOf("|") != -1) {
            String p1 = expression.substring(plusIndex + 1, expression.length());
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
     *            the cache of instances
     * @param expression,
     *            the metric expression
     * @param kbType,
     *            source or target
     *            
     * @return blocks, a map of sets with unique begin dates as keys and set of
     *         instances as values
     */
    protected TreeMap<String, Set<Instance>> orderByBeginDate(ACache cache, String expression, String kbType) {

        TreeMap<String, Set<Instance>> blocks = new TreeMap<String, Set<Instance>>();
        Parser p = new Parser(expression, 0.0d);
        String property = null;
        if (kbType.equalsIgnoreCase("source"))
            property = getFirstProperty(p.getLeftTerm());
        else
            property = getFirstProperty(p.getRightTerm());
        for (Instance instance : cache.getAllInstances()) {
            TreeSet<String> time = instance.getProperty(property);
            for (String value : time) {
                try {
                    // 2015-04-22T11:29:51+02:00
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    Date date = df.parse(value);
                    long epoch = date.getTime();
                    if (!blocks.containsKey(String.valueOf(epoch))) {
                        Set<Instance> l = new HashSet<Instance>();
                        l.add(instance);
                        blocks.put(String.valueOf(epoch), l);
                    } else {
                        blocks.get(String.valueOf(epoch)).add(instance);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }
        return blocks;

    }

}
