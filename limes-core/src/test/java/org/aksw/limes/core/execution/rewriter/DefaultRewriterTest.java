package org.aksw.limes.core.execution.rewriter;

import org.aksw.limes.core.execution.rewriter.DefaultRewriter;

public class DefaultRewriterTest {
    public static void main(String args[]) {
	String metric = "MIN(OR(m1(x.l, y.l)|0.5, m2(x.p, y.p)|0.7), OR(m1(x.l, y.l)|0.5, m2(x.p, y.p)|0.7))";
	System.out.println(DefaultRewriter.isBoolean(metric));
	System.out.println(DefaultRewriter.getInfix(metric));
	System.out.println(DefaultRewriter.factorExpression(metric));
    }
}
