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

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Enables one to register Jena Models as backends for the SparqlQueryModule
 *
 * @author Claus Stadler (cstadler@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 12, 2016
 */
public class ModelRegistry {
    static Logger logger = LoggerFactory.getLogger("LIMES");

    private static ModelRegistry instance = null;
    private Map<String, Model> map = new HashMap<String, Model>();

    public static ModelRegistry getInstance() {
        if (instance == null) {
            instance = new ModelRegistry();
        }
        return instance;
    }

    public static void register(String name, Model model) {
        getInstance().getMap().put(name, model);
        logger.info("Registry = " + getInstance().map.keySet());
    }

    public static void unregister(String name) {
        getInstance().getMap().remove(name);
    }

    public Map<String, Model> getMap() {
        return map;
    }
}
