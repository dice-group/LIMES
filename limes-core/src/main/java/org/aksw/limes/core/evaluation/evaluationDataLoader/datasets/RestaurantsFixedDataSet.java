package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

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
