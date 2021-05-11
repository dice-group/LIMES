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
package org.aksw.limes.core.ml.algorithm.matching.stablematching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * @author ngonga
 */
public class Resident {

    Logger logger = LoggerFactory.getLogger("LIMES");
    HashMap<Integer, Double> hospitalToWeight;
    int currentChoice;
    List<Double> sortedPreferences;
    int ID;
    public String label="";

    public Resident(int idValue, double[] preferences) {
        ID = idValue;

        hospitalToWeight = new HashMap<Integer, Double>();
        //sortedPreferences = new ArrayList<Double>();
        for (int i = 0; i < preferences.length; i++) {
            hospitalToWeight.put(i, preferences[i]);
            logger.debug("Adding "+preferences[i]+" at position "+i);
            //sortedPreferences.add(preferences[i]);
        }
        logger.debug("**** H2Weight = "+hospitalToWeight);
        currentChoice = hospitalToWeight.size();
        //Collections.sort(sortedPreferences);
        //System.exit(1);
        //System.out.println("Preferences of resident "+ID+" with label "+label+" are "+hospitalToWeight);
    }

    public int getNextChoice() {
        currentChoice--;
        logger.debug("Current choice  = " +currentChoice);
        return hospitalToWeight.get(currentChoice).intValue();
    }
}
