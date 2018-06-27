package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

/**
 * Class to Select DBLPACM Dataset for the evaluation.
 * Using this class you can perform a variety of functions on the selected dataset.
 * You can use the following functions like getName, getDataSetFolder, getConfigFile, getReferenceFile,
 * getSourceFile, getTargetFile, getSourceClass, getTargetClass, getEvaluationFilename etc.
 *
 * @author Cedric Richter
 *
 */

public class DBLPACMDataSet extends BaseDataSet {
    /**
     * @return the NameOfDataSet
     */

    @Override
    public String getName() {
        return "DBLPACM";
    }


    /**
     * @return the BaseFolder
     */
    @Override
    public String getDataSetFolder() {
        return getBaseFolder() + "DBLP-ACM/";
    }
    /**
     * @return the ConfigFile
     */

    @Override
    public String getConfigFile() {
        return "PublicationData.xml";
    }

    /**
     * @return the ReferenceFile
     */
    @Override
    public String getReferenceFile() {
        return "DBLP-ACM_perfectMapping.csv";
    }

    /**
     * @return the SourceFile
     */
    @Override
    public String getSourceFile() {
        return "DBLP2.csv";
    }

    /**
     * @return the TargetFile
     */
    @Override
    public String getTargetFile() {
        return "ACM.csv";
    }


    /**
     * @return the SourceClass
     */
    @Override
    public String getSourceClass() {
        return "dblp:book";
    }


    /**
     * @return the TargetClass
     */
    @Override
    public String getTargetClass() {
        return "acm:book";
    }


    /**
     * @return the EvaluationFileName
     */
    @Override
    public String getEvaluationFilename() {
        return "Pseudo_eval_DBLP-ACM.csv";
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
