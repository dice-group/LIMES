package org.aksw.limes.core.evaluation;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.aksw.limes.core.datastrutures.EvaluationRun;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.evaluator.Summary;
import org.aksw.limes.core.evaluation.quantitativeMeasures.RunRecord;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.ImmutableMap;

import de.vandermeer.asciitable.AT_Cell;
import de.vandermeer.asciitable.AT_Row;
import de.vandermeer.asciitable.AsciiTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SummaryTest {
    private static final Logger logger = LoggerFactory.getLogger(SummaryTest.class);
    public List<EvaluationRun> runs;
	public static final String algo1 = "algo1";
	public static final String algo2 = "algo2";
	public static final String data1 = "data1";
	public static final String data2 = "data2";
	public static final String implementation = "supervised";

	public static final double e31fm = 0.1;
	public static final double e32fm = 0.2;
	public static final double e33fm = 0.4;
	public static final double e34fm = 0.6;
	public static final double e35fm = 0.5;

	public static final double e41fm = 0.9;
	public static final double e42fm = 1.0;
	public static final double e43fm = 0.999;
	public static final double e44fm = 0.9828201;
	public static final double e45fm = 0.978;

	public static final double fmAvg1and2 = 0.1;
	public static final double fmAvg3 = 0.36;
	public static final double fmAvg4 = 0.97196402;

	public static final double fmVar1and2 = 0.0;
	public static final double fmVar3 = 0.03498;
	public static final double fmVar4 = 0.001370014;

	public static final double pAvg = 0.5;
	public static final double pVar = 0.0;

	public static final double runTimeAvg1 = 18;
	public static final double runTimeAvg2 = 100;
	public static final double runTimeAvg3 = 30;
	public static final double runTimeAvg4 = 10;

	public static final double lsSizeAvg1and3and4 = 3;
	public static final double lsSizeAvg2 = 3.8;

	public static final double runTimeVariance1 = 56;
	public static final double runTimeVariance2and4 = 0;
	public static final double runTimeVariance3 = 200;

	public static final double lsSizeVar1and4 = 0;
	public static final double lsSizeVar2 = 0.56;
	public static final double lsSizeVar3 = 2;

	public static final double e11and15runTime = 10;
	public static final double e12and14runTime = 20;
	public static final double e13runTime = 30;

	public static final double e31runTime = 10;
	public static final double e32runTime = 20;
	public static final double e33runTime = 30;
	public static final double e34runTime = 40;
	public static final double e35runTime = 50;

	public static final double e1and2lsSize = 3;

	public static final double e21and25lsSize = 3;
	public static final double e22and24lsSize = 4;
	public static final double e23lsSize = 5;

	public static final double e31lsSize = 1;
	public static final double e32lsSize = 2;
	public static final double e33lsSize = 3;
	public static final double e34lsSize = 4;
	public static final double e35lsSize = 5;

	public static final double statistic1 = 12.332;
	public static final double statistic2 = 0.332;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void prepareData() {
        Locale.setDefault(Locale.US);
		EvaluationRun e11 = new EvaluationRun(algo1, implementation, data1,
				ImmutableMap.of(EvaluatorType.F_MEASURE, fmAvg1and2, EvaluatorType.PRECISION, pAvg), 0);
		EvaluationRun e12 = new EvaluationRun(algo1, implementation, data1,
				ImmutableMap.of(EvaluatorType.F_MEASURE, fmAvg1and2, EvaluatorType.PRECISION, pAvg), 1);
		EvaluationRun e13 = new EvaluationRun(algo1, implementation, data1,
				ImmutableMap.of(EvaluatorType.F_MEASURE, fmAvg1and2, EvaluatorType.PRECISION, pAvg), 2);
		EvaluationRun e14 = new EvaluationRun(algo1, implementation, data1,
				ImmutableMap.of(EvaluatorType.F_MEASURE, fmAvg1and2, EvaluatorType.PRECISION, pAvg), 3);
		EvaluationRun e15 = new EvaluationRun(algo1, implementation, data1,
				ImmutableMap.of(EvaluatorType.F_MEASURE, fmAvg1and2, EvaluatorType.PRECISION, pAvg), 4);

		EvaluationRun e21 = new EvaluationRun(algo1, implementation, data2,
				ImmutableMap.of(EvaluatorType.F_MEASURE, fmAvg1and2, EvaluatorType.PRECISION, pAvg), 0);
		EvaluationRun e22 = new EvaluationRun(algo1, implementation, data2,
				ImmutableMap.of(EvaluatorType.F_MEASURE, fmAvg1and2, EvaluatorType.PRECISION, pAvg), 1);
		EvaluationRun e23 = new EvaluationRun(algo1, implementation, data2,
				ImmutableMap.of(EvaluatorType.F_MEASURE, fmAvg1and2, EvaluatorType.PRECISION, pAvg), 2);
		EvaluationRun e24 = new EvaluationRun(algo1, implementation, data2,
				ImmutableMap.of(EvaluatorType.F_MEASURE, fmAvg1and2, EvaluatorType.PRECISION, pAvg), 3);
		EvaluationRun e25 = new EvaluationRun(algo1, implementation, data2,
				ImmutableMap.of(EvaluatorType.F_MEASURE, fmAvg1and2, EvaluatorType.PRECISION, pAvg), 4);

		EvaluationRun e31 = new EvaluationRun(algo2, implementation, data1,
				ImmutableMap.of(EvaluatorType.F_MEASURE, e31fm, EvaluatorType.PRECISION, pAvg), 0);
		EvaluationRun e32 = new EvaluationRun(algo2, implementation, data1,
				ImmutableMap.of(EvaluatorType.F_MEASURE, e32fm, EvaluatorType.PRECISION, pAvg), 1);
		EvaluationRun e33 = new EvaluationRun(algo2, implementation, data1,
				ImmutableMap.of(EvaluatorType.F_MEASURE, e33fm, EvaluatorType.PRECISION, pAvg), 2);
		EvaluationRun e34 = new EvaluationRun(algo2, implementation, data1,
				ImmutableMap.of(EvaluatorType.F_MEASURE, e34fm, EvaluatorType.PRECISION, pAvg), 3);
		EvaluationRun e35 = new EvaluationRun(algo2, implementation, data1,
				ImmutableMap.of(EvaluatorType.F_MEASURE, e35fm, EvaluatorType.PRECISION, pAvg), 4);

		EvaluationRun e41 = new EvaluationRun(algo2, implementation, data2,
				ImmutableMap.of(EvaluatorType.F_MEASURE, e41fm, EvaluatorType.PRECISION, pAvg), 0);
		EvaluationRun e42 = new EvaluationRun(algo2, implementation, data2,
				ImmutableMap.of(EvaluatorType.F_MEASURE, e42fm, EvaluatorType.PRECISION, pAvg), 1);
		EvaluationRun e43 = new EvaluationRun(algo2, implementation, data2,
				ImmutableMap.of(EvaluatorType.F_MEASURE, e43fm, EvaluatorType.PRECISION, pAvg), 2);
		EvaluationRun e44 = new EvaluationRun(algo2, implementation, data2,
				ImmutableMap.of(EvaluatorType.F_MEASURE, e44fm, EvaluatorType.PRECISION, pAvg), 3);
		EvaluationRun e45 = new EvaluationRun(algo2, implementation, data2,
				ImmutableMap.of(EvaluatorType.F_MEASURE, e45fm, EvaluatorType.PRECISION, pAvg), 4);

		e11.setQuanititativeRecord(new RunRecord(0, e11and15runTime, 0.0, e1and2lsSize));
		e12.setQuanititativeRecord(new RunRecord(1, e12and14runTime, 0.0, e1and2lsSize));
		e13.setQuanititativeRecord(new RunRecord(2, e13runTime, 0.0, e1and2lsSize));
		e14.setQuanititativeRecord(new RunRecord(3, e12and14runTime, 0.0, e1and2lsSize));
		e15.setQuanititativeRecord(new RunRecord(4, e11and15runTime, 0.0, e1and2lsSize));

		e21.setQuanititativeRecord(new RunRecord(0, runTimeAvg2, 0.0, e21and25lsSize));
		e22.setQuanititativeRecord(new RunRecord(1, runTimeAvg2, 0.0, e22and24lsSize));
		e23.setQuanititativeRecord(new RunRecord(2, runTimeAvg2, 0.0, e23lsSize));
		e24.setQuanititativeRecord(new RunRecord(3, runTimeAvg2, 0.0, e22and24lsSize));
		e25.setQuanititativeRecord(new RunRecord(4, runTimeAvg2, 0.0, e21and25lsSize));

		e31.setQuanititativeRecord(new RunRecord(0, e31runTime, 0.0, e31lsSize));
		e32.setQuanititativeRecord(new RunRecord(1, e32runTime, 0.0, e32lsSize));
		e33.setQuanititativeRecord(new RunRecord(2, e33runTime, 0.0, e33lsSize));
		e34.setQuanititativeRecord(new RunRecord(3, e34runTime, 0.0, e34lsSize));
		e35.setQuanititativeRecord(new RunRecord(4, e35runTime, 0.0, e35lsSize));

		e41.setQuanititativeRecord(new RunRecord(0, runTimeAvg4, 0.0, e1and2lsSize));
		e42.setQuanititativeRecord(new RunRecord(1, runTimeAvg4, 0.0, e1and2lsSize));
		e43.setQuanititativeRecord(new RunRecord(2, runTimeAvg4, 0.0, e1and2lsSize));
		e44.setQuanititativeRecord(new RunRecord(3, runTimeAvg4, 0.0, e1and2lsSize));
		e45.setQuanititativeRecord(new RunRecord(4, runTimeAvg4, 0.0, e1and2lsSize));
		runs = new ArrayList<>();
		runs.add(e11);
		runs.add(e12);
		runs.add(e13);
		runs.add(e14);
		runs.add(e15);
		runs.add(e21);
		runs.add(e22);
		runs.add(e23);
		runs.add(e24);
		runs.add(e25);
		runs.add(e31);
		runs.add(e32);
		runs.add(e33);
		runs.add(e34);
		runs.add(e35);
		runs.add(e41);
		runs.add(e42);
		runs.add(e43);
		runs.add(e44);
		runs.add(e45);
	}

	@Test
	public void testSummary() {

		Summary s = new Summary(runs, 5);
		List<EvaluationRun> avgRuns = s.getAveragedRuns();
		assertEquals(4, avgRuns.size());
		avgRuns.sort((e1, e2) -> {
			if (e1.getAlgorithmName().equals(e2.getAlgorithmName())) {
				return e1.getDatasetName().compareTo(e2.getDatasetName());
			}
			return e1.getAlgorithmName().compareTo(e2.getAlgorithmName());
		});
		assertEquals(fmAvg1and2, avgRuns.get(0).qualititativeScores.get(EvaluatorType.F_MEASURE), 0);
		assertEquals(fmAvg1and2, avgRuns.get(1).qualititativeScores.get(EvaluatorType.F_MEASURE), 0);
		assertEquals(fmAvg3, avgRuns.get(2).qualititativeScores.get(EvaluatorType.F_MEASURE), 0);
		assertEquals(fmAvg4, avgRuns.get(3).qualititativeScores.get(EvaluatorType.F_MEASURE), 0);

		assertEquals(fmAvg1and2, avgRuns.get(0).qualititativeScoresWithVariance.get(EvaluatorType.F_MEASURE).getFirst(),
				0);
		assertEquals(fmAvg1and2, avgRuns.get(1).qualititativeScoresWithVariance.get(EvaluatorType.F_MEASURE).getFirst(),
				0);
		assertEquals(fmAvg3, avgRuns.get(2).qualititativeScoresWithVariance.get(EvaluatorType.F_MEASURE).getFirst(), 0);
		assertEquals(fmAvg4, avgRuns.get(3).qualititativeScoresWithVariance.get(EvaluatorType.F_MEASURE).getFirst(),
				0);

		assertEquals(fmVar1and2,
				avgRuns.get(0).qualititativeScoresWithVariance.get(EvaluatorType.F_MEASURE).getSecond(), 0);
		assertEquals(fmVar1and2,
				avgRuns.get(1).qualititativeScoresWithVariance.get(EvaluatorType.F_MEASURE).getSecond(), 0);
		assertEquals(fmVar3, avgRuns.get(2).qualititativeScoresWithVariance.get(EvaluatorType.F_MEASURE).getSecond(),
				0.01);
		assertEquals(fmVar4,
				avgRuns.get(3).qualititativeScoresWithVariance.get(EvaluatorType.F_MEASURE).getSecond(), 0.01);

		assertEquals(pAvg, avgRuns.get(0).qualititativeScores.get(EvaluatorType.PRECISION), 0);
		assertEquals(pAvg, avgRuns.get(1).qualititativeScores.get(EvaluatorType.PRECISION), 0);
		assertEquals(pAvg, avgRuns.get(2).qualititativeScores.get(EvaluatorType.PRECISION), 0);
		assertEquals(pAvg, avgRuns.get(3).qualititativeScores.get(EvaluatorType.PRECISION), 0);

		assertEquals(pAvg, avgRuns.get(0).qualititativeScoresWithVariance.get(EvaluatorType.PRECISION).getFirst(), 0);
		assertEquals(pAvg, avgRuns.get(1).qualititativeScoresWithVariance.get(EvaluatorType.PRECISION).getFirst(), 0);
		assertEquals(pAvg, avgRuns.get(2).qualititativeScoresWithVariance.get(EvaluatorType.PRECISION).getFirst(), 0);
		assertEquals(pAvg, avgRuns.get(3).qualititativeScoresWithVariance.get(EvaluatorType.PRECISION).getFirst(), 0);

		assertEquals(pVar, avgRuns.get(0).qualititativeScoresWithVariance.get(EvaluatorType.PRECISION).getSecond(), 0);
		assertEquals(pVar, avgRuns.get(1).qualititativeScoresWithVariance.get(EvaluatorType.PRECISION).getSecond(), 0);
		assertEquals(pVar, avgRuns.get(2).qualititativeScoresWithVariance.get(EvaluatorType.PRECISION).getSecond(), 0);
		assertEquals(pVar, avgRuns.get(3).qualititativeScoresWithVariance.get(EvaluatorType.PRECISION).getSecond(), 0);

		assertEquals(runTimeAvg1, avgRuns.get(0).getQuanititativeRecord().getRunTime(), 0);
		assertEquals(runTimeAvg2, avgRuns.get(1).getQuanititativeRecord().getRunTime(), 0);
		assertEquals(runTimeAvg3, avgRuns.get(2).getQuanititativeRecord().getRunTime(), 0);
		assertEquals(runTimeAvg4, avgRuns.get(3).getQuanititativeRecord().getRunTime(), 0);

		assertEquals(lsSizeAvg1and3and4, avgRuns.get(0).getQuanititativeRecord().getLinkSpecSize(), 0);
		assertEquals(lsSizeAvg2, avgRuns.get(1).getQuanititativeRecord().getLinkSpecSize(), 0);
		assertEquals(lsSizeAvg1and3and4, avgRuns.get(2).getQuanititativeRecord().getLinkSpecSize(), 0);
		assertEquals(lsSizeAvg1and3and4, avgRuns.get(3).getQuanititativeRecord().getLinkSpecSize(), 0);

		assertEquals(runTimeVariance1, avgRuns.get(0).getQuanititativeRecord().getRunTimeVariance(), 0);
		assertEquals(runTimeVariance2and4, avgRuns.get(1).getQuanititativeRecord().getRunTimeVariance(), 0);
		assertEquals(runTimeVariance3, avgRuns.get(2).getQuanititativeRecord().getRunTimeVariance(), 0);
		assertEquals(runTimeVariance2and4, avgRuns.get(3).getQuanititativeRecord().getRunTimeVariance(), 0);

		assertEquals(lsSizeVar1and4, avgRuns.get(0).getQuanititativeRecord().getLinkSpecSizeVariance(), 0);
		assertEquals(lsSizeVar2, avgRuns.get(1).getQuanititativeRecord().getLinkSpecSizeVariance(), 0.01);
		assertEquals(lsSizeVar3, avgRuns.get(2).getQuanititativeRecord().getLinkSpecSizeVariance(), 0);
		assertEquals(lsSizeVar1and4, avgRuns.get(3).getQuanititativeRecord().getLinkSpecSizeVariance(), 0);
	}

	@Test
	public void testStatisticalResultsToTable()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Map<String, Map<String, Map<String, Double>>> statisticalTestResults = new HashMap<>();
		HashMap<String, Map<String, Double>> inner = new HashMap<>();
		final String ham = "ham";
		final String fuzzy = "fuzzy";
		final String crisp = "crisp";
		final String all = "all";
		final double allham = 1.0;
		final double allfuzzy = 1.1;
		final double allcrisp = 1.2;
		final double hamfuzzy = 2.0;
		final double hamcrisp = 2.1;
		final double fuzzycrisp = 3.0;
		HashMap<String, Double> allInner = new HashMap<>();
		allInner.put(ham, allham);
		allInner.put(fuzzy, allfuzzy);
		allInner.put(crisp, allcrisp);
		inner.put(all, allInner);

		HashMap<String, Double> hamInner = new HashMap<>();
		hamInner.put(fuzzy, hamfuzzy);
		hamInner.put(crisp, hamcrisp);
		inner.put(ham, hamInner);

		HashMap<String, Double> fuzInner = new HashMap<>();
		fuzInner.put(crisp, fuzzycrisp);
		inner.put(fuzzy, fuzInner);
		statisticalTestResults.put(data1, inner);
		Summary s = new Summary(new ArrayList<EvaluationRun>(), 5);
		Field algos = Summary.class.getDeclaredField("usedAlgorithms");
		algos.setAccessible(true);
		List<String> usedAlgos = new ArrayList<String>(inner.keySet());
		usedAlgos.add(crisp);
		algos.set(s, usedAlgos);
		s.setStatisticalTestResults(statisticalTestResults);
		AsciiTable at = s.statisticalResultsToTable(data1, true);
		logger.info("{}",at.render());
		String[] expectedCells = new String[] { "", ham, fuzzy, crisp, all, allham + "0", allfuzzy + "0",
				allcrisp + "0", ham, "-", hamfuzzy + "0", hamcrisp + "0", fuzzy, hamfuzzy + "0", "-",
				fuzzycrisp + "0" };
		int i = 0;
		for (AT_Row row : at.getRawContent()) {
			if (row.getCells() != null) {
				for (AT_Cell cell : row.getCells()) {
					assertEquals(expectedCells[i], cell.getContent().toString());
					i++;
				}
			}
		}
	}

	@Test
	public void testWriteToFiles() throws FileNotFoundException, IOException {
		Summary s = new Summary(runs, 5);
		// Values in this map are arbitrary only to check correctly writing to file
		Map<String, Map<String, Map<String, Double>>> statisticalTestResults = new HashMap<>();
		Map<String, Map<String, Double>> d1a1a2Map = new HashMap<>();
		Map<String, Double> a2Map1 = new HashMap<>();
		a2Map1.put(algo2, statistic1);
		d1a1a2Map.put(algo1, a2Map1);
		statisticalTestResults.put(data1, d1a1a2Map);

		Map<String, Map<String, Double>> d2a1a2Map = new HashMap<>();
		Map<String, Double> a2Map2 = new HashMap<>();
		a2Map2.put(algo2, statistic2);
		d2a1a2Map.put(algo1, a2Map2);
		statisticalTestResults.put(data2, d2a1a2Map);
		s.setStatisticalTestResults(statisticalTestResults);
		File f = folder.newFolder();
		s.printToFiles(f.getAbsolutePath());
		String[] paths = {
				f.getAbsolutePath() + File.separatorChar + "Run0" + File.separatorChar + EvaluatorType.F_MEASURE,
				f.getAbsolutePath() + File.separatorChar + "Run0" + File.separatorChar + EvaluatorType.PRECISION,
				f.getAbsolutePath() + File.separatorChar + "Run1" + File.separatorChar + EvaluatorType.F_MEASURE,
				f.getAbsolutePath() + File.separatorChar + "Run1" + File.separatorChar + EvaluatorType.PRECISION, // 3
				f.getAbsolutePath() + File.separatorChar + "Run2" + File.separatorChar + EvaluatorType.F_MEASURE,
				f.getAbsolutePath() + File.separatorChar + "Run2" + File.separatorChar + EvaluatorType.PRECISION,
				f.getAbsolutePath() + File.separatorChar + "Run3" + File.separatorChar + EvaluatorType.F_MEASURE, // 6
				f.getAbsolutePath() + File.separatorChar + "Run3" + File.separatorChar + EvaluatorType.PRECISION,
				f.getAbsolutePath() + File.separatorChar + "Run4" + File.separatorChar + EvaluatorType.F_MEASURE,
				f.getAbsolutePath() + File.separatorChar + "Run4" + File.separatorChar + EvaluatorType.PRECISION, // 9
				f.getAbsolutePath() + File.separatorChar + "Avg" + File.separatorChar + EvaluatorType.F_MEASURE,
				f.getAbsolutePath() + File.separatorChar + "Avg" + File.separatorChar + EvaluatorType.PRECISION,
				f.getAbsolutePath() + File.separatorChar + "Avg" + File.separatorChar + EvaluatorType.F_MEASURE
						+ "Variance", // 12
				f.getAbsolutePath() + File.separatorChar + "Avg" + File.separatorChar + EvaluatorType.PRECISION
						+ "Variance",
				f.getAbsolutePath() + File.separatorChar + "statistics" + File.separatorChar + data1,
				f.getAbsolutePath() + File.separatorChar + "statistics" + File.separatorChar + data2, // 15
				// Quantitative
				f.getAbsolutePath() + File.separatorChar + "Run0" + File.separatorChar + Summary.TIME,
				f.getAbsolutePath() + File.separatorChar + "Run0" + File.separatorChar + Summary.LS_SIZE,
				f.getAbsolutePath() + File.separatorChar + "Run1" + File.separatorChar + Summary.TIME, // 18
				f.getAbsolutePath() + File.separatorChar + "Run1" + File.separatorChar + Summary.LS_SIZE,
				f.getAbsolutePath() + File.separatorChar + "Run2" + File.separatorChar + Summary.TIME,
				f.getAbsolutePath() + File.separatorChar + "Run2" + File.separatorChar + Summary.LS_SIZE, // 21
				f.getAbsolutePath() + File.separatorChar + "Run3" + File.separatorChar + Summary.TIME,
				f.getAbsolutePath() + File.separatorChar + "Run3" + File.separatorChar + Summary.LS_SIZE,
				f.getAbsolutePath() + File.separatorChar + "Run4" + File.separatorChar + Summary.TIME, // 24
				f.getAbsolutePath() + File.separatorChar + "Run4" + File.separatorChar + Summary.LS_SIZE,
				f.getAbsolutePath() + File.separatorChar + "Avg" + File.separatorChar + Summary.TIME,
				f.getAbsolutePath() + File.separatorChar + "Avg" + File.separatorChar + Summary.LS_SIZE, // 27
				f.getAbsolutePath() + File.separatorChar + "Avg" + File.separatorChar + Summary.TIME + "Variance",
				f.getAbsolutePath() + File.separatorChar + "Avg" + File.separatorChar + Summary.LS_SIZE + "Variance", };
		String[] expectedValues = {
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + fmAvg1and2 + "\t" + fmAvg1and2 + "\n" + algo2 + "\t"
						+ e31fm + "\t" + e41fm + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + pAvg + "\t" + pAvg + "\n" + algo2 + "\t" + pAvg
						+ "\t" + pAvg + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + fmAvg1and2 + "\t" + fmAvg1and2 + "\n" + algo2 + "\t"
						+ e32fm + "\t" + e42fm + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + pAvg + "\t" + pAvg + "\n" + algo2 + "\t" + pAvg
						+ "\t" + pAvg + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + fmAvg1and2 + "\t" + fmAvg1and2 + "\n" + algo2 + "\t"
						+ e33fm + "\t" + e43fm + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + pAvg + "\t" + pAvg + "\n" + algo2 + "\t" + pAvg
						+ "\t" + pAvg + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + fmAvg1and2 + "\t" + fmAvg1and2 + "\n" + algo2 + "\t"
						+ e34fm + "\t" + e44fm + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + pAvg + "\t" + pAvg + "\n" + algo2 + "\t" + pAvg
						+ "\t" + pAvg + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + fmAvg1and2 + "\t" + fmAvg1and2 + "\n" + algo2 + "\t"
						+ e35fm + "\t" + e45fm + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + pAvg + "\t" + pAvg + "\n" + algo2 + "\t" + pAvg
						+ "\t" + pAvg + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + fmAvg1and2 + "\t" + fmAvg1and2 + "\n" + algo2 + "\t"
						+ fmAvg3 + "\t" + fmAvg4 + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + pAvg + "\t" + pAvg + "\n" + algo2 + "\t" + pAvg
						+ "\t" + pAvg + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + fmVar1and2 + "\t" + fmVar1and2 + "\n" + algo2 + "\t"
						+ 0.0344 + "\t" + 0.0013700136182415992 + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + pVar + "\t" + pVar + "\n" + algo2 + "\t" + pVar
						+ "\t" + pVar + "\n",
				"\t" + algo2 + "\n" + algo1 + "\t" + statistic1 + "\n",
				"\t" + algo2 + "\n" + algo1 + "\t" + statistic2 + "\n",
		// Quantitative
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + e11and15runTime + "\t" + runTimeAvg2 + "\n" + algo2
						+ "\t" + e31runTime + "\t" + runTimeAvg4 + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + e1and2lsSize + "\t" + e21and25lsSize + "\n" + algo2
						+ "\t" + e31lsSize + "\t" + e1and2lsSize + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + e12and14runTime + "\t" + runTimeAvg2 + "\n" + algo2
						+ "\t" + e32runTime + "\t" + runTimeAvg4 + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + e1and2lsSize + "\t" + e22and24lsSize + "\n" + algo2
						+ "\t" + e32lsSize + "\t" + e1and2lsSize + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + e13runTime + "\t" + runTimeAvg2 + "\n" + algo2
						+ "\t" + e33runTime + "\t" + runTimeAvg4 + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + e1and2lsSize + "\t" + e23lsSize + "\n" + algo2
						+ "\t" + e33lsSize + "\t" + e1and2lsSize + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + e12and14runTime + "\t" + runTimeAvg2 + "\n" + algo2
						+ "\t" + e34runTime + "\t" + runTimeAvg4 + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + e1and2lsSize + "\t" + e22and24lsSize + "\n" + algo2
						+ "\t" + e34lsSize + "\t" + e1and2lsSize + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + e11and15runTime + "\t" + runTimeAvg2 + "\n" + algo2
						+ "\t" + e35runTime + "\t" + runTimeAvg4 + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + e1and2lsSize + "\t" + e21and25lsSize + "\n" + algo2
						+ "\t" + e35lsSize + "\t" + e1and2lsSize + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + runTimeAvg1 + "\t" + runTimeAvg2 + "\n" + algo2
						+ "\t" + runTimeAvg3 + "\t" + runTimeAvg4 + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + lsSizeAvg1and3and4 + "\t" + lsSizeAvg2 + "\n"
						+ algo2 + "\t" + lsSizeAvg1and3and4 + "\t" + lsSizeAvg1and3and4 + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + runTimeVariance1 + "\t" + runTimeVariance2and4
						+ "\n" + algo2 + "\t" + runTimeVariance3 + "\t" + runTimeVariance2and4 + "\n",
				"\t" + data1 + "\t" + data2 + "\n" + algo1 + "\t" + lsSizeVar1and4 + "\t0.5599999999999999\n" + algo2
						+ "\t" + lsSizeVar3 + "\t" + lsSizeVar1and4 + "\n", };
		for (int i = 0; i < paths.length; i++) {
			assertEquals("expected value for " + paths[i] + " was not encountered", expectedValues[i],
					new String(Files.readAllBytes(Paths.get(paths[i]))));
		}
	}

}
