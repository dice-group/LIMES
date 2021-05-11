/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
