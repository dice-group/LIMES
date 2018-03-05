package org.aksw.limes.core.io.preprocessing.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.APreprocessingFunction;
import org.aksw.limes.core.io.preprocessing.IPreprocessingFunction;
import org.apache.commons.lang.ArrayUtils;

public class Concat extends APreprocessingFunction implements IPreprocessingFunction {
	public static final String GLUE_FLAG = "glue=";
	private String resultProperty;
	private String glue;

	@Override
	public Instance applyFunctionAfterCheck(Instance inst, String property, String... arguments) {
		resultProperty = property;
		
		//glue flag might have leading white spaces, trailing should NOT be removed, as they could be the glue
		glue = arguments[arguments.length-1].replaceAll("^\\s+","");
		//Check if glue flag is used
		if (glue.startsWith(GLUE_FLAG)) {
			glue = glue.replace(GLUE_FLAG, "");
			arguments = (String[]) ArrayUtils.removeElement(arguments, arguments[arguments.length-1]);
		} else { //Else don't use glue
			glue = "";
		}
		ArrayList<ArrayList<String>> oldValues = new ArrayList<>();
		for (String prop : arguments) {
			ArrayList<String> treeValues = new ArrayList<>();
			inst.getProperty(prop).forEach(e -> treeValues.add(e));
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
	public String[] retrieveArguments(String args) {
		String[] res = super.retrieveArguments(args);
		if(args.contains(GLUE_FLAG)){
			String glue = args.substring(args.indexOf(GLUE_FLAG), args.length()); 
			res[res.length -1] = glue;
			return res;
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

}
