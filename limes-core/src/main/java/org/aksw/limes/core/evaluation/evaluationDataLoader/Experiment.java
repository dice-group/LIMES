/**
 *
 */
package org.aksw.limes.core.evaluation.evaluationDataLoader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class reads an OAEI from a file
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public class Experiment {
    static Logger logger = LoggerFactory.getLogger(Experiment.class);
    static String SEPARATOR = "\t";
    static String CSVSEPARATOR = ",";

    public static AMapping readOAEIMapping(String file) {
        AMapping m = MappingFactory.createDefaultMapping();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            //read properties;
            String s = reader.readLine();
            String e1 = "", e2;
            while (s != null) {
                //                String[] split = s.split(" ");
                {
                    if (s.contains("entity1")) {
                        e1 = s.substring(s.indexOf("=") + 2, s.lastIndexOf(">") - 2);
                    } else if (s.contains("entity2")) {
                        e2 = s.substring(s.indexOf("=") + 2, s.lastIndexOf(">") - 2);
                        m.add(e1, e2, 1.0);
                    }
                    s = reader.readLine();
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }


    public static ACache readOAEIFile(String file, String token) {
        ACache c = new MemoryCache();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            //read properties;
            String s = reader.readLine();
            while (s != null) {
                String[] split = s.split(" ");
                String value = split[2];
                if (split.length > 3) {
                    for (int i = 3; i < split.length; i++) {
                        value = value + " " + split[i];
                    }
                }
                if (split[0].contains(token) && !split[1].contains("#type")) {
                    c.addTriple(split[0].substring(1, split[0].length() - 1),
                            split[1].substring(1, split[1].length() - 1),
                            value.substring(1, value.length() - 3).toLowerCase());
                }
                s = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //        logger.info(c);
        c.resetIterator();
        return c;
    }

    public static ACache readOAEIFileFull(String file, String token) {
        HashMap<String,Instance> instMap = new HashMap<String,Instance>();
        ACache c = new MemoryCache();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader( Experiment.class.getClassLoader().getResourceAsStream(file)));
            //read properties;
            String s = reader.readLine();
            while (s != null) {
                String[] split = s.split(" ");
                String value = split[2];
                if (split.length > 3) {
                    for (int i = 3; i < split.length; i++) {
                        value = value + " " + split[i];
                    }
                }
                if (!split[1].contains("#type")) {
                	if(split[0].contains(token)){
                        c.addTriple(split[0].substring(1, split[0].length() - 1),
                                split[1].substring(1, split[1].length() - 1),
                                value.substring(1, value.length() - 3).toLowerCase());
                		if(value.substring(1, value.length() - 3).toLowerCase().contains("http")){
                			instMap.put(value.substring(1, value.length() - 3).toLowerCase(), new Instance(""));
                		}
                	}else{
                		Instance i = instMap.get(split[0].substring(1, split[0].length() - 1).toLowerCase());
                		if(i == null || i.getUri().equals("")){
                			i = new Instance(split[0].substring(1, split[0].length() - 1).toLowerCase());
                		}else{
                		}
                		i.addProperty(split[1].substring(1, split[1].length() - 1),
                                value.substring(1, value.length() - 3).toLowerCase());
                		instMap.put(i.getUri(),i);
                		if(value.substring(1, value.length() - 3).toLowerCase().contains("http")){
                			if(!instMap.containsKey(value.substring(1, value.length() - 3).toLowerCase())){
                				instMap.put(value.substring(1, value.length() - 3).toLowerCase(), new Instance(""));
                			}
                		}
                	}
                }
                s = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //        logger.info(c);
        c.resetIterator();
        return fixProperties(c, instMap);
    }
    
    private static ACache fixProperties(ACache c, HashMap<String,Instance> instMap){
    	ACache fixedCache = new MemoryCache();
    	Instance i = c.getNextInstance();
    	while(i != null){
    		Instance newI = new Instance(i.getUri());
    		for(String prop: i.getAllProperties()){
    			String propValue = i.getProperty(prop).first();
    			addProperties(newI, prop, propValue, instMap);
    			
    		}
    		fixedCache.addInstance(newI);
    		i = c.getNextInstance();
    	}
    	return fixedCache;
    }
    
    private static void addProperties(Instance newI, String prop, String propValue, HashMap<String,Instance> instMap){
		if(instMap.containsKey(propValue)){
			Instance subInst = instMap.get(propValue);
    		for(String subProp: subInst.getAllProperties()){
    			addProperties(newI, prop + "/" + subProp, instMap.get(propValue).getProperty(subProp).first(), instMap);
    		}
    		newI.addProperty(prop, propValue);
    	}else{
    		newI.addProperty(prop, propValue);
    	}
    }
}
