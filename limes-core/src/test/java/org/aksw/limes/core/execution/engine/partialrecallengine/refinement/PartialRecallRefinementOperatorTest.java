package org.aksw.limes.core.execution.engine.partialrecallengine.refinement;

import static org.junit.Assert.*;

import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.execution.planning.planner.LigerPlanner;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.junit.Test;

public class PartialRecallRefinementOperatorTest {

    @Test
    public void test1() {
        String fileNameOrUri = Thread.currentThread().getContextClassLoader()
                .getResource("datasets/Amazon-GoogleProducts.xml").getPath();
        AConfigurationReader reader = new XMLConfigurationReader(fileNameOrUri);
        Configuration config = reader.read();
        ACache sour = HybridCache.getData(config.getSourceInfo());
        ACache targ = HybridCache.getData(config.getTargetInfo());

        LinkSpecification ls = new LinkSpecification("levenshtein(x.surname, y.surname)", 0.3);
        PartialRecallRefinementOperator operator = new LigerRefinementOperator(sour, targ, 0.5, 1000, ls);

        assertTrue(operator.getBest().getLinkSpecification().equals(ls));
    }

    @Test
    public void badValues1() {
        String fileNameOrUri = Thread.currentThread().getContextClassLoader()
                .getResource("datasets/Amazon-GoogleProducts.xml").getPath();
        AConfigurationReader reader = new XMLConfigurationReader(fileNameOrUri);
        Configuration config = reader.read();
        ACache sour = HybridCache.getData(config.getSourceInfo());
        ACache targ = HybridCache.getData(config.getTargetInfo());

        LinkSpecification ls = new LinkSpecification("levenshtein(x.surname, y.surname)", 0.3);
        PartialRecallRefinementOperator operator1 = new LigerRefinementOperator(sour, targ, 0.5, -1000, ls);
        assertTrue(operator1.getOptimizationTime() == 0);
    }

    @Test
    public void badValues2() {
        String fileNameOrUri = Thread.currentThread().getContextClassLoader()
                .getResource("datasets/Amazon-GoogleProducts.xml").getPath();
        AConfigurationReader reader = new XMLConfigurationReader(fileNameOrUri);
        Configuration config = reader.read();
        ACache sour = HybridCache.getData(config.getSourceInfo());
        ACache targ = HybridCache.getData(config.getTargetInfo());

        LinkSpecification ls = new LinkSpecification("levenshtein(x.surname, y.surname)", 0.3);
        PartialRecallRefinementOperator operator1 = new LigerRefinementOperator(sour, targ, 1.5, 1000, ls);
        assertTrue(operator1.getRecall() == 1.0);
    }

    @Test
    public void badValues3() {
        String fileNameOrUri = Thread.currentThread().getContextClassLoader()
                .getResource("datasets/Amazon-GoogleProducts.xml").getPath();
        AConfigurationReader reader = new XMLConfigurationReader(fileNameOrUri);
        Configuration config = reader.read();
        ACache sour = HybridCache.getData(config.getSourceInfo());
        ACache targ = HybridCache.getData(config.getTargetInfo());

        LinkSpecification ls = new LinkSpecification("levenshtein(x.surname, y.surname)", 0.3);
        PartialRecallRefinementOperator operator1 = new LigerRefinementOperator(sour, targ, -1.5, 1000, ls);
        assertTrue(operator1.getRecall() == 1.0);
    }

    @Test
    public void selectivity() {
        String fileNameOrUri = Thread.currentThread().getContextClassLoader()
                .getResource("datasets/Amazon-GoogleProducts.xml").getPath();
        AConfigurationReader reader = new XMLConfigurationReader(fileNameOrUri);
        Configuration config = reader.read();
        ACache sour = HybridCache.getData(config.getSourceInfo());
        ACache targ = HybridCache.getData(config.getTargetInfo());

        LinkSpecification ls = new LinkSpecification("levenshtein(x.surname, y.surname)", 0.3);
        LigerPlanner planner = new LigerPlanner(sour, targ);
        Plan plan = planner.plan(ls);

        PartialRecallRefinementOperator operator1 = new LigerRefinementOperator(sour, targ, 0.5, 1000, ls);
        int com = operator1.checkSelectivity(plan.getSelectivity() * 0.5);
        assertTrue(com == 0);

        int com1 = operator1.checkSelectivity(plan.getSelectivity() * 0.7);
        assertTrue(com1 > 0);

        int com2 = operator1.checkSelectivity(plan.getSelectivity() * 0.3);
        assertTrue(com2 < 0);

    }

    @Test
    public void nextFunction() {
        String fileNameOrUri = Thread.currentThread().getContextClassLoader()
                .getResource("datasets/Amazon-GoogleProducts.xml").getPath();
        AConfigurationReader reader = new XMLConfigurationReader(fileNameOrUri);
        Configuration config = reader.read();
        ACache sour = HybridCache.getData(config.getSourceInfo());
        ACache targ = HybridCache.getData(config.getTargetInfo());

        LinkSpecification ls = new LinkSpecification("levenshtein(x.surname, y.surname)", 0.3);

        PartialRecallRefinementOperator operator = new LigerRefinementOperator(sour, targ, 0.5, 1000, ls);
        
        assertTrue(operator.next(1.0) == -1.0d);
        assertTrue(operator.next(1.0d) == -1.0d);
        assertTrue(operator.next(1) == -1.0d);
        assertTrue(operator.next(1d) == -1.0d);
        assertTrue(operator.next(1.000000) == -1.0d);
        assertTrue(operator.next(1.000000d) == -1.0d);
        
        assertTrue(operator.next(3.0) == -1.0d);
        assertTrue(operator.next(-3) == -1.0d);
        
        assertTrue(operator.next(0.9999999) == 1.0d);
        

    }

}
