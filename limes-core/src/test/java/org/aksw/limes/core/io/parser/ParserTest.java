package org.aksw.limes.core.io.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.aksw.limes.core.exceptions.UnsupportedOperator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserTest {
    private static final Logger logger = LoggerFactory.getLogger(ParserTest.class.getName());

    public void printParsingTree(String s, double threshold) {
        Parser p = new Parser(s, threshold);
        try {
            if (p.isAtomic()) {
                logger.debug("-->" + s + " with threshold " + threshold + " will be carried out.");
            } else {
                printParsingTree(p.leftTerm, p.getThreshold1());
                printParsingTree(p.rightTerm, p.getThreshold2());
                logger.debug("--> <" + p.operator + "> will be carried out on " + p.leftTerm + " and " + p.rightTerm
                        + " with " + "threshold " + threshold);
            }
        } catch (UnsupportedOperator e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testIsAtomic() {
        Parser p = new Parser("trigrams(x.osnp:valueLabel, y.rdfs:label)", 0.5);
        try {
            assertTrue(p.isAtomic());
        } catch (UnsupportedOperator e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            p = new Parser("MAX(trigrams(x.skos:prefLabel,y.rdfs:label),trigrams(x.osnp:valueLabel, y.rdfs:label))", 0.5);

            assertFalse(p.isAtomic());
        } catch (UnsupportedOperator e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testParcer() {
        Parser p = null;
        try {
            p = new Parser("MAX(trigrams(x.skos:prefLabel,y.rdfs:label),trigrams(x.osnp:valueLabel, y.rdfs:label))",
                    0.5);
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertTrue(p.getOperator().equals(Parser.MAX));
        assertTrue(p.getLeftTerm().equals("trigrams(x.skos:prefLabel,y.rdfs:label)"));
        assertTrue(p.getRightTerm().equals("trigrams(x.osnp:valueLabel,y.rdfs:label)"));
    }

    @Test
    public void atomicParcer() {

        try {
            Parser p = new Parser(
                    "blabala(trigrams(x.skos:prefLabel,y.rdfs:label)|0.5,trigrams(x.osnp:valueLabel, y.rdfs:label)|0.5)",
                    0.5);
            p.isAtomic();
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void weightedTest(){
    	double theta = 0.8;
    	Parser p = new Parser("ADD("
    							+ "0.5* ADD("
    								+ "0.8* ExactMatch(a.purl:identifier, b.sg:hasPerson),"
    								+ "0.2* Jaro(a.foaf:name, b.sg:publishedName)),"
    							+ "0.5* ADD("
    								+ "0.8* ExactMatch(a.DBgridID, b.SGgridID),"
    								+ "0.2* Jaro(a.foaf:name, b.SGgridName)))",theta);
    	assertEquals("ADD(0.8*ExactMatch(a.purl:identifier,b.sg:hasPerson),0.2*Jaro(a.foaf:name,b.sg:publishedName))",p.getLeftTerm());
    	assertEquals("ADD(0.8*ExactMatch(a.DBgridID,b.SGgridID),0.2*Jaro(a.foaf:name,b.SGgridName))",p.getRightTerm());
    	assertEquals(0.5,p.getLeftCoefficient(),0.0);
    	assertEquals(0.5,p.getRightCoefficient(),0.0);
        Parser p1 = new Parser(p.getLeftTerm(), Math.abs(theta - p.getRightCoefficient()) / p.getLeftCoefficient());
    	assertEquals("ExactMatch(a.purl:identifier,b.sg:hasPerson)", p1.getLeftTerm());
    	assertEquals("Jaro(a.foaf:name,b.sg:publishedName)",p1.getRightTerm());
    	assertEquals(0.8,p1.getLeftCoefficient(),0.0);
    	assertEquals(0.2,p1.getRightCoefficient(),0.0);
        Parser p2 = new Parser(p.getRightTerm(), Math.abs(theta - p.getLeftCoefficient()) / p.getRightCoefficient());
        assertEquals("ExactMatch(a.DBgridID,b.SGgridID)",p2.getLeftTerm());
        assertEquals("Jaro(a.foaf:name,b.SGgridName)", p2.getRightTerm());
    	assertEquals(0.8,p2.getLeftCoefficient(),0.0);
    	assertEquals(0.2,p2.getRightCoefficient(),0.0);
    }
    
    @Test
    public void weih2(){
    	Parser p = new Parser("ADD(0.3*trigrams(x.rdfs:label,y.dc:title)|0.3, 0.7*euclidean(x.lat|x.long,y.latitude|y.longitude)|0.5)",0.8);
    	System.out.println(p.getLeftCoefficient());
    	System.out.println(p.getRightCoefficient());
    }

}
