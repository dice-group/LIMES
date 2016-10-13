/**
 *
 */
package org.aksw.limes.core.evaluation;

import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.evaluation.quantitativeMeasures.RunRecord;
import org.aksw.limes.core.evaluation.quantitativeMeasures.RunsData;
import org.junit.Test;

/**
 * @author mofeed
 */
public class QuantitativeMeasuresTest {

    @Test
    public void test() {
        RunsData runs = new RunsData();

        double time1 = System.currentTimeMillis();
        RunRecord r1 = new RunRecord(11, time1);
        runs.addRun(r1);

        double time2 = System.currentTimeMillis();
        RunRecord r2 = new RunRecord(12, time2);
        runs.addRun(r2);

        double time3 = System.currentTimeMillis();
        RunRecord r3 = new RunRecord(13, time3);
        runs.addRun(r3);

        RunRecord res1 = runs.getRun(11);
        RunRecord res2 = runs.getRun(12);
        RunRecord res3 = runs.getRun(13);


        assertTrue(runs.getRun(11).getRunTime() == r1.getRunTime());
        assertTrue(runs.getRunInfo(12, "Time") == r2.getRunTime());


    }
}
