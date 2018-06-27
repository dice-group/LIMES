package org.aksw.limes.core.evaluation.evaluationDataLoader;

/**
 * Interface class for DataSet, contains the abstract methods that BaseDataSet can implement.
 *
 * @author Cedric Richter
 */

public interface IDataSet {

    /**
     * @return the NameOfDataSet
     */

    public String getName();

    /**
     * @return the BaseFolder
     */

    public String getBaseFolder();

    /**
     * @return the DataSetFolder
     */

    public String getDataSetFolder();

    /**
     * @return the ConfigFile
     */

    public String getConfigFile();

    /**
     * @return the getReferenceFile
     */
    public String getReferenceFile();

    /**
     * @return the SourceFile
     */

    public String getSourceFile();

    /**
     * @return the TargetFile
     */

    public String getTargetFile();

    /**
     * @return the SourceClass
     */

    public String getSourceClass();

    /**
     * @return the TargetClass
     */

    public String getTargetClass();

    /**
     * @return the EvaluationFileName
     */

    public String getEvaluationFilename();

    /**
     * @return a new OAEIType
     */
    public String getOAEIType();

    /**
     * @return a new Oracle
     */
    public IDataSetIO getIO();

}
