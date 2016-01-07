package org.aksw.limes.core.io.serializer;

import java.util.HashMap;
import java.util.Map.Entry;
import java.io.*;
import java.util.*;

import org.aksw.limes.core.io.mapping.Mapping;
import org.apache.log4j.*;

/**
 * Implements a simple serializer that generates NTriple files.
 *
 * @author ngonga
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 25, 2015
 */
public class NtSerializer implements ISerializer {

	private static Logger logger = Logger.getLogger(NtSerializer.class.getName());
	protected PrintWriter writer;
    protected Set<String> statements;
    protected Map<String, String> prefixMap;
    protected File folder = new File("");

    public NtSerializer() {
        prefixMap = new HashMap<String, String>();
        statements = new TreeSet<String>();
    }

    public NtSerializer(HashMap<String, String> prefixes) {
        statements = new TreeSet<String>();
        prefixMap = prefixes;
    }

    public void addStatement(String subject, String predicate, String object, double similarity) {
        statements.add("<" + subject + "> " + predicate + " <" + object + "> .");
    }

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
     * Gets a mapping and serializes it to a file in the N3 format. The method
     * assume that the class already knows all the prefixes used in the uris and
     * expands those.
     *
     * @param m Mapping to serialize
     * @param predicate Predicate to use while serializing
     * @param file File in which the mapping is to be serialized
     */
    public void writeToFile(Mapping m, String predicate, String file) {
        open(file);
        String predicatePrefix = getPrefix(predicate);

        if (m.size() > 0) {
            //first get the prefix used in the subjects
//            String source = m.getMap().keySet().iterator().next();
//            String target = m.getMap().get(source).keySet().iterator().next();
            for (String s : m.getMap().keySet()) {
                for (String t : m.getMap().get(s).keySet()) {
                    writer.println("<" + s + "> "
                            + "<" + expand(predicate, predicatePrefix) + "> "
                            + "<" + t + "> .");
                }
            }
        }
        close();
    }

    /**
     * Expands a string by replacing a prefix by its full value
     *
     * @param s Input string
     * @param predicate Predicate to expand with
     * @return Expanded version of s
     */
    public static String expand(String s, String predicate) {
        if (predicate != null && s.indexOf(":") > 0) {
            String split[] = s.split(":");
            return predicate + split[1];
        } else {
            return s;
        }
    }

    /**
     * Writes in the file statement by statement. Rather slow, not to be used
     *
     * @param subject Source object of a mapping, subject of the triple to be
     * written
     * @param predicate Predicate to be written
     * @param object Target object of a mapping, object of the triple to be
     * written
     * @param similarity Similarity achieved by the subject and object
     */
    public void printStatement(String subject, String predicate, String object, double similarity) {
        String predicatePrefix = getPrefix(predicate);
        try {
            writer.println("<" + subject + "> "
                    + "<" + expand(predicate, predicatePrefix) + "> "
                    + "<" + object + "> .");
        } catch (Exception e) {
            logger.warn("Error writing");
        }
    }

    /**
     * Method to print prefixes: "@prefix key: url ."
     *
     * @param prefixMap
     * @param file
     */
    public void printPrefixes(Map<String, String> prefixMap, String file) {
        open(file);
        for (Entry<String, String> e : prefixMap.entrySet()) {
            String key = e.getKey();
            String url = e.getValue();
            if (!url.startsWith("<") && url.endsWith(">")) {
                url = "<" + e.getValue() + ">";
            }
            if (!key.endsWith(":")) {
                key += ":";
            }
            String out = "@prefix " + key + " " + url + " .";
            writer.println(out);
        }
        writer.flush();
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
        return "N3Serializer";
    }

    public void setPrefixes(Map<String, String> prefixes) {
        prefixMap = prefixes;
    }

    /**
     * Gets the prefix for a given string (i.e., source resource, predicate,
     * target resource) of a mapping
     */
    private String getPrefix(String entry) {
        for (String prefix : prefixMap.keySet()) {
            if (entry.startsWith(prefix + ":")) {
                return prefixMap.get(prefix);
            }
        }
        return null;
    }


    public String getFileExtension() {
        return "nt";
    }

    @Override
    public File getFile(String fileName) {
        return new File(folder.getAbsolutePath() + File.separatorChar + fileName);
    }

    @Override
    public void setFolderPath(File folder) {
        this.folder = folder;
    }
}
