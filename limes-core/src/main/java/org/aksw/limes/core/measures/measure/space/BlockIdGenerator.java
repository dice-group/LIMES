/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.aksw.limes.core.measures.measure.space;

import java.util.ArrayList;

import org.aksw.limes.core.io.cache.Instance;

/**
 *
 * @author ngonga
 */
public class BlockIdGenerator {
    int dim = 2;
    ArrayList<Double> thresholds;
    ArrayList<String> properties;
    ISpaceMeasure measure;
    Instance zero;

    /** Initializes the generator. The basic idea here is the following: First, pick
     * a random instance origin. That is the center upon which the block ids will be computed.
     * Each measure can return the threshold for blocking that is equivalent to the similarity
     * threshold given in by the user. For euclidean metrics, this value is the same. Yet,
     * for metrics that squeeze space, this might not be the case. It is important to notice that
     * the generation assumes that the size of props.split("|") is the same as dimensions.
     * @param origin The random instance used as reference for computing block ids
     * @param props List of properties that make up each dimension
     * @param measureName Name of the measure to be used to compute the similarity of instances
     * @param dimensions Number of dimensions to be considered
     * @param threshold General similarity threshold for blocking
     */    

    public BlockIdGenerator(String props, String measureName, double threshold)
    {
        thresholds = new ArrayList<Double>();
        properties = new ArrayList<String>();
        String[] split = props.split("\\|");
        dim = split.length;
        measure = SpaceMeasureFactory.getMeasure(measureName, dim);
        for(int i=0; i<dim; i++)
        {
            thresholds.add(measure.getThreshold(i, threshold));
            properties.add(split[i]);
        }
    }
    /** Computes the block ID for a given instance a. The idea behind the blocking
     * is to tile the target space into blocks of dimension thresdhold^dimensions.
     * Each instance s from the source space is then compared with the blocks lying
     * directly around s's block and the block where s is.
     * @param a The instance whose blockId is to be computed
     * @return The ID for the block of a
     */
    public ArrayList<Integer> getBlockId(Instance a)
    {
        int blockId;
        ArrayList<Integer> blockIds = new ArrayList<Integer>();
        double value;
        for(int i=0; i<dim; i++)
        {
            value = Double.parseDouble(a.getProperty(properties.get(i)).first());
            blockId = (int)(java.lang.Math.floor((value + thresholds.get(i)/2)/thresholds.get(i)));
            blockIds.add(blockId);
        }
        return blockIds;
    }

    /** Returns the ids of all the blocks surrounding a given block for comparison
     * Will be extremely useful for parallelizing as we can use blocking on T and S
     * as then put use locality
     * @param blockId
     * @return
     */
    public static ArrayList<ArrayList<Integer>> getBlocksToCompare(ArrayList<Integer> blockId)
    {
        int dim = blockId.size();
        if(dim == 0) return new ArrayList<ArrayList<Integer>>();

        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        result.add(blockId);

        ArrayList<Integer> minus = new ArrayList<Integer>();
        ArrayList<Integer> plus = new ArrayList<Integer>();
        ArrayList<Integer> copy = new ArrayList<Integer>();
        for(int i=0; i<dim; i++)
        {
            minus.add(0);
            copy.add(0);
            plus.add(0);
        }
        ArrayList<Integer> id;

        for(int i = 0; i < dim; i++)
        {
            for(int j = 0; j < Math.pow(3, i); j++)
            {
                //System.out.println("Result"+result);
                id = result.get(j);
                //System.out.println(j+" -> "+id);
                minus = new ArrayList<Integer>();
                plus = new ArrayList<Integer>();
                for(int k = 0; k < dim; k++)
                {                                        
                    if(k!=i)
                    {
                        minus.add(id.get(k));
                        plus.add(id.get(k));
                    }
                    else
                    {
                        minus.add(id.get(k)-1);
                        plus.add(id.get(k)+1);
                    }
                }
                result.add(minus);
                result.add(plus);
            }                       
        }
        return result;
    }


    public static void main(String args[])
    {
        ArrayList<Integer> blockId = new ArrayList<Integer>();
        ArrayList<Integer> blockId2 = new ArrayList<Integer>();
        blockId.add(0); blockId2.add(0);
        blockId.add(0);blockId2.add(0);
        blockId.add(0);blockId2.add(0);
        System.out.println(BlockIdGenerator.getBlocksToCompare(blockId));
        System.out.println(blockId.equals(blockId2));
    }
}
