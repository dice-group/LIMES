package org.aksw.limes.core.execution.rewriter;

import org.aksw.limes.core.execution.rewriter.DefaultRewriter;

public class DefaultRewriterTest {
    public static void main(String args[]) {
	
	DefaultRewriter rewriter = new DefaultRewriter();
	
	String metric = "MIN(OR(m1(x.l, y.l)|0.5, m2(x.p, y.p)|0.7), OR(m1(x.l, y.l)|0.5, m2(x.p, y.p)|0.7))";
	System.out.println(rewriter.isBoolean(metric));
	System.out.println(rewriter.getInfix(metric));
	System.out.println(rewriter.factorExpression(metric));
    }
}
