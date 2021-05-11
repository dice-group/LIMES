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
package org.aksw.limes.core.io.mapping.writer;

import org.aksw.limes.core.io.mapping.AMapping;
import org.apache.jena.rdf.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Nov 12, 2015
 */
public class RDFMappingWriter implements IMappingWriter {
    private static final Logger logger = LoggerFactory.getLogger(RDFMappingWriter.class.getName());

    public Model mappingModel = ModelFactory.createDefaultModel();

    public static void writeModel(Model model, String format, String outputFile) throws IOException {
        logger.info("Saving dataset to " + outputFile + " ...");
        long starTime = System.currentTimeMillis();
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        model.write(fileOutputStream, format);
        fileOutputStream.close();
        logger.info("Saving file done in " + (System.currentTimeMillis() - starTime) + "ms.");
    }


    @Override
    public void write(AMapping mapping, String outputFile) throws IOException {
        String format = outputFile.substring(outputFile.indexOf(".") + 1).trim().toLowerCase();
        switch (format) {
            case "n3":
            case "nt":
                write(mapping, outputFile, "N-TRIPLE");
                break;
            case "ttl":
                write(mapping, outputFile, "TTL");
                break;
            case "rdf":
                write(mapping, outputFile, "RDF/XML");
                break;
            case "jsonld":
                write(mapping, outputFile, "JSON-LD");
                break;
            default:
                logger.error("Serialization " + format + " is not yet implemented, exit with error!");
                throw new RuntimeException();
        }
    }

    /* (non-Javadoc)
     * @see org.aksw.limes.core.io.config.writer.IConfigurationWriter#write(org.aksw.limes.core.io.config.Configuration, java.lang.String, java.lang.String)
     */
    @Override
    public void write(AMapping mapping, String outputFile, String format) throws IOException {
        Property p = ResourceFactory.createProperty(mapping.getPredicate());
        for (String source : mapping.getMap().keySet()) {
            for (String target : mapping.getMap().get(source).keySet()) {
                Resource s = ResourceFactory.createResource(source);
                Resource o = ResourceFactory.createResource(target);
                mappingModel.add(s, p, o);
            }
        }
        writeModel(mappingModel, format, outputFile);
    }


}
