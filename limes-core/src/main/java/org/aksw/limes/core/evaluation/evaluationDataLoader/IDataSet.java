package org.aksw.limes.core.evaluation.evaluationDataLoader;

public interface IDataSet {

    public String getName();

    public String getBaseFolder();

    public String getDataSetFolder();

    public String getConfigFile();

    public String getReferenceFile();

    public String getSourceFile();

    public String getTargetFile();

    public String getSourceClass();

    public String getTargetClass();

    public String getEvaluationFilename();

    public String getOAEIType();

    public IDataSetIO getIO();

}
