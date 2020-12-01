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

import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 12, 2016
 */
public class TTLSerializer implements ISerializer {

    PrintWriter writer;
    Logger logger = LoggerFactory.getLogger(TTLSerializer.class.getName());
    TreeSet<String> statements; //List of statements to be printed
    Map<String, String> prefixList;
    File folder = new File("");

    /**
     * Constructor
     */
    public TTLSerializer() {
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
        statements.add("<" + subject + "> <" + predicate + "> <" + object + "> .");
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
     * @param mapping Mapping to be written
     * @param predicate mapping predicate used to connect subjects and objects
     * @param file Output file
     */
    public void writeToFile(AMapping mapping, String predicate, String file) {
        open(file);
        printPrefixes();
        statements = new TreeSet<String>();
        for (String s : mapping.getMap().keySet()) {
            for (String t : mapping.getMap().get(s).keySet()) {
                writer.println("<" + s + "> <" + predicate + "> <" + t + "> .");
            }
        }
        close();
    }

    /**
     * Print prefixes
     */
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

    /* (non-Javadoc)
     * @see org.aksw.limes.core.io.serializer.ISerializer#printStatement(java.lang.String, java.lang.String, java.lang.String, double)
     */
    public void printStatement(String subject, String predicate, String object, double similarity) {
        try {
            writer.println("<" + subject + "> <" + predicate + "> <" + object + "> .");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
            logger.warn("Error writing");
        }
    }

    /* (non-Javadoc)
     * @see org.aksw.limes.core.io.serializer.ISerializer#close()
     */
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

    /* (non-Javadoc)
     * @see org.aksw.limes.core.io.serializer.ISerializer#open(java.lang.String)
     */
    public boolean open(String file) {
        try {
            // if no parent folder is given, then take that of the config that was set by the controller
            if (!file.contains("/") && !file.contains("\\")) {
                String filePath = folder.getAbsolutePath() + File.separatorChar + file;
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

    /* (non-Javadoc)
     * @see org.aksw.limes.core.io.serializer.ISerializer#setPrefixes(java.util.Map)
     */
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
