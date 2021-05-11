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
package org.aksw.limes.core.evaluation.quantitativeMeasures;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class represents a set of the run records
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0 */
public class RunsData implements IQuantitativeMeasure {

    /** list of recorded runs, each with its information like id,time,memory...etc */
    protected Map<Long, RunRecord> runs = new HashMap<Long, RunRecord>();

    // retrieves a run information based on given Id
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
        if (Info.equals("Id"))
            return runs.get(Long.valueOf(runId)).getRunId();
        else if (Info.equals("Time"))
            return runs.get(Long.valueOf(runId)).getRunTime();
        else if (Info.equals("Memory"))
            return runs.get(Long.valueOf(runId)).getRunMemory();
        else return 0;
    }

    // adding new run to list
    @Override
    public void addRun(RunRecord record) {
        runs.put(record.getRunId(), record);

    }

}
