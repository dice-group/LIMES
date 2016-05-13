package org.aksw.limes.core.measures.mapper.temporal.simpleTemporal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.Mapper;

/**
 * Abstract class of temporal relations mappers.
 * 
 * @author kleanthi
 */
public abstract class SimpleTemporalMapper extends Mapper implements ISimpleTemporalMapper {

    /**
     * Extract first property (beginDate) from metric expression.
     * 
     * @param expression,
     *            metric expression
     * 
     * @return first property of metric expression as string
     */
    protected String getFirstProperty(String properties) {
	properties = properties.substring(properties.indexOf(".") + 1, properties.length());
	int plusIndex = properties.indexOf("|");
	if (properties.indexOf("|") != -1) {
	    String p1 = properties.substring(0, plusIndex);
	    return p1;
	} else
	    return properties;
    }

    /**
     * Extract second property (machineID) from metric expression.
     * 
     * @param expression,
     *            the metric expression
     * 
     * @return second property of metric expression as string
     */
    protected String getSecondProperty(String properties) {
	properties = properties.substring(properties.indexOf(".") + 1, properties.length());
	int plusIndex = properties.indexOf("|");
	if (properties.indexOf("|") != -1) {
	    String p1 = properties.substring(plusIndex + 1, properties.length());
	    return p1;
	} else
	    return properties;
    }

    /**
     * Orders a cache of instances based on their begin date property. For each
     * instance, it retrieves its begin date property, converts its value to an
     * epoch (string) using the SimpleDateFormat function and places the
     * instance inside the corresponding set("bucket") of instances.
     * 
     * @param cache,
     *            the cache of instances
     * 
     * @param expression,
     *            the metric expression
     * 
     * @return blocks, a map of sets with unique begin dates as keys and
     *         set of instances as values
     */
    protected TreeMap<String, Set<Instance>> orderByBeginDate(Cache cache, String expression) {

	TreeMap<String, Set<Instance>> blocks = new TreeMap<String, Set<Instance>>();
	Parser p = new Parser(expression, 0.0d);
	String property = getFirstProperty(p.getTerm1());
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
