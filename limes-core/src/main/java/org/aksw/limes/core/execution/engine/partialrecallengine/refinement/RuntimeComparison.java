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
package org.aksw.limes.core.execution.engine.partialrecallengine.refinement;

import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.io.ls.LinkSpecification;

import java.util.*;
import java.util.Map.Entry;

public class RuntimeComparison {

    /**
     * Compares two plans based on their runtime estimation.
     *
     * @param newPlan,
     *            the first plan
     * @param bestPlan,
     *            the second plan
     *
     * @return 0 if both plans have the same estimated runtime, a value below 0
     *         if newPlan is faster than the oldPlan, and a value above 0 if the
     *         oldPlan is faster that the newPlan
     */
    public static int comparePlans(Plan newPlan, Plan bestPlan) {
        // < 0 means that newPlan is faster than bestPlan
        return Double.compare(newPlan.getRuntimeCost(), bestPlan.getRuntimeCost());
    }

    /**
     * Sorts a map of link specifications in ascending order based on the
     * runtime estimations of their corresponding plans. To secure the
     * deterministic nature of the function, if two link specifications have the
     * same runtime estimation, the function compares the String representation
     * of the link specifications.
     *
     * @param plans,
     *            a map of Link Specifications and their corresponding plans
     * @return an ordered list of link specifications the runtime estimations of
     *         their corresponding plans
     */
    public static LinkedList<PartialRecallRefinementNode> sortLinkSpecifications(
            HashMap<LinkSpecification, Plan> plans) {

        List<Entry<LinkSpecification, Plan>> temp = new LinkedList<Entry<LinkSpecification, Plan>>(plans.entrySet());

        Collections.sort(temp, new Comparator<Entry<LinkSpecification, Plan>>() {
            public int compare(Entry<LinkSpecification, Plan> o1, Entry<LinkSpecification, Plan> o2) {
                String str1 = o1.getKey().getFullExpression();
                String str2 = o2.getKey().getFullExpression();
                Double d1 = new Double(o1.getValue().getRuntimeCost());
                Double d2 = new Double(o2.getValue().getRuntimeCost());
                int com = d1.compareTo(d2);
                if (com == 0)
                    return str1.compareTo(str2);
                else
                    return d1.compareTo(d2);
            }
        });

        LinkedList<PartialRecallRefinementNode> sortedList = new LinkedList<PartialRecallRefinementNode>();
        for (Entry<LinkSpecification, Plan> entry : temp) {
            PartialRecallRefinementNode node = new PartialRecallRefinementNode(entry.getKey(), entry.getValue());
            sortedList.add(node);
        }
        return sortedList;
    }

}
