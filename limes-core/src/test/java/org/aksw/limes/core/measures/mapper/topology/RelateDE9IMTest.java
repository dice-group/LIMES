package org.aksw.limes.core.measures.mapper.topology;

import org.aksw.limes.core.measures.mapper.topology.im.RelateDE9IM;
import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RelateDE9IMTest extends TestCase {
	GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
	WKTReader reader = new WKTReader( geometryFactory );

	public RelateDE9IMTest(String name) { super(name); }

	public static TestSuite suite() { return new TestSuite(RelateDE9IMTest.class); }


	public void testIsDisjoint()  {

		assertTrue((new RelateDE9IM("FF*FF****")).isDisjoint());
		assertTrue((new RelateDE9IM("FF1FF2T*0")).isDisjoint());
		assertTrue(! (new RelateDE9IM("*F*FF****")).isDisjoint());
	}

	public void testIsTouches() {
		assertTrue((new RelateDE9IM("FT*******")).isTouches());
		assertTrue((new RelateDE9IM("F**T*****")).isTouches());
		assertTrue((new RelateDE9IM("F***T****")).isTouches());	
	}

	public void testIsIntersects() {

		assertTrue(! (new RelateDE9IM("FF*FF****")).isIntersects());
		assertTrue(! (new RelateDE9IM("FF1FF2T*0")).isIntersects());
		assertTrue((new RelateDE9IM("*F*FF****")).isIntersects());

	}

	public void testIsWithin() {

		assertTrue((new RelateDE9IM("T0F00F000")).isWithin());
		assertTrue(! (new RelateDE9IM("T00000FF0")).isWithin());
	}

	public void testIsContains() {

		assertTrue(! (new RelateDE9IM("T0F00F000")).isContains());
		assertTrue((new RelateDE9IM("T00000FF0")).isContains());
	}

	public void testIsEquals()  {

		assertTrue((new RelateDE9IM("0FFFFFFF2")).isEquals());
		assertTrue((new RelateDE9IM("1FFF0FFF2")).isEquals());
		assertTrue((new RelateDE9IM("2FFF1FFF2")).isEquals());

		assertTrue(! (new RelateDE9IM("0F0FFFFF2")).isEquals());
		assertTrue(  (new RelateDE9IM("1FFF1FFF2")).isEquals());
		assertTrue(! (new RelateDE9IM("2FFF1*FF2")).isEquals());
	}

	public void testIsOverlaps()  {

		assertTrue((new RelateDE9IM("2*2***2**")).isOverlaps());
		assertTrue((new RelateDE9IM("1*2***2**")).isOverlaps());
		assertTrue(! (new RelateDE9IM("0FFFFFFF2")).isOverlaps());
		assertTrue(! (new RelateDE9IM("1FFF0FFF2")).isOverlaps());
		assertTrue(! (new RelateDE9IM("2FFF1FFF2")).isOverlaps());
	}
	public void testIsCrosses(){
		//Geometry g1= (Polygon) reader.read("POLYGON ((20 10, 30 0, 40 10, 30 20, 20 10))");
		//Geometry g2= (Polygon) reader.read("POLYGON ((10 10, 20 0, 30 10, 20 20, 10 10))");
		assertTrue(! (new RelateDE9IM("T*T******")).isCrosses());
		assertTrue(! (new RelateDE9IM("T*****T**")).isCrosses());
		assertTrue(! (new RelateDE9IM("0********")).isCrosses());
	}

	public void testIsCovers() {

		assertTrue((new RelateDE9IM("T*****FF*")).isCovers());
		assertTrue((new RelateDE9IM("*T****FF*")).isCovers());
		assertTrue((new RelateDE9IM("***T**FF*")).isCovers());
		assertTrue((new RelateDE9IM("****T*FF*")).isCovers());
	}
	public void testIsCoveredBy() {

		assertTrue((new RelateDE9IM("T*F**F***")).isCoveredBy());
		assertTrue((new RelateDE9IM("*TF**F***")).isCoveredBy());
		assertTrue((new RelateDE9IM("**FT*F***")).isCoveredBy());
		assertTrue((new RelateDE9IM("**F*TF***")).isCoveredBy());

	}

}