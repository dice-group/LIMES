/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.space.blocking;


import java.util.ArrayList;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.space.ISpaceMeasure;
import org.aksw.limes.core.measures.measure.space.SpaceMeasureFactory;
import org.apache.log4j.Logger;

/**
 * Not tested yet. Works on the angles by transforming the thresholds into angular
 * thresholds. Does everything modulo 90 and 180°.
 * @author ngonga
 */
public class GeoBlockingModule implements BlockingModule {

    static Logger logger = Logger.getLogger("LIMES");
    int dim = 2;
    ArrayList<Double> thresholds;
    ArrayList<String> properties;
    ISpaceMeasure measure;
    Instance zero;
    int latLimit, longLimit;

    public GeoBlockingModule(String props, String measureName, double threshold) {
        thresholds = new ArrayList<Double>();
        properties = new ArrayList<String>();
        String[] split = props.split("\\|");
        dim = split.length;
        if (dim > 2) {
            logger.warn("Dimensions higher than 2. Hope we are on a " + (dim + 1) + "D planet.");
        }
        measure = SpaceMeasureFactory.getMeasure(measureName, dim);
        for (int i = 0; i < dim; i++) {
            thresholds.add(measure.getThreshold(i, threshold));
            properties.add(split[i]);
            if (split[i].toLowerCase().startsWith("la")) {
                latLimit = (int) (90 / thresholds.get(i));
            }
            if (split[i].toLowerCase().startsWith("lo")) {
                longLimit = (int) (180 / thresholds.get(i));
            }
        }
    }

    /** Generate IDs for blocks
     *
     * @param blockId ID of the input point
     * @return IDs of blocks for points to map
     */
    public ArrayList<ArrayList<Integer>> getBlocksToCompare(ArrayList<Integer> blockId) {
        int dim = blockId.size();
        if (dim == 0) {
            return new ArrayList<ArrayList<Integer>>();
        }

        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        result.add(blockId);

        ArrayList<Integer> minus = new ArrayList<Integer>();
        ArrayList<Integer> plus = new ArrayList<Integer>();
        ArrayList<Integer> copy = new ArrayList<Integer>();
        for (int i = 0; i < dim; i++) {
            minus.add(0);
            copy.add(0);
            plus.add(0);
        }
        ArrayList<Integer> id;

        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < Math.pow(3, i); j++) {
                //System.out.println("Result"+result);
                id = result.get(j);
                //System.out.println(j+" -> "+id);
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

        int value;
        for (int i = 0; i < result.size(); i++) {
            for (int j = 0; j < dim; j++) {
                value = result.get(i).get(j);
                if (properties.get(i).startsWith("la")) {

                    if (value > latLimit) {
                        result.get(i).set(j, value - 2*latLimit);
                    } else if (value < latLimit) {
                        result.get(i).set(j, value + 2*latLimit);
                    }
                }
                if (properties.get(i).startsWith("lo")) {
                    if (value > longLimit) {
                        result.get(i).set(j, value - 2*longLimit);
                    } else if (value < longLimit) {
                        result.get(i).set(j, value + 2*longLimit);
                    }
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

            if (properties.get(i).startsWith("la")) {
                if (value > latLimit) {
                    value = value - 2*latLimit;
                } else if (value < latLimit) {
                    value = value + 2*latLimit;
                }
            }
            if (properties.get(i).startsWith("lo")) {
                if (value > longLimit) {
                    value = value - 2*longLimit;
                } else if (value < longLimit) {
                    value = value + 2*longLimit;
                }
            }
            blockId = (int) java.lang.Math.floor(value / thresholds.get(i));
            blockIds.add(blockId);
        }
        return blockIds;
    }

    /** We assume that every point has exactly one longitude and one latitude
     * 
     * @param a Instance with lat and long
     * @return Block id of instance
     */
    public ArrayList<ArrayList<Integer>> getAllBlockIds(Instance a) {
        ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
        list.add(getBlockId(a));
            return list;
    }
    
        /** We assume that every point has exactly one longitude and one latitude
     * 
     * @param a Instance with lat and long
     * @return Block id of instance
     */
    public ArrayList<ArrayList<Integer>> getAllSourceIds(Instance a, String props) {
        String[] sourceProps = props.split("\\|");
        ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();

        int blockId;
        ArrayList<Integer> blockIds = new ArrayList<Integer>();
        double value;
        for (int i = 0; i < dim; i++) {
            value = Double.parseDouble(a.getProperty(sourceProps[i]).first());

            if (sourceProps[i].startsWith("la")) {
                if (value > latLimit) {
                    value = value - 2*latLimit;
                } else if (value < latLimit) {
                    value = value + 2*latLimit;
                }
            }
            if (sourceProps[i].startsWith("lo")) {
                if (value > longLimit) {
                    value = value - 2*longLimit;
                } else if (value < longLimit) {
                    value = value + 2*longLimit;
                }
            }
            blockId = (int) java.lang.Math.floor(value / thresholds.get(i));

            blockIds.add(blockId);
        }
        
        return list;
    }
}
