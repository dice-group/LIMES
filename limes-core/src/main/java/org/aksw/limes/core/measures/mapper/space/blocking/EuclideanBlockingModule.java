/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.aksw.limes.core.measures.mapper.space.blocking;

import java.util.ArrayList;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.space.ISpaceMeasure;
import org.aksw.limes.core.measures.measure.space.SpaceMeasureFactory;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class EuclideanBlockingModule implements IBlockingModule {
    int dim = 2;
    ArrayList<Double> thresholds;
    ArrayList<String> properties;
    ISpaceMeasure measure;
    Instance zero;

    /**
     * Initializes the generator. The basic idea here is the following: First,
     * pick a random instance origin. That is the center upon which the block
     * ids will be computed. Each measure can return the threshold for blocking
     * that is equivalent to the similarity threshold given in by the user. For
     * euclidean metrics, this value is the same. Yet, for metrics that squeeze
     * space, this might not be the case. It is important to notice that the
     * generation assumes that the size of props.split("|") is the same as
     * dimensions.
     *
     * @param props
     *            List of properties that make up each dimension
     * @param measureName
     *            Name of the measure to be used to compute the similarity of
     *            instances
     * @param threshold
     *            General similarity threshold for the metric. This threshold is
     *            transformed into a distance threshold, as sim = a, d = (1 -
     *            a)/a. The space tiling is carried out according to distances,
     *            not similarities. Still, we can ensure that all points within
     *            the similarity range are found.
     */

    public EuclideanBlockingModule(String props, String measureName, double threshold) {
        thresholds = new ArrayList<Double>();
        properties = new ArrayList<String>();
        String[] split = props.split("\\|");
        dim = split.length;
        measure = SpaceMeasureFactory.getMeasure(measureName, dim);
        for (int i = 0; i < dim; i++) {
            thresholds.add(measure.getThreshold(i, threshold));
            properties.add(split[i]);
        }
    }

    public static ArrayList<Double> copyList(ArrayList<Double> list) {
        ArrayList<Double> copy = new ArrayList<Double>();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                copy.add(list.get(i));
            }
        }
        return copy;
    }

    public static ArrayList<ArrayList<Double>> addIdsToList(ArrayList<ArrayList<Double>> keys,
            TreeSet<String> propValues) {
        ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> values = new ArrayList<Double>();
        for (String s : propValues) {
            values.add(Double.parseDouble(s));
        }
        if (keys.size() == 0) {
            for (int j = 0; j < values.size(); j++) {
                ArrayList<Double> list = new ArrayList<Double>();
                list.add(values.get(j));
                result.add(list);
            }
        } else {
            ArrayList<Double> copy;
            for (int i = 0; i < keys.size(); i++) {
                for (int j = 0; j < values.size(); j++) {
                    copy = copyList(keys.get(i));
                    copy.add(values.get(j));
                    result.add(copy);
                }
            }
        }
        return result;
    }

    /**
     * Computes the block ID for a given instance a. The idea behind the
     * blocking is to tile the target space into blocks of dimension
     * thresdhold^dimensions. Each instance s from the source space is then
     * compared with the blocks lying directly around s's block and the block
     * where s is.
     *
     * @param a
     *            The instance whose blockId is to be computed
     * @return The ID for the block of a
     */
    public ArrayList<Integer> getBlockId(Instance a) {
        int blockId;
        ArrayList<Integer> blockIds = new ArrayList<Integer>();
        double value;
        for (int i = 0; i < dim; i++) {
            value = Double.parseDouble(a.getProperty(properties.get(i)).first());
            blockId = (int) java.lang.Math.floor(value / thresholds.get(i));
            blockIds.add(blockId);
        }
        return blockIds;
    }

    /**
     * Computes the ids of all the blocks surrounding a given block for
     * comparison Will be extremely useful for parallelizing as we can use
     * blocking on T and S as then put use locality
     *
     * @param blockId
     *            index
     * @return the ids of all the blocks surrounding a given block for
     *         comparison
     */
    public ArrayList<ArrayList<Integer>> getBlocksToCompare(ArrayList<Integer> blockId) {
        int dim = blockId.size();
        if (dim == 0)
            return new ArrayList<ArrayList<Integer>>();

        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        result.add(blockId);

        ArrayList<Integer> minus = new ArrayList<Integer>();
        ArrayList<Integer> plus = new ArrayList<Integer>();

        ArrayList<Integer> id;

        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < Math.pow(3, i); j++) {
                id = result.get(j);
                minus = new ArrayList<Integer>();
                plus = new ArrayList<Integer>();
                for (int k = 0; k < dim; k++) {
                    if (k != i) {
                        minus.add(id.get(k));
                        plus.add(id.get(k));
                    } else {
                        minus.add(id.get(k) - 1);
                        plus.add(id.get(k) + 1);
                    }
                }
                result.add(minus);
                result.add(plus);
            }
        }
        return result;
    }

    public ArrayList<ArrayList<Integer>> getAllBlockIds(Instance a) {
        int blockId;
        ArrayList<ArrayList<Integer>> blockIds = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Double>> combinations = new ArrayList<ArrayList<Double>>();
        // get all property combinations
        for (int i = 0; i < dim; i++) {
            combinations = addIdsToList(combinations, a.getProperty(properties.get(i)));
        }
        for (int i = 0; i < combinations.size(); i++) {
            ArrayList<Double> combination = combinations.get(i);
            ArrayList<Integer> block = new ArrayList<Integer>();
            for (int j = 0; j < combination.size(); j++) {
                blockId = (int) java.lang.Math.floor((combination.get(j)) / thresholds.get(j));
                block.add(blockId);
            }
            blockIds.add(block);
        }
        return blockIds;
    }

    public ArrayList<ArrayList<Integer>> getAllSourceIds(Instance a, String sourceProps) {
        String[] props = sourceProps.split("\\|");
        int blockId;
        ArrayList<ArrayList<Integer>> blockIds = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Double>> combinations = new ArrayList<ArrayList<Double>>();
        // get all property combinations
        for (int i = 0; i < dim; i++) {
            combinations = addIdsToList(combinations, a.getProperty(props[i]));
        }
        for (int i = 0; i < combinations.size(); i++) {
            ArrayList<Double> combination = combinations.get(i);
            ArrayList<Integer> block = new ArrayList<Integer>();
            for (int j = 0; j < combination.size(); j++) {
                blockId = (int) java.lang.Math.floor((combination.get(j)) / thresholds.get(j));
                block.add(blockId);
            }
            blockIds.add(block);
        }
        return blockIds;
    }
}
