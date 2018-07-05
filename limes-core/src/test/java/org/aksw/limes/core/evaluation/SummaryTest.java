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
						+ "Variance", // 11
				f.getAbsolutePath() + File.separatorChar + "Avg" + File.separatorChar + EvaluatorType.PRECISION
						+ "Variance",
				f.getAbsolutePath() + File.separatorChar + "statistics" + File.separatorChar + "data1",
				f.getAbsolutePath() + File.separatorChar + "statistics" + File.separatorChar + "data2", };
        assertEquals("\tdata1\tdata2\n"+algo1+"\t0.5\t0.5\n"+algo2+"\t0.5\t0.5\n", new String(Files.readAllBytes(Paths.get(paths[1]))));
        assertEquals("\tdata1\tdata2\n"+algo1+"\t0.5\t0.5\n"+algo2+"\t0.5\t0.5\n", new String(Files.readAllBytes(Paths.get(paths[3]))));
        assertEquals("\tdata1\tdata2\n"+algo1+"\t0.5\t0.5\n"+algo2+"\t0.5\t0.5\n", new String(Files.readAllBytes(Paths.get(paths[5]))));
        assertEquals("\tdata1\tdata2\n"+algo1+"\t0.5\t0.5\n"+algo2+"\t0.5\t0.5\n", new String(Files.readAllBytes(Paths.get(paths[7]))));
        assertEquals("\tdata1\tdata2\n"+algo1+"\t0.5\t0.5\n"+algo2+"\t0.5\t0.5\n", new String(Files.readAllBytes(Paths.get(paths[9]))));
        assertEquals("\tdata1\tdata2\n"+algo1+"\t0.5\t0.5\n"+algo2+"\t0.5\t0.5\n", new String(Files.readAllBytes(Paths.get(paths[11]))));
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t0.0\t0.0\n" + algo2 + "\t0.0\t0.0\n",
				new String(Files.readAllBytes(Paths.get(paths[13]))));

        assertEquals("\tdata1\tdata2\n"+algo1+"\t0.1\t0.1\n"+algo2+"\t0.36\t0.97196402\n", new String(Files.readAllBytes(Paths.get(paths[0]))));
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
		assertEquals("\tdata1\tdata2\n" + algo1 + "\t0.0\t0.0\n" + algo2 + "\t0.02088\t3.342495833295187E-4\n",
				new String(Files.readAllBytes(Paths.get(paths[12]))));

		assertEquals("\talgo1\talgo2\nalgo1\t-\t12.332\nalgo2\t-\t-\n",
				new String(Files.readAllBytes(Paths.get(paths[14]))));
		assertEquals("\talgo1\talgo2\nalgo1\t-\t0.332\nalgo2\t-\t-\n",
				new String(Files.readAllBytes(Paths.get(paths[15]))));
    }

}
