package org.aksw.limes.core.io.mapping.reader;

import java.io.BufferedReader;
import java.io.FileReader;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;

/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 12, 2015
 */
public class RDFMappingReader implements IMappingReader{


	@Override
	public Mapping read(String file) {
		if(file.endsWith(".nt") || file.endsWith(".n3")){
			return readNtFile(file);
		}
		return null;
	}
	
    /**
     * Reads mapping from nt file
     *
     * @param file Input file for reading
     * @return Mapping that represents the content of the file
     */
    public static Mapping readNtFile(String file) {
        Mapping m = new MemoryMapping();
        try {
        	file = System.getProperty("user.dir") + "/" + file;
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

}
