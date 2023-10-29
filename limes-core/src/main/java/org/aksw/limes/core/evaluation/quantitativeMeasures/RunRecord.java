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

/**
 * The class represents the structure of run record where the run quantitative information are recorded
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class RunRecord {
    /** The id  number of the run*/
    private long runId;
    /** The time duration of the run*/
    private double runTime;
    /** The memory space utilized by the run*/
    private double runMemory = 0;

    private double linkSpecSize;
    private double runTimeVariance;
    private double runMemoryVariance;
    private double linkSpecSizeVariance;

    public RunRecord() {}

    /**
     * @param runId the id of the run
     * @param runTime the time duration recorded by the run
     */
    public RunRecord(long runId, double runTime) {
        this.runId = runId;
        this.runTime = runTime;
    }

    /**
     * @param runId the id of the run
     * @param runTime the time duration recorded by the run
     * @param runMemory the size of the utilized memory
     */
    public RunRecord(long runId, double runTime, double runMemory) {
        this(runId, runTime);
        this.runMemory = runMemory;
    }

    public RunRecord(long runId, double runTime, double runMemory, double linkSpecSize) {
        this(runId, runTime, runMemory);
        this.linkSpecSize = linkSpecSize;
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
        return (runId == ((RunRecord) run).getRunId());
    }

    @Override
    public String toString() {
        String out = runId + ":";
        if (runTime != 0) {
            out += " time: " + runTime;
        }
        if (runMemory != 0) {
            out += " mem: " + runMemory;
        }
        if (linkSpecSize != 0) {
            out += " ls size: " + linkSpecSize;
        }
        return out;
    }

    public double getLinkSpecSize() {
        return linkSpecSize;
    }

    public void setLinkSpecSize(double linkSpecSize) {
        this.linkSpecSize = linkSpecSize;
    }

    public double getRunTimeVariance() {
        return runTimeVariance;
    }

    public void setRunTimeVariance(double runTimeVariance) {
        this.runTimeVariance = runTimeVariance;
    }

    public double getRunMemoryVariance() {
        return runMemoryVariance;
    }

    public void setRunMemoryVariance(double runMemoryVariance) {
        this.runMemoryVariance = runMemoryVariance;
    }

    public double getLinkSpecSizeVariance() {
        return linkSpecSizeVariance;
    }

    public void setLinkSpecSizeVariance(double linkSpecSizeVariance) {
        this.linkSpecSizeVariance = linkSpecSizeVariance;
    }

    @Override
    public RunRecord clone() {
        RunRecord clone = new RunRecord();
        clone.runId = runId;
        clone.runTime = runTime;
        clone.runMemory = runMemory;
        clone.linkSpecSize = linkSpecSize;
        clone.runTimeVariance = runTimeVariance;
        clone.runMemoryVariance = runMemoryVariance;
        clone.linkSpecSizeVariance = linkSpecSizeVariance;
        return clone;
    }

}
