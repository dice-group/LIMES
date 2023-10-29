/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 *
 */
package org.aksw.limes.core.evaluation;

import org.aksw.limes.core.evaluation.quantitativeMeasures.RunRecord;
import org.aksw.limes.core.evaluation.quantitativeMeasures.RunsData;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

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
