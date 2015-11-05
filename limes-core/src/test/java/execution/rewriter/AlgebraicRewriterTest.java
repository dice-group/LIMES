package execution.rewriter;

import org.aksw.limes.core.config.LinkSpecification;
import org.aksw.limes.core.execution.rewriter.AlgebraicRewriter;
import org.aksw.limes.core.measures.mapper.SetOperations.Operator;

public class AlgebraicRewriterTest {
    
    /**
     * Test for update threshold
     *
     */
    public void updateThresholdsTest() {
	AlgebraicRewriter ar = new AlgebraicRewriter();
	LinkSpecification spec = new LinkSpecification();
	LinkSpecification spec2 = new LinkSpecification();
	spec.readSpec("AND(trigrams(x.p, y.p)|0.6, AND(euclidean(x.q, y.q)|0.7, linear(x.s, y.s)|0.7)|0.6)", 0.5);
	spec2.readSpec("AND(euclidean(x.q, y.q)|0.7, linear(x.s, y.s)|0.7)", 0.7);
	System.out.println(spec + "\n" + spec2);
	System.out.println(ar.updateThresholds(spec));
	System.out.println(ar.updateThresholds(spec2));
	// spec = ar.updateThresholds(spec.clone());
	System.out.println(spec);
	System.out.println(spec2);
    }

    
    /**
     * Test for rewriting
     *
     */
    public static void rewriteTest() {
	LinkSpecification spec = new LinkSpecification();
	spec.readSpec(
		"AND(XOR(levenshtein(x.authors,y.authors)|0.9135,XOR(cosine(x.venue,y.venue)|0.5183,overlap(x.title,y.title)|0.5183)|0.4506)|0.4506,AND(XOR(overlap(x.authors,y.authors)|0.4506,OR(levenshtein(x.authors,y.authors)|0.4506,euclidean(x.year,y.year)|0.9304)|0.5558)|0.0073,overlap(x.title,y.title)|0.7019)|0.7019)",
		0.5);
	// spec.readSpec("OR(trigrams(x.q, y.q)|0.75, AND(jaccard(x.q, y.q)|0.7,
	// jaccard(x.q, y.q)|0.5)|0.8)", 0.5);
	AlgebraicRewriterTest ar = new AlgebraicRewriterTest();
	System.out.println(spec);
    }

    /**
     * Test for dependency computation
     *
     */
    public void dependencyTest() {
	AlgebraicRewriter ar = new AlgebraicRewriter();
	LinkSpecification spec = new LinkSpecification();
	spec.readSpec("jaccard(x.q, y.q)", 0.5);
	LinkSpecification spec2 = new LinkSpecification();
	spec2.readSpec("jaccard(x.q, y.o)", 0.7);
	// System.out.println(ar.computeAtomicDependency(spec, spec2));

	spec.readSpec("OR(jaccard(x.q, y.q)|0.75, AND(jaccard(x.q, y.q)|0.7, jaccard(x.q, y.q)|0.5)|0.8)", 0.5);
	spec = ar.computeAllDependencies(spec);
	System.out.println(spec);
	spec = ar.collapseSpec(spec);
	System.out.println(spec);
	spec = ar.removeUnaryOperators(spec);
	System.out.println(spec);
	spec = ar.updateThresholds(spec);
	spec = ar.removeUnaryOperators(spec);
	System.out.println(spec);
    }

    /**
     * Test for unary removal threshold
     *
     */
    public void unaryTest() {
	AlgebraicRewriter ar = new AlgebraicRewriter();
	LinkSpecification spec = new LinkSpecification();
	LinkSpecification spec2 = new LinkSpecification();
	LinkSpecification spec3 = new LinkSpecification();

	spec.readSpec("trigrams(x.p, y.p)", 0.7);
	spec2.readSpec("euclidean(x.q, y.q)", 0.3);
	spec3.threshold = 0.5;
	spec3.operator = Operator.AND;
	spec.addChild(spec3);
	spec3.addChild(spec2);
	System.out.println(spec);
	spec = ar.removeUnaryOperators(spec);
	System.out.println(spec);
    }
    
    /**
     * Computes dependencies between nodes of a spec
     *
     * @param args
     */
    public static void main(String args[]) {
	// still need to write the overall rewriter method
	rewriteTest();
    }

}
