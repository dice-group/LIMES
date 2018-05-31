package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

public class PersonNewDataSet extends BaseDataSet {
    @Override
    public String getName() {
        return "PersonNew";
    }

    @Override
    public String getDataSetFolder() {
        return super.getBaseFolder() + "Person1/";
    }

    @Override
    public String getConfigFile() {
        return "personsNew.xml";
    }

    @Override
    public String getReferenceFile() {
        return "dataset11_dataset12_goldstandard_person.xml";
    }

    @Override
    public String getSourceFile() {
        return "person11.nt";
    }

    @Override
    public String getTargetFile() {
        return "person12.nt";
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
        return "Pseudo_eval_Persons1.csv";
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
