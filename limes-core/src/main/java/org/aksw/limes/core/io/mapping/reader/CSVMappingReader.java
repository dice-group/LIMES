package org.aksw.limes.core.io.mapping.reader;

import java.io.BufferedReader;
import java.io.FileReader;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;

public class CSVMappingReader implements IMappingReader{
	
	public String SEPARATOR = "\t";

	@Override
	public Mapping read(String file) {
        return read(file, SEPARATOR);
    }
    
    public Mapping read(String file, String separator) {
        Mapping m = new MemoryMapping();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine();
            String split[];
            while (s != null) {
                //split first line
                split = s.split(separator);
                m.add(split[0].substring(1, split[0].length() - 1), split[1].substring(1, split[1].length() - 1), 1.0);
                s = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    public Mapping readThreeColumnFile(String file) {
        Mapping m = new MemoryMapping();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s = reader.readLine();
            String split[];
            while (s != null) {
                //split first line
                split = s.split(SEPARATOR);
                m.add(split[0].substring(1, split[0].length() - 1), split[1].substring(1, split[1].length() - 1), Double.parseDouble(split[2]));
                s = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }
    


}
