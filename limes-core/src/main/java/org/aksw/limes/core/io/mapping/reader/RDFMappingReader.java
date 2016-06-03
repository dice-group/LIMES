package org.aksw.limes.core.io.mapping.reader;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Jun 3, 2016
 */
public class RDFMappingReader extends AMappingReader {


    private static final String N3 = ".n3";
    private static final String NT = ".nt";
    private static final String SPACE = " ";

    public RDFMappingReader(String file){
        super(file);
    }
    /**
     * Reads mapping from nt file
     *
     * @param file  input file for reading
     * @return Mapping that represents the content of the file
     */
    public AMapping readNtFile() {
        AMapping m = MappingFactory.createDefaultMapping();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine();
            String split[];
            while (s != null) {
                //split first line
                split = s.split(SPACE);
                m.add(split[0].substring(1, split[0].length() - 1), split[2].substring(1, split[2].length() - 1), 1.0);
                s = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    @Override
    public AMapping read() {
        if (file.endsWith(NT) || file.endsWith(N3)) {
            return readNtFile();
        }
        return null;
    }

}
