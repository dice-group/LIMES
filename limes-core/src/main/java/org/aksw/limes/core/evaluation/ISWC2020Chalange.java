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
import org.aksw.limes.core.io.query.JsonQueryModule;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.SupervisedMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.aksw.limes.core.ml.algorithm.dragon.Dragon;
import org.aksw.limes.core.ml.algorithm.wombat.AWombat;
import org.apache.log4j.Logger;


public class ISWC2020Chalange {

	private static final Logger logger = Logger.getLogger(ISWC2020Chalange.class);
	private static final String sourceFile = "/home/abdullah/iswc2020/offers_corpus_english_v2.json";
	private static final String testFile="/home/abdullah/iswc2020/LIMES/limes-core/resources/testset_1500.json";
	public static void main(String[] args) {

		long startTime ;
		long endTime ;
		long timeElapsed;
		List<AMapping> mappings=new ArrayList<AMapping>();

		logger.info(" WOMBAT start .....");
		logger.info(" Cameras start .....");
		startTime=System.nanoTime();
		mappings=experimentCameras();
		System.out.println("mappings size...."+mappings.size());
		endTime = System.nanoTime();
		timeElapsed = endTime - startTime;
		System.out.println(" Camerass execution time in milliseconds : " + timeElapsed / 1000000);
		
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
		///home/abdullah/dataset/
		String computerGoldStandardData="/home/abdullah/iswc2020/computers_gs.json";
	//	String computerGoldStandardData="/home/abdullah/dataset/ISWC2020/computers_gs.json";


		KBInfo sourceInfoLeft = new KBInfo();
		sourceInfoLeft.setEndpoint(computerGoldStandardData);
		sourceInfoLeft.setVar("?x");
		sourceInfoLeft.setPageSize(2000);
		sourceInfoLeft.setId("sourceKbId");
		sourceInfoLeft.addCatogery("Computers_and_Accessories");
		sourceInfoLeft.addProperty("title_left");
		//sourceInfoLeft.addProperty("description_left");
		//sourceInfoLeft.addProperty("brand_left");
		//sourceInfoLeft.addProperty("specTableContent_left");
		//sourceInfoLeft.addProperty("price_left");
		//sourceInfoLeft.addProperty("cluster_id_left");
		//sourceInfoLeft.addProperty("keyValuePairs_left");
		sourceInfoLeft.setType("json");
		ACache	scLeft= new HybridCache();
		JsonQueryModule jsonModelLeft=new JsonQueryModule();
		scLeft = jsonModelLeft.fillLeftCache(sourceInfoLeft);
		
		KBInfo sourceInfoRight = new KBInfo();
		sourceInfoRight.setEndpoint(computerGoldStandardData);
		sourceInfoRight.setVar("?x");
		sourceInfoRight.setPageSize(2000);
		sourceInfoRight.setId("sourceKbId");
		sourceInfoRight.addCatogery("Computers_and_Accessories");
		sourceInfoRight.addProperty("title_right");
		//sourceInfoRight.addProperty("description_right");
		//sourceInfoRight.addProperty("brand_right");
		//sourceInfoRight.addProperty("specTableContent_right");
		//sourceInfoRight.addProperty("price_right");
		//sourceInfoRight.addProperty("cluster_id_right");
		//sourceInfoRight.addProperty("keyValuePairs_right");
		sourceInfoRight.setType("json");
		ACache	scRight= new HybridCache();
		JsonQueryModule jsonModelRight=new JsonQueryModule();
		scRight = jsonModelRight.fillRightCache(sourceInfoRight);

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

		for(int i = 0;i< traingData.size();i++) {
			logger.info("training data.... "+traingData.get(i));

			KBInfo sourceInfoTrainLeft = new KBInfo();
			sourceInfoTrainLeft.setEndpoint(traingData.get(i));
			sourceInfoTrainLeft.setVar("?x");
			sourceInfoTrainLeft.setPageSize(2000);
			sourceInfoTrainLeft.setId("sourceKbId");
			sourceInfoTrainLeft.addCatogery("Computers_and_Accessories");
			sourceInfoTrainLeft.addProperty("title_left");
			//sourceInfoTrainLeft.addProperty("description_left");
			//sourceInfoTrainLeft.addProperty("brand_left");
			//sourceInfoTrainLeft.addProperty("specTableContent_left");
			//sourceInfoTrainLeft.addProperty("price_left");
			//sourceInfoTrainLeft.addProperty("cluster_id_left");
			//sourceInfoTrainLeft.addProperty("keyValuePairs_left");
			sourceInfoTrainLeft.setType("json");
			ACache	scTrainLeft= new HybridCache();
			JsonQueryModule jsonModelTrainLeft=new JsonQueryModule();
			scTrainLeft = jsonModelTrainLeft.fillLeftCache(sourceInfoTrainLeft);

			KBInfo sourceInfoTrainRight = new KBInfo();
			sourceInfoTrainRight.setEndpoint(traingData.get(i));
			sourceInfoTrainRight.setVar("?x");
			sourceInfoTrainRight.setPageSize(2000);
			sourceInfoTrainRight.setId("sourceKbId");
			sourceInfoTrainRight.addCatogery("Computers_and_Accessories");
			sourceInfoTrainRight.addProperty("title_right");
			//sourceInfoTrainRight.addProperty("description_right");
			//sourceInfoTrainRight.addProperty("brand_right");
			//sourceInfoTrainRight.addProperty("specTableContent_right");
			//sourceInfoTrainRight.addProperty("price_right");
			//sourceInfoTrainRight.addProperty("cluster_id_right");
			//sourceInfoTrainRight.addProperty("keyValuePairs_right");
			
			sourceInfoTrainRight.setType("json");
			ACache	scTrainRight= new HybridCache();
			JsonQueryModule jsonModelTrainRight=new JsonQueryModule();
			scTrainRight = jsonModelTrainRight.fillRightCache(sourceInfoTrainRight);

			JsonMappingReader jsonMappingReaderTraining=new JsonMappingReader(traingData.get(i));
			AMapping trainingMaping=jsonMappingReaderTraining.readP();
			System.out.println("training  map size= "+trainingMaping.size());
			logger.info("gold standard adedd... "+computerGoldStandardData);
			JsonMappingReader jsonMappingReaderGoldStandard=new JsonMappingReader(computerGoldStandardData);
			AMapping goldStandardMaping=jsonMappingReaderGoldStandard.readP();
			System.out.println("goldstandard map size= "+goldStandardMaping.size());

			SupervisedMLAlgorithm wombatSimple = null;
			try {
				wombatSimple = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.SUPERVISED_BATCH).asSupervised();
			} catch (UnsupportedMLImplementationException e) {
				e.printStackTrace();
			}
			//Set<String> measure = new HashSet<>(Arrays.asList("jaccard", "euclidean","cosine","JaroWinkler"));
			Set<String> measure = new HashSet<>(Arrays.asList("jaccard","qgrams"));
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER, 5);
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_REFINEMENT_TREE_SIZE, 5000);
			//wombatSimple.setParameter(AWombat.PARAMETER_MIN_PROPERTY_COVERAGE, 0.6);
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATION_TIME_IN_MINUTES, 20);
			wombatSimple.setParameter(AWombat.PARAMETER_EXECUTION_TIME_IN_MINUTES, 600);
			wombatSimple.setParameter(AWombat.PARAMETER_ATOMIC_MEASURES, measure);
			wombatSimple.init(null, scTrainLeft, scTrainRight);
			MLResults mlModel = null;
			try {
				mlModel = wombatSimple.learn(trainingMaping);
				//mlModel = wombatSimple.learn(trainingMaping);
			} catch (UnsupportedMLImplementationException e) {
				e.printStackTrace();
			}
			resultMap = wombatSimple.predict(scLeft, scRight, mlModel);
			allMappings.add(resultMap);
			System.out.println("wombar mapping... "+resultMap.size());
			FMeasure fmeausre =new FMeasure();
			double f=fmeausre.calculate(resultMap, new GoldStandard(goldStandardMaping));
			double r=fmeausre.recall(resultMap, new GoldStandard(goldStandardMaping));
			double p=fmeausre.precision(resultMap, new GoldStandard(goldStandardMaping));
			System.out.println(" Ex, LS, f , r, p");
			System.out.println("Ex. computer "+ i+", "+mlModel.getLinkSpecification().getFullExpression()+", "+f+" , "+r+" , "+p);
		}
		return allMappings;
	}

	public static List<AMapping> experimentWatches() {

		String watchesGoldStandatdData="/home/abdullah/iswc2020/watches_gs.json";
		
		KBInfo sourceInfoLeft = new KBInfo();
		sourceInfoLeft.setEndpoint(watchesGoldStandatdData);
		sourceInfoLeft.setVar("?x");
		sourceInfoLeft.setPageSize(2000);
		sourceInfoLeft.setId("sourceKbId");
		sourceInfoLeft.addCatogery("Jewelry");
		sourceInfoLeft.addProperty("title_left");
		//sourceInfoLeft.addProperty("description_left");
		//sourceInfoLeft.addProperty("brand_left");
		//sourceInfoLeft.addProperty("specTableContent_left");
		//sourceInfoLeft.addProperty("price_left");
		//sourceInfoLeft.addProperty("cluster_id_left");
		//sourceInfoLeft.addProperty("keyValuePairs_left");
		sourceInfoLeft.setType("json");
		ACache	scLeft= new HybridCache();
		JsonQueryModule jsonModelLeft=new JsonQueryModule();
		scLeft = jsonModelLeft.fillLeftCache(sourceInfoLeft);

		KBInfo sourceInfoRight = new KBInfo();
		sourceInfoRight.setEndpoint(watchesGoldStandatdData);
		sourceInfoRight.setVar("?x");
		sourceInfoRight.setPageSize(2000);
		sourceInfoRight.setId("sourceKbId");
		sourceInfoRight.addCatogery("Jewelry");
		sourceInfoRight.addProperty("title_right");
		//sourceInfoRight.addProperty("description_right");
		//sourceInfoRight.addProperty("brand_right");
		//sourceInfoRight.addProperty("specTableContent_right");
		//sourceInfoRight.addProperty("price_right");
		//sourceInfoRight.addProperty("price_right");
		//sourceInfoRight.addProperty("keyValuePairs_right");
		sourceInfoRight.setType("json");
		ACache	scRight= new HybridCache();
		JsonQueryModule jsonModelRight=new JsonQueryModule();
		scRight = jsonModelRight.fillRightCache(sourceInfoRight);

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


		for(int i = 0;i< traingData.size();i++) {
			logger.info("training data.... "+traingData.get(i));

			KBInfo sourceInfoTrainLeft = new KBInfo();
			sourceInfoTrainLeft.setEndpoint(traingData.get(i));
			sourceInfoTrainLeft.setVar("?x");
			sourceInfoTrainLeft.setPageSize(2000);
			sourceInfoTrainLeft.setId("sourceKbId");
			sourceInfoTrainLeft.addCatogery("Jewelry");
			sourceInfoTrainLeft.addProperty("title_left");
			//sourceInfoTrainLeft.addProperty("description_left");
			//sourceInfoTrainLeft.addProperty("brand_left");
			//sourceInfoTrainLeft.addProperty("specTableContent_left");
			//sourceInfoTrainLeft.addProperty("price_left");
			//sourceInfoTrainLeft.addProperty("cluster_id_left");
			//sourceInfoTrainLeft.addProperty("keyValuePairs_left");
			sourceInfoTrainLeft.setType("json");
			ACache	scTrainLeft= new HybridCache();
			JsonQueryModule jsonModelTrainLeft=new JsonQueryModule();
			scTrainLeft = jsonModelTrainLeft.fillLeftCache(sourceInfoTrainLeft);

			KBInfo sourceInfoTrainRight = new KBInfo();
			sourceInfoTrainRight.setEndpoint(traingData.get(i));
			sourceInfoTrainRight.setVar("?x");
			sourceInfoTrainRight.setPageSize(2000);
			sourceInfoTrainRight.setId("sourceKbId");
			sourceInfoTrainRight.addCatogery("Jewelry");
			sourceInfoTrainRight.addProperty("title_right");
			//sourceInfoTrainRight.addProperty("description_right");
			//sourceInfoTrainRight.addProperty("brand_right");
			//sourceInfoTrainRight.addProperty("specTableContent_right");
			//sourceInfoTrainRight.addProperty("price_right");
			//sourceInfoTrainRight.addProperty("cluster_id_right");
			//sourceInfoTrainRight.addProperty("keyValuePairs_right");
			sourceInfoTrainRight.setType("json");
			ACache	scTrainRight= new HybridCache();
			JsonQueryModule jsonModelTrainRight=new JsonQueryModule();
			scTrainRight = jsonModelTrainRight.fillRightCache(sourceInfoTrainRight);


			JsonMappingReader jsonMappingReaderTraining=new JsonMappingReader(traingData.get(i));
			AMapping trainingMaping=jsonMappingReaderTraining.readP();
			System.out.println("training  map size= "+trainingMaping.size());
			logger.info("gold standard adedd... "+watchesGoldStandatdData);
			JsonMappingReader jsonMappingReaderGoldStandard=new JsonMappingReader(watchesGoldStandatdData);
			AMapping goldStandardMaping=jsonMappingReaderGoldStandard.readP();
			System.out.println("goldstandard map size= "+goldStandardMaping.size());

			SupervisedMLAlgorithm wombatSimple = null;
			try {
				wombatSimple = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.SUPERVISED_BATCH).asSupervised();
			} catch (UnsupportedMLImplementationException e) {
				e.printStackTrace();
			}
			//Set<String> measure = new HashSet<>(Arrays.asList("jaccard", "euclidean","cosine","JaroWinkler"));
			Set<String> measure = new HashSet<>(Arrays.asList("jaccard","qgrams"));
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER, 5);
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_REFINEMENT_TREE_SIZE, 5000);
	        //wombatSimple.setParameter(AWombat.PARAMETER_MIN_PROPERTY_COVERAGE, 0.60);
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATION_TIME_IN_MINUTES, 20);
			wombatSimple.setParameter(AWombat.PARAMETER_EXECUTION_TIME_IN_MINUTES, 600);
			wombatSimple.setParameter(AWombat.PARAMETER_ATOMIC_MEASURES, measure);
			wombatSimple.init(null, scTrainLeft, scTrainRight);
			MLResults mlModel = null;
			try {
				mlModel = wombatSimple.learn(trainingMaping);
				//mlModel = wombatSimple.learn(trainingMaping);
			} catch (UnsupportedMLImplementationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultMap = wombatSimple.predict(scLeft, scRight, mlModel);
			allMappings.add(resultMap);
			System.out.println("wombar mapping... "+resultMap.size());
			FMeasure fmeausre =new FMeasure();
			double f=fmeausre.calculate(resultMap, new GoldStandard(goldStandardMaping));
			double r=fmeausre.recall(resultMap, new GoldStandard(goldStandardMaping));
			double p=fmeausre.precision(resultMap, new GoldStandard(goldStandardMaping));
			System.out.println("Ex, LS, f , r, p");
			System.out.println("Ex. watches "+ i+", "+mlModel.getLinkSpecification().getFullExpression()+", "+f+" , "+r+" , "+p);
		}
		return allMappings;
	}

	public static List<AMapping> experimentCameras() {

		String cameraGoldStandardData="/home/abdullah/iswc2020/cameras_gs.json";

		KBInfo sourceInfoLeft = new KBInfo();
		sourceInfoLeft.setEndpoint(cameraGoldStandardData);
		sourceInfoLeft.setVar("?x");
		sourceInfoLeft.setPageSize(2000);
		sourceInfoLeft.setId("sourceKbId");
		sourceInfoLeft.addCatogery("Camera_and_Photo");
		sourceInfoLeft.addProperty("title_left");
		//sourceInfoLeft.addProperty("description_left");
		//sourceInfoLeft.addProperty("brand_left");
		//sourceInfoLeft.addProperty("specTableContent_left");
		//sourceInfoLeft.addProperty("price_left");
		//sourceInfoLeft.addProperty("cluster_id_left");
		//sourceInfoLeft.addProperty("keyValuePairs_left");
		sourceInfoLeft.setType("json");
		ACache	scLeft= new HybridCache();
		JsonQueryModule jsonModelLeft=new JsonQueryModule();
		scLeft = jsonModelLeft.fillLeftCache(sourceInfoLeft);

		KBInfo sourceInfoRight = new KBInfo();
		sourceInfoRight.setEndpoint(cameraGoldStandardData);
		sourceInfoRight.setVar("?x");
		sourceInfoRight.setPageSize(2000);
		sourceInfoRight.setId("sourceKbId");
		sourceInfoRight.addCatogery("Camera_and_Photo");
		sourceInfoRight.addProperty("title_right");
		//sourceInfoRight.addProperty("description_right");
		//sourceInfoRight.addProperty("brand_right");
		//sourceInfoRight.addProperty("specTableContent_right");
		//sourceInfoRight.addProperty("price_right");
		//sourceInfoRight.addProperty("cluster_id_right");
		//sourceInfoRight.addProperty("keyValuePairs_right");
		
		sourceInfoRight.setType("json");
		ACache	scRight= new HybridCache();
		JsonQueryModule jsonModelRight=new JsonQueryModule();
		scRight = jsonModelRight.fillRightCache(sourceInfoRight);

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

		for(int i = 0;i< traingData.size();i++) {
			logger.info("training data.... "+traingData.get(i));

			KBInfo sourceInfoTrainLeft = new KBInfo();
			sourceInfoTrainLeft.setEndpoint(traingData.get(i));
			sourceInfoTrainLeft.setVar("?x");
			sourceInfoTrainLeft.setPageSize(2000);
			sourceInfoTrainLeft.setId("sourceKbId");
			sourceInfoTrainLeft.addCatogery("Camera_and_Photo");
			sourceInfoTrainLeft.addProperty("title_left");
			//sourceInfoTrainLeft.addProperty("description_left");
			//sourceInfoTrainLeft.addProperty("brand_left");
			//sourceInfoTrainLeft.addProperty("specTableContent_left");
			//sourceInfoTrainLeft.addProperty("price_left");
			//sourceInfoTrainLeft.addProperty("cluster_id_left");
			//sourceInfoTrainLeft.addProperty("keyValuePairs_left");
			sourceInfoTrainLeft.setType("json");
			ACache	scTrainLeft= new HybridCache();
			JsonQueryModule jsonModelTrainLeft=new JsonQueryModule();
			scTrainLeft = jsonModelTrainLeft.fillLeftCache(sourceInfoTrainLeft);

			KBInfo sourceInfoTrainRight = new KBInfo();
			sourceInfoTrainRight.setEndpoint(traingData.get(i));
			sourceInfoTrainRight.setVar("?x");
			sourceInfoTrainRight.setPageSize(2000);
			sourceInfoTrainRight.setId("sourceKbId");
			sourceInfoTrainRight.addCatogery("Camera_and_Photo");
			sourceInfoTrainRight.addProperty("title_right");
			//sourceInfoTrainRight.addProperty("description_right");
			//sourceInfoTrainRight.addProperty("brand_right");
			//sourceInfoTrainRight.addProperty("specTableContent_right");
			//sourceInfoTrainRight.addProperty("price_right");
			//sourceInfoTrainRight.addProperty("cluster_id_right");
			//sourceInfoTrainRight.addProperty("keyValuePairs_right");
			sourceInfoTrainRight.setType("json");
			ACache	scTrainRight= new HybridCache();
			JsonQueryModule jsonModelTrainRight=new JsonQueryModule();
			scTrainRight = jsonModelTrainRight.fillRightCache(sourceInfoTrainRight);


			JsonMappingReader jsonMappingReaderTraining=new JsonMappingReader(traingData.get(i));
			AMapping trainingMaping=jsonMappingReaderTraining.readP();
			System.out.println("training map size= "+trainingMaping.size());
			logger.info("gold standard adedd... "+cameraGoldStandardData);
			JsonMappingReader jsonMappingReaderGoldStandard=new JsonMappingReader(cameraGoldStandardData);
			AMapping goldStandardMaping=jsonMappingReaderGoldStandard.readP();
			System.out.println("goldstandard map size= "+goldStandardMaping.size());

			SupervisedMLAlgorithm wombatSimple = null;
			try {
				wombatSimple = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.SUPERVISED_BATCH).asSupervised();
			} catch (UnsupportedMLImplementationException e) {
				e.printStackTrace();

			}
			//Set<String> measure = new HashSet<>(Arrays.asList("jaccard", "euclidean","cosine","JaroWinkler"));
			Set<String> measure = new HashSet<>(Arrays.asList("jaccard","qgrams"));
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER, 5);
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_REFINEMENT_TREE_SIZE, 5000);
			//wombatSimple.setParameter(AWombat.PARAMETER_MIN_PROPERTY_COVERAGE, 0.60);
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATION_TIME_IN_MINUTES, 20);
			wombatSimple.setParameter(AWombat.PARAMETER_EXECUTION_TIME_IN_MINUTES, 600);
			wombatSimple.setParameter(AWombat.PARAMETER_ATOMIC_MEASURES, measure);

			wombatSimple.init(null, scTrainLeft, scTrainRight);
			MLResults mlModel = null;
			try {
				mlModel = wombatSimple.learn(trainingMaping);
				//mlModel = wombatSimple.learn(trainingMaping);
			} catch (UnsupportedMLImplementationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultMap = wombatSimple.predict(scLeft, scRight, mlModel);
			allMappings.add(resultMap);
			System.out.println("wombat mapping... "+resultMap.size());

			FMeasure fmeausre =new FMeasure();
			double f=fmeausre.calculate(resultMap, new GoldStandard(goldStandardMaping));
			double r=fmeausre.recall(resultMap, new GoldStandard(goldStandardMaping));
			double p=fmeausre.precision(resultMap, new GoldStandard(goldStandardMaping));

			System.out.println("Ex, LS, f , r, p");
			System.out.println("Ex. camera "+ i+", "+mlModel.getLinkSpecification().getFullExpression()+", "+f+" , "+r+" , "+p);
			//return resultMap;
		}
		return allMappings;
	}

	public static List<AMapping> experimentShoes() {

		String shoesGoldStandardData="/home/abdullah/iswc2020/shoes_gs.json";

		KBInfo sourceInfoLeft = new KBInfo();
		sourceInfoLeft.setEndpoint(shoesGoldStandardData);
		sourceInfoLeft.setVar("?x");
		sourceInfoLeft.setPageSize(2000);
		sourceInfoLeft.setId("sourceKbId");
		sourceInfoLeft.addCatogery("Shoes");
		sourceInfoLeft.addProperty("title_left");
		//sourceInfoLeft.addProperty("description_left");
		//sourceInfoLeft.addProperty("brand_left");
		//sourceInfoLeft.addProperty("specTableContent_left");
		//sourceInfoLeft.addProperty("price_left");
		//sourceInfoLeft.addProperty("cluster_id_left");
		//sourceInfoLeft.addProperty("keyValuePairs_left");
		sourceInfoLeft.setType("json");
		ACache	scLeft= new HybridCache();
		JsonQueryModule jsonModelLeft=new JsonQueryModule();
		scLeft = jsonModelLeft.fillLeftCache(sourceInfoLeft);

		KBInfo sourceInfoRight = new KBInfo();
		sourceInfoRight.setEndpoint(shoesGoldStandardData);
		sourceInfoRight.setVar("?x");
		sourceInfoRight.setPageSize(2000);
		sourceInfoRight.setId("sourceKbId");
		sourceInfoRight.addCatogery("Shoes");
		sourceInfoRight.addProperty("title_right");
		//sourceInfoRight.addProperty("description_right");
		//sourceInfoRight.addProperty("brand_right");
		//sourceInfoRight.addProperty("specTableContent_right");
		//sourceInfoRight.addProperty("price_right");
		//sourceInfoRight.addProperty("cluster_id_right");
		//sourceInfoRight.addProperty("keyValuePairs_right");
		sourceInfoRight.setType("json");
		ACache	scRight= new HybridCache();
		JsonQueryModule jsonModelRight=new JsonQueryModule();
		scRight = jsonModelRight.fillRightCache(sourceInfoRight);

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

		for(int i = 0;i< traingData.size();i++) {
			logger.info("training data.... "+traingData.get(i));
			KBInfo sourceInfoTrainLeft = new KBInfo();
			sourceInfoTrainLeft.setEndpoint(traingData.get(i));
			sourceInfoTrainLeft.setVar("?x");
			sourceInfoTrainLeft.setPageSize(2000);
			sourceInfoTrainLeft.setId("sourceKbId");
			sourceInfoTrainLeft.addCatogery("Shoes");
			sourceInfoTrainLeft.addProperty("title_left");
			//sourceInfoTrainLeft.addProperty("description_left");
			//sourceInfoTrainLeft.addProperty("brand_left");
			//sourceInfoTrainLeft.addProperty("specTableContent_left");
			//sourceInfoTrainLeft.addProperty("price_left");
			//sourceInfoTrainLeft.addProperty("cluster_id_left");
			//sourceInfoTrainLeft.addProperty("keyValuePairs_left");
			sourceInfoTrainLeft.setType("json");
			ACache	scTrainLeft= new HybridCache();
			JsonQueryModule jsonModelTrainLeft=new JsonQueryModule();
			scTrainLeft = jsonModelTrainLeft.fillLeftCache(sourceInfoTrainLeft);

			KBInfo sourceInfoTrainRight = new KBInfo();
			sourceInfoTrainRight.setEndpoint(traingData.get(i));
			sourceInfoTrainRight.setVar("?x");
			sourceInfoTrainRight.setPageSize(2000);
			sourceInfoTrainRight.setId("sourceKbId");
			sourceInfoTrainRight.addCatogery("Shoes");
			sourceInfoTrainRight.addProperty("title_right");
			//sourceInfoTrainRight.addProperty("description_right");
			//sourceInfoTrainRight.addProperty("brand_right");
			//sourceInfoTrainRight.addProperty("specTableContent_right");
			//sourceInfoTrainRight.addProperty("price_right");
			//sourceInfoTrainRight.addProperty("cluster_id_right");
			//sourceInfoTrainRight.addProperty("keyValuePairs_right");
			sourceInfoTrainRight.setType("json");
			ACache	scTrainRight= new HybridCache();
			JsonQueryModule jsonModelTrainRight=new JsonQueryModule();
			scTrainRight = jsonModelTrainRight.fillRightCache(sourceInfoTrainRight);

			JsonMappingReader jsonMappingReaderTraining=new JsonMappingReader(traingData.get(i));
			AMapping trainingMaping=jsonMappingReaderTraining.readP();
			System.out.println("training map size= "+trainingMaping.size());
			logger.info("gold standard adedd... "+shoesGoldStandardData);
			JsonMappingReader jsonMappingReaderGoldStandard=new JsonMappingReader(shoesGoldStandardData);
			AMapping goldStandardMaping=jsonMappingReaderGoldStandard.readP();
			System.out.println("goldstandard map size= "+goldStandardMaping.size());

			SupervisedMLAlgorithm wombatSimple = null;
			try {
				wombatSimple = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.SUPERVISED_BATCH).asSupervised();
			} catch (UnsupportedMLImplementationException e) {
				e.printStackTrace();

			}
			//Set<String> measure = new HashSet<>(Arrays.asList("jaccard", "euclidean","cosine","JaroWinkler"));
			Set<String> measure = new HashSet<>(Arrays.asList("jaccard","qgrams"));
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER, 5);
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_REFINEMENT_TREE_SIZE, 5000);
			//wombatSimple.setParameter(AWombat.PARAMETER_MIN_PROPERTY_COVERAGE, 0.60);
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATION_TIME_IN_MINUTES, 20);
			wombatSimple.setParameter(AWombat.PARAMETER_EXECUTION_TIME_IN_MINUTES, 600);
			wombatSimple.setParameter(AWombat.PARAMETER_ATOMIC_MEASURES, measure);

			wombatSimple.init(null, scTrainLeft, scTrainRight);
			MLResults mlModel = null;
			try {
				mlModel = wombatSimple.learn(trainingMaping);
			} catch (UnsupportedMLImplementationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultMap = wombatSimple.predict(scLeft, scRight, mlModel);
			allMappings.add(resultMap);
			System.out.println("wombat mapping... "+resultMap.size());

			FMeasure fmeausre =new FMeasure();
			double f=fmeausre.calculate(resultMap, new GoldStandard(goldStandardMaping));
			double r=fmeausre.recall(resultMap, new GoldStandard(goldStandardMaping));
			double p=fmeausre.precision(resultMap, new GoldStandard(goldStandardMaping));

			allMappings.add(resultMap);
			System.out.println("Ex, LS, f , r, p");
			System.out.println("Ex. shoes "+ i+", "+mlModel.getLinkSpecification().getFullExpression()+", "+f+" , "+r+" , "+p);
			//return resultMap;
		}
		return allMappings;
	}

	public static List<AMapping> experimentAll() {

		String allGoldStandardData="/home/abdullah/iswc2020/all_gs.json";

		KBInfo sourceInfoLeft = new KBInfo();
		sourceInfoLeft.setEndpoint(allGoldStandardData);
		sourceInfoLeft.setVar("?x");
		sourceInfoLeft.setPageSize(2000);
		sourceInfoLeft.setId("sourceKbId");
		sourceInfoLeft.addCatogery("all");
		sourceInfoLeft.addProperty("title_left");
		//sourceInfoLeft.addProperty("description_left");
		//sourceInfoLeft.addProperty("brand_left");
		//sourceInfoLeft.addProperty("specTableContent_left");
		//sourceInfoLeft.addProperty("price_left");
		//sourceInfoLeft.addProperty("cluster_id_left");
		//sourceInfoLeft.addProperty("keyValuePairs_left");
		sourceInfoLeft.setType("json");
		ACache	scLeft= new HybridCache();
		JsonQueryModule jsonModelLeft=new JsonQueryModule();
		scLeft = jsonModelLeft.fillLeftCache(sourceInfoLeft);

		KBInfo sourceInfoRight = new KBInfo();
		sourceInfoRight.setEndpoint(allGoldStandardData);
		sourceInfoRight.setVar("?x");
		sourceInfoRight.setPageSize(2000);
		sourceInfoRight.setId("sourceKbId");
		sourceInfoRight.addCatogery("all");
		sourceInfoRight.addProperty("title_right");
		//sourceInfoRight.addProperty("description_right");
		//sourceInfoRight.addProperty("brand_right");
		//sourceInfoRight.addProperty("specTableContent_right");
		//sourceInfoRight.addProperty("price_right");
		//sourceInfoRight.addProperty("cluster_id_right");
		//sourceInfoRight.addProperty("keyValuePairs_right");
		sourceInfoRight.setType("json");
		ACache	scRight= new HybridCache();
		JsonQueryModule jsonModelRight=new JsonQueryModule();
		scRight = jsonModelRight.fillRightCache(sourceInfoRight);

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

		for(int i = 0;i< traingData.size();i++) {

			logger.info("training data.... "+traingData.get(i));

			KBInfo sourceInfoTrainLeft = new KBInfo();
			sourceInfoTrainLeft.setEndpoint(traingData.get(i));
			sourceInfoTrainLeft.setVar("?x");
			sourceInfoTrainLeft.setPageSize(2000);
			sourceInfoTrainLeft.setId("sourceKbId");
			sourceInfoTrainLeft.addCatogery("all");
			sourceInfoTrainLeft.addProperty("title_left");
			//sourceInfoTrainLeft.addProperty("description_left");
			//sourceInfoTrainLeft.addProperty("brand_left");
			//sourceInfoTrainLeft.addProperty("specTableContent_left");
			//sourceInfoTrainLeft.addProperty("price_left");
			//sourceInfoTrainLeft.addProperty("cluster_id_left");
			//sourceInfoTrainLeft.addProperty("keyValuePairs_left");
			sourceInfoTrainLeft.setType("json");
			ACache	scTrainLeft= new HybridCache();
			JsonQueryModule jsonModelTrainLeft=new JsonQueryModule();
			scTrainLeft = jsonModelTrainLeft.fillLeftCache(sourceInfoTrainLeft);

			KBInfo sourceInfoTrainRight = new KBInfo();
			sourceInfoTrainRight.setEndpoint(traingData.get(i));
			sourceInfoTrainRight.setVar("?x");
			sourceInfoTrainRight.setPageSize(2000);
			sourceInfoTrainRight.setId("sourceKbId");
			sourceInfoTrainRight.addCatogery("all");
			sourceInfoTrainRight.addProperty("title_right");
			//sourceInfoTrainRight.addProperty("description_right");
			//sourceInfoTrainRight.addProperty("brand_right");
			//sourceInfoTrainRight.addProperty("specTableContent_right");
			//sourceInfoTrainRight.addProperty("price_right");
			//sourceInfoTrainRight.addProperty("cluster_id_right");
			//sourceInfoTrainRight.addProperty("keyValuePairs_right");
			sourceInfoTrainRight.setType("json");
			ACache	scTrainRight= new HybridCache();
			JsonQueryModule jsonModelTrainRight=new JsonQueryModule();
			scTrainRight = jsonModelTrainRight.fillRightCache(sourceInfoTrainRight);

			JsonMappingReader jsonMappingReaderTraining=new JsonMappingReader(traingData.get(i));
			AMapping trainingMaping=jsonMappingReaderTraining.readP();
			System.out.println("training  map size= "+trainingMaping.size());
			logger.info("gold standard adedd... "+allGoldStandardData);
			JsonMappingReader jsonMappingReaderGoldStandard=new JsonMappingReader(allGoldStandardData);
			AMapping goldStandardMaping=jsonMappingReaderGoldStandard.readP();
			System.out.println("goldstandard map size= "+goldStandardMaping.size());

			//JsonMappingReader jsonMappingReaderTesting=new JsonMappingReader(testFile);
			//AMapping testingMaping=jsonMappingReaderTesting.read();
			//System.out.println("testing  map size= "+testingMaping.size());
			SupervisedMLAlgorithm wombatSimple = null;
			try {
				wombatSimple = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.SUPERVISED_BATCH).asSupervised();
			} catch (UnsupportedMLImplementationException e) {
				e.printStackTrace();

			}
			//Set<String> measure = new HashSet<>(Arrays.asList("jaccard", "euclidean","cosine","JaroWinkler"));
			Set<String> measure = new HashSet<>(Arrays.asList("jaccard","qgrams"));
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER, 5);
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_REFINEMENT_TREE_SIZE, 5000);
	        //wombatSimple.setParameter(AWombat.PARAMETER_MIN_PROPERTY_COVERAGE, 0.60);
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATION_TIME_IN_MINUTES, 20);
			wombatSimple.setParameter(AWombat.PARAMETER_EXECUTION_TIME_IN_MINUTES, 600);
			wombatSimple.setParameter(AWombat.PARAMETER_ATOMIC_MEASURES, measure);
			wombatSimple.init(null, scTrainLeft, scTrainRight);
			MLResults mlModel = null;
			try {
				mlModel = wombatSimple.learn(trainingMaping);
			} catch (UnsupportedMLImplementationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultMap = wombatSimple.predict(scLeft, scRight, mlModel);
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

	/*
	private static List<ACache> fillSampleSourceTargetCaches(AMapping sample, ACache sc) {
		List<ACache> sourceTargetCaches =new ArrayList<ACache>();
		ACache sourceSample=new HybridCache();
		ACache targetSample=new HybridCache();
		for (String s : sample.getMap().keySet()) {
			if (sc.containsUri(s)) {
				sourceSample.addInstance(sc.getInstance(s));
				sourceTargetCaches.add(sourceSample);
				for (String t : sample.getMap().get(s).keySet())
					if (sc.containsUri(t)) {
						targetSample.addInstance(sc.getInstance(t));
						sourceTargetCaches.add(targetSample);}
					else
						logger.warn("Instance " + t + " does not exist in the target dataset");
			} else {
				logger.warn("Instance " + s + " does not exist in the source dataset");
			}
		}
		return sourceTargetCaches;
	}

	 */
}


