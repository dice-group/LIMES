package org.aksw.limes.core.measures.mapper.topology.im;

import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.measures.mapper.pointsets.OrchidMapper;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;
import com.vividsolutions.jts.simplify.VWSimplifier;


public class PolygonSiplification {


	Set<Polygon> sourceWithSimpilification(String str1,String str2) throws ParseException {
		double time1;
		double time2 ;
		double time3=0;
		final String ngeo = "http://www.opengis.net/ont/geosparql#";
		Set<Polygon> polygons=new HashSet<Polygon>();
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
		WKTReader reader = new WKTReader( geometryFactory );
		Property p = ResourceFactory.createProperty(ngeo,"asWKT");
		Model model=ModelFactory.createDefaultModel();
		double value = Double.parseDouble(str1);
		java.io.InputStream in = FileManager.get().open( str2 );
		if (in == null) {
			throw new IllegalArgumentException(str2 + " not found");
		}
		else
			model.read(in, null, "TTL");

		StmtIterator iter = model.listStatements(null, p, (RDFNode) null);
		while (iter.hasNext()) {

			Statement stmt = iter.nextStatement();
			Resource sub=stmt.getSubject();
			Property pro=stmt.getPredicate();
			RDFNode o = stmt.getObject();

			String strO = o.toString();
			if(!strO.contains("MULTIPOLYGON")) {
				strO=	strO.substring(strO.indexOf("("),strO.indexOf(")"));
				strO="POLYGON "+strO+"))";}
			time1= System.currentTimeMillis();
			Geometry pLtemp=  reader.read(strO);
			Geometry geomTemp=	TopologyPreservingSimplifier.simplify(pLtemp,value);
			String str= geomTemp.toString();
			time2= System.currentTimeMillis();
			time3=time3+(time2-time1);

			String strP = pro.toString();
			strP.replaceAll(strP, "asWKT");
			String strS = sub.toString();
			Polygon sourceWithSimpilification=new Polygon(strS, OrchidMapper.getPoints(str));
			polygons.add(sourceWithSimpilification);
		}
		return polygons ;
	}

	Set<Polygon> sourceWithoutSimpilification(String str2) {

		final String ngeo = "http://www.opengis.net/ont/geosparql#";
		Set<Polygon> polygons1=new HashSet<Polygon>();
		Property p = ResourceFactory.createProperty(ngeo,"asWKT");
		Model model=ModelFactory.createDefaultModel();
		java.io.InputStream in = FileManager.get().open( str2 );
		if (in == null) {
			throw new IllegalArgumentException(str2 + " not found");
		}
		else
			model.read(in, null, "TTL");
		StmtIterator iter = model.listStatements(null, p, (RDFNode) null);
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			Resource sub=stmt.getSubject();
			Property pro=stmt.getPredicate();
			RDFNode o = stmt.getObject();
			String strO = o.toString();
			if(!strO.contains("MULTIPOLYGON")) {
				strO=	strO.substring(strO.indexOf("("),strO.indexOf(")"));
				strO="POLYGON "+strO+"))";}
			String strP = pro.toString();
			strP.replaceAll(strP, "asWKT");
			String strS = sub.toString();
			Polygon sourceWithoutSimpilification=new Polygon(strS, OrchidMapper.getPoints(strO));
			polygons1.add(sourceWithoutSimpilification);
		}
		return polygons1 ;
	}

	ACache cacheWithoutSimplification(String str2) throws ParseException {

		String ngeo = "http://www.opengis.net/ont/geosparql#";
		Property p = ResourceFactory.createProperty(ngeo,"asWKT");
		Model model=ModelFactory.createDefaultModel();
		java.io.InputStream in = FileManager.get().open( str2 );
		if (in == null) {
			throw new IllegalArgumentException(str2 + " not found");
		}
		else
			model.read(in, null, "TTL");
		StmtIterator iter = model.listStatements(null, p, (RDFNode) null);
		ACache s1 = new MemoryCache();
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			Resource sub=stmt.getSubject();
			Property pro=stmt.getPredicate();
			RDFNode o = stmt.getObject();
			String strO = o.toString();
			if(!strO.contains("MULTIPOLYGON")) {
				strO=	strO.substring(strO.indexOf("("),strO.indexOf(")"));
				strO="POLYGON "+strO+"))";}
			String strP = pro.toString();
			s1.addTriple(sub.toString(), strP.replaceAll(strP, "asWKT"), strO);
		}
		return s1 ;
	}

	static ACache cacheWithSimpilification(String str1,String str2) throws ParseException {
		double time1;
		double time2 ;
		double time3=0;
		ACache s1 = new MemoryCache();
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
		WKTReader reader = new WKTReader( geometryFactory );
		String ngeo = "http://www.opengis.net/ont/geosparql#";
		Property p = ResourceFactory.createProperty(ngeo,"asWKT");
		Model model=ModelFactory.createDefaultModel();
		double value = Double.parseDouble(str1);
		java.io.InputStream in = FileManager.get().open( str2 );
		if (in == null) {
			throw new IllegalArgumentException(str2 + " not found");
		}
		else
			model.read(in, null, "TTL");
		StmtIterator iter = model.listStatements(null, p, (RDFNode) null);
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			Resource sub=stmt.getSubject();
			Property pro=stmt.getPredicate();
			RDFNode o = stmt.getObject();
			String strO = o.toString();
			if(!strO.contains("MULTIPOLYGON")) {
				strO=	strO.substring(strO.indexOf("("),strO.indexOf(")"));
				strO="POLYGON "+strO+"))";}
			time1= System.nanoTime()/1000000;
			Geometry pLtemp=  reader.read(strO);
			Geometry geomTemp=	VWSimplifier.simplify(pLtemp,value); //TopologyPreservingSimplifier VWSimplifier
			time2= System.nanoTime()/1000000;
			time3=time3+(time2-time1);
			String strP = pro.toString();
			s1.addTriple(sub.toString(), strP.replaceAll(strP, "asWKT"), geomTemp.toString());
		}
		System.out.println("the total time is "+time3);
		return s1 ;
	}
}

