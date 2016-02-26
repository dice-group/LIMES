package org.aksw.limes.core.evaluation.quality;

/**
 * @author mofeed
 *
 */
public class RunRecord {
	private long runId;
	private double runTime;
	private double runMemory=0;

	/**
	 * @param runId
	 * @param runTime
	 */
	public RunRecord(long runId, double runTime) {
		this.runId = runId;
		this.runTime = runTime;
	}
	/**
	 * @param runId
	 * @param runTime
	 * @param runMemory
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
		return  ( runId == ((RunRecord)run).getRunId());
	}
	@Override
	public String toString() {
		return runId+":"+runTime+":"+runMemory;
	}
}
