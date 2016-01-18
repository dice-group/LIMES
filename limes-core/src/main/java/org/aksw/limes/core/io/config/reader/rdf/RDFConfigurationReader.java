package org.aksw.limes.core.io.config.reader.rdf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.config.reader.IConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 12, 2015
 */
public class RDFConfigurationReader implements IConfigurationReader{
	private static final Logger logger = Logger.getLogger(RDFConfigurationReader.class.getName());

	
	/**
	 * @param inputFile path to RDF configuration file
	 * @return A filled Configuration object from the inputFile 
	 * @author sherif
	 */
	@Override
	public Configuration read(String inputFile) {
		return read(readModel(inputFile));
	}

	/**
	 * @param configurationModel
	 * @return true if the configurationModel contains all mandatory properties
	 * @author sherif
	 */
	public Configuration read(Model configurationModel){
		configModel = configurationModel;
		StmtIterator stats = configModel.listStatements(null, RDF.type, LIMES.LimesSpecs);
		if(stats.hasNext()){
			specsSubject = stats.next().getSubject();
		}else{
			logger.error("Missing " + LIMES.LimesSpecs + ", Exit with error.");
			System.exit(1);
		}
		configuration.setSourceInfo(new KBInfo());
		configuration.setTargetInfo(new KBInfo());
		configuration.setPrefixes((HashMap<String, String>) configModel.getNsPrefixMap());

		//1. 2. Source & Target information
		readKBDescription((Resource) getObject(specsSubject, LIMES.hasSource, true));
		readKBDescription((Resource) getObject(specsSubject, LIMES.hasTarget, true));

		//3.METRIC
		Resource metric = (Resource) getObject(specsSubject, LIMES.hasMetric, true);
		configuration.setMetricExpression(getObject(metric, LIMES.expression, true).toString());
		
		//4. Number of exemplars
		RDFNode ex = getObject(specsSubject, LIMES.exemplars, false);
		if(ex != null){
			configuration.setExemplars(Integer.parseInt(ex.toString()));
		}

		//5. ACCEPTANCE file and conditions
		Resource acceptance = (Resource) getObject(specsSubject, LIMES.hasAcceptance, true);
		configuration.setAcceptanceThreshold(parseDouble(getObject(acceptance, LIMES.threshold, true).toString()));
		configuration.setAcceptanceFile(getObject(acceptance, LIMES.file, true).toString());
		configuration.setAcceptanceRelation(getObject(acceptance, LIMES.relation, true).toString());

		//6. VERIFICATION file and conditions
		Resource review = (Resource) getObject(specsSubject, LIMES.hasReview, true);
		configuration.setVerificationThreshold(parseDouble(getObject(review, LIMES.threshold, true).toString()));
		configuration.setVerificationFile(getObject(review, LIMES.file, true).toString());
		configuration.setVerificationRelation(getObject(review, LIMES.relation, true).toString());

		//7. EXECUTION plan
		RDFNode execution = getObject(specsSubject, LIMES.executionPlan, false);
		if(execution != null){
			configuration.setExecutionPlan(execution.toString());
		}

		//8. TILING if necessary 
		RDFNode g = getObject(specsSubject, LIMES.granularity, false);
		if(g != null){
			configuration.setGranularity(Integer.parseInt(g.toString()));
		}

		//9. OUTPUT format
		RDFNode output = getObject(specsSubject, LIMES.outputFormat, false);
		if(output != null){
			configuration.setOutputFormat(output.toString());
		}
		return configuration;
	}
	
	private static Model configModel = ModelFactory.createDefaultModel();
	private Resource specsSubject;

	/**
	 * 
	 *@author sherif
	 */
	public RDFConfigurationReader() {
	}

	/**
	 * Read either the source or the dataset description
	 * @param kb
	 * @author sherif
	 */
	public void readKBDescription(Resource kb) {
		KBInfo kbinfo = null;
		if(configModel.contains(kb, RDF.type, LIMES.SourceDataset)) {
			kbinfo = configuration.getSourceInfo();
		}else if(configModel.contains(kb, RDF.type, LIMES.TargetDataset)) {
			kbinfo = configuration.getTargetInfo();
		}else{
			logger.error("Either " + LIMES.SourceDataset + " or " + LIMES.TargetDataset + " type statement is missing");
			System.exit(1);
		}
		kbinfo.setId(getObject(kb, RDFS.label, true).toString());;
		kbinfo.setEndpoint(getObject(kb, LIMES.endPoint, true).toString());
		RDFNode graph = getObject(kb, LIMES.graph, false);
		if(graph != null){
			kbinfo.setGraph(graph.toString());
		}
		for(RDFNode r : getObjects(kb, LIMES.restriction, false)){
			String restriction = r.toString();
			if (restriction.endsWith(".")) {
				restriction = restriction.substring(0, restriction.length() - 1);
			}
			kbinfo.addRestriction(restriction);
		}
		for(RDFNode properity : getObjects(kb, LIMES.property, true)){
			XMLConfigurationReader.processProperty(kbinfo, properity.toString());
		}
		kbinfo.setPageSize(parseInt(getObject(kb, LIMES.pageSize, true).toString()));
		kbinfo.setVar(getObject(kb, LIMES.variable, true).toString());
		RDFNode type = getObject(kb, LIMES.type, false);
		if(type != null){
			kbinfo.setType(type.toString().toLowerCase());
		}
		kbinfo.setPrefixes(configuration.getPrefixes());
	}
	
	/**
	 * @param s
	 * @return
	 * @author sherif
	 */
	private int parseInt(String s){
		if(s.contains("^")){
			s = s.substring(0, s.indexOf("^"));
		}
		if(s.contains("@")){
			s = s.substring(0, s.indexOf("@"));
		}
		return Integer.parseInt(s);
	}

	/**
	 * @param s
	 * @param p
	 * @param isMandatory if set the program exit in case o not found, 
	 * 			otherwise a null value returned
	 * @return the object o of triple (s, p, o) if exists, null otherwise
	 * @author sherif
	 */
	private static RDFNode getObject(Resource s, Property p, boolean isMandatory){
		StmtIterator statements = configModel.listStatements(s, p, (RDFNode) null);
		if(statements.hasNext()){
			return statements.next().getObject();	
		}else{
			if(isMandatory){
				logger.error("Missing mandatory property " + p + ", Exit with error.");
				System.exit(1);
			}
		}
		return null;
	}

	/**
	 * @param s
	 * @param p
	 * @param isMandatory if set the program exit in case o not found, 
	 * 			otherwise a null value returned
	 * @return Set of all objects o of triples (s, p, o) if exist, null otherwise
	 * @author sherif
	 */
	private static Set<RDFNode> getObjects(Resource s, Property p, boolean isMandatory){
		Set<RDFNode> result = new HashSet<>();
		StmtIterator statements = configModel.listStatements(s, p, (RDFNode) null);
		while(statements.hasNext()){
			result.add(statements.next().getObject());	
		}
		if(isMandatory && result.size() == 0){
			logger.error("Missing mandatory property: " + p + ", Exit with error.");
			System.exit(1);
		}else if(result.size() == 0){
			return null;
		}
		return result;
	}


	/**
	 * @param s
	 * @return
	 * @author sherif
	 */
	private double parseDouble(String s){
		if(s.contains("^")){
			s = s.substring(0, s.indexOf("^"));
		}
		if(s.contains("@")){
			s = s.substring(0, s.indexOf("@"));
		}
		return Double.parseDouble(s);
	}


	/**
	 * read RDF model from file/URL
	 * @param fileNameOrUri
	 * @return
	 * @author sherif
	 */
	public static Model readModel(String fileNameOrUri)
	{
		long startTime = System.currentTimeMillis();
		Model model=ModelFactory.createDefaultModel();
		java.io.InputStream in = FileManager.get().open(System.getProperty("user.dir")+"/"+fileNameOrUri );
		if (in == null) {
			throw new IllegalArgumentException(fileNameOrUri + " not found");
		}
		if(fileNameOrUri.contains(".ttl") || fileNameOrUri.contains(".n3")){
			logger.info("Opening Turtle file");
			model.read(in, null, "TTL");
		}else if(fileNameOrUri.contains(".rdf")){
			logger.info("Opening RDFXML file");
			model.read(in, null);
		}else if(fileNameOrUri.contains(".nt")){
			logger.info("Opening N-Triples file");
			model.read(in, null, "N-TRIPLE");
		}else{
			logger.info("Content negotiation to get RDFXML from " + fileNameOrUri);
			model.read(fileNameOrUri);
		}
		logger.info("Loading " + fileNameOrUri + " is done in " + (System.currentTimeMillis()-startTime) + "ms.");
		return model;
	}


}
