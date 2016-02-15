package org.aksw.limes.core.execution.rewriter;

import org.aksw.limes.core.execution.rewriter.DefaultRewriter;
import org.junit.Test;
import static org.junit.Assert.*;

public class DefaultRewriterTest {
    
    @Test
    public void test() {
	String metric = "MIN(AND(m1(x.l, y.l)|0.5, m3(x.p, y.p)|0.7), OR(m1(x.l, y.l)|0.5, m2(x.p, y.p)|0.7))";
	DefaultRewriter r = new DefaultRewriter();
	System.out.println(r.isBoolean(metric));
	System.out.println(r.getInfix(metric));
	System.out.println(r.factorExpression(metric));
    }
}
