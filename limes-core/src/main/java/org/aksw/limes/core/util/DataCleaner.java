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
package org.aksw.limes.core.util;

import org.aksw.limes.core.io.config.KBInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Nov 23, 2015
 */
public class DataCleaner {
    static Logger logger = LoggerFactory.getLogger(DataCleaner.class);

    KBInfo kbInfo;

    public static String[] separate(String line, String SEP, int NumberOfProperties){
        String[] result = new String[NumberOfProperties];
        String[] split = line.split(SEP);
        if (split.length == NumberOfProperties) {
            for (int i = 0; i < split.length; i++) {
                split[i] = removeQuotes(split[i]);
            }
            return split;
        } else {
            // brute force: read each character if it equals SEP and is NOT between a open " make a split
            for (int i = 0; i < NumberOfProperties; i++) {
                int sepOc = line.indexOf(SEP);
                int openApostrophe = line.indexOf("\"");
                if (openApostrophe == -1 && sepOc == -1) {
                    result[i] = removeQuotes(line);
                    return result;
                }
                if (sepOc == -1) {
                    result[i] = removeQuotes(line);
                    return result;
                }
                if (sepOc < openApostrophe) {
                    result[i] = removeQuotes(line.substring(0, sepOc));
                    line = line.substring(sepOc + 1);
                } else if (openApostrophe == -1) {
                    result[i] = line.substring(0, sepOc);
                    line = line.substring(sepOc + 1);
                } else {
                    // we found a SEP within an apostrophe
                    int closingApostrophe = line.indexOf("\"", openApostrophe + 1);
                    int nextSep = line.indexOf(SEP, closingApostrophe);
                    if (openApostrophe < nextSep && openApostrophe > -1) {
                        result[i] = removeQuotes(line.substring(openApostrophe, nextSep));
                    } else
                        result[i] = "";
                    line = line.substring(nextSep + 1);
                }
            }

        }
        return result;
    }


    public static String removeQuotes(String s) {
        int openApostrophe = s.indexOf("\"");
        if (openApostrophe == 0)
            s = s.substring(1);
        if (s.endsWith("\""))
            s = s.substring(0, s.length() - 1);
        return s;
    }

}
