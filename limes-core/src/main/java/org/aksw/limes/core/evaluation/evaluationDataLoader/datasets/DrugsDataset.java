package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

/**
 * Class to Select Drugs Dataset for the evaluation.
 * Using this class you can perform a variety of functions on the selected dataset.
 * You can use the following functions like getName, getDataSetFolder, getConfigFile, getReferenceFile,
 * getSourceFile, getTargetFile, getSourceClass, getTargetClass, getEvaluationFilename etc.
 *
 * @author Cedric Richter
 *
 */

public class DrugsDataset extends BaseDataSet {
    /**
     * @return the NameOfDataSet
     */

    @Override
    public String getName() {
        return "DRUGS";
    }

    /**
     * @return the BaseFolder
     */

    @Override
    public String getDataSetFolder() {
        return getBaseFolder() + "dailymed-drugbank-ingredients/";
    }

    /**
     * @return the ConfigFile
     */

    @Override
    public String getConfigFile() {
        return "dailymed-drugbank.xml";
    }

    /**
     * @return the ReferenceFile
     */
    @Override
    public String getReferenceFile() {
        return "reference2.csv";
    }

    /**
     * @return the SourceFile
     */
    @Override
    public String getSourceFile() {
        return "source2.csv";
    }


    /**
     * @return the TargetFile
     */
    @Override
    public String getTargetFile() {
        return "target2.csv";
    }

    /**
     * @return the SourceClass
     */
    @Override
    public String getSourceClass() {
        return "dailymed:drug";
    }

    /**
     * @return the TargetClass
     */
    @Override
    public String getTargetClass() {
        return "drugbank:drug";
    }

    /**
     * @return the EvaluationFileName
     */
    @Override
    public String getEvaluationFilename() {
        return "Pseudo_eval_Drugs.csv";
    }

    @Override
    public String getOAEIType() {
        return null;
    }

    /**
     * @return a new Oracle
     */
    @Override
    public IDataSetIO getIO() {
        return new OracleIO();
    }
}
