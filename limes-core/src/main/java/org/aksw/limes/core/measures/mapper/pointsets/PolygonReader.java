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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.pointsets;

import org.aksw.limes.core.datastrutures.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class PolygonReader {
    private static final Logger logger = LoggerFactory.getLogger(PolygonReader.class);

    public static boolean keepPolygons = true;

    public static Set<Polygon> readPolygons(String file) {
        return readPolygons(file, -1);
    }

    /**
     * Read a number of polygons from a file.
     *
     * @param file,
     *            name of the file that contains the polygons
     * @param numberOfEntries,
     *            number of polygons to be read
     * @return polygon, as a set
     */
    public static Set<Polygon> readPolygons(String file, int numberOfEntries) {
        long startTime = System.currentTimeMillis();
        Map<String, Polygon> result = new HashMap<String, Polygon>();
        String s, split[];
        try {
            BufferedReader buf = new BufferedReader(new FileReader(file));
            s = buf.readLine();
            while (s != null) {
                while (s.contains("  ")) {
                    s = s.replaceAll(Pattern.quote("  "), " ");
                }
                s = s.replaceAll(Pattern.quote(" "), "\t");
                split = s.split("\t");
                if (split.length % 2 != 1) {
                    System.err.println("Error: " + split.length + " => " + s);
                } else {
                    if (!result.containsKey(split[0])) {
                        result.put(split[0], new Polygon(split[0]));
                    }
                    // data is stored as long, lat
                    for (int i = 1; i < split.length; i = i + 2) {
                        result.get(split[0]).add(new Point("", Arrays.asList(
                                new Double[] { Double.parseDouble(split[i + 1]), Double.parseDouble(split[i]) })));
                    }
                }
                if (result.keySet().size() == numberOfEntries) {
                    break;
                }
                s = buf.readLine();
                buf.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Set<Polygon> r = new HashSet<Polygon>();
        if (keepPolygons) {
            for (Polygon p : result.values()) {
                r.add(p);
            }
            logger.info("Read " + r.size() + " polygons done in " + (System.currentTimeMillis() - startTime) + "ms.");
            return r;
        } else {
            logger.info(
                    "Read " + result.size() + " polygons done in " + (System.currentTimeMillis() - startTime) + "ms.");
            return new HashSet<Polygon>(result.values());
        }
    }

}
