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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class VariableGranularityBlocker implements IBlockingModule {
    static Logger logger = LoggerFactory.getLogger(VariableGranularityBlocker.class);
    int dim = 2;
    ArrayList<Double> thresholds;
    ArrayList<String> properties;
    ISpaceMeasure measure;
    Instance zero;
    int granularity;

    public VariableGranularityBlocker(String props, String measureName, double threshold) {
        thresholds = new ArrayList<Double>();
        properties = new ArrayList<String>();
        String[] split = props.split("\\|");
        dim = split.length;
        measure = SpaceMeasureFactory.getMeasure(measureName, dim);
        for (int i = 0; i < dim; i++) {
            thresholds.add(measure.getThreshold(i, threshold));
            properties.add(split[i]);
        }
        granularity = 2;
    }

    public VariableGranularityBlocker(String props, String measureName, double threshold, int _granularity) {
        thresholds = new ArrayList<Double>();
        properties = new ArrayList<String>();
        String[] split = props.split("\\|");
        dim = split.length;
        measure = SpaceMeasureFactory.getMeasure(measureName, dim);
        for (int i = 0; i < dim; i++) {
            thresholds.add(measure.getThreshold(i, threshold));
            properties.add(split[i]);
        }
        granularity = _granularity;
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
        double value = 0;
        for (String s : propValues) {
            try {
                value = Double.parseDouble(s);
            } catch (Exception e) {
                logger.warn(s + " is not a number. Will be replaced by 0.");
            }
            values.add(value);
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


    public void setGranularity(int n) {
        granularity = n;
    }

    /**
     * Computes the block ID for a given instance a. The idea behind the blocking
     * is to tile the target space into blocks of dimension thresdhold^dimensions.
     * Each instance s from the source space is then compared with the blocks lying
     * directly around s's block and the block where s is.
     *
     * @param blockId The instance whose blockId is to be computed
     * @return The ID for the block of a
     */
    public ArrayList<ArrayList<Integer>> getBlocksToCompare(ArrayList<Integer> blockId) {
        int dim = blockId.size();
        if (dim == 0) {
            return new ArrayList<ArrayList<Integer>>();
        }
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        result.add(blockId);

        ArrayList<ArrayList<Integer>> toAdd = new ArrayList<ArrayList<Integer>>();

        ArrayList<Integer> id;

        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < Math.pow(2 * granularity + 1, i); j++) {
                id = result.get(j);
                toAdd = new ArrayList<ArrayList<Integer>>();
                for (int k = 0; k < 2 * granularity; k++) {
                    toAdd.add(new ArrayList<Integer>());
                }
                for (int k = 0; k < dim; k++) {
                    if (k != i) {
                        for (int l = 0; l < 2 * granularity; l++) {
                            toAdd.get(l).add(id.get(k));
                        }
                    } else {
                        for (int l = 0; l < granularity; l++) {
                            toAdd.get(l).add(id.get(k) - (l + 1));
                        }
                        for (int l = 0; l < granularity; l++) {
                            toAdd.get(l + granularity).add(id.get(k) + l + 1);
                        }
                    }
                }
                //Merge results
                for (int l = 0; l < 2 * granularity; l++) {
                    result.add(toAdd.get(l));
                }
            }
        }
        return result;
    }

    public ArrayList<Integer> getBlockId(Instance a) {
        int blockId;
        ArrayList<Integer> blockIds = new ArrayList<Integer>();
        double value;
        for (int i = 0; i < dim; i++) {
            value = Double.parseDouble(a.getProperty(properties.get(i)).first());
            blockId = (int) java.lang.Math.floor((granularity * value) / thresholds.get(i));
            blockIds.add(blockId);
        }
        return blockIds;
    }

    /**
     * Computes all the block ids for a given instance. If it is known that
     * the coordinates of an instance are unique, then use getBlockId. If not, use
     * this method.
     *
     * @param a Instance, whose ids are to be returned
     * @return An ArrayList of blockids
     */
    public ArrayList<ArrayList<Integer>> getAllBlockIds(Instance a) {
        int blockId;
        ArrayList<ArrayList<Integer>> blockIds = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Double>> combinations = new ArrayList<ArrayList<Double>>();
        //get all property combinations
        for (int i = 0; i < dim; i++) {
            combinations = addIdsToList(combinations, a.getProperty(properties.get(i)));
        }
        for (int i = 0; i < combinations.size(); i++) {
            ArrayList<Double> combination = combinations.get(i);
            ArrayList<Integer> block = new ArrayList<Integer>();
            for (int j = 0; j < combination.size(); j++) {
                blockId = (int) java.lang.Math.floor((granularity * combination.get(j)) / thresholds.get(j));
                block.add(blockId);
            }
            blockIds.add(block);
        }
        return blockIds;
    }

    /**
     * Computes all the block ids for a given instance. If it is known that
     * the coordinates of an instance are unique, then use getBlockId. If not, use
     * this method.
     *
     * @param a Instance, whose ids are to be returned
     * @return An ArrayList of blockids
     */
    public ArrayList<ArrayList<Integer>> getAllSourceIds(Instance a, String props) {
        int blockId;
        String sourceProps[] = props.split("\\|");

        ArrayList<ArrayList<Integer>> blockIds = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Double>> combinations = new ArrayList<ArrayList<Double>>();
        //get all property combinations
        for (int i = 0; i < dim; i++) {
            combinations = addIdsToList(combinations, a.getProperty(sourceProps[i]));
        }
        for (int i = 0; i < combinations.size(); i++) {
            ArrayList<Double> combination = combinations.get(i);
            ArrayList<Integer> block = new ArrayList<Integer>();
            for (int j = 0; j < combination.size(); j++) {
                blockId = (int) java.lang.Math.floor((granularity * combination.get(j)) / thresholds.get(j));
                block.add(blockId);
            }
            blockIds.add(block);
        }
        return blockIds;
    }
}
