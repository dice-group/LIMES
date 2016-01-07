/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.io.serializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.aksw.limes.core.io.mapping.Mapping;
import org.apache.log4j.Logger;

/**
 *
 * @author ngonga
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 25, 2015
 */
public class TtlSerializer implements ISerializer {

    PrintWriter writer;
    Logger logger = Logger.getLogger(TtlSerializer.class.getName());
    TreeSet<String> statements; //List of statements to be printed
    Map<String, String> prefixList;
    File folder = new File("");

    /**
     * Constructor
     *
     */
    public TtlSerializer() {
        statements = new TreeSet<String>();
        prefixList = new HashMap<String, String>();
    }

    /**
     * Adds a statement to the list of statements to be printed
     *
     * @param subject Subject of the triple
     * @param predicate Predicate of the triple
     * @param object Object of the triple
     * @param similarity Similarity of subject and object
     */
    public void addStatement(String subject, String predicate, String object, double similarity) {
        statements.add("<" + subject + "> " + predicate + " <" + object + "> .");
    }

    /*
     * Flushes the printer
     *
     */
    public void flush() {
        try {
            for (String s : statements) {
                writer.println(s);
            }
            statements = new TreeSet<String>();
        } catch (Exception e) {
            logger.warn("Error writing");
        }
    }

    /**
     * Write the content of the mapping including the expansion of the prefixes
     * to a file
     *
     * @param prefixes List of prefixes
     * @param m Mapping to be written
     * @param file Output file
     */
    public void writeToFile(Mapping m, String predicate, String file) {
        open(file);
        printPrefixes();
        statements = new TreeSet<String>();
        for (String s : m.getMap().keySet()) {
            for (String t : m.getMap().get(s).keySet()) {
                writer.println("<" + s + "> " + predicate + " <" + t + "> .");
            }
        }
        close();
    }

    public void printPrefixes() {
        try {
            Iterator<String> iter = prefixList.keySet().iterator();
            String prefix;
            while (iter.hasNext()) {
                prefix = iter.next();
                writer.println("@prefix " + prefix + ": <" + prefixList.get(prefix) + "> .");
            }
        } catch (Exception e) {
            logger.warn("Error writing");
        }
    }

    public void printStatement(String subject, String predicate, String object, double similarity) {
        try {
            writer.println("<" + subject + "> " + predicate + " <" + object + "> .");
        } catch (Exception e) {
        	e.printStackTrace();	
        	System.err.println(e);
            logger.warn("Error writing");
        }
    }

    public boolean close() {
        try {
            if (statements.size() > 0) {
                for (String s : statements) {
                    writer.println(s);
                }
            }
            writer.close();
        } catch (Exception e) {
            logger.warn("Error closing PrintWriter");
            logger.warn(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean open(String file) {
        try {
            // if no parent folder is given, then take that of the config that was set by the controller
        	if (!file.contains("/") && !file.contains("\\")) {
                String filePath = folder.getAbsolutePath()+File.separatorChar+file;
                writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));
            } else {
                writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            }
        } catch (Exception e) {
            logger.warn("Error creating PrintWriter");
            logger.warn(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getName() {
        return "TtlSerializer";
    }

    public void setPrefixes(Map<String, String> prefixes) {
        prefixList = prefixes;
    }

    public String getFileExtension() {
        return "ttl";
    }

    @Override
    public File getFile(String fileName) {
        return new File(folder.getAbsolutePath() + File.separatorChar + fileName);
    }

    @Override
    public void setFolderPath(File f) {
        folder = f;
    }
}
