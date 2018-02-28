package org.aksw.limes.core.io.preprocessing.functions;

import java.util.ArrayList;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.preprocessing.AProcessingFunction;
import org.aksw.limes.core.io.preprocessing.IProcessingFunction;

public class Concat extends AProcessingFunction implements IProcessingFunction {
	private String resultProperty;
	private String glue;

	@Override
	public Instance applyFunction(Instance inst, String[] properties, String... arguments) {
		resultProperty = arguments[0];
		if (arguments.length > 1 && arguments[1] != null) {
			glue = arguments[1];
		} else {
			glue = "";
		}
		ArrayList<ArrayList<String>> oldValues = new ArrayList<>();
		for (String prop : properties) {
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

	public static ArrayList<String> concatArrayElements(ArrayList<String> first, ArrayList<String> toConcat, String... glue) {
		ArrayList<String> res = new ArrayList<>();
		for (String firstPart : first) {
			res.addAll(concatStringToElements(firstPart, toConcat, glue));
		}
		return res;
	}

	public static ArrayList<String> concatStringToElements(String firstPart, ArrayList<String> toConcat, String... glue) {
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
	public int minNumberOfProperties() {
		return 2;
	}

	@Override
	public int maxNumberOfProperties() {
		return -1;
	}

	@Override
	public int minNumberOfArguments() {
		return 1;
	}

	@Override
	public int maxNumberOfArguments() {
		return 2;
	}

}
