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
    


}
