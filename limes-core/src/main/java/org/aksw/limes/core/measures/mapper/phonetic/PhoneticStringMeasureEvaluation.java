package org.aksw.limes.core.measures.mapper.phonetic;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser.DataSets;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory.ExecutionEngineType;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory.ExecutionPlannerType;
import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.execution.rewriter.Rewriter;
import org.aksw.limes.core.execution.rewriter.RewriterFactory;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.string.EDJoinMapper;
import org.aksw.limes.core.measures.mapper.string.PPJoinPlusPlus;
import org.aksw.limes.core.measures.mapper.string.fastngram.FastNGramMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class PhoneticStringMeasureEvaluation {


	//static double theta=1.0;

	private static final Logger logger = LoggerFactory.getLogger(PhoneticStringMeasureEvaluation.class);

	public static void main(String[] args) {
		Double[] theta= {1.0,0.9,0.8,0.7,0.6};
		String PROPERTIES= "(x.description,y.description)";
		EvaluationData eval = DataSetChooser.getData(DataSets.ABTBUY);
		//String PROPERTIES= "(x.authors,y.authors)";
		FMeasure fmeausre =new FMeasure();

		// phonetic similarity algorithms 

		Caverphone1Mapper caverphone1=new Caverphone1Mapper();
		Caverphone2Mapper caverphone2=new Caverphone2Mapper();
		MetaPhoneMapper metaphone=new MetaPhoneMapper();
		DoubleMetaPhoneMapper doublemetaphone=new DoubleMetaPhoneMapper();
		SoundexMapper soundex =new SoundexMapper();
		KoelnPhoneticMapper koelnphonetic =new KoelnPhoneticMapper();
		NysiisMapper nysiis =new NysiisMapper();
		RefinedSoundexMapper refinedsoundex =new RefinedSoundexMapper();
		DaitchMokotoffMapper daitchmkotoff =new DaitchMokotoffMapper();
		MatchRatingMapper matchrating=new MatchRatingMapper();

		PPJoinPlusPlus ppjpp= new PPJoinPlusPlus();
		EDJoinMapper edj=new EDJoinMapper();
		FastNGramMapper fng=new FastNGramMapper();
		System.out.println("theta, Algo., F, R, P");
		ACache sourceCache = eval.getSourceCache();
		//System.out.println("cache size: "+sourceCache.getAllProperties());
		ACache targetCache = eval.getTargetCache();
		for(int i=0;i<theta.length;++i) {
			AMapping caverphone1Mapping= caverphone1.getMapping(sourceCache, targetCache,"?x", "?y", "caverphone1"+PROPERTIES, theta[i]);
			//	logger.info("size: " + caverphone1Mapping.size());
			double fCaverphone1=fmeausre.calculate(caverphone1Mapping, new GoldStandard(eval.getReferenceMapping()));
			double rCaverphone1=fmeausre.recall(caverphone1Mapping, new GoldStandard(eval.getReferenceMapping()));
			double pCaverphone1=fmeausre.precision(caverphone1Mapping, new GoldStandard(eval.getReferenceMapping()));
			System.out.println(theta[i]+", Caverphone1"+", "+ fCaverphone1+ ", "+rCaverphone1 +", "+ pCaverphone1);
			AMapping caverphone2Mapping= caverphone2.getMapping(sourceCache, targetCache,"?x", "?y", "caverphone2"+PROPERTIES, theta[i]);
			//	logger.info("size: " + caverphone2Mapping.size());
			double fCaverphone2=fmeausre.calculate(caverphone2Mapping, new GoldStandard(eval.getReferenceMapping()));
			double rCaverphone2=fmeausre.recall(caverphone2Mapping, new GoldStandard(eval.getReferenceMapping()));
			double pCaverphone2=fmeausre.precision(caverphone2Mapping, new GoldStandard(eval.getReferenceMapping()));
			System.out.println(theta[i]+",Caverphone2"+", "+ fCaverphone2+ ", "+rCaverphone2 +", "+ pCaverphone2);
			AMapping metaphoneMapping= metaphone.getMapping(sourceCache, targetCache,"?x", "?y", "metaphone"+PROPERTIES, theta[i]);
			//	logger.info("size: " + metaphoneMapping.size());
			double fMetaphone=fmeausre.calculate(metaphoneMapping, new GoldStandard(eval.getReferenceMapping()));
			double rMetaphone=fmeausre.recall(metaphoneMapping, new GoldStandard(eval.getReferenceMapping()));
			double pMetaphone=fmeausre.precision(metaphoneMapping, new GoldStandard(eval.getReferenceMapping()));
			System.out.println(theta[i]+",Metaphone"+", "+ fMetaphone+ ", "+rMetaphone +", "+  pMetaphone);
			AMapping doublmetaphoneMapping= doublemetaphone.getMapping(sourceCache, targetCache,"?x", "?y", "doublemetaphone"+PROPERTIES, theta[i]);
			//	logger.info("size: " + doublmetaphoneMapping.size());
			double fDMetaphone=fmeausre.calculate(doublmetaphoneMapping, new GoldStandard(eval.getReferenceMapping()));
			double rDMetaphone=fmeausre.recall(doublmetaphoneMapping, new GoldStandard(eval.getReferenceMapping()));
			double pDMetaphone=fmeausre.precision(doublmetaphoneMapping, new GoldStandard(eval.getReferenceMapping()));
			System.out.println(theta[i]+",DoublMetaphone"+", "+ fDMetaphone+ ", "+rDMetaphone +", "+  pDMetaphone);
			AMapping soundexMapping= soundex.getMapping(sourceCache, targetCache,"?x", "?y", "soundex"+PROPERTIES, theta[i]);
			//	logger.info("size: " + soundexMapping.size());
			double fSoundex=fmeausre.calculate(soundexMapping, new GoldStandard(eval.getReferenceMapping()));
			double rSoundex=fmeausre.recall(soundexMapping, new GoldStandard(eval.getReferenceMapping()));
			double pSoundex=fmeausre.precision(soundexMapping, new GoldStandard(eval.getReferenceMapping()));
			System.out.println(theta[i]+",Soundex"+", "+ fSoundex+ ", "+rSoundex +", "+  pSoundex);
			AMapping refinedsoundexMapping= refinedsoundex.getMapping(sourceCache, targetCache,"?x", "?y", "refinedsoundex"+PROPERTIES, theta[i]);
			//	logger.info("size: " + refinedsoundexMapping.size());

			double fRSoundex=fmeausre.calculate(refinedsoundexMapping, new GoldStandard(eval.getReferenceMapping()));
			double rRSoundex=fmeausre.recall(refinedsoundexMapping, new GoldStandard(eval.getReferenceMapping()));
			double pRSoundex=fmeausre.precision(refinedsoundexMapping, new GoldStandard(eval.getReferenceMapping()));
			System.out.println(theta[i]+",RSoundex"+", "+ fRSoundex+ ", "+rRSoundex +", "+  pRSoundex);

			AMapping koelnphoneticMapping= koelnphonetic.getMapping(sourceCache, targetCache,"?x", "?y", "koeln"+PROPERTIES, theta[i]);
			//	logger.info("size: " + koelnphoneticMapping.size());
			double fKoeln=fmeausre.calculate(koelnphoneticMapping, new GoldStandard(eval.getReferenceMapping()));
			double rKoeln=fmeausre.recall(koelnphoneticMapping, new GoldStandard(eval.getReferenceMapping()));
			double pKoeln=fmeausre.precision(koelnphoneticMapping, new GoldStandard(eval.getReferenceMapping()));
			System.out.println(theta[i]+",Koeln"+", "+ fKoeln+ ", "+rKoeln +", "+  pKoeln);

			AMapping nysiisMapping= nysiis.getMapping(sourceCache, targetCache,"?x", "?y", "nysiis"+PROPERTIES, theta[i]);
			//	logger.info("size: " + nysiisMapping.size());
			double fNysiis=fmeausre.calculate(nysiisMapping, new GoldStandard(eval.getReferenceMapping()));
			double rNysiis=fmeausre.recall(nysiisMapping, new GoldStandard(eval.getReferenceMapping()));
			double pNysiis=fmeausre.precision(nysiisMapping, new GoldStandard(eval.getReferenceMapping()));
			System.out.println(theta[i]+",Nysiis"+", "+ fNysiis+ ", "+rNysiis +", "+  pNysiis);
			AMapping daitchmkotoffMapping= daitchmkotoff.getMapping(sourceCache, targetCache,"?x", "?y", "daitchmokotoff"+PROPERTIES,theta[i]);
			//	logger.info("size: " + daitchmkotoffMapping.size());

			double fDaitch=fmeausre.calculate(daitchmkotoffMapping, new GoldStandard(eval.getReferenceMapping()));
			double rDaitch=fmeausre.recall(daitchmkotoffMapping, new GoldStandard(eval.getReferenceMapping()));
			double pDaitch=fmeausre.precision(daitchmkotoffMapping, new GoldStandard(eval.getReferenceMapping()));
			System.out.println(theta[i]+",Daitch"+", "+ fDaitch+ ", "+rDaitch +", "+  pDaitch);

			AMapping matchratingMapping= matchrating.getMapping(sourceCache, targetCache,"?x", "?y", "matchrating"+PROPERTIES, theta[i]);
			//	logger.info("size: " + matchratingMapping.size());
			double fMatch=fmeausre.calculate(matchratingMapping, new GoldStandard(eval.getReferenceMapping()));
			double rMatch=fmeausre.recall(matchratingMapping, new GoldStandard(eval.getReferenceMapping()));
			double pMatch=fmeausre.precision(matchratingMapping, new GoldStandard(eval.getReferenceMapping()));
			System.out.println(theta[i]+",Match"+", "+ fMatch+ ", "+rMatch +", "+  pMatch);

			// string similarity algorithms 


			AMapping cosineMapping =ppjpp.getMapping(sourceCache, targetCache, "?x", "?y", "cosine"+PROPERTIES, theta[i]);
			double fCosine=fmeausre.calculate(cosineMapping, new GoldStandard(eval.getReferenceMapping()));
			double rCosine=fmeausre.recall(cosineMapping, new GoldStandard(eval.getReferenceMapping()));
			double pCosine=fmeausre.precision(cosineMapping, new GoldStandard(eval.getReferenceMapping()));
			System.out.println(theta[i]+",Cosine"+", "+ fCosine+ ", "+rCosine +", "+  pCosine);

			AMapping overlapMapping =ppjpp.getMapping(sourceCache, targetCache, "?x", "?y", "overlap"+PROPERTIES, theta[i]);
			double fOverlap=fmeausre.calculate(overlapMapping, new GoldStandard(eval.getReferenceMapping()));
			double rOverlap=fmeausre.recall(overlapMapping, new GoldStandard(eval.getReferenceMapping()));
			double pOverlap=fmeausre.precision(overlapMapping, new GoldStandard(eval.getReferenceMapping()));
			System.out.println(theta[i]+",Overlap"+", "+ fOverlap+ ", "+rOverlap +", "+  pOverlap);

			AMapping trigramMapping =ppjpp.getMapping(sourceCache, targetCache, "?x", "?y", "trigram"+PROPERTIES, theta[i]);
			double fTrigram=fmeausre.calculate(trigramMapping, new GoldStandard(eval.getReferenceMapping()));
			double rTrigram=fmeausre.recall(trigramMapping, new GoldStandard(eval.getReferenceMapping()));
			double pTrigram=fmeausre.precision(trigramMapping, new GoldStandard(eval.getReferenceMapping()));
			System.out.println(theta[i]+",Trigram"+", "+ fTrigram+ ", "+rTrigram +", "+  pTrigram);

			AMapping jaccardMapping =ppjpp.getMapping(sourceCache, targetCache, "?x", "?y", "jaccard"+PROPERTIES, theta[i]);
			double fJaccard=fmeausre.calculate(jaccardMapping, new GoldStandard(eval.getReferenceMapping()));
			double rJaccard=fmeausre.recall(jaccardMapping, new GoldStandard(eval.getReferenceMapping()));
			double pJaccard=fmeausre.precision(jaccardMapping, new GoldStandard(eval.getReferenceMapping()));
			System.out.println(theta[i]+",Jaccard"+", "+ fJaccard+ ", "+rJaccard +", "+  pJaccard);

			AMapping levenshteinMapping= edj.getMapping(sourceCache, targetCache, "?x", "?y", "levenshtein"+PROPERTIES, theta[i]);
			double fLevenshtein=fmeausre.calculate(levenshteinMapping, new GoldStandard(eval.getReferenceMapping()));
			double rLevenshtein=fmeausre.recall(levenshteinMapping, new GoldStandard(eval.getReferenceMapping()));
			double pLevenshtein=fmeausre.precision(levenshteinMapping, new GoldStandard(eval.getReferenceMapping()));
			System.out.println(theta[i]+",Levenshtein"+", "+ fLevenshtein+ ", "+rLevenshtein +", "+  pLevenshtein);

			AMapping qgramsMapping=fng.getMapping(sourceCache, targetCache, "?x", "?y", "qgrams"+PROPERTIES, theta[i]);
			double fQgram=fmeausre.calculate(qgramsMapping, new GoldStandard(eval.getReferenceMapping()));
			double rQgram=fmeausre.recall(qgramsMapping, new GoldStandard(eval.getReferenceMapping()));
			double pQgram=fmeausre.precision(qgramsMapping, new GoldStandard(eval.getReferenceMapping()));
			System.out.println(theta[i]+",Qgram"+", "+ fQgram+ ", "+rQgram +", "+  pQgram);

			// TODO Auto-generated method stub

		}
	}
	
	public AMapping subLinkMap(LinkSpecification ls, ACache sCache, ACache tCache) {
		Rewriter rw = RewriterFactory.getDefaultRewriter();
		LinkSpecification rwLs = rw.rewrite(ls);
		IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT, sCache, tCache);
	
		ExecutionEngine engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sCache, tCache, "?x" , "?y");
	
		AMapping resultMap = engine.execute(rwLs, planner);
		//map = resultMap.getSubMap(ls.getThreshold());
		return resultMap;
	}


}
