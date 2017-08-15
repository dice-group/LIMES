/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.matching.stablematching;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
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
