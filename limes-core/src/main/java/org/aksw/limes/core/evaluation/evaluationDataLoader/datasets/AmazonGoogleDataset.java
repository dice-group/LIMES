package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

public class AmazonGoogleDataset extends BaseDataSet {
    @Override
    public String getName() {
        return "amazongoogleproducts";
    }

    @Override
    public String getDataSetFolder() {
        return this.getBaseFolder()+"Amazon-GoogleProducts/";
    }

    @Override
    public String getConfigFile() {
        return "Amazon-GoogleProducts.xml";
    }

    @Override
    public String getReferenceFile() {
        return "Amzon_GoogleProducts_perfectMapping.csv";
    }

    @Override
    public String getSourceFile() {
        return "Amazon.csv";
    }

    @Override
    public String getTargetFile() {
        return "GoogleProducts.csv";
    }

    @Override
    public String getSourceClass() {
        return "amazon:product";
    }

    @Override
    public String getTargetClass() {
        return "google:product";
    }

    @Override
    public String getEvaluationFilename() {
        return "Pseudo_eval_Amazon-GoogleProducts.csv";
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
