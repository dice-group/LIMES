package org.aksw.limes.core.io.mapping.reader;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 12, 2015
 */
public class RDFMappingReader extends AMappingReader {


    public RDFMappingReader(String file){
        super(System.getProperty("user.dir") + "/" + file);
    }
    /**
     * Reads mapping from nt file
     *
     * @param file
     *         Input file for reading
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
                split = s.split(" ");
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
        if (file.endsWith(".nt") || file.endsWith(".n3")) {
            return readNtFile();
        }
        return null;
    }

}
