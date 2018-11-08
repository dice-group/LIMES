package org.aksw.limes.core.io.cache;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 8, 2015
 */
public abstract class ACache implements ICache {
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

    public abstract ACache getSample(int size);

    public abstract void replaceInstance(String uri, Instance a);

    public abstract Set<String> getAllProperties();
    
    @Override
    public abstract ACache clone();
    
    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

//    /**
//     * Method to processData according to specific preprocessing steps.
//     *
//     * @param propertyProcess
//     *         Map maps propertyNames to preprocessing functions.
//     * @return cacheof processed data
//     */
//    public abstract ACache processData(Map<String, String> propertyProcess);

    /**
     * Method to process data of a property into a new property with specific preprocessing.
     *
     * @param sourcePropertyName
     *         Name of the property to process.
     * @param targetPropertyName
     *         Name of the new property to process data into.
     * @param processingChain
     *         Preprocessing Expression.
     * @return Cache after property add 
     */
    public abstract ACache addProperty(String sourcePropertyName, String targetPropertyName, String processingChain);


    /**
     * Basic method to create a JENA Model out of a cache.
     * Restriction 1: Assumes all objects are literal values. Thus, resource URIs are represented as Strings.
     * Restriction 2: Adds a rdf:Type statement for all instances.
     *
     * @param baseURI
     *         Base URI of properties, could be empty.
     * @param IDbaseURI
     *         Base URI for id of resources: URI(instance) := IDbaseURI+instance.getID(). Could be empty.
     * @param rdfType
     *         rdf:Type of the instances.
     * @return JENA RDF Model
     */
    public abstract Model parseCSVtoRDFModel(String baseURI, String IDbaseURI, String rdfType);

}
