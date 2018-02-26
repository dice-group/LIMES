package org.aksw.limes.core.measures.mapper.topology.im;

import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class Testing {

	public static void main(String[] args) throws ParseException{

		GeometryFactory geometryFactory =JTSFactoryFinder.getGeometryFactory( null );
		WKTReader reader = new WKTReader( geometryFactory );

		Geometry g1= reader.read("POLYGON ((20 10, 30 0, 40 10, 30 20, 20 10))");
		Geometry g2= reader.read("POLYGON ((10 10, 20 0, 30 10, 20 20, 10 10))");

		RelateDE9IM relatedeIM=new RelateDE9IM();

		int[]im;
		im=new int[9];
		im=relatedeIM.relateIM(g1, g2);
		for(int i=0;i<9;i++)
			System.out.println(" im "+ im[i]);

		double t1=System.nanoTime();
		System.out.println("contains:"+g1.contains(g2));
		System.out.println("equals: "+g1.equals(g2));
		System.out.println("disjoint: "+g1.disjoint(g2));
		System.out.println("coverdby: "+g1.coveredBy(g2));
		System.out.println("covers: "+g1.covers(g2));
		System.out.println("within: "+g1.within(g2));
		System.out.println("intersects: "+g1. intersects(g2));
		System.out.println("overlaps: "+g1.overlaps(g2));
		System.out.println("crosses: "+g1.crosses(g2));
		System.out.println("the TIME1= "+(System.nanoTime()-t1)/1000);

		double t2=System.nanoTime();
		System.out.println("contains: "+relatedeIM.isContains());
		System.out.println("equals: "+relatedeIM.isEquals());
		System.out.println("disjoint:  "+relatedeIM.isDisjoint());
		System.out.println("coverdby: "+relatedeIM.isCoveredBy());
		System.out.println("covers: "+relatedeIM.isCovers());
		System.out.println("within: "+relatedeIM.isWithin());
		System.out.println("intersects: "+relatedeIM. isIntersects());
		System.out.println("overlaps: "+relatedeIM.isOverlaps());
		System.out.println("crosses: "+relatedeIM.isCrosses(g1,g2));
		System.out.println("the TIME2= "+(System.nanoTime()-t2)/1000);
	}



}
