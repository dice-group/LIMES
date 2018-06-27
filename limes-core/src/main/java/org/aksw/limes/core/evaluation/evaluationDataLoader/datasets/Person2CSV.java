package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

public class Person2CSV extends BaseDataSet {

    /**
     * @return the NameOfDataSet
     */
    @Override
    public String getName() {
        return "Persons2_CSV";
    }

    /**
     * @return the BaseFolder
     */
    @Override
    public String getDataSetFolder() {
        return super.getBaseFolder() + "Persons2";
    }

    /**
     * @return the ConfigFile
     */
    @Override
    public String getConfigFile() {
        return "persons2_csv.xml";
    }

    /**
     * @return the ReferenceFile
     */
    @Override
    public String getReferenceFile() {
        return "dataset21_dataset22_goldstandard_person.xml.csv";
    }

    /**
     * @return the SourceFile
     */
    @Override
    public String getSourceFile() {
        return "person21.nt";
    }

    /**
     * @return the TargetFile
     */
    @Override
    public String getTargetFile() {
        return "person22.nt";
    }

    /**
     * @return the SourceClass
     */
    @Override
    public String getSourceClass() { return "http://www.okkam.org/ontology_person1.owl#Person"; }

    /**
     * @return the TargetClass
     */
    @Override
    public String getTargetClass() {
        return "okkamperson2:Person";
    }

    /**
     * @return the EvaluationFileName
     */
    @Override
    public String getEvaluationFilename() {
        return "Pseudo_eval_Persons2.csv";
    }


    /**
     * @return OAEIType
     */
    @Override
    public String getOAEIType() {
        return "-Person";
    }


    /**
     * @return get OAEIDataSetIO utils to load dataSet
     */
    @Override
    public IDataSetIO getIO() {
        return new OAEIDataSetIO();
    }


}
