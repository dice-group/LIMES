package org.aksw.limes.core.measures.mapper.phonetic;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser.DataSets;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.string.EDJoinMapper;
import org.aksw.limes.core.measures.mapper.string.PPJoinPlusPlus;
import org.aksw.limes.core.measures.mapper.string.fastngram.FastNGramMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhoneticStringMeasureEvaluation {

    private static final Logger logger = LoggerFactory.getLogger(PhoneticStringMeasureEvaluation.class);

	public static void main(String[] args) {

		EvaluationData eval = DataSetChooser.getData(DataSets.DBLPACM);

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
		
		AMapping caverphone1Mapping= caverphone1.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "caverphone1(x.authors,y.authors)", 0.6);
		logger.info("size: " + caverphone1Mapping.size());
		AMapping caverphone2Mapping= caverphone2.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "caverphone2(x.authors,y.authors)", 0.6);
        logger.info("size: " + caverphone2Mapping.size());
		AMapping metaphoneMapping= metaphone.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "metaphone(x.authors,y.authors)", 0.6);
        logger.info("size: " + metaphoneMapping.size());
		AMapping doublmetaphoneMapping= doublemetaphone.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "doublemetaphone(x.authors,y.authors)", 0.6);
        logger.info("size: " + doublmetaphoneMapping.size());
		AMapping soundexMapping= soundex.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "soundex(x.authors,y.authors)", 0.6);
        logger.info("size: " + soundexMapping.size());
		AMapping refinedsoundexMapping= refinedsoundex.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "refinedsoundex(x.authors,y.authors)", 0.6);
        logger.info("size: " + refinedsoundexMapping.size());
		AMapping koelnphoneticMapping= koelnphonetic.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "koeln(x.authors,y.authors)", 0.6);
        logger.info("size: " + koelnphoneticMapping.size());
		AMapping nysiisMapping= nysiis.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "nysiis(x.authors,y.authors)", 0.6);
        logger.info("size: " + nysiisMapping.size());
		AMapping daitchmkotoffMapping= daitchmkotoff.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "daitchmokotoff(x.authors,y.authors)", 0.6);
        logger.info("size: " + daitchmkotoffMapping.size());
		AMapping matchratingMapping= matchrating.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "matchrating(x.authors,y.authors)", 0.6);
        logger.info("size: " + matchratingMapping.size());
		// string similarity algorithms 
		PPJoinPlusPlus ppjpp= new PPJoinPlusPlus();
		EDJoinMapper edj=new EDJoinMapper();
		FastNGramMapper fng=new FastNGramMapper();
		AMapping cosineMapping =ppjpp.getMapping(eval.getSourceCache(), eval.getTargetCache(), "?x", "?y", "cosine(x.authors,y.authors)", 1.0);
		AMapping overlapMapping =ppjpp.getMapping(eval.getSourceCache(), eval.getTargetCache(), "?x", "?y", "overlap(x.authors,y.authors)", 1.0);
		AMapping trigramMapping =ppjpp.getMapping(eval.getSourceCache(), eval.getTargetCache(), "?x", "?y", "trigram(x.authors,y.authors)", 1.0);
		AMapping jaccardMapping =ppjpp.getMapping(eval.getSourceCache(), eval.getTargetCache(), "?x", "?y", "jaccard(x.authors,y.authors)", 1.0);
		AMapping levenshteinMapping= edj.getMapping(eval.getSourceCache(), eval.getTargetCache(), "?x", "?y", "levenshtein(x.authors,y.authors)", 1.0);
		AMapping qgramsMapping=fng.getMapping(eval.getSourceCache(), eval.getTargetCache(), "?x", "?y", "qgrams(x.authors,y.authors)", 1.0);

		// TODO Auto-generated method stub

	}

}
