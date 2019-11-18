package org.aksw.limes.core.io.preprocessing.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.APreprocessingFunction;
import org.aksw.limes.core.io.preprocessing.IPreprocessingFunction;
import org.apache.commons.lang.ArrayUtils;

public class Concat extends APreprocessingFunction implements IPreprocessingFunction {
	public static final String GLUE_KEYWORD = "glue=";
	private String resultProperty;
	private String glue;

	@Override
	public Instance applyFunctionAfterCheck(Instance inst, String property, String... arguments) {
		resultProperty = property;
		glue = retrieveKeywordArgumentValue(arguments[arguments.length-1], GLUE_KEYWORD);
		if (!glue.equals("")) {
			//Remaining arguments are the properties that will be concatenated
			arguments = (String[]) ArrayUtils.removeElement(arguments, arguments[arguments.length-1]);
		}
		ArrayList<ArrayList<String>> oldValues = new ArrayList<>();
		for (String prop : arguments) {
			ArrayList<String> treeValues = new ArrayList<>();
			inst.getProperty(prop.trim()).forEach(e -> treeValues.add(e));
			oldValues.add(treeValues);
		}
		ArrayList<String> newValues = concatElementsInOrder(oldValues, glue);
		inst.addProperty(resultProperty, new TreeSet<String>(newValues));
		return inst;

	}

	public static ArrayList<String> concatElementsInOrder(ArrayList<ArrayList<String>> toConcatList, String... glue) {
		ArrayList<String> res = toConcatList.get(0);
		toConcatList.remove(0);
		for (ArrayList<String> toConcat : toConcatList) {
			res = concatArrayElements(res, toConcat, glue);
		}
		return res;
	}

	public static ArrayList<String> concatArrayElements(ArrayList<String> first, ArrayList<String> toConcat,
			String... glue) {
		if(toConcat.isEmpty()){
			return first;
		}
		ArrayList<String> res = new ArrayList<>();
		for (String firstPart : first) {
			res.addAll(concatStringToElements(firstPart, toConcat, glue));
		}
		return res;
	}

	public static ArrayList<String> concatStringToElements(String firstPart, ArrayList<String> toConcat,
			String... glue) {
		ArrayList<String> res = new ArrayList<>();
		for (String secondPart : toConcat) {
			if (glue.length > 0) {
				res.add(firstPart + glue[0] + secondPart);
			} else {
				res.add(firstPart + secondPart);
			}
		}
		return res;
	}
	
	@Override
	public int minNumberOfArguments() {
		return 2;
	}

	@Override
	public int maxNumberOfArguments() {
		return -1;
	}

    public boolean isComplex() {
        return true;
    }

}
