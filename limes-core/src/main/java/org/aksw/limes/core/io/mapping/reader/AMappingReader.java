package org.aksw.limes.core.io.mapping.reader;

import org.aksw.limes.core.io.mapping.AMapping;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Nov 12, 2015
 */
public abstract class AMappingReader {
    
    protected String file;
    
    AMappingReader(String file){
        this.file = file;
    }
    public abstract AMapping read();
}
