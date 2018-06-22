package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

public class DBPLinkMDBDataset extends BaseDataSet {
    @Override
    public String getName() {
        return "DBPLINKEDMDB";
    }

    @Override
    public String getDataSetFolder() {
        return getBaseFolder() + "dbpedia-linkedmdb/";
    }

    @Override
    public String getConfigFile() {
        return "dbpedia-linkedmdb.xml";
    }

    @Override
    public String getReferenceFile() {
        return "reference.csv";
    }

    @Override
    public String getSourceFile() {
        return "source.csv";
    }

    @Override
    public String getTargetFile() {
        return "target.csv";
    }

    @Override
    public String getSourceClass() {
        return "dbpedia:film";
    }

    @Override
    public String getTargetClass() {
        return "linkedmdb:movie";
    }

    @Override
    public String getEvaluationFilename() {
        return "Pseudo_eval_DBPedia-LinkedMDB.csv";
    }

    @Override
    public String getOAEIType() {
        return null;
    }

    @Override
    public IDataSetIO getIO() {
        return new OracleIO();
    }
}
