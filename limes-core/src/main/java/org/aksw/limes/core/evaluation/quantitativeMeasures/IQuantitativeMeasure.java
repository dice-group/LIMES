package org.aksw.limes.core.evaluation.quantitativeMeasures;

import java.util.List;


/**
 * An Interface specifies the method signatures to be implemented by all quantitative measures
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public interface IQuantitativeMeasure {
    /**
     * It retrieves the run record with its information
     * @param runId the id of the run
     * @return the run record including its information
     */
    RunRecord getRun(long runId);

    /**
     * It adds the run record with its information
     * @param record the run record including its information
     */
    void addRun(RunRecord record);

    /**
     * It retrieves a set of run records with their information
     * @return it return list of the runs records
     */
    List<RunRecord> getRuns();
    /**
     * It sets the run record with its information
     * @param runId the id of the run
     * @param record the run record data
     */
    void setRun(long runId, RunRecord record);
    /**
     * It gets a specific information from a run record
     * @param runId the id of the run
     * @param Info The name of the requested information
     * @return double -  The value of the required infromation
     */
    double getRunInfo(long runId, String Info);
}
