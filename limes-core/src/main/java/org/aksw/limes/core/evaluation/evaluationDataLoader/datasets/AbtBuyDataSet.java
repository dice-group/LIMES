package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

/**
 * /**
 * Class to Select AbtBuy Dataset for the evaluation.
 * Using this class you can perform a variety of functions on the selected dataset.
 * You can use the following functions like getName, getDataSetFolder, getConfigFile, getReferenceFile,
 * getSourceFile, getTargetFile, getSourceClass, getTargetClass, getEvaluationFilename etc.
 *
 * @author Cedric Richter
 *
 */

public class AbtBuyDataSet extends BaseDataSet {

    /**
     * @return the NameOfDataSet
     */
    @Override
    public String getName() {
        return "AbtBuy";
    }

    /**
     * @return the BaseFolder
     */
    @Override
    public String getDataSetFolder() {
        return getBaseFolder() + "Abt-Buy/";
    }

    /**
     * @return the ConfigFile
     */
    @Override
    public String getConfigFile() {
        return "Abt-Buy.xml";
    }

    /**
     * @return the ReferenceFile
     */
    @Override
    public String getReferenceFile() {
        return "abt_buy_perfectMapping.csv";
    }

    /**
     * @return the SourceFile
     */
    @Override
    public String getSourceFile() {
        return "Abt.csv";
    }

    /**
     * @return the TargetFile
     */
    @Override
    public String getTargetFile() {
        return "Buy.csv";
    }

    /**
     * @return the SourceClass
     */
    @Override
    public String getSourceClass() {
        return "abt:product";
    }

    /**
     * @return the TargetClass
     */
    @Override
    public String getTargetClass() {
        return "buy:product";
    }

    /**
     * @return the EvaluationFileName
     */
    @Override
    public String getEvaluationFilename() {
        return "Pseudo_eval_Abt-Buy.csv";
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
