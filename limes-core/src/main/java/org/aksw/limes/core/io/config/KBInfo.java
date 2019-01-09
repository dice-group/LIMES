/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.io.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;

/**
 * Contains the information necessary to access a knowledge base
 * 
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 12, 2016
 */
public class KBInfo implements Serializable {

    private static final String DEFAULT_QUERY_TYPE = "sparql";

    private static final long serialVersionUID = 7915400434442160847L;

    protected String id;
    protected String endpoint;
    protected String graph;
    protected String var;
    protected List<String> properties;
    protected List<String> optionalProperties;
    protected ArrayList<String> restrictions;
    protected LinkedHashMap<String, Map<String, String>> functions;
    protected Map<String, String> prefixes;
    protected int pageSize;
    protected String type;
    protected int maxoffset;
    protected int minoffset;

    /**
     * Constructor
     */
    public KBInfo() {
        id = null;
        endpoint = null;
        graph = null;
        restrictions = new ArrayList<>();
        properties = new ArrayList<>();
        optionalProperties = new ArrayList<>();
        prefixes = new HashMap<>();
        functions = new LinkedHashMap<>();
        pageSize = -1;      //-1 means query all at once
        type = DEFAULT_QUERY_TYPE;    //default value
        maxoffset  = -1;
        minoffset = -1;
    }

    /**
     * @param var variable name
     */
    public KBInfo(String var) {
        this();
        this.var = var;
    }

    public KBInfo(String id, String endpoint, String graph, String var,
            List<String> properties, List<String> optionalProperties,
            ArrayList<String> restrictions, LinkedHashMap<String, Map<String, String>> functions,
            Map<String, String> prefixes, int pageSize, String type) {
        super();
        this.id = id;
        this.endpoint = endpoint;
        this.graph = graph;
        this.var = var;
        this.properties = properties;
        this.optionalProperties = optionalProperties;
        this.restrictions = restrictions;
        this.functions = functions;
        this.prefixes = prefixes;
        this.pageSize = pageSize;
        this.type = type;
    }

    public KBInfo(String id, String endpoint, String graph, String var,
                  List<String> properties, List<String> optionalProperties,
                  ArrayList<String> restrictions, LinkedHashMap<String, Map<String, String>> functions,
                  Map<String, String> prefixes, int pageSize, String type, int minoffset, int maxoffset) {
        this(id, endpoint, graph, var, properties, optionalProperties, restrictions, functions,
                prefixes, pageSize, type);
        this.maxoffset = maxoffset;
        this.minoffset = minoffset;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    public List<String> getOptionalProperties() {
        return optionalProperties;
    }

    public void setOptionalProperties(List<String> optionalProperties) {
        this.optionalProperties = optionalProperties;
    }

    public ArrayList<String> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(ArrayList<String> restrictions) {
        this.restrictions = restrictions;
    }

    public void addRestriction(String restriction) {
        this.restrictions.add(restriction);
    }

    public void addProperty(String property) {
        this.properties.add(property);
    }
    
    public void addOptionalProperty(String optionalProperty) {
        this.optionalProperties.add(optionalProperty);
    }
    
    public LinkedHashMap<String, Map<String, String>> getFunctions() {
        return functions;
    }

    public void setFunctions(LinkedHashMap<String, Map<String, String>> functions) {
        this.functions = functions;
    }

    public Map<String, String> getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(Map<String, String> prefixes) {
        this.prefixes = prefixes;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void setMaxOffset(int maxoffset) {
        this.maxoffset = maxoffset;
    }
    
    public int getMaxOffset() {
        return maxoffset;
    }
    
    public void setMinOffset(int minoffset) {
        this.minoffset = minoffset;
    }
    
    public int getMinOffset() {
        return minoffset;
    }

    /**
     * @return String representation of knowledge base info
     */
    @Override
    public String toString() {
        String s = "ID: " + id + "\n";
        s = s + "Var: " + var + "\n";
        s = s + "Prefixes: " + prefixes + "\n";
        s = s + "Endpoint: " + endpoint + "\n";
        s = s + "Graph: " + graph + "\n";
        s = s + "Restrictions: " + restrictions + "\n";
        s = s + "Properties: " + properties + "\n";
        s = s + "OptionalProperties: " + optionalProperties + "\n";
        s = s + "Functions: " + functions + "\n";
        s = s + "Page size: " + pageSize + "\n";
        s = s + "Type: " + type + "\n";
        s = s + "MinOffset: " + minoffset + "\n";
        s = s + "MaxOffset: " + maxoffset + "\n";
        return s;
    }

    /**
     * Compute a hash code for the knowledge base encoded by this KBInfo. Allow
     * the hybrid cache to cache and retrieve the content of remote knowledge
     * bases on the hard drive for the user's convenience
     *
     * @return The hash code of this KBInfo
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((endpoint == null) ? 0 : endpoint.hashCode());
        result = prime * result + ((graph == null) ? 0 : graph.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + pageSize;
        result = prime * result
                + ((prefixes == null) ? 0 : prefixes.hashCode());
        result = prime * result
                + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result
                + ((restrictions == null) ? 0 : restrictions.hashCode());
        //result = prime * result + ((var == null) ? 0 : var.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + maxoffset;
        result = prime * result + minoffset;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        KBInfo other = (KBInfo) obj;
        if (endpoint == null) {
            if (other.endpoint != null) {
                return false;
            }
        } else if (!endpoint.equals(other.endpoint)) {
            return false;
        }
        if (graph == null) {
            if (other.graph != null) {
                return false;
            }
        } else if (!graph.equals(other.graph)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (pageSize != other.pageSize) {
            return false;
        }
        if(maxoffset != other.maxoffset) {
            return false;
        }
        if(minoffset != other.minoffset) {
            return false;
        }
        if (prefixes == null) {
            if (other.prefixes != null) {
                return false;
            }
        } else if (!prefixes.equals(other.prefixes)) {
            return false;
        }
        if (properties == null) {
            if (other.properties != null) {
                return false;
            }
        } else if (!properties.equals(other.properties)) {
            return false;
        }
        if (restrictions == null) {
            if (other.restrictions != null) {
                return false;
            }
        } else if (!restrictions.equals(other.restrictions)) {
            return false;
        }
        if (var == null) {
            if (other.var != null) {
                return false;
            }
        } else if (!var.equals(other.var)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the class contained in the restriction
     *
     * @return Class label
     */
    public String getClassOfendpoint() {
        for (String rest : restrictions) {
            if (rest.matches(".* rdf:type .*")) {
                String result = rest.substring(rest.indexOf("rdf:type") + 8).replaceAll("<", "").replaceAll(">", "").trim();
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the class contained in the restriction
     *
     * @param expanded is expanded
     * @return Class label
     */
    public String getClassOfendpoint(boolean expanded) {
        for (String rest : restrictions) {
            if (rest.matches(".* rdf:type .*")) {
                String result = rest.substring(rest.indexOf("rdf:type") + 8).replaceAll("<", "").replaceAll(">", "").trim();
                if (!expanded) return result;
                else {
                    String namespace = result.substring(0, result.indexOf(":"));
                    if (prefixes.containsKey(namespace))
                        return prefixes.get(namespace) + result.substring(result.indexOf(":") + 1);
                    else return result;
                }
            }
        }
        return null;
    }

    /**
     * Returns class URI if restriction to a rdf:type exists
     *
     * @return class restriction string
     */
    public String getClassRestriction() {
        String ret = null;
        for (String s : restrictions) {
            if (s.indexOf("rdf:type") > -1) {
                ret = s.substring(s.indexOf("rdf:type") + 8).trim();
            }
        }
        return ret;
    }

    public String getPrefix(String baseUri) {
        if (prefixes.containsValue(baseUri)) {
            for (Entry<String, String> e : prefixes.entrySet()) {
                if (e.getValue().equals(baseUri)) {
                    return e.getKey();
                }
            }
        }
        return null;
    }

    public void afterPropertiesSet() {
        List<String> copy = new ArrayList<String>(properties);
        properties.clear();

        for (String property : copy) {
            XMLConfigurationReader.processProperty(this, property);
        }
    }
}
