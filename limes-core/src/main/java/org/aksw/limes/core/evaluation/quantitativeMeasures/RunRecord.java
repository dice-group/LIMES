package org.aksw.limes.core.evaluation.quantitativeMeasures;

/**
 * The class represents the structure of run record where the run quantitative information are recorded
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class RunRecord {
    /** The id  number of the run*/
    private long runId;
    /** The time duration of the run*/
    private double runTime;
    /** The memory space utilized by the run*/
    private double runMemory = 0;

    public RunRecord() {}
    /**
     * @param runId the id of the run
     * @param runTime the time duration recorded by the run
     */
    public RunRecord(long runId, double runTime) {
        this.runId = runId;
        this.runTime = runTime;
    }

    /**
     * @param runId the id of the run
     * @param runTime the time duration recorded by the run
     * @param runMemory the size of the utilized memory
     */
    public RunRecord(long runId, double runTime, double runMemory) {
        this(runId, runTime);
        this.runMemory = runMemory;
    }


    public long getRunId() {
        return runId;
    }

    public void setRunId(long runId) {
        this.runId = runId;
    }

    public double getRunTime() {
        return runTime;
    }

    public void setRunTime(double runTime) {
        this.runTime = runTime;
    }

    public double getRunMemory() {
        return runMemory;
    }

    public void setRunMemory(double runMemory) {
        this.runMemory = runMemory;
    }

    @Override
    public boolean equals(Object run) {
        return (runId == ((RunRecord) run).getRunId());
    }

    @Override
    public String toString() {
        return runId + ":" + runTime + ":" + runMemory;
    }
}
