package org.aksw.limes.core.io.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.aksw.limes.core.io.preprocessing.Preprocessor;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implements a cache that is exclusively in memory. Fastest cache as it does
 * not need to read from the hard drive.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Klaus Lyko
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 8, 2015
 */
public class MemoryCache extends ACache {
    private static final Logger logger = LoggerFactory.getLogger(MemoryCache.class);
    
    // maps uris to instance. A bit redundant as instance contain their URI
    protected HashMap<String, Instance> instanceMap = null;

    // Iterator for getting next instance
    protected Iterator<Instance> instanceIterator;

    public MemoryCache() {
        instanceMap = new HashMap<String, Instance>();
    }

    /**
     * Returns the next instance in the list of instances
     *
     * @return null if no next instance, else the next instance
     */
    public Instance getNextInstance() {
        if (instanceIterator == null) {
            instanceIterator = instanceMap.values().iterator();
        }

        if (instanceIterator.hasNext()) {
            return instanceIterator.next();
        } else {
            return null;
        }
    }

    /**
     * Returns all the instance contained in the cache
     *
     * @return ArrayList containing all instances
     */
    public ArrayList<Instance> getAllInstances() {
        return new ArrayList<Instance>(instanceMap.values());
    }

    public void addInstance(Instance i) {
        if (instanceMap.containsKey(i.getUri())) {
            // Instance m = instanceMap.get(i.getUri());
        } else {
            instanceMap.put(i.getUri(), i);
        }
    }

    /**
     * @param uri
     *         URI to look for
     * @return The instance with the URI uri if it is in the cache, else null
     */
    @Override
    public Instance getInstance(String uri) {
        if (instanceMap.containsKey(uri)) {
            return instanceMap.get(uri);
        } else {
            return null;
        }
    }

    /**
     * @return The size of the cache
     */
    @Override
    public int size() {
        return instanceMap.size();
    }

    /**
     * Adds a new spo statement to the cache
     *
     * @param s
     *         The URI of the instance linked to o via p
     * @param p
     *         The property which links s and o
     * @param o
     *         The value of the property of p for the entity s
     */
    @Override
    public void addTriple(String s, String p, String o) {
        if (instanceMap.containsKey(s)) {
            Instance m = instanceMap.get(s);
            m.addProperty(p, o);
        } else {
            Instance m = new Instance(s);
            m.addProperty(p, o);
            instanceMap.put(s, m);
        }
    }

    /**
     * @param i
     *         The instance to look for
     * @return true if the URI of the instance is found in the cache
     */
    public boolean containsInstance(Instance i) {
        return instanceMap.containsKey(i.getUri());
    }

    /**
     * @param uri
     *         The URI to looks for
     * @return True if an instance with the URI uri is found in the cache, else
     * false
     */
    public boolean containsUri(String uri) {
        return instanceMap.containsKey(uri);
    }

    public void resetIterator() {
        instanceIterator = instanceMap.values().iterator();
    }

    @Override
    public String toString() {
        return instanceMap.toString();
    }

    @Override
    public ArrayList<String> getAllUris() {
        return new ArrayList<String>(instanceMap.keySet());
    }

    public ACache getSample(int size) {
        ACache c = new MemoryCache();
        ArrayList<String> uris = getAllUris();
        while (c.size() < size) {
            int index = (int) Math.floor(Math.random() * size());
            Instance i = getInstance(uris.get(index));
            c.addInstance(i);
        }
        return c;
    }

    public ACache addProperty(String sourcePropertyName, String targetPropertyName, String processingChain) {
    	LinkedHashMap<String, Map<String,String>> functions = new LinkedHashMap<>();
    	HashMap<String, String> f1 = new HashMap<>();
    	f1.put(targetPropertyName, processingChain);
    	functions.put(sourcePropertyName,f1);
    	ACache c = Preprocessor.applyFunctionsToCache(this, functions, true);
        logger.debug("Cache is ready");
        return c;
    }

    /**
     * Returns a set of properties (most likely) all instances have.
     *
     * @return set of all properties
     */
    public Set<String> getAllProperties() {
        logger.debug("Get all properties...");
        if (size() > 0) {
            HashSet<String> props = new HashSet<String>();
            ACache c = this;
            for (Instance i : c.getAllInstances()) {
                props.addAll(i.getAllProperties());
            }
            return props;
        } else {
            return new HashSet<String>();
        }
    }

    public void replaceInstance(String uri, Instance a) {
        if (instanceMap.containsKey(uri)) {
            instanceMap.remove(uri);
        }
        instanceMap.put(uri, a);
    }

    public Model parseCSVtoRDFModel(String baseURI, String IDbaseURI, String rdfType) {
        if (baseURI.length() > 0 && !(baseURI.endsWith("#") || baseURI.endsWith("/"))) {
            baseURI += "#";
        }
        Model model = ModelFactory.createDefaultModel();
        // 2nd create Properties
        Resource r_rdfType = model.createResource(baseURI + rdfType);
        Set<String> props = getAllProperties();
        Map<String, Property> map = new HashMap<String, Property>();
        for (String prop : props) {
            map.put(prop, model.createProperty(baseURI + prop));
        }
        Instance i = getNextInstance();
        while (i != null) {

            String uri = IDbaseURI + i.getUri();
            // create resource with id
            Resource r = model.createResource(uri);
            Statement typeStmt = model.createStatement(r, RDF.type, r_rdfType);
            model.add(typeStmt);
            logger.debug("Created statement: " + typeStmt);
            props = i.getAllProperties();
            for (String prop : props) {
                for (String value : i.getProperty(prop)) {
                    Literal lit = model.createLiteral(value);
                    Statement stmt = model.createStatement(r, map.get(prop), lit);
                    logger.debug("Created statement: " + stmt);
                    model.add(stmt);
                }
            }
            i = getNextInstance();
        }
        return model;
    }

    /**
     * Ignores instanceIterator since there is no sane way to test the equality of iterators 
     */
	@Override
	public MemoryCache clone() {
		MemoryCache clone = new MemoryCache();
		for(Instance i : getAllInstances()){
			clone.addInstance(i.copy());
		}
		return clone;
	}

	@Override
	public int hashCode() {
		return Objects.hash(instanceMap);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MemoryCache){
			final MemoryCache other = (MemoryCache) obj;
			return Objects.equals(instanceMap, other.instanceMap);
		}else{
			return false;
		}
	}

}
