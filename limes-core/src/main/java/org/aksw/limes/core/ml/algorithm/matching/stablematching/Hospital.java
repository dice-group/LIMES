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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ngonga
 */
public class Hospital {
    Logger logger = LoggerFactory.getLogger("LIMES");

    HashMap<Integer, Double> residentToWeight;
    int currentChoice;
    int capacity;
    List<Double> sortedPreferences;
    ArrayList<Integer> acceptedResidents;
    int ID;
    public String label;

    public Hospital(int idValue, int capacityValue, double[] preferences) {
        ID = idValue;
        residentToWeight = new HashMap<Integer, Double>();
        sortedPreferences = new ArrayList<Double>();
        for (int i = 0; i < preferences.length; i++) {
            residentToWeight.put(i, preferences[i]);
            sortedPreferences.add(preferences[i]);
        }
        capacity = capacityValue;
        Collections.sort(sortedPreferences);
        acceptedResidents = new ArrayList<Integer>();
        logger.debug("Preferences of hospital "+ID+" with capacity "+capacity+" is "+residentToWeight);
    }

    /** Processes an application. If queue full, then get student out that has the
     * smallest weight
     * @param resident ID of resident who apply
     * @return ID of resident that was kicked out or -1 in case none was kicked out
     */
    public int grantAdmission(int resident) {
        //System.out.println("Capacity of "+ID+" is "+capacity);
        if(acceptedResidents.size()<capacity) {
            acceptedResidents.add(resident);
            logger.debug("Admission granted by "+ID+" to "+resident);
            logger.debug("Accepted residents are now "+acceptedResidents.size()+"/"+capacity);
            logger.debug(ID+"->"+acceptedResidents);
            return -1;
        } else {
            double min = residentToWeight.get(resident);
            int index = -1;
            for(int i=0; i<acceptedResidents.size(); i++) {
                if(residentToWeight.get(acceptedResidents.get(i)) < min) {
                    min = residentToWeight.get(resident);
                    index = i;
                }
            }
            if(index == -1) {
                logger.debug("Rejection of "+resident+ " by "+ID);
                logger.debug(ID+"->"+acceptedResidents);
                return resident;
            } else {
                logger.debug("Admission granted by "+ID+" to "+resident);
                int reject = acceptedResidents.get(index);
                acceptedResidents.set(index, resident);
                logger.debug("Rejection of "+index+ " by "+ID);
                logger.debug(ID+"->"+acceptedResidents);
                return reject;
            }
        }

    }
}
