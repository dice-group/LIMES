import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser.DataSets;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.measures.mapper.pointsets.PropertyFetcher;
import org.aksw.limes.core.measures.measure.phoneticmeasure.DoubleMetaphoneMeasure;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.Caverphone1;
import org.apache.commons.codec.language.DaitchMokotoffSoundex;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.codec.language.MatchRatingApproachEncoder;
import org.apache.commons.codec.language.Nysiis;
import org.apache.commons.codec.language.RefinedSoundex;
import org.apache.commons.codec.language.Soundex;
import org.apache.commons.codec.language.bm.BeiderMorseEncoder;
public class SoundexTest {
	
	 static String removeVowels(String name) {
	        // Extract first letter
	        final String firstLetter = name.substring(0, 1);

	        String EMPTY="";
	        String SPACE=" ";
			name = name.replaceAll("A", EMPTY);
	        name = name.replaceAll("E", EMPTY);
	        name = name.replaceAll("I", EMPTY);
	        name = name.replaceAll("O", EMPTY);
	        name = name.replaceAll("U", EMPTY);
	        System.out.println(name);
	        name = name.replaceAll("\\s{2,}\\b", SPACE);
  System.out.println(name);
	        // return isVowel(firstLetter) ? (firstLetter + name) : name;
	        if (isVowel(firstLetter)) {
	            return firstLetter + name;
	        }
	        return name;
	    }
	 static boolean isVowel(final String letter) {
	        return letter.equalsIgnoreCase("E") || letter.equalsIgnoreCase("A") || letter.equalsIgnoreCase("O") ||
	               letter.equalsIgnoreCase("I") || letter.equalsIgnoreCase("U");
	    }

	static Map<String, Set<String>> getValueToUriMap(ACache cache, String property) {
		Map<String, Set<String>> result = new HashMap<>();
		List<String> uris = cache.getAllUris();
		for (String uri : uris) {
			Set<String> values = cache.getInstance(uri).getProperty(property);
			for (String value : values) {
				if (!result.containsKey(value)) {
					result.put(value, new HashSet<>());
				}
				result.get(value).add(uri);
			}
		}
		return result;
	}

	private static Map<String, List<Integer>> getInvertedList(List<String> list) {
		

		Map<String, List<Integer>> result = new HashMap<>(list.size());
		for (int i = 0; i <  list.size(); i++) {
			
			String s = list.get(i);



				// System.out.println("STRING: "+s);
				String code = DoubleMetaphoneMeasure.getCode(s);

				if(code.length()!=0&&code!=null) {
			//	System.out.println("CODE: "+code);
				List<Integer> ref;
				if (!result.containsKey(code)) {
					ref = new LinkedList<>();
						result.put(code, ref);
				} else {
						ref = result.get(code);
				}
				ref.add(i);
			}
		}
		return result;
	}

	public static void main(String[] args) throws EncoderException {
		
		
		String str1= removeVowels("ABDULLAH");
System.out.println("String is "+str1);
		EvaluationData eval = DataSetChooser.getData(DataSets.DBLPSCHOLAR);
		List<String> listA, listB;
		Map<String, List<Integer>> invListA, invListB;
		List<String> properties = PropertyFetcher.getProperties("doublemetaphone(x.authors,y.authors)", 1.0);
		ACache sourceCache = eval.getSourceCache();
		ACache targetCache=  eval.getTargetCache();
		Map<String, Set<String>> sourceMap = getValueToUriMap(sourceCache, properties.get(0));
		Map<String, Set<String>> targetMap = getValueToUriMap(targetCache, properties.get(1));
		
		   //listA = new ArrayList<>(sourceMap.keySet());
		listB = new ArrayList<>(targetMap.keySet());
		
		// create inverted lists (code=>index of original list)
		 // invListA = getInvertedList(listA);
		invListB = getInvertedList(listB);


				for (Entry<String, List<Integer>> entry : invListB.entrySet())
				{
					System.out.println("key"+entry.getKey() +" || "+entry.getValue() );
				}


		Caverphone1 co = new Caverphone1();
		DaitchMokotoffSoundex da= new DaitchMokotoffSoundex();
		Nysiis ny = new Nysiis();
		MatchRatingApproachEncoder match = new MatchRatingApproachEncoder();
		BeiderMorseEncoder bm =new BeiderMorseEncoder();
		DoubleMetaphone d =new DoubleMetaphone();
		Soundex s=new Soundex();
		RefinedSoundex refs=new RefinedSoundex();

		//System.out.println(co.encode("bdastsrstssd"));
		//System.out.println(da.encode("bdastsrstssd"));
		//System.out.println(ny.encode("bdastsrstssd"));
		//System.out.println(match.encode("bdastsrstssd"));
		//System.out.println(bm.encode("bdastsrstssd"));
		//System.out.println(d.encode("bdastsrstssd"));
		//System.out.println(s.encode("bdastsrstssd"));
		//System.out.println(refs.encode("bdastsrstssdwwwuiqp"));


		// TODO Auto-generated method stub

	}

}
