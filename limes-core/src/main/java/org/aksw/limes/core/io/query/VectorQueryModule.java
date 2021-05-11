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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * This class reads vectors contained in a text file so as to enable LIMES to
 * compute the similarity of the entities described by these vectors efficiently.
 * The file format is assumed to be URI\tVector. The vector itself is read by the
 * metrics of the metric factory for maximum flexibility.
 *
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Nov 23, 2015
 */
public class VectorQueryModule implements IQueryModule {

    KBInfo kb;
    String SEP = "\t";

    /**
     * Constructor
     *
     * @param kbinfo
     *         Contains the knowledge base infos necessary to read the
     *         input stream
     */
    public VectorQueryModule(KBInfo kbinfo) {
        kb = kbinfo;
    }

    /**
     * Fills the cache c with the data contained in the data source described by
     * kb
     *
     * @param c
     *         The cache to be filled
     */
    public void fillCache(ACache c) {
        Logger logger = LoggerFactory.getLogger("LIMES");
        try {

            // in case a CSV is used, endpoint is the file to read
            BufferedReader reader = new BufferedReader(new FileReader(kb.getEndpoint()));
            logger.info("Reading vectors from " + kb.getEndpoint());
            String s = reader.readLine();
            String uri;

            //read properties. Vectors are assumed to have only one property,
            //which is that used by the user to in the description of the similarity
            //to be used. In general, we assume that vectors can only be compared
            //with other vectors.

            ArrayList<String> properties = new ArrayList<String>();
            properties.add(kb.getProperties().get(0));
            while (s != null) {
                if (s.contains(SEP)) {
                    uri = s.substring(1, s.indexOf(SEP) - 1);
                    c.addTriple(uri, properties.get(0), s.substring(s.indexOf(SEP)));
                }
                s = reader.readLine();
            }
            reader.close();
            logger.info("Retrieved " + c.size() + " statements");
        } catch (Exception e) {
            logger.error(MarkerFactory.getMarker("FATAL"),"Exception:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
