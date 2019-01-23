package org.aksw.limes.core.measures.mapper.phonetic;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser.DataSets;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.string.EDJoinMapper;
import org.aksw.limes.core.measures.mapper.string.PPJoinPlusPlus;
import org.aksw.limes.core.measures.mapper.string.fastngram.FastNGramMapper;

public class PhoneticStringMeasureEvaluation {

	public static void main(String[] args) {

		EvaluationData eval = DataSetChooser.getData(DataSets.ABTBUY);

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
		
		AMapping caverphone1Mapping= caverphone1.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "caverphone1(x.naem,y.name)", 0.6);
		AMapping caverphone2Mapping= caverphone2.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "caverphone2(x.naem,y.name)", 0.6);
		AMapping metaphoneMapping= metaphone.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "metaphone(x.naem,y.name)", 0.6);
		AMapping doublmetaphoneMapping= doublemetaphone.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "doublemetaphone(x.naem,y.name)", 0.6);
		AMapping soundexMapping= soundex.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "soundex(x.naem,y.name)", 0.6);
		AMapping refinedsoundexMapping= refinedsoundex.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "refinedsoundex(x.naem,y.name)", 0.6);
		AMapping koelnphoneticMapping= koelnphonetic.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "koeln(x.naem,y.name)", 0.6);
		AMapping nysiisMapping= nysiis.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "nysiis(x.naem,y.name)", 0.6);
		AMapping daitchmkotoffMapping= daitchmkotoff.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "daitchmokotoff(x.naem,y.name)", 0.6);
		AMapping matchratingMapping= matchrating.getMapping(eval.getSourceCache(), eval.getTargetCache(),"?x", "?y", "matchrating(x.naem,y.name)", 0.6);

		// string similarity algorithms 
		PPJoinPlusPlus ppjpp= new PPJoinPlusPlus();
		EDJoinMapper edj=new EDJoinMapper();
		FastNGramMapper fng=new FastNGramMapper();
		AMapping cosineMapping =ppjpp.getMapping(eval.getSourceCache(), eval.getTargetCache(), "?x", "?y", "cosine(x.name,y.name)", 1.0);
		AMapping overlapMapping =ppjpp.getMapping(eval.getSourceCache(), eval.getTargetCache(), "?x", "?y", "overlap(x.name,y.name)", 1.0);
		AMapping trigramMapping =ppjpp.getMapping(eval.getSourceCache(), eval.getTargetCache(), "?x", "?y", "trigram(x.name,y.name)", 1.0);
		AMapping jaccardMapping =ppjpp.getMapping(eval.getSourceCache(), eval.getTargetCache(), "?x", "?y", "jaccard(x.name,y.name)", 1.0);
		AMapping levenshteinMapping= edj.getMapping(eval.getSourceCache(), eval.getTargetCache(), "?x", "?y", "levenshtein(x.name,y.name)", 1.0);
		AMapping qgramsMapping=fng.getMapping(eval.getSourceCache(), eval.getTargetCache(), "?x", "?y", "qgrams(x.name,y.name)", 1.0);

		// TODO Auto-generated method stub

	}

}
