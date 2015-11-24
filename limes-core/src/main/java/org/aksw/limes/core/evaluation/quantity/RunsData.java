package org.aksw.limes.core.evaluation.quantity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mofeed
 *
 */
public class RunsData implements QuantitativeMeasure{

	protected Map<Long, RunRecord> runs = new HashMap<Long, RunRecord>();
	
	@Override
	public RunRecord getRun(long runId) {
		return runs.get(Long.valueOf(runId));
	}

	@Override
	public List<RunRecord> getRuns() {
		return (List<RunRecord>) runs.values();
	}

	@Override
	public void setRun(long runId, RunRecord record) {
		runs.put(Long.valueOf(runId), record);
		
	}

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

	@Override
	public void addRun(RunRecord record) {
		runs.put(record.getRunId(),record);
		
	}

}
