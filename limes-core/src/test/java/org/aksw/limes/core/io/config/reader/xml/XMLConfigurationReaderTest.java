package org.aksw.limes.core.io.config.reader.xml;

import org.aksw.limes.core.io.config.Configuration;


public class XMLConfigurationReaderTest {
	
	static void testRead(){
		XMLConfigurationReader c = new XMLConfigurationReader();
		System.out.println(c.read("/resources/lgd-lgd.xml"));
	}
	
	public static void main(String[] args){
		testRead();
		
	}

}
