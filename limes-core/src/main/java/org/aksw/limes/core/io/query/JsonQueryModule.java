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
import org.aksw.limes.core.io.cache.HybridCache;
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
	
	public JsonQueryModule() {
		
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

	public ACache fillLeftCache(KBInfo kbinfo) {
		ACache	scLeft= new HybridCache();
		try {
			// in case a JSON is use, endpoint is the file to read
			BufferedReader reader;
			try{
				reader = new BufferedReader(new FileReader(new File(kbinfo.getEndpoint())));
			}catch(Exception e){
				reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(kbinfo.getEndpoint())));
			}

			Stream<String> strings=	reader.lines();
			Iterator<String> iterator = strings.iterator();
			while(iterator.hasNext()) {
				JSONObject obj = new JSONObject(iterator.next());
				if(obj.get("category_left").toString().equals(kbinfo.getCatogery().get(0))) {
					Object subject=obj.get("id_left");
					for(String propertyLabel: kbinfo.getProperties()) {
						if(propertyLabel.equals("title_left")) {
							Object value= obj.get(propertyLabel);
							scLeft.addTriple(subject.toString(), "title", value.toString());
						}
						if(propertyLabel.equals("description_left")) {
							Object value= obj.get(propertyLabel);
							scLeft.addTriple(subject.toString(), "description", value.toString());
						}
						if(propertyLabel.equals("brand_left")) {
							Object value= obj.get(propertyLabel);
							scLeft.addTriple(subject.toString(), "brand", value.toString());
						}
						if(propertyLabel.equals("specTableContent_left")) {
							Object value= obj.get(propertyLabel);
							scLeft.addTriple(subject.toString(), "specTableContent", value.toString());
						}


					}
				}
				if(kbinfo.getCatogery().get(0).equals("all")) {

					Object subject=obj.get("id_left");
					for(String propertyLabel: kbinfo.getProperties()) {
						if(propertyLabel.equals("title_left")) {
							Object value= obj.get(propertyLabel);
							scLeft.addTriple(subject.toString(), "title", value.toString());
						}
						if(propertyLabel.equals("description_left")) {
							Object value= obj.get(propertyLabel);
							scLeft.addTriple(subject.toString(), "description", value.toString());
						}
						if(propertyLabel.equals("brand_left")) {
							Object value= obj.get(propertyLabel);
							scLeft.addTriple(subject.toString(), "brand", value.toString());
						}
						if(propertyLabel.equals("specTableContent_left")) {
							Object value= obj.get(propertyLabel);
							scLeft.addTriple(subject.toString(), "specTableContent", value.toString());
						}
					}
				}

			}

			reader.close();
			logger.info("Retrieved " + scLeft.size() + " statements");
		} catch (Exception e) {
			logger.error(MarkerFactory.getMarker("FATAL"),"Exception:" + e.getMessage());
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return scLeft;
	}
	
	public ACache fillRightCache(KBInfo kbInfo) {
		ACache	scRight= new HybridCache();
		try {
			// in case a JSON is use, endpoint is the file to read
			BufferedReader reader;
			try{
				reader = new BufferedReader(new FileReader(new File(kbInfo.getEndpoint())));
			}catch(Exception e){
				reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(kbInfo.getEndpoint())));
			}

			Stream<String> strings=	reader.lines();
			Iterator<String> iterator = strings.iterator();
			while(iterator.hasNext()) {
				JSONObject obj = new JSONObject(iterator.next());
				if(obj.get("category_right").toString().equals(kbInfo.getCatogery().get(0))) {
					Object subject=obj.get("id_right");
					for(String propertyLabel: kbInfo.getProperties()) {
						if(propertyLabel.equals("title_right")) {
							Object value= obj.get(propertyLabel);
							scRight.addTriple(subject.toString(), "title", value.toString());
						}
						if(propertyLabel.equals("description_right")) {
							Object value= obj.get(propertyLabel);
							scRight.addTriple(subject.toString(), "description", value.toString());
						}
						if(propertyLabel.equals("brand_right")) {
							Object value= obj.get(propertyLabel);
							scRight.addTriple(subject.toString(), "brand", value.toString());
						}
						if(propertyLabel.equals("specTableContent_right")) {
							Object value= obj.get(propertyLabel);
							scRight.addTriple(subject.toString(), "specTableContent", value.toString());
						}


					}
				}
				if(kbInfo.getCatogery().get(0).equals("all")) {

					Object subject=obj.get("id_right");
					for(String propertyLabel: kbInfo.getProperties()) {
						if(propertyLabel.equals("title_right")) {
							Object value= obj.get(propertyLabel);
							scRight.addTriple(subject.toString(), "title", value.toString());
						}
						if(propertyLabel.equals("description_right")) {
							Object value= obj.get(propertyLabel);
							scRight.addTriple(subject.toString(), "description", value.toString());
						}
						if(propertyLabel.equals("brand_right")) {
							Object value= obj.get(propertyLabel);
							scRight.addTriple(subject.toString(), "brand", value.toString());
						}
						if(propertyLabel.equals("specTableContent_right")) {
							Object value= obj.get(propertyLabel);
							scRight.addTriple(subject.toString(), "specTableContent", value.toString());
						}
					}
				}

			}

			reader.close();
			logger.info("Retrieved " + scRight.size() + " statements");
		} catch (Exception e) {
			logger.error(MarkerFactory.getMarker("FATAL"),"Exception:" + e.getMessage());
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return scRight;
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
