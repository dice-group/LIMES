/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.ml.algorithm.matching.stablematching;

import java.util.ArrayList;
import java.util.HashMap;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author ngonga
 */
public class HospitalResidents {

    static Logger logger = LoggerFactory.getLogger("LIMES");

    HashMap<Integer, String> residentReverseIndex = new HashMap<Integer, String>();
        HashMap<String, Integer> hospitalIndex = new HashMap<String, Integer>();
        HashMap<String, Integer> residentIndex = new HashMap<String, Integer>();
        HashMap<Integer, String> hospitalReverseIndex = new HashMap<Integer, String>();

    /** Implements hospital/residents for a similarity mapping. Can be used for
     * detecting stable matching between properties and classes
     * @param m Input Mapping
     * @return Stable matching including weights
     */
    public AMapping getMatching(AMapping m) {
        residentReverseIndex = new HashMap<Integer, String>();
        hospitalIndex = new HashMap<String, Integer>();
        residentIndex = new HashMap<String, Integer>();
        hospitalReverseIndex = new HashMap<Integer, String>();
        //index residents and hospitals
        int rCounter = 0;
        int hCounter = 0;
        for (String key : m.getMap().keySet()) {
            residentReverseIndex.put(rCounter, key);
            residentIndex.put(key, rCounter);
            rCounter++;
            for (String value : m.getMap().get(key).keySet()) {
                if (!hospitalIndex.containsKey(value)) {
                    hospitalIndex.put(value, hCounter);
                    hospitalReverseIndex.put(hCounter, value);
                    hCounter++;
                }
            }
        }
        //System.out.println(hospitalIndex);
        //now create resident and hospital list
        logger.debug(hCounter + " hospitals and " + rCounter + " residents");
        ArrayList<Resident> residents = new ArrayList<Resident>();
        ArrayList<Hospital> hospitals = new ArrayList<Hospital>();

        //create resident list
        for (int i = 0; i < rCounter; i++) {
            String r = residentReverseIndex.get(i);
            double preferences[] = new double[hCounter];
            //double sortedPreferences[] = new double[hCounter];
            //init
            for (int j = 0; j < hCounter; j++) {
                preferences[j] = 0;
                //sortedPreferences[j] = 0;
            }

            //create resident preference list
            int index;
            for (String h : m.getMap().get(r).keySet()) {
                index = hospitalIndex.get(h);
                //System.out.println(r+"\t"+h+"\t"+index+"\t"+m.getSimilarity(r, h));
                preferences[index] = m.getConfidence(r, h);
            }

            //System.out.print("Preferences===\n"+r+"\t");
            for (int j = 0; j < hCounter; j++) {
                //System.out.print(preferences[j]+"\t");
            }
            preferences = sort(preferences);
            //System.out.print("Sorted preferences===\n"+r+"\t");
            for (int j = 0; j < hCounter; j++) {
                //System.out.print(preferences[j]+"\t");
            }
            residents.add(new Resident(i, preferences));
            residents.get(i).label = r;
            //System.out.println(residents.get(i).label + " has preferences " + residents.get(i).hospitalToWeight);
        }

        //create hospital list
        for (int i = 0; i < hCounter; i++) {
            String h = hospitalReverseIndex.get(i);
            double preferences[] = new double[rCounter];
            //init
            for (int j = 0; j < rCounter; j++) {
                preferences[j] = 0;
            }
            //create resident preference list
            for (String r : m.getMap().keySet()) {
                int index = residentIndex.get(r);
                preferences[index] = m.getConfidence(r, h);
            }
            preferences = sort(preferences);
            if(rCounter%hCounter == 0)
            hospitals.add(new Hospital(i, (int)(rCounter / hCounter), preferences));
            else
                hospitals.add(new Hospital(i, (int)(rCounter / hCounter)+1, preferences));
            hospitals.get(i).label = h;
        }

        AMapping result = MappingFactory.createDefaultMapping();
        ArrayList<ArrayList<Integer>> match = getMatching(residents, hospitals);
        String uri1, uri2;
        for (int i = 0; i < match.size(); i++) {
            for (int j = 0; j < match.get(i).size(); j++) {
                uri1 = residentReverseIndex.get(match.get(i).get(j));
                uri2 = hospitalReverseIndex.get(i);
                result.add(uri1, uri2, m.getConfidence(uri1, uri2));
            }
        }
        return result;
    }

    public double[] sort(double[] input) {
        double[] result = new double[input.length];
        double max;
        int index;
        //logger.info(hospitalReverseIndex);
//        for (int k = 0; k < input.length; k++) {
//            logger.info("Input["+k+"] = "+input[k]);
//        }
        for (int k = 0; k < input.length; k++) {
            max = -2;
            index = 0;

            for (int jj = 0; jj < input.length; jj++) {
                if (input[jj] > max) {
                    max = input[jj];
                    index = jj;
                    //logger.info("max = "+max);
                    //logger.info("index = "+index);
                }
            }
            //logger.info("Hospital "+ hospitalReverseIndex.get(index)+" at position "+(input.length - k -1));
            result[input.length - k -1] = index;            
            input[index] = -1.0;
        }
        //System.exit(1);
        //System.out.println("");
        return result;
    }

    public ArrayList<ArrayList<Integer>> getMatching(ArrayList<Resident> r, ArrayList<Hospital> h) {
        ArrayList<Integer> unmatched = new ArrayList<Integer>();
        for (int i = 0; i < r.size(); i++) {
            unmatched.add(i);
        }
        //logger.info(hospitalReverseIndex);
        // implement hospital resident
        int rejected;
        while (unmatched.size() > 0) {
            // get current resident
            logger.debug("unmatched = "+unmatched);
            int residentID = unmatched.get(0);
            unmatched.remove(0);
            int bestHospital = r.get(residentID).getNextChoice();
            //logger.info(r.get(residentID).label + " picks "+hospitalReverseIndex.get(bestHospital));
            rejected = h.get(bestHospital).grantAdmission(residentID);
            if (rejected >= 0) {
                logger.debug("Unmatched = "+unmatched+"; Rejected = "+rejected);
                unmatched.add(rejected);
            }
        }

        //builds the final map
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < h.size(); i++) {
            result.add(h.get(i).acceptedResidents);
        }
        logger.debug("Results = "+result);
        return result;
    }

}
