package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

public class RestaurantsDataSet extends BaseDataSet {
    @Override
    public String getName() {
        return "Person1";
    }

    @Override
    public String getDataSetFolder() {
        return super.getBaseFolder() + "Restaurants/";
    }

    @Override
    public String getConfigFile() {
        return "restaurants.xml";
    }

    @Override
    public String getReferenceFile() {
        return "restaurant1_restaurant2_goldstandard.rdf";
    }

    @Override
    public String getSourceFile() {
        return "restaurant1.nt";
    }

    @Override
    public String getTargetFile() {
        return "restaurant2.nt";
    }

    @Override
    public String getSourceClass() {
        return "http://www.okkam.org/ontology_restaurant1.owl#Restaurant";
    }

    @Override
    public String getTargetClass() {
        return "http://www.okkam.org/ontology_restaurant2.owl#Restaurant";
    }

    @Override
    public String getEvaluationFilename() {
        return "Pseudo_eval_Restaurants.csv";
    }

    @Override
    public String getOAEIType() {
        return "-Restaurant";
    }

    @Override
    public IDataSetIO getIO() {
        return new OAEIDataSetIO();
    }
}
