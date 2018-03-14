package org.aksw.limes.core.measures.mapper.topology.im;

import java.util.ArrayList;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class Testing {

	public static void main(String[] args) throws ParseException{




		ArrayList<String> strs=new ArrayList<String>();
		String str = "";
		String ngeo = "http://geovocab.org/geometry#";
		Property p = ResourceFactory.createProperty(ngeo,"toWKT");
		String inputtFile= "/home/abdullah/Downloads/final_NT_out1234.nt";
		Model m= Reader.readModel(inputtFile);
		//Model m=ModelFactory.createDefaultModel();
		StmtIterator iter = m.listStatements(null, p, (RDFNode) null);

		while (iter.hasNext()) {

			Statement stmt = iter.nextStatement();
			RDFNode o = stmt.getObject();
			str = o.toString();
			strs.add(str);

		}
		//System.out.println(" STRING "+str);
		strs.addAll(strs);
		strs.addAll(strs);
		strs.addAll(strs);
		strs.addAll(strs);
		strs.addAll(strs);
		strs.addAll(strs);
		strs.addAll(strs);
		strs.addAll(strs);
		//strs.addAll(strs);
		//strs.addAll(strs);
		//strs.addAll(strs);


		GeometryFactory geometryFactory =JTSFactoryFinder.getGeometryFactory( null );
		WKTReader reader = new WKTReader( geometryFactory );
		double totalTime_1=0;
		double totalTime_2=0;
		double t1=0;
		double t2=0;

		for(int i=0;i<strs.size();i++) {
			Geometry g1= reader.read(strs.get(i));
			Geometry g2= reader.read(strs.get(i));
			//System.out.println(" the first Polygon: "+g1.toString());
			//	System.out.println(" the second Polygon: "+g2.toString());
			RelateDE9IM relatedeIM=new RelateDE9IM();
			relatedeIM.computeIM(g1, g2);


			t1=System.nanoTime();
			g1.contains(g2);
			g1.touches(g2);
			g1.equals(g2);
			g1.disjoint(g2);
			g1.coveredBy(g2);
			g1.covers(g2);
			g1.within(g2);
			g1. intersects(g2);
			g1.overlaps(g2);
			g1.crosses(g2);
			t2=System.nanoTime();
			totalTime_1=totalTime_1+(t2-t1)/1000000d;

			//			t1_1=System.nanoTime();
			//			relatedeIM.isContains();
			//			relatedeIM. isTouches();
			//			relatedeIM.isEquals();
			//			relatedeIM.isDisjoint();
			//			relatedeIM.isCoveredBy();
			//			relatedeIM.isCovers();
			//			relatedeIM.isWithin();
			//			relatedeIM. isIntersects();
			//			relatedeIM.isOverlaps();
			//			relatedeIM.isCrosses();
			//			t2_2=System.nanoTime();
			//			totalTime_2=totalTime_2+(t2_2-t1_1)/1000000d;
			//			System.out.println("the TOTAL TIME of current RADON= "+totalTime_2);
			//			//System.out.println("the valuf of (i)= "+i);
		}

		System.out.println("the TOTAL TIME of orginal DE9IM implementation= "+totalTime_1);

		//System.out.println("the TOTAL TIME of current DE9IM implementation= "+totalTime_2);
		System.out.print(" the size "+strs.size());
	}

}
