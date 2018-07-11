package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;

/**
 * Class to Select RestaurantCSV Dataset for the evaluation.
 * Using this class you can perform a variety of functions on the selected dataset.
 * You can use the following functions like getName, getDataSetFolder, getConfigFile, getReferenceFile,
 * getSourceFile, getTargetFile, getSourceClass, getTargetClass, getEvaluationFilename etc.
 *
 * @author Cedric Richter
 *
 */


public class RestaurantCSV extends BaseDataSet {

    /**
     * @return the NameOfDataSet
     */

    @Override
    public String getName() {
        return "restaurants";
    }

    /**
     * @return the BaseFolder
     */

    @Override
    public String getDataSetFolder() {
        return super.getBaseFolder() + "Restaurants/";
    }

    /**
     * @return the ConfigFile
     */
    @Override
    public String getConfigFile() {
        return "restaurants_csv.xml";
    }

    /**
     * @return the ReferenceFile
     */
    @Override
    public String getReferenceFile() {
        return "restaurant1_restaurant2_goldstandard.rdf.csv";
    }

    /**
     * @return the SourceFile
     */
    @Override
    public String getSourceFile() {
        return "restaurant1.nt";
    }

    /**
     * @return the TargetFile
     */
    @Override
    public String getTargetFile() {
        return "restaurant2.nt";
    }


    /**
     * @return the SourceClass
     */
    @Override
    public String getSourceClass() {
        return "http://www.okkam.org/ontology_restaurant1.owl#Restaurant";
    }

    /**
     * @return the TargetClass
     */
    @Override
    public String getTargetClass() {
        return "http://www.okkam.org/ontology_restaurant2.owl#Restaurant";
    }


    /**
     * @return the EvaluationFileName
     */
    @Override
    public String getEvaluationFilename() {
        return "Pseudo_eval_Restaurants.csv";
    }

    /**
     * @return OAEIType
     */
    @Override
    public String getOAEIType() {
        return "-Restaurant";
    }
    /**
     * @return Resolved Paths of DataSet using OAEIDataSetIO
     */
    @Override
    public IDataSetIO getIO() {
        return new OAEIDataSetIO();
    }

}
