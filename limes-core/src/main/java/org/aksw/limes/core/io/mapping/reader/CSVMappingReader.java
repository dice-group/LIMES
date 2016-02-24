package org.aksw.limes.core.io.mapping.reader;

import java.io.BufferedReader;
import java.io.FileReader;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.apache.log4j.Logger;

/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 12, 2015
 */
public class CSVMappingReader implements IMappingReader{
	static Logger logger = Logger.getLogger(CSVMappingReader.class.getName());

	protected String delimiter;

	public CSVMappingReader(){
		this.delimiter = ",";
	}
	
	public CSVMappingReader(String delimiter){
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
	public Mapping read(String file) {
		try {
			file = System.getProperty("user.dir") + "/" + file;
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			reader.close();
			String col[];
			if(line != null) {
				//split first line
				col = line.split(delimiter);
				if(col.length == 2){
					return readTwoColumnFile(file);
				} else if(col.length == 3){
					try
					{
					  Double.parseDouble(col[2]);
					  return readThreeColumnFileWithSimilarity(file);
					}
					catch(NumberFormatException e)
					{
					  return readThreeColumnFile(file);
					}
				} else {
					logger.error("Format not supported");
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
	public Mapping readTwoColumnFile(String file) {
		Mapping m = new MemoryMapping();
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
	public Mapping readThreeColumnFileWithSimilarity(String file) {
		Mapping m = new MemoryMapping();
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
	public Mapping readThreeColumnFile(String file) {
		Mapping m = new MemoryMapping();
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
	
	private String removeQuotes(String s){
		if((s.charAt(0) == '\"' && s.charAt(s.length()-1) == '\"') || (s.charAt(0) == '\'' && s.charAt(s.length()-1) == '\'')){
			return s.substring(1, s.length()-1);
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
	 * @param delimiter
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

}
