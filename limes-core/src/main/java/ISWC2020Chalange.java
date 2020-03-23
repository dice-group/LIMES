/*
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
import org.aksw.limes.core.io.mapping.reader.JsonMappingReader;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.SupervisedMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.WombatSimple;


public class ISWC2020Chalange {

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
		categorys.add(0,"computers");
		categorys.add(1,"cameras");
		categorys.add(2,"watches");
		categorys.add(3,"shoes");
		categorys.add(4,"all");

		for(String category:categorys) {
			long startTime = System.nanoTime();
			experiment(sc,tc, category);
			long endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			System.out.println(category+" Execution time in milliseconds : " + timeElapsed / 1000000);


		}


	}

	private static void experiment(ACache sc  , ACache tc, String category) {

		List<String> traingDataSize=new ArrayList<String>();
		traingDataSize.add(0,"/home/abdullah/iswc2020/"+category+"_train_small.json");
		traingDataSize.add(1,"/home/abdullah/iswc2020/"+category+"_train_medium.json");
		traingDataSize.add(2,"/home/abdullah/iswc2020/"+category+"_train_large.json");
		traingDataSize.add(3,"/home/abdullah/iswc2020/"+category+"_train_xlarge.json");



		for(int i=0;i>traingDataSize.size();i++) {
			JsonMappingReader jsonMappingReaderTraining=new JsonMappingReader(traingDataSize.get(i));
			JsonMappingReader jsonMappingReaderGoldStandard=new JsonMappingReader("/home/abdullah/iswc2020/"+category+"_gs.json");
			AMapping trainingMaping=jsonMappingReaderTraining.read();
			AMapping goldStandardMaping=jsonMappingReaderGoldStandard.read();


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
			AMapping resultMap = wombatSimple.predict(sc, tc, mlModel);



			FMeasure fmeausre =new FMeasure();
			double f=fmeausre.calculate(resultMap, new GoldStandard(goldStandardMaping));
			double r=fmeausre.recall(resultMap, new GoldStandard(goldStandardMaping));
			double p=fmeausre.precision(resultMap, new GoldStandard(goldStandardMaping));
			System.out.println(category+" Experiment "+i);
			System.out.println("f , r, p");
			System.out.println(f+" , "+r+" , "+p);

		}
	}
}
 */
