package org.aksw.limes.core.io.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains all the data related to a particular URI, i.e., all the (s p o)
 * statements where s is a particular URI. From the point of view of linking, it
 * an instance contains all the data linked to a particular instance ;)
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 12, 2016
 */
public class Instance implements Comparable<Object>, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Instance.class);

    /**
     *
     */
    private static final long serialVersionUID = -8613951110508439148L;
    public double distance;
    private String uri;
    private HashMap<String, TreeSet<String>> properties;
    
    private Blocks blockIds;
    private Blocks blocksToCompare;



    /**
     * Constructor
     *
     * @param _uri
     *         URI of the instance. This is the key to accessing it.
     */
    public Instance(String _uri) {
        uri = _uri;
        properties = new HashMap<String, TreeSet<String>>();
        // distance to exemplar
        distance = -1;
    }

    /**
     * Add a new (property, value) pair
     *
     * @param propUri
     *         URI of the property
     * @param value
     *         value of the property for this instance
     */
    public void addProperty(String propUri, String value) {
        if (properties.containsKey(propUri)) {
            properties.get(propUri).add(value);
        } else {
            TreeSet<String> values = new TreeSet<String>();
            values.add(value);
            properties.put(propUri, values);
        }
    }

    public void addProperty(String propUri, TreeSet<String> values) {
        // propUri = propUri.toLowerCase();
        if (properties.containsKey(propUri)) {
            Iterator<String> iter = values.iterator();
            while (iter.hasNext()) {
                properties.get(propUri).add(iter.next());
            }
        } else {
            properties.put(propUri, values);
        }
    }

    /*
     * Removes the old values of propUri and replaces them with values
     */
    public void replaceProperty(String propUri, TreeSet<String> values) {
        if (properties.containsKey(propUri)) {
            properties.remove(propUri);
        }
        addProperty(propUri, values);
    }

    /**
     * Returns the URI of this instance
     *
     * @return URI of this instance
     */
    public String getUri() {
        return uri;
    }

    /**
     * Return all the values for a given property
     *
     * @param propUri property URI
     * @return TreeSet of values associated with this URI
     */
    public TreeSet<String> getProperty(String propUri) {
        if (properties.containsKey(propUri)) {
            return properties.get(propUri);
        } else {
            logger.debug("Failed to access property <" + propUri + "> on " + uri);
            return new TreeSet<String>();
        }
    }

    /**
     * Returns all the properties associated with this instance
     *
     * @return A set of property Uris
     */
    public Set<String> getAllProperties() {
        return properties.keySet();
    }

    @Override
    public String toString() {
        String s = uri;
        String propUri;
        Iterator<String> iter = properties.keySet().iterator();
        while (iter.hasNext()) {
            propUri = iter.next();
            s = s + "; " + "\n" + propUri + " -> " + properties.get(propUri);
        }
        return s + "; distance = " + distance + "\n";
    }

    /**
     * Comparison with other Instances
     *
     * @param o
     *         Instance for comparison
     * @return 1 if the distance from the exemplar to the current instance is
     * smaller than the distance from the exemplar to o.
     */
    public int compareTo(Object o) {
        if (!o.getClass().equals(Instance.class))
            return -1;
        double diff = distance - ((Instance) o).distance;
        if (diff < 0) {
            return 1;
        } else if (diff > 0) {
            return -1;
        } else {
            return ((Instance) o).uri.compareTo(uri);
        }
    }

    public Instance copy() {
        Instance instance = new Instance(uri);
        HashMap<String, TreeSet<String>> ps = new HashMap<String, TreeSet<String>>();
        for (String p : properties.keySet()) {
            ps.put(p, new TreeSet<String>());
            for (String s : properties.get(p)) {
                ps.get(p).add(s);
            }
        }
        instance.properties = ps;
        return instance;
    }

    /**
     * Removes property with URI uri from this Instance
     *
     * @param uri of the property to be removed
     */
    public void removePropery(String uri) {
        if (properties.containsKey(uri)) {
            properties.remove(uri);
        }
    }

    
    
    /**
     * This class is used to save the Blocks in which a Instance gets put when using HR3.
     * Beware this class DELIBARETLY BREAKS the hashcode/equals by returning the same hashcode for all and
     * using a custom equals/compareTo method. This is used to exploit a join function where all Instances
     * are joined (to be compared with a similarity measure) that are put into at least 1 identical block.
     * 
     * Using this class for anything other than this purpose can deliver questionable results. Especially putting objects
     * of this class into a HashMap is ill-advised.
     * @author Daniel Obraczka
     *
     */
	public static class Blocks implements Comparable<Blocks>{
			public Set<Tuple> ids;

			public Blocks(Set<Tuple> _ids) {
				ids = _ids;
			}

			public <T> Set<T> intersection(Set<T> setA, Set<T> setB) {
				Set<T> tmp = new HashSet<T>();
				for (T x : setA)
					if (setB.contains(x))
						tmp.add(x);
				return tmp;
			}

			//If at least one block is shared this returns true
			@Override
			public boolean equals(Object obj) {
				if (obj instanceof Blocks) {
					if (intersection(((Blocks) obj).ids, this.ids).size() > 0) {
						return true;
					}
				}
				return false;
			}
			
			//Returns 1 on all objects to force the evaluation of equals/compareTo in a join operation
			@Override
			public int hashCode() {
			return 1;
			}

			//If at least one block is shared this returns true
			@Override
			public int compareTo(Blocks o) {
				if (intersection(o.ids, this.ids).size() != 0) {
						return 0;
				}
				if(o.ids.size() < this.ids.size()){
					return 1;
				}
				return -1;
			}


		}

//    public static void main(String[] args){
//    	HashSet<Tuple> s = new HashSet<>();
//    	s.add(new Tuple2<Integer, Integer>(0,1));
//    	s.add(new Tuple2<Integer, Integer>(76,3));
//    	s.add(new Tuple2<Integer, Integer>(54,1200));
//    	Blocks b = new Blocks(s);
//
//    	HashSet<Tuple> s2 = new HashSet<>();
//    	s2.add(new Tuple2<Integer, Integer>(0,1));
//    	s2.add(new Tuple2<Integer, Integer>(75,3));
//    	s2.add(new Tuple2<Integer, Integer>(54,1200));
//    	Blocks b2 = new Blocks(s2);
//    	System.out.println(b.intersectNotEmpty(b2));
//    }
//
//	public static class Blocks{
//			public Set<Tuple> ids;
//
//			public Blocks(Set<Tuple> _ids) {
//				ids = _ids;
//			}
//			
//			public long getId(){
//				long id = 1;
//				for(Tuple t : ids){
//					StringBuilder tupleId = new StringBuilder();
//					for(int i = 0; i < t.getArity(); i++){
//						tupleId.append(t.getField(i).toString());
//					}
//					System.out.println(Integer.valueOf(tupleId.toString()) + 1);
//					id = id * (Integer.valueOf(tupleId.toString()) + 1);
//				}
//				return id;
//			}
//			
//			public boolean intersectNotEmpty(Blocks o){
//				long id = getId();
//				for(Tuple t : o.ids){
//					StringBuilder tupleId = new StringBuilder();
//					for(int i = 0; i < t.getArity(); i++){
//						tupleId.append(t.getField(i).toString());
//					}
//					if(id % (Integer.valueOf(tupleId.toString()) + 1) == 0){
//						return true;
//					}
//				}
//				return false;
//			}
//
//		}

		public Blocks getBlockIds() {
			return blockIds;
		}

		public void setBlockIds(Set<Tuple> id) {
			this.blockIds = new Blocks(id);
		}

		public Blocks getBlocksToCompare() {
			return blocksToCompare;
		}

		public void setBlocksToCompare(Set<Tuple> id) {
			this.blocksToCompare = new Blocks(id);
		}

		public double getDistance() {
			return distance;
		}

		public void setDistance(double distance) {
			this.distance = distance;
		}

		public HashMap<String, TreeSet<String>> getProperties() {
			return properties;
		}

		public void setProperties(HashMap<String, TreeSet<String>> properties) {
			this.properties = properties;
		}

		public static Logger getLogger() {
			return logger;
		}

		public static long getSerialversionuid() {
			return serialVersionUID;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public void setBlockIds(Blocks blockIds) {
			this.blockIds = blockIds;
		}

		public void setBlocksToCompare(Blocks blocksToCompare) {
			this.blocksToCompare = blocksToCompare;
		}
    
}

