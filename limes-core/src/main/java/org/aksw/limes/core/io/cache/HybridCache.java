package org.aksw.limes.core.io.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.query.IQueryModule;
import org.aksw.limes.core.io.query.NoPrefixSparqlQueryModule;
import org.aksw.limes.core.io.query.QueryModuleFactory;
import org.apache.log4j.Logger;


/**
 * This cache implements a hybrid between memory and file cache. It generates a
 * hash for each data source associated with it and serializes the content of the
 * corresponding data source into a file. If another mapping task is associated
 * with the same data source, it retrieves the corresponding data from the file,
 * which is obviously more efficient for online data sources (no HTTP latency,
 * offline processing, etc.). Else, it retrieves the data, generates a hash and
 * caches it on the hard drive.
 * Enhancing it with folders: specify the folder, where the application has 
 * permissions to read and write files.
 * 
 * @author ngonga
 * @author Lyko
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 25, 2015
 */
public class HybridCache extends MemoryCache implements Serializable{
	static Logger logger = Logger.getLogger(HybridCache.class.getName());
	
	private static final long serialVersionUID = -2268344215686055231L;
	// maps uris to instance. A bit redundant as instance contain their URI
	protected HashMap<String, Instance> instanceMap;
    //Iterator for getting next instance
	protected Iterator<Instance> instanceIterator;
    
    // pointing to the parent folder of the "cache" folder
    private File folder = new File("");
    
    public HybridCache() {
        instanceMap = new HashMap<String, Instance>();
    }
    /**
     * Create cache specifying the parent folder. Make shure the Application has write permissions there.
     * @param folder File pointing to the the parent folder of the (to-be-created) "cache" folder.
     */
    public HybridCache(File folder) {
    	this();
    	setFolder(folder);
    }

    /**
     * Returns the next instance in the list of instances
     * @return null if no next instance, else the next instance
     */
    public Instance getNextInstance() {
    	if(instanceIterator == null) {
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
     * @return ArrayList containing all instances
     */
    public ArrayList<Instance> getAllInstances() {
        return new ArrayList<Instance>(instanceMap.values());
    }

    public void addInstance(Instance i) {
//    	System.out.println("Adding Instance " + i);
        if (instanceMap.containsKey(i.getUri())) {
//            Instance m = instanceMap.get(i.getUri());

        } else {
            instanceMap.put(i.getUri(), i);
        }
    }

    /**
     *
     * @param uri URI to look for
     * @return The instance with the URI uri if it is in the cache, else null
     */
    public Instance getInstance(String uri) {
        if (instanceMap.containsKey(uri)) {
            return instanceMap.get(uri);
        } else {
            return null;
        }
    }

    /**
     *
     * @return The size of the cache
     */
    public int size() {
        //logger.info("Size of key set = "+instanceMap.keySet().size());
        return instanceMap.size();
    }

    /**
     * Adds a new spo statement to the cache
     * @param s The URI of the instance linked to o via p
     * @param p The property which links s and o
     * @param o The value of the property of p for the entity s
     */
    public void addTriple(String s, String p, String o) {
    	//logger.info(instanceMap.containsKey(s));
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
     *
     * @param uri The URI to looks for
     * @return True if an instance with the URI uri is found in the cache, else false
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

    public ArrayList<String> getAllUris() {
        return new ArrayList<String>(instanceMap.keySet());
    }

    /**
	 *
	 * @param i The instance to look for
	 * @return true if the URI of the instance is found in the cache
	 */
	public boolean containsInstance(Instance i) {
	    return instanceMap.containsKey(i.getUri());
	}
	/** Tries to serialize the content of the cache to a file. If it fails,
     * no file is written to avoid the corruption of future data sources.
     * 
     * @param file File wherein the content of the cache is to be serialized
     * @throws IOException
     */
    public void saveToFile(File file) {
        FileOutputStream out;
        logger.info("Serializing " + size() + " objects to " + file.getAbsolutePath());

        try {
            out = new FileOutputStream(file);
            ObjectOutputStream serializer = new ObjectOutputStream(out);
            serializer.writeObject(this);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            file.delete();
        }
    }

    /** Tries to load the content of the cache from a file
     *
     * @param file File from which the content is to be loaded
     * @return A Hybrid cache
     * @throws IOException
     */
    public static HybridCache loadFromFile(File file) throws IOException {
    	String path = file.getAbsolutePath();
        String parentPath = path.substring(0, path.lastIndexOf("cache"));
        File parent = new File(parentPath);
        
        FileInputStream in = new FileInputStream(file);
        ObjectInputStream deSerializer = new ObjectInputStream(in);
        HybridCache cache;

        try {
            cache = (HybridCache) deSerializer.readObject();
            cache.setFolder(parent);
            return cache;
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        } finally {
            in.close();
        }
    }

    public static HybridCache getData(KBInfo kb) {
    	return getData(new File(""), kb);
    }
    
    
    /**
     * Method to get Data of the specified endpoint, and cache it to the "cache" folder in the folder specified.
     * @param folder Path to the parent folder of the "cache" folder.
     * @param kb Endpoint specification.
     * @return
     */
    public static HybridCache getData(File folder, KBInfo kb) {

        HybridCache cache = new HybridCache(folder);
        //1. Try to get content from a serialization
        String hash = kb.hashCode()+"";
        File cacheFile = new File(folder+"cache/" + hash + ".ser");
        logger.info("Checking for file "+cacheFile.getAbsolutePath());
        try {
            if (cacheFile.exists()) {
                logger.info("Found cached data. Loading data from file " + cacheFile.getAbsolutePath());
                cache = HybridCache.loadFromFile(cacheFile);
            }
            if (cache.size() == 0) {
                throw new Exception();
            } else {
                logger.info("Cached data loaded successfully from file " + cacheFile.getAbsolutePath());
                logger.info("Size = " + cache.size());
            }
        } //2. If it does not work, then get it from data sourceInfo as specified
        catch (Exception e) {
//        	e.printStackTrace();
            // need to add a QueryModuleFactory
            logger.info("No cached data found for " + kb.getId());
            IQueryModule module = QueryModuleFactory.getQueryModule(kb.getType(), kb);
            module.fillCache(cache);

            if (!new File(folder.getAbsolutePath()+ File.separatorChar + "cache").exists() || !new File(folder.getAbsolutePath()+ File.separatorChar + "cache").isDirectory()) {
                new File(folder.getAbsolutePath()+ File.separatorChar + "cache").mkdir();
            }
            cache.saveToFile(new File(folder.getAbsolutePath()+ File.separatorChar + "cache/" + hash + ".ser"));
        }

        return cache;
    }
    
    /** This method is used by learners which do not have prefix information.
    *
    * @param kb Info to the knowledge base to query
    * @return A cache filled with the entities to link
    */
    public static HybridCache getNoPrefixData(KBInfo kb) {
    	return getNoPrefixData(new File (""), kb);
    }
    
    /** This method is used by learners which do not have prefix information and with a specified folder containing the cache folder.
     *
     * @param folder Path to parent folder of the supposed cache folder.
     * @param kb Info to the knowledge base to query
     * @return A cache filled with the entities to link
     */
    public static HybridCache getNoPrefixData(File folder, KBInfo kb) {
        HybridCache cache = new HybridCache();
        //1. Try to get content from a serialization
        File cacheFile = new File(folder.getAbsolutePath()+ File.separatorChar +"cache/" + kb.hashCode() + ".ser");
        try {
            if (cacheFile.exists()) {
                logger.info("Found cached data. Loading data from file " + cacheFile.getAbsolutePath());
                cache = HybridCache.loadFromFile(cacheFile);
            }
            if (cache.size() == 0) {
                throw new Exception();
            } else {
                logger.info("Cached data loaded successfully from file " + cacheFile.getAbsolutePath());
                logger.info("Size = " + cache.size());
            }
        } //2. If it does not work, then get it from data sourceInfo as specified
        catch (Exception e) {
            // need to add a QueryModuleFactory
            logger.info("No cached data found for " + kb.getId());
            NoPrefixSparqlQueryModule module = new NoPrefixSparqlQueryModule(kb);
            module.fillCache(cache);

            if (!new File(folder.getAbsolutePath()+ File.separatorChar + "cache").exists() || !new File(folder.getAbsolutePath()+ File.separatorChar + "cache").isDirectory()) {
                new File(folder.getAbsolutePath()+ File.separatorChar + "cache").mkdir();
            }
            cache.saveToFile(new File(folder.getAbsolutePath()+ File.separatorChar + "cache/" + kb.hashCode() + ".ser"));
        }

        return cache;
    }

    
    /**
     * Returns the file  pointing to the parent folder of cache.
     * @return
     */
	public File getFolder() {
		return folder;
	}
	/**
	 * Set the parent folder of the cache sub folder.
	 * @param folder Pointing to the parent folder holding the cache. 
	 */
	public void setFolder(File folder) {
			this.folder = folder;
	}
}