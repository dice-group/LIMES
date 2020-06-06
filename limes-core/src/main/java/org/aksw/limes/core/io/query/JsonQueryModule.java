package org.aksw.limes.core.io.query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.util.DataCleaner;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

public class JsonQueryModule implements IQueryModule {

	Logger logger = LoggerFactory.getLogger(JsonQueryModule.class.getName());
	KBInfo kb;
	public JsonQueryModule(KBInfo kbinfo) {
		kb = kbinfo;
	}

	@Override
	public void fillCache(ACache c) {

		try {
			// in case a JSON is use, endpoint is the file to read
			BufferedReader reader;
			try{
				reader = new BufferedReader(new FileReader(new File(kb.getEndpoint())));
			}catch(Exception e){
				reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(kb.getEndpoint())));
			}


			Stream<String> strings=	reader.lines();
			Iterator<String> iterator = strings.iterator();
			while(iterator.hasNext()) {
				JSONObject obj = new JSONObject(iterator.next());
				if(obj.get("category").toString().equals(kb.getCatogery().get(0))) {
					Object subject=obj.get("id");
					for(String propertyLabel: kb.getProperties()) {
						Object value= obj.get(propertyLabel);
						c.addTriple(subject.toString(), propertyLabel, value.toString());
					}
				}
				if(kb.getCatogery().get(0).equals("all")) {

					Object subject=obj.get("id");
					for(String propertyLabel: kb.getProperties()) {
						Object value= obj.get(propertyLabel);
						c.addTriple(subject.toString(), propertyLabel, value.toString());

					}
				}

			}

			reader.close();
			logger.info("Retrieved " + c.size() + " statements");
		} catch (Exception e) {
			logger.error(MarkerFactory.getMarker("FATAL"),"Exception:" + e.getMessage());
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
	}

	public AMapping jsonToMapping(String fileName) {
		AMapping mapping=MappingFactory.createDefaultMapping();

		try {
			// in case a JSON is use, endpoint is the file to read
			BufferedReader reader;
			try{
				reader = new BufferedReader(new FileReader(new File(fileName)));
			}catch(Exception e){
				reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(fileName)));
			}


			Stream<String> strings=	reader.lines();
			Iterator<String> iterator = strings.iterator();
			while(iterator.hasNext()) {
				JSONObject obj = new JSONObject(iterator.next());
				//Iterator<String> keys = obj.keys();
				Object source=obj.get("id_left");
				Object target=obj.get("id_right");
				Object score=obj.get("label");
				mapping.add(source.toString(), target.toString(), Double.valueOf(score.toString()));
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
