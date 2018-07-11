package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;
/**
 * Class to Select DBLPScholar Dataset for the evaluation.
 * Using this class you can perform a variety of functions on the selected dataset.
 * You can use the following functions like getName, getDataSetFolder, getConfigFile, getReferenceFile,
 * getSourceFile, getTargetFile, getSourceClass, getTargetClass, getEvaluationFilename etc.
 *
 * @author Cedric Richter
 *
 */

public class DBLPScholorDataSet extends BaseDataSet {
    /**
     * @return the NameOfDataSet
     */

    @Override
    public String getName() {
        return "DBLPSCHOLAR";
    }

    /**
     * @return the BaseFolder
     */

    @Override
    public String getDataSetFolder() {
        return getBaseFolder() + "DBLP-Scholar/";
    }

    /**
     * @return the ConfigFile
     */

    @Override
    public String getConfigFile() {
        return "DBLP-Scholar.xml";
    }

    /**
     * @return the ReferenceFile
     */

    @Override
    public String getReferenceFile() {
        return "DBLP-Scholar_perfectMapping.csv";
    }

    /**
     * @return the SourceFile
     */

    @Override
    public String getSourceFile() {
        return "DBLP1.csv";
    }

    /**
     * @return the TargetFile
     */

    @Override
    public String getTargetFile() {
        return "Scholar.csv";
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
        return "scholar:book";
    }

    /**
     * @return the EvaluationFileName
     */

    @Override
    public String getEvaluationFilename() {
        return "Pseudo_eval_DBLP-Scholar.csv";
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
