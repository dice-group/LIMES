/**
 * 
 */
package org.aksw.limes.core.evaluation.evaluationDataLoader;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import java.io.*;
import java.lang.management.MemoryType;

import org.apache.log4j.Logger;
/**
 * 	@author mofeed
 *  @author ngonga
 *
 */
public class Experiement {
    static Logger logger = Logger.getLogger("LIMES");
    static String SEPARATOR = "\t";
    static String CSVSEPARATOR = ",";

    public static Mapping readOAEIMapping(String file) {
        Mapping m = MappingFactory.createMapping(MappingFactory.MappingType.MEMORY_MAPPING);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            //read properties;
            String s = reader.readLine();
            String e1 = "", e2;
            while (s != null) {
                String[] split = s.split(" ");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }



    public static Cache readOAEIFile(String file, String token) {
        Cache c = new MemoryCache();
        BufferedReader reader=null;
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
        }
        finally{try {
            reader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }}
        //        logger.info(c);
        c.resetIterator();
        return c;
    }




}
