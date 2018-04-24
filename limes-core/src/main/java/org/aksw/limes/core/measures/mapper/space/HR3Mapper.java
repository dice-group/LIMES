package org.aksw.limes.core.measures.mapper.space;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.mapper.space.blocking.BlockingFactory;
import org.aksw.limes.core.measures.mapper.space.blocking.IBlockingModule;
import org.aksw.limes.core.measures.measure.space.ISpaceMeasure;
import org.aksw.limes.core.measures.measure.space.SpaceMeasureFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses metric spaces to create blocks.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
@SuppressWarnings("Duplicates")
public class HR3Mapper extends AMapper {

    private static final Logger logger = LoggerFactory.getLogger(HR3Mapper.class);

    public int granularity = 4;

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
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression,
            double threshold) {
        AMapping mapping = MappingFactory.createDefaultMapping();
        // 0. get properties
        String property1, property2;
        // get property labels
        Parser p = new Parser(expression, threshold);
        // get first property label
        String term1 = p.getLeftTerm();
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
        String term2 = p.getRightTerm();
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
        IBlockingModule generator = BlockingFactory.getBlockingModule(property2, p.getOperator(), threshold,
                granularity);
        // initialize the measure for similarity computation
        ISpaceMeasure measure = SpaceMeasureFactory.getMeasure(p.getOperator(), dimensions);
        // compute blockid for each of the elements of the target
        HashMap<ArrayList<Integer>, HashSet<String>> targetBlocks = getTargetBlocks(target, generator);
        // comparison
        matchBlocks(source, target, threshold, mapping, targetBlocks, property1, property2, generator, measure);
        return mapping;
    }

    private HashMap<ArrayList<Integer>, HashSet<String>> getTargetBlocks(ACache target, IBlockingModule generator) {
        HashMap<ArrayList<Integer>, HashSet<String>> targetBlocks = new HashMap<>();
        for (String key : target.getAllUris()) {
            ArrayList<ArrayList<Integer>> blockIds = generator.getAllBlockIds(target.getInstance(key));
            for (int ids = 0; ids < blockIds.size(); ids++) {
                if (!targetBlocks.containsKey(blockIds.get(ids))) {
                    targetBlocks.put(blockIds.get(ids), new HashSet<String>());
                }
                targetBlocks.get(blockIds.get(ids)).add(key);
            }
        }
        return targetBlocks;
    }

    private void matchBlocks(ACache source, ACache target, double threshold, AMapping mapping, HashMap<ArrayList<Integer>, HashSet<String>> targetBlocks, String property1, String property2, IBlockingModule generator, ISpaceMeasure measure) {
        for (String sourceInstanceUri : source.getAllUris()) {
            // for all blocks in [-1, +1] in each dimension compute similarities and store them
            ArrayList<ArrayList<Integer>> blockIds = generator.getAllSourceIds(source.getInstance(sourceInstanceUri), property1);
            for (ArrayList<Integer> blockId : blockIds) {
                ArrayList<ArrayList<Integer>> blocksToCompare = generator.getBlocksToCompare(blockId);
                for (ArrayList<Integer> aBlocksToCompare : blocksToCompare) {
                    if (targetBlocks.containsKey(aBlocksToCompare)) {
                        final HashSet<String> uris = targetBlocks.get(aBlocksToCompare);
                        for (String targetInstanceUri : uris) {
                            final double sim = measure.getSimilarity(source.getInstance(sourceInstanceUri),
                                    target.getInstance(targetInstanceUri), property1, property2);
                            if (sim >= threshold) {
                                mapping.add(sourceInstanceUri, targetInstanceUri, sim);
                            }
                        }
                    }
                }
            }
        }
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
