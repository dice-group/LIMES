package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSet;

/**
 * Class to Select Base Folder of Datasets for the evaluation.
 * Using this class we define the base folder of the different datasets.
 *
 * @author Cedric Richter
 *
 */

public abstract class BaseDataSet implements IDataSet {

    /**
     * @return path of the base folder
     */

    @Override
    public String getBaseFolder() {
        return "src/main/resources/datasets/";
    }


}
