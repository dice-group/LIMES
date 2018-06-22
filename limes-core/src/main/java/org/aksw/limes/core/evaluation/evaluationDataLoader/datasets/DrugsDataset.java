package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

public class DrugsDataset extends BaseDataSet {
    @Override
    public String getName() {
        return "DRUGS";
    }

    @Override
    public String getDataSetFolder() {
        return getBaseFolder() + "dailymed-drugbank-ingredients/";
    }

    @Override
    public String getConfigFile() {
        return "dailymed-drugbank.xml";
    }

    @Override
    public String getReferenceFile() {
        return "reference2.csv";
    }

    @Override
    public String getSourceFile() {
        return "source2.csv";
    }

    @Override
    public String getTargetFile() {
        return "target2.csv";
    }

    @Override
    public String getSourceClass() {
        return "dailymed:drug";
    }

    @Override
    public String getTargetClass() {
        return "drugbank:drug";
    }

    @Override
    public String getEvaluationFilename() {
        return "Pseudo_eval_Drugs.csv";
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
