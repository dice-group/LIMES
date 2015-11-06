package org.aksw.limes.core.evaluation.quantity;

import java.util.List;


/**
 * @author Mofeed Hassan <mounir@informatik.uni-leipzig.de>
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-03
 *
 */
public interface QuantitativeMeasure {
	RunRecord getRun(int runId);
	List<RunRecord> getRuns();
	void setRun(int runId, RunRecord record);
	double getRunInfo(int runId, String Info);
}
