/**
 * 
 */
package org.aksw.limes.core.io.config.reader.rdf;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 12, 2015
 */
public class LIMES {				  
	public static final String uri = "http://limes.sf.net/ontology/";
	public static final String prefix = "limes";

	private static Property property(String name) {
		Property result = ResourceFactory.createProperty(uri + name);
		return result;
	}

	protected static final Resource resource( String local ){ 
		return ResourceFactory.createResource( uri + local ); 
	}

	public static String getURI(){ return uri;	}

	public static final Property hasSource		= property("hasSource");
	public static final Property hasTarget		= property("hasTarget");
	public static final Property hasMetric		= property("hasMetric");
	public static final Property hasAcceptance	= property("hasAcceptance");
	public static final Property hasReview		= property("hasReview");
	public static final Property endPoint		= property("endPoint");
	public static final Property variable		= property("variable");
	public static final Property pageSize		= property("pageSize");
	public static final Property restriction 	= property("restriction");
	public static final Property property 		= property("property");
	public static final Property expression 	= property("expression");
	public static final Property threshold 		= property("threshold");
	public static final Property file 			= property("file");
	public static final Property relation 		= property("relation");
	public static final Property graph 			= property("graph");
	public static final Property type 			= property("type");
	public static final Property exemplars 		= property("examplars");
	public static final Property executionPlan 	= property("executionPlan");
	public static final Property granularity 	= property("granularity");
	public static final Property outputFormat 	= property("outputFormat");
	
	public static final Resource LimesSpecs 	= resource( "LimesSpecs" );
	public static final Resource SourceDataset 	= resource( "SourceDataset" );
	public static final Resource TargetDataset 	= resource( "TargetDataset" );
	public static final Resource Metric 		= resource( "Metric" );
	public static final Resource Acceptance 	= resource( "Acceptance" );
	public static final Resource Review 		= resource( "Review" );
	
	
}
