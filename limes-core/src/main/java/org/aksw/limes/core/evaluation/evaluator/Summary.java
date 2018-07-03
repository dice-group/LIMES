package org.aksw.limes.core.evaluation.evaluator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.datastrutures.EvaluationRun;
import org.apache.commons.math3.util.Pair;

import de.vandermeer.asciitable.AsciiTable;

public class Summary {

	private List<EvaluationRun> singleRuns;
	private List<EvaluationRun> averagedRuns;
	private Map<String, Map<String, Double>> statisticalTestResults;
	private List<String> usedDatasets = new ArrayList<>();
	private List<String> usedAlgorithms = new ArrayList<>();
	public static final int PRECISION = 2;

	public Summary(List<EvaluationRun> singleRuns, int runsPerDataSet) {
		this.singleRuns = singleRuns;
		averagedRuns = calculateAvgRuns(runsPerDataSet);
	}

	public List<EvaluationRun> calculateAvgRuns(final int runsPerDataSet) {
		Map<String, Map<String, EvaluationRun>> algoDataRunMap = new HashMap<>();
		for (EvaluationRun e : singleRuns) {
			String algo = e.getAlgorithmName();
			String dataSet = e.getDatasetName();
			if (!algoDataRunMap.containsKey(algo)) {
				Map<String, EvaluationRun> dataRunMap = new HashMap<>();
				algoDataRunMap.put(algo, dataRunMap);
			}
			EvaluationRun eRun = algoDataRunMap.get(algo).get(dataSet);
			Map<String, EvaluationRun> dataRunMap = algoDataRunMap.get(algo);
			if (eRun != null) {
				for (EvaluatorType measureType : e.qualititativeScores.keySet()) {
					eRun.qualititativeScores.put(measureType,
							eRun.qualititativeScores.get(measureType) + e.qualititativeScores.get(measureType));
				}
			} else {
				eRun = e;
			}
			dataRunMap.put(dataSet, eRun);
			algoDataRunMap.put(algo, dataRunMap);
			if (!usedDatasets.contains(dataSet)) {
				usedDatasets.add(dataSet);
			}
			if (!usedAlgorithms.contains(algo)) {
				usedAlgorithms.add(algo);
			}
		}
		// Calculate mean
		algoDataRunMap.forEach((algo, map) -> {
			map.forEach((data, eRun) -> {
				for (EvaluatorType measureType : eRun.qualititativeScores.keySet()) {
					eRun.qualititativeScores.put(measureType,
							eRun.qualititativeScores.get(measureType) / runsPerDataSet);
				}
			});
		});
		List<EvaluationRun> result = new ArrayList<>();
		// Calculate variance
		for (EvaluationRun e : singleRuns) {
			for (EvaluatorType eType : e.qualititativeScores.keySet()) {
				EvaluationRun averagedRun = algoDataRunMap.get(e.getAlgorithmName()).get(e.getDatasetName());
				double squaredDifference = Math
						.pow(e.qualititativeScores.get(eType) - averagedRun.qualititativeScores.get(eType), 2);
				if (averagedRun.qualititativeScoresWithVariance.get(eType) != null) {
					Pair<Double, Double> valueVariance = averagedRun.qualititativeScoresWithVariance.get(eType);
					averagedRun.qualititativeScoresWithVariance.put(eType, new Pair<Double, Double>(
							valueVariance.getFirst(), squaredDifference + valueVariance.getSecond()));
				} else {
					averagedRun.qualititativeScoresWithVariance.put(eType,
							new Pair<Double, Double>(averagedRun.qualititativeScores.get(eType), squaredDifference));
				}
			}
		}
		algoDataRunMap.forEach((algo, map) -> {
			map.forEach((data, eRun) -> {
				for (EvaluatorType eType : eRun.qualititativeScores.keySet()) {
					Pair<Double, Double> old = eRun.qualititativeScoresWithVariance.get(eType);
					eRun.qualititativeScoresWithVariance.put(eType,
							new Pair<Double, Double>(old.getFirst(), old.getSecond() / runsPerDataSet));
				}
				result.add(eRun);
			});
		});
		return result;
	}

	@Override
	public String toString() {
		StringBuilder overall = new StringBuilder();
		overall.append(" ========= QUALITATIVE MEASURES ========\n");
		averagedRuns.sort((e1, e2) -> {
			if (e1.getAlgorithmName().equals(e2.getAlgorithmName())) {
				return e1.getDatasetName().compareTo(e2.getDatasetName());
			}
			return e1.getAlgorithmName().compareTo(e2.getAlgorithmName());
		});
		Collections.sort(usedDatasets);
		usedDatasets.add(0, "");
		AsciiTable at = new AsciiTable();
		at.addRule();
		at.addRow(usedDatasets);
		at.addRule();
		String currentAlgo = averagedRuns.get(0).getAlgorithmName();
		List<String> currentRow = new ArrayList<>();
		currentRow.add(currentAlgo);
		for (EvaluationRun er : averagedRuns) {
			if (!er.getAlgorithmName().equals(currentAlgo)) {
				at.addRow(currentRow);
				at.addRule();
				currentRow = new ArrayList<>();
				currentRow.add(er.getAlgorithmName());
				currentAlgo = er.getAlgorithmName();
			}
			StringBuilder cell = new StringBuilder();
			for (EvaluatorType eType : er.qualititativeScores.keySet()) {
				Pair<Double, Double> valueVariance = er.qualititativeScoresWithVariance.get(eType);
				cell.append(eType).append(": ").append(round(valueVariance.getFirst())).append(" (")
						.append(round(valueVariance.getSecond())).append(")").append("\n");
			}
			currentRow.add(cell.toString());
		}
		at.addRow(currentRow);
		at.addRule();
		overall.append(at.render());
		if (statisticalTestResults == null || statisticalTestResults.size() == 0) {
			return overall.toString();
		}
		overall.append("\n ========= STATISTICAL TEST RESULTS ========\n");
		at = new AsciiTable();
		at.addRule();

		List<String> header = new ArrayList<>();
		header.addAll(usedAlgorithms);
		header.add(0, "");
		at.addRow(header);
		at.addRule();
		currentRow = new ArrayList<>();
		for (String a : header) {
			if (!a.equals("")) {
				currentRow.add(a);
				for (String b : header) {
					if (!a.equals(b)) {
					if (statisticalTestResults.get(a) == null) {
						currentRow.add("-");
					} else {
						Double value = statisticalTestResults.get(a).get(b);
						if (value == null) {
							currentRow.add("-");
						} else {
							currentRow.add(value.toString());
						}
					}
					}
				}
				at.addRow(currentRow);
				currentRow = new ArrayList<>();
			}
		}
		at.addRule();
		overall.append(at.render());
		return overall.toString();
	}

	public String round(double d) {
		BigDecimal.valueOf(d).round(new java.math.MathContext(PRECISION, RoundingMode.HALF_UP));
		DecimalFormat twoDForm = new DecimalFormat("0." + new String(new char[PRECISION]).replace("\0", "0"));
		return twoDForm.format(d);
	}

	public List<EvaluationRun> getSingleRuns() {
		return singleRuns;
	}

	public List<EvaluationRun> getAveragedRuns() {
		return averagedRuns;
	}

	public Map<String, Map<String, Double>> getStatisticalTestResults() {
		return statisticalTestResults;
	}

	public void setStatisticalTestResults(Map<String, Map<String, Double>> statisticalTestResults) {
		this.statisticalTestResults = statisticalTestResults;
	}

}
