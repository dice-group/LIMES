package org.aksw.limes.core.io.cache;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 25, 2015
 */
public abstract class Cache implements ICache{
	public abstract void addInstance(Instance i);
	public abstract Instance getNextInstance();
	public abstract ArrayList<Instance> getAllInstances();
	public abstract ArrayList<String> getAllUris();
	public abstract void addTriple(String s, String p, String o);
	public abstract boolean containsInstance(Instance i);
	public abstract boolean containsUri(String uri);
	public abstract Instance getInstance(String uri);
	public abstract void resetIterator();
	public abstract int size();
	public abstract Cache getSample(int size);
	public abstract void replaceInstance(String uri, Instance a);
	public abstract Set<String> getAllProperties();

	/**
	 * Method to processData according to specific preprocessing steps.
	 * @param propertyProcess Map maps propertyNames to preprocessing functions.
	 * @return
	 */
	public abstract Cache processData(Map<String,String> propertyProcess);

	/**
	 * Method to process data of a property into a new property with specific preprocessing.
	 * @param sourcePropertyName Name of the property to process.
	 * @param targetPropertyName Name of the new property to process data into.
	 * @param processingChain Preprocessing Expression.
	 * @return
	 */
	public abstract Cache addProperty(String sourcePropertyName, String targetPropertyName, String processingChain);


	/**
	 * Basic method to create a JENA Model out of a cache.
	 * Restriction 1: Assumes all objects are literal values. Thus, resource URIs are represented as Strings.
	 * Restriction 2: Adds a rdf:Type statement for all instances.
	 * @param baseURI Base URI of properties, could be empty.
	 * @param IDbaseURI Base URI for id of resources: URI(instance) := IDbaseURI+instance.getID(). Could be empty.
	 * @param rdfType rdf:Type of the instances.
	 * @return JENA RDF Model
	 */
	public abstract Model parseCSVtoRDFModel(String baseURI, String IDbaseURI, String rdfType);

}
