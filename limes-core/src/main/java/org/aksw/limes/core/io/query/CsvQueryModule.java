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
import org.aksw.limes.core.util.DataCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;


/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Nov 23, 2015
 */
public class CsvQueryModule implements IQueryModule {
    Logger logger = LoggerFactory.getLogger(CsvQueryModule.class.getName());
    KBInfo kb;
    private String SEP = ",";

    public CsvQueryModule(KBInfo kbinfo) {
        kb = kbinfo;
    }

    public void setSeparation(String s) {
        SEP = s;
    }

    /**
     * Read a CSV file and write the content in a cache. The first line is the
     * name of the properties.
     *
     * @param c
     *         Cache in which the content is to be written
     */
    public void fillCache(ACache c) {
        try {
            // in case a CSV is use, endpoint is the file to read
            BufferedReader reader;
            try{
                reader = new BufferedReader(new FileReader(new File(kb.getEndpoint())));
            }catch(Exception e){
                reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(kb.getEndpoint())));
            }
            String s = reader.readLine();
            String split[];
            //first read name of properties. URI = first column
            if (s != null) {
                ArrayList<String> properties = new ArrayList<>();
                //split first line
                split = s.split(SEP);
                properties.addAll(Arrays.asList(split));

                s = reader.readLine();
                String id, value;
                while (s != null) {
                    //split = s.split(SEP);

                    split = DataCleaner.separate(s, SEP, properties.size());

                    id = split[0];
                    for (String propertyLabel : kb.getProperties()) {
                        value = split[properties.indexOf(propertyLabel)];
                        c.addTriple(id, propertyLabel, value);
                    }
                    s = reader.readLine();
                }
            } else {
                logger.warn("Input file " + kb.getEndpoint() + " was empty or faulty");
            }
            reader.close();
            logger.info("Retrieved " + c.size() + " statements");
        } catch (Exception e) {
            logger.error(MarkerFactory.getMarker("FATAL"),"Exception:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Read a CSV file and write the content in a cache. The first line is the
     * name of the properties.
     *
     * @param c
     *         Cache in which the content is to be written
     */
    public void fillAllInCache(ACache c) {
        Logger logger = LoggerFactory.getLogger("LIMES");
        String s = "";
        try {
            // in case a CSV is use, endpoint is the file to read
            BufferedReader reader = new BufferedReader(new FileReader(kb.getEndpoint()));
            s = reader.readLine();
            String split[];
            //first read name of properties. URI = first column
            if (s != null) {
                ArrayList<String> properties = new ArrayList<String>();
                //split first line
                split = s.split(SEP);
                properties.addAll(Arrays.asList(split));
                logger.info("Properties = " + properties);
                logger.info("KB Properties = " + kb.getProperties());
                //read remaining lines

                kb.setProperties(properties);
                s = reader.readLine();
                String id, value;
                while (s != null) {
                    split = s.split(SEP);
                    split = DataCleaner.separate(s, SEP, properties.size());
                    id = split[0].substring(1, split[0].length() - 1);
                    //logger.info(id);
                    for (String propertyLabel : kb.getProperties()) {
                        value = split[properties.indexOf(propertyLabel)];
                        c.addTriple(id, propertyLabel, value.replaceAll(Pattern.quote("@en"), ""));
                    }
                    s = reader.readLine();
                }
            } else {
                logger.warn("Input file " + kb.getEndpoint() + " was empty or faulty");
            }
            reader.close();
            logger.info("Retrieved " + c.size() + " statements");
        } catch (Exception e) {
            logger.error(MarkerFactory.getMarker("FATAL"),"Exception:" + e.getMessage());
            logger.warn(s);
            e.printStackTrace();
        }
    }

}