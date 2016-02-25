package org.aksw.limes.core.io.serializer;


import java.io.File;
import java.util.Map;

import org.aksw.limes.core.io.mapping.Mapping;

/**
 * Interface for serializers
 *
 * @author ngonga
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 25, 2015
 */
public interface ISerializer {

    /**
     * Writes the whole results of a mapping to a file
     *
     * @param prefixes Set of prefixes used
     * @param m Mapping computed by an organizer
     * @param file Output file, where the results are to be written
     */
    public void writeToFile(Mapping m, String predicate, String file);

    /**
     * Sets the prefixes to be used in the file.
     *
     * @param prefixes List of prefixes to use
     */
    public void setPrefixes(Map<String, String> prefixes);

    /**
     * Prints a triple in a file. Requires the method open to have been carried
     * out
     *
     * @param subject The subject of the triple
     * @param predicate The predicate of the triple
     * @param object The object of the triple
     */
    public void printStatement(String subject, String predicate, String object, double similarity);

    /**
     * Adds a triple to the buffer of the serializer. Requires the method open
     * to have been carried out
     *
     * @param subject The subject of the triple
     * @param predicate The predicate of the triple
     * @param object The object of the triple
     */
    public void addStatement(String subject, String predicate, String object, double similarity);

    /**
     * Closes the output file
     *
     * @return true if the file was closed successfully, else false
     */
    public boolean close();

    /**
     * Opens the output file
     *
     * @param file Path to the file in which the output is to be written
     * @return true if opening was carried out successfully, else false.
     */
    public boolean open(String file);

    /**
     * @return name
     */
    public String getName();

    /**
     * @return file extension
     */
    public String getFileExtension();

    /**
     * Method to open the file with the specific name
     *
     * @param fileName
     * @return
     */
    public File getFile(String fileName);

    /**
     * Method to set the folder where Serializations should be saved to.
     *
     * @param folder File which points to the folder to serialize within.
     */
    public void setFolderPath(File folder);
}
