package org.aksw.limes.core.evaluation.evaluationDataLoader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.config.reader.rdf.RDFConfigurationReader;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Just a little helper method to get PropertyMappings of the evaluation datasets.
 *
 * @author Klaus Lyko
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class PropMapper {
    static Logger logger = LoggerFactory.getLogger(PropMapper.class);


    /**
     * We basically decide by the name of the config file.
     * @param configFile The configureation file path
     * @return PropertyMapping-The properties mapping between the two datasets
     */
    public static PropertyMapping getPropertyMapping(String configFile) {
        AConfigurationReader cR = new RDFConfigurationReader(configFile);
        cR.read();
        return getPropertyMapping(cR, configFile);

    }

    /**
     * 
     * @param cR An AConfigurationReader object that contains the configuration informmation
     * @param name The name of the property mapping file
     * @return PropertyMapping-The properties mapping between the two datasets
     */
    public static PropertyMapping getPropertyMapping(AConfigurationReader cR, String name) {

        PropertyMapping pM = new PropertyMapping();

        if (!name.substring(name.lastIndexOf("/") + 1).startsWith("dbpedia-linkedmdb")
                && !name.substring(name.lastIndexOf("/") + 1).startsWith("dailymed-drugbank")) {
            int max = Math.max(cR.getConfiguration().getSourceInfo().getProperties().size(), cR.getConfiguration().getTargetInfo().getProperties().size()) - 1;
            for (int i = 0; i < max; i++) {
                pM.addStringPropertyMatch(cR.getConfiguration().getSourceInfo().getProperties().get(i), cR.getConfiguration().getTargetInfo().getProperties().get(i));
            }
            pM.addDatePropertyMatch(cR.getConfiguration().getSourceInfo().getProperties().get(max), cR.getConfiguration().getTargetInfo().getProperties().get(max));
        } else {
            int max = Math.max(cR.getConfiguration().getSourceInfo().getProperties().size(), cR.getConfiguration().getTargetInfo().getProperties().size() - 1);
            for (int i = 0; i < max; i++) {
                pM.addStringPropertyMatch(cR.getConfiguration().getSourceInfo().getProperties().get(i), cR.getConfiguration().getTargetInfo().getProperties().get(i));
            }
        }
        System.out.println("PM: " + pM);
        return pM;
    }

    /**
     * Get propertyMapping from a file with the same name as the LIMES config file without ".xml" concateneted with
     * "propertymatch". Content should be lines <i>Source Property \t Target Property \t {"number", "string"}</i>.
     * E.g. "dbo:name	refs:label	string".
     *
     * @param folder   Path of the folder
     * @param name     Name of the LIMES config file.
     * @return PropertyMapping read from the file.
     */
    public static PropertyMapping getPropertyMappingFromFile(String folder, String name) {
        if (name.indexOf("/") > 0)
            name = name.substring(name.lastIndexOf("/"));
        String filename = name.substring(0, name.lastIndexOf("."));
        filename += "propertymatch";
        return getPropertyMappingFromFile(folder + filename);
    }
    
    public static PropertyMapping getPropertyMappingFromFile(String filePath){
        PropertyMapping pM = new PropertyMapping();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String s = reader.readLine();
            String split[];
            while (s != null && s.length() > 0) {
                split = s.split("\t");
                if (split[2].equalsIgnoreCase("number"))
                    pM.addNumberPropertyMatch(split[0], split[1]);
                if (split[2].equalsIgnoreCase("string"))
                    pM.addStringPropertyMatch(split[0], split[1]);
                if (split[2].equalsIgnoreCase("date"))
                    pM.addDatePropertyMatch(split[0], split[1]);
                if (split[2].equalsIgnoreCase("pointset"))
                    pM.addPointsetPropertyMatch(split[0], split[1]);
                s = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pM;
    }
}
