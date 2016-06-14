package org.aksw.limes.core.io.parser;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Test;


public class ParserTest {
    private static final Logger logger = Logger.getLogger(ParserTest.class.getName());

    public void printParsingTree(String s, double threshold) {
        Parser p = new Parser(s, threshold);
        if (p.isAtomic()) {
            logger.debug("-->" + s + " with threshold " + threshold + " will be carried out.");
        } else {
            printParsingTree(p.leftTerm, p.getThreshold1());
            printParsingTree(p.rightTerm, p.getThreshold2());
            logger.debug("--> <" + p.operator + "> will be carried out on " + p.leftTerm + " and " + p.rightTerm + " with "
                    + "threshold " + threshold);
        }
    }

    @Test
    public void testIsAtomic() {
        Parser p = new Parser("trigrams(x.osnp:valueLabel, y.rdfs:label)", 0.5);
        assertTrue(p.isAtomic());

        p = new Parser("MAX(trigrams(x.skos:prefLabel,y.rdfs:label),trigrams(x.osnp:valueLabel, y.rdfs:label))", 0.5);
        assertFalse(p.isAtomic());
    }


    @Test
    public void testParcer() {
        Parser p = new Parser("MAX(trigrams(x.skos:prefLabel,y.rdfs:label),trigrams(x.osnp:valueLabel, y.rdfs:label))", 0.5);
        assertTrue(p.getOperator().equals(Parser.MAX));
        assertTrue(p.getLeftTerm().equals("trigrams(x.skos:prefLabel,y.rdfs:label)"));
        assertTrue(p.getRightTerm().equals("trigrams(x.osnp:valueLabel,y.rdfs:label)"));
    }

}
