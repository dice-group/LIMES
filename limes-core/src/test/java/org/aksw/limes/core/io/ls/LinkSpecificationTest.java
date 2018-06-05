package org.aksw.limes.core.io.ls;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.datastrutures.LogicOperator;
import org.junit.Test;

public class LinkSpecificationTest {

	@Test
	public void setLeafTest(){

		LinkSpecification ls = new LinkSpecification("OR("
				+ "MINUS("
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.001,jaccard(x.h,y.h)|0.1)|0.0,"
				+ "AND(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)|0.0,"
				+ "cosine(x.n,y.n)|0.4)|0.0"
				+ ",AND(cosine(x.n,y.n)|0.5,"
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.6,jaccard(x.h,y.h)|0.7)|0.0"
				+ ",jaccard(x.h,y.h)|0.8)|0.0"
				+ ")|0.0"
				+ ")" , 0.0);
		LinkSpecification test0 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)",1.0),0);
		LinkSpecification lsRes0 = new LinkSpecification("OR("
				+ "MINUS("
				+ "OR("
				+ "MINUS(trigrams(x.n,y.n)|1.0,jaccard(x.h,y.h)|0.1)|0.0,"
				+ "AND(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)|0.0,"
				+ "cosine(x.n,y.n)|0.4)|0.0"
				+ ",AND(cosine(x.n,y.n)|0.5,"
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.6,jaccard(x.h,y.h)|0.7)|0.0"
				+ ",jaccard(x.h,y.h)|0.8)|0.0"
				+ ")|0.0"
				+ ")" , 0.0);

		assertEquals(test0, lsRes0);

		LinkSpecification test1 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)",1.0),1);
		LinkSpecification lsRes1 = new LinkSpecification("OR("
				+ "MINUS("
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.001,trigrams(x.n,y.n)|1.0)|0.0,"
				+ "AND(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)|0.0,"
				+ "cosine(x.n,y.n)|0.4)|0.0"
				+ ",AND(cosine(x.n,y.n)|0.5,"
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.6,jaccard(x.h,y.h)|0.7)|0.0"
				+ ",jaccard(x.h,y.h)|0.8)|0.0"
				+ ")|0.0"
				+ ")" , 0.0);

		assertEquals(test1, lsRes1);

		LinkSpecification test2 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)",1.0),2);
		LinkSpecification lsRes2 = new LinkSpecification("OR("
				+ "MINUS("
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.001,jaccard(x.h,y.h)|0.1)|0.0,"
				+ "AND(trigrams(x.n,y.n)|1.0,jaccard(x.p,y.p)|0.3)|0.0)|0.0,"
				+ "cosine(x.n,y.n)|0.4)|0.0"
				+ ",AND(cosine(x.n,y.n)|0.5,"
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.6,jaccard(x.h,y.h)|0.7)|0.0"
				+ ",jaccard(x.h,y.h)|0.8)|0.0"
				+ ")|0.0"
				+ ")" , 0.0);
		assertEquals(test2, lsRes2);

		LinkSpecification test3 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)",1.0),3);
		LinkSpecification lsRes3 = new LinkSpecification("OR("
				+ "MINUS("
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.001,jaccard(x.h,y.h)|0.1)|0.0,"
				+ "AND(jaccard(x.h,y.h)|0.2,trigrams(x.n,y.n)|1.0)|0.0)|0.0,"
				+ "cosine(x.n,y.n)|0.4)|0.0"
				+ ",AND(cosine(x.n,y.n)|0.5,"
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.6,jaccard(x.h,y.h)|0.7)|0.0"
				+ ",jaccard(x.h,y.h)|0.8)|0.0"
				+ ")|0.0"
				+ ")" , 0.0);
		assertEquals(test3, lsRes3);

		LinkSpecification test4 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)",1.0),4);
		LinkSpecification lsRes4 = new LinkSpecification("OR("
				+ "MINUS("
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.001,jaccard(x.h,y.h)|0.1)|0.0,"
				+ "AND(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)|0.0,"
				+ "trigrams(x.n,y.n)|1.0)|0.0"
				+ ",AND(cosine(x.n,y.n)|0.5,"
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.6,jaccard(x.h,y.h)|0.7)|0.0"
				+ ",jaccard(x.h,y.h)|0.8)|0.0"
				+ ")|0.0"
				+ ")" , 0.0);
		assertEquals(test4, lsRes4);

		LinkSpecification test5 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)",1.0),5);
		LinkSpecification lsRes5 = new LinkSpecification("OR("
				+ "MINUS("
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.001,jaccard(x.h,y.h)|0.1)|0.0,"
				+ "AND(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)|0.0,"
				+ "cosine(x.n,y.n)|0.4)|0.0"
				+ ",AND(trigrams(x.n,y.n)|1.0,"
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.6,jaccard(x.h,y.h)|0.7)|0.0"
				+ ",jaccard(x.h,y.h)|0.8)|0.0"
				+ ")|0.0"
				+ ")" , 0.0);
		assertEquals(test5, lsRes5);

		LinkSpecification test6 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)",1.0),6);
		LinkSpecification lsRes6 = new LinkSpecification("OR("
				+ "MINUS("
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.001,jaccard(x.h,y.h)|0.1)|0.0,"
				+ "AND(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)|0.0,"
				+ "cosine(x.n,y.n)|0.4)|0.0"
				+ ",AND(cosine(x.n,y.n)|0.5,"
				+ "OR("
				+ "MINUS(trigrams(x.n,y.n)|1.0,jaccard(x.h,y.h)|0.7)|0.0"
				+ ",jaccard(x.h,y.h)|0.8)|0.0"
				+ ")|0.0"
				+ ")" , 0.0);
		assertEquals(test6, lsRes6);

		LinkSpecification test7 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)",1.0),7);
		LinkSpecification lsRes7 = new LinkSpecification("OR("
				+ "MINUS("
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.001,jaccard(x.h,y.h)|0.1)|0.0,"
				+ "AND(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)|0.0,"
				+ "cosine(x.n,y.n)|0.4)|0.0"
				+ ",AND(cosine(x.n,y.n)|0.5,"
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.6,trigrams(x.n,y.n)|1.0)|0.0"
				+ ",jaccard(x.h,y.h)|0.8)|0.0"
				+ ")|0.0"
				+ ")" , 0.0);
		assertEquals(test7, lsRes7);

		LinkSpecification test8 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)",1.0),8);
		LinkSpecification lsRes8 = new LinkSpecification("OR("
				+ "MINUS("
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.001,jaccard(x.h,y.h)|0.1)|0.0,"
				+ "AND(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)|0.0,"
				+ "cosine(x.n,y.n)|0.4)|0.0"
				+ ",AND(cosine(x.n,y.n)|0.5,"
				+ "OR("
				+ "MINUS(jaccard(x.p,y.p)|0.6,jaccard(x.h,y.h)|0.7)|0.0"
				+ ",trigrams(x.n,y.n)|1.0)|0.0"
				+ ")|0.0"
				+ ")" , 0.0);
		assertEquals(test8, lsRes8);

	}

	@Test
	public void testLukasiewiczTNorm(){
		String fullExpr = "LUKT(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)";
		LinkSpecification ls = new LinkSpecification(fullExpr, 0.0);

		assertEquals(LogicOperator.LUKASIEWICZT, ls.getOperator());
		assertEquals(2, ls.getChildren().size());
		assertEquals(0.0, ls.getThreshold(), 0);
		assertEquals(fullExpr, ls.getFullExpression());
	}

	@Test
	public void testLukasiewiczTConorm(){
		String fullExpr = "LUKTCO(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)";
		LinkSpecification ls = new LinkSpecification(fullExpr, 0.0);
		assertEquals(LogicOperator.LUKASIEWICZTCO, ls.getOperator());
		assertEquals(2, ls.getChildren().size());
		assertEquals(0.0, ls.getThreshold(), 0);
		assertEquals(fullExpr, ls.getFullExpression());
	}

	@Test
	public void testLukasiewiczDiff(){
		String fullExpr = "LUKDIFF(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)";
		LinkSpecification ls = new LinkSpecification(fullExpr, 0.0);
		assertEquals(LogicOperator.LUKASIEWICZDIFF, ls.getOperator());
		assertEquals(2, ls.getChildren().size());
		assertEquals(0.0, ls.getThreshold(), 0);
		assertEquals(fullExpr, ls.getFullExpression());
	}

	@Test
	public void testLukasiewiczComplex(){
		String fullExpr = "LUKT(trigrams(x.h,y.h)|0.7,LUKTCO(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)";
		LinkSpecification ls = new LinkSpecification(fullExpr, 0.0);
		assertEquals(LogicOperator.LUKASIEWICZT, ls.getOperator());
		assertEquals(2, ls.getChildren().size());
		assertEquals(0.0, ls.getThreshold(), 0);
		assertEquals(fullExpr, ls.getFullExpression());

		assertEquals(LogicOperator.LUKASIEWICZTCO,
				ls.getChildren().get(1).getOperator());
		assertEquals(2, ls.getChildren().get(1).getChildren().size());
		assertEquals(0.0, ls.getChildren().get(1).getThreshold(), 0);
		assertEquals("LUKTCO(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)",
				ls.getChildren().get(1).getFullExpression());
	}

	@Test
	public void testAlgebraicTNorm() {
		String fullExpr = "ALGT(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)";
		LinkSpecification ls = new LinkSpecification(fullExpr, 0.0);

		assertEquals(LogicOperator.ALGEBRAICT, ls.getOperator());
		assertEquals(2, ls.getChildren().size());
		assertEquals(0.0, ls.getThreshold(), 0);
		assertEquals(fullExpr, ls.getFullExpression());
	}

	@Test
	public void testAlgebraicTConorm() {
		String fullExpr = "ALGTCO(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)";
		LinkSpecification ls = new LinkSpecification(fullExpr, 0.0);
		assertEquals(LogicOperator.ALGEBRAICTCO, ls.getOperator());
		assertEquals(2, ls.getChildren().size());
		assertEquals(0.0, ls.getThreshold(), 0);
		assertEquals(fullExpr, ls.getFullExpression());
	}

	@Test
	public void testAlgebraicDiff() {
		String fullExpr = "ALGDIFF(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)";
		LinkSpecification ls = new LinkSpecification(fullExpr, 0.0);
		assertEquals(LogicOperator.ALGEBRAICDIFF, ls.getOperator());
		assertEquals(2, ls.getChildren().size());
		assertEquals(0.0, ls.getThreshold(), 0);
		assertEquals(fullExpr, ls.getFullExpression());
	}

	@Test
	public void testAlgebraicComplex() {
		String fullExpr = "ALGT(trigrams(x.h,y.h)|0.7,ALGTCO(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)";
		LinkSpecification ls = new LinkSpecification(fullExpr, 0.0);
		assertEquals(LogicOperator.ALGEBRAICT, ls.getOperator());
		assertEquals(2, ls.getChildren().size());
		assertEquals(0.0, ls.getThreshold(), 0);
		assertEquals(fullExpr, ls.getFullExpression());

		assertEquals(LogicOperator.ALGEBRAICTCO,
				ls.getChildren().get(1).getOperator());
		assertEquals(2, ls.getChildren().get(1).getChildren().size());
		assertEquals(0.0, ls.getChildren().get(1).getThreshold(), 0);
		assertEquals("ALGTCO(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)",
				ls.getChildren().get(1).getFullExpression());
	}
}
