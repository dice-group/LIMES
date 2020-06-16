package org.aksw.limes.core.io.mapping.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.stream.Stream;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

public class JsonMappingReader extends AMappingReader {



	static Logger logger = LoggerFactory.getLogger(JsonMappingReader.class.getName());

	protected String delimiter;

	/**
	 * @param file
	 *            input file for reading
	 */
	public JsonMappingReader(String file) {
		super(file);
	}

	@Override
	public AMapping read() {
		AMapping mapping=MappingFactory.createDefaultMapping();

		try {
			BufferedReader reader;
			try{
				reader = new BufferedReader(new FileReader(new File(file)));
			}catch(Exception e){
				reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(file)));
			}


			Stream<String> strings=	reader.lines();
			Iterator<String> iterator = strings.iterator();
			while(iterator.hasNext()) {
				JSONObject obj = new JSONObject(iterator.next());
				//Iterator<String> keys = obj.keys();
				Object source=obj.get("id_left");
				Object target=obj.get("id_right");
				Object score=obj.get("label");
				//if(Double.valueOf(score.toString())>0) {
					mapping.add(source.toString(), target.toString(), Double.valueOf(score.toString()));
				//}

			}

			reader.close();
			logger.info("Retrieved " + mapping.size() + " statements");
		} catch (Exception e) {
			logger.error(MarkerFactory.getMarker("FATAL"),"Exception:" + e.getMessage());
			e.printStackTrace();
		}

		// TODO Auto-generated method stub

		return mapping;
	}
	
	
	public AMapping readP() {
		AMapping mapping=MappingFactory.createDefaultMapping();

		try {
			BufferedReader reader;
			try{
				reader = new BufferedReader(new FileReader(new File(file)));
			}catch(Exception e){
				reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(file)));
			}


			Stream<String> strings=	reader.lines();
			Iterator<String> iterator = strings.iterator();
			while(iterator.hasNext()) {
				JSONObject obj = new JSONObject(iterator.next());
				//Iterator<String> keys = obj.keys();
				Object source=obj.get("id_left");
				Object target=obj.get("id_right");
				Object score=obj.get("label");
				if(Double.valueOf(score.toString())>0) {
					mapping.add(source.toString(), target.toString(), Double.valueOf(score.toString()));
				}

			}

			reader.close();
			logger.info("Retrieved " + mapping.size() + " statements");
		} catch (Exception e) {
			logger.error(MarkerFactory.getMarker("FATAL"),"Exception:" + e.getMessage());
			e.printStackTrace();
		}

		// TODO Auto-generated method stub

		return mapping;
	}


}
