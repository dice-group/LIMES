/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.atomic;

import algorithms.Correspondence;
import algorithms.ppjoinplus.PPJoinPlus;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.IMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 * DEPRECATED!!
 * Implements the PPJoin+ algorithm. No changes to original code. Please use
 * PPJoinPlusPlus instead. Includes measures and the like.
 * 
 * Not compatible anymore with library (EDJoinPlus.jar).
 * See commented code below.
 * 
 * @author ngonga
 */
@Deprecated
public class PPJoinMapper implements IMapper {

    static Logger logger = Logger.getLogger("LIMES");

    public String getName()
    {
        return "PPJoinMapper";
    }
    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression, double threshold) {

        logger.info("Starting PPJoinMapper");
        //System.out.println(source.getInstance(source.getAllUris().get(0)));
        //System.out.println(target.getInstance(target.getAllUris().get(0)));
        String property1 = null, property2 = null;
        //get property labels
        Parser p = new Parser(expression, threshold);

        //get first property label
        String term1 = "?"+p.getTerm1();
        String term2 = "?"+p.getTerm2();
        String split[];
        String var;
        
        if (term1.contains(".")) {
            split = term1.split("\\.");
            var = split[0];
            if (var.equals(sourceVar)) {
                property1 = split[1];
            } else {
                property2 = split[1];
            }
        } else {
            property1 = term1;
        }

        //get second property label
        if (term2.contains(".")) {
            split = term2.split("\\.");
            var = split[0];
            if (var.equals(sourceVar)) {
                property1 = split[1];
            } else {
                property2 = split[1];
            }
        } else {
            property2 = term2;
        }

        //if no properties then terminate
        if (property1 == null || property2 == null) {
            logger.fatal("Property values could not be read. Exiting");
            System.exit(1);
        }

        String info ="";
        info = info+"\nWill carry out mapping using the following parameter:\n";
        info = info+"Expression <"+expression+">\n";
        info = info+"Source property <"+property1+">\n";
        info = info+"Target property <"+property2+">\n";
        info = info+"Threshold <"+threshold+">\n";
        logger.info(info);
        
        if (!p.isAtomic()) {
            logger.fatal("Mappers can only deal with atomic expression");
            logger.fatal("Expression " + expression + " was given to a mapper to process");
            System.exit(1);
        }
        char metricName = expression.charAt(0);

        //3.1 fill objects from source in entry
        logger.info("Filling objects from source knowledge base.");
        HashMap<Integer, String> sourceMap = new HashMap<Integer, String>();
        ArrayList<String> uris = source.getAllUris();
        ArrayList<String> entries = new ArrayList<String>();
        Instance instance;
        int counter = 0, border = 0;
        for (int i = 0; i < uris.size(); i++) {
            instance = source.getInstance(uris.get(i));
            for (String s : instance.getProperty(property1)) {
                sourceMap.put(counter, uris.get(i));
                entries.add(s);
                counter++;
            }
        }

        //3.2 fill objects from target in entries
        logger.info("Filling objects from target knowledge base.");
        HashMap<Integer, String> targetMap = new HashMap<Integer, String>();
        border = counter - 1;
        uris = target.getAllUris();
        for (int i = 0; i < uris.size(); i++) {
            instance = target.getInstance(uris.get(i));
            for (String s : instance.getProperty(property2)) {
                targetMap.put(counter, uris.get(i));
                entries.add(s);
                counter++;
            }
        }

        String[] entryArray = new String[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            entryArray[i] = entries.get(i);
        }
        logger.info("Launching PPJoin++");
        LinkedList<Correspondence> result = PPJoinPlus.start(metricName, threshold, 2, entryArray);

        logger.info("Reorganizing ...");
        //transform result to mapping
        Mapping map = new MemoryMapping();

        Correspondence corr;
//        for (int i = 0; i < result.size(); i++) {
//            corr = result.get(i);
//            if ((corr.getFirstObject() <= border && corr.getSecondObject() > border)
//                    || (corr.getFirstObject() > border && corr.getSecondObject() <= border)) {
//                if (corr.getFirstObject() <= border) {
//                    map.add(sourceMap.get(corr.getFirstObject()), targetMap.get(corr.getSecondObject()), corr.getSimilarity());
//                } else {
//                    map.add(targetMap.get(corr.getFirstObject()), sourceMap.get(corr.getSecondObject()), corr.getSimilarity());
//                }
//            }
//        }
        logger.info("PPJoin++ complete.");
        return map;
    }

    public double getRuntimeApproximation(int sourceSize, int targetSize, double threshold, Language language) {
        if(language.equals(Language.DE))
        {
            //error = 667.22
            return 16.27 + 5.1*sourceSize + 4.9*targetSize - 23.44*threshold;
        }
        else
        {
            //error = 5.45
            return 26.03+ 0.005*(sourceSize + targetSize) - 49.5*threshold;            
        }
    }

    public double getMappingSizeApproximation(int sourceSize, int targetSize, double threshold, Language language) {
        if(language.equals(Language.DE))
        {
            //error = 667.22
            return 2333 + 0.14*sourceSize + 0.14*targetSize -3905*threshold;
        }
        else
        {
            //error = too high           
            return 0.003*(sourceSize + targetSize);
//            return -1.84 + 0.0006*sourceSize + 0.0006*targetSize;
        }
    }
    
    public double getSelectivity(int sourceSize, int targetSize, double threshold, Language language)
    {
        return getMappingSizeApproximation(sourceSize, targetSize, threshold, language)/(double)(sourceSize*targetSize);
    }
}
