package org.aksw.limes.core.io.config.writer;

import java.io.File;
import java.io.IOException;

import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;


public class RDFConfigurationWriterTest {
	
	static void testRead() throws IOException{
		XMLConfigurationReader cr = new XMLConfigurationReader();
		RDFConfigurationWriter cw = new RDFConfigurationWriter();
		Configuration config = cr.read("/resources/lgd-lgd.xml");
		cw.write(config, new File("").getAbsolutePath() +"/resources/test.jsonld");
		
	}
	
	public static void main(String[] args) throws IOException{
		testRead();
		
	}

}
