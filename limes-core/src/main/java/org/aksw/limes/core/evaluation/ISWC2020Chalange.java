package org.aksw.limes.core.evaluation;


import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.reader.JsonMappingReader;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.SupervisedMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.apache.log4j.Logger;


public class ISWC2020Chalange {

	private static final Logger logger = Logger.getLogger(ISWC2020Chalange.class);

	public static void main(String[] args) {
		String sourceFile = "/home/abdullah/iswc2020/offers_corpus_english_v2.json"; //=="one"
		String targetFile = "/home/abdullah/iswc2020/offers_corpus_english_v2.json"; //=="two"
		//String trainingFile= args[2];
		//String goldStandardFile= args[3];


		KBInfo sourceInfo = new KBInfo();
		KBInfo targetInfo = new KBInfo();
		sourceInfo.setEndpoint(sourceFile);
		sourceInfo.setVar("?x");
		sourceInfo.setPageSize(2000);
		sourceInfo.setId("sourceKbId");
		sourceInfo.addProperty("title");
		sourceInfo.addProperty("description");
		sourceInfo.addProperty("brand");
		sourceInfo.addProperty("price");
		sourceInfo.setType("json");

		targetInfo.setEndpoint(targetFile);
		targetInfo.setVar("?y");
		targetInfo.setPageSize(2000);
		targetInfo.setId("targetKbId");
		targetInfo.addProperty("title");
		targetInfo.addProperty("description");
		targetInfo.addProperty("brand");
		targetInfo.addProperty("price");
		//targetInfo.setEndpoint("target");
		targetInfo.setType("json");
		ACache sc = HybridCache.getData(sourceInfo);
		ACache tc = HybridCache.getData(targetInfo);

		List<String> categorys=new ArrayList<String>();
		categorys.add("computers");
		categorys.add("cameras");
		categorys.add("watches");
		categorys.add("shoes");
		categorys.add("all");

		for(String category:categorys) {
			long startTime = System.nanoTime();
			List<AMapping> mappings=experiment(sc,tc, category);
			System.out.println("mappings size...."+mappings.size());
			long endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			System.out.println(category+" Execution time in milliseconds : " + timeElapsed / 1000000);


		}


	}

	public static List<AMapping> experiment(ACache sc  , ACache tc, String category) {

		List<AMapping> allMappings = new ArrayList<AMapping>();
		AMapping resultMap = MappingFactory.createDefaultMapping();
		List<String> traingDataSize = new ArrayList<String>();

		String string = "/home/abdullah/iswc2020/"+category+"_train_small.json";
		logger.info("data 1 added "+ string);
		traingDataSize.add(string);
		String string2 = "/home/abdullah/iswc2020/"+category+"_train_medium.json";
		logger.info("data 2 added "+ string2);
		traingDataSize.add(string2);
		String string3 = "/home/abdullah/iswc2020/"+category+"_train_large.json";
		logger.info("data 3 added "+ string3);
		traingDataSize.add(string3);
		String string4 = "/home/abdullah/iswc2020/"+category+"_train_xlarge.json";
		logger.info("data 4 added "+ string4);
		traingDataSize.add(string4);

		logger.info(category+" added");

		for(int i = 0;i> traingDataSize.size();i++) {
			logger.info("training data.... "+traingDataSize.get(i));
			JsonMappingReader jsonMappingReaderTraining=new JsonMappingReader(traingDataSize.get(i));
			String string5="/home/abdullah/iswc2020/"+category+"_gs.json";
			logger.info("gold standard adedd... "+string5);
			JsonMappingReader jsonMappingReaderGoldStandard=new JsonMappingReader(string5);
			AMapping trainingMaping=jsonMappingReaderTraining.read();
			AMapping goldStandardMaping=jsonMappingReaderGoldStandard.read();

			logger.info("training map size= "+trainingMaping.size());
			logger.info("training map  "+trainingMaping);
			logger.info("goldstandard map size= "+trainingMaping.size());
			SupervisedMLAlgorithm wombatSimple = null;
			try {
				wombatSimple = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.SUPERVISED_BATCH).asSupervised();
			} catch (UnsupportedMLImplementationException e) {
				e.printStackTrace();

			}

			wombatSimple.init(null, sc, tc);
			MLResults mlModel = null;
			try {
				mlModel = wombatSimple.learn(trainingMaping);
			} catch (UnsupportedMLImplementationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultMap = wombatSimple.predict(sc, tc, mlModel);
			allMappings.add(resultMap);
			logger.info("wombar mapping... "+resultMap.size());

			FMeasure fmeausre =new FMeasure();
			double f=fmeausre.calculate(resultMap, new GoldStandard(goldStandardMaping));
			double r=fmeausre.recall(resultMap, new GoldStandard(goldStandardMaping));
			double p=fmeausre.precision(resultMap, new GoldStandard(goldStandardMaping));
			System.out.println(category+" Experiment "+i);
			System.out.println("f , r, p");
			System.out.println(f+" , "+r+" , "+p);
			//return resultMap;
		}
		return allMappings;
	}
}


