package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

/**
 * Class to Select DBPLinkMDB Dataset for the evaluation.
 * Using this class you can perform a variety of functions on the selected dataset.
 * You can use the following functions like getName, getDataSetFolder, getConfigFile, getReferenceFile,
 * getSourceFile, getTargetFile, getSourceClass, getTargetClass, getEvaluationFilename etc.
 *
 * @author Cedric Richter
 *
 */

public class DBPLinkMDBDataset extends BaseDataSet {
    /**
     * @return the NameOfDataSet
     */
    @Override
    public String getName() {
        return "DBPLINKEDMDB";
    }

    /**
     * @return the BaseFolder
     */
    @Override
    public String getDataSetFolder() {
        return getBaseFolder() + "dbpedia-linkedmdb/";
    }

    /**
     * @return the ConfigFile
     */
    @Override
    public String getConfigFile() {
        return "dbpedia-linkedmdb.xml";
    }

    /**
     * @return the ReferenceFile
     */

    @Override
    public String getReferenceFile() {
        return "reference.csv";
    }

    /**
     * @return the SourceFile
     */

    @Override
    public String getSourceFile() {
        return "source.csv";
    }

    /**
     * @return the TargetFile
     */

    @Override
    public String getTargetFile() {
        return "target.csv";
    }

    /**
     * @return the SourceClass
     */
    @Override
    public String getSourceClass() {
        return "dbpedia:film";
    }


    /**
     * @return the TargetClass
     */
    @Override
    public String getTargetClass() {
        return "linkedmdb:movie";
    }


    /**
     * @return the EvaluationFileName
     */
    @Override
    public String getEvaluationFilename() {
        return "Pseudo_eval_DBPedia-LinkedMDB.csv";
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
