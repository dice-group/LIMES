package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;


/**
 *  This class is used to select RestaurantsFixedDataSet
 * Basically this is another implementation of RestaurantsDataSet that supports immutability.
 * You can load the source and target cache, provide it with a configuration and fix reference mapping like FixedOAEIDataSetIO.
 *
 * @author Cedric Richter
 *
 */

public class RestaurantsFixedDataSet extends RestaurantsDataSet {

    @Override
    public String getName(){
        return "restaurantsfixed";
    }

    @Override
    public IDataSetIO getIO(){
        return new FixedOAEIDataSetIO();
    }

}
