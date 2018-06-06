package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

public class DBLPACMDataSet extends BaseDataSet {
    @Override
    public String getName() {
        return "DBLPACM";
    }

    @Override
    public String getDataSetFolder() {
        return getBaseFolder() + "DBLP-ACM/";
    }

    @Override
    public String getConfigFile() {
        return "PublicationData.xml";
    }

    @Override
    public String getReferenceFile() {
        return "DBLP-ACM_perfectMapping.csv";
    }

    @Override
    public String getSourceFile() {
        return "DBLP2.csv";
    }

    @Override
    public String getTargetFile() {
        return "ACM.csv";
    }

    @Override
    public String getSourceClass() {
        return "dblp:book";
    }

    @Override
    public String getTargetClass() {
        return "acm:book";
    }

    @Override
    public String getEvaluationFilename() {
        return "Pseudo_eval_DBLP-ACM.csv";
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
