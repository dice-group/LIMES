package org.aksw.limes.core.io.serializer;

import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Nov 25, 2015
 */
public class TabSeparatedSerializer extends NtSerializer {

    private static Logger logger = LoggerFactory.getLogger(TabSeparatedSerializer.class.getName());
    protected String seperator = "\t";

    /* (non-Javadoc)
     * @see org.aksw.limes.core.io.serializer.NtSerializer#addStatement(java.lang.String, java.lang.String, java.lang.String, double)
     */
    @Override
    public void addStatement(String subject, String predicate, String object, double similarity) {
        statements.add(subject + seperator + object + seperator + similarity);
    }

    /* (non-Javadoc)
     * @see org.aksw.limes.core.io.serializer.NtSerializer#printStatement(java.lang.String, java.lang.String, java.lang.String, double)
     */
    @Override
    public void printStatement(String subject, String predicate, String object, double similarity) {
        try {
            writer.println(subject + seperator + object + seperator + similarity);
        } catch (Exception e) {
            logger.warn("Error writing");
        }
    }

    /* (non-Javadoc)
     * @see org.aksw.limes.core.io.serializer.NtSerializer#getName()
     */
    public String getName() {
        return "TabSeparatedSerializer";
    }


    /**
     * Gets a mapping and serializes it to a file in the N3 format. The method
     * assume that the class already knows all the prefixes used in the uris and
     * expands those.
     *
     * @param m
     *         Mapping to serialize
     * @param predicate
     *         Predicate to use while serializing
     * @param file
     *         File in which the mapping is to be serialized
     */
    public void writeToFile(AMapping m, String predicate, String file) {
        open(file);

        if (m.size() > 0) {
            //first get the prefix used in the subjects
            //            String source = m.getMap().keySet().iterator().next();
            //            String target = m.getMap().get(source).keySet().iterator().next();
            for (String s : m.getMap().keySet()) {
                for (String t : m.getMap().get(s).keySet()) {
                    writer.println("<" + s + ">\t<" + t + ">\t" + m.getConfidence(s, t));
                }
            }
        }
        close();
    }

    /* (non-Javadoc)
     * @see org.aksw.limes.core.io.serializer.NtSerializer#getFileExtension()
     */
    public String getFileExtension() {
        return "tsv";
    }

    /**
     * @return the CSV file separator
     */
    public String getSeperator() {
        return seperator;
    }

    /**
     * @param seperator
     */
    public void setSeperator(String seperator) {
        this.seperator = seperator;
    }
}