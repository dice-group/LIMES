import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.measures.measure.phoneticmeasure.SoundexMeasure;
import org.apache.commons.codec.language.Soundex;
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
	public static void main(String[] args) {

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
		Soundex so = new Soundex();

		SoundexMeasure soundMeausre=new SoundexMeasure();
		String code= SoundexMeasure.getCode("abcdellah");
		double sim=soundMeausre.proximity("abcdellah", "abdullah");
		System.out.println("1 "+code);
		System.out.println("sim "+sim);
		System.out.println("2 "+so.encode("abdullah"));
		// TODO Auto-generated method stub

	}

}
