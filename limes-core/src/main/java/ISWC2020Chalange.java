
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

		String sourceFile = "/home/abdullah/dataset/simple.json"; //=="one"
		//String targetFile = "/home/abdullah/iswc2020/offers_corpus_english_v2.json"; //=="two"
		//String trainingFile= args[2];
		//String goldStandardFile= args[3];


		KBInfo sourceInfo = new KBInfo();
		KBInfo targetInfo = new KBInfo();
		sourceInfo.setEndpoint(sourceFile);
		sourceInfo.setVar("?x");
		sourceInfo.setPageSize(2000);
		sourceInfo.setId("sourceKbId");
		sourceInfo.addCatogery("Jewelry");
		sourceInfo.addProperty("title");
		//sourceInfo.addProperty("description");
		//sourceInfo.addProperty("brand");
		//sourceInfo.addProperty("price");
		sourceInfo.setType("json");

		ACache sc = HybridCache.getData(sourceInfo);
		System.out.println("cache size: "+sc);

	}

}

