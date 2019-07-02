/*
package org.aksw.limes.core.measures.mapper.phonetic;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.MappingFactory.MappingType;
import org.aksw.limes.core.measures.mapper.string.EDJoinMapper;
import org.aksw.limes.core.measures.mapper.string.PPJoinPlusPlus;
import org.aksw.limes.core.measures.mapper.string.fastngram.FastNGramMapper;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.SupervisedMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.UnsupervisedMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.aksw.limes.core.ml.algorithm.wombat.AWombat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class PhoneticStringMeasureEvaluation {

	private static final Logger logger = LoggerFactory.getLogger(PhoneticStringMeasureEvaluation.class);

	public static void main(String[] args) throws UnsupportedMLImplementationException {
		System.out.println(" Wombat started");
		FMeasure fmeausre =new FMeasure();
		//Double[] theta= {1.0};
		String PROPERTIES= "(x.authors,y.authors)";

		List<String> DataSet=new ArrayList<String>();

		//DataSet.add("PERSON1");
			//DataSet.add("PERSON1_CSV");
	//	DataSet.add("PERSON2");
		//DataSet.add("PERSON2_CSV");
		DataSet.add("RESTAURANTS");
		//DataSet.add("DBLPACM");
		DataSet.add("ABTBUY");
		//DataSet.add("DBLPSCHOLAR");
		//DataSet.add("DRUGS");
	//	DataSet.add("AMAZONGOOGLEPRODUCTS");

/*
		for(String d:DataSet) {
			EvaluationData eval = DataSetChooser.getData(d);
			//linkSpec.readSpec("", 1.0);
			//AMapping goldMap = lsPostProcessor.goldMap(eval.getSourceCache(),eval.getTargetCache());
			//AMapping goldMap=subLinkMap(linkSpec,eval.getSourceCache(), eval.getTargetCache());

			ACache sourceCache = eval.getSourceCache();
			List<String> numericProperties = Arrays.asList(
					"http://www.okkam.org/ontology_person1.owl#age", 
					"http://www.okkam.org/ontology_person1.owl#phone_numer",
					"http://www.okkam.org/ontology_person1.owl#soc_sec_id",
					"http://www.okkam.org/ontology_person1.owl#date_of_birth",
					"http://www.okkam.org/ontology_person1.owl#has_address",
					"http://www.okkam.org/ontology_person2.owl#age", 
					"http://www.okkam.org/ontology_person2.owl#phone_numer",
					"http://www.okkam.org/ontology_person2.owl#soc_sec_id",
					"http://www.okkam.org/ontology_person2.owl#date_of_birth",
					"http://www.okkam.org/ontology_person2.owl#has_address",
					"venue",
					"year",
					"http://www.okkam.org/ontology_restaurant1.owl#phone_number",
					"http://www.okkam.org/ontology_restaurant2.owl#phone_number",
					"http://www.okkam.org/ontology_restaurant1.owl#category",
					"http://www.okkam.org/ontology_restaurant2.owl#category",
					"http://www.okkam.org/ontology_restaurant1.owl#has_category",
					"http://www.okkam.org/ontology_restaurant2.owl#has_category",
					"http://www.okkam.org/ontology_restaurant1.owl#has_address",
					"http://www.okkam.org/ontology_restaurant2.owl#has_address",
					//"http://www.okkam.org/ontology_restaurant1.owl#name",
					//"http://www.okkam.org/ontology_restaurant2.owl#name",
					"price",
					"description",
					"title",
					//"name",
					"manufacturer"
					);
			//System.out.println("BEFORE: "+ sourceCache.size());
			sourceCache = cleanNumericProperties(sourceCache,numericProperties);
			//	System.out.println("AFTER: "+ sourceCache.size());
			ACache targetCache = eval.getTargetCache();
			targetCache = cleanNumericProperties(targetCache,numericProperties);
			AMapping referenceMapping = eval.getReferenceMapping();
			AMapping trainingMapping = eval.getReferenceMapping();

			AMapping fixed=trainingMap(trainingMapping);
			//trainingMap.setSize(50);
			//System.out.println("subMap: "+fixed.getSize());
			//System.out.println("Dataset, F, R, P");
			AMapping wombatMapping_ph=wombatUnsupervised_ph(sourceCache, targetCache);

			//AMapping wombatMapping=wombatSupervisedBatch(fixed,sourceCache, targetCache);
			double f=fmeausre.calculate(wombatMapping_ph, new GoldStandard(referenceMapping));
			double r=fmeausre.recall(wombatMapping_ph, new GoldStandard(referenceMapping));
			double p=fmeausre.precision(wombatMapping_ph, new GoldStandard(referenceMapping));
			AMapping wombatMapping_h=wombatUnsupervised_h(sourceCache, targetCache);

			//	AMapping wombatMapping=wombatSupervisedBatch(fixed,sourceCache, targetCache);
			double f_h=fmeausre.calculate(wombatMapping_h, new GoldStandard(referenceMapping));
			double r_h=fmeausre.recall(wombatMapping_h, new GoldStandard(referenceMapping));
			double p_h=fmeausre.precision(wombatMapping_h, new GoldStandard(referenceMapping));
			AMapping wombatMapping_s=wombatUnsupervised_s(sourceCache, targetCache);

			//AMapping wombatMapping=wombatSupervisedBatch(fixed,sourceCache, targetCache);
			double f_s=fmeausre.calculate(wombatMapping_s, new GoldStandard(referenceMapping));
			double r_s=fmeausre.recall(wombatMapping_s, new GoldStandard(referenceMapping));
			double p_s=fmeausre.precision(wombatMapping_s, new GoldStandard(referenceMapping));

			System.out.println(d+","+f+ ", " +r+ ", "+p);

			//System.out.println("HYBRID");
			System.out.println(d+","+f_h+ ", " +r_h+ ", "+p_h);

			//System.out.println("String");
			System.out.println(d+", "+f_s+ ", " +r_s+ ", "+p_s);

		} }
*/
		/*

		// phonetic similarity algorithms 
		EvaluationData eval = DataSetChooser.getData("DBLPSCHOLAR");
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
		//for(int i=0;i<theta.length;++i) {
		AMapping caverphone1Mapping= caverphone1.getMapping(sourceCache, targetCache,"?x", "?y", "caverphone1"+PROPERTIES, 1.0);
		//	logger.info("size: " + caverphone1Mapping.size());
		double fCaverphone1=fmeausre.calculate(caverphone1Mapping, new GoldStandard(eval.getReferenceMapping()));
		double rCaverphone1=fmeausre.recall(caverphone1Mapping, new GoldStandard(eval.getReferenceMapping()));
		double pCaverphone1=fmeausre.precision(caverphone1Mapping, new GoldStandard(eval.getReferenceMapping()));
		System.out.println(", Caverphone1"+", "+ fCaverphone1+ ", "+rCaverphone1 +", "+ pCaverphone1);
		AMapping caverphone2Mapping= caverphone2.getMapping(sourceCache, targetCache,"?x", "?y", "caverphone2"+PROPERTIES, 1.0);
		//	logger.info("size: " + caverphone2Mapping.size());
		double fCaverphone2=fmeausre.calculate(caverphone2Mapping, new GoldStandard(eval.getReferenceMapping()));
		double rCaverphone2=fmeausre.recall(caverphone2Mapping, new GoldStandard(eval.getReferenceMapping()));
		double pCaverphone2=fmeausre.precision(caverphone2Mapping, new GoldStandard(eval.getReferenceMapping()));
		System.out.println(",Caverphone2"+", "+ fCaverphone2+ ", "+rCaverphone2 +", "+ pCaverphone2);
		AMapping metaphoneMapping= metaphone.getMapping(sourceCache, targetCache,"?x", "?y", "metaphone"+PROPERTIES,1.0);
		//	logger.info("size: " + metaphoneMapping.size());
		double fMetaphone=fmeausre.calculate(metaphoneMapping, new GoldStandard(eval.getReferenceMapping()));
		double rMetaphone=fmeausre.recall(metaphoneMapping, new GoldStandard(eval.getReferenceMapping()));
		double pMetaphone=fmeausre.precision(metaphoneMapping, new GoldStandard(eval.getReferenceMapping()));
		System.out.println(",Metaphone"+", "+ fMetaphone+ ", "+rMetaphone +", "+  pMetaphone);
		AMapping doublmetaphoneMapping= doublemetaphone.getMapping(sourceCache, targetCache,"?x", "?y", "doublemetaphone"+PROPERTIES, 1.0);
		//	logger.info("size: " + doublmetaphoneMapping.size());
		double fDMetaphone=fmeausre.calculate(doublmetaphoneMapping, new GoldStandard(eval.getReferenceMapping()));
		double rDMetaphone=fmeausre.recall(doublmetaphoneMapping, new GoldStandard(eval.getReferenceMapping()));
		double pDMetaphone=fmeausre.precision(doublmetaphoneMapping, new GoldStandard(eval.getReferenceMapping()));
		System.out.println(",DoublMetaphone"+", "+ fDMetaphone+ ", "+rDMetaphone +", "+  pDMetaphone);
		AMapping soundexMapping= soundex.getMapping(sourceCache, targetCache,"?x", "?y", "soundex"+PROPERTIES, 1.0);
		//	logger.info("size: " + soundexMapping.size());
		double fSoundex=fmeausre.calculate(soundexMapping, new GoldStandard(eval.getReferenceMapping()));
		double rSoundex=fmeausre.recall(soundexMapping, new GoldStandard(eval.getReferenceMapping()));
		double pSoundex=fmeausre.precision(soundexMapping, new GoldStandard(eval.getReferenceMapping()));
		System.out.println(",Soundex"+", "+ fSoundex+ ", "+rSoundex +", "+  pSoundex);
		AMapping refinedsoundexMapping= refinedsoundex.getMapping(sourceCache, targetCache,"?x", "?y", "refinedsoundex"+PROPERTIES, 1.0);
		//	logger.info("size: " + refinedsoundexMapping.size());

		double fRSoundex=fmeausre.calculate(refinedsoundexMapping, new GoldStandard(eval.getReferenceMapping()));
		double rRSoundex=fmeausre.recall(refinedsoundexMapping, new GoldStandard(eval.getReferenceMapping()));
		double pRSoundex=fmeausre.precision(refinedsoundexMapping, new GoldStandard(eval.getReferenceMapping()));
		System.out.println(",RSoundex"+", "+ fRSoundex+ ", "+rRSoundex +", "+  pRSoundex);

		AMapping koelnphoneticMapping= koelnphonetic.getMapping(sourceCache, targetCache,"?x", "?y", "koeln"+PROPERTIES, 1.0);
		//	logger.info("size: " + sockoelnphoneticMapping.size());
		double fKoeln=fmeausre.calculate(koelnphoneticMapping, new GoldStandard(eval.getReferenceMapping()));
		double rKoeln=fmeausre.recall(koelnphoneticMapping, new GoldStandard(eval.getReferenceMapping()));
		double pKoeln=fmeausre.precision(koelnphoneticMapping, new GoldStandard(eval.getReferenceMapping()));
		System.out.println(",Koeln"+", "+ fKoeln+ ", "+rKoeln +", "+  pKoeln);

		AMapping nysiisMapping= nysiis.getMapping(sourceCache, targetCache,"?x", "?y", "nysiis"+PROPERTIES, 1.0);
		//	logger.info("size: " + nysiisMapping.size());
		double fNysiis=fmeausre.calculate(nysiisMapping, new GoldStandard(eval.getReferenceMapping()));
		double rNysiis=fmeausre.recall(nysiisMapping, new GoldStandard(eval.getReferenceMapping()));
		double pNysiis=fmeausre.precision(nysiisMapping, new GoldStandard(eval.getReferenceMapping()));
		System.out.println(",Nysiis"+", "+ fNysiis+ ", "+rNysiis +", "+  pNysiis);
		AMapping daitchmkotoffMapping= daitchmkotoff.getMapping(sourceCache, targetCache,"?x", "?y", "daitchmokotoff"+PROPERTIES,1.0);
		//	logger.info("size: " + daitchmkotoffMapping.size());

		double fDaitch=fmeausre.calculate(daitchmkotoffMapping, new GoldStandard(eval.getReferenceMapping()));
		double rDaitch=fmeausre.recall(daitchmkotoffMapping, new GoldStandard(eval.getReferenceMapping()));
		double pDaitch=fmeausre.precision(daitchmkotoffMapping, new GoldStandard(eval.getReferenceMapping()));
		System.out.println(",Daitch"+", "+ fDaitch+ ", "+rDaitch +", "+  pDaitch);

		AMapping matchratingMapping= matchrating.getMapping(sourceCache, targetCache,"?x", "?y", "matchrating"+PROPERTIES, 1.0);
		//	logger.info("size: " + matchratingMapping.size());
		double fMatch=fmeausre.calculate(matchratingMapping, new GoldStandard(eval.getReferenceMapping()));
		double rMatch=fmeausre.recall(matchratingMapping, new GoldStandard(eval.getReferenceMapping()));
		double pMatch=fmeausre.precision(matchratingMapping, new GoldStandard(eval.getReferenceMapping()));
		System.out.println(",Match"+", "+ fMatch+ ", "+rMatch +", "+  pMatch);

		// string similarity algorithms 


		AMapping cosineMapping =ppjpp.getMapping(sourceCache, targetCache, "?x", "?y", "cosine"+PROPERTIES, 1.0);
		double fCosine=fmeausre.calculate(cosineMapping, new GoldStandard(eval.getReferenceMapping()));
		double rCosine=fmeausre.recall(cosineMapping, new GoldStandard(eval.getReferenceMapping()));
		double pCosine=fmeausre.precision(cosineMapping, new GoldStandard(eval.getReferenceMapping()));
		System.out.println(",Cosine"+", "+ fCosine+ ", "+rCosine +", "+  pCosine);

		AMapping overlapMapping =ppjpp.getMapping(sourceCache, targetCache, "?x", "?y", "overlap"+PROPERTIES, 1.0);
		double fOverlap=fmeausre.calculate(overlapMapping, new GoldStandard(eval.getReferenceMapping()));
		double rOverlap=fmeausre.recall(overlapMapping, new GoldStandard(eval.getReferenceMapping()));
		double pOverlap=fmeausre.precision(overlapMapping, new GoldStandard(eval.getReferenceMapping()));
		System.out.println(",Overlap"+", "+ fOverlap+ ", "+rOverlap +", "+  pOverlap);

		AMapping trigramMapping =ppjpp.getMapping(sourceCache, targetCache, "?x", "?y", "trigram"+PROPERTIES, 1.0);
		double fTrigram=fmeausre.calculate(trigramMapping, new GoldStandard(eval.getReferenceMapping()));
		double rTrigram=fmeausre.recall(trigramMapping, new GoldStandard(eval.getReferenceMapping()));
		double pTrigram=fmeausre.precision(trigramMapping, new GoldStandard(eval.getReferenceMapping()));
		System.out.println(",Trigram"+", "+ fTrigram+ ", "+rTrigram +", "+  pTrigram);

		AMapping jaccardMapping =ppjpp.getMapping(sourceCache, targetCache, "?x", "?y", "jaccard"+PROPERTIES, 1.0);
		double fJaccard=fmeausre.calculate(jaccardMapping, new GoldStandard(eval.getReferenceMapping()));
		double rJaccard=fmeausre.recall(jaccardMapping, new GoldStandard(eval.getReferenceMapping()));
		double pJaccard=fmeausre.precision(jaccardMapping, new GoldStandard(eval.getReferenceMapping()));
		System.out.println(",Jaccard"+", "+ fJaccard+ ", "+rJaccard +", "+  pJaccard);

		AMapping levenshteinMapping= edj.getMapping(sourceCache, targetCache, "?x", "?y", "levenshtein"+PROPERTIES,1.0);
		double fLevenshtein=fmeausre.calculate(levenshteinMapping, new GoldStandard(eval.getReferenceMapping()));
		double rLevenshtein=fmeausre.recall(levenshteinMapping, new GoldStandard(eval.getReferenceMapping()));
		double pLevenshtein=fmeausre.precision(levenshteinMapping, new GoldStandard(eval.getReferenceMapping()));
		System.out.println(",Levenshtein"+", "+ fLevenshtein+ ", "+rLevenshtein +", "+  pLevenshtein);

		AMapping qgramsMapping=fng.getMapping(sourceCache, targetCache, "?x", "?y", "qgrams"+PROPERTIES,1.0);
		double fQgram=fmeausre.calculate(qgramsMapping, new GoldStandard(eval.getReferenceMapping()));
		double rQgram=fmeausre.recall(qgramsMapping, new GoldStandard(eval.getReferenceMapping()));
		double pQgram=fmeausre.precision(qgramsMapping, new GoldStandard(eval.getReferenceMapping()));
		System.out.println(",Qgram"+", "+ fQgram+ ", "+rQgram +", "+  pQgram);

		// TODO Auto-generated method stub


	}
	//	 */
		/*

		public static ACache cleanNumericProperties(ACache inputCache, List<String> numericProperties) {
			ACache noNumericPropertiesCache = new MemoryCache();
			for( Instance instance : inputCache.getAllInstances()) { 
				Set<String> allProperties = instance.getAllProperties();
				for(String p : allProperties) { //System.out.printhttp://www.okkam.org/ontology_person2.owl#has_addressln("---------------> " +p);
					if(!numericProperties.contains(p)) {
						for(String o : instance.getProperty(p)) {
							noNumericPropertiesCache.addTriple(instance.getUri(), p, o);
						}
					}
				}
			}
			return noNumericPropertiesCache;
		}

		public static AMapping wombatUnsupervised_ph(ACache sc,  ACache tc) throws UnsupportedMLImplementationException {




			UnsupervisedMLAlgorithm wombatSimpleU = null;
			//UnsupervisedMLAlgorithm wombatSimpleU = null;
			try {
				wombatSimpleU = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.UNSUPERVISED).asUnsupervised();
			} catch (UnsupportedMLImplementationException e) {
				e.printStackTrace();
				fail();
			}
			//  wombatSimpleU.init(null, sc, tc);

			Set<String> measures = new HashSet<>(Arrays.asList("soundex","doublemeta", "koeln", "meta", "nysiis", "caverphone1", "caverphone2", "refinedsoundex", "matchrating", "daitchmokotoff"));
			//Set<String> measures = new HashSet<>(Arrays.asList("jaccard", "trigrams", "cosine", "qgrams","soundex","doublemeta", "koeln", "meta", "nysiis", "caverphone1", "caverphone2", "refinedsoundex", "matchrating", "daitchmokotoff"));

			List<LearningParameter> lps=new ArrayList<LearningParameter>();
			LearningParameter lp =  new LearningParameter(AWombat.PARAMETER_ATOMIC_MEASURES, measures, MeasureType.class, 0, 0, 0, AWombat.PARAMETER_ATOMIC_MEASURES);
			lps.add(lp);

			//   lp.add(new LearningParameter("atomic measures", measures, MeasureType.class, 0, 0, 0, AWombat.PARAMETER_ATOMIC_MEASURES));
			wombatSimpleU.setParameter(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER, 2);
			wombatSimpleU.setParameter(AWombat.PARAMETER_MAX_REFINEMENT_TREE_SIZE, 1000);
			wombatSimpleU.init(lps, sc, tc);
			
			MLResults mlModel = wombatSimpleU.learn(new PseudoFMeasure());
			System.out.println("phonatic ls is "+mlModel.getLinkSpecification().getFullExpression());
			AMapping resultMap = wombatSimpleU.predict(sc, tc, mlModel);
			return resultMap ;
		}

		public static AMapping wombatUnsupervised_h(ACache sc,  ACache tc) throws UnsupportedMLImplementationException {




			UnsupervisedMLAlgorithm wombatSimpleU = null;
			//UnsupervisedMLAlgorithm wombatSimpleU = null;
			try {
				wombatSimpleU = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.UNSUPERVISED).asUnsupervised();
			} catch (UnsupportedMLImplementationException e) {
				e.printStackTrace();
				fail();
			}
			//  wombatSimpleU.init(null, sc, tc);

			//Set<String> measures = new HashSet<>(Arrays.asList("soundex","doublemeta", "koeln", "meta", "nysiis", "caverphone1", "caverphone2", "refinedsoundex", "matchrating", "daitchmokotoff"));
			Set<String> measures = new HashSet<>(Arrays.asList("jaccard", "trigrams", "cosine", "qgrams","soundex","doublemeta", "koeln", "meta", "nysiis", "caverphone1", "caverphone2", "refinedsoundex", "matchrating", "daitchmokotoff"));

			List<LearningParameter> lps=new ArrayList<LearningParameter>();
			LearningParameter lp =  new LearningParameter(AWombat.PARAMETER_ATOMIC_MEASURES, measures, MeasureType.class, 0, 0, 0, AWombat.PARAMETER_ATOMIC_MEASURES);
			lps.add(lp);

			//   lp.add(new LearningParameter("atomic measures", measures, MeasureType.class, 0, 0, 0, AWombat.PARAMETER_ATOMIC_MEASURES));
			wombatSimpleU.setParameter(AWombat.PARAMETER_MAX_REFINEMENT_TREE_SIZE, 1000);
			wombatSimpleU.setParameter(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER, 2);
			wombatSimpleU.init(lps, sc, tc);
			MLResults mlModel = wombatSimpleU.learn(new PseudoFMeasure());
			System.out.println("hybrid ls is "+mlModel.getLinkSpecification().getFullExpression());
			AMapping resultMap = wombatSimpleU.predict(sc, tc, mlModel);
			return resultMap ;
		}

		public static AMapping wombatUnsupervised_s(ACache sc,  ACache tc) throws UnsupportedMLImplementationException {




			UnsupervisedMLAlgorithm wombatSimpleU = null;
			//UnsupervisedMLAlgorithm wombatSimpleU = null;
			try {
				wombatSimpleU = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.UNSUPERVISED).asUnsupervised();
			} catch (UnsupportedMLImplementationException e) {
				e.printStackTrace();
				fail();
			}
			//  wombatSimpleU.init(null, sc, tc);

			//Set<String> measures = new HashSet<>(Arrays.asList("soundex","doublemeta", "koeln", "meta", "nysiis", "caverphone1", "caverphone2", "refinedsoundex", "matchrating", "daitchmokotoff"));
			//Set<String> measures = new HashSet<>(Arrays.asList("jaccard", "trigrams", "cosine", "qgrams","soundex","doublemeta", "koeln", "meta", "nysiis", "caverphone1", "caverphone2", "refinedsoundex", "matchrating", "daitchmokotoff"));
			Set<String> measures = new HashSet<>(Arrays.asList("jaccard", "trigrams", "cosine", "qgrams"));

			List<LearningParameter> lps=new ArrayList<LearningParameter>();
			LearningParameter lp =  new LearningParameter(AWombat.PARAMETER_ATOMIC_MEASURES, measures, MeasureType.class, 0, 0, 0, AWombat.PARAMETER_ATOMIC_MEASURES);
			lps.add(lp);

			//   lp.add(new LearningParameter("atomic measures", measures, MeasureType.class, 0, 0, 0, AWombat.PARAMETER_ATOMIC_MEASURES));
			wombatSimpleU.setParameter(AWombat.PARAMETER_MAX_REFINEMENT_TREE_SIZE, 1000);
			wombatSimpleU.setParameter(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER, 2);
			wombatSimpleU.init(lps, sc, tc);
			MLResults mlModel = wombatSimpleU.learn(new PseudoFMeasure());
			System.out.println("String ls "+mlModel.getLinkSpecification().getFullExpression());
			AMapping resultMap = wombatSimpleU.predict(sc, tc, mlModel);
			return resultMap ;
		}




		public static AMapping wombatSupervisedBatch(AMapping trainingMap,ACache sc,  ACache tc) throws UnsupportedMLImplementationException {
			SupervisedMLAlgorithm wombatSimple = null;

			try {
				wombatSimple = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
						MLImplementationType.SUPERVISED_BATCH).asSupervised();
			} catch (UnsupportedMLImplementationException e) {
				e.printStackTrace();
				fail();
			}
			Set<String> measures = new HashSet<>(Arrays.asList("jaccard","soundex","doublemeta", "koeln", "meta", "nysiis", "caverphone1", "caverphone2", "refinedsoundex", "matchrating", "daitchmokotoff"));

			List<LearningParameter> lps=new ArrayList<LearningParameter>();
			LearningParameter lp =  new LearningParameter(AWombat.PARAMETER_ATOMIC_MEASURES, measures, MeasureType.class, 0, 0, 0, AWombat.PARAMETER_ATOMIC_MEASURES);
			lps.add(lp);
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_REFINEMENT_TREE_SIZE, 1000);
			wombatSimple.setParameter(AWombat.PARAMETER_MAX_ITERATIONS_NUMBER, 2);
			wombatSimple.init(lps, sc, tc);
			MLResults mlModel = wombatSimple.learn( trainingMap);
			System.out.println("link spec. is "+mlModel.getLinkSpecification().getFullExpression());
			AMapping resultMap = wombatSimple.predict(sc, tc, mlModel);
			return resultMap; 
		}

		public static AMapping trainingMap(AMapping trainingMap) {
			AMapping fixed = MappingFactory.createMapping(MappingType.MEMORY_MAPPING);


			for (String sk : trainingMap.getMap().keySet()) {

				for (String tk : trainingMap.getMap().get(sk).keySet()) {
					if(fixed.getSize()<(trainingMap.getSize())/30)
						fixed.add(sk, tk, trainingMap.getConfidence(sk, tk));
				}
			}


			return fixed;
		}
		//	public static AMapping subLinkMap(LinkSpecification ls, ACache sCache, ACache tCache) {
		//		Rewriter rw = RewriterFactory.getDefaultRewriter();
		//		LinkSpecification rwLs = rw.rewrite(ls);
		//		IPlanner planner = ExecutionPlannerFactory.getPlanner(ExecutionPlannerType.DEFAULT, sCache, tCache);
		//	
		//		ExecutionEngine engine = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sCache, tCache, "?x" , "?y");
		//	
		//		AMapping resultMap = engine.execute(rwLs, planner);
		//		//map = resultMap.getSubMap(ls.getThreshold());
		//		return resultMap;
		//	}


	}
*/