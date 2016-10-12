package org.aksw.limes.core.io.config.reader;

import org.aksw.limes.core.io.config.Configuration;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 12, 2016
 */
public abstract class AConfigurationReader {

    protected String fileNameOrUri = new String();

    protected Configuration configuration = new Configuration();

    /**
      * @param fileNameOrUri file name or URI to be read
     */
    public AConfigurationReader(String fileNameOrUri) {
        this.fileNameOrUri = fileNameOrUri;
    }


    abstract public Configuration read();


    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
    
    /**
     * @param r resource 
     * @return non-prefixed version of the input resource r
     */
    protected String getURI(String r) {
        if(r.contains(":")){
            String prefix = r.substring(0,r.indexOf(":"));
            String prefixURI = configuration.getPrefixes().get(prefix);
            r = r.replace(prefix + ":", prefixURI);
        }
        return r;
    }

}
