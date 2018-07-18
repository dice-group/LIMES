package org.aksw.limes.core.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.aksw.limes.core.io.mapping.reader.CSVMappingReader;

public class SafeReaderFromFile {

	public static BufferedReader getReader(String file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		} catch (FileNotFoundException e1) {
			try {
				InputStream input = CSVMappingReader.class.getResourceAsStream(file);
				if (input == null) {
					try {
						reader = new BufferedReader(new InputStreamReader(
								CSVMappingReader.class.getResourceAsStream(file.replace("src/main/resources", "")),
								"UTF8"));
					} catch (UnsupportedEncodingException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}
				} else {
					reader = new BufferedReader(new InputStreamReader(input, "UTF8"));
				}
			} catch (UnsupportedEncodingException e2) {
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reader;
	}
}
