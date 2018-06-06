package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

public class AbtBuyDataSet extends BaseDataSet {
    @Override
    public String getName() {
        return "AbtBuy";
    }

    @Override
    public String getDataSetFolder() {
        return getBaseFolder() + "Abt-Buy/";
    }

    @Override
    public String getConfigFile() {
        return "Abt-Buy.xml";
    }

    @Override
    public String getReferenceFile() {
        return "abt_buy_perfectMapping.csv";
    }

    @Override
    public String getSourceFile() {
        return "Abt.csv";
    }

    @Override
    public String getTargetFile() {
        return "Buy.csv";
    }

    @Override
    public String getSourceClass() {
        return "abt:product";
    }

    @Override
    public String getTargetClass() {
        return "buy:product";
    }

    @Override
    public String getEvaluationFilename() {
        return "Pseudo_eval_Abt-Buy.csv";
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
