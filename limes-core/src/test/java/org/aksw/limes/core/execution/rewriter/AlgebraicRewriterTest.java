package org.aksw.limes.core.execution.rewriter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlgebraicRewriterTest {
    private static final Logger logger = LoggerFactory.getLogger(AlgebraicRewriterTest.class);
    /**
     * Test for update threshold
     */
    @Test
    public void updateThresholdsTest() {
        logger.info("{}","updateThresholdsTest");
        AlgebraicRewriter ar = new AlgebraicRewriter();
        LinkSpecification spec = new LinkSpecification();
        // LinkSpecification spec2 = new LinkSpecification();
        spec.readSpec("AND(trigrams(x.p, y.p)|0.6, AND(euclidean(x.q, y.q)|0.7, cosine(x.s, y.s)|0.7)|0.6)", 0.5);
        // spec2.readSpec("AND(euclidean(x.q, y.q)|0.7, linear(x.s, y.s)|0.7)",
        // 0.7);
        // logger.info("{}",spec + "\n" + spec2);
        assertFalse(spec.getThreshold() == 0.0d);
        assertFalse(spec.getChildren().get(1).getThreshold() == 0.0d);
        LinkSpecification specUpdated = ar.updateThresholds(spec);
        assertTrue(specUpdated.getThreshold() == 0.0d);
        assertTrue(specUpdated.getChildren().get(1).getThreshold() == 0.0d);

        // null spec
        specUpdated = ar.updateThresholds(null);
        assertTrue(specUpdated == null);
        // empty spec
        LinkSpecification l = new LinkSpecification("trigrams(x.p, y.p)", 0.0);
        specUpdated = ar.updateThresholds(l);
        assertTrue(specUpdated.equals(l));
        // atomic
        l = new LinkSpecification("trigrams(x.p, y.p)", 0.3);
        specUpdated = ar.updateThresholds(l);
        assertTrue(specUpdated.equals(l));

        spec.readSpec("AND(trigrams(x.p, y.p)|0.3, AND(euclidean(x.q, y.q)|0.3, cosine(x.s, y.s)|0.7)|0.6)", 0.5);
        specUpdated = ar.updateThresholds(spec);
        assertTrue(specUpdated.getThreshold() != 0.0d);
        assertTrue(specUpdated.getChildren().get(1).getThreshold() != 0.0d);
    }

    /**
     * Test for rewriting
     */
    @Test
    public void rewriteTest() {
        LinkSpecification spec = new LinkSpecification();
        // spec.readSpec(
        // "AND(XOR(levenshtein(x.authors,y.authors)|0.9135,XOR(cosine(x.venue,y.venue)|0.5183,overlap(x.title,y.title)|0.5183)|0.4506)|0.4506,AND(XOR(overlap(x.authors,y.authors)|0.4506,OR(levenshtein(x.authors,y.authors)|0.4506,euclidean(x.year,y.year)|0.9304)|0.5558)|0.0073,overlap(x.title,y.title)|0.7019)|0.7019)",
        // 0.5);
        spec.readSpec("AND(AND(euclidean(x.q, y.q)|0.7, euclidean(x.q, y.q)|0.5)|0.4, AND(euclidean(x.q, y.q)|0.7, euclidean(x.q, y.q)|0.6)|0.4)", 0.5);
        //logger.info("{}",spec);
        AlgebraicRewriter ar = new AlgebraicRewriter();
        spec = ar.rewrite(spec);
        //for(LinkSpecification ls : specUpdated.getAllLeaves()){
        //    logger.info("{}",ls);
        //   logger.info("{}",ls.getDependencies());
        //}
        logger.info("{}","Dependencies on original: " + spec.getDependencies());
        logger.info("{}","original: " + spec);

        //logger.info("{}",spec.getChildren().get(0).getDependencies());
    }

    /**
     * Test for dependency computation
     *
     */
    /*
     * @Test public void dependencyTest() { AlgebraicRewriter ar = new
     * AlgebraicRewriter(); LinkSpecification spec = new LinkSpecification();
     * spec.readSpec("jaccard(x.q, y.q)", 0.5); LinkSpecification spec2 = new
     * LinkSpecification(); spec2.readSpec("jaccard(x.q, y.o)", 0.7); //
     * logger.info("{}",ar.computeAtomicDependency(spec, spec2));
     * 
     * spec.readSpec(
     * "OR(jaccard(x.q, y.q)|0.75, AND(jaccard(x.q, y.q)|0.7, jaccard(x.q, y.q)|0.5)|0.8)"
     * , 0.5); spec = ar.computeAllDependencies(spec); logger.info("{}",spec);
     * spec = ar.collapseSpec(spec); logger.info("{}",spec); spec =
     * ar.removeUnaryOperators(spec); logger.info("{}",spec); spec =
     * ar.updateThresholds(spec); spec = ar.removeUnaryOperators(spec);
     * logger.info("{}",spec); }
     */

    /**
     * Test for unary removal threshold
     *
     */
    /*
     * @Test public void unaryTest() { AlgebraicRewriter ar = new
     * AlgebraicRewriter(); LinkSpecification spec = new LinkSpecification();
     * LinkSpecification spec2 = new LinkSpecification(); LinkSpecification
     * spec3 = new LinkSpecification();
     * 
     * spec.readSpec("trigrams(x.p, y.p)", 0.7); spec2.readSpec(
     * "euclidean(x.q, y.q)", 0.3); spec3.setThreshold(0.5);
     * spec3.setOperator(LogicOperator.AND); spec.addChild(spec3);
     * spec3.addChild(spec2); logger.info("{}",spec); spec =
     * ar.removeUnaryOperators(spec); logger.info("{}",spec); }
     */

}
