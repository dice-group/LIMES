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

import java.util.List;


/**
 * An Interface specifies the method signatures to be implemented by all quantitative measures
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public interface IQuantitativeMeasure {
    /**
     * It retrieves the run record with its information
     * @param runId the id of the run
     * @return the run record including its information
     */
    RunRecord getRun(long runId);

    /**
     * It adds the run record with its information
     * @param record the run record including its information
     */
    void addRun(RunRecord record);

    /**
     * It retrieves a set of run records with their information
     * @return it return list of the runs records
     */
    List<RunRecord> getRuns();

    /**
     * It sets the run record with its information
     * @param runId the id of the run
     * @param record the run record data
     */
    void setRun(long runId, RunRecord record);

    /**
     * It gets a specific information from a run record
     * @param runId the id of the run
     * @param Info The name of the requested information
     * @return double -  The value of the required infromation
     */
    double getRunInfo(long runId, String Info);
}
