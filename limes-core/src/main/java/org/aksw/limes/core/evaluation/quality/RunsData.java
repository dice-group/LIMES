package org.aksw.limes.core.evaluation.quality;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mofeed
 *
 */
public class RunsData implements QualitativeMeasure{

	// list of recorded runs, each with its information like id,time,memory,.....
	protected Map<Long, RunRecord> runs = new HashMap<Long, RunRecord>();
	
	// retrieves a run information based on giben Id
	@Override
	public RunRecord getRun(long runId) {
		return runs.get(Long.valueOf(runId));
	}

	// retrieves all runs
	@Override
	public List<RunRecord> getRuns() {
		return (List<RunRecord>) runs.values();
	}

	//set a run's information
	@Override
	public void setRun(long runId, RunRecord record) {
		runs.put(Long.valueOf(runId), record);
		
	}

	// get information of specific run
	@Override
	public double getRunInfo(long runId, String Info) {
		if(Info.equals("Id"))
			return runs.get(Long.valueOf(runId)).getRunId();
		else if(Info.equals("Time"))
			return runs.get(Long.valueOf(runId)).getRunTime();
		else if(Info.equals("Memory"))
			return runs.get(Long.valueOf(runId)).getRunMemory();
		else return 0;
	}

	// adding new run to list
	@Override
	public void addRun(RunRecord record) {
		runs.put(record.getRunId(),record);
		
	}

}
