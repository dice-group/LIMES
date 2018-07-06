package org.aksw.limes.core.evaluation;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.datastrutures.EvaluationRun;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.evaluator.Summary;
import org.aksw.limes.core.evaluation.quantitativeMeasures.RunRecord;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.ImmutableMap;

public class SummaryTest {
	public List<EvaluationRun> runs;
	public static final String algo1 = "algo1";
	public static final String algo2 = "algo2";

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void prepareData() {
		EvaluationRun e11 = new EvaluationRun(algo1, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1, EvaluatorType.PRECISION, 0.5), 0);
		EvaluationRun e12 = new EvaluationRun(algo1, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1, EvaluatorType.PRECISION, 0.5), 1);
		EvaluationRun e13 = new EvaluationRun(algo1, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1, EvaluatorType.PRECISION, 0.5), 2);
		EvaluationRun e14 = new EvaluationRun(algo1, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1, EvaluatorType.PRECISION, 0.5), 3);
		EvaluationRun e15 = new EvaluationRun(algo1, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1, EvaluatorType.PRECISION, 0.5), 4);

		EvaluationRun e21 = new EvaluationRun(algo1, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1, EvaluatorType.PRECISION, 0.5), 0);
		EvaluationRun e22 = new EvaluationRun(algo1, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1, EvaluatorType.PRECISION, 0.5), 1);
		EvaluationRun e23 = new EvaluationRun(algo1, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1, EvaluatorType.PRECISION, 0.5), 2);
		EvaluationRun e24 = new EvaluationRun(algo1, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1, EvaluatorType.PRECISION, 0.5), 3);
		EvaluationRun e25 = new EvaluationRun(algo1, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1, EvaluatorType.PRECISION, 0.5), 4);

		EvaluationRun e31 = new EvaluationRun(algo2, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1, EvaluatorType.PRECISION, 0.5), 0);
		EvaluationRun e32 = new EvaluationRun(algo2, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.2, EvaluatorType.PRECISION, 0.5), 1);
		EvaluationRun e33 = new EvaluationRun(algo2, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.4, EvaluatorType.PRECISION, 0.5), 2);
		EvaluationRun e34 = new EvaluationRun(algo2, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.6, EvaluatorType.PRECISION, 0.5), 3);
		EvaluationRun e35 = new EvaluationRun(algo2, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.5, EvaluatorType.PRECISION, 0.5), 4);

		EvaluationRun e41 = new EvaluationRun(algo2, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.9, EvaluatorType.PRECISION, 0.5), 0);
		EvaluationRun e42 = new EvaluationRun(algo2, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 1.0, EvaluatorType.PRECISION, 0.5), 1);
		EvaluationRun e43 = new EvaluationRun(algo2, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.999, EvaluatorType.PRECISION, 0.5), 2);
		EvaluationRun e44 = new EvaluationRun(algo2, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.9828201, EvaluatorType.PRECISION, 0.5), 3);
		EvaluationRun e45 = new EvaluationRun(algo2, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.978, EvaluatorType.PRECISION, 0.5), 4);

		e11.setQuanititativeRecord(new RunRecord(0, 10, 3));
		e12.setQuanititativeRecord(new RunRecord(1, 20, 3));
		e13.setQuanititativeRecord(new RunRecord(2, 30, 3));
		e14.setQuanititativeRecord(new RunRecord(3, 20, 3));
		e15.setQuanititativeRecord(new RunRecord(4, 10, 3));

		e21.setQuanititativeRecord(new RunRecord(0, 100, 3));
		e22.setQuanititativeRecord(new RunRecord(1, 100, 4));
		e23.setQuanititativeRecord(new RunRecord(2, 100, 5));
		e24.setQuanititativeRecord(new RunRecord(3, 100, 4));
		e25.setQuanititativeRecord(new RunRecord(4, 100, 3));

		e31.setQuanititativeRecord(new RunRecord(0, 10, 1));
		e32.setQuanititativeRecord(new RunRecord(1, 20, 2));
		e33.setQuanititativeRecord(new RunRecord(2, 30, 3));
		e34.setQuanititativeRecord(new RunRecord(3, 40, 4));
		e35.setQuanititativeRecord(new RunRecord(4, 50, 5));

		e41.setQuanititativeRecord(new RunRecord(0, 10, 3));
		e42.setQuanititativeRecord(new RunRecord(1, 10, 3));
		e43.setQuanititativeRecord(new RunRecord(2, 10, 3));
		e44.setQuanititativeRecord(new RunRecord(3, 10, 3));
		e45.setQuanititativeRecord(new RunRecord(4, 10, 3));
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
		assertEquals(0.1, avgRuns.get(0).qualititativeScores.get(EvaluatorType.F_MEASURE), 0);
		assertEquals(0.1, avgRuns.get(1).qualititativeScores.get(EvaluatorType.F_MEASURE), 0);
		assertEquals(0.36, avgRuns.get(2).qualititativeScores.get(EvaluatorType.F_MEASURE), 0);
		assertEquals(0.97196402, avgRuns.get(3).qualititativeScores.get(EvaluatorType.F_MEASURE), 0);

		assertEquals(0.1, avgRuns.get(0).qualititativeScoresWithVariance.get(EvaluatorType.F_MEASURE).getFirst(), 0);
		assertEquals(0.1, avgRuns.get(1).qualititativeScoresWithVariance.get(EvaluatorType.F_MEASURE).getFirst(), 0);
		assertEquals(0.36, avgRuns.get(2).qualititativeScoresWithVariance.get(EvaluatorType.F_MEASURE).getFirst(), 0);
		assertEquals(0.97196402, avgRuns.get(3).qualititativeScoresWithVariance.get(EvaluatorType.F_MEASURE).getFirst(),
				0);

		assertEquals(0.0, avgRuns.get(0).qualititativeScoresWithVariance.get(EvaluatorType.F_MEASURE).getSecond(), 0);
		assertEquals(0.0, avgRuns.get(1).qualititativeScoresWithVariance.get(EvaluatorType.F_MEASURE).getSecond(), 0);
		assertEquals(0.03498, avgRuns.get(2).qualititativeScoresWithVariance.get(EvaluatorType.F_MEASURE).getSecond(),
				0.01);
		assertEquals(0.001370014,
				avgRuns.get(3).qualititativeScoresWithVariance.get(EvaluatorType.F_MEASURE).getSecond(), 0.01);

		assertEquals(0.5, avgRuns.get(0).qualititativeScores.get(EvaluatorType.PRECISION), 0);
		assertEquals(0.5, avgRuns.get(1).qualititativeScores.get(EvaluatorType.PRECISION), 0);
		assertEquals(0.5, avgRuns.get(2).qualititativeScores.get(EvaluatorType.PRECISION), 0);
		assertEquals(0.5, avgRuns.get(3).qualititativeScores.get(EvaluatorType.PRECISION), 0);

		assertEquals(0.5, avgRuns.get(0).qualititativeScoresWithVariance.get(EvaluatorType.PRECISION).getFirst(), 0);
		assertEquals(0.5, avgRuns.get(1).qualititativeScoresWithVariance.get(EvaluatorType.PRECISION).getFirst(), 0);
		assertEquals(0.5, avgRuns.get(2).qualititativeScoresWithVariance.get(EvaluatorType.PRECISION).getFirst(), 0);
		assertEquals(0.5, avgRuns.get(3).qualititativeScoresWithVariance.get(EvaluatorType.PRECISION).getFirst(), 0);

		assertEquals(0.0, avgRuns.get(0).qualititativeScoresWithVariance.get(EvaluatorType.PRECISION).getSecond(), 0);
		assertEquals(0.0, avgRuns.get(1).qualititativeScoresWithVariance.get(EvaluatorType.PRECISION).getSecond(), 0);
		assertEquals(0.0, avgRuns.get(2).qualititativeScoresWithVariance.get(EvaluatorType.PRECISION).getSecond(), 0);
		assertEquals(0.0, avgRuns.get(3).qualititativeScoresWithVariance.get(EvaluatorType.PRECISION).getSecond(), 0);

		assertEquals(18, avgRuns.get(0).getQuanititativeRecord().getRunTime(), 0);
		assertEquals(100, avgRuns.get(1).getQuanititativeRecord().getRunTime(), 0);
		assertEquals(30, avgRuns.get(2).getQuanititativeRecord().getRunTime(), 0);
		assertEquals(10, avgRuns.get(3).getQuanititativeRecord().getRunTime(), 0);

		assertEquals(3, avgRuns.get(0).getQuanititativeRecord().getLinkSpecSize(), 0);
		assertEquals(3.8, avgRuns.get(1).getQuanititativeRecord().getLinkSpecSize(), 0);
		assertEquals(3, avgRuns.get(2).getQuanititativeRecord().getLinkSpecSize(), 0);
		assertEquals(3, avgRuns.get(3).getQuanititativeRecord().getLinkSpecSize(), 0);

		assertEquals(56, avgRuns.get(0).getQuanititativeRecord().getRunTimeVariance(), 0);
		assertEquals(0, avgRuns.get(1).getQuanititativeRecord().getRunTimeVariance(), 0);
		assertEquals(200, avgRuns.get(2).getQuanititativeRecord().getRunTimeVariance(), 0);
		assertEquals(0, avgRuns.get(3).getQuanititativeRecord().getRunTimeVariance(), 0);

		assertEquals(0, avgRuns.get(0).getQuanititativeRecord().getLinkSpecSizeVariance(), 0);
		assertEquals(0.56, avgRuns.get(1).getQuanititativeRecord().getLinkSpecSizeVariance(), 0.01);
		assertEquals(2, avgRuns.get(2).getQuanititativeRecord().getLinkSpecSizeVariance(), 0);
		assertEquals(0, avgRuns.get(3).getQuanititativeRecord().getLinkSpecSizeVariance(), 0);
	}

	@Test
	public void testWriteToFiles() throws FileNotFoundException, IOException {
		Summary s = new Summary(runs, 5);
		// Values in this map are arbitrary only to check correctly writing to file
		Map<String, Map<String, Map<String, Double>>> statisticalTestResults = new HashMap<>();
		Map<String, Map<String, Double>> d1a1a2Map = new HashMap<>();
		Map<String, Double> a2Map1 = new HashMap<>();
		a2Map1.put("algo2", 12.332);
		d1a1a2Map.put("algo1", a2Map1);
		statisticalTestResults.put("data1", d1a1a2Map);
		Map<String, Map<String, Double>> d2a1a2Map = new HashMap<>();
		Map<String, Double> a2Map2 = new HashMap<>();
		a2Map2.put("algo2", 0.332);
		d2a1a2Map.put("algo1", a2Map2);
		statisticalTestResults.put("data2", d2a1a2Map);
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
				f.getAbsolutePath() + File.separatorChar + "statistics" + File.separatorChar + "data1",
				f.getAbsolutePath() + File.separatorChar + "statistics" + File.separatorChar + "data2", // 15
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

		assertEquals("\tdata1\tdata2\n" + algo1 + "\t0.5\t0.5\n" + algo2 + "\t0.5\t0.5\n",
				new String(Files.readAllBytes(Paths.get(paths[1]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t0.5\t0.5\n" + algo2 + "\t0.5\t0.5\n",
				new String(Files.readAllBytes(Paths.get(paths[3]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t0.5\t0.5\n" + algo2 + "\t0.5\t0.5\n",
				new String(Files.readAllBytes(Paths.get(paths[5]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t0.5\t0.5\n" + algo2 + "\t0.5\t0.5\n",
				new String(Files.readAllBytes(Paths.get(paths[7]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t0.5\t0.5\n" + algo2 + "\t0.5\t0.5\n",
				new String(Files.readAllBytes(Paths.get(paths[9]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t0.5\t0.5\n" + algo2 + "\t0.5\t0.5\n",
				new String(Files.readAllBytes(Paths.get(paths[11]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t0.0\t0.0\n" + algo2 + "\t0.0\t0.0\n",
				new String(Files.readAllBytes(Paths.get(paths[13]))));

		assertEquals("\tdata1\tdata2\n" + algo1 + "\t0.1\t0.1\n" + algo2 + "\t0.1\t0.9\n",
				new String(Files.readAllBytes(Paths.get(paths[0]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t0.1\t0.1\n" + algo2 + "\t0.2\t1.0\n",
				new String(Files.readAllBytes(Paths.get(paths[2]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t0.1\t0.1\n" + algo2 + "\t0.4\t0.999\n",
				new String(Files.readAllBytes(Paths.get(paths[4]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t0.1\t0.1\n" + algo2 + "\t0.6\t0.9828201\n",
				new String(Files.readAllBytes(Paths.get(paths[6]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t0.1\t0.1\n" + algo2 + "\t0.5\t0.978\n",
				new String(Files.readAllBytes(Paths.get(paths[8]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t0.1\t0.1\n" + algo2 + "\t0.36\t0.97196402\n",
				new String(Files.readAllBytes(Paths.get(paths[10]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t0.0\t0.0\n" + algo2 + "\t0.0344\t0.0013700136182415992\n",
				new String(Files.readAllBytes(Paths.get(paths[12]))));

		assertEquals("\talgo1\talgo2\nalgo1\t-\t12.332\nalgo2\t-\t-\n",
				new String(Files.readAllBytes(Paths.get(paths[14]))));
		assertEquals("\talgo1\talgo2\nalgo1\t-\t0.332\nalgo2\t-\t-\n",
				new String(Files.readAllBytes(Paths.get(paths[15]))));

		// Quantitative
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t10.0\t100.0\n" + algo2 + "\t10.0\t10.0\n",
				new String(Files.readAllBytes(Paths.get(paths[16]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t3.0\t3.0\n" + algo2 + "\t1.0\t3.0\n",
				new String(Files.readAllBytes(Paths.get(paths[17]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t20.0\t100.0\n" + algo2 + "\t20.0\t10.0\n",
				new String(Files.readAllBytes(Paths.get(paths[18]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t3.0\t4.0\n" + algo2 + "\t2.0\t3.0\n",
				new String(Files.readAllBytes(Paths.get(paths[19]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t30.0\t100.0\n" + algo2 + "\t30.0\t10.0\n",
				new String(Files.readAllBytes(Paths.get(paths[20]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t3.0\t5.0\n" + algo2 + "\t3.0\t3.0\n",
				new String(Files.readAllBytes(Paths.get(paths[21]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t20.0\t100.0\n" + algo2 + "\t40.0\t10.0\n",
				new String(Files.readAllBytes(Paths.get(paths[22]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t3.0\t4.0\n" + algo2 + "\t4.0\t3.0\n",
				new String(Files.readAllBytes(Paths.get(paths[23]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t10.0\t100.0\n" + algo2 + "\t50.0\t10.0\n",
				new String(Files.readAllBytes(Paths.get(paths[24]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t3.0\t3.0\n" + algo2 + "\t5.0\t3.0\n",
				new String(Files.readAllBytes(Paths.get(paths[25]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t18.0\t100.0\n" + algo2 + "\t30.0\t10.0\n",
				new String(Files.readAllBytes(Paths.get(paths[26]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t3.0\t3.8\n" + algo2 + "\t3.0\t3.0\n",
				new String(Files.readAllBytes(Paths.get(paths[27]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t56.0\t0.0\n" + algo2 + "\t200.0\t0.0\n",
				new String(Files.readAllBytes(Paths.get(paths[28]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t0.0\t0.5599999999999999\n" + algo2 + "\t2.0\t0.0\n",
				new String(Files.readAllBytes(Paths.get(paths[29]))));
	}

}
