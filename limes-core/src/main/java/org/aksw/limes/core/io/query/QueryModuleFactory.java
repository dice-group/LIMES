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


import org.aksw.limes.core.io.config.KBInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Nov 23, 2015
 */
public class QueryModuleFactory {

    static Logger logger = LoggerFactory.getLogger(QueryModuleFactory.class.getName());

    public static IQueryModule getQueryModule(String name, KBInfo kbinfo) {
        logger.info("Generating <" + name + "> reader");
        if (name.toLowerCase().startsWith("csv")) {
            return new CsvQueryModule(kbinfo);
        }
        //processes N3 files
        else if (name.toLowerCase().startsWith("n3") || name.toLowerCase().startsWith("nt")) {
            kbinfo.setType("N3");
            return new FileQueryModule(kbinfo);
        }
        //processes N-TRIPLE files
        else if (name.toLowerCase().startsWith("n-triple")) {
            kbinfo.setType("N-TRIPLE");
            return new FileQueryModule(kbinfo);
        }
        //process turtle files
        else if (name.toLowerCase().startsWith("turtle") || name.toLowerCase().startsWith("ttl")) {
            kbinfo.setType("TURTLE");
            return new FileQueryModule(kbinfo);
        }
        //process rdf/xml files        
        else if (name.toLowerCase().startsWith("rdf") || name.toLowerCase().startsWith("xml")) {
            kbinfo.setType("RDF/XML");
            return new FileQueryModule(kbinfo);
        } else if (name.toLowerCase().startsWith("sparql")) {
<<<<<<< HEAD
=======
            return new SparqlQueryModule(kbinfo);
        } else if (name.toLowerCase().startsWith("resilientsparql")) {
>>>>>>> feature/greaterThanStrSimilarity
        	return new ResilientSparqlQueryModule(kbinfo);
        } else if (name.toLowerCase().startsWith("vector")) {
            return new VectorQueryModule(kbinfo);
        }
        //default
        return new ResilientSparqlQueryModule(kbinfo);
    }
}
