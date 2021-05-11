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
package org.aksw.limes.core.ml.algorithm.eagle.coala;
/**
 * Wraps around Wekas cluster assignments, to identify most relevant instances in a cluster.
 * @author Klaus Lyko
 */
public class ClusterInstance implements Comparable<ClusterInstance>{

    int cluster = -1;
    int instanceNumber = -1;
    String uri1 = "";
    String uri2 = "";
    double clusterProbability = 0;

    public ClusterInstance(int instanceNumber, int clusterNumber, double clusterProbability ) {
        this.instanceNumber = instanceNumber;
        this.cluster = clusterNumber;
        this.clusterProbability = clusterProbability;
    }

    public ClusterInstance(int instanceNumber, String uri1, String uri2, int clusterNumber, double clusterProbability ) {
        this.instanceNumber = instanceNumber;
        this.uri1 = uri1;
        this.uri2 = uri2;
        this.cluster = clusterNumber;
        this.clusterProbability = clusterProbability;
    }

    @Override
    public int compareTo(ClusterInstance arg0) {
        return Double.compare(clusterProbability, arg0.clusterProbability);
    }

    @Override
    public String toString() {
        return ""+instanceNumber+" - "+clusterProbability;
    }

}
