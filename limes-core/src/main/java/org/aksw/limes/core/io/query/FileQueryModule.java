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
package org.aksw.limes.core.io.query;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.KBInfo;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFReaderI;
import org.apache.jena.riot.RiotNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class FileQueryModule implements IQueryModule {

    KBInfo kb;
    Model model;
    private Logger logger = LoggerFactory.getLogger(FileQueryModule.class.getName());

    /**
     * Constructor
     *
     * @param kbinfo
     *         Loads the endpoint as a file and if that fails as a resource.
     */
    @SuppressWarnings("resource")
    public FileQueryModule(KBInfo kbinfo) {
        try {

            kb = kbinfo;
            model = ModelFactory.createDefaultModel();
            System.out.println("Trying to get reader " + kb.getType());

            String __type = kb.getType() == null ? "" : kb.getType().trim().toLowerCase();
            boolean __isNQuads = __type.equals("nquads");

            if (__isNQuads) {
                final String onlyGraph = kb.getGraph();

                org.apache.jena.riot.system.StreamRDF sink = new org.apache.jena.riot.system.StreamRDFBase() {
                    @Override public void triple(org.apache.jena.graph.Triple t) {
                        model.getGraph().add(t);
                    }
                    @Override public void quad(org.apache.jena.sparql.core.Quad q) {
                        model.getGraph().add(q.asTriple());
                    }
                };

                try {
                    org.apache.jena.riot.RDFParser.create()
                        .source(kb.getEndpoint())
                        .lang(org.apache.jena.riot.Lang.NQUADS)
                        .parse(sink);
                } catch (org.apache.jena.riot.RiotNotFoundException e) {
                    InputStream in = getClass().getClassLoader().getResourceAsStream(kb.getEndpoint());
                    if (in == null) {
                        logger.error(MarkerFactory.getMarker("FATAL"),
                            "endpoint could not be loaded as a file or resource");
                        throw new RuntimeException(e);
                    }
                    org.apache.jena.riot.RDFParser.create()
                        .source(in)
                        .lang(org.apache.jena.riot.Lang.NQUADS)
                        .parse(sink);
                    in.close();
                }
            } else {

                try {
                    model.read(kb.getEndpoint(), kb.getType());
                } catch (RiotNotFoundException e) {
                    InputStream in = getClass().getClassLoader().getResourceAsStream(kb.getEndpoint());
                    if (in == null) {
                        logger.error(MarkerFactory.getMarker("FATAL"),"endpoint could not be loaded as a file or resource");
                        throw new RuntimeException(e);
                    }
                    RDFReaderI r = model.getReader(kb.getType());
                    InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
                    r.read(model, reader, null);
                    reader.close();
                    in.close();
                }
            }

            logger.info("RDF model read from " + kb.getEndpoint() + " is of size " + model.size());
            ModelRegistry.register(kb.getEndpoint(), model);


        } catch (Exception e) {
            logger.error(MarkerFactory.getMarker("FATAL"),"Error loading endpoint", e);
        }
    }

    /**
     * Reads data from a model in the model registry
     *
     * @param c
     *         Cache to be filled
     */
    public void fillCache(ACache c) {
        SparqlQueryModule sqm = new SparqlQueryModule(kb);
        sqm.fillCache(c, false);

    }

}
