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
import org.aksw.limes.core.evaluation.quantitativeMeasures.RunRecord;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vandermeer.asciitable.AT_Cell;
import de.vandermeer.asciitable.AT_Row;
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
	public static final String MEMORY = "MEM";
	public static final String TIME = "TIME";
	public static final String LS_SIZE = "LS_SIZE";

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
				if (eRun.getQuanititativeRecord() != null) {
					RunRecord rr = eRun.getQuanititativeRecord();
					if (rr.getRunMemory() != 0) {
						rr.setRunMemory(rr.getRunMemory() + e.getQuanititativeRecord().getRunMemory());
					}
					if (rr.getRunTime() != 0) {
						rr.setRunTime(rr.getRunTime() + e.getQuanititativeRecord().getRunTime());
					}
					if (rr.getLinkSpecSize() != 0) {
						rr.setLinkSpecSize(rr.getLinkSpecSize() + e.getQuanititativeRecord().getLinkSpecSize());
					}
					eRun.setQuanititativeRecord(rr);
				}
			} else {
				eRun = e.clone();
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
				if (eRun.getQuanititativeRecord() != null) {
					RunRecord rr = eRun.getQuanititativeRecord();
					if (rr.getRunMemory() != 0) {
						rr.setRunMemory(rr.getRunMemory() / runsPerDataSet);
					}
					if (rr.getRunTime() != 0) {
						rr.setRunTime(rr.getRunTime() / runsPerDataSet);
					}
					if (rr.getLinkSpecSize() != 0) {
						rr.setLinkSpecSize(rr.getLinkSpecSize() / runsPerDataSet);
					}
					eRun.setQuanititativeRecord(rr);
				}
			});
		});
		List<EvaluationRun> result = new ArrayList<>();
		// Calculate variance
		for (EvaluationRun e : singleRuns) {
			EvaluationRun averagedRun = algoDataRunMap.get(e.getAlgorithmName()).get(e.getDatasetName());
			for (EvaluatorType eType : e.qualititativeScores.keySet()) {
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
			if (e.getQuanititativeRecord() != null) {
				RunRecord rr = e.getQuanititativeRecord();
				if (rr.getRunMemory() != 0) {
					double squaredDifference = Math
							.pow(rr.getRunMemory() - averagedRun.getQuanititativeRecord().getRunMemory(), 2);
					averagedRun.getQuanititativeRecord().setRunMemoryVariance(
							squaredDifference + averagedRun.getQuanititativeRecord().getRunMemoryVariance());
				}
				if (rr.getRunTime() != 0) {
					double squaredDifference = Math
							.pow(rr.getRunTime() - averagedRun.getQuanititativeRecord().getRunTime(), 2);
					averagedRun.getQuanititativeRecord().setRunTimeVariance(
							squaredDifference + averagedRun.getQuanititativeRecord().getRunTimeVariance());
				}
				if (rr.getLinkSpecSize() != 0) {
					double squaredDifference = Math
							.pow(rr.getLinkSpecSize() - averagedRun.getQuanititativeRecord().getLinkSpecSize(), 2);
					averagedRun.getQuanititativeRecord().setLinkSpecSizeVariance(
							squaredDifference + averagedRun.getQuanititativeRecord().getLinkSpecSizeVariance());
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
				if (eRun.getQuanititativeRecord() != null) {
					RunRecord rr = eRun.getQuanititativeRecord();
					if (rr.getRunMemoryVariance() != 0) {
						rr.setRunMemoryVariance(rr.getRunMemoryVariance() / runsPerDataSet);
					}
					if (rr.getRunTimeVariance() != 0) {
						rr.setRunTimeVariance(rr.getRunTimeVariance() / runsPerDataSet);
					}
					if (rr.getLinkSpecSizeVariance() != 0) {
						rr.setLinkSpecSizeVariance(rr.getLinkSpecSizeVariance() / runsPerDataSet);
					}
					eRun.setQuanititativeRecord(rr);
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
			if (er.getQuanititativeRecord() != null) {
				RunRecord rr = er.getQuanititativeRecord();
				if (rr.getRunMemory() != 0) {
					cell.append("Mem: ").append(round(rr.getRunMemory())).append(" (")
							.append(round(rr.getRunMemoryVariance())).append(")").append("\n");
				}
				if (rr.getRunTime() != 0) {
					cell.append("Time: ").append(round(rr.getRunTime())).append(" (")
							.append(round(rr.getRunTimeVariance())).append(")").append("\n");
				}
				if (rr.getLinkSpecSize() != 0) {
					cell.append("LSsize: ").append(round(rr.getLinkSpecSize())).append(" (")
							.append(round(rr.getLinkSpecSizeVariance())).append(")").append("\n");
				}
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
			overall.append(statisticalResultsToTable(dataSet, true).render());
		}
		return overall.toString();
	}

	public AsciiTable statisticalResultsToTable(String dataSet, boolean round) {
			AsciiTable at = new AsciiTable();
			at.addRule();
			List<String> header = new ArrayList<>();
			header.addAll(usedAlgorithms);
			header.set(0, "");
			at.addRow(header);
			at.addRule();
		List<String> firstColumn = new ArrayList<>(usedAlgorithms);
			firstColumn.remove(firstColumn.size() - 1);
			List<String> currentRow = new ArrayList<>();
			for (String a : firstColumn) {
					currentRow.add(a);
					for (String b : header) {
					if (!b.equals("")) {
							if (statisticalTestResults.get(dataSet).get(a) == null) {
								if (statisticalTestResults.get(dataSet).get(b) == null) {
									currentRow.add("-");
								} else {
									Double value = statisticalTestResults.get(dataSet).get(b).get(a);
									if (value == null) {
										currentRow.add("-");
									} else {
								if (round) {
									currentRow.add(round(value));
								} else {
									currentRow.add(value.toString());
								}
									}
								}
							} else {
								Double value = statisticalTestResults.get(dataSet).get(a).get(b);
								if (value == null) {
							if (statisticalTestResults.get(dataSet).get(b) != null) {
								value = statisticalTestResults.get(dataSet).get(b).get(a);
								if (value == null) {
									currentRow.add("-");
								} else {
									if (round) {
										currentRow.add(round(value));
									} else {
										currentRow.add(value.toString());
									}
								}
							} else {
								currentRow.add("-");
							}
								} else {
							if (round) {
								currentRow.add(round(value));
							} else {
								currentRow.add(value.toString());
							}
								}
							}
						}
					}
					at.addRow(currentRow);
					currentRow = new ArrayList<>();
			}
			at.addRule();
		return at;
	}

	public String round(double d) {
		try {
			d = BigDecimal.valueOf(d).round(new MathContext(PRECISION, RoundingMode.HALF_UP)).doubleValue();
		} catch (NumberFormatException e) {
			System.out.println(d);
			return "NaN";
		}
		DecimalFormat twoDForm = new DecimalFormat("########0." + new String(new char[PRECISION]).replace("\0", "0"));
		return twoDForm.format(d);
	}

	public void printToFiles(String dir) throws FileNotFoundException {
		// TODO Fix code duplication with toString
		Map<Integer, Map<String, Map<String, List<EvaluationRun>>>> grouped = singleRuns.stream()
				.collect(Collectors.groupingBy(EvaluationRun::getRunInExperiment, Collectors.groupingBy(
						EvaluationRun::getAlgorithmName, Collectors.groupingBy(EvaluationRun::getDatasetName))));
		List<String> evaluations = new ArrayList<>(usedEvaluators);
		if (singleRuns.get(0).getQuanititativeRecord().getRunMemory() != 0) {
			evaluations.add(MEMORY);
		}
		if (singleRuns.get(0).getQuanititativeRecord().getRunTime() != 0) {
			evaluations.add(TIME);
		}
		if (singleRuns.get(0).getQuanititativeRecord().getLinkSpecSize() != 0) {
			evaluations.add(LS_SIZE);
		}
		for (String eType : evaluations) {
			for (Integer run : grouped.keySet()) {
				String runDir = "Run" + run;
				runDir = createDirectoriesIfNecessary(dir, runDir);
				String rows = "\t" + String.join("\t", usedDatasets) + "\n";
				for (String algo : usedAlgorithms) {
					String row = algo;
					for (String data : usedDatasets) {
						if (eType.equals(MEMORY)) {
							row += "\t" + grouped.get(run).get(algo).get(data).get(0).getQuanititativeRecord()
									.getRunMemory();
						} else if (eType.equals(TIME)) {
							row += "\t"
									+ grouped.get(run).get(algo).get(data).get(0).getQuanititativeRecord().getRunTime();
						} else if (eType.equals(LS_SIZE)) {
							row += "\t" + grouped.get(run).get(algo).get(data).get(0).getQuanititativeRecord()
									.getLinkSpecSize();
						} else {
							row += "\t" + grouped.get(run).get(algo).get(data).get(0).qualititativeScores
									.get(EvaluatorType.valueOf(eType));
						}
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
		for (String eType : evaluations) {
			String runDir = "Avg";
			runDir = createDirectoriesIfNecessary(dir, runDir);
			String rows = "\t" + String.join("\t", usedDatasets) + "\n";
			String rowsVar = "\t" + String.join("\t", usedDatasets) + "\n";
			for (String algo : usedAlgorithms) {
				String row = algo;
				String rowVar = algo;
				for (String data : usedDatasets) {
					if (eType.equals(MEMORY)) {
						row += "\t" + groupedAvg.get(algo).get(data).get(0).getQuanititativeRecord().getRunMemory();
						rowVar += "\t"
								+ groupedAvg.get(algo).get(data).get(0).getQuanititativeRecord().getRunMemoryVariance();
					} else if (eType.equals(TIME)) {
						row += "\t" + groupedAvg.get(algo).get(data).get(0).getQuanititativeRecord().getRunTime();
						rowVar += "\t"
								+ groupedAvg.get(algo).get(data).get(0).getQuanititativeRecord().getRunTimeVariance();
					} else if (eType.equals(LS_SIZE)) {
						row += "\t" + groupedAvg.get(algo).get(data).get(0).getQuanititativeRecord().getLinkSpecSize();
						rowVar += "\t" + groupedAvg.get(algo).get(data).get(0).getQuanititativeRecord()
								.getLinkSpecSizeVariance();
					} else {
						row += "\t" + groupedAvg.get(algo).get(data).get(0).qualititativeScores
								.get(EvaluatorType.valueOf(eType));
						rowVar += "\t"
								+ groupedAvg.get(algo).get(data).get(0).qualititativeScoresWithVariance
										.get(EvaluatorType.valueOf(eType)).getSecond();
					}
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
			AsciiTable at = statisticalResultsToTable(dataSet, false);
			System.out.println(at.render());
			String result = "";
			for (AT_Row row : at.getRawContent()) {
				if (row.getCells() != null) {
					for (AT_Cell cell : row.getCells()) {
						result += cell.getContent().toString() + "\t";
					}
					if (result.endsWith("\t")) {
						result = result.substring(0, result.length() - 1);
					}
					result += "\n";
				}
			}
			System.out.println(result);
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
