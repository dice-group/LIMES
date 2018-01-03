package org.aksw.limes.core.gui.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectPropertiesGetter {
    protected Logger logger = LoggerFactory.getLogger(ProjectPropertiesGetter.class);
	
	public static String getProperty(String projectPropertiesPath, String propertyKey){
		Properties props = new Properties();
    	try {
			props.load(new FileInputStream(projectPropertiesPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
        return props.get(propertyKey).toString();
		
	}
}
