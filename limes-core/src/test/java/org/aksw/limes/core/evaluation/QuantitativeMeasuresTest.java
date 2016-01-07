/**
 * 
 */
package org.aksw.limes.core.evaluation;

import static org.junit.Assert.*;

import org.aksw.limes.core.evaluation.quantity.RunRecord;
import org.aksw.limes.core.evaluation.quantity.RunsData;
import org.junit.Test;

/**
 * @author mofeed
 *
 */
public class QuantitativeMeasuresTest {

	@Test
	public void test() {
		RunsData runs = new RunsData();
		RunRecord r1 = new RunRecord(11,System.currentTimeMillis());
		runs.addRun(r1);
		RunRecord r2 = new RunRecord(12,System.currentTimeMillis());
		runs.addRun(r2);
		RunRecord r3 = new RunRecord(13,System.currentTimeMillis());
		runs.addRun(r3);

		RunRecord res1 = runs.getRun(11);
		RunRecord res2 = runs.getRun(12);

		assertTrue(res1.getRunTime() < res2.getRunTime());
		

}
}
