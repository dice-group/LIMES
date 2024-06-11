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
package org.aksw.limes.core.io.config.reader;

import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;

import java.util.*;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 12, 2016
 */
public abstract class AConfigurationReader {

    protected String fileNameOrUri = new String();

    protected Configuration configuration = new Configuration();

    /**
     * @param fileNameOrUri file name or URI to be read
     */
    public AConfigurationReader(String fileNameOrUri) {
        this.fileNameOrUri = fileNameOrUri;
    }


    abstract public Configuration read();


    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * This method replaces any URIs used in the kbInfo with their prefixes
     * @param info
     */
    public static void replaceURIsWithPrefixes(KBInfo info) {
        Map<String, String> prefixes = info.getPrefixes();
        HashMap<String, String> rev = new HashMap<>();
        for(Map.Entry<String, String> entry : prefixes.entrySet()) {
            rev.put(entry.getValue(), entry.getKey());
        }
        info.setProperties(replaceURIsWithPrefixes(info.getProperties(), rev));
        info.setOptionalProperties(replaceURIsWithPrefixes(info.getOptionalProperties(), rev));
        info.setRestrictions(replaceURIsWithPrefixes(info.getRestrictions(), rev));
        info.setFunctions(replaceURIsWithPrefixes(info.getFunctions(), rev));
    }

    private static ArrayList<String> replaceURIsWithPrefixes(Collection<String> props, HashMap<String, String> rev) {
        ArrayList<String> replacements = new ArrayList<>();
        for (String property : props) {
            String originalProp = property;
            for (Map.Entry<String, String> prefixEntry : rev.entrySet()) {
                if(property.contains(prefixEntry.getKey())){
                    property = property.replace(prefixEntry.getKey(), prefixEntry.getValue() + ":");
                }
            }
            replacements.add(property);

            if(property.contains("://")){
                throw new IllegalArgumentException("LIMES does not support using URIs in the properties, optionalProperties, restrictions, or functions in the configuration file. " +
                        "Please define a prefix and use the prefix for the following URI: " + originalProp);
            }
        }
        return replacements;
    }

    private static LinkedHashMap<String, Map<String, String>> replaceURIsWithPrefixes(Map<String, Map<String, String>> funcs, HashMap<String, String> rev) {
        LinkedHashMap<String, Map<String, String>> replacements = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : funcs.entrySet()) {
            String property = entry.getKey();
            String originalProp = property;

            //Replace key of function
            for (Map.Entry<String, String> prefixEntry : rev.entrySet()) {
                if(property.contains(prefixEntry.getKey())){
                    property = property.replace(prefixEntry.getKey(), prefixEntry.getValue() + ":");
                }
            }
            if(property.contains("://")){
                throw new IllegalArgumentException("LIMES does not support using namespace IRIs in the properties, optionalProperties, restrictions, or functions in the configuration file. " +
                        "Please define a prefix and use the prefix for the namespace of the following IRI: " + originalProp);
            }

            //Replace value map
            Map<String, String> intermediateReplacement = new HashMap<>();
            for (Map.Entry<String, String> stringEntry : entry.getValue().entrySet()) {
                String subKey = stringEntry.getKey();
                String origSubKey = subKey;
                for (Map.Entry<String, String> prefixEntry : rev.entrySet()) {
                    subKey = subKey.replace(prefixEntry.getKey(), prefixEntry.getValue() + ":");
                }
                intermediateReplacement.put(subKey, stringEntry.getValue());
                if(subKey.contains("://")){
                    throw new IllegalArgumentException("LIMES does not support using namespace IRIs in the properties, optionalProperties, restrictions, or functions in the configuration file. " +
                            "Please define a prefix and use the prefix for the namespace of the following IRI: " + origSubKey);
                }

            }

            replacements.put(property, intermediateReplacement);
        }
        return replacements;
    }


}
