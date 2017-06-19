package org.aksw.limes.core.io.ls;

import static org.junit.Assert.*;

import java.util.List;

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
		ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)",1.0),6);
		LinkSpecification lsRes = new LinkSpecification("OR("
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
		assertEquals(ls, lsRes);
	}
}
