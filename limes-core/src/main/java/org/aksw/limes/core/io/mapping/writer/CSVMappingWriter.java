package org.aksw.limes.core.io.mapping.writer;

import java.io.IOException;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.serializer.CSVSerializer;


/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Nov 12, 2015
 */
public class CSVMappingWriter implements IMappingWriter {


    /* 
     * Serialize the input mapping a file in the CSV format with the default comma separator  
     * 
     * (non-Javadoc)
     * @see org.aksw.limes.core.io.mapping.writer.IMappingWriter#write(org.aksw.limes.core.io.mapping.AMapping, java.lang.String)
     */
    @Override
    public void write(AMapping mapping, String outputFile) throws IOException {
        write(mapping, outputFile, ",");
    }

    
    /* 
     * Serialize the input mapping a file in the CSV format with the input separator  
     * 
     * (non-Javadoc)
     * @see org.aksw.limes.core.io.mapping.writer.IMappingWriter#write(org.aksw.limes.core.io.mapping.AMapping, java.lang.String, java.lang.String)
     */
    @Override
    public void write(AMapping mapping, String outputFile, String separator)
            throws IOException {
        CSVSerializer csvSerializer = new CSVSerializer();
        csvSerializer.setSeparator(separator);
        csvSerializer.writeToFile(mapping, "", outputFile);
    }




}
