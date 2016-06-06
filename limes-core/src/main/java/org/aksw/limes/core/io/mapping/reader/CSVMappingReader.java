package org.aksw.limes.core.io.mapping.reader;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Jun 3, 2016
 */
public class CSVMappingReader extends AMappingReader {
    private static final String COMMA = ",";

    static Logger logger = Logger.getLogger(CSVMappingReader.class.getName());

    protected String delimiter;

    public CSVMappingReader(String file) {
        super(file);
        this.delimiter = COMMA;
    }

    public CSVMappingReader(String file, String delimiter) {
        this(file);
        this.delimiter = delimiter;
    }

    /*
     * Read Mapping from the input CSV file
     * First column contains source URIs
     * Second column contains Target URIs
     * Third column contains similarity,
     * In case of only 2 columns, all similarities is set to 1
     */
    @Override
    public AMapping read() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            reader.close();
            String col[];
            if (line != null) {
                //split first line
                col = line.split(delimiter);
                if (col.length == 2) {
                    return readTwoColumnFile(file);
                } else if (col.length == 3) {
                    try {
                        Double.parseDouble(col[2]);
                        return readThreeColumnFileWithSimilarity(file);
                    } catch (NumberFormatException e) {
                        return readThreeColumnFile(file);
                    }
                } else {
                    logger.fatal("Format not supported");
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * Read Mapping from the input 2 column CSV file
     * First column contains source URIs
     * Second column contains Target URIs
     * All similarities is set to 1
     *
     * @param file
     * @param separator
     * @return
     */
    public AMapping readTwoColumnFile(String file) {
        AMapping m = MappingFactory.createDefaultMapping();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine();
            String split[];
            while (s != null) {
                //split first line
                split = s.split(delimiter);
                m.add(split[0].substring(1, split[0].length() - 1), split[1].substring(1, split[1].length() - 1), 1.0);
                s = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }


    /**
     * Read Mapping from the input 3 column CSV file
     * First column contains source URIs
     * Second column contains Target URIs
     * Third column contains similarity
     *
     * @param file
     * @return
     */
    public AMapping readThreeColumnFileWithSimilarity(String file) {
        AMapping m = MappingFactory.createDefaultMapping();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine();
            String split[];
            while (s != null) {
                //split first line
                split = s.split(delimiter);
                m.add(split[0].substring(1, split[0].length() - 1), split[1].substring(1, split[1].length() - 1), Double.parseDouble(split[2]));
                s = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    /**
     * Read Mapping from the input 3 column CSV file
     * First column contains source URIs
     * Second column contains linking property
     * Third column contains Target URIs
     *
     * @param file
     * @return
     */
    public AMapping readThreeColumnFile(String file) {
        AMapping m = MappingFactory.createDefaultMapping();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine();
            String split[];
            while (s != null) {
                //split first line
                split = s.split(delimiter);
                m.add(removeQuotes(split[0]), removeQuotes(split[2]), 1d);
                m.setPredicate(removeQuotes(split[1]));
                s = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    private String removeQuotes(String s) {
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
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

}
