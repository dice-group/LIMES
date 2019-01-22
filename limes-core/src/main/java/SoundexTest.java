import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser.DataSets;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.string.PPJoinPlusPlus;
import org.aksw.limes.core.measures.measure.phoneticmeasure.SoundexMeasure;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.Caverphone1;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.codec.language.Nysiis;
import org.apache.commons.codec.language.bm.BeiderMorseEncoder;
public class SoundexTest {

	static Map<String, List<Integer>> getInvertedList(List<String> list) {
		Map<String, List<Integer>> result = new HashMap<>(list.size());
		for (int i = 0, listASize = list.size(); i < listASize; i++) {
			String s = list.get(i);
			String code = SoundexMeasure.getCode(s);
			List<Integer> ref;
			if (!result.containsKey(code)) {
				ref = new LinkedList<>();
				result.put(code, ref);
			} else {
				ref = result.get(code);
			}
			ref.add(i);
		}
		return result;
	}
	public static void main(String[] args) throws EncoderException {
		
		EvaluationData eval = DataSetChooser.getData(DataSets.ABTBUY);
PPJoinPlusPlus pp= new PPJoinPlusPlus();
AMapping mapping = pp.getMapping(eval.getSourceCache(), eval.getTargetCache(), "x", "y", "cosine(x.name,y.name)", 1.0);

System.out.println("MAPPING is "+mapping.size());
		List<String> list=new ArrayList<>();
		list.add("abdullah");
		list.add("abdallah");
		list.add("abdolah");
		list.add("ahmed");
		list.add("Muhammed");
		list.add("Mohamed");
		Map<String, List<Integer>> invListA;
		invListA = getInvertedList(list);
		System.out.println(invListA.values());
		Caverphone1 co = new Caverphone1();
		Nysiis ny = new Nysiis();
		BeiderMorseEncoder bm =new BeiderMorseEncoder();
       DoubleMetaphone d =new DoubleMetaphone();
		SoundexMeasure soundMeausre=new SoundexMeasure();
		String code= SoundexMeasure.getCode("abcdellah");
		double sim=soundMeausre.proximity("abcdellah", "abdullah");
		System.out.println("1 "+code);
		System.out.println("sim "+sim);
		bm.setMaxPhonemes(1);
		System.out.println("2 "+bm.encode("generation"));
		// TODO Auto-generated method stub

	}

}
