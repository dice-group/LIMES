package org.aksw.limes.core.evaluation.quantitativeMeasures;

import java.util.List;


/**
 * @author Mofeed Hassan <mounir@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-03
 *
 */
public interface IQuantitativeMeasure {
	RunRecord getRun(long runId);
	void addRun(RunRecord record);
	List<RunRecord> getRuns();
	void setRun(long runId, RunRecord record);
	double getRunInfo(long runId, String Info);
}
