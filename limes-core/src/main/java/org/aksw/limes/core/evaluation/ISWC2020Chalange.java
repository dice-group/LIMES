package org.aksw.limes.core.evaluation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.reader.JsonMappingReader;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.SupervisedMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.aksw.limes.core.ml.algorithm.wombat.AWombat;
import org.apache.log4j.Logger;


public class ISWC2020Chalange {

	private static final Logger logger = Logger.getLogger(ISWC2020Chalange.class);
	private static final String sourceFile = "/home/abdullah/iswc2020/offers_corpus_english_v2.json";
	public static void main(String[] args) {

		long startTime ;
		long endTime ;
		long timeElapsed;
		List<AMapping> mappings=new ArrayList<AMapping>();

		logger.info(" Computers start .....");
		startTime=System.nanoTime();
		mappings=experimentComputers();
		System.out.println("mappings size...."+mappings.size());
		endTime = System.nanoTime();
		timeElapsed = endTime - startTime;
		System.out.println(" Computers execution time in milliseconds : " + timeElapsed / 1000000);

		logger.info(" Watches start .....");
		startTime=System.nanoTime();
		mappings=experimentWatches();
		System.out.println("mappings size...."+mappings.size());
		endTime = System.nanoTime();
		timeElapsed = endTime - startTime;
		System.out.println(" Watches execution time in milliseconds : " + timeElapsed / 1000000);

		logger.info(" Cameras start .....");
		startTime=System.nanoTime();
		mappings=experimentCameras();
		System.out.println("mappings size...."+mappings.size());
		endTime = System.nanoTime();
		timeElapsed = endTime - startTime;
		System.out.println(" Camerass execution time in milliseconds : " + timeElapsed / 1000000);

		logger.info(" Shoes start .....");
		startTime=System.nanoTime();
		mappings=experimentShoes();
		System.out.println("mappings size...."+mappings.size());
		endTime = System.nanoTime();
		timeElapsed = endTime - startTime;
		System.out.println(" Shoes execution time in milliseconds : " + timeElapsed / 1000000);

		logger.info(" All start .....");
		startTime=System.nanoTime();
		mappings=experimentAll();
		System.out.println("mappings size...."+mappings.size());
		endTime = System.nanoTime();
		timeElapsed = endTime - startTime;
		System.out.println(" All execution time in milliseconds : " + timeElapsed / 1000000);

	}


	public static List<AMapping> experimentComputers() {
		KBInfo sourceInfo = new KBInfo();
		sourceInfo.setEndpoint(sourceFile);
		sourceInfo.setVar("?x");
		sourceInfo.setPageSize(2000);
		sourceInfo.setId("sourceKbId");
		sourceInfo.addCatogery("Computers_and_Accessories");
		sourceInfo.addProperty("title");
		sourceInfo.addProperty("description");
		sourceInfo.addProperty("brand");
		sourceInfo.addProperty("price");
		sourceInfo.setType("json");

		ACache sc = HybridCache.getData(sourceInfo);

		List<AMapping> allMappings = new ArrayList<AMapping>();
		AMapping resultMap = MappingFactory.createDefaultMapping();
		List<String> traingData = new ArrayList<String>();

		String computerTrainSmall = "/home/abdullah/iswc2020/computers_train_small.json";
		traingData.add(computerTrainSmall);
		logger.info("data 1 added "+ computerTrainSmall);

		String computerTrainMedium = "/home/abdullah/iswc2020/computers_train_medium.json";
		traingData.add(computerTrainMedium);
		logger.info("data 2 added "+ computerTrainMedium);

		String computerTrainLarge = "/home/abdullah/iswc2020/computers_train_large.json";
		traingData.add(computerTrainLarge);
		logger.info("data 3 added "+ computerTrainLarge);

		String computerTrainXlarge = "/home/abdullah/iswc2020/computers_train_xlarge.json";
		traingData.add(computerTrainXlarge);
		logger.info("data 4 added "+ computerTrainXlarge);
		logger.info("training size... "+traingData.size());

		String computerGoldStandardData="/home/abdullah/iswc2020/computers_gs.json";

		for(int i = 0;i< traingData.size();i++) {
			logger.info("training data.... "+traingData.get(i));
			JsonMappingReader jsonMappingReaderTraining=new JsonMappingReader(traingData.get(i));
			AMapping trainingMaping=jsonMappingReaderTraining.read();
			System.out.println("training map size= "+trainingMaping.size());
			
			logger.info("gold standard adedd... "+computerGoldStandardData);
			JsonMappingReader jsonMappingReaderGoldStandard=new JsonMappingReader(computerGoldStandardData);
			AMapping goldStandardMaping=jsonMappingReaderGoldStandard.read();
			//logger.info("training map  "+trainingMaping);
			System.out.println("goldstandard map size= "+goldStandardMaping.size());
			SupervisedMLAlgorithm wombatSimple = null;
			try {
				wombatSimple = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.SUPERVISED_BATCH).asSupervised();
			} catch (UnsupportedMLImplementationException e) {
				e.printStackTrace();

			}

			//Set<String> measure = new HashSet<>(Arrays.asList("jaccard", "qgrams"));
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER, 3);
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATION_TIME_IN_MINUTES, 20);
			wombatSimple.setParameter(AWombat.PARAMETER_EXECUTION_TIME_IN_MINUTES, 300);
			//wombatSimple.setParameter(AWombat.PARAMETER_ATOMIC_MEASURES, measure);

			wombatSimple.init(null, sc, sc);
			MLResults mlModel = null;
			try {
				mlModel = wombatSimple.learn(trainingMaping);
			} catch (UnsupportedMLImplementationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			resultMap = wombatSimple.predict(sc, sc, mlModel);
			allMappings.add(resultMap);
			System.out.println("wombar mapping... "+resultMap.size());

			FMeasure fmeausre =new FMeasure();
			double f=fmeausre.calculate(resultMap, new GoldStandard(goldStandardMaping));
			double r=fmeausre.recall(resultMap, new GoldStandard(goldStandardMaping));
			double p=fmeausre.precision(resultMap, new GoldStandard(goldStandardMaping));
			//System.out.println(" Experiment Computers "+i);
			System.out.println(" Ex, LS, f , r, p");
			System.out.println("Ex. computer "+ i+", "+mlModel.getLinkSpecification().getFullExpression()+", "+f+" , "+r+" , "+p);
			//return resultMap;
		}
		return allMappings;
	}

	public static List<AMapping> experimentWatches() {

		KBInfo sourceInfo = new KBInfo();
		sourceInfo.setEndpoint(sourceFile);
		sourceInfo.setVar("?x");
		sourceInfo.setPageSize(2000);
		sourceInfo.setId("sourceKbId");
		sourceInfo.addCatogery("Jewelry");
		sourceInfo.addProperty("title");
		sourceInfo.addProperty("description");
		sourceInfo.addProperty("brand");
		sourceInfo.addProperty("price");
		sourceInfo.setType("json");

		ACache sc = HybridCache.getData(sourceInfo);

		List<AMapping> allMappings = new ArrayList<AMapping>();
		AMapping resultMap = MappingFactory.createDefaultMapping();
		List<String> traingData = new ArrayList<String>();

		String watchesTrainSmall = "/home/abdullah/iswc2020/watches_train_small.json";
		traingData.add(watchesTrainSmall);
		logger.info("data 1 added "+ watchesTrainSmall);

		String watchesTrainMedium = "/home/abdullah/iswc2020/watches_train_medium.json";
		traingData.add(watchesTrainMedium);
		logger.info("data 2 added "+ watchesTrainMedium);

		String watchesTrainLarge = "/home/abdullah/iswc2020/watches_train_large.json";
		traingData.add(watchesTrainLarge);
		logger.info("data 3 added "+ watchesTrainLarge);

		String watchesTrainXlarge = "/home/abdullah/iswc2020/watches_train_xlarge.json";
		traingData.add(watchesTrainXlarge);
		logger.info("data 4 added "+ watchesTrainXlarge);

		String watchesGoldStandatdData="/home/abdullah/iswc2020/watches_gs.json";

		for(int i = 0;i< traingData.size();i++) {
			logger.info("training data.... "+traingData.get(i));
			JsonMappingReader jsonMappingReaderTraining=new JsonMappingReader(traingData.get(i));
			AMapping trainingMaping=jsonMappingReaderTraining.read();
			System.out.println("training map size= "+trainingMaping.size());
			
			logger.info("gold standard adedd... "+watchesGoldStandatdData);
			JsonMappingReader jsonMappingReaderGoldStandard=new JsonMappingReader(watchesGoldStandatdData);
			AMapping goldStandardMaping=jsonMappingReaderGoldStandard.read();
			System.out.println("goldstandard map size= "+goldStandardMaping.size());
			SupervisedMLAlgorithm wombatSimple = null;
			try {
				wombatSimple = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.SUPERVISED_BATCH).asSupervised();
			} catch (UnsupportedMLImplementationException e) {
				e.printStackTrace();

			}

			//Set<String> measure = new HashSet<>(Arrays.asList("jaccard", "qgrams"));
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER, 3);
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATION_TIME_IN_MINUTES, 20);
			wombatSimple.setParameter(AWombat.PARAMETER_EXECUTION_TIME_IN_MINUTES, 300);
			//wombatSimple.setParameter(AWombat.PARAMETER_ATOMIC_MEASURES, measure);

			wombatSimple.init(null, sc, sc);
			MLResults mlModel = null;
			try {
				mlModel = wombatSimple.learn(trainingMaping);
			} catch (UnsupportedMLImplementationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



			resultMap = wombatSimple.predict(sc, sc, mlModel);
			allMappings.add(resultMap);
			System.out.println("wombar mapping... "+resultMap.size());

			FMeasure fmeausre =new FMeasure();
			double f=fmeausre.calculate(resultMap, new GoldStandard(goldStandardMaping));
			double r=fmeausre.recall(resultMap, new GoldStandard(goldStandardMaping));
			double p=fmeausre.precision(resultMap, new GoldStandard(goldStandardMaping));
			//System.out.println(" Experiment Watches "+i);
			System.out.println("Ex, LS, f , r, p");
			System.out.println("Ex. watches "+ i+", "+mlModel.getLinkSpecification().getFullExpression()+", "+f+" , "+r+" , "+p);
			//return resultMap;
		}
		return allMappings;
	}




	public static List<AMapping> experimentCameras() {
		KBInfo sourceInfo = new KBInfo();
		sourceInfo.setEndpoint(sourceFile);
		sourceInfo.setVar("?x");
		sourceInfo.setPageSize(2000);
		sourceInfo.setId("sourceKbId");
		sourceInfo.addCatogery("Camera_and_Photo");
		sourceInfo.addProperty("title");
		sourceInfo.addProperty("description");
		sourceInfo.addProperty("brand");
		sourceInfo.addProperty("price");
		sourceInfo.setType("json");
		ACache sc = HybridCache.getData(sourceInfo);

		List<AMapping> allMappings = new ArrayList<AMapping>();
		AMapping resultMap = MappingFactory.createDefaultMapping();
		List<String> traingData = new ArrayList<String>();

		String camerasTrainSmall = "/home/abdullah/iswc2020/cameras_train_small.json";
		traingData.add(camerasTrainSmall);
		logger.info("data 1 added "+ camerasTrainSmall);

		String camerasTrainMedium = "/home/abdullah/iswc2020/cameras_train_medium.json";
		traingData.add(camerasTrainMedium);
		logger.info("data 2 added "+ camerasTrainMedium);

		String camerasTrainLarge = "/home/abdullah/iswc2020/cameras_train_large.json";
		traingData.add(camerasTrainLarge);
		logger.info("data 3 added "+ camerasTrainLarge);

		String camerasTrainXlarge = "/home/abdullah/iswc2020/cameras_train_xlarge.json";
		traingData.add(camerasTrainXlarge);
		logger.info("data 4 added "+ camerasTrainXlarge);

		String cameraGoldStandardData="/home/abdullah/iswc2020/cameras_gs.json";

		for(int i = 0;i< traingData.size();i++) {
			logger.info("training data.... "+traingData.get(i));
			JsonMappingReader jsonMappingReaderTraining=new JsonMappingReader(traingData.get(i));
			AMapping trainingMaping=jsonMappingReaderTraining.read();
			System.out.println("training map size= "+trainingMaping.size());
			
			logger.info("gold standard adedd... "+cameraGoldStandardData);
			JsonMappingReader jsonMappingReaderGoldStandard=new JsonMappingReader(cameraGoldStandardData);
			AMapping goldStandardMaping=jsonMappingReaderGoldStandard.read();
			System.out.println("goldstandard map size= "+goldStandardMaping.size());
			SupervisedMLAlgorithm wombatSimple = null;
			try {
				wombatSimple = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.SUPERVISED_BATCH).asSupervised();
			} catch (UnsupportedMLImplementationException e) {
				e.printStackTrace();

			}

			//Set<String> measure = new HashSet<>(Arrays.asList("jaccard", "qgrams"));
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER, 3);
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATION_TIME_IN_MINUTES, 20);
			wombatSimple.setParameter(AWombat.PARAMETER_EXECUTION_TIME_IN_MINUTES, 300);
			//wombatSimple.setParameter(AWombat.PARAMETER_ATOMIC_MEASURES, measure);

			wombatSimple.init(null, sc, sc);
			MLResults mlModel = null;
			try {
				mlModel = wombatSimple.learn(trainingMaping);
			} catch (UnsupportedMLImplementationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultMap = wombatSimple.predict(sc, sc, mlModel);
			allMappings.add(resultMap);
			System.out.println("wombar mapping... "+resultMap.size());

			FMeasure fmeausre =new FMeasure();
			double f=fmeausre.calculate(resultMap, new GoldStandard(goldStandardMaping));
			double r=fmeausre.recall(resultMap, new GoldStandard(goldStandardMaping));
			double p=fmeausre.precision(resultMap, new GoldStandard(goldStandardMaping));
			//System.out.println(" Experiment Cameras "+i);
			System.out.println("Ex, LS, f , r, p");
			System.out.println("Ex. camera "+ i+", "+mlModel.getLinkSpecification().getFullExpression()+", "+f+" , "+r+" , "+p);
			//return resultMap;
		}
		return allMappings;
	}


	public static List<AMapping> experimentShoes() {

		KBInfo sourceInfo = new KBInfo();
		sourceInfo.setEndpoint(sourceFile);
		sourceInfo.setVar("?x");
		sourceInfo.setPageSize(2000);
		sourceInfo.setId("sourceKbId");
		sourceInfo.addCatogery("Shoes");
		sourceInfo.addProperty("title");
		sourceInfo.addProperty("description");
		sourceInfo.addProperty("brand");
		sourceInfo.addProperty("price");
		sourceInfo.setType("json");
		ACache sc = HybridCache.getData(sourceInfo);

		List<AMapping> allMappings = new ArrayList<AMapping>();
		AMapping resultMap = MappingFactory.createDefaultMapping();
		List<String> traingData = new ArrayList<String>();

		String shoesTrainSmall = "/home/abdullah/iswc2020/shoes_train_small.json";
		traingData.add(shoesTrainSmall);
		logger.info("data 1 added "+ shoesTrainSmall);

		String shoesTrainMedium = "/home/abdullah/iswc2020/shoes_train_medium.json";
		traingData.add(shoesTrainMedium);
		logger.info("data 2 added "+ shoesTrainMedium);

		String shoesTrainLarge = "/home/abdullah/iswc2020/shoes_train_large.json";
		traingData.add(shoesTrainLarge);
		logger.info("data 3 added "+ shoesTrainLarge);

		String shoesTrainXlarge = "/home/abdullah/iswc2020/shoes_train_xlarge.json";
		traingData.add(shoesTrainXlarge);
		logger.info("data 4 added "+ shoesTrainXlarge);

		String shoesGoldStandardData="/home/abdullah/iswc2020/shoes_gs.json";

		for(int i = 0;i< traingData.size();i++) {
			logger.info("training data.... "+traingData.get(i));
			JsonMappingReader jsonMappingReaderTraining=new JsonMappingReader(traingData.get(i));
			AMapping trainingMaping=jsonMappingReaderTraining.read();
			System.out.println("training map size= "+trainingMaping.size());
			
			logger.info("gold standard adedd... "+shoesGoldStandardData);
			JsonMappingReader jsonMappingReaderGoldStandard=new JsonMappingReader(shoesGoldStandardData);
			AMapping goldStandardMaping=jsonMappingReaderGoldStandard.read();
			System.out.println("goldstandard map size= "+goldStandardMaping.size());
			SupervisedMLAlgorithm wombatSimple = null;
			try {
				wombatSimple = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.SUPERVISED_BATCH).asSupervised();
			} catch (UnsupportedMLImplementationException e) {
				e.printStackTrace();

			}
			//Set<String> measure = new HashSet<>(Arrays.asList("jaccard", "qgrams"));
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER, 3);
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATION_TIME_IN_MINUTES, 20);
			wombatSimple.setParameter(AWombat.PARAMETER_EXECUTION_TIME_IN_MINUTES, 300);
			//wombatSimple.setParameter(AWombat.PARAMETER_ATOMIC_MEASURES, measure);

			wombatSimple.init(null, sc, sc);
			MLResults mlModel = null;
			try {
				mlModel = wombatSimple.learn(trainingMaping);
			} catch (UnsupportedMLImplementationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultMap = wombatSimple.predict(sc, sc, mlModel);
			allMappings.add(resultMap);
			System.out.println("wombar mapping... "+resultMap.size());

			FMeasure fmeausre =new FMeasure();
			double f=fmeausre.calculate(resultMap, new GoldStandard(goldStandardMaping));
			double r=fmeausre.recall(resultMap, new GoldStandard(goldStandardMaping));
			double p=fmeausre.precision(resultMap, new GoldStandard(goldStandardMaping));
			System.out.println("Ex, LS, f , r, p");
			System.out.println("Ex. shoes "+ i+", "+mlModel.getLinkSpecification().getFullExpression()+", "+f+" , "+r+" , "+p);
			//return resultMap;
		}
		return allMappings;
	}

	public static List<AMapping> experimentAll() {

		KBInfo sourceInfo = new KBInfo();
		sourceInfo.setEndpoint(sourceFile);
		sourceInfo.setVar("?x");
		sourceInfo.setPageSize(2000);
		sourceInfo.setId("sourceKbId");
		sourceInfo.addCatogery("all");
		sourceInfo.addProperty("title");
		sourceInfo.addProperty("description");
		sourceInfo.addProperty("brand");
		sourceInfo.addProperty("price");
		sourceInfo.setType("json");
		ACache sc = HybridCache.getData(sourceInfo);

		List<AMapping> allMappings = new ArrayList<AMapping>();
		AMapping resultMap = MappingFactory.createDefaultMapping();
		List<String> traingData = new ArrayList<String>();

		String allTrainSmall = "/home/abdullah/iswc2020/all_train_small.json";
		traingData.add(allTrainSmall);
		logger.info("data 1 added "+ allTrainSmall);

		String allTrainMedium = "/home/abdullah/iswc2020/all_train_medium.json";
		traingData.add(allTrainMedium);
		logger.info("data 2 added "+ allTrainMedium);

		String allTrainLarge = "/home/abdullah/iswc2020/all_train_large.json";
		traingData.add(allTrainLarge);
		logger.info("data 3 added "+ allTrainLarge);

		String allTrainXlarge = "/home/abdullah/iswc2020/all_train_xlarge.json";
		traingData.add(allTrainXlarge);
		logger.info("data 4 added "+ allTrainXlarge);

		String allGoldStandardData="/home/abdullah/iswc2020/all_gs.json";

		for(int i = 0;i< traingData.size();i++) {

			logger.info("training data.... "+traingData.get(i));
			JsonMappingReader jsonMappingReaderTraining=new JsonMappingReader(traingData.get(i));
			AMapping trainingMaping=jsonMappingReaderTraining.read();
			System.out.println("training map size= "+trainingMaping.size());
			
			logger.info("gold standard adedd... "+allGoldStandardData);
			JsonMappingReader jsonMappingReaderGoldStandard=new JsonMappingReader(allGoldStandardData);
			AMapping goldStandardMaping=jsonMappingReaderGoldStandard.read();
			System.out.println("goldstandard map size= "+goldStandardMaping.size());
			SupervisedMLAlgorithm wombatSimple = null;
			try {
				wombatSimple = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.SUPERVISED_BATCH).asSupervised();
			} catch (UnsupportedMLImplementationException e) {
				e.printStackTrace();

			}


			//Set<String> measure = new HashSet<>(Arrays.asList("jaccard", "qgrams"));
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER, 3);
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATION_TIME_IN_MINUTES, 20);
			wombatSimple.setParameter(AWombat.PARAMETER_EXECUTION_TIME_IN_MINUTES, 300);
			//wombatSimple.setParameter(AWombat.PARAMETER_ATOMIC_MEASURES, measure);
			wombatSimple.init(null, sc, sc);
			MLResults mlModel = null;
			try {
				mlModel = wombatSimple.learn(trainingMaping);
			} catch (UnsupportedMLImplementationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultMap = wombatSimple.predict(sc, sc, mlModel);
			allMappings.add(resultMap);
			System.out.println("wombar mapping... "+resultMap.size());

			FMeasure fmeausre =new FMeasure();
			double f=fmeausre.calculate(resultMap, new GoldStandard(goldStandardMaping));
			double r=fmeausre.recall(resultMap, new GoldStandard(goldStandardMaping));
			double p=fmeausre.precision(resultMap, new GoldStandard(goldStandardMaping));
			System.out.println("Ex, LS, f , r, p");
			System.out.println("Ex. all "+ i+", "+mlModel.getLinkSpecification().getFullExpression()+", "+f+" , "+r+" , "+p);
			//return resultMap;
		}
		return allMappings;
	}


}


