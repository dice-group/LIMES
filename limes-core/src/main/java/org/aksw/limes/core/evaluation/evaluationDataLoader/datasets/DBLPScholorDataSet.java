package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

public class DBLPScholorDataSet extends BaseDataSet {
    @Override
    public String getName() {
        return "DBLPSCHOLAR";
    }

    @Override
    public String getDataSetFolder() {
        return getBaseFolder() + "DBLP-Scholar/";
    }

    @Override
    public String getConfigFile() {
        return "DBLP-Scholar.xml";
    }

    @Override
    public String getReferenceFile() {
        return "DBLP-Scholar_perfectMapping.csv";
    }

    @Override
    public String getSourceFile() {
        return "DBLP1.csv";
    }

    @Override
    public String getTargetFile() {
        return "Scholar.csv";
    }

    @Override
    public String getSourceClass() {
        return "dblp:book";
    }

    @Override
    public String getTargetClass() {
        return "scholar:book";
    }

    @Override
    public String getEvaluationFilename() {
        return "Pseudo_eval_DBLP-Scholar.csv";
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
