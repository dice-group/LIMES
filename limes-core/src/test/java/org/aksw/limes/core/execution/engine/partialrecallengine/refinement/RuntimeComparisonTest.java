package org.aksw.limes.core.execution.engine.partialrecallengine.refinement;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.aksw.limes.core.execution.engine.partialrecallengine.refinement.PartialRecallRefinementNode;
import org.aksw.limes.core.execution.engine.partialrecallengine.refinement.RuntimeComparison;
import org.aksw.limes.core.execution.planning.plan.NestedPlan;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.execution.planning.planner.LigerPlanner;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.junit.Test;

public class RuntimeComparisonTest {

    @Test
    public void comparePlans() {
        Plan plan1 = new NestedPlan();
        plan1.setRuntimeCost(58.938581234345);
        Plan plan2 = new NestedPlan();
        plan2.setRuntimeCost(58.938581234345);
        
        int com1 = RuntimeComparison.comparePlans(plan1, plan2);
        assertTrue(com1 == 0);
        
        
        plan2.setRuntimeCost(58.938581288777);
        int com2 = RuntimeComparison.comparePlans(plan1, plan2);
        assertTrue(com2 < 0);
        
        plan2.setRuntimeCost(58.938581234333);
        int com3 = RuntimeComparison.comparePlans(plan1, plan2);
        assertTrue(com3 > 0);
        
    }
    
    @Test
    public void sortPlans() {
        String fileNameOrUri = Thread.currentThread().getContextClassLoader()
                .getResource("datasets/Amazon-GoogleProducts.xml").getPath();
        AConfigurationReader reader = new XMLConfigurationReader(fileNameOrUri);
        Configuration config = reader.read();
        ACache sour = HybridCache.getData(config.getSourceInfo());
        ACache targ = HybridCache.getData(config.getTargetInfo());
        HashMap<LinkSpecification, Plan> newNodes = new HashMap<LinkSpecification, Plan>();
        LigerPlanner planner = new LigerPlanner(sour, targ);
        
        LinkSpecification ls1 = new LinkSpecification(
                "OR(levenshtein(x.title,y.name)|0.2013,overlap(x.description,y.description)|0.8702)", 0.34);
        Plan plan1 = planner.plan(ls1);
        newNodes.put(ls1, plan1);
        
        LinkSpecification ls2 = new LinkSpecification(
                "OR(levenshtein(x.title,y.name)|0.2013,overlap(x.description,y.description)|0.5702)", 0.34);
        Plan plan2 = planner.plan(ls2);
        newNodes.put(ls2, plan2);
        
        LinkSpecification ls3 = new LinkSpecification(
                "levenshtein(x.surname, y.surname)", 0.3);
        Plan plan3 = planner.plan(ls3);
        newNodes.put(ls3, plan3);
        
        LinkSpecification ls4 = new LinkSpecification(
                "AND(levenshtein(x.title,y.name)|0.45,cosine(x.description,y.description)|0.67)", 0.2);
        Plan plan4 = planner.plan(ls4);
        newNodes.put(ls4, plan4);
        
        LinkSpecification ls5 = new LinkSpecification(
                "MINUS(qgrams(x.title,y.name)|0.45,levenshtein(x.description,y.description)|0.67)", 0.87);
        Plan plan5 = planner.plan(ls5);
        newNodes.put(ls5, plan5);
        
        LinkedList<PartialRecallRefinementNode> sorted = RuntimeComparison.sortLinkSpecifications(newNodes);
        assertTrue(sorted.size() == 5);
        
        for(PartialRecallRefinementNode node: sorted){
            System.out.println("\nLS: "+node.getLinkSpecification().toString());
            System.out.println("\nRuntime: "+node.getPlan().getRuntimeCost());
        }
        
        
    }

}
