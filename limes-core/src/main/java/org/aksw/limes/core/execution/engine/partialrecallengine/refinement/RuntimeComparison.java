package org.aksw.limes.core.execution.engine.partialrecallengine.refinement;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.io.ls.LinkSpecification;

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
