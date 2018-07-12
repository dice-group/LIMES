package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;


/**
 * A dataset for cities coming from DBpedia and Wikidata
 * @author Zohaib Shoket
 */
public class CitiesDataSet extends BaseDataSet {

    /**
     * @return the NameOfDataSet
     */
    @Override
    public String getName() {
        return "Cities";
    }

    /**
     * @return the BaseFolder
     */
    @Override
    public String getDataSetFolder() {
        return getBaseFolder() + "Cities/";
    }

    /**
     * @return the ConfigFile
     */
    @Override
    public String getConfigFile() {
        return "CityDataSet.xml";
    }

    /**
     * @return the ReferenceFile
     */
    @Override
    public String getReferenceFile() {
        return "CitiesDataGoldStandard.csv";
    }

    /**
     * @return the SourceFile
     */
    @Override
    public String getSourceFile() {
        return "CityDataSetDBpedia.nt";
    }

    /**
     * @return the TargetFile
     */
    @Override
    public String getTargetFile() {
        return "CityDatasetWIKI.nt";
    }

    /**
     * @return the SourceClass
     */
    @Override
    public String getSourceClass() {
        return "http://www.w3.org/2002/07/owl#City";
    }

    /**
     * @return the TargetClass
     */
    @Override
    public String getTargetClass() { return "http://www.bigdata.com/rdf#City"; }

    /**
     * @return the EvaluationFileName
     */
    @Override
    public String getEvaluationFilename() {
        return "Pseudo_eval_City.csv";
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
