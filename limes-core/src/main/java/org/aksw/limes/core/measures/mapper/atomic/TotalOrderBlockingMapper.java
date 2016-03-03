/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.atomic;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.Mapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.aksw.limes.core.measures.measure.space.ISpaceMeasure;
import org.aksw.limes.core.measures.measure.space.SpaceMeasureFactory;
import org.aksw.limes.core.measures.measure.space.blocking.BlockingFactory;
import org.aksw.limes.core.measures.measure.space.blocking.BlockingModule;
import org.apache.log4j.Logger;

/**
 * Uses metric spaces to create blocks.
 * 
 * @author ngonga
 */
public class TotalOrderBlockingMapper extends Mapper {
    static Logger logger = Logger.getLogger(TotalOrderBlockingMapper.class.getName());

    public int granularity = 4;

    // this might only work for substraction. Need to create something that
    // transforms
    // the threshold on real numbers into a threshold in the function space.
    // Then it will work
    // perfectly

    public String getName() {
	return "TotalOrderBlockingMapper";
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
	Mapping mapping = new MemoryMapping();

	// maps each block id to a set of instances. Actually one should
	// integrate LIMES here
	HashMap<ArrayList<Integer>, TreeSet<String>> targetBlocks = new HashMap<ArrayList<Integer>, TreeSet<String>>();

	// 0. get properties
	String property1, property2;
	// get property labels
	Parser p = new Parser(expression, threshold);
	// get first property label
	String term1 = p.getTerm1();
	if (term1.contains(".")) {
	    String split[] = term1.split("\\.");
	    property1 = split[1];
	    if (split.length >= 2)
		for (int part = 2; part < split.length; part++)
		    property1 += "." + split[part];
	} else {
	    property1 = term1;
	}

	// get second property label
	String term2 = p.getTerm2();
	if (term2.contains(".")) {
	    String split[] = term2.split("\\.");
	    property2 = split[1];
	    if (split.length >= 2)
		for (int part = 2; part < split.length; part++)
		    property2 += "." + split[part];
	} else {
	    property2 = term2;
	}

	// get number of dimensions we are dealing with
	int dimensions = property2.split("\\|").length;
	// important. The Blocking module takes care of the transformation from
	// similarity to
	// distance threshold. Central for finding the right blocks and might
	// differ from blocker
	// to blocker.
	BlockingModule generator = BlockingFactory.getBlockingModule(property2, p.getOperator(), threshold,
		granularity);

	// initialize the measure for similarity computation
	ISpaceMeasure measure = SpaceMeasureFactory.getMeasure(p.getOperator(), dimensions);

	// compute blockid for each of the elements of the target
	// implement our simple yet efficient blocking approach
	ArrayList<ArrayList<Integer>> blockIds;
	for (String key : target.getAllUris()) {
	    blockIds = generator.getAllBlockIds(target.getInstance(key));
	    for (int ids = 0; ids < blockIds.size(); ids++) {
		if (!targetBlocks.containsKey(blockIds.get(ids))) {
		    targetBlocks.put(blockIds.get(ids), new TreeSet<String>());
		}
		targetBlocks.get(blockIds.get(ids)).add(key);
	    }
	}

	ArrayList<ArrayList<Integer>> blocksToCompare;
	// comparison
	TreeSet<String> uris;
	double sim;
	int counter = 0;
	source.getAllUris().size();
	for (String sourceInstanceUri : source.getAllUris()) {
	    counter++;
	    if (counter % 1000 == 0) {
		// logger.info("Processed " + (counter * 100 / size) + "% of the
		// links");
		// get key

	    }
	    // logger.info("Getting "+property1+" from "+sourceInstanceUri);
	    blockIds = generator.getAllSourceIds(source.getInstance(sourceInstanceUri), property1);
	    // logger.info("BlockId for "+sourceInstanceUri+" is "+blockId);
	    // for all blocks in [-1, +1] in each dimension compute similarities
	    // and store them
	    for (int ids = 0; ids < blockIds.size(); ids++) {
		blocksToCompare = generator.getBlocksToCompare(blockIds.get(ids));

		// logger.info(sourceInstanceUri+" is to compare with blocks
		// "+blocksToCompare);
		for (int index = 0; index < blocksToCompare.size(); index++) {
		    if (targetBlocks.containsKey(blocksToCompare.get(index))) {
			uris = targetBlocks.get(blocksToCompare.get(index));
			for (String targetInstanceUri : uris) {
			    sim = measure.getSimilarity(source.getInstance(sourceInstanceUri),
				    target.getInstance(targetInstanceUri), property1, property2);
			    if (sim >= threshold) {
				mapping.add(sourceInstanceUri, targetInstanceUri, sim);
			    }
			}
		    }
		}
	    }
	}
	// logger.info("Cmin = "+necessaryComparisons+"; C = "+comparisons);
	return mapping;
    }

    // need to change this
    public double getRuntimeApproximation(int sourceSize, int targetSize, double threshold, Language language) {
	if (language.equals(Language.DE)) {
	    // error = 667.22
	    return 16.27 + 5.1 * sourceSize + 4.9 * targetSize - 23.44 * threshold;
	} else {
	    // error = 5.45
	    return 200 + 0.005 * (sourceSize + targetSize) - 56.4 * threshold;
	}
    }

    public double getMappingSizeApproximation(int sourceSize, int targetSize, double threshold, Language language) {
	if (language.equals(Language.DE)) {
	    // error = 667.22
	    return 2333 + 0.14 * sourceSize + 0.14 * targetSize - 3905 * threshold;
	} else {
	    // error = 5.45
	    return 0.006 * (sourceSize + targetSize) - 134.2 * threshold;
	}
    }
}
