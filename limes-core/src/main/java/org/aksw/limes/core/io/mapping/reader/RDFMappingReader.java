package org.aksw.limes.core.io.mapping.reader;

import org.aksw.limes.core.io.config.reader.rdf.RDFConfigurationReader;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 12, 2016
 */
public class RDFMappingReader extends AMappingReader {

    /**
     * @param file  input file for reading
     */
    public RDFMappingReader(String file){
        super(file);
    }
    /**
     * Reads mapping from RDF file (NT, N3, TTL, JASON-LD)
     *
     * @return Mapping that represents the content of the file
     */
    @Override
    public AMapping read() {
        AMapping mapping = MappingFactory.createDefaultMapping();
        Model mappingModel = RDFConfigurationReader.readModel(file);
        StmtIterator iter = mappingModel.listStatements();
        while (iter.hasNext()){
            Statement stmt = iter.nextStatement();
            mapping.add(stmt.getSubject().toString(),stmt.getObject().toString(),1d);
            mapping.setPredicate(stmt.getPredicate().toString());
        }
        return mapping;
    }

}
