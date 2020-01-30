package org.aksw.limes.core.io.mapping.reader;

import java.io.BufferedReader;
import java.io.FileReader;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.util.DataCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

/**
 * Read AMapping from CSV file by default the CSV file delimiter is the comma,
 * need to be set for other delimiters (including tab)
 *
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Aug 12, 2016
 */
public class CSVMappingReader extends AMappingReader {
    private static final String COMMA = ",";

    static Logger logger = LoggerFactory.getLogger(CSVMappingReader.class.getName());

    protected String delimiter;

    /**
     * @param file
     *            input file for reading
     */
    public CSVMappingReader(String file) {
        super(file);
        this.delimiter = COMMA;
    }

    /**
     * @param file
     *            input file for reading
     * @param delimiter
     *            of the file
     */
    public CSVMappingReader(String file, String delimiter) {
        this(file);
        this.delimiter = delimiter;
    }

    /*
     * Read Mapping from the input CSV file First column contains source URIs
     * Second column contains Target URIs Third column contains similarity, In
     * case of only 2 columns, all similarities is set to 1
     */
    @Override
    public AMapping read() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            reader.close();
            String col[];
            if (line != null) {
                // split first line
                col = line.split(delimiter);
                if (col.length == 2) {
                    return readTwoColumnFile();
                } else if (col.length == 3) {
                    try {
                        Double.parseDouble(col[2]);
                        return readThreeColumnFileWithSimilarity();
                    } catch (NumberFormatException e) {
                        return readThreeColumnFile();
                    }
                } else {
                    logger.error(MarkerFactory.getMarker("FATAL"), "Format not supported");
                    throw new RuntimeException();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * Read Mapping from the input 2 column CSV file First column contains
     * source URIs Second column contains Target URIs All similarities is set to 1.0
     *
     * @return AMapping object contains the mapping
     */
    public AMapping readTwoColumnFile() {
        AMapping m = MappingFactory.createDefaultMapping();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            String split[];
            while (line != null) {
                // split first line
                // split = line.split(delimiter);
                split = DataCleaner.separate(line, delimiter, 2);
                // check if it's the line with the properties
                if (!split[0].startsWith("id")) {
                    //if (!split[0].startsWith("<")) {
                        m.add(split[0], split[1], 1.0);
                    //} else {
                    //    m.add(split[0].substring(1, split[0].length() - 1), split[1].substring(1, split[1].length() - 1), 1.0);
                    //}
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    /**
     * Read Mapping from the input 3 column CSV file First column contains
     * source URIs Second column contains Target URIs Third column contains
     * similarity
     *
     * @return AMapping object contains the mapping
     */
    public AMapping readThreeColumnFileWithSimilarity() {
        AMapping m = MappingFactory.createDefaultMapping();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine();
            String split[];
            while (s != null) {
                // split first line
                split = s.split(delimiter);
                split[0] = removeBrackets(removeQuotes(split[0]));
                split[1] = removeBrackets(removeQuotes(split[1]));
                m.add(split[0], split[1], Double.parseDouble(split[2]));
                s = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    /**
     * Read Mapping from the input 3 column CSV file First column contains
     * source URIs Second column contains linking property Third column contains
     * Target URIs
     *
     * @return AMapping object contains the mapping
     */
    public AMapping readThreeColumnFile() {
        AMapping m = MappingFactory.createDefaultMapping();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine();
            String split[];
            while (s != null) {
                // split first line
                split = s.split(delimiter);
                m.add(removeQuotes(split[0]), removeQuotes(split[2]), 1d);
                if (split[1].startsWith("\"<") && split[1].endsWith(">\"")) {
                    String tmp = removeQuotes(split[1]);
                    m.setPredicate(tmp.substring(1, tmp.length() - 1));
                } else {
                    m.setPredicate(removeQuotes(split[1]));
                }
                s = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    private String removeBrackets(String s) {
        s = s.trim();
        if ((s.charAt(0) == '<' && s.charAt(s.length() - 1) == '>')) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    private String removeQuotes(String s) {
        s = s.trim();
        if ((s.charAt(0) == '\"' && s.charAt(s.length() - 1) == '\"') || (s.charAt(0) == '\'' && s.charAt(s.length() - 1) == '\'')) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    /**
     * @return the delimiter used for reading the mapping
     */
    public String getSDelimiter() {
        return delimiter;
    }

    /**
     * set the delimiter used for reading the mapping from csv file
     *
     * @param delimiter
     *            to be set
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

}
