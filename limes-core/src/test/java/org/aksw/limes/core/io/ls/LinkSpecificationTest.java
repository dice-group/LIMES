package org.aksw.limes.core.io.ls;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class LinkSpecificationTest {

    @Test
    public void testLS() {
        LinkSpecification ls = new LinkSpecification(
                "OR(OR(cosine(x.name,y.name)|0.4044,OR"
                + "(OR(cosine(x.name,y.name)|0.4044,levenshtein(x.description,y.description)|0.4135)|0.5151,"
                + "MINUS(OR(levenshtein(x.description,y.description)|0.4135,euclidean(x.price,y.price)|0.8596)|0.5151,"
                + "euclidean(x.price,y.price)|0.8993)|0.5905)|0.5801)|0.5151,"
                + "MINUS(OR(cosine(x.name,y.name)|0.4044,levenshtein(x.description,y.description)|0.4135)|0.5151,"
                + "euclidean(x.price,y.price)|0.8993)|0.5905)",
                0.6929);
        
        System.out.println(ls.toStringPretty());

    }

    // @Test
    public void setLeafTest() {

        LinkSpecification ls = new LinkSpecification(
                "OR(" + "MINUS(" + "OR(" + "MINUS(jaccard(x.p,y.p)|0.001,jaccard(x.h,y.h)|0.1)|0.0,"
                        + "AND(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)|0.0," + "cosine(x.n,y.n)|0.4)|0.0"
                        + ",AND(cosine(x.n,y.n)|0.5," + "OR(" + "MINUS(jaccard(x.p,y.p)|0.6,jaccard(x.h,y.h)|0.7)|0.0"
                        + ",jaccard(x.h,y.h)|0.8)|0.0" + ")|0.0" + ")",
                0.0);
        LinkSpecification test0 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)", 1.0), 0);
        LinkSpecification lsRes0 = new LinkSpecification(
                "OR(" + "MINUS(" + "OR(" + "MINUS(trigrams(x.n,y.n)|1.0,jaccard(x.h,y.h)|0.1)|0.0,"
                        + "AND(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)|0.0," + "cosine(x.n,y.n)|0.4)|0.0"
                        + ",AND(cosine(x.n,y.n)|0.5," + "OR(" + "MINUS(jaccard(x.p,y.p)|0.6,jaccard(x.h,y.h)|0.7)|0.0"
                        + ",jaccard(x.h,y.h)|0.8)|0.0" + ")|0.0" + ")",
                0.0);

        assertEquals(test0, lsRes0);

        LinkSpecification test1 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)", 1.0), 1);
        LinkSpecification lsRes1 = new LinkSpecification(
                "OR(" + "MINUS(" + "OR(" + "MINUS(jaccard(x.p,y.p)|0.001,trigrams(x.n,y.n)|1.0)|0.0,"
                        + "AND(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)|0.0," + "cosine(x.n,y.n)|0.4)|0.0"
                        + ",AND(cosine(x.n,y.n)|0.5," + "OR(" + "MINUS(jaccard(x.p,y.p)|0.6,jaccard(x.h,y.h)|0.7)|0.0"
                        + ",jaccard(x.h,y.h)|0.8)|0.0" + ")|0.0" + ")",
                0.0);

        assertEquals(test1, lsRes1);

        LinkSpecification test2 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)", 1.0), 2);
        LinkSpecification lsRes2 = new LinkSpecification(
                "OR(" + "MINUS(" + "OR(" + "MINUS(jaccard(x.p,y.p)|0.001,jaccard(x.h,y.h)|0.1)|0.0,"
                        + "AND(trigrams(x.n,y.n)|1.0,jaccard(x.p,y.p)|0.3)|0.0)|0.0," + "cosine(x.n,y.n)|0.4)|0.0"
                        + ",AND(cosine(x.n,y.n)|0.5," + "OR(" + "MINUS(jaccard(x.p,y.p)|0.6,jaccard(x.h,y.h)|0.7)|0.0"
                        + ",jaccard(x.h,y.h)|0.8)|0.0" + ")|0.0" + ")",
                0.0);
        assertEquals(test2, lsRes2);

        LinkSpecification test3 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)", 1.0), 3);
        LinkSpecification lsRes3 = new LinkSpecification(
                "OR(" + "MINUS(" + "OR(" + "MINUS(jaccard(x.p,y.p)|0.001,jaccard(x.h,y.h)|0.1)|0.0,"
                        + "AND(jaccard(x.h,y.h)|0.2,trigrams(x.n,y.n)|1.0)|0.0)|0.0," + "cosine(x.n,y.n)|0.4)|0.0"
                        + ",AND(cosine(x.n,y.n)|0.5," + "OR(" + "MINUS(jaccard(x.p,y.p)|0.6,jaccard(x.h,y.h)|0.7)|0.0"
                        + ",jaccard(x.h,y.h)|0.8)|0.0" + ")|0.0" + ")",
                0.0);
        assertEquals(test3, lsRes3);

        LinkSpecification test4 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)", 1.0), 4);
        LinkSpecification lsRes4 = new LinkSpecification(
                "OR(" + "MINUS(" + "OR(" + "MINUS(jaccard(x.p,y.p)|0.001,jaccard(x.h,y.h)|0.1)|0.0,"
                        + "AND(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)|0.0," + "trigrams(x.n,y.n)|1.0)|0.0"
                        + ",AND(cosine(x.n,y.n)|0.5," + "OR(" + "MINUS(jaccard(x.p,y.p)|0.6,jaccard(x.h,y.h)|0.7)|0.0"
                        + ",jaccard(x.h,y.h)|0.8)|0.0" + ")|0.0" + ")",
                0.0);
        assertEquals(test4, lsRes4);

        LinkSpecification test5 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)", 1.0), 5);
        LinkSpecification lsRes5 = new LinkSpecification(
                "OR(" + "MINUS(" + "OR(" + "MINUS(jaccard(x.p,y.p)|0.001,jaccard(x.h,y.h)|0.1)|0.0,"
                        + "AND(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)|0.0," + "cosine(x.n,y.n)|0.4)|0.0"
                        + ",AND(trigrams(x.n,y.n)|1.0," + "OR(" + "MINUS(jaccard(x.p,y.p)|0.6,jaccard(x.h,y.h)|0.7)|0.0"
                        + ",jaccard(x.h,y.h)|0.8)|0.0" + ")|0.0" + ")",
                0.0);
        assertEquals(test5, lsRes5);

        LinkSpecification test6 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)", 1.0), 6);
        LinkSpecification lsRes6 = new LinkSpecification(
                "OR(" + "MINUS(" + "OR(" + "MINUS(jaccard(x.p,y.p)|0.001,jaccard(x.h,y.h)|0.1)|0.0,"
                        + "AND(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)|0.0," + "cosine(x.n,y.n)|0.4)|0.0"
                        + ",AND(cosine(x.n,y.n)|0.5," + "OR(" + "MINUS(trigrams(x.n,y.n)|1.0,jaccard(x.h,y.h)|0.7)|0.0"
                        + ",jaccard(x.h,y.h)|0.8)|0.0" + ")|0.0" + ")",
                0.0);
        assertEquals(test6, lsRes6);

        LinkSpecification test7 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)", 1.0), 7);
        LinkSpecification lsRes7 = new LinkSpecification(
                "OR(" + "MINUS(" + "OR(" + "MINUS(jaccard(x.p,y.p)|0.001,jaccard(x.h,y.h)|0.1)|0.0,"
                        + "AND(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)|0.0," + "cosine(x.n,y.n)|0.4)|0.0"
                        + ",AND(cosine(x.n,y.n)|0.5," + "OR(" + "MINUS(jaccard(x.p,y.p)|0.6,trigrams(x.n,y.n)|1.0)|0.0"
                        + ",jaccard(x.h,y.h)|0.8)|0.0" + ")|0.0" + ")",
                0.0);
        assertEquals(test7, lsRes7);

        LinkSpecification test8 = ls.setLeaf(new LinkSpecification("trigrams(x.n,y.n)", 1.0), 8);
        LinkSpecification lsRes8 = new LinkSpecification(
                "OR(" + "MINUS(" + "OR(" + "MINUS(jaccard(x.p,y.p)|0.001,jaccard(x.h,y.h)|0.1)|0.0,"
                        + "AND(jaccard(x.h,y.h)|0.2,jaccard(x.p,y.p)|0.3)|0.0)|0.0," + "cosine(x.n,y.n)|0.4)|0.0"
                        + ",AND(cosine(x.n,y.n)|0.5," + "OR(" + "MINUS(jaccard(x.p,y.p)|0.6,jaccard(x.h,y.h)|0.7)|0.0"
                        + ",trigrams(x.n,y.n)|1.0)|0.0" + ")|0.0" + ")",
                0.0);
        assertEquals(test8, lsRes8);

    }
}
