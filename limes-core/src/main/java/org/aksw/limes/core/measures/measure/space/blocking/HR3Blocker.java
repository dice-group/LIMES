/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.measure.space.blocking;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.space.ISpaceMeasure;
import org.aksw.limes.core.measures.measure.space.SpaceMeasureFactory;
import org.apache.log4j.Logger;

/**
 * This class implements the HR3 algorithm which can ensure that a certain RRR
 * is reached.
 * @author ngonga
 */
public class HR3Blocker implements BlockingModule {
        static Logger logger = Logger.getLogger("LIMES");
    int dim = 2;
    ArrayList<Double> thresholds;
    ArrayList<String> properties;
    ISpaceMeasure measure;
    Instance zero;
    int granularity;
    HashMap<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> cache;
    public HR3Blocker(String props, String measureName, double threshold) {
        logger.info("Using HR3");
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
        cache = new HashMap<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> ();
    }

    //Simple init meant to reuse the getBlocksTocompare methods
    public HR3Blocker(int granularity, int dimensions)
    {
        this.granularity = granularity;
        dim = dimensions;
    }
    
    public HR3Blocker(String props, String measureName, double threshold, int _granularity) {
//        logger.info("Using HR3");
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
        cache = new HashMap<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> ();
    }

    public void setGranularity(int n) {
        granularity = n;
    }

    /** Computes the blocks that are to be compared with a given block
     * @param blockId ID of the block for which comparisons are needed
     * @return List of IDs that are to be compared
     */
    public ArrayList<ArrayList<Integer>> getBlocksToCompare(ArrayList<Integer> blockId) {
        int dim = blockId.size();
        if (dim == 0) {
            return new ArrayList<ArrayList<Integer>>();
        }
//        if(cache.containsKey(blockId)) 
//        {
//            return cache.get(blockId);
//        }
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> hr3result = new ArrayList<ArrayList<Integer>>();
        result.add(blockId);

        ArrayList<ArrayList<Integer>> toAdd;
        ArrayList<Integer> id;

        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < Math.pow(2 * granularity + 1, i); j++) {
                //System.out.println("Result"+result);
                id = result.get(j);
                //System.out.println(j+" -> "+id);
                toAdd = new ArrayList<ArrayList<Integer>>();
                for (int k = 0; k < 2 * granularity; k++) {
                    toAdd.add(new ArrayList<Integer>());
                }
                //System.out.println(toAdd.size());
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
                    //System.out.println(i+". "+(k+1)+". "+toAdd);
                }
                //Merge results
                for (int l = 0; l < 2 * granularity; l++) {
                    result.add(toAdd.get(l));
                }
            }
        }
        
        //now run hr3 check 
        int alphaPowered = (int)Math.pow(granularity, dim);
        ArrayList<Integer> block;
        int hr3Index;
        int index;
        for(int i=0; i<result.size(); i++)
        {            
            hr3Index = 0;
            block = result.get(i);
            for(int j=0; j<dim; j++)
            {
                if(block.get(j) == blockId.get(j))
                {
                    hr3Index = 0;
                    break;
                }
                else
                {
                    index = (Math.abs(blockId.get(j) - block.get(j)) - 1);
                    hr3Index = hr3Index + (int)Math.pow(index, dim);
                }
            }
            if(hr3Index < alphaPowered) hr3result.add(block);
        }
      
        return hr3result;
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

    /** Computes all the block ids for a given instance. If it is known that
     * the coordinates of an instance are unique, then use getBlockId. If not, use
     * this method.
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
        for(int i=0; i<combinations.size(); i++)
        {
            ArrayList<Double> combination = combinations.get(i);
            ArrayList<Integer> block = new ArrayList<Integer>();
            for(int j=0; j<combination.size();j++)
            {
                blockId = (int) java.lang.Math.floor((granularity * combination.get(j)) / thresholds.get(j));
                block.add(blockId);
            }
            blockIds.add(block);
        }
        return blockIds;
    }
    
        /** Computes all the block ids for a given instance. If it is known that
     * the coordinates of an instance are unique, then use getBlockId. If not, use
     * this method.
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
        for(int i=0; i<combinations.size(); i++)
        {
            ArrayList<Double> combination = combinations.get(i);
            ArrayList<Integer> block = new ArrayList<Integer>();
            for(int j=0; j<combination.size();j++)
            {
                blockId = (int) java.lang.Math.floor((granularity * combination.get(j)) / thresholds.get(j));
                block.add(blockId);
            }
            blockIds.add(block);
        }
        return blockIds;
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
            try
            {
                value = Double.parseDouble(s);
            }
            catch(Exception e)
            {
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

    public static void main(String args[]) {

        ArrayList<ArrayList<Double>> keys = new ArrayList<ArrayList<Double>>();
        TreeSet<String> key = new TreeSet<String>();
        key.add(1.0 + "");
        key.add(2.0 + "");

        TreeSet<String> key2 = new TreeSet<String>();
        key2.add(1.0 + "");
        key2.add(2.0 + "");

        TreeSet<String> key3 = new TreeSet<String>();
        key3.add(1.0 + "");
        key3.add(2.0+"");

        
        System.out.println(keys);
        keys = addIdsToList(keys, key);
        System.out.println(keys);
        keys = addIdsToList(keys, key2);
        System.out.println(keys);
        keys = addIdsToList(keys, key3);
        System.out.println(keys);


        //MemoryCache cache = new MemoryCache();
        /**
        cache.addTriple("A", "lon", "1");
        cache.addTriple("A", "lat", "1");
        cache.addTriple("B", "lon", "2");
        cache.addTriple("B", "lat", "1");

        HR3Blocker blocker = new HR3Blocker("lon|lat", "euclidean", 0.5);

        blocker.setGranularity(3);

        System.out.println(blocker.getBlockId(cache.getInstance("A")));
        System.out.println(blocker.getBlockId(cache.getInstance("B")));

        ArrayList<Integer> blockId = new ArrayList<Integer>();
        blockId.add(0);
        blockId.add(0);
        System.out.println(blocker.getBlocksToCompare(blockId).size());
        System.out.println(blocker.getBlocksToCompare(blockId));
         * */
    }
}
