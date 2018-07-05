package org.aksw.limes.core.evaluation.evaluator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.aksw.limes.core.datastrutures.EvaluationRun;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vandermeer.asciitable.AsciiTable;

public class Summary {

	public static final Logger logger = LoggerFactory.getLogger(Summary.class);
	private List<EvaluationRun> singleRuns;
	private List<EvaluationRun> averagedRuns;
	private Map<String, Map<String, Map<String, Double>>> statisticalTestResults;
	private List<String> usedDatasets = new ArrayList<>();
	private List<String> usedAlgorithms = new ArrayList<>();
	private List<String> usedEvaluators = new ArrayList<>();
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
					if (!usedEvaluators.contains(measureType.toString())) {
						usedEvaluators.add(measureType.toString());
					}
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
		List<String> datasetsCopy = new ArrayList<String>(usedDatasets);
		Collections.sort(datasetsCopy);
		datasetsCopy.add(0, "");
		AsciiTable at = new AsciiTable();
		at.addRule();
		at.addRow(datasetsCopy);
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
		for (String dataSet : statisticalTestResults.keySet()) {
			overall.append("\n +++++ " + dataSet + " +++++ \n");
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
							if (statisticalTestResults.get(dataSet).get(a) == null) {
								currentRow.add("-");
							} else {
								Double value = statisticalTestResults.get(dataSet).get(a).get(b);
								if (value == null) {
									currentRow.add("-");
								} else {
									currentRow.add(round(value));
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
		}
		return overall.toString();
	}

	public String round(double d) {
		BigDecimal.valueOf(d).round(new MathContext(PRECISION, RoundingMode.HALF_UP));
		DecimalFormat twoDForm = new DecimalFormat("0." + new String(new char[PRECISION]).replace("\0", "0"));
		return twoDForm.format(d);
	}

	public void printToFiles(String dir) throws FileNotFoundException {
		// TODO Fix code duplication
		Map<Integer, Map<String, Map<String, List<EvaluationRun>>>> grouped = singleRuns.stream()
				.collect(Collectors.groupingBy(EvaluationRun::getRunInExperiment, Collectors.groupingBy(
						EvaluationRun::getAlgorithmName, Collectors.groupingBy(EvaluationRun::getDatasetName))));
		for (String eType : usedEvaluators) {
			for (Integer run : grouped.keySet()) {
				String runDir = "Run" + run;
				runDir = createDirectoriesIfNecessary(dir, runDir);
				String rows = "\t" + String.join("\t", usedDatasets) + "\n";
				for (String algo : usedAlgorithms) {
					String row = algo;
					for (String data : usedDatasets) {
						row += "\t" + grouped.get(run).get(algo).get(data).get(0).qualititativeScores
								.get(EvaluatorType.valueOf(eType));
					}
					row += "\n";
					rows += row;
				}
				try (PrintWriter out = new PrintWriter(runDir + File.separatorChar + eType)) {
					out.print(rows);
				}
			}
		}
		Map<String, Map<String, List<EvaluationRun>>> groupedAvg = averagedRuns.stream().collect(Collectors
				.groupingBy(EvaluationRun::getAlgorithmName, Collectors.groupingBy(EvaluationRun::getDatasetName)));
		for (String eType : usedEvaluators) {
			String runDir = "Avg";
			runDir = createDirectoriesIfNecessary(dir, runDir);
			String rows = "\t" + String.join("\t", usedDatasets) + "\n";
			String rowsVar = "\t" + String.join("\t", usedDatasets) + "\n";
			for (String algo : usedAlgorithms) {
				String row = algo;
				String rowVar = algo;
				for (String data : usedDatasets) {
					row += "\t" + groupedAvg.get(algo).get(data).get(0).qualititativeScores
							.get(EvaluatorType.valueOf(eType));
					rowVar += "\t" + groupedAvg.get(algo).get(data).get(0).qualititativeScoresWithVariance
							.get(EvaluatorType.valueOf(eType)).getSecond();
				}
				row += "\n";
				rowVar += "\n";
				rows += row;
				rowsVar += rowVar;
			}
			try (PrintWriter out = new PrintWriter(runDir + File.separatorChar + eType)) {
				out.print(rows);
			}
			try (PrintWriter out = new PrintWriter(runDir + File.separatorChar + eType + "Variance")) {
				out.print(rowsVar);
			}
		}

		String runDir = createDirectoriesIfNecessary(dir, "statistics");
		for (String dataSet : statisticalTestResults.keySet()) {
			List<String> header = new ArrayList<>();
			header.addAll(usedAlgorithms);
			header.add(0, "");
			String result = String.join("\t", header) + "\n";
			for (String a : header) {
				if (!a.equals("")) {
					result += a;
					for (String b : header) {
						if (!a.equals(b)) {
							if (statisticalTestResults.get(dataSet).get(a) == null) {
								result += "\t-";
							} else {
								Double value = statisticalTestResults.get(dataSet).get(a).get(b);
								if (value == null) {
									result += "\t-";
								} else {
									result += "\t" + value;
								}
							}
						}
					}
					result += "\n";
				}
			}
			try (PrintWriter out = new PrintWriter(runDir + File.separatorChar + dataSet)) {
				out.print(result);
			}
		}
	}

	private String createDirectoriesIfNecessary(String base, String folder) {
		File f = new File(base + File.separatorChar + folder);
		if (!f.exists()) {
			boolean success = f.mkdirs();
			if (success) {
				logger.info("Successfully created directory: " + f.getPath());
			} else {
				logger.error("Error while trying to create: " + f.getPath());
			}
		} else {
			logger.info(f.getPath() + " already exists");
		}
		return f.getAbsolutePath();
	}

	public List<EvaluationRun> getSingleRuns() {
		return singleRuns;
	}

	public List<EvaluationRun> getAveragedRuns() {
		return averagedRuns;
	}

	public Map<String, Map<String, Map<String, Double>>> getStatisticalTestResults() {
		return statisticalTestResults;
	}

	public void setStatisticalTestResults(Map<String, Map<String, Map<String, Double>>> statisticalTestResults) {
		this.statisticalTestResults = statisticalTestResults;
	}

}
