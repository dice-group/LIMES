package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

public class Person2DataSet extends BaseDataSet {
    @Override
    public String getName() {
        return "Person2";
    }

    @Override
    public String getDataSetFolder() {
        return super.getBaseFolder() + "Persons2/";
    }

    @Override
    public String getConfigFile() {
        return "persons2.xml";
    }

    @Override
    public String getReferenceFile() {
        return "dataset21_dataset22_goldstandard_person.xml";
    }

    @Override
    public String getSourceFile() {
        return "person21.nt";
    }

    @Override
    public String getTargetFile() {
        return "person22.nt";
    }

    @Override
    public String getSourceClass() {
        return "http://www.okkam.org/ontology_person1.owl#Person";
    }

    @Override
    public String getTargetClass() {
        return "okkamperson2:Person";
    }

    @Override
    public String getEvaluationFilename() {
        return "Pseudo_eval_Persons2.csv";
    }

    @Override
    public String getOAEIType() {
        return "-Person";
    }

    @Override
    public IDataSetIO getIO() {
        return new OAEIDataSetIO();
    }
}
