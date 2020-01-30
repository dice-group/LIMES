package org.aksw.limes.core.execution.engine.partialrecallengine.refinement;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.apache.log4j.Logger;
import org.junit.Test;

public class LigerRefinementOperatorTest {
    protected static final Logger logger = Logger.getLogger(LigerRefinementOperatorTest.class.getName());

    @Test
    public void addSpecifications() {
        String fileNameOrUri = Thread.currentThread().getContextClassLoader()
                .getResource("datasets/Amazon-GoogleProducts.xml").getPath();
        AConfigurationReader reader = new XMLConfigurationReader(fileNameOrUri);
        Configuration config = reader.read();
        ACache sour = HybridCache.getData(config.getSourceInfo());
        ACache targ = HybridCache.getData(config.getTargetInfo());

        LinkSpecification ls = new LinkSpecification("levenshtein(x.surname, y.surname)", 0.3);
        LigerRefinementOperator operator = new LigerRefinementOperator(sour, targ, 0.5, 1000, ls);

        List<LinkSpecification> newLSs = new ArrayList<LinkSpecification>();
        LinkSpecification ls1 = new LinkSpecification(
                "OR(levenshtein(x.title,y.name)|0.2013,overlap(x.description,y.description)|0.8702)", 0.34);
        newLSs.add(ls1);
        LinkSpecification ls2 = new LinkSpecification(
                "OR(levenshtein(x.title,y.name)|0.2013,overlap(x.description,y.description)|0.5702)", 0.34);
        newLSs.add(ls2);
        LinkSpecification ls3 = new LinkSpecification("levenshtein(x.surname, y.surname)", 0.3);
        newLSs.add(ls3);
        LinkSpecification ls4 = new LinkSpecification(
                "AND(levenshtein(x.title,y.name)|0.45,cosine(x.description,y.description)|0.67)", 0.2);
        newLSs.add(ls4);
        LinkSpecification ls5 = new LinkSpecification(
                "MINUS(qgrams(x.title,y.name)|0.45,levenshtein(x.description,y.description)|0.67)", 0.87);
        newLSs.add(ls5);
        LinkSpecification ls6 = new LinkSpecification("levenshtein(x.surname, y.surname)", 0.3);
        newLSs.add(ls6);

        operator.addSpecifications(newLSs);

        // ls2 and ls6 are duplicates
        assertTrue(operator.total.size() == 5);
        // ls1 and ls3 only make it due to selectivity higher than the desired
        assertTrue(operator.getNewNodes().size() == 2);

    }

    @Test
    public void refineAtomicLinkSpecification() {

        String fileNameOrUri = Thread.currentThread().getContextClassLoader()
                .getResource("datasets/Amazon-GoogleProducts.xml").getPath();
        AConfigurationReader reader = new XMLConfigurationReader(fileNameOrUri);
        Configuration config = reader.read();
        ACache sour = HybridCache.getData(config.getSourceInfo());
        ACache targ = HybridCache.getData(config.getTargetInfo());

        LinkSpecification ls = new LinkSpecification("levenshtein(x.surname, y.surname)", 0.3);
        LigerRefinementOperator operator = new LigerRefinementOperator(sour, targ, 0.5, 1000, ls);

        List<LinkSpecification> newLSs = new ArrayList<LinkSpecification>();
        LinkSpecification ls1 = new LinkSpecification("overlap(x.description,y.description)", 1.00);
        newLSs.add(ls1);
        LinkSpecification ls2 = new LinkSpecification("levenshtein(x.title,y.name)", 0.34);
        newLSs.add(ls2);
        LinkSpecification ls3 = new LinkSpecification(
                "MINUS(qgrams(x.title,y.name)|0.45,levenshtein(x.description,y.description)|0.67)", 0.3);
        newLSs.add(ls3);
        LinkSpecification ls4 = new LinkSpecification("cosine(x.description,y.description)", 0.2);
        newLSs.add(ls4);
        LinkSpecification ls5 = new LinkSpecification("qgrams(x.title,y.name)", 0.87);
        newLSs.add(ls5);
        LinkSpecification ls6 = new LinkSpecification("trigrams(x.surname, y.surname)", 0.3);
        newLSs.add(ls6);
        LinkSpecification ls7 = new LinkSpecification("levenshtein(x.surname, y.surname)", 1.0);
        newLSs.add(ls7);

        List<LinkSpecification> refinedOnes = new ArrayList<LinkSpecification>();

        for (int i = 0; i < newLSs.size(); i++) {
            LinkSpecification newSp = operator.refineAtomicLinkSpecification(newLSs.get(i));
            if (newSp != null) {
                // checking for pointer equality
                // newSp is a clone
                assertTrue(newSp != newLSs.get(i));
                refinedOnes.add(newSp);
                if (i == 0) {
                    assertTrue(newSp == null);
                }
                if (i == 1) {
                    assertTrue(newSp.getThreshold() == 0.4);
                }
                if (i == 2) {
                    assertTrue(newSp == null);
                }
                if (i == 3) {
                    assertTrue(newSp.getThreshold() == 0.3);
                }
                if (i == 4) {
                    assertTrue(newSp.getThreshold() == 0.9);
                }
                if (i == 5) {
                    assertTrue(newSp.getThreshold() == 0.4);
                }
                if (i == 6) {
                    assertTrue(newSp == null);
                }
            }
        }
        assertTrue(refinedOnes.size() == 4);

    }

    @Test
    public void merge() {
        String fileNameOrUri = Thread.currentThread().getContextClassLoader()
                .getResource("datasets/Amazon-GoogleProducts.xml").getPath();
        AConfigurationReader reader = new XMLConfigurationReader(fileNameOrUri);
        Configuration config = reader.read();
        ACache sour = HybridCache.getData(config.getSourceInfo());
        ACache targ = HybridCache.getData(config.getTargetInfo());

        LinkSpecification ls = new LinkSpecification("levenshtein(x.surname, y.surname)", 0.3);
        LigerRefinementOperator operator = new LigerRefinementOperator(sour, targ, 0.5, 1000, ls);

        List<LinkSpecification> newLSs = new ArrayList<LinkSpecification>();
        LinkSpecification ls1 = new LinkSpecification(
                "OR(levenshtein(x.title,y.name)|0.2013,overlap(x.description,y.description)|0.8702)", 0.34);
        newLSs.add(ls1);
        LinkSpecification ls2 = new LinkSpecification(
                "OR(levenshtein(x.title,y.name)|0.2013,overlap(x.description,y.description)|0.5702)", 0.34);
        newLSs.add(ls2);
        LinkSpecification ls3 = new LinkSpecification(
                "AND(cosine(x.title,y.name)|0.45,levenshtein(x.description,y.description)|0.67)", 0.3);
        newLSs.add(ls3);
        LinkSpecification ls4 = new LinkSpecification(
                "AND(levenshtein(x.title,y.name)|0.65,cosine(x.description,y.description)|0.67)", 0.2);
        newLSs.add(ls4);
        LinkSpecification ls5 = new LinkSpecification(
                "OR(qgrams(x.title,y.name)|0.45,levenshtein(x.description,y.description)|0.67)", 0.87);
        newLSs.add(ls5);

        for (LinkSpecification parent : newLSs) {
            LinkSpecification leftChild = parent.getChildren().get(0);
            LinkSpecification refinedLeftChild = operator.refineAtomicLinkSpecification(leftChild);
            LinkSpecification rightChild = parent.getChildren().get(1);
            LinkSpecification refinedRightChild = operator.refineAtomicLinkSpecification(rightChild);
            
            // 1st attempt: refine left, keep right
            List<LinkSpecification> refinedLefts = new ArrayList<LinkSpecification>();
            refinedLefts.add(refinedLeftChild);
            assertTrue(refinedLeftChild != leftChild);
            List<LinkSpecification> rights = new ArrayList<LinkSpecification>();
            rights.add(rightChild);
            List<LinkSpecification> mergedWithLeft = operator.merge(parent, refinedLefts, rights, true);
            for (LinkSpecification newLS : mergedWithLeft) {
                assertTrue(parent != newLS);
                assertTrue(leftChild.getFullExpression().equals(newLS.getChildren().get(0).getFullExpression()));
                assertTrue(leftChild.getThreshold() < newLS.getChildren().get(0).getThreshold());
                assertTrue(leftChild != newLS.getChildren().get(0));
                assertTrue(rightChild.getFullExpression().equals(newLS.getChildren().get(1).getFullExpression()));
                assertTrue(rightChild.getThreshold() == newLS.getChildren().get(1).getThreshold());
                assertTrue(rightChild != newLS.getChildren().get(1));
            }
            // 2nd attempt: keep left, refine left
            List<LinkSpecification> lefts = new ArrayList<LinkSpecification>();
            lefts.add(leftChild);
            List<LinkSpecification> refinedRights = new ArrayList<LinkSpecification>();
            refinedRights.add(refinedRightChild);
            assertTrue(refinedRightChild != rightChild);
            
            List<LinkSpecification> mergedWithRight = operator.merge(parent, lefts, refinedRights, false);
            for (LinkSpecification newLS : mergedWithRight) {
                assertTrue(parent != newLS);
                assertTrue(leftChild.getFullExpression().equals(newLS.getChildren().get(0).getFullExpression()));
                assertTrue(leftChild.getThreshold() == newLS.getChildren().get(0).getThreshold());
                assertTrue(leftChild != newLS.getChildren().get(0));
                assertTrue(rightChild.getFullExpression().equals(newLS.getChildren().get(1).getFullExpression()));
                assertTrue(rightChild.getThreshold() < newLS.getChildren().get(1).getThreshold());
                assertTrue(rightChild != newLS.getChildren().get(1));

            }

        }

    }
}
