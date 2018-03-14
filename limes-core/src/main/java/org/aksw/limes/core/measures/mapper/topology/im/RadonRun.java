package org.aksw.limes.core.measures.mapper.topology.im;




import java.util.ArrayList;


import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;

import org.aksw.limes.core.measures.mapper.topology.RADON;
import org.apache.jena.rdf.model.Model;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class RadonRun {

	public static void main(String[] args) {

		ArrayList<String> strs=new ArrayList<String>();

		String ngeo = "http://geovocab.org/geometry#";

		Property p = ResourceFactory.createProperty(ngeo,"toWKT");

		String inputtFile= "/home/abdullah/Downloads/final_NT_out1234.nt";

		Model m= Reader.readModel(inputtFile);
		//Model m=ModelFactory.createDefaultModel();
		StmtIterator iter = m.listStatements(null, p, (RDFNode) null);
		ACache s = new MemoryCache();

		while (iter.hasNext()) {

			Statement stmt = iter.nextStatement();
			Resource sub=stmt.getSubject();
			Property pro=stmt.getPredicate();
			RDFNode o = stmt.getObject();
			String strO = o.toString();
			String strP = pro.toString();
			String pp=strP.replaceAll(strP, "toWKT");
			//System.out.println("Property is "+pp);
			String strS = sub.toString();
			s.addTriple(strS, pp, strO);
			String str= strO;
			strs.add(str);

		}

		double t=System.currentTimeMillis();
		RADON_2.getMapping(s, s, "?x", "?y", "top_within(x.toWKT, y.toWKT)", 1.0d);
		System.out.println(" the TIME of current Radon= "+(System.currentTimeMillis()-t));
		System.out.println(" is DONE");
		t=System.currentTimeMillis();
		RADON.getMapping(s, s, "?x", "?y", "top_within(x.toWKT, y.toWKT)", 1.0d, RADON.INTERSECTS);
		RADON.getMapping(s, s, "?x", "?y", "top_within(x.toWKT, y.toWKT)", 1.0d, RADON.CONTAINS);
		RADON.getMapping(s, s, "?x", "?y", "top_within(x.toWKT, y.toWKT)", 1.0d, RADON.COVEREDBY);
		RADON.getMapping(s, s, "?x", "?y", "top_within(x.toWKT, y.toWKT)", 1.0d, RADON.COVERS);
		RADON.getMapping(s, s, "?x", "?y", "top_within(x.toWKT, y.toWKT)", 1.0d, RADON.CROSSES);
		RADON.getMapping(s, s, "?x", "?y", "top_within(x.toWKT, y.toWKT)", 1.0d, RADON.DISJOINT);
		RADON.getMapping(s, s, "?x", "?y", "top_within(x.toWKT, y.toWKT)", 1.0d, RADON.OVERLAPS);
		RADON.getMapping(s, s, "?x", "?y", "top_within(x.toWKT, y.toWKT)", 1.0d, RADON.EQUALS);
		RADON.getMapping(s, s, "?x", "?y", "top_within(x.toWKT, y.toWKT)", 1.0d, RADON.WITHIN);
		RADON.getMapping(s, s, "?x", "?y", "top_within(x.toWKT, y.toWKT)", 1.0d, RADON.TOUCHES);
		System.out.println(" the TIME of original RADON= "+(System.currentTimeMillis()-t));
		System.out.println(" is DONE");
	}

}
