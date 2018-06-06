package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSet;
import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

public abstract class BaseDataSet implements IDataSet {

    @Override
    public String getBaseFolder() {
        return "src/main/resources/datasets/";
    }


}
